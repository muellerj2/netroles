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
import ch.ethz.sn.visone3.roles.blocks.factories.BasicCloseableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;

public class RelationalFactoryBundle implements
    RoleOperatorFactoryBundle<BinaryRelation, VariableRoleOperatorBuilderFactory<BinaryRelation>, VariableGenericRoleOperatorBuilderFactory<BinaryRelation>, BasicCloseableRoleOperatorFactory<BinaryRelation>, RoleOperatorBuilderFactory<BinaryRelation>> {

  @Override
  public VariableRoleOperatorBuilderFactory<BinaryRelation> regular() {
    return new RelationalRegularRolesFactory();
  }

  @Override
  public VariableRoleOperatorBuilderFactory<BinaryRelation> equitable() {
    return regular().equitable();
  }

  @Override
  public VariableRoleOperatorBuilderFactory<BinaryRelation> weak() {
    return new RelationalWeakRolesFactory();
  }

  @Override
  public VariableRoleOperatorBuilderFactory<BinaryRelation> weaklyEquitable() {
    return weak().equitable();
  }

  @Override
  public RoleOperatorBuilderFactory<BinaryRelation> strongStructural() {
    return new RelationalStrongStructuralRolesFactory();
  }

  @Override
  public RoleOperatorBuilderFactory<BinaryRelation> weakStructural() {
    return new RelationalWeakStructuralRolesFactory();
  }

  @Override
  public VariableGenericRoleOperatorBuilderFactory<BinaryRelation> generic() {
    return new RelationalGenericRolesFactory();
  }

  @Override
  public BasicCloseableRoleOperatorFactory<BinaryRelation> basic() {
    return new RelationalBasicRoleOperatorFactory();
  }

}
