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
package ch.ethz.sn.visone3.roles.impl.structures;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.spi.BinaryRelationUtilityService;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

public class BinaryRelationUtilityServiceImpl implements BinaryRelationUtilityService {

  @Override
  public BinaryRelation universal(int size) {
    return CommonBinRelationRankingUtils.universal(size);
  }

  @Override
  public BinaryRelation identity(int size) {
    return CommonBinRelationRankingUtils.identity(size);
  }

  @Override
  public BinaryRelation fromEquivalence(ConstMapping.OfInt equivalence) {
    return CommonBinRelationRankingUtils.fromEquivalence(equivalence);
  }

  @Override
  public BinaryRelation infimum(BinaryRelation r1, BinaryRelation r2, boolean lazy) {
    return lazy ? CommonBinRelationRankingUtils.intersectLazily(r1, r2)
        : CommonBinRelationRankingUtils.intersect(r1, r2);
  }

  @Override
  public BinaryRelation supremum(BinaryRelation r1, BinaryRelation r2, boolean lazy) {
    return lazy ? lazySupremum(r1, r2) : supremum(r1, r2);
  }

  /**
   * Determines the lattice supremum (=set union) of two binary relations.
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice supremum/set union of two binary relations
   */
  private static BinaryRelation supremum(BinaryRelation r1, BinaryRelation r2) {
    if (!r2.isLazilyEvaluated() && r1.isLazilyEvaluated()) {
      BinaryRelation tmp = r1;
      r1 = r2;
      r2 = tmp;
    }
    int n = r1.domainSize();
    RelationBuilder<? extends BinaryRelation> builder = RelationBuilders
        .denseRelationBuilder(n);
    boolean[] value = new boolean[r1.domainSize()];
    int[] visited = new int[r1.domainSize()];
    int maxvisited = 0;
    for (int i = 0; i < n; ++i) {
      for (int j : r1.iterateInRelationFrom(i)) {
        builder.add(i, j);
        visited[maxvisited++] = j;
        value[j] = true;
      }
      if (!r2.isLazilyEvaluated() || !r2.isRandomAccess()) {
        for (int j : r2.iterateInRelationFrom(i)) {
          if (!value[j]) {
            builder.add(i, j);
          }
        }
      } else {
        for (int j = 0; j < n; ++j) {
          if (!value[j] && r2.contains(i, j)) {
            builder.add(i, j);
          }
        }
      }
      for (int j = 0; j < maxvisited; ++j) {
        value[visited[j]] = false;
      }
      maxvisited = 0;
    }

    return builder.build();
  }

  /**
   * Determines the lattice supremum (=set union) of two binary relations (perhaps in a lazy way).
   * 
   * @param r1
   *          the first relation
   * @param r2
   *          the second relation
   * @return the lattice supremum/set union of two binary relations
   */
  private static BinaryRelation lazySupremum(BinaryRelation r1, BinaryRelation r2) {
    if (r1.isRandomAccess() && r2.isRandomAccess()) {
      return new LazyUncachedBinaryRelationMatrixImpl(r1.domainSize(),
          (i, j) -> r1.contains(i, j) || r2.contains(i, j));
    }
    return supremum(r1, r2);
  }

  @Override
  public BinaryRelation closeTransitively(BinaryRelation relation) {
    return CommonBinRelationRankingUtils.closeTransitively(relation, false);
  }

}
