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
 * along with visone3.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.roles.blocks.Reducers;

import java.util.ServiceLoader;

public class ReducerFactoryLoader {

  private ReducerFactoryLoader() {
    loader = ServiceLoader.load(ReducerFactoryService.class);
  }

  private static final ReducerFactoryLoader INSTANCE = new ReducerFactoryLoader();
  private ServiceLoader<ReducerFactoryService> loader;

  public static ReducerFactoryLoader getInstance() {
    return INSTANCE;
  }

  public <T> Reducers.Factory<T> getFactory(Class<T> structureType) {
    for (ReducerFactoryService service : loader) {
      Reducers.Factory<T> result = service.getFactory(structureType);
      if (result != null) {
        return result;
      }
    }
    throw new IllegalStateException(
        "No factory for type " + structureType + " provided by any service");
  }

  public Reducers.DistanceFactory getDistanceFactory() {
    return ServiceLoader.load(ReducerDistanceFactoryService.class).iterator().next().getFactory();
  }
}
