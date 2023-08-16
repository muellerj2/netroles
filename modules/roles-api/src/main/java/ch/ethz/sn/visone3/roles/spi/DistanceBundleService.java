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

import ch.ethz.sn.visone3.roles.blocks.bundles.DistanceFactoryBundle;

/**
 * Service offering bundles of factories for distance operators on role
 * structures.
 */
public interface DistanceBundleService {

  /**
   * Returns a bundle of factories for distance operators on the specified role
   * structure type, or {@code null} if this service does not support this type of
   * role structure.
   * 
   * @param <U>           the role structure type.
   * @param structureType class object representing the role structure type.
   * @return the bundle of factories or {@code null} if this service does not
   *         support this kind of role structure.
   */
  <U> DistanceFactoryBundle<U> getBundle(Class<U> structureType);
}
