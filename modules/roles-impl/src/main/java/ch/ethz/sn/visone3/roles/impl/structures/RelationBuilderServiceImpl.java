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
package ch.ethz.sn.visone3.roles.impl.structures;

import ch.ethz.sn.visone3.roles.spi.RelationBuilderService;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;

/**
 * Implementation of the service providing relation builders.
 */
public class RelationBuilderServiceImpl implements RelationBuilderService {

  @Override
  public RelationBuilder<? extends BinaryRelation> denseRelationBuilder(int size) {
    return new BinaryRelationMatrixImpl.Builder(size);
  }

  @Override
  public RelationBuilder<? extends Ranking> denseUnsafeRankingBuilder(int size) {
    RelationBuilder<? extends Ranking> builder = new BinaryRelationMatrixImpl.Builder(size);
    for (int i = 0; i < size; ++i) {
      builder.add(i, i);
    }
    return builder;
  }

  @Override
  public RelationBuilder<? extends Ranking> denseSafeRankingBuilder(int size) {
    return new RelationBuilder<Ranking>() {
      private RelationBuilder<? extends BinaryRelation> internal = new BinaryRelationMatrixImpl.Builder(
          size);

      @Override
      public void add(int i, int j) {
        internal.add(i, j);
      }

      @Override
      public Ranking build() {
        return Rankings.finestCoarseningRanking(internal.build());
      }
    };
  }


  /**
   * Returns a relation builder for a reducible relation (i.e., a relation where
   * pairs can be removed).
   * 
   * @param size domain size
   * @return the relation builder.
   */
  public static RelationBuilder<? extends ReducibleRelationOrRanking> denseReducibleRelationOrRankingBuilder(
      int size) {
    return new BinaryRelationMatrixImpl.Builder(size);
  }

  @Override
  public BinaryRelation relationFromMatrix(boolean[][] matrix) {
    return new BinaryRelationMatrixImpl(matrix);
  }

  @Override
  public Ranking rankingFromMatrixUnsafe(boolean[][] matrix) {
    return new BinaryRelationMatrixImpl(matrix);
  }
}
