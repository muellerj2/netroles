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

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

abstract class RelationalConstantRoleOperatorBase implements RoleOperator<BinaryRelation> {

  @Override
  public boolean isIsotone() {
    return true;
  }

  @Override
  public boolean isNonincreasing() {
    return false;
  }

  @Override
  public boolean isNondecreasing() {
    return false;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
    return BinaryRelations.infimum(toRefine, relative(in));
  }

  @Override
  public BinaryRelation relativeCoarsening(BinaryRelation in, BinaryRelation toCoarsen) {
    return BinaryRelations.supremum(toCoarsen, relative(in));
  }

  @Override
  public BinaryRelation interior(BinaryRelation in) {
    return restrict(in);
  }
  
  @Override
  public BinaryRelation closure(BinaryRelation in) {
    return extend(in);
  }
}
