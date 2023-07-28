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
 * Common base interface for binary relations and rankings.
 * 
 */
public interface RelationBase {

  /**
   * Returns an {@link java.lang.Iterable} view over all elements j such that (j, i) is contained in
   * the relation for argument i.
   * 
   * @param i
   *          the provided element
   * @return an {@link java.lang.Iterable} view over all elements j such that (j, i) is contained in
   *         the relation for argument i.
   */
  public PrimitiveIterable.OfInt iterateInRelationTo(int i);

  /**
   * Returns an {@link java.lang.Iterable} view over all elements j such that (i, j) is contained in
   * the relation for argument i.
   * 
   * @param i
   *          the provided element
   * @return an {@link java.lang.Iterable} view over all elements j such that (i, j) is contained in
   *         the relation for argument i.
   */
  public PrimitiveIterable.OfInt iterateInRelationFrom(int i);

  /**
   * Count the number of relation pairs (j, i) for argument i.
   * 
   * @param i
   *          the provided element
   * @return the number of relation pairs (j, i) for argument i
   */
  public int countInRelationTo(int i);

  /**
   * Count the number of relation pairs (i, j) for argument i.
   * 
   * @param i
   *          the provided element
   * @return the number of relation pairs (i, j) for argument i
   */
  public int countInRelationFrom(int i);

  /**
   * Count the number of elements j such that both (i, j) and (j, i) are contained in the relation
   * for argument i.
   * 
   * @param i
   *          the provided element
   * @return the number of elements j such that both (i, j) and (j, i) are contained in the relation
   *         for argument i.
   */
  public int countSymmetricRelationPairs(int i);

  /**
   * Count the number of pairs in this relation.
   * 
   * @return the number of pairs.
   */
  public int countRelationPairs();

  /**
   * Returns the size of the underlying domain.
   * 
   * @return the size of the domain.
   */
  public int domainSize();

  /**
   * Checks whether the pair (i, j) is contained in the relation.
   * 
   * @param i
   *          the first element
   * @param j
   *          the second element
   * @return true if the pair (i, j) is contained in the structure for arguments i and j, false
   *         otherwise.
   */
  boolean contains(int i, int j);

  /**
   * Tests whether this relation contains the same relationships as the provided relation argument.
   * 
   * @param rhs
   *          the second relation
   * @return true if this relation contains the same relationships as the provided relation
   *         argument.
   */
  default boolean equals(RelationBase rhs) {
    return Relations.equals(this, rhs);
  }

  /**
   * Inverts the structure.
   * 
   * @return the inverted structure.
   */
  RelationBase invert();

  boolean isRandomAccess();

  boolean isLazilyEvaluated();
}
