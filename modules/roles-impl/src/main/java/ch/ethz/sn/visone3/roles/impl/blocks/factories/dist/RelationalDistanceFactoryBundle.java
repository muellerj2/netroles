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
package ch.ethz.sn.visone3.roles.impl.blocks.factories.dist;

import ch.ethz.sn.visone3.roles.blocks.bundles.DistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.DistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;

class RelationalDistanceFactoryBundle implements DistanceFactoryBundle<BinaryRelation> {

  @Override
  public VariableDistanceBuilderFactory<BinaryRelation> regular() {
    return new RelationalRegularRolesDistanceFactory();
  }

  @Override
  public VariableDistanceBuilderFactory<BinaryRelation> equitable() {
    return regular().equitable();
  }

  @Override
  public VariableDistanceBuilderFactory<BinaryRelation> weak() {
    return new GenericWeakRolesDistanceFactory<>();
  }

  @Override
  public VariableDistanceBuilderFactory<BinaryRelation> weaklyEquitable() {
    return weak().equitable();
  }

  @Override
  public DistanceBuilderFactory<BinaryRelation> strongStructural() {
    return new GenericStrongStructuralRolesDistanceFactory<>();
  }

  @Override
  public DistanceBuilderFactory<BinaryRelation> weakStructural() {
    return new GenericWeakStructuralRolesDistanceFactory<>();
  }
}
