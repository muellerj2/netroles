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
 * Provides methods to produce operators calculating pairwise distances of
 * network nodes from a given role structure for commonly used role notions.
 * 
 * <p>
 * {@link #BASIC} provides access for constructing distance operators that
 * represent basic operations transforming distance matrices, independent of a
 * particular definition of role or network structure. {@link #BINARYRELATION},
 * {@link #RANKING} and {@link #EQUIVALENCE} provide factories to operators that
 * derive distances based on a provided binary relation, ranking or equivalence,
 * respectively. These operators compute distances according to a common notion
 * of role and and network structure.
 * 
 * For example, the following code defines a operator computing the distance of
 * pairwise comparisons from perfect regular equivalence in outgoing direction.
 * These pairwise comparisons are performed by matching incident relationships
 * (or edges). According to the operator definition, the matching must also obey
 * a particular weak ordering among relationships/edges or neighbors (always
 * matching one relationship/edge with a greater equal one), and admissible
 * matchings are assigned a user-defined cost. The pairwise distances then
 * describe the minimum cost achievable under the chosen role notion given the
 * specified weak ordering and matching costs among relationships/edges.
 * 
 * <pre>
 * Network network = ...;
 * Comparator&lt;Relationship&gt; edgeComparator = ...;
 * ToIntBiFunction&lt;Relationship, Relationship&gt; matchingCost = ...;
 * NetworkView&lt;Relationship, Relationship&gt; outgoingView =
 *   NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
 * Operator&lt;ConstMapping.OfInt, IntDistanceMatrix&gt; regularDistanceOp =
 *   DistanceOperators.EQUIVALENCE.regular().of(outgoingView)
 *     .comp(edgeComparator).substCost(matchingCost).make();
 * </pre>
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
