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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.spi.BinaryRelationUtilityService;
import ch.ethz.sn.visone3.roles.spi.RelationBuilderLoader;
import ch.ethz.sn.visone3.roles.spi.RelationBuilderService;
import ch.ethz.sn.visone3.roles.spi.RelationUtilityServiceLoader;

/**
 * Provides several commonly used operations on binary relations.
 *
 */
public class BinaryRelations {

  private BinaryRelations() {
  }

  private static final BinaryRelationUtilityService SERVICE = RelationUtilityServiceLoader
      .getBinaryRelationService();

  private static final RelationBuilderService BUILDERSERVICE = RelationBuilderLoader
      .getService();

  /**
   * Returns a representation of the universal binary relation for the specified domain size.
   * 
   * @param size
   *          the size of the binary relation's domain
   * @return the representation of the universal binary relation
   */
  public static BinaryRelation universal(int size) {
    return SERVICE.universal(size);
  }

  /**
   * Returns the representation of the identity relation for the specified domain size.
   * 
   * @param size
   *          the size of the ranking's domain
   * @return the representation of the identity relation
   */
  public static BinaryRelation identity(int size) {
    return SERVICE.identity(size);
  }

  /**
   * Derives the binary relation representation of the given equivalence.
   * 
   * @param equivalence
   *          the equivalence
   * @return the representation of the equivalence as a binary relation
   */
  public static BinaryRelation fromEquivalence(ConstMapping.OfInt equivalence) {
    return SERVICE.fromEquivalence(equivalence);
  }

  /**
   * Generates a binary relation representation from a matrix.
   * 
   * @param mat
   *          the matrix
   * @return the binary relation representation
   */
  public static BinaryRelation fromMatrix(boolean[][] mat) {
    return BUILDERSERVICE.relationFromMatrix(mat);
  }

  /**
   * Determines the lattice infimum (=set intersection) of two binary relations.
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice infimum/set intersection of two binary relations
   */
  public static BinaryRelation infimum(BinaryRelation r1, BinaryRelation r2) {
    return SERVICE.infimum(r1, r2, false);
  }

  /**
   * Determines the lattice infimum (=set intersection) of two binary relations (perhaps in a lazy
   * way).
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice infimum/set intersection of two binary relations
   */
  public static BinaryRelation lazyInfimum(BinaryRelation r1, BinaryRelation r2) {
    return SERVICE.infimum(r1, r2, true);
  }

  /**
   * Determines the lattice supremum (=set union) of two binary relations.
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice supremum/set union of two binary relations
   */
  public static BinaryRelation supremum(BinaryRelation r1, BinaryRelation r2) {
    return SERVICE.supremum(r1, r2, false);
  }

  /**
   * Determines the lattice supremum (=set union) of two binary relations (perhaps in a lazy way).
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice supremum/set union of two binary relations
   */
  public static BinaryRelation lazySupremum(BinaryRelation r1, BinaryRelation r2) {
    return SERVICE.supremum(r1, r2, true);
  }

  /**
   * Determines the transitive closure of a relation.
   * 
   * @param r
   *          the relation
   * @return the transitive closure of the argument relation
   */
  public static BinaryRelation closeTransitively(BinaryRelation r) {
    return SERVICE.closeTransitively(r);
  }

}
