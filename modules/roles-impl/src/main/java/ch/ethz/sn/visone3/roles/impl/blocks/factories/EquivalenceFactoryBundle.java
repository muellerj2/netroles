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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;

class EquivalenceFactoryBundle implements
    RoleOperatorFactoryBundle<ConstMapping.OfInt, EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt>, EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt>, BasicRoleOperatorFactory<ConstMapping.OfInt>, WeakStructuralRoleOperatorBuilderFactory<ConstMapping.OfInt>> {

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt> regular() {
    return new EquivalenceRegularRolesFactory();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt> equitable() {
    return regular().equitable();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt> weak() {
    return new EquivalenceWeakRolesFactory();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt> weaklyEquitable() {
    return weak().equitable();
  }

  @Override
  public RoleOperatorBuilderFactory<ConstMapping.OfInt> strongStructural() {
    return new EquivalenceStrongStructuralRolesFactory();
  }

  @Override
  public WeakStructuralRoleOperatorBuilderFactory<ConstMapping.OfInt> weakStructural() {
    return new EquivalenceWeakStructuralRolesFactory();
  }

  @Override
  public EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt> generic() {
    return new EquivalenceGenericRolesFactory();
  }

  @Override
  public BasicRoleOperatorFactory<ConstMapping.OfInt> basic() {
    return new EquivalenceBasicRoleOperatorFactory();
  }

}
