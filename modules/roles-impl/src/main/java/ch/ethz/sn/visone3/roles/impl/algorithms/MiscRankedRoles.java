/*
 * This file is part of netroles.
 *
 * netroles is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netroles is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netroles.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sn.visone3.roles.impl.algorithms;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.StreamSupport;

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.Pair;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationOrRanking;
import ch.ethz.sn.visone3.roles.impl.structures.LazyUncachedBinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.impl.structures.RelationBuilderServiceImpl;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;
import ch.ethz.sn.visone3.roles.structures.Relations;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * Implements algorithms to compute weak, weak structural and strong structural
 * roles on binary relations and rankings.
 */
public class MiscRankedRoles {

  private MiscRankedRoles() {
  }

  /**
   * Computes the weak roles ranking relative to the given unweighted network.
   * Runs in O(n) time and needs O(1) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak roles ranking for the given network.
   */
  public static Ranking weakRolesRanking(int n, NetworkView<?, ?> positionView) {
    return weakRolesInternal(n, positionView);
  }

  /**
   * Computes the coarsest weak roles ranking relative to the given network that
   * refines the given relation. Runs in O(n^2) time and needs O(n^2) additional
   * space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the coarsest weak roles ranking for the given network that refines
   *         the given ranking.
   */
  public static Ranking refiningWeakRoles(int n, NetworkView<?, ?> positionView,
      Ranking refinedRanking) {
    return Rankings.infimum(refinedRanking, weakRolesRanking(n, positionView));
  }

  /**
   * Computes the weak roles relation relative to the given unweighted network.
   * Runs in O(n) time and needs O(1) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak roles relation for the given network.
   */
  public static BinaryRelation weakRolesRelation(int n, NetworkView<?, ?> positionView) {
    return weakRolesInternal(n, positionView);
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network that
   * refines the given relation. Runs in O(n^2) time and needs O(1) additional
   * space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static BinaryRelation refiningWeakRoles(int n, NetworkView<?, ?> positionView,
      BinaryRelation refinedRelation) {
    return BinaryRelations.infimum(refinedRelation, weakRolesInternal(n, positionView));
  }

  private static BinaryRelationOrRanking weakRolesInternal(int n,
      NetworkView<?, ?> positionView) {
    int[] list = new int[n];
    boolean[] isolate = new boolean[n];
    int firstFree = 0, lastFree = n;
    for (int i = 0; i < n; ++i) {
      isolate[i] = !positionView.ties(i).iterator().hasNext();
      if (isolate[i]) {
        list[firstFree++] = i;
      } else {
        list[--lastFree] = i;
      }
    }
    assert (firstFree == lastFree);
    int firstNonisolate = firstFree;

    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public PrimitiveIterable.OfInt iterateInRelationTo(int i) {
        return Mappings.wrapUnmodifiable(list, 0, isolate[i] ? firstNonisolate : n);
      }

      @Override
      public PrimitiveIterable.OfInt iterateInRelationFrom(int i) {
        return Mappings.wrapUnmodifiable(list, isolate[i] ? 0 : firstNonisolate, n);
      }

      @Override
      public boolean contains(int i, int j) {
        return isolate[i] || !isolate[j];
      }

      @Override
      public int domainSize() {
        return n;
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return isolate[i] ? firstNonisolate : n - firstNonisolate;
      }

      @Override
      public int countInRelationTo(int i) {
        return isolate[i] ? firstNonisolate : n;
      }

      @Override
      public int countInRelationFrom(int i) {
        return isolate[i] ? n : n - firstNonisolate;
      }

      @Override
      public int countRelationPairs() {
        return n * firstNonisolate + (n - firstNonisolate) * (n - firstNonisolate);
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof RelationBase) {
          return equals((RelationBase) rhs);
        }
        return false;
      }

      @Override
      public int hashCode() {
        if (hasHashCode_) {
          return hashCode_;
        }
        hashCode_ = Relations.hashCode(this);
        hasHashCode_ = true;
        return hashCode_;
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }

      @Override
      public String toString() {
        return Relations.toString(this);
      }
    };
  }

  /**
   * Computes the weak roles ranking relative to the given unweighted network.
   * Runs in O(n) time and needs O(1) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak roles ranking for the given network.
   */
  public static Ranking weakRolesRanking(int n,
      TransposableNetworkView<?, ?> positionView) {
    return weakRolesInternal(n, positionView, Rankings.universal(n));
  }

  /**
   * Computes the coarsest weak roles ranking relative to the given network that
   * refines the given relation. Runs in O(n^2) time and needs O(n^2) additional
   * space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the coarsest weak roles ranking for the given network that refines
   *         the given ranking.
   */
  public static Ranking refiningWeakRoles(int n, TransposableNetworkView<?, ?> positionView,
      Ranking refinedRanking) {
    return weakRolesInternal(n, positionView, refinedRanking);
  }

  /**
   * Computes the weak roles relation relative to the given unweighted network.
   * Runs in O(n) time and needs O(1) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak roles relation for the given network.
   */
  public static BinaryRelation weakRolesRelation(int n,
      TransposableNetworkView<?, ?> positionView) {
    return weakRolesInternal(n, positionView, BinaryRelations.universal(n));
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network that
   * refines the given relation. Runs in O(n^2) time and needs O(1) additional
   * space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static BinaryRelation refiningWeakRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation refinedRelation) {
    return weakRolesInternal(n, positionView, refinedRelation);
  }

  private static BinaryRelationOrRanking weakRolesInternal(int n,
      TransposableNetworkView<?, ?> positionView, RelationBase refinedRelation) {
    return new LazyUncachedBinaryRelationMatrixImpl(n, (i, j) -> {
      return refinedRelation.contains(i, j)
          && (!positionView.ties(i, j, i).iterator().hasNext()
              || positionView.ties(i, j, j).iterator().hasNext());
    });
  }

  /**
   * Computes the weak roles ranking relative to the given network with weakly
   * ordered ties. Runs in O(m + n log n) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak roles ranking for the given network.
   */
  public static <V> Ranking weakRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakRolesInternal(n, positionView, comparator);
  }

  /**
   * Computes the coarsest weak roles ranking relative to the given network with
   * weakly ordered ties that refines the given relation. Runs in O(n^2) time and
   * needs O(n^2) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the coarsest weak roles ranking for the given network that refines
   *         the given ranking.
   */
  public static <V> Ranking refiningWeakRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      Comparator<? super V> comparator) {
    return Rankings.infimum(refinedRanking, weakRolesRanking(n, positionView, comparator));
  }

  /**
   * Computes the weak roles relation relative to the given network with weakly
   * ordered ties. Runs in O(m + n log n) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak roles relation for the given network.
   */
  public static <V> BinaryRelation weakRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakRolesInternal(n, positionView, comparator);
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network with
   * weakly ordered ties that refines the given relation. Runs in O(n^2) time and
   * needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that weakly orders ties.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static <V> BinaryRelation refiningWeakRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {
    return BinaryRelations.infimum(refinedRelation, weakRolesRelation(n, positionView, comparator));
  }

  private static <T> BinaryRelationOrRanking weakRolesInternal(int n,
      NetworkView<T, ?> positionView, Comparator<? super T> comparator) {
    @SuppressWarnings("unchecked")
    T[] maxRelationships = (T[]) new Object[n];
    for (int i = 0; i < n; ++i) {
      T maxRelationship = null;
      for (T ri : positionView.ties(i)) {
        if (maxRelationship == null) {
          maxRelationship = ri;
        } else if (comparator.compare(maxRelationship, ri) < 0) {
          maxRelationship = ri;
        }
      }
      maxRelationships[i] = maxRelationship;
    }

    int[] sortedVertices = StreamSupport.stream(Mappings.intRange(0, n).spliterator(), false)
        .sorted(new Comparator<Integer>() {

          @Override
          public int compare(Integer lhs, Integer rhs) {
            T rl = maxRelationships[lhs], rr = maxRelationships[rhs];
            ;
            if (rl == null) {
              return rl == null ? 0 : 1;
            } else if (rr == null) {
              return -1;
            } else {
              return comparator.compare(rl, rr);
            }
          }

        }).mapToInt(i -> i).toArray();

    int[] firstSame = new int[n], lastSame = new int[n];
    int firstCurrGroup = 0;
    T currGroupRep = null;
    for (int pos = 0; pos < n; ++pos) {
      int i = sortedVertices[pos];
      T curRel = maxRelationships[i];
      if (currGroupRep == null && curRel != null || comparator.compare(currGroupRep, curRel) < 0) {
        for (int pos2 = firstCurrGroup; pos2 < pos; ++pos2) {
          int j = sortedVertices[pos2];
          firstSame[j] = firstCurrGroup;
          lastSame[j] = pos;
        }
        firstCurrGroup = pos;
        currGroupRep = curRel;
      }
    }
    for (int pos = firstCurrGroup; pos < n; ++pos) {
      int i = sortedVertices[pos];
      firstSame[i] = firstCurrGroup;
      lastSame[i] = n;
    }

    int relCount = 0;
    for (int i = 0; i < n; ++i) {
      relCount += lastSame[i];
    }
    int relationshipCount = relCount;

    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public PrimitiveIterable.OfInt iterateInRelationTo(int i) {
        return Mappings.wrapUnmodifiable(sortedVertices, 0, lastSame[i]);
      }

      @Override
      public PrimitiveIterable.OfInt iterateInRelationFrom(int i) {
        return Mappings.wrapUnmodifiable(sortedVertices, firstSame[i], n);
      }

      @Override
      public boolean contains(int i, int j) {
        return firstSame[i] <= firstSame[j];
      }

      @Override
      public int domainSize() {
        return n;
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return lastSame[i] - firstSame[i];
      }

      @Override
      public int countInRelationTo(int i) {
        return lastSame[i];
      }

      @Override
      public int countInRelationFrom(int i) {
        return n - firstSame[i];
      }

      @Override
      public int countRelationPairs() {
        return relationshipCount;
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof RelationBase) {
          return equals((RelationBase) rhs);
        }
        return false;
      }

      @Override
      public int hashCode() {
        if (hasHashCode_) {
          return hashCode_;
        }
        hashCode_ = Relations.hashCode(this);
        hasHashCode_ = true;
        return hashCode_;
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }

      @Override
      public String toString() {
        return Relations.toString(this);
      }
    };
  }

  /**
   * Computes the weak roles ranking relative to the given network with weakly
   * ordered ties. Runs in O(m + n log n) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak roles ranking for the given network.
   */
  public static <V> Ranking weakRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakRoles(n, positionView, Rankings.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak roles ranking relative to the given network with
   * weakly ordered ties that refines the given ranking. Runs in O(n^2) time and
   * needs O(n^2) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the coarsest weak roles ranking for the given network that refines
   *         the given ranking.
   */
  public static <V> Ranking refiningWeakRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking refinedRanking, Comparator<? super V> comparator) {
    return weakRolesInternal(n, positionView, refinedRanking, comparator);
  }

  /**
   * Computes the weak roles relation relative to the given network with weakly
   * ordered ties. Runs in O(m + n log n) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak roles relation for the given network.
   */
  public static <V> BinaryRelation weakRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakRoles(n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network with
   * weakly ordered ties that refines the given relation. Runs in O(n^2) time and
   * needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that weakly orders ties.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static <V> BinaryRelation refiningWeakRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, Comparator<? super V> comparator) {
    return weakRolesInternal(n, positionView, refinedRelation, comparator);
  }

  private static <T> BinaryRelationOrRanking weakRolesInternal(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation,
      Comparator<? super T> comparator) {

    return new LazyUncachedBinaryRelationMatrixImpl(n, (i, j) -> {
      if (!refinedRelation.contains(i, j)) {
        return false;
      }
      T maxRelOfI = StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .max(comparator).orElse(null);
      return maxRelOfI == null
          || StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
              .anyMatch(rel -> comparator.compare(maxRelOfI, rel) <= 0);
    });
  }

  /**
   * Computes the weak roles ranking relative to the given network with partially
   * ordered ties. Runs in O(m*m) time and needs O(1) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak roles ranking for the given network.
   */
  public static <V> Ranking weakRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return refiningWeakRoles(n, positionView, Rankings.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak roles ranking relative to the given network with
   * partially ordered ties that refines the given relation. Runs in O(m*m) time
   * and needs O(1) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that partially orders ties.
   * @return the coarsest weak roles ranking for the given network that refines
   *         the given ranking.
   */
  public static <V> Ranking refiningWeakRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking refinedRanking, PartialComparator<? super V> comparator) {
    return weakRolesInternal(n, positionView, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak roles relation relative to the given network with partially
   * ordered ties. Runs in O(m*m) time and needs O(1) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak roles relation for the given network.
   */
  public static <V> BinaryRelation weakRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network with
   * partially ordered ties that refines the given relation. Runs in O(m*m) time
   * and needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that partially orders ties.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static <V> BinaryRelation refiningWeakRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, PartialComparator<? super V> comparator) {
    return refiningWeakRoles(n, positionView, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  // weakRolesRanking for BiPredicate<> argument deliberately omitted, since it
  // is not guaranteed to produce rankings for all inputs

  /**
   * Computes the weak roles relation relative to the given network with some
   * notion of compatibility among ties. Runs in O(m*m) time and needs O(1)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak roles relation for the given network.
   */
  public static <V> BinaryRelation weakRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return refiningWeakRoles(n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak roles relation relative to the given network with
   * some notion of compatibility among ties that refines the given relation. Runs
   * in O(m*m) time and needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a binary predicate that says whether the first tie's
   *                        value is compatible with the second one.
   * @return the coarsest weak roles relation for the given network that refines
   *         the given relation.
   */
  public static <V> BinaryRelation refiningWeakRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, BiPredicate<? super V, ? super V> comparator) {
    return weakRolesInternal(n, positionView, refinedRelation, comparator);
  }

  private static <T> boolean weakRolesDominationAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {
    boolean dominated = true;
    for (T ri : positionView.ties(i, j, i)) {
      boolean matched = false;
      for (T rj : positionView.ties(i, j, j)) {
        if (comparator.test(ri, rj)) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        dominated = false;
        break;
      }
    }
    return dominated;
  }

  private static <T> BinaryRelationOrRanking weakRolesInternal(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation,
      BiPredicate<? super T, ? super T> comparator) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (weakRolesDominationAt(i, j, positionView, comparator)) {
          builder.add(i, j);
        }
      }
    }
    return builder.build();
  }

  /**
   * Computes the weak exact roles ranking relative to the given unweighted
   * network. Runs in O(n) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak exact roles ranking for the given network.
   */
  public static Ranking weakExactRolesRanking(int n, NetworkView<?, ?> positionView) {
    return weakExactRolesInternal(n, positionView);
  }

  /**
   * Computes the coarsest weak exact roles ranking relative to the given network
   * that refines the given relation. Runs in O(n^2) time and needs O(n^2)
   * additional space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the coarsest weak exact roles ranking for the given network that
   *         refines the given ranking.
   */
  public static Ranking refiningWeakExactRoles(int n, NetworkView<?, ?> positionView,
      Ranking refinedRanking) {
    return Rankings.infimum(refinedRanking, weakExactRolesRanking(n, positionView));
  }

  /**
   * Computes the weak exact roles relation relative to the given unweighted
   * network. Runs in O(n) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak exact roles relation for the given network.
   */
  public static BinaryRelation weakExactRolesRelation(int n,
      NetworkView<?, ?> positionView) {
    return weakExactRolesInternal(n, positionView);
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * that refines the given relation. Runs in O(n^2) time and needs O(n^2)
   * additional space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static BinaryRelation refiningWeakExactRoles(int n, NetworkView<?, ?> positionView,
      BinaryRelation refinedRelation) {
    return BinaryRelations.infimum(refinedRelation, weakExactRolesRelation(n, positionView));
  }

  private static <T> BinaryRelationOrRanking weakExactRolesInternal(int n,
      NetworkView<T, ?> positionView) {

    int[] degs = new int[n];
    for (int i = 0; i < n; ++i) {
      degs[i] = positionView.countTies(i);
    }

    int[] sortedVertices = PrimitiveCollections.countingSort(degs, n);

    int[] firstSame = new int[n], lastSame = new int[n];
    if (n > 0) {
      int firstCurrGroup = 0;
      int currGroupDegree = degs[sortedVertices[0]];
      for (int pos = 0; pos < n; ++pos) {
        int i = sortedVertices[pos];
        int currDegree = degs[i];
        if (currGroupDegree < currDegree) {
          for (int pos2 = firstCurrGroup; pos2 < pos; ++pos2) {
            int j = sortedVertices[pos2];
            firstSame[j] = firstCurrGroup;
            lastSame[j] = pos;
          }
          firstCurrGroup = pos;
          currGroupDegree = currDegree;
        }
      }
      for (int pos = firstCurrGroup; pos < n; ++pos) {
        int i = sortedVertices[pos];
        firstSame[i] = firstCurrGroup;
        lastSame[i] = n;
      }
    }

    int relCount = 0;
    for (int i = 0; i < n; ++i) {
      relCount += lastSame[i];
    }
    int relationshipCount = relCount;

    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public PrimitiveIterable.OfInt iterateInRelationTo(int i) {
        return Mappings.wrapUnmodifiable(sortedVertices, 0, lastSame[i]);
      }

      @Override
      public PrimitiveIterable.OfInt iterateInRelationFrom(int i) {
        return Mappings.wrapUnmodifiable(sortedVertices, firstSame[i], n);
      }

      @Override
      public boolean contains(int i, int j) {
        return firstSame[i] <= firstSame[j];
      }

      @Override
      public int domainSize() {
        return n;
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return lastSame[i] - firstSame[i];
      }

      @Override
      public int countInRelationTo(int i) {
        return lastSame[i];
      }

      @Override
      public int countInRelationFrom(int i) {
        return n - firstSame[i];
      }

      @Override
      public int countRelationPairs() {
        return relationshipCount;
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof RelationBase) {
          return equals((RelationBase) rhs);
        }
        return false;
      }

      @Override
      public int hashCode() {
        if (hasHashCode_) {
          return hashCode_;
        }
        hashCode_ = Relations.hashCode(this);
        hasHashCode_ = true;
        return hashCode_;
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }

      @Override
      public String toString() {
        return Relations.toString(this);
      }
    };
  }

  /**
   * Computes the weak exact roles ranking relative to the given unweighted
   * network. Runs in O(n) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak exact roles ranking for the given network.
   */
  public static Ranking weakExactRolesRanking(int n,
      TransposableNetworkView<?, ?> positionView) {
    return weakExactRolesInternal(n, positionView, Rankings.universal(n));
  }

  /**
   * Computes the coarsest weak exact roles ranking relative to the given network
   * that refines the given relation. Runs in O(n^2) time and needs O(n^2)
   * additional space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the coarsest weak exact roles ranking for the given network that
   *         refines the given ranking.
   */
  public static Ranking refiningWeakExactRoles(int n,
      TransposableNetworkView<?, ?> positionView, Ranking refinedRanking) {
    return weakExactRolesInternal(n, positionView, refinedRanking);
  }

  /**
   * Computes the weak exact roles relation relative to the given unweighted
   * network. Runs in O(n) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak exact roles relation for the given network.
   */
  public static BinaryRelation weakExactRolesRelation(int n,
      TransposableNetworkView<?, ?> positionView) {
    return weakExactRolesInternal(n, positionView, BinaryRelations.universal(n));
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * that refines the given relation. Runs in O(n^2) time and needs O(n^2)
   * additional space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static BinaryRelation refiningWeakExactRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation refinedRelation) {
    return weakExactRolesInternal(n, positionView, refinedRelation);
  }

  private static <T> BinaryRelationOrRanking weakExactRolesInternal(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation) {
    return new LazyUncachedBinaryRelationMatrixImpl(n, (i, j) -> {
      return refinedRelation.contains(i, j)
          && positionView.countTies(i, j, i) <= positionView.countTies(i, j, j);
    });
  }

  /**
   * Computes the weak exact roles ranking relative to the given network with
   * weakly ordered ties. Runs in O(m n) time and needs O(m) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak exact roles ranking for the given network.
   */
  public static <V> Ranking weakExactRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakExactRoles(n, positionView, Rankings.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak exact roles ranking relative to the given network
   * with weakly ordered ties that refines the given relation. Runs in O(m n) time
   * and needs O(m+n^2) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the coarsest weak exact roles ranking for the given network that
   *         refines the given ranking.
   */
  public static <V> Ranking refiningWeakExactRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      Comparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRanking, comparator);
  }

  /**
   * Computes the weak exact roles relation relative to the given network with
   * weakly ordered ties. Runs in O(m n) time and needs O(m + n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak exact roles relation for the given network.
   */
  public static <V> BinaryRelation weakExactRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakExactRoles(n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * with weakly ordered ties that refines the given relation. Runs in O(m n) time
   * and needs O(m+n^2) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that weakly orders ties.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static <V> BinaryRelation refiningWeakExactRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRelation, comparator);
  }

  private static <T> BinaryRelationOrRanking weakExactRolesInternal(int n,
      NetworkView<T, ?> positionView, RelationBase refinedRelation,
      Comparator<? super T> comparator) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    // Strategy: To test whether one node is dominated by another, all incident
    // relationships are sorted according to the comparator. Then the former
    // node is dominated by the latter if the top-ranked incident link of the
    // former is exceeded by the top-ranked incident link of the latter, the
    // second-ranked incident link of the former the second-ranked of the
    // latter, and so on
    @SuppressWarnings("unchecked")
    T[][] relationships = (T[][]) new Object[n][];
    Comparator<? super T> reversedComparator = comparator.reversed();
    for (int i = 0; i < n; ++i) {
      @SuppressWarnings("unchecked")
      T[] relationshipsForI = (T[]) StreamSupport.stream(positionView.ties(i).spliterator(), false)
          .sorted(reversedComparator).toArray();
      relationships[i] = relationshipsForI;
    }
    for (int i = 0; i < n; ++i) {
      int nilen = relationships[i].length;
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (nilen <= relationships[j].length && (i != j)) {
          boolean isDominated = true;
          for (int k = 0; k < nilen && isDominated; ++k) {
            isDominated = comparator.compare(relationships[i][k], relationships[j][k]) <= 0;
          }
          if (isDominated) {
            builder.add(i, j);
          }
        }
      }
    }

    return builder.build();
  }

  /**
   * Computes the weak exact roles ranking relative to the given network with
   * weakly ordered ties. Runs in O(m n) time and needs O(m) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak exact roles ranking for the given network.
   */
  public static <V> Ranking weakExactRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakExactRoles(n, positionView, Rankings.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak exact roles ranking relative to the given network
   * with weakly ordered ties that refines the given relation. Runs in O(m n) time
   * and needs O(m+n^2) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the coarsest weak exact roles ranking for the given network that
   *         refines the given ranking.
   */
  public static <V> Ranking refiningWeakExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking refinedRanking, Comparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRanking, comparator);
  }

  /**
   * Computes the weak exact roles relation relative to the given network with
   * weakly ordered ties. Runs in O(m n) time and needs O(m + n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak exact roles relation for the given network.
   */
  public static <V> BinaryRelation weakExactRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return refiningWeakExactRoles(n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * that refines the given relation with weakly ordered ties. Runs in O(m n) time
   * and needs O(m+n^2) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that weakly orders ties.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static <V> BinaryRelation refiningWeakExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, Comparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRelation, comparator);
  }

  private static <T> BinaryRelationOrRanking weakExactRolesInternal(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation,
      Comparator<? super T> comparator) {
    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    Comparator<? super T> reversedComparator = comparator.reversed();

    // Strategy: To test whether one node is dominated by another, all incident
    // relationships are sorted according to the comparator. Then the former
    // node is dominated by the latter if the top-ranked incident link of the
    // former is exceeded by the top-ranked incident link of the latter, the
    // second-ranked incident link of the former the second-ranked of the
    // latter, and so on
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (i == j || positionView.countTies(i, j, i) > positionView.countTies(i, j, j)) {
          continue;
        }
        @SuppressWarnings("unchecked")
        T[] relationshipsForI = (T[]) StreamSupport
            .stream(positionView.ties(i, j, i).spliterator(), false).sorted(reversedComparator)
            .toArray();
        @SuppressWarnings("unchecked")
        T[] relationshipsForJ = (T[]) StreamSupport
            .stream(positionView.ties(i, j, j).spliterator(), false).sorted(reversedComparator)
            .toArray();
        int nilen = relationshipsForI.length;
        boolean isDominated = true;
        for (int k = 0; k < nilen && isDominated; ++k) {
          isDominated = comparator.compare(relationshipsForI[k], relationshipsForJ[k]) <= 0;
        }
        if (isDominated) {
          builder.add(i, j);
        }
      }
    }

    return builder.build();
  }

  /**
   * Computes the weak exact roles ranking relative to the given network with
   * partially ordered ties. Runs in O(m^2 sqrt(n)) time and needs O(n) additional
   * space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak exact roles ranking for the given network.
   */
  public static <V> Ranking weakExactRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, Rankings.universal(n),
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak exact roles ranking relative to the given network
   * with partially ordered ties that refines the given relation. Runs in O(m*m)
   * time and needs O(1) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that partially orders ties.
   * @return the coarsest weak exact roles ranking for the given network that
   *         refines the given ranking.
   */
  public static <V> Ranking refiningWeakExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking refinedRanking, PartialComparator<? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak exact roles relation relative to the given network with
   * partially ordered ties. Runs in O(m^2 sqrt(n)) time and needs O(n) additional
   * space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak exact roles relation for the given network.
   */
  public static <V> BinaryRelation weakExactRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakExactRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * with partially ordered ties that refines the given relation. Runs in O(m*m)
   * time and needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that partially orders ties.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static <V> BinaryRelation refiningWeakExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, PartialComparator<? super V> comparator) {
    return refiningWeakExactRoles(n, positionView, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  // weakExactRolesRanking for BiPredicate<> argument deliberately omitted,
  // since it
  // is not guaranteed to produce rankings for all inputs

  /**
   * Computes the weak exact roles relation relative to the given network with
   * some notion of compatibility between ties. Runs in O(m^2 sqrt(n)) time and
   * needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak exact roles relation for the given network.
   */
  public static <V> BinaryRelation weakExactRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return weakExactRolesInternal(n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest weak exact roles relation relative to the given network
   * with some notion of compatibility between ties that refines the given
   * relation. Runs in O(m*m) time and needs O(1) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a binary predicate that says whether the first tie's
   *                        value is compatible with the second one.
   * @return the coarsest weak exact roles relation for the given network that
   *         refines the given relation.
   */
  public static <V> BinaryRelation refiningWeakExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation refinedRelation, BiPredicate<? super V, ? super V> comparator) {
    return weakExactRolesInternal(n, positionView, refinedRelation, comparator);
  }

  private static <T> boolean weakExactRolesDominationAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {

    if (i == j) {
      return true;
    }
    int neighboricount = positionView.countTies(i, j, i);
    int neighborjcount = positionView.countTies(i, j, j);
    if (neighboricount > neighborjcount) {
      return false;
    }

    NetworkBuilder networkBuilder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    networkBuilder.ensureNode(neighboricount + neighborjcount - 1);
    int nicount = 0;
    boolean canDominate = true;
    for (T ri : positionView.ties(i, j, i)) {
      int njcount = neighboricount;
      boolean anyMatching = false;
      for (T rj : positionView.ties(i, j, j)) {
        if (comparator.test(ri, rj)) {
          anyMatching = true;
          networkBuilder.addEdge(nicount, njcount);
        }
        ++njcount;
      }
      ++nicount;
      if (!anyMatching) {
        canDominate = false;
        break;
      }
    }
    return canDominate
        && BipartiteMatching.maximumMatching(networkBuilder.build().asUndirectedGraph(),
            Mappings.intRange(0, neighboricount)).size() == neighboricount;
  }

  private static <T> BinaryRelationOrRanking weakExactRolesInternal(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation,
      BiPredicate<? super T, ? super T> compatibilityTester) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    // Strategy: To test whether one node is dominated by another, a
    // bipartite graph consisting of the nodes in the neighborhoods of both
    // current nodes and including edges whenever a node from the first
    // neighborhood can be substituted for by a node from the latter. If a
    // maximum bipartite matching between these neighborhoods with the size
    // of the first neighborhood exists in this graph, then the first node
    // is dominated by the second.
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (i == j) {
          continue;
        }
        if (weakExactRolesDominationAt(i, j, positionView, compatibilityTester)) {
          builder.add(i, j);
        }
      }
    }

    return builder.build();
  }

  private static <T> boolean pMatchingWeakRolesDominationAt(int i, int j, int p,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {

    if (i == j) {
      return true;
    }
    int neighboricount = positionView.countTies(i, j, i);
    int neighborjcount = positionView.countTies(i, j, j);
    if (neighboricount > p * neighborjcount) {
      return false;
    }

    NetworkBuilder networkBuilder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    networkBuilder.ensureNode(neighboricount + p * neighborjcount - 1);
    int nicount = 0;
    boolean canDominate = true;
    for (T ri : positionView.ties(i, j, i)) {
      int njcount = neighboricount;
      boolean anyMatching = false;
      for (T rj : positionView.ties(i, j, j)) {
        if (comparator.test(ri, rj)) {
          anyMatching = true;
          for (int q = 0; q < p; ++q) {
            networkBuilder.addEdge(nicount, njcount + q * neighborjcount);
          }
        }
        ++njcount;
      }
      ++nicount;
      if (!anyMatching) {
        canDominate = false;
        break;
      }
    }
    return canDominate
        && BipartiteMatching.maximumMatching(networkBuilder.build().asUndirectedGraph(),
            Mappings.intRange(0, neighboricount)).size() == neighboricount;
  }

  /**
   * Computes the binary relation under the notion of weak roles with a specified
   * degree of strictness {@code p}, meaning that in a pairwise comparison of
   * {@code i} with {@code j}, each edge incident to {@code j} can substitute for
   * {@code p} edges incident to {@code i}, on the given network with some notion
   * of compatibility between ties. Runs in O(p^{1.5} m^2 sqrt(n)) time and needs
   * O(pn) additional space.
   * 
   * @param <T>          type representing ties.
   * @param p            degree of strictness of tie substitution.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the binary relation under the notion of weak roles with the specified
   *         degree of strictness {@code p} for the given network.
   */
  public static <T> BinaryRelation pMatchingWeakRoles(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {
    return pMatchingRefiningWeakRoles(p, n, positionView, BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the coarsest binary relation that refines the specified relation and
   * is compatible with the notion of weak roles with a specified degree of
   * strictness {@code p}, meaning that in a pairwise comparison of {@code i} with
   * {@code j}, each edge incident to {@code j} can substitute for {@code p} edges
   * incident to {@code i}, on the given network with some notion of compatibility
   * between ties. Runs in O(p^{1.5} m^2 sqrt(n)) time and needs O(pn) additional
   * space.
   * 
   * @param <T>             type representing ties.
   * @param p               degree of strictness of tie substitution.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a binary predicate that says whether the first tie's
   *                        value is compatible with the second one.
   * @return the coarsest binary relation refining the specified relation and
   *         compatible with the notion of weak roles with a specified degree of
   *         strictness {@code p} on the given network.
   */
  public static <T> BinaryRelation pMatchingRefiningWeakRoles(int p, int n,
      TransposableNetworkView<T, ?> positionView, RelationBase refinedRelation,
      BiPredicate<? super T, ? super T> comparator) {

    if (p == n) {
      return weakRolesInternal(n, positionView, refinedRelation, comparator);
    }
    if (p == 1) {
      return weakExactRolesInternal(n, positionView, refinedRelation, comparator);
    }

    RelationBuilder<? extends BinaryRelation> builder = RelationBuilders
        .denseRelationBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    // Strategy: To test whether one node is dominated by another, a
    // bipartite graph consisting of the nodes in the neighborhoods of both
    // current nodes and including edges whenever a node from the first
    // neighborhood can be substituted for by a node from the latter. If a
    // maximum bipartite matching between these neighborhoods with the size
    // of the first neighborhood exists in this graph, then the first node
    // is dominated by the second.
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (i == j) {
          continue;
        }
        if (p >= positionView.countTies(i, j, i)
            ? weakRolesDominationAt(i, j, positionView, comparator)
            : pMatchingWeakRolesDominationAt(i, j, p, positionView, comparator)) {
          builder.add(i, j);
        }
      }
    }

    return builder.build();
  }

  /**
   * Computes the strong structural roles ranking for the given unweighted
   * network. Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the strong structural roles ranking for the given network.
   */
  public static Ranking strongStructuralRolesRanking(int n,
      NetworkView<?, ?> positionView) {

    return strongStructuralRolesRanking(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the coarsest strong structural roles ranking for the given
   * unweighted network that refines the given ranking. Runs in O(n^2 + m max deg)
   * time and needs O(n) additional space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the strong structural roles ranking for the given network that
   *         refines the given ranking.
   */
  public static Ranking refiningStrongStructuralRoles(int n, NetworkView<?, ?> positionView,
      Ranking refinedRanking) {

    return Rankings.infimum(refinedRanking, strongStructuralRolesRanking(n, positionView));
  }

  /**
   * Computes the strong structural roles relation for the given unweighted
   * network. Runs in O(iso(G)n + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the strong structural roles relation for the given network.
   */
  public static BinaryRelation strongStructuralRolesRelation(int n,
      NetworkView<?, ?> positionView) {

    return strongStructuralRolesRelation(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the coarsest strong structural roles relation for the given
   * unweighted network that refines the given relation. Runs in O(n^2 + m max
   * deg) time and needs O(n) additional space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine..
   * @return the coarsest strong structural roles relation for the given network
   *         that refines the given relation.
   */
  public static BinaryRelation refiningStrongStructuralRoles(int n,
      NetworkView<?, ?> positionView, BinaryRelation refinedRelation) {

    return BinaryRelations.infimum(refinedRelation, strongStructuralRolesRelation(n, positionView));
  }

  /**
   * Computes the strong structural roles ranking for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the strong structural roles ranking for the given network.
   */
  public static <V> Ranking strongStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return strongStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest strong structural roles ranking for the given network
   * with weakly ordered ties that refines the given ranking. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the strong structural roles ranking for the given network that
   *         refines the given ranking.
   */
  public static <V> Ranking refiningStrongStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      Comparator<? super V> comparator) {

    return Rankings.infimum(refinedRanking,
        strongStructuralRolesRanking(n, positionView, comparator));
  }

  /**
   * Computes the strong structural roles relation for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return strongStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest strong structural roles relation for the given network
   * with weakly ordered ties that refines the given relation. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine
   * @param comparator      a comparator that weakly orders ties.
   * @return the coarsest strong structural roles relation for the given network
   *         that refines the given relation.
   */
  public static <V> BinaryRelation refiningStrongStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        strongStructuralRolesRelation(n, positionView, comparator));
  }

  /**
   * Computes the strong structural roles ranking for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the strong structural roles ranking for the given network.
   */
  public static <V> Ranking strongStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return strongStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest strong structural roles ranking for the given network
   * with weakly ordered ties that refines the given ranking. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that partially orders ties.
   * @return the strong structural roles ranking for the given network that
   *         refines the given ranking.
   */
  public static <V> Ranking refiningStrongStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      PartialComparator<? super V> comparator) {

    return Rankings.infimum(refinedRanking,
        strongStructuralRolesRanking(n, positionView, comparator));
  }

  /**
   * Computes the strong structural roles relation for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return strongStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest strong structural roles relation for the given network
   * with partially ordered ties that refines the given relation. Runs in O(n^2 +
   * m max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that partially orders ties.
   * @return the coarsest strong structural roles relation for the given network
   *         that refines the given relation.
   */
  public static <V> BinaryRelation refiningStrongStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      PartialComparator<? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        strongStructuralRolesRelation(n, positionView, comparator));
  }

  /**
   * Computes the strong structural ranking for the given network with a notion of
   * compatibility between ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * <p>
   * This method is deliberately set to private, since it is not guaranteed to
   * produce valid rankings for all inputs.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the strong structural roles relation for the given network.
   */
  private static <V> Ranking strongStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return strongStructuralRolesInternal(n, comparator, positionView);
  }

  /**
   * Computes the strong structural roles relation for the given network with a
   * notion of compatibility between ties. Runs in O(n^2 + m max deg) time and
   * needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return strongStructuralRolesInternal(n, comparator, positionView);
  }

  /**
   * Computes the coarsest strong structural roles relation for the given network
   * with a notion of compatibility between ties that refines the given relation.
   * Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a binary predicate that says whether the first tie's
   *                        value is compatible with the second one
   * @return the coarsest strong structural roles relation for the given network
   *         that refines the given relation.
   */
  public static <V> BinaryRelation refiningStrongStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      BiPredicate<? super V, ? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        strongStructuralRolesRelation(n, positionView, comparator));
  }

  private static <T, U> BinaryRelationOrRanking strongStructuralRolesInternal(int n,
      BiPredicate<? super T, ? super U> comparator, NetworkView<T, U> positionView) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);

    NeighborhoodInclusion.structuralPreorder(n, positionView::ties, positionView::inverseTies,
        positionView::tieTarget, positionView::inverseTieTarget,
        new NeighborhoodInclusion.StructuralPreorderVisitor<T, U>() {

          @Override
          public void startNode(int i) {
          }

          @Override
          public void finishNode(int i) {
          }

          @Override
          public void isolate(int i) {
            for (int j = 0; j < n; ++j) {
              builder.add(i, j);
            }
          }

          @Override
          public boolean canMatch(int source, T edge, int middle, U matchedby) {
            return comparator.test(edge, matchedby);
          }

          @Override
          public void addDomination(int i, int j) {
            builder.add(i, j);
          }
        });

    return builder.build();
  }

  /**
   * Computes the strong structural roles ranking for the given unweighted
   * network. Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the strong structural roles ranking for the given network.
   */
  public static Ranking strongStructuralRolesRanking(int n,
      TransposableNetworkView<?, ?> positionView) {
    return strongStructuralRolesRanking(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the strong structural roles ranking for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the strong structural roles ranking for the given network.
   */
  public static <V> Ranking strongStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return strongStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the strong structural roles ranking for the given network with
   * partially ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the strong structural roles ranking for the given network.
   */
  public static <V> Ranking strongStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return strongStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the strong structural ranking for the given network with a notion of
   * compatibility between ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * <p>
   * This method is deliberately set to private, since it is not guaranteed to
   * produce valid rankings for all inputs.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the strong structural roles relation for the given network.
   */
  private static <V> Ranking strongStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return strongStructuralRolesTransImpl(n, comparator, positionView);
  }

  /**
   * Computes the strong structural roles relation for the given unweighted
   * network. Runs in O(iso(G)n + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the strong structural roles relation for the given network.
   */
  public static BinaryRelation strongStructuralRolesRelation(int n,
      TransposableNetworkView<?, ?> positionView) {
    return strongStructuralRolesRelation(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the strong structural roles relation for the given network with
   * weakly ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return strongStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the strong structural roles relation for the given network with
   * partially ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return strongStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the strong structural roles relation for the given network with a
   * notion of compatibility between ties. Runs in O(n^2 + m max deg) time and
   * needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one
   * @return the strong structural roles relation for the given network.
   */
  public static <V> BinaryRelation strongStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return strongStructuralRolesTransImpl(n, comparator, positionView);
  }

  private static <T> BinaryRelationOrRanking strongStructuralRolesTransImpl(int n,
      BiPredicate<? super T, ? super T> comparator,
      TransposableNetworkView<T, ?> positionView) {
    return new LazyUncachedBinaryRelationMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      if (degi > degj) {
        return false;
      }
      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(rj -> positionView.tieTarget(i, j, j, rj))).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi, ++posj) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = positionView.tieTarget(i, j, j, rj);
        }
        if (ritarget != rjtarget || !comparator.test(ri, rj)) {
          return false;
        }
      }
      return posi == degi;
    });
  }

  /**
   * Computes the weak structural roles ranking for the given unweighted network.
   * Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak structural roles ranking for the given network.
   */
  public static Ranking weakStructuralRolesRanking(int n, NetworkView<?, ?> positionView) {

    return weakStructuralRolesRanking(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the coarsest weak structural roles ranking for the given unweighted
   * network that refines the given ranking. Runs in O(n^2 + m max deg) time and
   * needs O(n) additional space.
   * 
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @return the weak structural roles ranking for the given network that refines
   *         the given ranking.
   */
  public static Ranking refiningWeakStructuralRoles(int n, NetworkView<?, ?> positionView,
      Ranking refinedRanking) {

    return Rankings.infimum(refinedRanking, weakStructuralRolesRanking(n, positionView));
  }

  /**
   * Computes the weak structural roles relation for the given unweighted network.
   * Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak structural roles relation for the given network.
   */
  public static BinaryRelation weakStructuralRolesRelation(int n,
      NetworkView<?, ?> positionView) {

    return weakStructuralRolesRelation(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the coarsest weak structural roles relation for the given unweighted
   * network that refines the given relation. Runs in O(n^2 + m max deg) time and
   * needs O(n) additional space.
   * 
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @return the weak structural roles relation for the given network that refines
   *         the given ranking.
   */
  public static BinaryRelation refiningWeakStructuralRoles(int n,
      NetworkView<?, ?> positionView, BinaryRelation refinedRelation) {

    return BinaryRelations.infimum(refinedRelation, weakStructuralRolesRelation(n, positionView));
  }

  /**
   * Computes the weak structural roles ranking for the given network with weakly
   * ordered ties. Runs in O(n^2 + m max deg) time and needs O(n) additional
   * space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak structural roles ranking for the given network.
   */
  public static <V> Ranking weakStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak structural roles ranking for the given network
   * with weakly ordered ties that refines the given ranking. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that weakly orders ties.
   * @return the weak structural roles ranking for the given network that refines
   *         the given ranking.
   */
  public static <V> Ranking refiningWeakStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      Comparator<? super V> comparator) {

    return Rankings.infimum(refinedRanking,
        weakStructuralRolesRanking(n, positionView, comparator));
  }

  /**
   * Computes the weak structural roles relation for the given network with weakly
   * ordered ties. Runs in O(n^2 + m max deg) time and needs O(n) additional
   * space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak structural roles relation for the given network
   * with weakly ordered ties that refines the given relation. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that weakly orders ties.
   * @return the weak structural roles relation for the given network that refines
   *         the given ranking.
   */
  public static <V> BinaryRelation refiningWeakStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        weakStructuralRolesRelation(n, positionView, comparator));
  }

  /**
   * Computes the weak structural roles ranking for the given network with
   * partially ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak structural roles ranking for the given network.
   */
  public static <V> Ranking weakStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak structural roles ranking for the given network
   * with partially ordered ties that refines the given ranking. Runs in O(n^2 + m
   * max deg) time and needs O(n) additional space.
   * 
   * @param <V>            type representing ties.
   * @param n              number of nodes.
   * @param positionView   network as viewed from the position of the individual
   *                       nodes.
   * @param refinedRanking ranking to refine.
   * @param comparator     a comparator that partially orders ties.
   * @return the weak structural roles ranking for the given network that refines
   *         the given ranking.
   */
  public static <V> Ranking refiningWeakStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking refinedRanking,
      PartialComparator<? super V> comparator) {

    return Rankings.infimum(refinedRanking,
        weakStructuralRolesRanking(n, positionView, comparator));
  }

  /**
   * Computes the weak structural roles relation for the given network with
   * partially ordered ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakStructuralRolesRelation(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the coarsest weak structural roles relation for the given network
   * with partially ordered ties that refines the given relation. Runs in O(n^2 +
   * m max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a comparator that partially orders ties.
   * @return the weak structural roles relation for the given network that refines
   *         the given ranking.
   */
  public static <V> BinaryRelation refiningWeakStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      PartialComparator<? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        weakStructuralRolesRelation(n, positionView, comparator));
  }

  /**
   * Computes the weak structural ranking for the given network with a notion of
   * compatibility between ties. Runs in O(n^2 + m max deg) time and needs O(n)
   * additional space.
   * 
   * <p>
   * This method is deliberately set to private, since it is not guaranteed to
   * produce valid rankings for all inputs.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak structural roles relation for the given network.
   */
  private static <V> Ranking weakStructuralRolesRanking(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return weakStructuralRolesInternal(n, positionView, comparator);
  }

  /**
   * Computes the weak structural roles relation for the given network with a
   * notion of compatibility between ties. Runs in O(n^2 + m max deg) time and
   * needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      NetworkView<? extends V, ? extends V> positionView, BiPredicate<? super V, ? super V> comparator) {
    return weakStructuralRolesInternal(n, positionView, comparator);
  }

  /**
   * Computes the coarsest weak structural roles relation for the given network
   * with a notion of compatibility between ties that refines the given relation.
   * Runs in O(n^2 + m max deg) time and needs O(n) additional space.
   * 
   * @param <V>             type representing ties.
   * @param n               number of nodes.
   * @param positionView    network as viewed from the position of the individual
   *                        nodes.
   * @param refinedRelation relation to refine.
   * @param comparator      a binary predicate that says whether the first tie's
   *                        value is compatible with the second one.
   * @return the weak structural roles relation for the given network that refines
   *         the given ranking.
   */
  public static <V> BinaryRelation refiningWeakStructuralRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation refinedRelation,
      BiPredicate<? super V, ? super V> comparator) {

    return BinaryRelations.infimum(refinedRelation,
        weakStructuralRolesRelation(n, positionView, comparator));
  }

  private static <V, T extends V, U extends V> BinaryRelationOrRanking weakStructuralRolesInternal(
      int n, NetworkView<T, U> positionView, BiPredicate<? super V, ? super V> comparator) {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    PrimitiveList<Pair<Integer, T>> loops = (PrimitiveList<Pair<Integer, T>>) Mappings
        .newList((Class) Pair.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    PrimitiveList<Pair<Integer, T>> singleLoops = (PrimitiveList<Pair<Integer, T>>) Mappings
        .newList((Class) Pair.class);

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);

    NeighborhoodInclusion.vicinalPreorder(n, positionView::ties, positionView::inverseTies,
        positionView::tieTarget, positionView::inverseTieTarget,
        new NeighborhoodInclusion.VicinalPreorderVisitor<T, U>() {

          @Override
          public void startNode(int i) {
          }

          @Override
          public void finishNode(int i) {
          }

          @Override
          public void isolate(int i) {
            for (int j = 0; j < n; ++j) {
              builder.add(i, j);
            }
          }

          @Override
          public boolean canMatch(int source, T edge, int middle, U matchedby) {
            return comparator.test(edge, matchedby);
          }

          @Override
          public boolean canMatchLoop(int firstSource, T firstLoop, int secondSource,
              T secondLoop) {
            return comparator.test(firstLoop, secondLoop);
          }

          @Override
          public void addDomination(int i, int j) {
            builder.add(i, j);
          }

          @Override
          public void visitLoop(int i, T loop, boolean onlyIncidentEdge) {
            Pair<Integer, T> p = new Pair<>(i, loop);
            loops.add(p);
            if (onlyIncidentEdge) {
              singleLoops.add(p);
            }
          }
        });
    for (Pair<Integer, T> ri : singleLoops) {
      for (Pair<Integer, T> rj : loops) {
        if (comparator.test(ri.getSecond(), rj.getSecond())) {
          builder.add(ri.getFirst(), rj.getFirst());
        }
      }
    }

    return builder.build();
  }

  /**
   * Computes the weak structural roles ranking for the given network. Runs in O(m
   * * n log(n)) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak structural roles ranking for the given network.
   */
  public static Ranking weakStructuralRolesRanking(int n, TransposableNetworkView<?, ?> positionView) {
    return weakStructuralRolesRanking(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the weak structural roles ranking for the given network with weakly
   * ordered ties. Runs in O(m * n log(n)) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak structural roles ranking for the given network.
   */
  public static <V> Ranking weakStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak structural roles ranking for the given network with
   * partially ordered ties. Runs in O(m * n log(n)) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak structural roles ranking for the given network.
   */
  public static <V> Ranking weakStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakStructuralRolesRanking(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak structural ranking for the given network with a notion of
   * compatibility between ties. Runs in O(m * n log(n)) time and needs O(n)
   * additional space.
   * 
   * <p>
   * This method is deliberately set to private, since it is not guaranteed to
   * produce valid rankings for all inputs.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak structural roles relation for the given network.
   */
  private static <V> Ranking weakStructuralRolesRanking(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return weakStructuralRolesTransImpl(n, positionView, comparator);
  }

  /**
   * Computes the weak structural roles relation for the given network. Runs in
   * O(m * n log(n)) time and needs O(n) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @return the weak structural roles relation for the given network.
   */
  public static BinaryRelation weakStructuralRolesRelation(int n, TransposableNetworkView<?, ?> positionView) {
    return weakStructuralRolesTransImpl(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the weak structural roles relation for the given network with weakly
   * ordered ties. Runs in O(m * n log(n)) time and needs O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that weakly orders ties.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakStructuralRolesTransImpl(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak structural roles relation for the given network with
   * partially ordered ties. Runs in O(m * n log(n)) time and needs O(n)
   * additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a comparator that partially orders ties.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakStructuralRolesTransImpl(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the weak structural roles relation for the given network with a
   * notion of compatibility between ties. Runs in O(m * n log(n)) time and needs
   * O(n) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param comparator   a binary predicate that says whether the first tie's
   *                     value is compatible with the second one.
   * @return the weak structural roles relation for the given network.
   */
  public static <V> BinaryRelation weakStructuralRolesRelation(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator) {
    return weakStructuralRolesTransImpl(n, positionView, comparator);
  }

  private static <T> BinaryRelationOrRanking weakStructuralRolesTransImpl(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {
    return new LazyUncachedBinaryRelationMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      if (degi > degj) {
        return false;
      }
      ToIntFunction<T> getTargetTransposedj = r -> {
        int target = positionView.tieTarget(i, j, j, r);
        if (target == j) {
          return i;
        } else if (target == i) {
          return j;
        }
        return target;
      };

      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(getTargetTransposedj::applyAsInt)).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi, ++posj) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = getTargetTransposedj.applyAsInt(rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = getTargetTransposedj.applyAsInt(rj);
        }
        if (ritarget != rjtarget || !comparator.test(ri, rj)) {
          return false;
        }
      }
      return posi == degi;
    });
  }

}
