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
import ch.ethz.sn.visone3.roles.structures.Ranking;

/**
 * Provides common operations on and factory methods for special kinds of
 * rankings.
 */
public interface RankingUtilityService {

  /**
   * Returns the universal ranking (all elements equivalent) with a domain of the
   * specified size.
   * 
   * @param size the domain size (number of elements in the base set).
   * @return the universal ranking.
   */
  Ranking universal(int size);

  /**
   * Returns the identity ranking (all distinct pairwise elements incomparable)
   * with a domain of the specified size.
   * 
   * @param size the domain size (number of elements in the base set).
   * @return the identity ranking.
   */
  Ranking identity(int size);

  /**
   * Returns the ranking representation of the specified equivalence.
   * 
   * @param equivalence the equivalence.
   * @return the representation as a ranking.
   */
  Ranking fromEquivalence(ConstMapping.OfInt equivalence);

  /**
   * Computes the greatest common refinement of two rankings.
   * 
   * @param r1   the first ranking.
   * @param r2   the second ranking.
   * @param lazy true if the computation should/can happen lazily, otherwise
   *             false.
   * @return the greatest common refinement of two rankings.
   */
  Ranking infimum(Ranking r1, Ranking r2, boolean lazy);

  /**
   * Computes the least common coarsening of two rankings.
   * 
   * @param r1   the first ranking.
   * @param r2   the second ranking.
   * @param lazy true if the computation should/can happen lazily, otherwise
   *             false.
   * @return the least common coarsening of two rankings.
   */
  Ranking supremum(Ranking r1, Ranking r2, boolean lazy);

  /**
   * Computes the reflexive transitive closure of a binary relation, i.e., the
   * finest ranking coarsening the binary relation.
   * 
   * @param relation the binary relation.
   * @return the reflexive transitive closure of the specified binary relation.
   */
  Ranking closeTransitively(BinaryRelation relation);
}
