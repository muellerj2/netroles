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
import ch.ethz.sn.visone3.roles.blocks.factories.BuilderFactory;

public interface FactoryBundle<T, U, V extends Operator<T, U>, //
    X extends BuilderFactory<V>, Y extends BuilderFactory<V>, Z> {
  /**
   * Returns a factory to construct role operators based on the substitution mechanism of regular
   * roles.
   * 
   * @return the factory
   */
  X regular();

  /**
   * Returns a factory to construct role operators based on the substitution mechanism of equitable
   * regular roles.
   * 
   * @return the factory
   */
  X equitable();

  /**
   * Returns a factory to construct role operators based on the substitution mechanism of weak
   * roles.
   * 
   * @return the factory
   */
  X weak();

  /**
   * Returns a factory to construct role operators based on the substitution mechanism of weakly
   * equitable roles.
   * 
   * @return the factory
   */
  X weaklyEquitable();

  /**
   * Returns a factory to construct role operators based on the substitution mechanism of strong
   * structural roles.
   * 
   * @return the factory
   */
  Y strongStructural();

  /**
   * Returns a factory to construct role operators based on the substitution mechanism of weak
   * structural roles.
   * 
   * @return the factory
   */
  Z weakStructural();
}
