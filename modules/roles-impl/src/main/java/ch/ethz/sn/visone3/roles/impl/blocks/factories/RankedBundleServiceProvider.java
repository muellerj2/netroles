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
package ch.ethz.sn.visone3.roles.impl.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicSymmetrizableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.spi.RoleOperatorBundleService;
import ch.ethz.sn.visone3.roles.structures.Ranking;

/**
 * Service implementation providing a bundle of factories for rankings.
 */
public class RankedBundleServiceProvider implements RoleOperatorBundleService {

  @SuppressWarnings("unchecked")
  @Override
  public <U, V extends RoleOperatorBuilderFactory<U>, W extends GenericRoleOperatorBuilderFactory<U>, X extends BasicRoleOperatorFactory<U>, Y> RoleOperatorFactoryBundle<U, V, W, X, Y> getBundle(
      Class<U> structureType, Class<V> roleOperatorFactory,
      Class<W> genericRoleOperatorFactory, Class<X> basicRoleOperatorFactory,
      Class<Y> weakStructuralRoleOperatorFactory) {
    if (Ranking.class.equals(structureType)
        && (basicRoleOperatorFactory == null || basicRoleOperatorFactory
            .isAssignableFrom(BasicSymmetrizableRoleOperatorFactory.class))
        && (roleOperatorFactory == null
            || roleOperatorFactory.isAssignableFrom(EquitableLooseRoleOperatorBuilderFactory.class))
        && (genericRoleOperatorFactory == null || genericRoleOperatorFactory
            .isAssignableFrom(EquitableLooseGenericRoleOperatorBuilderFactory.class))
        && (weakStructuralRoleOperatorFactory == null || weakStructuralRoleOperatorFactory
            .isAssignableFrom(WeakStructuralRoleOperatorBuilderFactory.class))) {
      return (RoleOperatorFactoryBundle<U, V, W, X, Y>) new RankedFactoryBundle();
    }
    return null;
  }

}
