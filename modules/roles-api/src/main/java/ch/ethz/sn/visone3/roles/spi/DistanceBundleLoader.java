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

import ch.ethz.sn.visone3.roles.blocks.bundles.DistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.bundles.GenericDistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicDistanceOperatorFactory;

import java.util.ServiceLoader;

public class DistanceBundleLoader {

  private DistanceBundleLoader() {
    loader = ServiceLoader.load(DistanceBundleService.class);
  }

  private static final DistanceBundleLoader INSTANCE = new DistanceBundleLoader();
  private ServiceLoader<DistanceBundleService> loader;

  public static DistanceBundleLoader getInstance() {
    return INSTANCE;
  }

  public <U> DistanceFactoryBundle<U> getBundle(
      Class<U> structureType) {
    for (DistanceBundleService service : loader) {
      DistanceFactoryBundle<U> result = service.getBundle(structureType);
      if (result != null) {
        return result;
      }
    }
    throw new IllegalStateException(
        "No factory bundle for type " + structureType + " provided by any service");
  }

  public GenericDistanceFactoryBundle getGenericDistanceBundle() {
    return ServiceLoader.load(GenericDistanceBundleService.class).iterator().next().getBundle();
  }

  public BasicDistanceOperatorFactory getBasicDistanceFactory() {
    return ServiceLoader.load(BasicDistanceFactoryService.class).iterator().next().getFactory();
  }
}
