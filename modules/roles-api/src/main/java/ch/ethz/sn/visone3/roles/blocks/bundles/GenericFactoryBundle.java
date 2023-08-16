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

package ch.ethz.sn.visone3.roles.blocks.bundles;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericBuilderFactory;

/**
 * Provides a factory for producing generic, heavily user-customizable operators
 * where user-specified comparator and/or cost function can potentially depend
 * on the input role structure.
 * 
 * @param <T> role structure type.
 * @param <U> output type of the operator.
 * @param <V> operator type.
 * @param <X> factory type for builders of generic, heavily user-customizable
 *            operators.
 */
public interface GenericFactoryBundle<T, U, V extends Operator<T, U>, //
    X extends GenericBuilderFactory<T, V>> {

  /**
   * Returns a factory for constructing generic, heavily user-customizable
   * operators.
   * 
   * <p>
   * Using this factory, the user-specified comparator and/or cost functions can
   * depend on the input role structure. This greatly extends the range of
   * definable operators compared to the other provided factories where
   * comparators or cost functions must be chosen independently of the input role
   * structure.
   * 
   * @return the factory to construct generic operators.
   */
  X generic();
}
