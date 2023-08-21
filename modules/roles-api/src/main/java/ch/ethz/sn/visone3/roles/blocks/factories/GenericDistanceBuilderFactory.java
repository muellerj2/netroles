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

import ch.ethz.sn.visone3.roles.blocks.builders.GenericDistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;

/**
 * Factory to construct builders for generic, heavily user-customizable
 * operators producing pairwise distances of nodes. The distance operators
 * produce the distance matrix based on the specified views on network
 * positions.
 * 
 * <p>
 * This factory produces builders that allow to define the traits of the
 * produced operators and set comparators and cost functions that can depend on
 * the input role structure. The comparator is used to restrict and thus refine
 * the substitution between ties, and the cost functions assign costs for the
 * substitution of two ties or failure to substitute a tie. This dependence on
 * the input role structure greatly extends the possibilities for users to
 * customize the distance operator compared to other provided operators that
 * always use the same fixed comparator and cost function for all input role
 * structures.
 * 
 * @param <U> role structure type.
 */
public interface GenericDistanceBuilderFactory<U>
    extends DistanceBuilderFactoryBase<U>,
    VariablePFactoryBase<GenericDistanceBuilderFactory<U>> {

  @Override
  <T> GenericDistanceOperatorBuilder<T, U> of(NetworkView<? extends T, ? extends T> positionView);

  @Override
  <T> GenericDistanceOperatorBuilder<T, U> of(TransposableNetworkView<? extends T, ? extends T> positionView);

}
