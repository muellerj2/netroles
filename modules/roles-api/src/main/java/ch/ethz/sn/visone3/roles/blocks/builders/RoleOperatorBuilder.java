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

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;

/**
 * This builder constructs a role operator producing role structures based on network position, an
 * input role structure, and the chosen substitution mechanism between ties.
 * 
 * @param <T>
 *          type for ties
 * @param <U>
 *          type for role structure
 */
public interface RoleOperatorBuilder<T, U>
    extends OperatorBuilder<T, RoleOperator<U>, RoleOperatorBuilder<T, U>> {

}
