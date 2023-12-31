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

import ch.ethz.sn.visone3.roles.blocks.OperatorTraits;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

abstract class RankedTraitsAdjustableOperator implements RoleOperator<Ranking> {

  private final OperatorTraits traits;

  public RankedTraitsAdjustableOperator(OperatorTraits traits) {
    this.traits = traits;
  }

  @Override
  public boolean isIsotone() {
    return traits.isIsotone();
  }

  @Override
  public boolean isNonincreasing() {
    return traits.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return traits.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return traits.isConstant();
  }

  @Override
  public Ranking relativeRefining(Ranking in, Ranking toRefine) {
    return Rankings.infimum(toRefine, relative(in));
  }

  @Override
  public Ranking relativeCoarsening(Ranking in, Ranking toCoarsen) {
    return Rankings.supremum(toCoarsen, relative(in));
  }

}
