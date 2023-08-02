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
import ch.ethz.sn.visone3.roles.structures.Ranking;

public class RankedDistanceFactoryBundle implements DistanceFactoryBundle<Ranking> {

  @Override
  public VariableDistanceBuilderFactory<Ranking> regular() {
    return new RankedRegularRolesDistanceFactory();
  }

  @Override
  public VariableDistanceBuilderFactory<Ranking> equitable() {
    return regular().equitable();
  }

  @Override
  public VariableDistanceBuilderFactory<Ranking> weak() {
    return new GenericWeakRolesDistanceFactory<>();
  }

  @Override
  public VariableDistanceBuilderFactory<Ranking> weaklyEquitable() {
    return weak().equitable();
  }

  @Override
  public DistanceBuilderFactory<Ranking> strongStructural() {
    return new GenericStrongStructuralRolesDistanceFactory<>();
  }

  @Override
  public DistanceBuilderFactory<Ranking> weakStructural() {
    return new GenericWeakStructuralRolesDistanceFactory<>();
  }
}
