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

import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;

import java.util.ServiceLoader;

public class RoleOperatorBundleLoader {

  private RoleOperatorBundleLoader() {
    loader = ServiceLoader.load(RoleOperatorBundleService.class);
  }

  private static final RoleOperatorBundleLoader INSTANCE = new RoleOperatorBundleLoader();
  private ServiceLoader<RoleOperatorBundleService> loader;

  public static RoleOperatorBundleLoader getInstance() {
    return INSTANCE;
  }

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
