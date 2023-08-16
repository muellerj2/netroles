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
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;

/**
 * Factory class for some basic operations on equivalences.
 */
class EquivalenceBasicRoleOperatorFactory
    implements BasicRoleOperatorFactory<ConstMapping.OfInt> {

  @Override
  public RoleOperator<ConstMapping.OfInt> forward() {
    return new EquivalenceIsotoneRoleOperatorBase() {

      @Override
      public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
          ConstMapping.OfInt toRefine) {
        return Equivalences.infimum(toRefine, in);
      }

      @Override
      public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
        return in;
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public boolean isNonincreasing() {
        return true;
      }

    };
  }

  @Override
  public RoleOperator<ConstMapping.OfInt> produceConstant(OfInt constant) {
    return new EquivalenceConstantRoleOperatorBase() {

      @Override
      public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
        return constant;
      }
    };
  }

  @Override
  public RoleOperator<OfInt> meetWithConstant(OfInt constant) {
    return new EquivalenceIsotoneRoleOperatorBase() {

      @Override
      public boolean isNonincreasing() {
        return true;
      }

      @Override
      public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
          ConstMapping.OfInt toRefine) {
        return Equivalences.infimum(toRefine, relative(in));
      }

      @Override
      public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
        return Equivalences.infimum(constant, in);
      }
    };
  }

  @Override
  public RoleOperator<OfInt> joinWithConstant(OfInt constant) {
    return new EquivalenceIsotoneRoleOperatorBase() {

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
          ConstMapping.OfInt toRefine) {
        return Equivalences.infimum(toRefine, relative(in));
      }

      @Override
      public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
        return Equivalences.supremum(constant, in);
      }
    };
  }

}
