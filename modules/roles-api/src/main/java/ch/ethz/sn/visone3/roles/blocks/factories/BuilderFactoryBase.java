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

import ch.ethz.sn.visone3.roles.blocks.builders.OperatorBuilderBase;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;

/**
 * Base for factories producing builders for role operators.
 * 
 * @param <U> the role structure type.
 */
public interface BuilderFactoryBase<U> {

  /**
   * Constructs a builder for the chosen tie substitution mechanism and role
   * structure type on the specified network structure.
   * 
   * @param positionView network viewed from the position of the individual nodes.
   * @param <T>          the type of represented ties.
   * @return builder to configure and produce a concrete operator instance
   *         according to chosen kind of role/substitution mechanism and type of
   *         role structure.
   */
  <T> OperatorBuilderBase<T, U, ?, ?, ?, ?> of(NetworkView<? extends T, ? extends T> positionView);

  /**
   * Constructs a builder for the chosen tie substitution mechanism and role
   * structure type on the specified network structure.
   * 
   * @param positionView network viewed from the position of the individual nodes.
   * @param <T>          the type of represented ties.
   * @return builder to configure and produce a concrete operator instance
   *         according to chosen kind of role/substitution mechanism and type of
   *         role structure.
   * @throws UnsupportedOperationException if a transposable network position view
   *                                       is not supported.
   */
  <T> OperatorBuilderBase<T, U, ?, ?, ?, ?> of(TransposableNetworkView<? extends T, ? extends T> positionView);
}
