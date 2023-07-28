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

package ch.ethz.sn.visone3.roles.impl.blocks.reducers.rank;

import ch.ethz.sn.visone3.roles.blocks.RoleReducer;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

class RankingJoin implements RoleReducer<Ranking> {

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
    return true;
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
  public Ranking combine(Ranking first, Ranking second) {
    return Rankings.supremum(first, second);
  }

  @Override
  public Ranking refiningCombine(Ranking base, Ranking first, Ranking second) {
    return Rankings.infimum(combine(first, second), base);
  }

  @Override
  public Ranking coarseningCombine(Ranking base, Ranking first, Ranking second) {
    return Rankings.supremum(Rankings.supremum(base, first), second);
  }

  @Override
  public Ranking refine(Ranking base, Ranking combined) {
    return Rankings.infimum(base, combined);
  }

  @Override
  public Ranking coarsen(Ranking base, Ranking combined) {
    return Rankings.supremum(base, combined);
  }

}
