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

package ch.ethz.sn.visone3.roles.blocks;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.blocks.bundles.DistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.bundles.GenericDistanceFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicDistanceOperatorFactory;
import ch.ethz.sn.visone3.roles.spi.DistanceBundleLoader;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;

/**
 * Provides methods to produce operators calculating pairwise distances of network nodes for the
 * specified role notions.
 */
public class DistanceOperators {

  private DistanceOperators() {
  }

  /**
   * Factory bundle for distance operators relative to given equivalences.
   */
  public static final DistanceFactoryBundle<ConstMapping.OfInt> EQUIVALENCE = DistanceBundleLoader
      .getInstance().getBundle(ConstMapping.OfInt.class);
  /**
   * Factory bundle for distance operators relative to given rankings.
   */
  public static final DistanceFactoryBundle<Ranking> RANKING = DistanceBundleLoader.getInstance()
      .getBundle(Ranking.class);
  /**
   * Factory bundle for distance operators relative to given relations.
   */
  public static final DistanceFactoryBundle<BinaryRelation> BINARYRELATION = DistanceBundleLoader
      .getInstance().getBundle(BinaryRelation.class);

  /**
   * Factory bundle for distance operators relative to some given user-specified role structure
   * type.
   */
  public static final GenericDistanceFactoryBundle GENERIC = DistanceBundleLoader.getInstance()
      .getGenericDistanceBundle();

  /**
   * Factory for basic common kinds of transformations on distances.
   */
  public static final BasicDistanceOperatorFactory BASIC = DistanceBundleLoader.getInstance()
      .getBasicDistanceFactory();

}
