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

package ch.ethz.sn.visone3.roles.impl.structures;

import java.util.Arrays;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.Relations;

/**
 * Provides implementations for common operations on binary relations and
 * rankings as well as representations of some special kinds of binary relations
 * or rankings.
 */
public class CommonBinRelationRankingUtils {

  private CommonBinRelationRankingUtils() {
  }

  /**
   * Returns a representation of the universal binary relation for the specified domain size.
   * 
   * @param domainSize
   *          the size of the binary relation's domain
   * @return the representation of the universal binary relation
   */
  public static BinaryRelationOrRanking universal(int domainSize) {
    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public OfInt iterateInRelationTo(int i) {
        return Mappings.intRange(0, domainSize);
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return iterateInRelationTo(i);
      }

      @Override
      public int domainSize() {
        return domainSize;
      }

      @Override
      public int countInRelationTo(int i) {
        return domainSize;
      }

      @Override
      public int countInRelationFrom(int i) {
        return domainSize;
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return domainSize;
      }

      @Override
      public int countRelationPairs() {
        return domainSize * domainSize;
      }

      @Override
      public boolean contains(int i, int j) {
        return true;
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof BinaryRelation) {
          return equals((BinaryRelation) rhs);
        }
        return false;
      }

      @Override
      public boolean equals(RelationBase rhs) {
        if (rhs == null) {
          return false;
        }
        int n = domainSize();
        if (n != rhs.domainSize()) {
          return false;
        }
        for (int i = 0; i < n; ++i) {
          if (rhs.countSymmetricRelationPairs(i) != n) {
            return false;
          }
        }
        return true;
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
      public String toString() {
        return Relations.toString(this);
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }
    };
  }

  /**
   * Returns the representation of the identity relation for the specified domain size.
   * 
   * @param domainSize
   *          the size of the ranking's domain
   * @return the representation of the identity relation
   */
  public static BinaryRelationOrRanking identity(int domainSize) {
    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public OfInt iterateInRelationTo(int i) {
        return Iterators.singletonInt(i);
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return iterateInRelationTo(i);
      }

      @Override
      public int domainSize() {
        return domainSize;
      }

      @Override
      public int countInRelationTo(int i) {
        return 1;
      }

      @Override
      public int countInRelationFrom(int i) {
        return 1;
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return 1;
      }

      @Override
      public int countRelationPairs() {
        return domainSize;
      }

      @Override
      public boolean contains(int i, int j) {
        return i == j;
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof BinaryRelation) {
          return equals((BinaryRelation) rhs);
        }
        return false;
      }

      @Override
      public boolean equals(RelationBase rhs) {
        if (rhs == null) {
          return false;
        }
        int n = domainSize();
        if (n != rhs.domainSize()) {
          return false;
        }
        for (int i = 0; i < n; ++i) {
          if (rhs.countInRelationTo(i) != 1 || !rhs.contains(i, i)) {
            return false;
          }
        }
        return true;
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
      public String toString() {
        return Relations.toString(this);
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }
    };
  }

  /**
   * Derives the binary relation representation of the given equivalence.
   * 
   * @param equivalence
   *          the equivalence
   * @return the representation of the equivalence as a binary relation
   */
  public static BinaryRelationOrRanking fromEquivalence(int[] equivalence) {
    int[] equivalenceCopy = Arrays.copyOf(equivalence, equivalence.length);
    final int[] counts = new int[equivalence.length];
    // count
    for (int i = 0; i < equivalence.length; i++) {
      ++counts[equivalence[i]];
    }
    int relCount = counts[0] * counts[0];
    // accumulate
    for (int i = 1; i < equivalence.length; i++) {
      relCount += counts[i] * counts[i];
      counts[i] += counts[i - 1];
    }
    int relationshipCount = relCount;

    int[] sortedVertices = PrimitiveCollections.countingSort(equivalenceCopy, equivalence.length);

    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public OfInt iterateInRelationTo(int i) {
        int eqClass = equivalenceCopy[i];
        return () -> Arrays
            .stream(sortedVertices, eqClass == 0 ? 0 : counts[eqClass - 1], counts[eqClass])
            .iterator();
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return iterateInRelationTo(i);
      }

      @Override
      public int domainSize() {
        return equivalenceCopy.length;
      }

      @Override
      public int countInRelationTo(int i) {
        int eqClass = equivalenceCopy[i];
        return counts[eqClass] - (eqClass == 0 ? 0 : counts[eqClass - 1]);
      }

      @Override
      public int countInRelationFrom(int i) {
        return countInRelationTo(i);
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return countInRelationTo(i);
      }

      @Override
      public int countRelationPairs() {
        return relationshipCount;
      }

      @Override
      public boolean contains(int i, int j) {
        return equivalenceCopy[i] == equivalenceCopy[j];
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
      public String toString() {
        return Relations.toString(this);
      }

      @Override
      public boolean isRandomAccess() {
        return true;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return false;
      }
    };

  }

  /**
   * Derives the binary relation representation of the given equivalence.
   * 
   * @param equivalence
   *          the equivalence
   * @return the representation of the equivalence as a binary relation
   */
  public static BinaryRelationOrRanking fromEquivalence(ConstMapping.OfInt equivalence) {
    int[] equivalenceCopy = equivalence.toUnboxedArray();
    final int[] counts = new int[equivalenceCopy.length];
    // count
    for (int i = 0; i < equivalenceCopy.length; i++) {
      ++counts[equivalenceCopy[i]];
    }
    int relCount = counts[0] * counts[0];
    // accumulate
    for (int i = 1; i < equivalenceCopy.length; i++) {
      relCount += counts[i] * counts[i];
      counts[i] += counts[i - 1];
    }
    int relationshipCount = relCount;

    int[] sortedVertices = PrimitiveCollections.countingSort(equivalenceCopy,
        equivalenceCopy.length);

    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;

      @Override
      public OfInt iterateInRelationTo(int i) {
        int eqClass = equivalenceCopy[i];
        return () -> Arrays
            .stream(sortedVertices, eqClass == 0 ? 0 : counts[eqClass - 1], counts[eqClass])
            .iterator();
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return iterateInRelationTo(i);
      }

      @Override
      public int domainSize() {
        return equivalenceCopy.length;
      }

      @Override
      public int countInRelationTo(int i) {
        int eqClass = equivalenceCopy[i];
        return counts[eqClass] - (eqClass == 0 ? 0 : counts[eqClass - 1]);
      }

      @Override
      public int countInRelationFrom(int i) {
        return countInRelationTo(i);
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return countInRelationTo(i);
      }

      @Override
      public int countRelationPairs() {
        return relationshipCount;
      }

      @Override
      public boolean contains(int i, int j) {
        return equivalenceCopy[i] == equivalenceCopy[j];
      }

      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof BinaryRelation) {
          return equals((BinaryRelation) rhs);
        }
        return false;
      }

      @Override
      public String toString() {
        return Relations.toString(this);
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
    };

  }

  /**
   * Creates a relation representation from a matrix.
   * 
   * @param mat
   *          the matrix
   * @return the binary relation representation
   */
  public static BinaryRelationOrRanking fromMatrix(boolean[][] mat) {
    return new BinaryRelationMatrixImpl(mat);
  }

  /**
   * Intersects two relations.
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the intersection of the two arguments
   */
  public static BinaryRelationOrRanking intersect(RelationBase r1, RelationBase r2) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(r1.domainSize());

    if (!r1.isRandomAccess() && !r2.isRandomAccess()) {
      boolean[] value = new boolean[r1.domainSize()];
      int[] visited = new int[r1.domainSize()];
      int maxvisited = 0;
      for (int i = 0; i < r1.domainSize(); ++i) {
        for (int j : r1.iterateInRelationFrom(i)) {
          visited[maxvisited++] = j;
          value[j] = true;
        }
        for (int j : r2.iterateInRelationFrom(i)) {
          if (value[j]) {
            builder.add(i, j);
          }
        }
        for (int j = 0; j < maxvisited; ++j) {
          value[visited[j]] = false;
        }
        maxvisited = 0;
      }
    } else {
      if (!r2.isRandomAccess() || (r1.isLazilyEvaluated() && !r2.isLazilyEvaluated())) {
        RelationBase tmp = r1;
        r1 = r2;
        r2 = tmp;
      }

      for (int i = 0; i < r1.domainSize(); ++i) {
        for (int j : r1.iterateInRelationFrom(i)) {
          if (r2.contains(i, j)) {
            builder.add(i, j);
          }
        }
      }
    }
    return builder.build();
  }

  /**
   * Intersects two relations (perhaps lazily).
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the intersection of the two arguments
   */
  public static BinaryRelationOrRanking intersectLazily(final RelationBase r1,
      final RelationBase r2) {
    if (r1.isRandomAccess() && r2.isRandomAccess()) {
      return new LazyUncachedBinaryRelationMatrixImpl(r1.domainSize(),
          (i, j) -> r1.contains(i, j) && r2.contains(i, j));
    }
    return intersect(r1, r2);
  }

  /**
   * Closes transitively on a binary relation.
   * 
   * @param r                 the binary relation
   * @param addReflexivePairs true if the reflexive pairs should be added (i.e.,
   *                          the reflexive transitive closure should be computed)
   * @return the transitive or reflexive transitive closure of the specified
   *         binary relation
   */
  public static BinaryRelationOrRanking closeTransitively(BinaryRelation r,
      boolean addReflexivePairs) {
    int n = r.domainSize();
    boolean[][] closure = new boolean[n][n];
    if (addReflexivePairs) {
      for (int i = 0; i < n; ++i) {
        closure[i][i] = true;
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j : r.iterateInRelationFrom(i)) {
        if (!closure[i][j]) {
          if (i != j) {
            for (int k = 0; k < n; ++k) {
              if ((closure[k][i] || k == i) && !closure[k][j]) {
                closure[k][j] = true;
                for (int l = 0; l < n; ++l) {
                  if (!closure[k][l] && closure[j][l]) {
                    closure[k][l] = true;
                  }
                }
              }
            }
          } else {
            closure[i][j] = true;
          }
        }
      }
    }

    return fromMatrix(closure);
  }

}
