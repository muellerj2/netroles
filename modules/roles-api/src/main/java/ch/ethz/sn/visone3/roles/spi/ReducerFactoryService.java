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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.roles.blocks.Reducers;

/**
 * Service offering factories for reducers.
 */
public interface ReducerFactoryService {

  /**
   * Returns a factory for the specified role structure type, or {@code null} if
   * this structure type is not supported by this service.
   * 
   * @param <T>           the role structure type.
   * @param structureType class object representing the role structure type.
   * @return a factory for the specified role structure type, or {@code null} if
   *         this structure type is not supported.
   */
  <T> Reducers.Factory<T> getFactory(Class<T> structureType);
}
