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

package ch.ethz.sn.visone3.roles.impl.blocks.factories.dist;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicDistanceOperatorFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.structures.LazyIntDistanceMatrixImpl;

/**
 * Factory class for some basic operations on distances.
 */
class BasicDistanceOperatorFactoryImpl implements BasicDistanceOperatorFactory {

  @Override
  public Operator<IntDistanceMatrix, IntDistanceMatrix> symmetrizeAdd() {
    return new IsotoneDistanceOperatorBase<IntDistanceMatrix>() {

      @Override
      public IntDistanceMatrix apply(IntDistanceMatrix in) {
        return new LazyIntDistanceMatrixImpl(in.getDomainSize(),
            (i, j) -> in.getDistance(i, j) + in.getDistance(j, i));
      }

      @Override
      public boolean isNonincreasing() {
        return true;
      }
    };
  }

  @Override
  public Operator<IntDistanceMatrix, IntDistanceMatrix> symmetrizeMax() {
    return new IsotoneDistanceOperatorBase<IntDistanceMatrix>() {

      @Override
      public IntDistanceMatrix apply(IntDistanceMatrix in) {
        return new LazyIntDistanceMatrixImpl(in.getDomainSize(),
            (i, j) -> Math.max(in.getDistance(i, j), in.getDistance(j, i)));
      }

      @Override
      public boolean isNonincreasing() {
        return true;
      }
    };
  }

  @Override
  public Operator<IntDistanceMatrix, IntDistanceMatrix> symmetrizeMin() {
    return new IsotoneDistanceOperatorBase<IntDistanceMatrix>() {

      @Override
      public IntDistanceMatrix apply(IntDistanceMatrix in) {
        return new LazyIntDistanceMatrixImpl(in.getDomainSize(),
            (i, j) -> Math.min(in.getDistance(i, j), in.getDistance(j, i)));
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }
    };
  }

}
