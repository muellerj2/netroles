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
package ch.ethz.sn.visone3.roles.blocks.factories;

/**
 * Factory to produce builders for generic, heavily user-customizable role
 * operators based on the specified views on network positions and a chosen
 * setting of ``loose'', ``equitable'' or intermediate degree of ``strictness``
 * for the substitution of ties in pairwise comparisons.
 * 
 * <p>
 * This factory produces builders that allow to define the traits of the
 * produced operators and set comparators among ties that can depend on the
 * input role structure. The comparator is used to restrict and thus refine the
 * substitution between ties. This dependence on the input role structure
 * greatly extends the possibilities for users to customize the role operator
 * compared to other provided operators that always use the same fixed
 * comparator for all input role structures.
 * 
 * @param <U> role structure type.
 */
public interface VariableGenericRoleOperatorBuilderFactory<U>
    extends GenericRoleOperatorBuilderFactory<U>,
    VariablePFactoryBase<VariableGenericRoleOperatorBuilderFactory<U>> {

}
