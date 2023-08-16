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

import ch.ethz.sn.visone3.roles.blocks.Reducer;
import ch.ethz.sn.visone3.roles.blocks.Reducers;

/**
 * Loader for services providing factories for {@link Reducer} objects.
 */
public class ReducerFactoryLoader {

  private ReducerFactoryLoader() {
    loader = ServiceLoader.load(ReducerFactoryService.class);
  }

  private static final ReducerFactoryLoader INSTANCE = new ReducerFactoryLoader();
  private ServiceLoader<ReducerFactoryService> loader;

  /**
   * Gets the singleton instance of this loader.
   * 
   * @return the singleton instance.
   */
  public static ReducerFactoryLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Returns a factory compatible with the specified role structure type, if any
   * registered service provides such a factory.
   * 
   * @param <T>           the role structure type.
   * @param structureType class object representing the role structure type.
   * @return a factory compatible with the specified role structure type.
   * @throws UnsupportedOperationException if no registered service provides a
   *                                       factory for this role structure type.
   */
  public <T> Reducers.Factory<T> getFactory(Class<T> structureType) {
    for (ReducerFactoryService service : loader) {
      Reducers.Factory<T> result = service.getFactory(structureType);
      if (result != null) {
        return result;
      }
    }
    throw new UnsupportedOperationException(
        "No factory for type " + structureType + " provided by any service");
  }

  /**
   * Returns the factory for reducers on distances provided by the registered
   * service.
   * 
   * @return the factory for reducers on distances.
   * @throws NoSuchElementException if no service providing a factory for distance
   *                                reducers is registered.
   */
  public Reducers.DistanceFactory getDistanceFactory() {
    return ServiceLoader.load(ReducerDistanceFactoryService.class).iterator().next().getFactory();
  }
}
