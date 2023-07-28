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
 * along with visone3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sn.visone3.roles.impl.algorithms;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.structures.IntDistanceMatrixImpl;
import ch.ethz.sn.visone3.roles.impl.structures.LazyIntDistanceMatrixImpl;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RoleDistanceAlgorithms {

  private RoleDistanceAlgorithms() {
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, Ranking rankingRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, mismatchPenalty);
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, BinaryRelation relationRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, mismatchPenalty);
  }

  private static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeToImpl(
      int n, NetworkView<T, U> positionView, RelationBase structureRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {

    // there is probably room for more laziness here
    boolean[][] dominated = new boolean[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j : structureRelativeTo.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          int target = positionView.inverseTieTarget(i, r);
          dominated[j][target] = true;
        }
      }
    }
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        if (!dominated[target][j]) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        mismatchPenalty);
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, BinaryRelation relationRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        mismatchPenalty);
  }

  private static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeToImpl(
      int n, NetworkView<T, U> positionView, RelationBase structureRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    @SuppressWarnings("unchecked")
    U[][] dominated = (U[][]) new Object[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j : structureRelativeTo.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          int target = positionView.inverseTieTarget(i, r);
          if (dominated[j][target] == null || comparator.compare(dominated[j][target], r) < 0) {
            dominated[j][target] = r;
          }
        }
      }
    }
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        if (dominated[target][j] == null || comparator.compare(dominated[target][j], r) < 0) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        mismatchPenalty);
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, BinaryRelation relationRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        mismatchPenalty);
  }

  private static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeToImpl(
      int n, NetworkView<T, U> positionView, RelationBase structureRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {

    // there is probably room for more laziness here
    @SuppressWarnings("unchecked")
    ArrayList<U>[][] dominatedValues = new ArrayList[n][n];

    for (int i = 0; i < n; ++i) {
      for (int j : structureRelativeTo.iterateInRelationTo(i)) {
        for (U r : positionView.inverseTies(i)) {
          int target = positionView.inverseTieTarget(i, r);
          ArrayList<U> dominated = dominatedValues[j][target];
          if (dominated == null) {
            dominatedValues[j][target] = dominated = new ArrayList<>();
            dominated.add(r);
          } else {
            boolean doInsert = true;
            for (int k = 0; k < dominated.size(); ++k) {
              PartialComparator.ComparisonResult compRes = comparator.compare(r, dominated.get(k));
              if (compRes == PartialComparator.ComparisonResult.EQUAL
                  || compRes == PartialComparator.ComparisonResult.LESS) {
                doInsert = false;
                break;
              } else if (compRes == PartialComparator.ComparisonResult.GREATER) {
                dominated.set(k, dominated.get(dominated.size() - 1));
                dominated.remove(dominated.size() - 1);
                --k;
              }
            }
            if (doInsert) {
              dominated.add(r);
            }
          }
        }
      }
    }
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        ArrayList<U> dominators = dominatedValues[target][j];
        boolean isDominated = false;
        if (dominators != null) {
          for (U r2 : dominators) {
            PartialComparator.ComparisonResult res = comparator.compare(r, r2);
            if (res == PartialComparator.ComparisonResult.EQUAL
                || res == PartialComparator.ComparisonResult.LESS) {
              isDominated = true;
              break;
            }
          }
        }
        if (!isDominated) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        mismatchPenalty);
  }

  private static <V, T extends V> int regularRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int errsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      boolean dominated = false;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (structureRelativeTo.contains(ritarget, rjtarget)) {
          if (comparator.test(ri, rj)) {
            dominated = true;
            break;
          }
        }
      }
      if (!dominated) {
        errsum += mismatchPenalty.applyAsInt(ri);
      }
    }
    return errsum;
  }

  private static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeToImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> regularRolesDistanceRelativeToAt(i, j,
        positionView, structureRelativeTo, comparator, mismatchPenalty));
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {

    // there is probably room for more laziness here
    int[] sortedVertices = PrimitiveCollections.countingSort(equivalenceRelativeTo).array();
    int classStart = 0;
    boolean[][] dominated = new boolean[n][n];
    for (int i = 0; i < n; ++i) {
      int vert = sortedVertices[i];
      int eqClass = equivalenceRelativeTo.getInt(vert);
      if (equivalenceRelativeTo.getInt(sortedVertices[classStart]) != eqClass) {
        classStart = i;
      }
      for (int j = classStart; j < n
          && equivalenceRelativeTo.getInt(sortedVertices[j]) == eqClass; ++j) {
        for (U r : positionView.inverseTies(vert)) {
          int target = positionView.inverseTieTarget(vert, r);
          dominated[sortedVertices[j]][target] = true;
        }
      }
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        if (!dominated[target][j]) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {

    @SuppressWarnings("unchecked")
    U[][] dominated = (U[][]) new Object[n][n];

    // there is probably room for more laziness here
    int[] sortedVertices = PrimitiveCollections.countingSort(equivalenceRelativeTo).array();
    int classStart = 0;
    for (int i = 0; i < n; ++i) {
      int vert = sortedVertices[i];
      int eqClass = equivalenceRelativeTo.getInt(vert);
      if (equivalenceRelativeTo.getInt(sortedVertices[classStart]) != eqClass) {
        classStart = i;
      }
      for (int j = classStart; j < n
          && equivalenceRelativeTo.getInt(sortedVertices[j]) == eqClass; ++j) {
        for (U r : positionView.inverseTies(vert)) {
          int target = positionView.inverseTieTarget(vert, r);
          int vert2 = sortedVertices[j];
          if (dominated[vert2][target] == null
              || comparator.compare(dominated[vert2][target], r) < 0) {
            dominated[vert2][target] = r;
          }
        }
      }
    }
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        if (dominated[target][j] == null || comparator.compare(dominated[target][j], r) < 0) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(
      int n, NetworkView<T, U> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {

    @SuppressWarnings("unchecked")
    ArrayList<U>[][] dominatedValues = new ArrayList[n][n];

    // there is probably room for more laziness here
    int[] sortedVertices = PrimitiveCollections.countingSort(equivalenceRelativeTo).array();
    int classStart = 0;
    for (int i = 0; i < n; ++i) {
      int vert = sortedVertices[i];
      int eqClass = equivalenceRelativeTo.getInt(vert);
      if (equivalenceRelativeTo.getInt(sortedVertices[classStart]) != eqClass) {
        classStart = i;
      }
      for (int j = classStart; j < n
          && equivalenceRelativeTo.getInt(sortedVertices[j]) == eqClass; ++j) {
        for (U r : positionView.inverseTies(vert)) {
          int target = positionView.inverseTieTarget(vert, r);
          int vert2 = sortedVertices[j];
          ArrayList<U> dominated = dominatedValues[vert2][target];
          if (dominated == null) {
            dominatedValues[vert2][target] = dominated = new ArrayList<>();
            dominated.add(r);
          } else {
            boolean doInsert = true;
            for (int k = 0; k < dominated.size(); ++k) {
              PartialComparator.ComparisonResult compRes = comparator.compare(r, dominated.get(k));
              if (compRes == PartialComparator.ComparisonResult.EQUAL
                  || compRes == PartialComparator.ComparisonResult.LESS) {
                doInsert = false;
                break;
              } else if (compRes == PartialComparator.ComparisonResult.GREATER) {
                dominated.set(k, dominated.get(dominated.size() - 1));
                dominated.remove(dominated.size() - 1);
                --k;
              }
            }
            if (doInsert) {
              dominated.add(r);
            }
          }
        }
      }
    }
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int errsum = 0;
      for (T r : positionView.ties(i)) {
        int target = positionView.tieTarget(i, r);
        ArrayList<U> dominators = dominatedValues[target][j];
        boolean isDominated = false;
        if (dominators != null) {
          for (U r2 : dominators) {
            PartialComparator.ComparisonResult res = comparator.compare(r, r2);
            if (res == PartialComparator.ComparisonResult.EQUAL
                || res == PartialComparator.ComparisonResult.LESS) {
              isDominated = true;
              break;
            }
          }
        }
        if (!isDominated) {
          errsum += mismatchPenalty.applyAsInt(r);
        }
      }
      return errsum;
    });
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  private static <V, T extends V> int regularRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int errsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      boolean dominated = false;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
          if (comparator.test(ri, rj)) {
            dominated = true;
            break;
          }
        }
      }
      if (!dominated) {
        errsum += mismatchPenalty.applyAsInt(ri);
      }
    }
    return errsum;
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> regularRolesDistanceRelativeToAt(i, j,
        positionView, equivalenceRelativeTo, comparator, mismatchPenalty));
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        substitutionCost);
  }

  private static <V, T extends V> int regularRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    int costsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int cost = substitutionCost.applyAsInt(ri, null);
      if (cost > 0) {
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
            cost = Math.min(cost, substitutionCost.applyAsInt(ri, rj));
            if (cost == 0) {
              break;
            }
          }
        }
      }
      costsum += cost;
    }
    return costsum;
  }

  private static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeToImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> regularRolesDistanceRelativeToAt(i, j,
        positionView, structureRelativeTo, comparator, substitutionCost));
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  private static <V, T extends V> int regularRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    int costsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int cost = substitutionCost.applyAsInt(ri, null);
      if (cost > 0) {
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
            if (comparator.test(ri, rj)) {
              cost = Math.min(cost, substitutionCost.applyAsInt(ri, rj));
              if (cost == 0) {
                break;
              }
            }
          }
        }
      }
      costsum += cost;
    }
    return costsum;
  }

  public static <V, T extends V> IntDistanceMatrix regularRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> regularRolesDistanceRelativeToAt(i, j,
        positionView, equivalenceRelativeTo, comparator, substitutionCost));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.alwaysTrue());
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.alwaysTrue());
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      BiPredicate<? super V, ? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator);
  }

  private static <V, T extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    builder.ensureNode(degi + degj - 1);
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int jpos = degi;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
          builder.addEdge(ipos, jpos);
        }
        ++jpos;
      }
      ++ipos;
    }
    return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
        Mappings.intRange(0, degi));
  }

  private static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeToImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, structureRelativeTo, comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      NetworkView<T, ? extends V> positionView, ConstMapping.OfInt equivalenceRelativeTo) {
    int maxclassCalc = 0;
    for (int k : equivalenceRelativeTo) {
      maxclassCalc = Math.max(maxclassCalc, k);
    }
    final int nClasses = maxclassCalc + 1;
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int[] countsi = new int[nClasses];
      int[] countsj = new int[nClasses];
      for (T ti : positionView.ties(i)) {
        int target = positionView.tieTarget(i, ti);
        ++countsi[equivalenceRelativeTo.getInt(target)];
      }
      for (T tj : positionView.ties(j)) {
        int target = positionView.tieTarget(j, tj);
        ++countsj[equivalenceRelativeTo.getInt(target)];
      }
      int sum = 0;
      for (int k = 0; k < nClasses; ++k) {
        sum += Math.max(countsi[k] - countsj[k], 0);
      }
      return sum;
    });
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo) {
    int maxclassCalc = 0;
    for (int k : equivalenceRelativeTo) {
      maxclassCalc = Math.max(maxclassCalc, k);
    }
    final int nClasses = maxclassCalc + 1;
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int[] countsi = new int[nClasses];
      int[] countsj = new int[nClasses];
      for (T ti : positionView.ties(i, j, i)) {
        int target = positionView.tieTarget(i, j, i, ti);
        ++countsi[equivalenceRelativeTo.getInt(target)];
      }
      for (T tj : positionView.ties(i, j, j)) {
        int target = positionView.tieTarget(i, j, j, tj);
        ++countsj[equivalenceRelativeTo.getInt(target)];
      }
      int sum = 0;
      for (int k = 0; k < nClasses; ++k) {
        sum += Math.max(countsi[k] - countsj[k], 0);
      }
      return sum;
    });
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  private static <V, T extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    builder.ensureNode(degi + degj - 1);
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int jpos = degi;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
          if (comparator.test(ri, rj)) {
            builder.addEdge(ipos, jpos);
          }
        }
        ++jpos;
      }
      ++ipos;
    }
    return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
        Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, equivalenceRelativeTo, comparator));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        mismatchPenalty);
  }

  private static <V, T extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj - 1);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
            builder.addEdge(ipos, jpos);
            weights.add(penalty);
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));

  }

  private static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeToImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, structureRelativeTo, comparator, mismatchPenalty));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  private static <V, T extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj - 1);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
            if (comparator.test(ri, rj)) {
              builder.addEdge(ipos, jpos);
              weights.add(penalty);
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, equivalenceRelativeTo, comparator, mismatchPenalty));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      Comparator<? super V> comparator, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView, Ranking rankingRelativeTo,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, rankingRelativeTo, comparator,
        substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeToImpl(n, positionView, relationRelativeTo, comparator,
        substitutionCost);
  }

  private static <V, T extends V, U extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, U> positionView, RelationBase structureRelativeTo,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj - 1);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      maxSubstCost += substFailureCost;
      int jpos = degi;
      for (T rj : positionView.ties(j, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
          int substCost = substitutionCost.applyAsInt(ri, rj);
          if (substCost < substFailureCost) {
            builder.addEdge(ipos, jpos);
            weights.add(substFailureCost - substCost);
          }
        }
        ++jpos;
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  private static <V, T extends V, U extends V> IntDistanceMatrix exactRolesDistanceRelativeToImpl(
      int n, TransposableNetworkView<T, U> positionView, RelationBase structureRelativeTo,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, structureRelativeTo, comparator, substitutionCost));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  private static <V, T extends V> int exactRolesDistanceRelativeToAt(int i, int j,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj - 1);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      if (substFailureCost > 0) {
        maxSubstCost += substFailureCost;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
            if (comparator.test(ri, rj)) {
              int substCost = substitutionCost.applyAsInt(ri, rj);
              if (substCost < substFailureCost) {
                builder.addEdge(ipos, jpos);
                weights.add(substFailureCost - substCost);
              }
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix exactRolesDistanceRelativeTo(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> exactRolesDistanceRelativeToAt(i, j,
        positionView, equivalenceRelativeTo, comparator, substitutionCost));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.alwaysTrue());
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, Comparator<? super V> comparator) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, PartialComparator<? super V> comparator) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator) {

    if (p == n) {
      return regularRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator,
          e -> 1);
    } else if (p == 1) {
      return exactRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, structureRelativeTo, comparator,
            e -> 1);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          structureRelativeTo, comparator);
    });
  }

  private static <V, T extends V> int pApproximateRegularRolesDistanceRelativeToAt(int i, int j,
      int p, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    builder.ensureNode(degi + p * degj - 1);
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int jpos = degi;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
          for (int q = 0; q < p; ++q) {
            builder.addEdge(ipos, jpos + q * degj);
          }
        }
        ++jpos;
      }
      ++ipos;
    }
    return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
        Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue());
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator) {

    if (p == n) {
      return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator,
          e -> 1);
    } else if (p == 1) {
      return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, equivalenceRelativeTo,
            comparator, e -> 1);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          equivalenceRelativeTo, comparator);
    });
  }

  private static <V, T extends V> int pApproximateRegularRolesDistanceRelativeToAt(int i, int j,
      int p, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    builder.ensureNode(degi + p * degj - 1);
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int jpos = degi;
      for (T rj : positionView.ties(i, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
          if (comparator.test(ri, rj)) {
            for (int q = 0; q < p; ++q) {
              builder.addEdge(ipos, jpos + q * degj);
            }
          }
        }
        ++jpos;
      }
      ++ipos;
    }
    return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
        Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    if (p == n) {
      return regularRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator,
          mismatchPenalty);
    } else if (p == 1) {
      return exactRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator,
          mismatchPenalty);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, structureRelativeTo, comparator,
            mismatchPenalty);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          structureRelativeTo, comparator, mismatchPenalty);
    });
  }

  private static <V, T extends V> int pApproximateRegularRolesDistanceRelativeToAt(int i, int j,
      int p, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj - 1);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
            for (int q = 0; q < p; ++q) {
              builder.addEdge(ipos, jpos + q * degj);
              weights.add(penalty);
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {

    if (p == n) {
      return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator,
          mismatchPenalty);
    } else if (p == 1) {
      return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator,
          mismatchPenalty);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, equivalenceRelativeTo,
            comparator, mismatchPenalty);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          equivalenceRelativeTo, comparator, mismatchPenalty);
    });
  }

  private static <V, T extends V> int pApproximateRegularRolesDistanceRelativeToAt(int i, int j,
      int p, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj - 1);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
            if (comparator.test(ri, rj)) {
              for (int q = 0; q < p; ++q) {
                builder.addEdge(ipos, jpos + q * degj);
                weights.add(penalty);
              }
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, structureRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    if (p == n) {
      return regularRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator,
          substitutionCost);
    } else if (p == 1) {
      return exactRolesDistanceRelativeToImpl(n, positionView, structureRelativeTo, comparator,
          substitutionCost);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, structureRelativeTo, comparator,
            substitutionCost);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          structureRelativeTo, comparator, substitutionCost);
    });
  }

  private static <V, T extends V, U extends V> int pApproximateRegularRolesDistanceRelativeToAt(
      int i, int j, int p, TransposableNetworkView<T, U> positionView,
      RelationBase structureRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj - 1);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      maxSubstCost += substFailureCost;
      int jpos = degi;
      for (T rj : positionView.ties(j, j, j)) {
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        if (structureRelativeTo.contains(ritarget, rjtarget) && comparator.test(ri, rj)) {
          int substCost = substitutionCost.applyAsInt(ri, rj);
          if (substCost < substFailureCost) {
            for (int q = 0; q < p; ++q) {
              builder.addEdge(ipos, jpos + q * degj);
              weights.add(substFailureCost - substCost);
            }
          }
        }
        ++jpos;
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return pApproximateRegularRolesDistanceRelativeTo(p, n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V> IntDistanceMatrix pApproximateRegularRolesDistanceRelativeTo(
      int p, int n, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    if (p == n) {
      return regularRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator,
          substitutionCost);
    } else if (p == 1) {
      return exactRolesDistanceRelativeTo(n, positionView, equivalenceRelativeTo, comparator,
          substitutionCost);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (p >= degi) {
        return regularRolesDistanceRelativeToAt(i, j, positionView, equivalenceRelativeTo,
            comparator, substitutionCost);
      }
      return pApproximateRegularRolesDistanceRelativeToAt(i, j, p, positionView,
          equivalenceRelativeTo, comparator, substitutionCost);
    });
  }

  private static <V, T extends V> int pApproximateRegularRolesDistanceRelativeToAt(int i, int j,
      int p, TransposableNetworkView<T, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj - 1);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int ritarget = positionView.tieTarget(i, j, i, ri);
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      if (substFailureCost > 0) {
        maxSubstCost += substFailureCost;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          int rjtarget = positionView.tieTarget(i, j, j, rj);
          if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo.getInt(rjtarget)) {
            if (comparator.test(ri, rj)) {
              int substCost = substitutionCost.applyAsInt(ri, rj);
              if (substCost < substFailureCost) {
                for (int q = 0; q < p; ++q) {
                  builder.addEdge(ipos, jpos + q * degj);
                  weights.add(substFailureCost - substCost);
                }
              }
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      NetworkView<T, ?> positionView, ToIntFunction<? super T> mismatchPenalty) {
    int[] sortedVertices = new int[n];
    int lastIsolate = 0;
    int firstNonisolate = n;

    for (int i = 0; i < n; ++i) {
      if (positionView.ties(i).iterator().hasNext()) {
        sortedVertices[--firstNonisolate] = i;
      } else {
        sortedVertices[lastIsolate++] = i;
      }
    }

    int[][] distance = new int[n][n];

    if (firstNonisolate == n || firstNonisolate == 0) {
      return new IntDistanceMatrixImpl(distance);
    }

    for (int iv = firstNonisolate; iv < n; ++iv) {
      int errorCost = 0;
      int v = sortedVertices[iv];
      for (T r : positionView.ties(v)) {
        errorCost += mismatchPenalty.applyAsInt(r);
      }
      for (int iw = 0; iw < lastIsolate; ++iw) {
        int w = sortedVertices[iw];
        distance[v][w] = errorCost;
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntFunction<? super T> mismatchPenalty) {
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (positionView.ties(i, j, j).iterator().hasNext()) {
        return 0;
      } else {
        return StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
            .mapToInt(mismatchPenalty).sum();
      }
    });
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      NetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {

    @SuppressWarnings("unchecked")
    T[] maximalRelationships = (T[]) new Object[n];
    for (int i = 0; i < n; ++i) {
      maximalRelationships[i] = StreamSupport.stream(positionView.ties(i).spliterator(), false)
          .max(comparator).orElse(null);
    }

    int[][] distance = new int[n][n];

    for (int i = 0; i < n; ++i) {
      @SuppressWarnings("unchecked")
      T[] sortedRelationships = (T[]) StreamSupport
          .stream(positionView.ties(i).spliterator(), false).sorted(comparator).toArray();
      int[] cost = new int[sortedRelationships.length];
      int errorCost = 0;
      T previous = null;
      int previousCost = 0;
      for (int j = sortedRelationships.length - 1; j >= 0; --j) {
        if (previous != null && comparator.compare(sortedRelationships[j], previous) < 0) {
          previousCost = errorCost;
        }
        cost[j] = previousCost;
        errorCost += mismatchPenalty.applyAsInt(sortedRelationships[j]);
        previous = sortedRelationships[j];
      }

      for (int j = 0; j < n; ++j) {
        if (maximalRelationships[j] == null) {
          distance[i][j] = errorCost;
        } else {
          int pos = Arrays.binarySearch(sortedRelationships, maximalRelationships[j], comparator);
          if (pos < 0) {
            pos = -pos - 2;
          }
          if (pos < 0) {
            distance[i][j] = errorCost;
          } else {
            distance[i][j] = cost[pos];
          }
        }
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      T maxRj = StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .max(comparator).orElse(null);

      Stream<? extends T> streamRi = StreamSupport.stream(positionView.ties(i, j, i).spliterator(),
          false);
      if (maxRj != null) {
        streamRi = streamRi.filter(ri -> comparator.compare(ri, maxRj) > 0);
      }
      return streamRi.mapToInt(mismatchPenalty).sum();
    });
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {
    return weakEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        mismatchPenalty);
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n,
        (i, j) -> weakEquivalenceDistanceAt(i, j, positionView, comparator, mismatchPenalty));
  }

  private static <T> int weakEquivalenceDistanceAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    int errorsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      boolean matched = false;
      for (T rj : positionView.ties(i, j, j)) {
        if (comparator.test(ri, rj)) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        errorsum += mismatchPenalty.applyAsInt(ri);
      }
    }
    return errorsum;
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        substitutionCost);
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        substitutionCost);
  }

  private static <T> int weakEquivalenceDistanceAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    int costsum = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int cost = substitutionCost.applyAsInt(ri, null);
      for (T rj : positionView.ties(i, j, j)) {
        if (comparator.test(ri, rj)) {
          cost = Math.min(cost, substitutionCost.applyAsInt(ri, rj));
        }
      }
      costsum += cost;
    }
    return costsum;
  }

  public static <T> IntDistanceMatrix weakEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n,
        (i, j) -> weakEquivalenceDistanceAt(i, j, positionView, comparator, substitutionCost));
  }

  public static <T> IntDistanceMatrixImpl weakExactEquivalenceDistance(int n,
      NetworkView<T, ?> positionView) {
    int[][] distance = new int[n][n];
    if (n == 0) {
      return new IntDistanceMatrixImpl(distance);
    }
    int[] deg = new int[n];
    for (int i = 0; i < n; ++i) {
      deg[i] = positionView.countTies(i);
    }
    int[] sortedVertices = PrimitiveCollections.countingSort(deg);

    int firstSameDegree = 0;
    int currentDegree = deg[sortedVertices[0]];

    for (int pos = 1; pos < n; ++pos) {
      int nextDegree = deg[sortedVertices[pos]];
      if (nextDegree != currentDegree) {
        for (int lhs = pos; lhs < n; ++lhs) {
          int i = sortedVertices[lhs];
          int ideg = deg[i];
          for (int rhs = firstSameDegree; rhs < pos; ++rhs) {
            int j = sortedVertices[rhs];
            distance[i][j] = ideg - deg[j];
          }
        }
        firstSameDegree = pos;
        currentDegree = nextDegree;
      }
    }

    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrixImpl pApproximateWeakEquivalenceDistance(int p, int n,
      NetworkView<T, ?> positionView) {
    int[][] distance = new int[n][n];
    if (n == 0) {
      return new IntDistanceMatrixImpl(distance);
    }
    int[] deg = new int[n];
    for (int i = 0; i < n; ++i) {
      deg[i] = positionView.countTies(i);
    }
    int[] sortedVertices = PrimitiveCollections.countingSort(deg);

    int firstSameDegree = 0;
    int currentDegree = deg[sortedVertices[0]];

    for (int pos = 1; pos < n; ++pos) {
      int nextDegree = deg[sortedVertices[pos]];
      if (nextDegree != currentDegree) {
        for (int lhs = pos; lhs < n; ++lhs) {
          int i = sortedVertices[lhs];
          int ideg = deg[i];
          for (int rhs = firstSameDegree; rhs < pos; ++rhs) {
            int j = sortedVertices[rhs];
            distance[i][j] = Math.max(ideg - p * deg[j], 0);
          }
        }
        firstSameDegree = pos;
        currentDegree = nextDegree;
      }
    }

    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (i == j) {
        return 0;
      }
      return Math.max(positionView.countTies(i, j, i) - positionView.countTies(i, j, j), 0);
    });
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (i == j) {
        return 0;
      }
      return Math.max(positionView.countTies(i, j, i) - p * positionView.countTies(i, j, j), 0);
    });
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      NetworkView<T, ?> positionView, Comparator<? super T> comparator) {

    @SuppressWarnings("unchecked")
    T[][] sortedRelationships = (T[][]) new Object[n][];
    Comparator<? super T> reversedComparator = comparator.reversed();
    for (int i = 0; i < n; ++i) {
      @SuppressWarnings("unchecked")
      T[] sortedRelationshipsForI = (T[]) StreamSupport
          .stream(positionView.ties(i).spliterator(), false).sorted(reversedComparator).toArray();
      sortedRelationships[i] = sortedRelationshipsForI;
    }

    int[][] distance = new int[n][n];

    for (int i = 0; i < n; ++i) {
      int nilen = sortedRelationships[i].length;
      for (int j = 0; j < n; ++j) {
        if (i != j) {
          int njlen = sortedRelationships[j].length;
          int nj = 0, ni = 0;
          int errors = 0;
          for (; ni < nilen && nj < njlen; ++ni) {
            if (comparator.compare(sortedRelationships[i][ni], sortedRelationships[j][nj]) <= 0) {
              ++nj;
            } else {
              ++errors;
            }
          }
          distance[i][j] = errors + nilen - ni;
        }
      }
    }

    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator) {

    Comparator<? super T> reversedComparator = comparator.reversed();

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (i == j) {
        return 0;
      }
      @SuppressWarnings("unchecked")
      T[] sortedRelationshipsForI = (T[]) StreamSupport
          .stream(positionView.ties(i, j, i).spliterator(), false).sorted(reversedComparator)
          .toArray();
      @SuppressWarnings("unchecked")
      T[] sortedRelationshipsForJ = (T[]) StreamSupport
          .stream(positionView.ties(i, j, j).spliterator(), false).sorted(reversedComparator)
          .toArray();
      int nilen = sortedRelationshipsForI.length;
      int njlen = sortedRelationshipsForJ.length;
      int nj = 0, ni = 0;
      int errors = 0;
      for (; ni < nilen && nj < njlen; ++ni) {
        if (comparator.compare(sortedRelationshipsForI[ni], sortedRelationshipsForJ[nj]) <= 0) {
          ++nj;
        } else {
          ++errors;
        }
      }
      return errors + nilen - ni;
    });
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator) {

    Comparator<? super T> reversedComparator = comparator.reversed();

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (i == j) {
        return 0;
      }
      @SuppressWarnings("unchecked")
      T[] sortedRelationshipsForI = (T[]) StreamSupport
          .stream(positionView.ties(i, j, i).spliterator(), false).sorted(reversedComparator)
          .toArray();
      @SuppressWarnings("unchecked")
      T[] sortedRelationshipsForJ = (T[]) StreamSupport
          .stream(positionView.ties(i, j, j).spliterator(), false).sorted(reversedComparator)
          .toArray();
      int nilen = sortedRelationshipsForI.length;
      int njlen = sortedRelationshipsForJ.length;
      int nj = 0, ni = 0, njcount = 0;
      int errors = 0;
      for (; ni < nilen && nj < njlen; ++ni) {
        if (comparator.compare(sortedRelationshipsForI[ni], sortedRelationshipsForJ[nj]) <= 0) {
          if (++njcount == p) {
            ++nj;
            njcount = 0;
          }
        } else {
          ++errors;
        }
      }
      return errors + nilen - ni;
    });
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator) {
    return weakExactEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
      builder.ensureNode(degi + degj);
      int ipos = 0;
      for (T ri : positionView.ties(i, j, i)) {
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            builder.addEdge(ipos, jpos);
          }
          ++jpos;
        }
        ++ipos;
      }
      return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
          Mappings.intRange(0, degi));
    });
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView,
        MiscUtils.lessEqualPredicate(comparator));
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (degi == 0) {
        return 0;
      }
      int degj = positionView.countTies(i, j, j);
      NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
      builder.ensureNode(degi + p * degj - 1);
      int ipos = 0;
      for (T ri : positionView.ties(i, j, i)) {
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            for (int q = 0; q < p; ++q) {
              builder.addEdge(ipos, jpos + q * degj);
            }
          }
          ++jpos;
        }
        ++ipos;
      }
      return degi - BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
          Mappings.intRange(0, degi));
    });
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      NetworkView<T, ?> positionView, ToIntFunction<? super T> mismatchPenalty) {

    int[][] distance = new int[n][n];
    if (n == 0) {
      return new IntDistanceMatrixImpl(distance);
    }
    int[] deg = new int[n];
    for (int i = 0; i < n; ++i) {
      deg[i] = positionView.countTies(i);
    }
    int[] sortedVertices = PrimitiveCollections.countingSort(deg);

    int lastSameDegree = n;
    int currentDegree = deg[sortedVertices[n - 1]];

    for (int pos = n - 2; pos >= 0; --pos) {
      int nextDegree = deg[sortedVertices[pos]];
      if (nextDegree != currentDegree) {
        for (int lhs = pos + 1; lhs < lastSameDegree; ++lhs) {
          int i = sortedVertices[lhs];
          int ideg = deg[i];
          int[] penalties = StreamSupport.stream(positionView.ties(i).spliterator(), false)
              .mapToInt(mismatchPenalty).toArray();
          Arrays.sort(penalties);
          int totalPenalty = 0;
          int lastPenaltyInTotal = 0;
          for (int rhs = pos; rhs >= 0; --rhs) {
            int j = sortedVertices[rhs];
            int degDifference = ideg - deg[j];
            for (; lastPenaltyInTotal < degDifference; ++lastPenaltyInTotal) {
              totalPenalty += penalties[lastPenaltyInTotal];
            }
            distance[i][j] = totalPenalty;
          }
        }
        lastSameDegree = pos + 1;
        currentDegree = nextDegree;
      }
    }

    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntFunction<? super T> mismatchPenalty) {

    int[][] distance = new int[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (i != j) {
          int degi = positionView.countTies(i, j, i);
          int degj = positionView.countTies(i, j, j);
          if (degi <= degj) {
            continue;
          }
          @SuppressWarnings("unchecked")
          T[] iRelationshipsSortedByPenalty = (T[]) StreamSupport
              .stream(positionView.ties(i, j, i).spliterator(), false)
              .sorted(Comparator.comparingInt(mismatchPenalty)).toArray();
          int totalPenalty = 0;
          for (int k = 0; k < degi - degj; ++k) {
            T ri = iRelationshipsSortedByPenalty[k];
            totalPenalty += mismatchPenalty.applyAsInt(ri);
          }
          distance[i][j] = totalPenalty;
        }
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      NetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {

    int[][] distance = new int[n][n];
    for (int i = 0; i < n; ++i) {
      @SuppressWarnings("unchecked")
      T[] iRelationshipsSortedByPenalty = (T[]) StreamSupport
          .stream(positionView.ties(i).spliterator(), false)
          .sorted(Comparator.comparingInt(mismatchPenalty)).toArray();
      for (int j = 0; j < n; ++j) {
        TreeSet<T> jRelationshipsSortedByComparator = new TreeSet<>(comparator);
        positionView.ties(j).forEach(r -> jRelationshipsSortedByComparator.add(r));
        int totalPenalty = 0;
        for (int k = iRelationshipsSortedByPenalty.length - 1; k >= 0; --k) {
          T ri = iRelationshipsSortedByPenalty[k];
          T rj = jRelationshipsSortedByComparator.ceiling(ri);
          if (rj == null) {
            totalPenalty += mismatchPenalty.applyAsInt(ri);
          } else {
            jRelationshipsSortedByComparator.remove(rj);
          }
          distance[i][j] = totalPenalty;
        }
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {

    int[][] distance = new int[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        @SuppressWarnings("unchecked")
        T[] iRelationshipsSortedByPenalty = (T[]) StreamSupport
            .stream(positionView.ties(i, j, i).spliterator(), false)
            .sorted(Comparator.comparingInt(mismatchPenalty)).toArray();
        TreeSet<T> jRelationshipsSortedByComparator = new TreeSet<>(comparator);
        positionView.ties(i, j, j).forEach(r -> jRelationshipsSortedByComparator.add(r));
        int totalPenalty = 0;
        for (int k = iRelationshipsSortedByPenalty.length - 1; k >= 0; --k) {
          T ri = iRelationshipsSortedByPenalty[k];
          T rj = jRelationshipsSortedByComparator.ceiling(ri);
          if (rj == null) {
            totalPenalty += mismatchPenalty.applyAsInt(ri);
          } else {
            jRelationshipsSortedByComparator.remove(rj);
          }
        }
        distance[i][j] = totalPenalty;
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {
    return weakExactEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        mismatchPenalty);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {

    return new LazyIntDistanceMatrixImpl(n,
        (i, j) -> weakExactEquivalenceDistanceAt(i, j, positionView, comparator, mismatchPenalty));
  }

  private static <T> int weakExactEquivalenceDistanceAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    int degj = positionView.countTies(j, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            builder.addEdge(ipos, jpos);
            weights.add(penalty);
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntFunction<? super T> mismatchPenalty) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView, MiscUtils.alwaysTrue(),
        mismatchPenalty);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntFunction<? super T> mismatchPenalty) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    if (p == 1) {
      return weakExactEquivalenceDistance(n, positionView, comparator, mismatchPenalty);
    } else if (p == n) {
      return weakEquivalenceDistance(n, positionView, comparator, mismatchPenalty);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (degi <= p) {
        return weakEquivalenceDistanceAt(i, j, positionView, comparator, mismatchPenalty);
      }
      return pApproximateWeakEquivalenceDistanceAt(i, j, p, positionView, comparator,
          mismatchPenalty);
    });
  }

  private static <T> int pApproximateWeakEquivalenceDistanceAt(int i, int j, int p,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    int degi = positionView.countTies(i, j, i);
    int degj = positionView.countTies(j, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj);
    int maxErrSum = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int penalty = mismatchPenalty.applyAsInt(ri);
      if (penalty > 0) {
        maxErrSum += penalty;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            for (int q = 0; q < p; ++q) {
              builder.addEdge(ipos, jpos + q * degj);
              weights.add(penalty);
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxErrSum - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakExactEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(), substitutionCost);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakExactEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        substitutionCost);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return weakExactEquivalenceDistance(n, positionView, MiscUtils.lessEqualPredicate(comparator),
        substitutionCost);
  }

  public static <T> IntDistanceMatrix weakExactEquivalenceDistance(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {

    return new LazyIntDistanceMatrixImpl(n,
        (i, j) -> weakExactEquivalenceDistanceAt(i, j, positionView, comparator, substitutionCost));
  }

  private static <T> int weakExactEquivalenceDistanceAt(int i, int j,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + degj);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      if (substFailureCost > 0) {
        maxSubstCost += substFailureCost;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            int subCost = substitutionCost.applyAsInt(ri, rj);
            if (subCost < substFailureCost) {
              builder.addEdge(ipos, jpos);
              weights.add(substFailureCost - substitutionCost.applyAsInt(ri, rj));
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView, MiscUtils.alwaysTrue(),
        substitutionCost);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, Comparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView, PartialComparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return pApproximateWeakEquivalenceDistance(p, n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <T> IntDistanceMatrix pApproximateWeakEquivalenceDistance(int p, int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    if (p == 1) {
      return weakExactEquivalenceDistance(n, positionView, comparator, substitutionCost);
    } else if (p == n) {
      return weakEquivalenceDistance(n, positionView, comparator, substitutionCost);
    }

    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      if (degi <= p) {
        return weakEquivalenceDistanceAt(i, j, positionView, comparator, substitutionCost);
      }
      return pApproximateWeakEquivalenceDistanceAt(i, j, p, positionView, comparator,
          substitutionCost);
    });

  }

  private static <T> int pApproximateWeakEquivalenceDistanceAt(int i, int j, int p,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    int degi = positionView.countTies(i, j, i);
    if (degi == 0) {
      return 0;
    }
    int degj = positionView.countTies(i, j, j);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    builder.ensureNode(degi + p * degj - 1);
    int maxSubstCost = 0;
    int ipos = 0;
    for (T ri : positionView.ties(i, j, i)) {
      int substFailureCost = substitutionCost.applyAsInt(ri, null);
      if (substFailureCost > 0) {
        maxSubstCost += substFailureCost;
        int jpos = degi;
        for (T rj : positionView.ties(i, j, j)) {
          if (comparator.test(ri, rj)) {
            int subCost = substitutionCost.applyAsInt(ri, rj);
            if (subCost < substFailureCost) {
              for (int q = 0; q < p; ++q) {
                builder.addEdge(ipos, jpos + q * degj);
                weights.add(substFailureCost - subCost);
              }
            }
          }
          ++jpos;
        }
      }
      ++ipos;
    }
    return maxSubstCost - WeightedBipartiteMatching.maximumMatchingWeight(
        builder.build().asUndirectedGraph(), weights, Mappings.intRange(0, degi));
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistanceImpl(n, positionView, comparator, mismatchPenalty);
  }

  private static <T, U> IntDistanceMatrix strongStructuralEquivalenceDistanceImpl(int n,
      NetworkView<T, U> positionView, BiPredicate<? super T, ? super U> comparator,
      ToIntFunction<? super T> mismatchPenalty) {

    int[][] distance = new int[n][n];

    NeighborhoodInclusion.iterateIncidentEdgeDominations(n, positionView::ties,
        positionView::inverseTies, positionView::tieTarget,
        new NeighborhoodInclusion.EdgeDominationVisitor<T, U>() {

          int totalPenalty, penalty;
          int[] errorImprovement = new int[n];

          @Override
          public void startNode(int i) {
            totalPenalty = 0;
          }

          @Override
          public void finishNode(int i) {
            for (int j = 0; j < n; ++j) {
              distance[i][j] = totalPenalty - errorImprovement[j];
              errorImprovement[j] = 0;
            }
          }

          @Override
          public boolean processEdge(T r) {
            penalty = mismatchPenalty.applyAsInt(r);
            totalPenalty += penalty;
            return true;
          }

          @Override
          public void matchEdge(int source, T edge, int middle, U matchedby) {
            if (comparator.test(edge, matchedby)) {
              errorImprovement[positionView.inverseTieTarget(middle, matchedby)] += penalty;
            }
          }
        });

    return new IntDistanceMatrixImpl(distance);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return strongStructuralEquivalenceDistanceTransImpl(n, positionView, comparator,
        mismatchPenalty);
  }

  private static <T> IntDistanceMatrix strongStructuralEquivalenceDistanceTransImpl(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      int errorsum = 0;
      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(rj -> positionView.tieTarget(i, j, j, rj))).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = positionView.tieTarget(i, j, j, rj);
        }
        if (ritarget != rjtarget || !comparator.test(ri, rj)) {
          errorsum += mismatchPenalty.applyAsInt(ri);
        } else {
          ++posj;
        }
      }
      for (; posi < degi; ++posi) {
        T ri = sortedTiesi[posi];
        errorsum += mismatchPenalty.applyAsInt(ri);
      }
      return errorsum;
    });
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistanceImpl(n, positionView, comparator, substitutionCost);
  }

  private static <T, U> IntDistanceMatrix strongStructuralEquivalenceDistanceImpl(int n,
      NetworkView<T, U> positionView, BiPredicate<? super T, ? super U> comparator,
      ToIntBiFunction<? super T, ? super U> substitutionCost) {

    int[][] distance = new int[n][n];

    NeighborhoodInclusion.iterateIncidentEdgeDominations(n, positionView::ties,
        positionView::inverseTies, positionView::tieTarget,
        new NeighborhoodInclusion.EdgeDominationVisitor<T, U>() {

          int totalPenalty, penalty;
          int[] errorImprovement = new int[n];

          @Override
          public void startNode(int i) {
            totalPenalty = 0;
          }

          @Override
          public void finishNode(int i) {
            for (int j = 0; j < n; ++j) {
              distance[i][j] = totalPenalty - errorImprovement[j];
              errorImprovement[j] = 0;
            }
          }

          @Override
          public boolean processEdge(T r) {
            penalty = substitutionCost.applyAsInt(r, null);
            totalPenalty += penalty;
            return true;
          }

          @Override
          public void matchEdge(int source, T edge, int middle, U matchedby) {
            if (comparator.test(edge, matchedby)) {
              errorImprovement[positionView.inverseTieTarget(middle, matchedby)] += Math.max(0,
                  penalty - substitutionCost.applyAsInt(edge, matchedby));
            }
          }
        });

    return new IntDistanceMatrixImpl(distance);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix strongStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return strongStructuralEquivalenceDistanceTransImpl(n, positionView, comparator,
        substitutionCost);
  }

  private static <T> IntDistanceMatrix strongStructuralEquivalenceDistanceTransImpl(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      int errorsum = 0;
      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(rj -> positionView.tieTarget(i, j, j, rj))).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = positionView.tieTarget(i, j, j, rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = positionView.tieTarget(i, j, j, rj);
        }
        int error = substitutionCost.applyAsInt(ri, null);
        if (ritarget == rjtarget && comparator.test(ri, rj)) {
          error = Math.min(error, substitutionCost.applyAsInt(ri, rj));
          ++posj;
        }
        errorsum += error;
      }
      for (; posi < degi; ++posi) {
        T ri = sortedTiesi[posi];
        errorsum += substitutionCost.applyAsInt(ri, null);
      }
      return errorsum;
    });
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView, Comparator<? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistanceImpl(n, positionView, comparator, mismatchPenalty);
  }

  private static <V, T extends V, U extends V> IntDistanceMatrix weakStructuralEquivalenceDistanceImpl(
      int n, NetworkView<T, U> positionView, BiPredicate<? super V, ? super V> comparator,
      ToIntFunction<? super V> mismatchPenalty) {
    int[][] distance = new int[n][n];
    @SuppressWarnings("unchecked")
    T[] loops = (T[]) new Object[n];
    PrimitiveList.OfInt loopsList = Mappings.newIntList(n);

    NeighborhoodInclusion.iterateIncidentEdgeDominations(n, positionView::ties,
        positionView::inverseTies, positionView::tieTarget,
        new NeighborhoodInclusion.EdgeDominationVisitor<T, U>() {

          int totalPenalty, penalty;
          int[] errorImprovement = new int[n];
          int currNode;
          @SuppressWarnings("unchecked")
          U[] inverseTies = (U[]) new Object[n];

          @Override
          public void startNode(int i) {
            totalPenalty = 0;
            currNode = i;
            for (U inverseTie : positionView.inverseTies(i)) {
              final int target = positionView.inverseTieTarget(i, inverseTie);
              inverseTies[target] = inverseTie;
            }
          }

          @Override
          public void finishNode(int i) {
            for (int j = 0; j < n; ++j) {
              distance[i][j] = totalPenalty - errorImprovement[j];
              errorImprovement[j] = 0;
              inverseTies[j] = null;
            }
          }

          @Override
          public boolean processEdge(T r) {
            // memorize loop and skip it for now
            final int target = positionView.tieTarget(currNode, r);
            if (target == currNode) {
              loopsList.addInt(currNode);
              loops[currNode] = r;
              return false;
            }
            penalty = mismatchPenalty.applyAsInt(r);
            totalPenalty += penalty;

            // handle reciprocation
            U inverseTie = inverseTies[target];
            if (inverseTie != null && comparator.test(r, inverseTie)) {
              errorImprovement[target] += penalty;
            }
            return true;
          }

          @Override
          public void matchEdge(int source, T edge, int middle, U matchedby) {
            int k = middle, j = positionView.inverseTieTarget(middle, matchedby);

            // skip loop
            if (k == j) {
              return;
            }

            if (comparator.test(edge, matchedby)) {
              errorImprovement[j] += penalty;
            }
          }
        });

    // handle loops
    for (int i : loopsList) {
      T ri = loops[i];
      int penalty = mismatchPenalty.applyAsInt(ri);
      for (int j = 0; j < n; ++j) {
        T rj = loops[j];
        if (rj == null || !comparator.test(ri, rj)) {
          distance[i][j] += penalty;
        }
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), mismatchPenalty);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator, ToIntFunction<? super V> mismatchPenalty) {
    return weakStructuralEquivalenceDistanceTransImpl(n, positionView, comparator, mismatchPenalty);
  }

  private static <T> IntDistanceMatrix weakStructuralEquivalenceDistanceTransImpl(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> mismatchPenalty) {
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      ToIntFunction<T> getTransposedTargetj = r -> {
        int target = positionView.tieTarget(i, j, j, r);
        if (target == i) {
          return j;
        } else if (target == j) {
          return i;
        }
        return target;
      };
      int errorsum = 0;
      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(getTransposedTargetj::applyAsInt)).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = getTransposedTargetj.applyAsInt(rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = getTransposedTargetj.applyAsInt(rj);
        }
        if (ritarget != rjtarget || !comparator.test(ri, rj)) {
          errorsum += mismatchPenalty.applyAsInt(ri);
        } else {
          ++posj;
        }
      }
      for (; posi < degi; ++posi) {
        T ri = sortedTiesi[posi];
        errorsum += mismatchPenalty.applyAsInt(ri);
      }
      return errorsum;
    });
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        substitutionCost);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView, Comparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      NetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V, T extends V, U extends V> IntDistanceMatrix weakStructuralEquivalenceDistance(
      int n, NetworkView<T, U> positionView, BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {

    int[][] distance = new int[n][n];
    @SuppressWarnings("unchecked")
    T[] loops = (T[]) new Object[n];
    PrimitiveList.OfInt loopsList = Mappings.newIntList(n);

    NeighborhoodInclusion.iterateIncidentEdgeDominations(n, positionView::ties,
        positionView::inverseTies, positionView::tieTarget,
        new NeighborhoodInclusion.EdgeDominationVisitor<T, U>() {

          int totalPenalty, penalty;
          int[] errorImprovement = new int[n];
          int currNode;
          @SuppressWarnings("unchecked")
          U[] inverseTies = (U[]) new Object[n];

          @Override
          public void startNode(int i) {
            totalPenalty = 0;
            currNode = i;
            for (U inverseTie : positionView.inverseTies(i)) {
              inverseTies[positionView.inverseTieTarget(i, inverseTie)] = inverseTie;
            }
          }

          @Override
          public void finishNode(int i) {
            for (int j = 0; j < n; ++j) {
              distance[i][j] = totalPenalty - errorImprovement[j];
              errorImprovement[j] = 0;
              inverseTies[j] = null;
            }
          }

          @Override
          public boolean processEdge(T r) {
            // memorize loop and skip it otherwise
            final int target = positionView.tieTarget(currNode, r);
            if (target == currNode) {
              loopsList.addInt(currNode);
              loops[currNode] = r;
              return false;
            }
            penalty = substitutionCost.applyAsInt(r, null);
            totalPenalty += penalty;
            // handle reciprocation
            U inverseTie = inverseTies[target];
            if (inverseTie != null && comparator.test(r, inverseTie)) {
              errorImprovement[target] += Math.max(0,
                  penalty - substitutionCost.applyAsInt(r, inverseTie));
              ;
            }
            return true;
          }

          @Override
          public void matchEdge(int source, T edge, int middle, U matchedby) {
            int k = middle, j = positionView.inverseTieTarget(middle, matchedby);

            // skip loop
            if (k == j) {
              return;
            }

            if (comparator.test(edge, matchedby)) {
              errorImprovement[j] += Math.max(0,
                  penalty - substitutionCost.applyAsInt(edge, matchedby));
              ;
            }
          }
        });

    // handle loops
    for (int i : loopsList) {
      T ri = loops[i];
      int penalty = substitutionCost.applyAsInt(ri, null);
      for (int j = 0; j < n; ++j) {
        T rj = loops[j];
        if (rj == null || !comparator.test(ri, rj)) {
          distance[i][j] += penalty;
        } else {
          distance[i][j] += Math.min(penalty, substitutionCost.applyAsInt(ri, rj));
        }
      }
    }
    return new IntDistanceMatrixImpl(distance);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView, MiscUtils.alwaysTrue(),
        substitutionCost);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator, ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistance(n, positionView,
        MiscUtils.lessEqualPredicate(comparator), substitutionCost);
  }

  public static <V> IntDistanceMatrix weakStructuralEquivalenceDistance(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BiPredicate<? super V, ? super V> comparator,
      ToIntBiFunction<? super V, ? super V> substitutionCost) {
    return weakStructuralEquivalenceDistanceTransImpl(n, positionView, comparator,
        substitutionCost);
  }

  private static <T> IntDistanceMatrix weakStructuralEquivalenceDistanceTransImpl(int n,
      TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      int degi = positionView.countTies(i, j, i);
      int degj = positionView.countTies(i, j, j);
      ToIntFunction<T> getTransposedTargetj = r -> {
        int target = positionView.tieTarget(i, j, j, r);
        if (target == j) {
          return i;
        } else if (target == i) {
          return j;
        }
        return target;
      };
      int errorsum = 0;
      @SuppressWarnings("unchecked")
      T[] sortedTiesi = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
          .sorted(Comparator.comparingInt(ri -> positionView.tieTarget(i, j, i, ri))).toArray();
      @SuppressWarnings("unchecked")
      T[] sortedTiesj = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
          .sorted(Comparator.comparingInt(getTransposedTargetj::applyAsInt)).toArray();
      int posi = 0;
      for (int posj = 0; posi < degi && posj < degj; ++posi) {
        T ri = sortedTiesi[posi];
        T rj = sortedTiesj[posj];
        int ritarget = positionView.tieTarget(i, j, i, ri);
        int rjtarget = getTransposedTargetj.applyAsInt(rj);
        while (rjtarget < ritarget && ++posj < degj) {
          rj = sortedTiesj[posj];
          rjtarget = getTransposedTargetj.applyAsInt(rj);
        }
        int error = substitutionCost.applyAsInt(ri, null);
        if (ritarget == rjtarget && comparator.test(ri, rj)) {
          error = Math.min(error, substitutionCost.applyAsInt(ri, rj));
          ++posj;
        }
        errorsum += error;
      }
      for (; posi < degi; ++posi) {
        T ri = sortedTiesi[posi];
        errorsum += substitutionCost.applyAsInt(ri, null);
      }
      return errorsum;
    });
  }

}
