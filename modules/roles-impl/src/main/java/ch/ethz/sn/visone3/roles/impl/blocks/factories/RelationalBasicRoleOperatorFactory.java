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
import ch.ethz.sn.visone3.roles.blocks.factories.BasicCloseableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.impl.structures.LazyUncachedBinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

public class RelationalBasicRoleOperatorFactory
    implements BasicCloseableRoleOperatorFactory<BinaryRelation> {

  @Override
  public RoleOperator<BinaryRelation> forward() {
    return new RelationalIsotoneRoleOperatorBase() {
      @Override
      public BinaryRelation relative(BinaryRelation in) {
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
  public RoleOperator<BinaryRelation> produceConstant(BinaryRelation constant) {
    return new RelationalConstantRoleOperatorBase() {

      @Override
      public BinaryRelation relative(BinaryRelation in) {
        // TODO Auto-generated method stub
        return constant;
      }
    };
  }

  @Override
  public RoleOperator<BinaryRelation> meetWithConstant(BinaryRelation constant) {
    return new RelationalIsotoneRoleOperatorBase() {

      @Override
      public boolean isNonincreasing() {
        return true;
      }

      @Override
      public BinaryRelation relative(BinaryRelation in) {
        return BinaryRelations.infimum(constant, in);
      }
    };
  }

  @Override
  public RoleOperator<BinaryRelation> joinWithConstant(BinaryRelation constant) {
    return new RelationalIsotoneRoleOperatorBase() {

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public BinaryRelation relative(BinaryRelation in) {
        return BinaryRelations.supremum(constant, in);
      }
    };
  }

  @Override
  public RoleOperator<BinaryRelation> symmetrize() {
    return new RelationalIsotoneRoleOperatorBase() {

      @Override
      public BinaryRelation relative(BinaryRelation in) {
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
  public RoleOperator<BinaryRelation> invert() {
    return new RelationalIsotoneRoleOperatorBase() {

      @Override
      public BinaryRelation relative(BinaryRelation in) {
        return in.invert();
      }

    };
  }

  @Override
  public RoleOperator<BinaryRelation> closeTransitively() {
    return new RelationalIsotoneRoleOperatorBase() {

      @Override
      public BinaryRelation relative(BinaryRelation in) {
        return BinaryRelations.closeTransitively(in);
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }
    };
  }

}
