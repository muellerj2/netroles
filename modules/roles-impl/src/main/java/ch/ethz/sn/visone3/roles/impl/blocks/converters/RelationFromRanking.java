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

package ch.ethz.sn.visone3.roles.impl.blocks.converters;

import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;

class RelationFromRanking implements RoleConverter<Ranking, BinaryRelation> {

  @Override
  public boolean isIsotone() {
    return true;
  }

  @Override
  public boolean isNonincreasing() {
    return true;
  }

  @Override
  public boolean isNondecreasing() {
    return true;
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public BinaryRelation convert(Ranking in) {
    return in.asBinaryRelation();
  }

  @Override
  public BinaryRelation convertRefining(Ranking in, BinaryRelation toRefine) {
    return BinaryRelations.infimum(convert(in), toRefine);
  }

  @Override
  public BinaryRelation convertCoarsening(Ranking in, BinaryRelation toCoarsen) {
    return BinaryRelations.supremum(convert(in), toCoarsen);
  }

}
