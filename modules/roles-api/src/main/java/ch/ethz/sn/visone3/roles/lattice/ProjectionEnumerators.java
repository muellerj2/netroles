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
    return Arrays.equals(proj1, 0, numDimensions, proj2, 0, numDimensions);
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
   * @param projection           the projection.
   * @param numDimProjection     the number of dimensions in the projection.
   * @param numDimBinaryRelation the number of dimensions of the equivalence
   *                             (number of elements in the base set).
   * @return an iterable producing the only minimal extension.
   */
  public static Iterable<ConstMapping.OfInt> minimalExtensionEquivalences(ConstMapping.OfInt projection,
      int numPartialDimensions, int numActualDimensions) {
    int max = 0;
    int[] value = projection.intStream().limit(numPartialDimensions).toArray();
    for (int val : value) {
      max = Math.max(max, val);
    }
    value = Arrays.copyOf(value, numActualDimensions);
    for (int i = numPartialDimensions; i < numActualDimensions; ++i) {
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
}
