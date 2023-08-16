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

import java.util.ServiceLoader;

/**
 * Provides access to the service for producing general composition operators.
 */
public class CompositionLoader {

  private CompositionLoader() {
    loader = ServiceLoader.load(CompositionService.class);
  }

  private static final CompositionLoader INSTANCE = new CompositionLoader();
  private ServiceLoader<CompositionService> loader;

  /**
   * Get the singleton instance.
   * 
   * @return the instance.
   */
  public static CompositionLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Get the loaded service.
   * 
   * @return the service.
   */
  public CompositionService getService() {
    return loader.iterator().next();
  }

}
