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

import ch.ethz.sn.visone3.roles.blocks.bundles.DistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.bundles.GenericDistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicDistanceOperatorFactory;

/**
 * Provides access to the services offering factories for distance operators.
 */
public class DistanceBundleLoader {

  private DistanceBundleLoader() {
    loader = ServiceLoader.load(DistanceBundleService.class);
  }

  private static final DistanceBundleLoader INSTANCE = new DistanceBundleLoader();
  private ServiceLoader<DistanceBundleService> loader;

  /**
   * Gets the singleton loader instance.
   * 
   * @return the singleton
   */
  public static DistanceBundleLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Returns a bundle of factories for distance operators on the specified role
   * structure type, if any service provides such a bundle.
   * 
   * @param <U>           the role structure type.
   * @param structureType the class object representing the role structure type.
   * @return a factory bundle for distance operators on the specified role
   *         structure type.
   * @throws UnsupportedOperationException if there is no registered service that
   *                                       offers these factories for the
   *                                       specified role structure type.
   */
  public <U> DistanceFactoryBundle<U> getBundle(
      Class<U> structureType) {
    for (DistanceBundleService service : loader) {
      DistanceFactoryBundle<U> result = service.getBundle(structureType);
      if (result != null) {
        return result;
      }
    }
    throw new UnsupportedOperationException(
        "No factory bundle for type " + structureType + " provided by any service");
  }

  /**
   * Returns a bundle of factories for distance operators that are
   * user-customizable for any input role structure type.
   * 
   * @return a factory bundle for generic user-customizable distance operators.
   * @throws NoSuchElementException if there is no registered service offering a
   *                                factory bundle for generic user-customizable
   *                                distance operators.
   */
  public GenericDistanceFactoryBundle getGenericDistanceBundle() {
    return ServiceLoader.load(GenericDistanceBundleService.class).iterator().next().getBundle();
  }

  /**
   * Returns a bundle of factories for basic operators on distance matrices.
   * 
   * @return a factory bundle for basic operators on distances.
   * @throws NoSuchElementException if there is no registered service offering a
   *                                factory bundle for basic distance operators.
   */
  public BasicDistanceOperatorFactory getBasicDistanceFactory() {
    return ServiceLoader.load(BasicDistanceFactoryService.class).iterator().next().getFactory();
  }
}
