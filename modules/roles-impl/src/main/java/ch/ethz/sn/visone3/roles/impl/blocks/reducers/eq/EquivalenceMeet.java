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

package ch.ethz.sn.visone3.roles.impl.blocks.reducers.eq;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;

class EquivalenceMeet implements RoleReducer<ConstMapping.OfInt> {

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
  public OfInt combine(OfInt first, OfInt second) {
    return Equivalences.infimum(first, second);
  }

  @Override
  public OfInt refiningCombine(OfInt base, OfInt first, OfInt second) {
    return Equivalences.infimum(base, Equivalences.infimum(first, second));
  }

  @Override
  public OfInt coarseningCombine(OfInt base, OfInt first, OfInt second) {
    return Equivalences.supremum(base, Equivalences.infimum(first, second));
  }

  @Override
  public OfInt refine(OfInt base, OfInt combined) {
    return Equivalences.infimum(base, combined);
  }

  @Override
  public OfInt coarsen(OfInt base, OfInt combined) {
    return Equivalences.supremum(base, combined);
  }

}
