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

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Loader for the distance matrix service.
 */
public class DistanceMatrixBuilderLoader {

  /**
   * Returns the registered distance matrix service.
   * 
   * @return the registered distance matrix service.
   * @throws NoSuchElementException if no distance matrix service is registered.
   */
  public static DistanceMatrixBuilderService getService() {
    return ServiceLoader.load(DistanceMatrixBuilderService.class).iterator().next();
  }
}
