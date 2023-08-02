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
package ch.ethz.sn.visone3.roles.structures;

import ch.ethz.sn.visone3.roles.spi.RelationBuilderLoader;
import ch.ethz.sn.visone3.roles.spi.RelationBuilderService;

public class RelationBuilders {

  private RelationBuilders() {

  }

  private static final RelationBuilderService SERVICE = RelationBuilderLoader
      .getService();

  public static RelationBuilder<? extends BinaryRelation> denseRelationBuilder(int size) {
    return SERVICE.denseRelationBuilder(size);
  }

  public static RelationBuilder<? extends Ranking> denseUnsafeRankingBuilder(int size) {
    return SERVICE.denseUnsafeRankingBuilder(size);
  }

  public static RelationBuilder<? extends Ranking> denseSafeRankingBuilder(int size) {
    return SERVICE.denseSafeRankingBuilder(size);
  }
}
