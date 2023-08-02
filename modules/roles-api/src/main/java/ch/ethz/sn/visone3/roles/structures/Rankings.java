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

package ch.ethz.sn.visone3.roles.structures;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.spi.RankingUtilityService;
import ch.ethz.sn.visone3.roles.spi.RelationBuilderLoader;
import ch.ethz.sn.visone3.roles.spi.RelationBuilderService;
import ch.ethz.sn.visone3.roles.spi.RelationUtilityServiceLoader;

/**
 * Provides several commonly used operations on rankings.
 *
 */
public class Rankings {

  private Rankings() {
  }

  private static final RankingUtilityService SERVICE = RelationUtilityServiceLoader
      .getRankingService();

  private static final RelationBuilderService BUILDERSERVICE = RelationBuilderLoader
      .getService();

  /**
   * Returns a ranking representation of the universal relation for the specified domain size.
   * 
   * @param size
   *          the size of the ranking's domain
   * @return the ranking representation of the universal relation
   */
  public static Ranking universal(int size) {
    return SERVICE.universal(size);
  }

  /**
   * Returns the ranking representation of the identity relation for the specified domain size.
   * 
   * @param size
   *          the size of the ranking's domain
   * @return the ranking representation of the identity relation
   */
  public static Ranking identity(int size) {
    return SERVICE.identity(size);
  }

  /**
   * Derives the ranking representation of the given equivalence.
   * 
   * @param equivalence
   *          the equivalence
   * @return the representation of the equivalence as a ranking
   */
  public static Ranking fromEquivalence(ConstMapping.OfInt equivalence) {
    return SERVICE.fromEquivalence(equivalence);
  }

  /**
   * Generates a ranking representation from a matrix.
   * 
   * @param mat
   *          the matrix
   * @return the ranking representation
   */
  public static Ranking fromMatrixUnsafe(boolean[][] mat) {
    return BUILDERSERVICE.rankingFromMatrixUnsafe(mat);
  }

  /**
   * Determines the lattice infimum (=set intersection) of two rankings.
   * 
   * @param r1
   *          the first ranking
   * @param r2
   *          the second ranking
   * @return the lattice infimum/set intersection of two rankings
   */
  public static Ranking infimum(Ranking r1, Ranking r2) {
    return SERVICE.infimum(r1, r2, false);
  }

  /**
   * Determines the lattice infimum (=set intersection) of two rankings (perhaps lazily).
   * 
   * @param r1
   *          the first ranking
   * @param r2
   *          the second ranking
   * @return the lattice infimum/set intersection of two rankings
   */
  public static Ranking lazyInfimum(Ranking r1, Ranking r2) {
    return SERVICE.infimum(r1, r2, true);
  }

  /**
   * Determines the lattice supremum (=set union followed by transitive closure) of two rankings.
   * 
   * @param r1
   *          the first ranking
   * @param r2
   *          the second ranking
   * @return the lattice supremum of two rankings
   */
  public static Ranking supremum(Ranking r1, Ranking r2) {
    return SERVICE.supremum(r1, r2, false);
  }

  /**
   * Determines the finest ranking that coarsens a binary relation.
   * 
   * @param r
   *          the relation
   * @return the smallest ranking coarsening the given binary relation
   */
  public static Ranking finestCoarseningRanking(BinaryRelation r) {
    return SERVICE.closeTransitively(r);
  }
}
