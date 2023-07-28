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

package ch.ethz.sn.visone3.roles.impl.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicSymmetrizableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.structures.Ranking;

public class RankedFactoryBundle implements
    RoleOperatorFactoryBundle<Ranking, EquitableLooseRoleOperatorBuilderFactory<Ranking>, EquitableLooseGenericRoleOperatorBuilderFactory<Ranking>, BasicSymmetrizableRoleOperatorFactory<Ranking>, WeakStructuralRoleOperatorBuilderFactory<Ranking>> {

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<Ranking> regular() {
    return new RankedRegularRolesFactory();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<Ranking> equitable() {
    return regular().equitable();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<Ranking> weak() {
    return new RankedWeakRolesFactory();
  }

  @Override
  public EquitableLooseRoleOperatorBuilderFactory<Ranking> weaklyEquitable() {
    return weak().equitable();
  }

  @Override
  public RoleOperatorBuilderFactory<Ranking> strongStructural() {
    return new RankedStrongStructuralRolesFactory();
  }

  @Override
  public WeakStructuralRoleOperatorBuilderFactory<Ranking> weakStructural() {
    return new RankedWeakStructuralRolesFactory();
  }

  @Override
  public EquitableLooseGenericRoleOperatorBuilderFactory<Ranking> generic() {
    return new RankedGenericRolesFactory();
  }

  @Override
  public BasicSymmetrizableRoleOperatorFactory<Ranking> basic() {
    return new RankedBasicRoleOperatorFactory();
  }

}
