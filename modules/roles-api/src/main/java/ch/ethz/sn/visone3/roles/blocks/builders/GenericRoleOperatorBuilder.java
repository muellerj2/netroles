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
 * This builder constructs a role operator based on network structure and a
 * user-specified substitution mechanism which can depend on the input role
 * structure.
 * 
 * <p>
 * This builder allows to specify the traits of the resulting operators, and
 * configure a comparator that can depend on the input role structure. The
 * comparator restricts and thus refines the substitution between ties. The
 * dependence of the comparator and the cost functions on the input role
 * structure greatly extends the possibilities for user customization compared
 * to the other provided builders for operators.
 * 
 * @param <T> type for ties
 * @param <U> type for role structure
 */
public interface GenericRoleOperatorBuilder<T, U>
    extends GenericOperatorBuilder<T, U, RoleOperator<U>, GenericRoleOperatorBuilder<T, U>> {

}
