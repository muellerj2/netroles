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

package ch.ethz.sn.visone3.roles.structures;

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

/**
 * Interface of rankings.
 * 
 * <p>
 * This interface does not derive from BinaryRelation deliberately. While a ranking is a binary
 * relation, the lattice of rankings is not a sublattice of the lattice of binary relations.
 * Implicit conversions between binary relations and rankings might thus result in unexpected
 * behavior change to users. However, it is still possible to convert a ranking into a binary
 * relation via explicit {@link #asBinaryRelation()}.
 *
 */
public interface Ranking extends RelationBase {

  /**
   * Inverts a ranking.
   * 
   * @return the inverted ranking
   */
  Ranking invert();

  /**
   * Returns an {@link java.lang.Iterable} view over all elements that are considered less than or
   * equal to the provided element.
   * 
   * @param i
   *          the provided element
   * @return an {@link java.lang.Iterable} view over all elements that are considered less than or
   *         equal to element {@code i}.
   */
  default PrimitiveIterable.OfInt iterateLessEqualThan(int i) {
    return iterateInRelationTo(i);
  }

  /**
   * Returns an {@link java.lang.Iterable} view over all elements that are considered greater than
   * or equal to the provided element.
   * 
   * @param i
   *          the provided element
   * @return an {@link java.lang.Iterable} view over all elements that are considered greater than
   *         or equal to element {@code i}.
   */
  default PrimitiveIterable.OfInt iterateGreaterEqualThan(int i) {
    return iterateInRelationFrom(i);
  }

  /**
   * Counts the number of elements considered less than or equal to the provided element.
   * 
   * @param i
   *          the provided element
   * @return the number of elements considered less than or equal to element {@code i}.
   */
  default int countLessEqualThan(int i) {
    return countInRelationTo(i);
  }

  /**
   * Counts the number of elements considered greater than or equal to the provided element.
   * 
   * @param i
   *          the provided element
   * @return the number of elements considered greater than or equal to element {@code i}.
   */
  default int countGreaterEqualThan(int i) {
    return countInRelationFrom(i);
  }

  /**
   * Counts the number of elements considered equal to the provided element.
   * 
   * @param i
   *          the provided element
   * @return the number of elements considered equal to element {@code i}.
   */
  default int countEqual(int i) {
    return countSymmetricRelationPairs(i);
  }

  /**
   * Returns whether one element is less than or equal to another.
   * 
   * @param i
   *          the first element
   * @param j
   *          the second element
   * @return true if element {@code i} is less than or equal to element {@code j} in the ranking.
   */
  default boolean lessEqualThan(int i, int j) {
    return contains(i, j);
  }

  /**
   * Returns a representation of this ranking as a binary relation.
   * 
   * @return the binary relation representation
   */
  BinaryRelation asBinaryRelation();
}
