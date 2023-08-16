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

import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;

/**
 * Provides access to the services offering factories for role operators.
 */
public class RoleOperatorBundleLoader {

  private RoleOperatorBundleLoader() {
    loader = ServiceLoader.load(RoleOperatorBundleService.class);
  }

  private static final RoleOperatorBundleLoader INSTANCE = new RoleOperatorBundleLoader();
  private ServiceLoader<RoleOperatorBundleService> loader;

  /**
   * Gets the loader's singleton instance.
   * 
   * @return the singleton.
   */
  public static RoleOperatorBundleLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Returns a bundle compatible with the specified role structure and factory
   * types, if any registered service offers them.
   * 
   * @param <U>                               role structure type.
   * @param <V>                               factory type for role operators.
   * @param <W>                               factory type for generic
   *                                          (user-customizable) role operators.
   * @param <X>                               factory type for basic operations on
   *                                          the specified role structure type.
   * @param <Y>                               factory type specifically to
   *                                          construct weak structural role
   *                                          operators.
   * @param structureType                     class object representing the role
   *                                          structure type.
   * @param roleOperatorFactory               class object representing the
   *                                          factory type for role operators.
   * @param genericRoleOperatorFactory        class object representing the
   *                                          factory type for generic
   *                                          user-customizable role operators.
   * @param basicRoleOperatorFactory          class object representing the
   *                                          factory type for basic operations on
   *                                          the type of role structure.
   * @param weakStructuralRoleOperatorFactory class object representing the
   *                                          factory type used to construct weak
   *                                          structural role operators.
   * @return bundle of factories compatible with the specified role structure and
   *         factory types.
   * @throws UnsupportedOperationException if no registered service provides a
   *                                       bundle of factories compatible with the
   *                                       specified role structure and factory
   *                                       types.
   */
  public <U, V extends RoleOperatorBuilderFactory<U>, //
      W extends GenericRoleOperatorBuilderFactory<U>, //
      X extends BasicRoleOperatorFactory<U>, Y> //
  RoleOperatorFactoryBundle<U, V, W, X, Y> getBundle(Class<U> structureType,
      Class<V> roleOperatorFactory, Class<W> genericRoleOperatorFactory,
      Class<X> basicRoleOperatorFactory, Class<Y> weakStructuralRoleOperatorFactory) {
    for (RoleOperatorBundleService service : loader) {
      RoleOperatorFactoryBundle<U, V, W, X, Y> result = service.getBundle(structureType,
          roleOperatorFactory, genericRoleOperatorFactory, basicRoleOperatorFactory,
          weakStructuralRoleOperatorFactory);
      if (result != null) {
        return result;
      }
    }
    throw new UnsupportedOperationException(
        "No factory bundle for type " + structureType + ", role operator factory "
            + roleOperatorFactory + ", generic role operator factory " + genericRoleOperatorFactory
            + " basic factory " + basicRoleOperatorFactory + "and weak structural role factory "
            + weakStructuralRoleOperatorFactory + " provided by any service");
  }

}
