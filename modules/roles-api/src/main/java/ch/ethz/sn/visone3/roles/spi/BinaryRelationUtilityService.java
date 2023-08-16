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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;

/**
 * Provides common operations on and factory methods for special kinds of binary
 * relations.
 */
public interface BinaryRelationUtilityService {

  /**
   * Returns the universal binary relation (all pairs contained) with a domain of
   * the specified size.
   * 
   * @param size the domain size (number of elements in the base set).
   * @return the universal binary relation.
   */
  BinaryRelation universal(int size);

  /**
   * Returns the identity binary relation (only reflexive pairs contained) with a
   * domain of the specified size.
   * 
   * @param size the domain size (number of elements in the base set).
   * @return the identity binary relation.
   */
  BinaryRelation identity(int size);

  /**
   * Returns the binary relation representation of the specified equivalence.
   * 
   * @param equivalence the equivalence.
   * @return the representation as a binary relation.
   */
  BinaryRelation fromEquivalence(ConstMapping.OfInt equivalence);

  /**
   * Computes the greatest common refinement of two binary relations.
   * 
   * @param r1   the first binary relation.
   * @param r2   the second binary relation.
   * @param lazy true if the computation should/can happen lazily, otherwise
   *             false.
   * @return the greatest common refinement of two binary relations.
   */
  BinaryRelation infimum(BinaryRelation r1, BinaryRelation r2, boolean lazy);

  /**
   * Computes the least common coarsening of two binary relations.
   * 
   * @param r1   the first binary relation.
   * @param r2   the second binary relation.
   * @param lazy true if the computation should/can happen lazily, otherwise
   *             false.
   * @return the least common coarsening of two binary relations.
   */
  BinaryRelation supremum(BinaryRelation r1, BinaryRelation r2, boolean lazy);

  /**
   * Computes the transitive closure of a binary relation.
   * 
   * @param relation the binary relation.
   * @return the transitive closure of the specified binary relation.
   */
  BinaryRelation closeTransitively(BinaryRelation relation);
}
