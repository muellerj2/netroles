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

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationOrRanking;
import ch.ethz.sn.visone3.roles.impl.structures.ReducibleRelationOrRanking;
import ch.ethz.sn.visone3.roles.impl.structures.RelationBuilderServiceImpl;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * Provides algorithms to compute relative roles, role closure and role interior
 * for regular roles on the lattice of rankings and binary relations.
 */
public class RegularRankedRoles {

  private RegularRankedRoles() {
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given unweighted network. Runs in O(m n) time and needs O(n^2)
   * additional space.
   * 
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  public static Ranking rankedRegularRoles(int n, NetworkView<?, ?> positionView,
      Ranking rankingRelativeTo) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo,
        Rankings.universal(n));
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given unweighted network. Runs in O(m n) time and needs O(n^2)
   * additional space.
   * 
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  public static Ranking rankedRegularRoles(int n,
      TransposableNetworkView<?, ?> positionView, Ranking rankingRelativeTo) {
    return rankedRegularRoles(n, positionView, rankingRelativeTo, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given unweighted network. Runs in O(m n) time and needs
   * O(n^2) additional space.
   * 
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static BinaryRelation rankedRegularRoles(int n, NetworkView<?, ?> positionView,
      BinaryRelation relationRelativeTo) {
    return refiningRankedRegularRoles(n, positionView, relationRelativeTo,
        BinaryRelations.universal(n));
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given unweighted network. Runs in O(m n) time and needs
   * O(n^2) additional space.
   * 
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static BinaryRelation rankedRegularRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation relationRelativeTo) {
    return rankedRegularRoles(n, positionView, relationRelativeTo, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the specified ranking on the given network. Runs in O(m n) time
   * and needs O(n^2) additional space.
   * 
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to the specified ranking the given network.
   */
  public static Ranking refiningRankedRegularRoles(int n, NetworkView<?, ?> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking) {
    return rankedRegularRolesImpl(n, positionView, rankingRelativeTo, refinedRanking);
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the specified ranking on the given network. Runs in O(m^2) time
   * and needs O(n^2) additional space.
   * 
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to the specified ranking on the given network.
   */
  public static Ranking refiningRankedRegularRoles(int n,
      TransposableNetworkView<?, ?> positionView, Ranking rankingRelativeTo,
      Ranking refinedRanking) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo, refinedRanking,
        MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network. Runs in O(m^2) time
   * and needs O(n^2) additional space.
   * 
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the relation to refine.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static BinaryRelation refiningRankedRegularRoles(int n,
      NetworkView<?, ?> positionView, BinaryRelation relationRelativeTo,
      BinaryRelation refinedRelation) {
    return rankedRegularRolesImpl(n, positionView, relationRelativeTo, refinedRelation);
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network. Runs in O(m^2) time
   * and needs O(n^2) additional space.
   * 
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the relation to refine.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static BinaryRelation refiningRankedRegularRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation relationRelativeTo,
      BinaryRelation refinedRelation) {
    return refiningRankedRegularRoles(n, positionView, relationRelativeTo, refinedRelation,
        MiscUtils.alwaysTrue());
  }

  private static <T, U> BinaryRelationOrRanking rankedRegularRolesImpl(int n,
      NetworkView<T, U> positionView, RelationBase relationRelativeTo,
      RelationBase refinedRelation) {
    // TODO: make lazy
    boolean[][] dominated = new boolean[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j : relationRelativeTo.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          dominated[j][positionView.inverseTieTarget(i, r)] = true;
        }
      }
    }
    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      loop: for (int j : refinedRelation.iterateInRelationFrom(i)) {
        for (T r : positionView.ties(i)) {
          if (!dominated[positionView.tieTarget(i, r)][j]) {
            continue loop;
          }
        }
        builder.add(i, j);
      }
    }
    return builder.build();
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given network with weakly ordered ties. Runs in O(m n) time
   * and O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param comparator        a comparator that weakly orders ties.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  public static <V> Ranking rankedRegularRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo, Rankings.universal(n),
        comparator);
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given network with weakly ordered ties. Runs in O(m^2) time
   * and O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param comparator        a comparator that weakly orders ties.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  public static <V> Ranking rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Comparator<? super V> comparator) {
    return rankedRegularRoles(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given network with weakly ordered ties. Runs in O(m n) time
   * and O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param comparator         a comparator that weakly orders ties.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static <V> BinaryRelation rankedRegularRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation relationRelativeTo,
      Comparator<? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, relationRelativeTo,
        BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given network with weakly ordered ties. Runs in O(m^2) time
   * and O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param comparator         a comparator that weakly orders ties.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static <V> BinaryRelation rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator) {
    return rankedRegularRoles(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the specified ranking on the given network with weakly ordered
   * ties. Runs in O(m n) time and needs O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @param comparator        a comparator that weakly orders ties.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to the specified ranking on the given network.
   */
  public static <V> Ranking refiningRankedRegularRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking rankingRelativeTo,
      Ranking refinedRanking, Comparator<? super V> comparator) {
    return rankedRegularRolesImpl(n, positionView, rankingRelativeTo, refinedRanking, comparator);
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the given network with weakly ordered ties. Runs in O(m^2) time
   * and needs O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @param comparator        a comparator that weakly orders ties.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to the specified ranking on the given network.
   */
  public static <V> Ranking refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking, Comparator<? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network with weakly ordered
   * ties. Runs in O(m n) time and needs O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the ranking to refine.
   * @param comparator         a comparator that weakly orders ties.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static <V> BinaryRelation refiningRankedRegularRoles(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation relationRelativeTo,
      BinaryRelation refinedRelation, Comparator<? super V> comparator) {
    return rankedRegularRolesImpl(n, positionView, relationRelativeTo, refinedRelation, comparator);
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network with weakly ordered
   * edges. Runs in O(m^2) time and needs O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the ranking to refine.
   * @param comparator         a comparator that weakly orders ties.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static <V> BinaryRelation refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, relationRelativeTo, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  private static <V, T extends V, U extends V> BinaryRelationOrRanking rankedRegularRolesImpl(int n,
      NetworkView<T, U> positionView, RelationBase rankingRelativeTo,
      RelationBase refinedRanking, Comparator<? super V> comparator) {
    @SuppressWarnings("unchecked")
    U[][] maxValue = (U[][]) new Object[n][n];

    for (int i = 0; i < n; ++i) {
      for (int j : rankingRelativeTo.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          int k = positionView.inverseTieTarget(i, r);
          if (maxValue[j][k] == null || comparator.compare(r, maxValue[j][k]) > 0) {
            maxValue[j][k] = r;
          }
        }
      }
    }
    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      loop: for (int j : refinedRanking.iterateInRelationFrom(i)) {
        for (T r : positionView.ties(i)) {
          int k = positionView.tieTarget(i, r);
          if (maxValue[k][j] == null || comparator.compare(r, maxValue[k][j]) > 0) {
            continue loop;
          }
        }
        builder.add(i, j);
      }
    }
    return builder.build();
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given network with partially ordered ties. Runs in O(m^2) time
   * and O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param comparator        a comparator that partially orders ties.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  public static <V> Ranking rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, PartialComparator<? super V> comparator) {
    return rankedRegularRoles(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given network with partially ordered ties. Runs in O(m^2)
   * time and O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param comparator         a comparator that partially orders ties.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static <V> BinaryRelation rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator) {
    return rankedRegularRoles(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the specified ranking on the given network with partially ordered
   * ties. Runs in O(m^2) time and needs O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @param comparator        a comparator that partially orders ties.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to specified ranking on the given network.
   */
  public static <V> Ranking refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking, PartialComparator<? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network with partially
   * ordered ties. Runs in O(m^2) time and needs O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the relation that is refined by the result
   * @param comparator         a comparator that partially orders ties.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static <V> BinaryRelation refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      PartialComparator<? super V> comparator) {

    return refiningRankedRegularRoles(n, positionView, relationRelativeTo, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that is regular relative to the specified
   * ranking on the given network with a notion of compatibility between the ties.
   * Runs in O(m^2) time and O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param comparator        a binary predicate that says whether the first tie's
   *                          value is compatible with the second one.
   * @return the maximum ranking that is regular relative to the specified ranking
   *         on the given network.
   */
  private static <V> Ranking rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, rankingRelativeTo, Rankings.universal(n),
        comparator);
  }

  /**
   * Computes the maximum relation that is regular relative to the specified
   * relation on the given network with a notion of compatibility between the
   * ties. Runs in O(m^2) time and O(n^2) additional space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param comparator         a binary predicate that says whether the first
   *                           tie's value is compatible with the second one.
   * @return the maximum relation that is regular relative to specified relation
   *         on the given network.
   */
  public static <V> BinaryRelation rankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    return refiningRankedRegularRoles(n, positionView, relationRelativeTo,
        BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is regular
   * relative to the given network with a notion of compatibility between ties.
   * Runs in O(m^2) time and needs O(n^2) additional space.
   * 
   * @param <V>               type representing ties.
   * @param n                 number of nodes.
   * @param positionView      network as viewed from the position of the
   *                          individual nodes.
   * @param rankingRelativeTo the ranking the output ranking should be regular
   *                          relative to.
   * @param refinedRanking    the ranking to refine.
   * @param comparator        a binary predicate that says whether the first tie's
   *                          value is compatible with the second one.
   * @return the maximum ranking that refines a given ranking and is regular
   *         relative to the specified ranking on the given network.
   */
  private static <V> Ranking refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking,
      BiPredicate<? super V, ? super V> comparator) {
    return rankedRegularRolesImpl(n, positionView, rankingRelativeTo, refinedRanking, comparator);
  }

  /**
   * Computes the maximum relation that refines a given relation and is regular
   * relative to the specified relation on the given network with a notion of
   * compatibility between ties. Runs in O(m^2) time and needs O(n^2) additional
   * space.
   * 
   * @param <V>                type representing ties.
   * @param n                  number of nodes.
   * @param positionView       network as viewed from the position of the
   *                           individual nodes.
   * @param relationRelativeTo the relation the output relation should be regular
   *                           relative to.
   * @param refinedRelation    the relation to refine.
   * @param comparator         a binary predicate that says whether the first
   *                           tie's value is compatible with the second one.
   * @return the maximum relation that refines a given relation and is regular
   *         relative to the specified relation on the given network.
   */
  public static <V> BinaryRelation refiningRankedRegularRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      BiPredicate<? super V, ? super V> comparator) {

    return rankedRegularRolesImpl(n, positionView, relationRelativeTo, refinedRelation, comparator);
  }

  private static <V, T extends V> BinaryRelationOrRanking rankedRegularRolesImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView, RelationBase relationRelativeTo,
      RelationBase refinedRelation, BiPredicate<? super V, ? super V> comparator) {

    // TODO: make lazy
    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      loop: for (int j : refinedRelation.iterateInRelationFrom(i)) {
        for (T ri : positionView.ties(i, j, i)) {
          int targeti = positionView.tieTarget(i, j, i, ri);
          boolean matched = false;
          for (T rj : positionView.ties(i, j, j)) {
            int targetj = positionView.tieTarget(i, j, j, rj);
            if (relationRelativeTo.contains(targeti, targetj) && comparator.test(ri, rj)) {
              matched = true;
              break;
            }
          }
          if (!matched) {
            continue loop;
          }
        }
        builder.add(i, j);
      }
    }
    return builder.build();
  }

  /**
   * Computes the regular interior of the specified ranking for the given
   * unweighted network. Runs in O(m n) time and needs O(n^2) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param ranking      initial ranking whose interior is sought.
   * @return the regular interior of the ranking for the given network on the
   *         lattice of rankings.
   */
  public static Ranking computeRankedRolesInterior(int n, NetworkView<?, ?> positionView,
      Ranking ranking) {

    return computeRankedRolesInteriorImpl(n, positionView, ranking);
  }

  /**
   * Computes the regular interior of the specified relation for a given
   * unweighted network. Runs in O(m n) time and needs O(n^2) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param relation     initial relation whose interior is sought.
   * @return the regular interior of the relation for the given network on the
   *         lattice of binary relations.
   */
  public static BinaryRelation computeRankedRolesInterior(int n,
      NetworkView<?, ?> positionView, BinaryRelation relation) {

    return computeRankedRolesInteriorImpl(n, positionView, relation);
  }

  private static BinaryRelationOrRanking computeRankedRolesInteriorImpl(int n,
      NetworkView<?, ?> positionView, RelationBase relation) {
    return computeRankedRolesInteriorImpl(n, new NetworkView[] { positionView },
        new boolean[] { false }, relation);
  }

  private static <U> int[][] countDominations(int n, NetworkView<?, U> positionView,
      RelationBase relation) {
    int[][] dominationCounts = new int[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          ++dominationCounts[j][positionView.inverseTieTarget(i, r)];
        }
      }
    }
    return dominationCounts;
  }

  private static <T> boolean checkDomination(int i, int j, NetworkView<T, ?> positionView,
      int[][] dominationCounts) {
    for (T r : positionView.ties(i)) {
      if (dominationCounts[positionView.tieTarget(i, r)][j] == 0) {
        return false;
      }
    }
    return true;
  }

  private static <U> void removeOrdering(int i, int j, NetworkView<?, U> positionView,
      ReducibleRelationOrRanking ranking, PrimitiveList.OfInt stackSrc,
      PrimitiveList.OfInt stackTrgt, int[][] dominationCounts) {

    for (U r : positionView.inverseTies(j)) {
      int t = positionView.inverseTieTarget(j, r);
      if (--dominationCounts[i][t] == 0) {
        for (U r2 : positionView.inverseTies(i)) {
          int s = positionView.inverseTieTarget(i, r2);
          if (ranking.contains(s, t)) {
            ranking.remove(s, t);
            stackSrc.addInt(s);
            stackTrgt.addInt(t);
          }
        }
      }
    }
  }

  private static BinaryRelationOrRanking computeRankedRolesInteriorImpl(int n,
      NetworkView<?, ?>[] views, boolean[] invertRelation, RelationBase relation) {

    int nViews = views.length;
    int[][][] dominationCounts = new int[nViews][][];

    for (int i = 0; i < nViews; ++i) {
      dominationCounts[i] = countDominations(n, views[i],
          invertRelation[i] ? relation.invert() : relation);
    }

    PrimitiveList.OfInt stackSrc = Mappings.newIntList();
    PrimitiveList.OfInt stackTrgt = Mappings.newIntList();
    BinaryRelationMatrixImpl.Builder builder = new BinaryRelationMatrixImpl.Builder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationFrom(i)) {
        boolean dominated = true;
        for (int k = 0; k < nViews && dominated; ++k) {
          dominated = checkDomination(i, j, views[k], dominationCounts[k]);
        }
        if (dominated) {
          builder.add(i, j);
        } else {
          stackSrc.addInt(i);
          stackTrgt.addInt(j);
        }
      }
    }
    ReducibleRelationOrRanking reducibleRelation = builder.build();

    while (!stackSrc.isEmpty()) {
      int pos = stackSrc.size() - 1;
      int i = stackSrc.getInt(pos);
      int j = stackTrgt.getInt(pos);
      stackSrc.removeIndex(pos);
      stackTrgt.removeIndex(pos);

      for (int k = 0; k < nViews; ++k) {
        boolean invert = invertRelation[k];
        removeOrdering(invert ? j : i, invert ? i : j, views[k], reducibleRelation, stackSrc,
            stackTrgt, dominationCounts[k]);
      }
    }

    return reducibleRelation;
  }

  /**
   * Computes the ecological closure of a given ranking for an unweighted network.
   * Runs in O(n^3) time and needs O(n^2) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param ranking      initial ranking whose closure is sought.
   * @return the ecological closure of the ranking for the given network on the
   *         lattice of rankings.
   */
  public static Ranking computeRankedRolesClosure(int n, NetworkView<?, ?> positionView,
      Ranking ranking) {
    return computeRankedRolesClosureImpl(n, positionView, ranking);
  }

  private static <U> boolean[][] testDominations(int n, NetworkView<?, U> positionView,
      RelationBase relation) {
    boolean[][] dominations = new boolean[n][n];

    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          dominations[j][positionView.inverseTieTarget(i, r)] = true;
        }
      }
    }

    return dominations;
  }

  private static <T> int countNondominatedNeighbors(int i, int j,
      NetworkView<T, ?> positionView, boolean[][] dominations) {
    int count = 0;
    for (T r : positionView.ties(i)) {
      if (!dominations[positionView.tieTarget(i, r)][j]) {
        ++count;
      }
    }
    return count;
  }

  private static <U> void updateNonnominatedNeighborsRanking(int i, int j, int n,
      NetworkView<?, U> positionView, boolean[][] closureOrdering, boolean[][] dominations,
      int[][] totalNondominatedNeighborsCounts, PrimitiveList.OfInt stackSrc,
      PrimitiveList.OfInt stackTrgt) {

    for (U r : positionView.inverseTies(j)) {
      int t = positionView.inverseTieTarget(j, r);
      if (!dominations[i][t]) {
        dominations[i][t] = true;
        for (U r2 : positionView.inverseTies(i)) {
          int s = positionView.inverseTieTarget(i, r2);
          if (!closureOrdering[s][t] && --totalNondominatedNeighborsCounts[s][t] == 0) {
            for (int k = 0; k < n; ++k) {
              if (closureOrdering[k][s] && !closureOrdering[k][t]) {
                for (int l = 0; l < n; ++l) {
                  if (!closureOrdering[k][l] && closureOrdering[t][l]) {
                    closureOrdering[k][l] = true;
                    stackSrc.addInt(k);
                    stackTrgt.addInt(l);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static Ranking computeRankedRolesClosureImpl(int n,
      NetworkView<?, ?> positionView, Ranking ranking) {
    return computeRankedRolesClosureImpl(n, new NetworkView[] { positionView },
        new boolean[] { false }, ranking);
  }

  private static Ranking computeRankedRolesClosureImpl(int n, NetworkView<?, ?>[] views,
      boolean[] inverseRelation, Ranking ranking) {

    final int nViews = views.length;
    boolean[][][] dominations = new boolean[nViews][][];
    int[][] totalNondominatedNeighborCounts = new int[n][n];

    for (int k = 0; k < nViews; ++k) {
      dominations[k] = testDominations(n, views[k],
          inverseRelation[k] ? ranking.invert() : ranking);
    }

    PrimitiveList.OfInt stackSrc = Mappings.newIntList();
    PrimitiveList.OfInt stackTrgt = Mappings.newIntList();
    boolean[][] closureOrdering = new boolean[n][n];

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : ranking.iterateInRelationFrom(i)) {
        closureOrdering[i][j] = true;
        builder.add(i, j);
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (!closureOrdering[i][j]) {
          boolean dominated = true;
          for (int k = 0; k < nViews; ++k) {
            int count = countNondominatedNeighbors(i, j, views[k], dominations[k]);
            totalNondominatedNeighborCounts[i][j] += count;
            dominated = dominated && (count == 0);
          }
          if (dominated) {
            for (int k = 0; k < n; ++k) {
              if (closureOrdering[k][i] && !closureOrdering[k][j]) {
                for (int l = 0; l < n; ++l) {
                  if (!closureOrdering[k][l] && closureOrdering[j][l]) {
                    closureOrdering[k][l] = true;
                    stackSrc.addInt(k);
                    stackTrgt.addInt(l);
                  }
                }
              }
            }
          }
        }
      }
    }

    while (!stackSrc.isEmpty()) {
      int pos = stackSrc.size() - 1;
      int i = stackSrc.getInt(pos);
      int j = stackTrgt.getInt(pos);
      stackSrc.removeIndex(pos);
      stackTrgt.removeIndex(pos);
      builder.add(i, j);
      for (int k = 0; k < nViews; ++k) {
        boolean invert = inverseRelation[k];
        updateNonnominatedNeighborsRanking(invert ? j : i, invert ? i : j, n, views[k],
            closureOrdering, dominations[k], totalNondominatedNeighborCounts, stackSrc, stackTrgt);
      }
    }
    return builder.build();
  }

  private static <U> void updateNonnominatedNeighborsRelation(int i, int j, int n,
      NetworkView<?, U> positionView, boolean[][] closureOrdering, boolean[][] dominations,
      int[][] totalNondominatedNeighborsCounts, PrimitiveList.OfInt stackSrc,
      PrimitiveList.OfInt stackTrgt) {

    for (U r : positionView.inverseTies(j)) {
      int t = positionView.inverseTieTarget(j, r);
      if (!dominations[i][t]) {
        dominations[i][t] = true;
        for (U r2 : positionView.inverseTies(i)) {
          int s = positionView.inverseTieTarget(i, r2);
          if (!closureOrdering[s][t] && --totalNondominatedNeighborsCounts[s][t] == 0) {
            closureOrdering[s][t] = true;
            stackSrc.addInt(s);
            stackTrgt.addInt(t);
          }
        }
      }
    }
  }

  /**
   * Computes the ecological closure of a given binary relation for an unweighted
   * network. Runs in O(m n) time and needs O(n^2) additional space.
   * 
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param relation     initial relation whose closure is sought.
   * @return the ecological closure of the relation for the given network on the
   *         lattice of binary relations.
   */
  public static BinaryRelation computeRankedRolesClosure(int n,
      NetworkView<?, ?> positionView, BinaryRelation relation) {
    return computeRankedRolesClosureImpl(n, positionView, relation);
  }

  private static BinaryRelation computeRankedRolesClosureImpl(int n,
      NetworkView<?, ?> positionView, BinaryRelation relation) {
    return computeRankedRolesClosureImpl(n, new NetworkView[] { positionView },
        new boolean[] { false }, relation);
  }

  private static BinaryRelation computeRankedRolesClosureImpl(int n,
      NetworkView<?, ?>[] views, boolean[] inverseRelation, BinaryRelation relation) {

    final int nViews = views.length;
    boolean[][][] dominations = new boolean[nViews][][];
    int[][] totalNondominatedNeighborCounts = new int[n][n];

    for (int k = 0; k < nViews; ++k) {
      dominations[k] = testDominations(n, views[k],
          inverseRelation[k] ? relation.invert() : relation);
    }

    PrimitiveList.OfInt stackSrc = Mappings.newIntList();
    PrimitiveList.OfInt stackTrgt = Mappings.newIntList();
    boolean[][] closureOrdering = new boolean[n][n];

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationFrom(i)) {
        closureOrdering[i][j] = true;
        builder.add(i, j);
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (!closureOrdering[i][j]) {
          boolean dominated = true;
          for (int k = 0; k < nViews; ++k) {
            int count = countNondominatedNeighbors(i, j, views[k], dominations[k]);
            totalNondominatedNeighborCounts[i][j] += count;
            dominated = dominated && (count == 0);
          }
          if (dominated) {
            closureOrdering[i][j] = true;
            stackSrc.addInt(i);
            stackTrgt.addInt(j);
          }
        }
      }
    }

    while (!stackSrc.isEmpty()) {
      int pos = stackSrc.size() - 1;
      int i = stackSrc.getInt(pos);
      int j = stackTrgt.getInt(pos);
      stackSrc.removeIndex(pos);
      stackTrgt.removeIndex(pos);
      builder.add(i, j);
      for (int k = 0; k < nViews; ++k) {
        boolean invert = inverseRelation[k];
        updateNonnominatedNeighborsRelation(invert ? j : i, invert ? i : j, n, views[k],
            closureOrdering, dominations[k], totalNondominatedNeighborCounts, stackSrc, stackTrgt);
      }
    }
    return builder.build();
  }

  /**
   * Computes the ecological closure of a given ranking for a network with weakly
   * ordered ties. Runs in O(m^2+n^3) time and needs O(n^2) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param ranking      initial ranking whose closure is sought.
   * @param comparator   a comparator that weakly orders ties.
   * @return the ecological closure of the ranking for the given network on the
   *         lattice of rankings.
   */
  public static <V> Ranking computeRankedRolesClosure(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking ranking,
      Comparator<? super V> comparator) {

    return computeRankedRolesClosureImpl(n, positionView, ranking, comparator);
  }

  @SuppressWarnings("unchecked")
  private static <V> Ranking computeRankedRolesClosureImpl(int n,
      NetworkView<? extends V, ? extends V> positionView, Ranking ranking,
      Comparator<? super V> comparator) {
    return computeRankedRolesClosureImpl(n, new NetworkView[] { positionView },
        new boolean[] { false }, ranking, comparator);
  }

  private static <U> void testDominations(int n, NetworkView<?, U> positionView,
      RelationBase relation, Comparator<? super U> comparator, U[][] maxDominationsValue) {

    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          int k = positionView.inverseTieTarget(i, r);
          U maxValue = maxDominationsValue[j][k];
          if (maxValue == null || comparator.compare(maxValue, r) < 0) {
            maxDominationsValue[j][k] = r;
          }
        }
      }
    }
  }

  private static <V, T extends V, U extends V> int countNondominatedNeighbors(int i, int j,
      NetworkView<T, ?> positionView, Comparator<? super V> comparator,
      U[][] maxDominationsValue) {

    int count = 0;
    for (T r : positionView.ties(i)) {
      int k = positionView.tieTarget(i, r);
      U maxValue = maxDominationsValue[k][j];
      if (maxValue == null || comparator.compare(r, maxValue) > 0) {
        ++count;
      }
    }
    return count;
  }

  private static <U> void updateNondominatedNeighborsRanking(int i, int j, int n,
      NetworkView<?, U> positionView, Comparator<? super U> comparator,
      boolean[][] closureOrdering, U[][] maxDominationsValue,
      int[][] totalNondominatedNeighborCount, PrimitiveList.OfInt stackSrc,
      PrimitiveList.OfInt stackTrgt) {

    for (U r : positionView.inverseTies(j)) {
      int t = positionView.inverseTieTarget(j, r);
      U prevDomValue = maxDominationsValue[i][t];
      if (prevDomValue == null || comparator.compare(r, prevDomValue) > 0) {
        maxDominationsValue[i][t] = r;
        for (U r2 : positionView.inverseTies(i)) {
          int s = positionView.inverseTieTarget(i, r2);
          if (!closureOrdering[s][t]
              && (prevDomValue == null || comparator.compare(r2, prevDomValue) > 0)
              && comparator.compare(r, r2) >= 0) {
            if (--totalNondominatedNeighborCount[s][t] == 0) {
              for (int k = 0; k < n; ++k) {
                if (closureOrdering[k][s] && !closureOrdering[k][t]) {
                  for (int l = 0; l < n; ++l) {
                    if (!closureOrdering[k][l] && closureOrdering[t][l]) {
                      closureOrdering[k][l] = true;
                      stackSrc.addInt(k);
                      stackTrgt.addInt(l);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static <V, U extends V> Ranking computeRankedRolesClosureImpl(int n,
      NetworkView<? extends V, U>[] views, boolean[] inverseRanking, Ranking ranking,
      Comparator<? super V> comparator) {

    final int nViews = views.length;
    int[][] totalNondominatedNeighborCount = new int[n][n];
    @SuppressWarnings("unchecked")
    U[][][] maxDominationsValue = (U[][][]) new Object[nViews][n][n];

    for (int k = 0; k < nViews; ++k) {
      testDominations(n, views[k], inverseRanking[k] ? ranking.invert() : ranking, comparator,
          maxDominationsValue[k]);
    }

    PrimitiveList.OfInt stackSrc = Mappings.newIntList();
    PrimitiveList.OfInt stackTrgt = Mappings.newIntList();
    boolean[][] closureOrdering = new boolean[n][n];

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : ranking.iterateInRelationFrom(i)) {
        closureOrdering[i][j] = true;
        builder.add(i, j);
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (!closureOrdering[i][j]) {
          for (int k = 0; k < nViews; ++k) {
            int count = countNondominatedNeighbors(i, j, views[k], comparator,
                maxDominationsValue[k]);
            totalNondominatedNeighborCount[i][j] += count;
          }
          if (totalNondominatedNeighborCount[i][j] == 0) {
            for (int k = 0; k < n; ++k) {
              if (closureOrdering[k][i] && !closureOrdering[k][j]) {
                for (int l = 0; l < n; ++l) {
                  if (!closureOrdering[k][l] && closureOrdering[j][l]) {
                    closureOrdering[k][l] = true;
                    stackSrc.addInt(k);
                    stackTrgt.addInt(l);
                  }
                }
              }
            }
          }
        }
      }
    }

    while (!stackSrc.isEmpty()) {
      int pos = stackSrc.size() - 1;
      int i = stackSrc.getInt(pos);
      int j = stackTrgt.getInt(pos);
      stackSrc.removeIndex(pos);
      stackTrgt.removeIndex(pos);
      builder.add(i, j);
      for (int k = 0; k < nViews; ++k) {
        boolean invert = inverseRanking[k];
        updateNondominatedNeighborsRanking(invert ? j : i, invert ? i : j, n, views[k], comparator,
            closureOrdering, maxDominationsValue[k], totalNondominatedNeighborCount, stackSrc,
            stackTrgt);
      }
    }
    return builder.build();
  }

  /**
   * Computes the regular closure of a given relation for a network with weakly
   * ordered ties. Runs in O(m^2) time and needs O(n^2) additional space.
   * 
   * @param <V>          type representing ties.
   * @param n            number of nodes.
   * @param positionView network as viewed from the position of the individual
   *                     nodes.
   * @param relation     initial relation whose closure is sought.
   * @param comparator   a comparator that weakly orders ties.
   * @return the ecological closure of the relation for the given network on the
   *         lattice of binary relations.
   */
  public static <V> BinaryRelation computeRankedRolesClosure(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation relation,
      Comparator<? super V> comparator) {

    return computeRankedRolesClosureImpl(n, positionView, relation, comparator);
  }

  @SuppressWarnings("unchecked")
  private static <V> BinaryRelation computeRankedRolesClosureImpl(int n,
      NetworkView<? extends V, ? extends V> positionView, BinaryRelation relation,
      Comparator<? super V> comparator) {
    return computeRankedRolesClosureImpl(n, new NetworkView[] { positionView },
        new boolean[] { false }, relation, comparator);
  }

  private static <U> void updateNondominatedNeighborsRelation(int i, int j, int n,
      NetworkView<?, U> positionView, Comparator<? super U> comparator,
      boolean[][] closureOrdering, U[][] maxDominationsValue,
      int[][] totalNondominatedNeighborCount, PrimitiveList.OfInt stackSrc,
      PrimitiveList.OfInt stackTrgt) {

    for (U r : positionView.inverseTies(j)) {
      int t = positionView.inverseTieTarget(j, r);
      U prevDomValue = maxDominationsValue[i][t];
      if (prevDomValue == null || comparator.compare(r, prevDomValue) > 0) {
        maxDominationsValue[i][t] = r;
        for (U r2 : positionView.inverseTies(i)) {
          int s = positionView.inverseTieTarget(i, r2);
          if (!closureOrdering[s][t]
              && (prevDomValue == null || comparator.compare(r2, prevDomValue) > 0)
              && comparator.compare(r, r2) >= 0) {
            if (--totalNondominatedNeighborCount[s][t] == 0) {
              closureOrdering[s][t] = true;
              stackSrc.addInt(s);
              stackTrgt.addInt(t);
            }
          }
        }
      }
    }
  }

  private static <V, U extends V> BinaryRelation computeRankedRolesClosureImpl(int n,
      NetworkView<? extends V, U>[] views, boolean[] inverseRelation,
      BinaryRelation relation, Comparator<? super V> comparator) {

    final int nViews = views.length;
    int[][] totalNondominatedNeighborCount = new int[n][n];
    @SuppressWarnings("unchecked")
    U[][][] maxDominationsValue = (U[][][]) new Object[nViews][n][n];

    for (int k = 0; k < nViews; ++k) {
      testDominations(n, views[k], inverseRelation[k] ? relation.invert() : relation, comparator,
          maxDominationsValue[k]);
    }

    PrimitiveList.OfInt stackSrc = Mappings.newIntList();
    PrimitiveList.OfInt stackTrgt = Mappings.newIntList();
    boolean[][] closureOrdering = new boolean[n][n];

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : relation.iterateInRelationFrom(i)) {
        closureOrdering[i][j] = true;
        builder.add(i, j);
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (!closureOrdering[i][j]) {
          for (int k = 0; k < nViews; ++k) {
            int count = countNondominatedNeighbors(i, j, views[k], comparator,
                maxDominationsValue[k]);
            totalNondominatedNeighborCount[i][j] += count;
          }
          if (totalNondominatedNeighborCount[i][j] == 0) {
            closureOrdering[i][j] = true;
            stackSrc.addInt(i);
            stackTrgt.addInt(j);
          }
        }
      }
    }

    while (!stackSrc.isEmpty()) {
      int pos = stackSrc.size() - 1;
      int i = stackSrc.getInt(pos);
      int j = stackTrgt.getInt(pos);
      stackSrc.removeIndex(pos);
      stackTrgt.removeIndex(pos);
      builder.add(i, j);
      for (int k = 0; k < nViews; ++k) {
        boolean invert = inverseRelation[k];
        updateNondominatedNeighborsRelation(invert ? j : i, invert ? i : j, n, views[k], comparator,
            closureOrdering, maxDominationsValue[k], totalNondominatedNeighborCount, stackSrc,
            stackTrgt);
      }
    }
    return builder.build();
  }

}
