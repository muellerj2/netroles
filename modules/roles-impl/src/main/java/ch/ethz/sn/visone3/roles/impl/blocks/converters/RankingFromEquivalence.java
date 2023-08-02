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

package ch.ethz.sn.visone3.roles.impl.blocks.converters;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

class RankingFromEquivalence implements RoleConverter<ConstMapping.OfInt, Ranking> {

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
  public Ranking convert(OfInt in) {
    return Rankings.fromEquivalence(in);
  }

  @Override
  public Ranking convertRefining(OfInt in, Ranking toRefine) {
    return Rankings.infimum(convert(in), toRefine);
  }

  @Override
  public Ranking convertCoarsening(OfInt in, Ranking toCoarsen) {
    return Rankings.supremum(convert(in), toCoarsen);
  }

}
