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

package ch.ethz.sn.visone3.roles.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;

/**
 * Factory for producing some basic operators on the type of role structure.
 *
 * @param <T>
 *          role structure type
 */
public interface BasicRoleOperatorFactory<T> {

  /**
   * Constructs a role operator that does nothing but forward its argument without modification.
   * 
   * @return the forwarding role operator
   */
  RoleOperator<T> forward();

  /**
   * Constructs a role operator that always produces a constant.
   * 
   * @param constant
   *          the constant
   * @return a role operator always producing a constant
   */
  RoleOperator<T> produceConstant(T constant);

  /**
   * Constructs a role operator that produces the meet of its argument with a constant.
   * 
   * @param constant
   *          the constant
   * @return a role operator always producing the meet with the supplied constant
   */
  RoleOperator<T> meetWithConstant(T constant);

  /**
   * Constructs a role operator that produces the join of its argument with a constant.
   * 
   * @param constant
   *          the constant
   * @return a role operator always producing the join with the supplied constant
   */
  RoleOperator<T> joinWithConstant(T constant);
}
