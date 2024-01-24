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
package ch.ethz.sn.visone3.roles.lattice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

/**
 * Enumerators and other methods related to the projections of lattice elements
 * used by the back-track search enumeration algorithm.
 * 
 * @see BacktrackSearchEnumerator
 */
public class ProjectionEnumerators {

  private ProjectionEnumerators() {
  }

  /**
   * Generates all extensions of a projection by a single dimension for binary
   * relations.
   * 
   * @param projection    the projection.
   * @param nextDimension the ordinal of the next dimension.
   * @return an iterable over all projections when extending by another dimension.
   */
  public static Iterable<boolean[]> generateExtensionsBinaryRelations(boolean[] projection, int nextDimension) {
    return () -> new Iterator<boolean[]>() {
      private byte count = 0;

      @Override
      public boolean hasNext() {
        return count < 2;
      }

      @Override
      public boolean[] next() {
        if (count >= 2) {
          throw new NoSuchElementException();
        }
        projection[nextDimension] = count == 0;
        ++count;
        return projection;
      }
    };
  }

  /**
   * Produces the extremal full binary relation extension of a projection
   * according to the refinement ordering.
   * 
   * @param projection           the projection.
   * @param numDimProjection     the number of dimensions in the projection.
   * @param numDimBinaryRelation the number of dimensions of the binary relation
   *                             (which is the domain size squared).
   * @param maximal              true if the maximal extension is to be generated,
   *                             else the minimal one is produced.
   * @return an iterable producing the only maximal or minimal extension.
   */
  public static Iterable<BinaryRelation> extremalExtensionBinaryRelations(boolean[] projection, int numDimProjection,
      int numDimBinaryRelation, boolean maximal) {
    return () -> new Iterator<BinaryRelation>() {
      private boolean notGenerated = true;

      @Override
      public boolean hasNext() {
        return notGenerated;
      }

      @Override
      public BinaryRelation next() {
        if (!notGenerated) {
          throw new NoSuchElementException();
        }
        notGenerated = false;
        for (int i = numDimProjection; i < numDimBinaryRelation; ++i) {
          projection[i] = maximal;
        }
        return projectionToBinaryRelation(projection, numDimBinaryRelation);
      }
    };
  }

  /**
   * Returns true if the given binary relation is succeeded by some binary
   * relation that has the given projection.
   * 
   * @param relation         the binary relation.
   * @param projection       the projection.
   * @param numDimProjection the number of dimensions of the projection.
   * @return True if the binary relation is succeeded by some binary relation that
   *         projects to the given projection, false otherwise.
   */
  public static boolean someExtensionSucceedsRelation(BinaryRelation relation, boolean[] projection,
      int numDimProjection) {
    int n = relation.domainSize();
    int count = 0;
    for (int i = 0; i < n && count < numDimProjection; ++i) {
      for (int j = 0; j < n && count < numDimProjection; ++j) {
        if (relation.contains(i, j) && !projection[count]) {
          return false;
        }
        ++count;
      }
    }
    return true;
  }

  /**
   * Returns true if the given binary relation is preceded by some binary
   * relations that has the given projection.
   * 
   * @param relation         the binary relation.
   * @param projection       the projection.
   * @param numDimProjection the number of dimensions of the projection.
   * @return True if the binary relation is preceded by some binary relation that
   *         projects to the given projection, false otherwise.
   */
  public static boolean someExtensionPrecedesRelation(BinaryRelation relation, boolean[] projection,
      int numDimProjection) {
    int n = relation.domainSize();
    int count = 0;
    for (int i = 0; i < n && count < numDimProjection; ++i) {
      for (int j = 0; j < n && count < numDimProjection; ++j) {
        if (!relation.contains(i, j) && projection[count]) {
          return false;
        }
        ++count;
      }
    }
    return true;
  }

  /**
   * Converts a (full) projection representation to a binary relation.
   * 
   * @param projection    the projection.
   * @param numDimensions the number of dimensions in the projection (which must
   *                      equal the squared domain size).
   * @return the conversion of the projection representation to a binary relation.
   */
  public static BinaryRelation projectionToBinaryRelation(boolean[] projection, int numDimensions) {
    int domainSize = (int) Math.sqrt(numDimensions);
    if (domainSize * domainSize != numDimensions) {
      throw new IllegalArgumentException("numDimensions not square");
    }
    boolean[][] matrix = new boolean[domainSize][domainSize];
    int count = 0;
    for (int i = 0; i < domainSize; ++i) {
      for (int j = 0; j < domainSize; ++j) {
        matrix[i][j] = projection[count++];
      }
    }
    return BinaryRelations.fromMatrix(matrix);
  }

  /**
   * Projects a given binary relation the given number of dimensions.
   * 
   * @param relation      the binary relation.
   * @param numDimensions the number of dimensions of the projection.
   * @return the representation of the projection of the binary relation on the
   *         given number of dimensions.
   */
  public static boolean[] projectRelation(BinaryRelation relation, int numDimensions) {
    int domainSize = relation.domainSize();
    boolean[] projection = new boolean[numDimensions];
    int count = 0;
    for (int i = 0; i < domainSize && count < numDimensions; ++i) {
      for (int j = 0; j < domainSize && count < numDimensions; ++j) {
        projection[count++] = relation.contains(i, j);
      }
    }
    if (count < numDimensions) {
      throw new IllegalArgumentException("numDimensions exceeds the squared domain size");
    }
    return projection;
  }

  /**
   * Returns true if two projections of binary relations are equal.
   * 
   * @param proj1         the first projection.
   * @param proj2         the second projection.
   * @param numDimensions the number of dimensions of both projections.
   * @return true if the projections are equal, false otherwise.
   */
  public static boolean projectionEquals(boolean[] proj1, boolean[] proj2, int numDimensions) {
    for (int i = 0; i < numDimensions; ++i) {
      if (proj1[i] != proj2[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if two projections of equivalences are equal.
   * 
   * @param proj1         the first projection.
   * @param proj2         the second projection.
   * @param numDimensions the number of dimensions of both projections.
   * @return true if the projections are equal, false otherwise.
   */
  public static boolean projectionEquals(ConstMapping.OfInt proj1, ConstMapping.OfInt proj2, int numDimensions) {
    for (int i = 0; i < numDimensions; ++i) {
      if (proj1.getInt(i) != proj2.getInt(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if two projections of rankings are equal.
   * 
   * @param proj1         the first projection.
   * @param proj2         the second projection.
   * @param numDimensions the number of dimensions of both projections.
   * @return true if the projections are equal, false otherwise.
   */
  public static boolean projectionEquals(RankingProjectionState proj1, RankingProjectionState proj2,
      int numDimensions) {
    return RankingProjectionState.projectionEquals(proj1, proj2, numDimensions);
  }

  /**
   * Generates all extensions of a projection by a single dimension for
   * equivalences.
   * 
   * @param projection    the projection.
   * @param nextDimension the ordinal of the next dimension.
   * @return an iterable over all projections when extending by another dimension.
   */
  public static Iterable<ConstMapping.OfInt> generateExtensionsEquivalences(ConstMapping.OfInt projection,
      int nextDimension) {
    int max = -1;
    int[] value = projection.intStream().limit(nextDimension).toArray();
    for (int val : value) {
      max = Math.max(max, val);
    }
    int finalMax = max;
    return () -> new Iterator<ConstMapping.OfInt>() {

      int pos = -1;

      @Override
      public boolean hasNext() {
        return pos <= finalMax;
      }

      @Override
      public OfInt next() {
        if (pos > finalMax) {
          throw new NoSuchElementException();
        }
        ++pos;
        int[] result = Arrays.copyOf(value, nextDimension + 1);
        result[nextDimension] = pos;
        return Mappings.wrapUnmodifiableInt(result);
      }

    };
  }

  /**
   * Produces the minimal full equivalence extension of a projection according to
   * the refinement ordering.
   * 
   * @param projection        the projection.
   * @param numDimProjection  the number of dimensions in the projection.
   * @param numDimEquivalence the number of dimensions of the equivalence (number
   *                          of elements in the base set).
   * @return an iterable producing the only minimal extension.
   */
  public static Iterable<ConstMapping.OfInt> minimalExtensionEquivalences(ConstMapping.OfInt projection,
      int numDimProjection, int numDimEquivalence) {
    int max = 0;
    int[] value = projection.intStream().limit(numDimProjection).toArray();
    for (int val : value) {
      max = Math.max(max, val);
    }
    value = Arrays.copyOf(value, numDimEquivalence);
    for (int i = numDimProjection; i < numDimEquivalence; ++i) {
      value[i] = ++max;
    }
    return Collections.singletonList(Mappings.wrapUnmodifiableInt(value));
  }

  /**
   * Projects the equivalence to a lower number of dimensions.
   * 
   * <p>
   * This particular implementation just returns the equivalence itself as a
   * representation of its projection.
   * 
   * @param equivalence   the equivalence.
   * @param numDimensions the number of dimensions to project to.
   * @return the equivalence itself as a representation of its projection.
   */
  public static ConstMapping.OfInt projectEquivalence(ConstMapping.OfInt equivalence, int numDimensions) {
    // projectionEquals() will deal with the fact that we are actually not
    // projecting anything
    return equivalence;
  }

  /**
   * Returns true if the given equivalence is succeeded by one extension of the
   * given projection.
   * 
   * @param equivalence      the given equivalence.
   * @param projection       the projection.
   * @param numDimProjection the number of dimensions of the projection.
   * @return true if the equivalence is succeeded by one extension, false
   *         otherwise.
   */
  public static boolean someExtensionSucceedsEquivalence(ConstMapping.OfInt equivalence, ConstMapping.OfInt projection,
      int numDimProjection) {
    int numDimEq = equivalence.size();
    int[] equivalenceToProjectionClass = new int[numDimEq];

    Arrays.fill(equivalenceToProjectionClass, -1);
    for (int i = 0; i < numDimProjection; ++i) {
      int eqClass = equivalence.getInt(i);
      int projClass = projection.getInt(i);
      int eqToProjClass = equivalenceToProjectionClass[eqClass];
      if (eqToProjClass == -1) {
        equivalenceToProjectionClass[eqClass] = projClass;
      } else if (eqToProjClass != projClass) {
        return false;
      }
    }
    return true;
  }

  /**
   * Internal representation of the projection of a ranking.
   * 
   * <p>
   * Internally maintains the reflexive-transitive closure of the projection for
   * two reasons: to check efficiently whether a pair must exist or must not exist
   * due to transitivity or reflexivity, and to produce the minimum extension
   * (=closure).
   */
  public static class RankingProjectionState {
    private boolean[][] projection;
    private boolean[][] reflexiveTransitiveClosure;
    private int numDimension;

    private RankingProjectionState(int numDimension, boolean[][] currentProjection, boolean[][] currentClosure) {
      this.projection = currentProjection;
      this.reflexiveTransitiveClosure = currentClosure;
      this.numDimension = numDimension;
    }

    private RankingProjectionState(int size) {
      this.projection = new boolean[size][size];
      this.reflexiveTransitiveClosure = new boolean[size][size];
      this.numDimension = 0;
      for (int i = 0; i < size; ++i) {
        this.reflexiveTransitiveClosure[i][i] = true;
      }
    }

    private Iterable<RankingProjectionState> generateNextWidenings(int nextDimension) {
      if (nextDimension != numDimension) {
        throw new IllegalArgumentException(
            "requested number of dimensions does not match with widening by one dimension");
      }
      final int row = nextDimension / projection.length;
      final int col = nextDimension - row * projection.length;

      return () -> new Iterator<RankingProjectionState>() {
        private int count = 0;
        private boolean alreadyChecked = false;

        @Override
        public boolean hasNext() {

          if (!alreadyChecked) {
            if (count == 0 && reflexiveTransitiveClosure[row][col]) {
              ++count;
            }

            if (count == 1 && !reflexiveTransitiveClosure[row][col]) {

              boolean skip = false;
              if (col <= row) {
                for (int k = 0; k < col; ++k) {
                  if (!projection[row][k] && projection[col][k]) {
                    skip = true;
                    break;
                  }
                }
              }
              if (!skip) {
                for (int k = 0; k < row; ++k) {
                  if (!projection[k][col] && projection[k][row]) {
                    skip = true;
                    break;
                  }
                }
              }
              if (skip) {
                ++count;
              }
            }
            alreadyChecked = true;
          }
          return count < 2;
        }

        @Override
        public RankingProjectionState next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          alreadyChecked = false;
          if (count == 0) {
            projection[row][col] = false;
            ++count;
            return new RankingProjectionState(nextDimension + 1, projection, reflexiveTransitiveClosure);
          } else if (count == 1) {
            projection[row][col] = true;
            boolean[][] extendedClosure = reflexiveTransitiveClosure;
            if (!reflexiveTransitiveClosure[row][col]) {
              // update transitive closure if new ordering added
              int n = projection.length;
              extendedClosure = new boolean[n][n];
              for (int i = 0; i < n; ++i) {
                System.arraycopy(reflexiveTransitiveClosure[i], 0, extendedClosure[i], 0, n);
              }

              for (int k = 0; k < n; ++k) {
                if (extendedClosure[k][row] && !extendedClosure[k][col]) {
                  for (int l = 0; l < n; ++l) {
                    if (!extendedClosure[k][l] && extendedClosure[col][l]) {
                      extendedClosure[k][l] = true;
                    }
                  }
                }
              }
            }
            ++count;
            return new RankingProjectionState(nextDimension + 1, projection, extendedClosure);
          } else {
            // no element to return, but hasNext() returned true
            throw new IllegalStateException();
          }
        }
      };
    }

    private Iterable<Ranking> minimalExtension(int numDimProjection, int numDimRanking) {
      return Collections.singletonList(Rankings.fromMatrixUnsafe(reflexiveTransitiveClosure));
    }

    private Ranking toRanking(int numDimEquivalence) {
      if (numDimEquivalence != this.numDimension) {
        throw new IllegalStateException("projection is missing some dimensions");
      }
      return Rankings.fromMatrixUnsafe(projection);
    }

    private static RankingProjectionState toProjection(Ranking ranking, int numDimensions) {
      boolean[][] rankMat = new boolean[ranking.domainSize()][ranking.domainSize()];
      for (int i = 0; i < rankMat.length; ++i) {
        for (int j : ranking.iterateGreaterEqualThan(i)) {
          rankMat[i][j] = true;
        }
      }
      return new RankingProjectionState(numDimensions, rankMat, null);
    }

    private static boolean projectionEquals(RankingProjectionState state1, RankingProjectionState state2,
        int numDimensions) {
      if (state1.numDimension < numDimensions || state2.numDimension < numDimensions) {
        throw new IllegalArgumentException(
            "projections do not include the number of dimensions requested for comparison");
      }
      int maxRow = numDimensions / state1.projection.length;
      int numColsInMaxRow = numDimensions - maxRow * state1.projection.length;
      for (int i = 0; i < maxRow; ++i) {
        for (int j = 0; j < state1.projection.length; ++j) {
          if (state1.projection[i][j] != state2.projection[i][j]) {
            return false;
          }
        }
      }
      for (int j = 0; j < numColsInMaxRow; ++j) {
        if (state1.projection[maxRow][j] != state2.projection[maxRow][j]) {
          return false;
        }
      }
      return true;
    }

    private boolean someExtensionSucceedsRanking(Ranking ranking, int numDimProjection) {
      if (numDimProjection > this.numDimension) {
        throw new IllegalArgumentException("mismatch in number of dimensions");
      }

      int n = ranking.domainSize();
      int count = 0;
      for (int i = 0; i < n && count < numDimProjection; ++i) {
        for (int j = 0; j < n && count < numDimProjection; ++j) {
          if (ranking.contains(i, j) && !projection[i][j]) {
            return false;
          }
          ++count;
        }
      }
      return true;
    }
  }

  /**
   * Returns a zero-dimensional projection of a ranking with the specified domain
   * size.
   * 
   * @param domainSize the domain size of the ranking (which is the square root of
   *                   the number of dimensions)
   * @return the zero-dimensional projection
   */
  public static RankingProjectionState createZeroDimProjectionRanking(int domainSize) {
    return new RankingProjectionState(domainSize);
  }

  /**
   * Generates all widenings of a projection by a single dimension for rankings.
   * 
   * @param projection    the projection.
   * @param nextDimension the ordinal of the next dimension.
   * @return an iterable over all projections when widening by another dimension.
   */
  public static Iterable<RankingProjectionState> generateWideningsRankings(RankingProjectionState projection,
      int nextDimension) {
    return projection.generateNextWidenings(nextDimension);
  }

  /**
   * Produces the minimal full extension of the given projection to a ranking
   * according to the refinement ordering.
   * 
   * @param projection       the projection.
   * @param numDimProjection the number of dimensions in the projection.
   * @param numDimRanking    the number of dimensions of the ranking (number of
   *                         elements in the base set squared).
   * @return an iterable producing the only minimal extension.
   */
  public static Iterable<Ranking> minimalExtensionRankings(RankingProjectionState projection, int numDimProjection,
      int numDimRanking) {
    return projection.minimalExtension(numDimProjection, numDimRanking);
  }

  /**
   * Converts a (full) projection representation to a ranking.
   * 
   * @param projection    the projection.
   * @param numDimensions the number of dimensions in the projection (which must
   *                      equal the squared domain size).
   * @return the conversion of the projection representation to a ranking
   */
  public static Ranking projectionToRanking(RankingProjectionState projection, int numDimensions) {
    return projection.toRanking(numDimensions);
  }

  /**
   * Projects a given ranking to the given number of dimensions.
   * 
   * @param ranking       the ranking.
   * @param numDimensions the number of dimensions of the projection.
   * @return the representation of the projection of the ranking on the given
   *         number of dimensions.
   */
  public static RankingProjectionState projectRanking(Ranking ranking, int numDimensions) {
    return RankingProjectionState.toProjection(ranking, numDimensions);
  }

  /**
   * Returns true if the given ranking is succeeded by some ranking that has the
   * given projection.
   * 
   * @param ranking          the ranking.
   * @param projection       the projection.
   * @param numDimProjection the number of dimensions of the projection.
   * @return True if the ranking is succeeded by some ranking that projects to the
   *         given projection, false otherwise.
   */
  public static boolean someExtensionSucceedsRanking(Ranking ranking, RankingProjectionState projection,
      int numDimProjection) {
    return projection.someExtensionSucceedsRanking(ranking, numDimProjection);
  }

}
