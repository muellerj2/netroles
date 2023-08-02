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
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;

abstract class EquivalenceConstantRoleOperatorBase implements RoleOperator<ConstMapping.OfInt> {

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
  public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in, ConstMapping.OfInt toRefine) {
    return Equivalences.infimum(toRefine, relative(in));
  }

  @Override
  public ConstMapping.OfInt relativeCoarsening(ConstMapping.OfInt in, ConstMapping.OfInt toCoarsen) {
    return Equivalences.supremum(toCoarsen, relative(in));
  }

  @Override
  public ConstMapping.OfInt interior(ConstMapping.OfInt in) {
    return restrict(in);
  }
  
  @Override
  public ConstMapping.OfInt closure(ConstMapping.OfInt in) {
    return extend(in);
  }
}
