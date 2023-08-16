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

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicSymmetrizableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.impl.structures.LazyUncachedBinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

/**
 * Factory class for some basic operations on rankings.
 */
class RankedBasicRoleOperatorFactory
    implements BasicSymmetrizableRoleOperatorFactory<Ranking> {

  @Override
  public RoleOperator<Ranking> forward() {
    return new RankedIsotoneRoleOperatorBase() {
      @Override
      public Ranking relative(Ranking in) {
        return in;
      }

      @Override
      public boolean isNonincreasing() {
        return true;
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }
    };
  }

  @Override
  public RoleOperator<Ranking> produceConstant(Ranking constant) {
    return new RankedConstantRoleOperatorBase() {

      @Override
      public Ranking relative(Ranking in) {
        // TODO Auto-generated method stub
        return constant;
      }
    };
  }

  @Override
  public RoleOperator<Ranking> meetWithConstant(Ranking constant) {
    return new RankedIsotoneRoleOperatorBase() {

      @Override
      public boolean isNonincreasing() {
        return true;
      }

      @Override
      public Ranking relative(Ranking in) {
        return Rankings.infimum(constant, in);
      }
    };
  }

  @Override
  public RoleOperator<Ranking> joinWithConstant(Ranking constant) {
    return new RankedIsotoneRoleOperatorBase() {

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public Ranking relative(Ranking in) {
        return Rankings.supremum(constant, in);
      }
    };
  }

  @Override
  public RoleOperator<Ranking> symmetrize() {
    return new RankedIsotoneRoleOperatorBase() {

      @Override
      public Ranking relative(Ranking in) {
        return new LazyUncachedBinaryRelationMatrixImpl(in.domainSize(),
            (i, j) -> in.contains(i, j) && in.contains(j, i));
      }

      @Override
      public boolean isNonincreasing() {
        return true;
      }
    };
  }

  @Override
  public RoleOperator<Ranking> invert() {
    return new RankedIsotoneRoleOperatorBase() {

      @Override
      public Ranking relative(Ranking in) {
        return in.invert();
      }
    };
  }

}
