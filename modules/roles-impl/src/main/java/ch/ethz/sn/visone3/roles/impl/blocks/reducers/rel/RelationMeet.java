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

package ch.ethz.sn.visone3.roles.impl.blocks.reducers.rel;

import ch.ethz.sn.visone3.roles.blocks.RoleReducer;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

class RelationMeet implements RoleReducer<BinaryRelation> {

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
    return false;
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public boolean isAssociative() {
    return true;
  }

  @Override
  public boolean isCommutative() {
    return true;
  }

  @Override
  public BinaryRelation combine(BinaryRelation first, BinaryRelation second) {
    return BinaryRelations.infimum(first, second);
  }

  @Override
  public BinaryRelation combineLazily(BinaryRelation first, BinaryRelation second) {
    return BinaryRelations.lazyInfimum(first, second);
  }

  @Override
  public BinaryRelation refiningCombine(BinaryRelation base, BinaryRelation first,
      BinaryRelation second) {
    return BinaryRelations.infimum(BinaryRelations.infimum(base, first), second);
  }

  @Override
  public BinaryRelation coarseningCombine(BinaryRelation base, BinaryRelation first,
      BinaryRelation second) {
    return BinaryRelations.supremum(base, combine(first, second));
  }

  @Override
  public BinaryRelation refine(BinaryRelation base, BinaryRelation combined) {
    return BinaryRelations.infimum(base, combined);
  }

  @Override
  public BinaryRelation coarsen(BinaryRelation base, BinaryRelation combined) {
    return BinaryRelations.supremum(base, combined);
  }

}
