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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.spi.RankingUtilityService;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

/**
 * Service making implementations of common operations on rankings available.
 */
public class RankingUtilityServiceImpl implements RankingUtilityService {

  @Override
  public Ranking universal(int size) {
    return CommonBinRelationRankingUtils.universal(size);
  }

  @Override
  public Ranking identity(int size) {
    return CommonBinRelationRankingUtils.identity(size);
  }

  @Override
  public Ranking fromEquivalence(ConstMapping.OfInt equivalence) {
    return CommonBinRelationRankingUtils.fromEquivalence(equivalence);
  }

  @Override
  public Ranking infimum(Ranking r1, Ranking r2, boolean lazy) {
    return lazy ? CommonBinRelationRankingUtils.intersectLazily(r1, r2)
        : CommonBinRelationRankingUtils.intersect(r1, r2);
  }

  @Override
  public Ranking supremum(Ranking r1, Ranking r2, boolean lazy) {
    return supremum(r1, r2); // no lazy evaluation (yet?)
  }

  /**
   * Determines the lattice supremum (=set union followed by transitive closure) of two rankings.
   * 
   * @param r1
   *          the first ranking
   * @param r2
   *          the second ranking
   * @return the lattice supremum of two rankings
   */
  private static Ranking supremum(Ranking r1, Ranking r2) {

    int n = r1.domainSize();
    boolean[][] closure = new boolean[n][n];
    RelationBuilder<? extends Ranking> builder = RelationBuilders
        .denseUnsafeRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      for (int j : r1.iterateInRelationFrom(i)) {
        closure[i][j] = true;
        builder.add(i, j);
      }
    }

    for (int i = 0; i < n; ++i) {
      for (int j : r2.iterateInRelationFrom(i)) {
        if (!closure[i][j]) {
          for (int k = 0; k < n; ++k) {
            if (k != j && closure[k][i] && !closure[k][j]) {
              for (int l = 0; l < n; ++l) {
                if (!closure[k][l] && closure[j][l]) {
                  closure[k][l] = true;
                  builder.add(k, l);
                }
              }
            }
          }
        }
      }
    }

    return builder.build();
  }

  @Override
  public Ranking closeTransitively(BinaryRelation relation) {
    return CommonBinRelationRankingUtils.closeTransitively(relation, true);
  }

}
