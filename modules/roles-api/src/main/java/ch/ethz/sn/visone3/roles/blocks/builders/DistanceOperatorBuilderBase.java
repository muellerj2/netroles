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

package ch.ethz.sn.visone3.roles.blocks.builders;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;

public interface DistanceOperatorBuilderBase<T, U, //
    V extends DistanceOperatorBuilderBase<T, U, V, W, X, Y, Z, S>, W, X, Y, Z, S>
    extends OperatorBuilderBase<T, Operator<U, IntDistanceMatrix>, V, W, X, Y> {

  /**
   * Sets substitution costs between ties.
   * 
   * <p>
   * Note: Substitution cost of a tie by {@code null} is supposed to be the cost of failing to
   * substitute a tie.
   * 
   * @param substitutionCost
   *          the inherent cost in substituting the former tie by the latter
   * @return this builder (for chaining)
   */
  V substCost(Z substitutionCost);

  /**
   * Sets costs for failing to substitute a tie.
   * 
   * @param substitutionCost
   *          the cost in failing to substitute the specified tie
   * @return this builder (for chaining)
   */
  V failCost(S substitutionCost);
}
