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

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.builders.DistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;

/**
 * Factory to produce builders for distance operators.
 * 
 * @param <U> the input role structure type.
 */
public interface DistanceBuilderFactory<U> extends DistanceBuilderFactoryBase<U>,
    BuilderFactory<Operator<U, IntDistanceMatrix>> {

  @Override
  <T> DistanceOperatorBuilder<T, U> of(NetworkView<? extends T, ? extends T> positionView);

  @Override
  <T> DistanceOperatorBuilder<T, U> of(TransposableNetworkView<? extends T, ? extends T> positionView);
}
