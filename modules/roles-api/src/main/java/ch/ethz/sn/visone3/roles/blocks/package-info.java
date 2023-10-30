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
/**
 * Role, distance and other operators for representing appropriate notions of
 * role.
 * 
 * <p>
 * This package is organized as follows:
 * <ul>
 * <li>The {@link ch.ethz.sn.visone3.roles.blocks.Operators} class offers
 * methods for composing operators in a data-flow-like manner. The operators can
 * be composed sequentially (series(), compose*()) or in parallel working on the
 * same initial input followed by a reduction operation (parallel()).</li>
 * <li>The {@link ch.ethz.sn.visone3.roles.blocks.RoleOperators} class provides
 * builders to construct operators for established notions of role on the
 * lattices of equivalences, rankings and binary relations. Moreover, it also
 * offers basic operators working on these lattices.</li>
 * <li>The {@link ch.ethz.sn.visone3.roles.blocks.DistanceOperators} class
 * provides builders to construct operators producing distances based on
 * established notions of role, as well as some basic operators working on
 * distances.</li>
 * <li>The {@link ch.ethz.sn.visone3.roles.blocks.Reducers} class provides
 * reduction operations for equivalences, rankings, binary relations and
 * distances. These are mainly intended to be used in conjunction with the
 * Operators.parallel() methods to combine several structures of the same type
 * into a single result.</li>
 * <li>The {@link ch.ethz.sn.visone3.roles.blocks.Converters} class provides
 * operators representing conversion operations between the different kinds of
 * role structure representations (equivalence, ranking, binary relation) and
 * distance matrices.
 * <li>
 * </ul>
 * 
 * <p>
 * For example, a role operator that applies the notion of regular equivalence
 * in both edge directions on a network can be defined as follows using the APIs
 * of {@link ch.ethz.sn.visone3.roles.blocks.Operators}
 * {@link ch.ethz.sn.visone3.roles.blocks.RoleOperators} and
 * {@link ch.ethz.sn.visone3.roles.blocks.Reducers}:
 * 
 * <pre>
 * Network network = ...;
 * NetworkView&lt;?, ?&gt; outgoingView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
 * NetworkView&lt;?, ?&gt; incomingView = NetworkView.fromNetworkRelation(network, Direction.INCOMING);
 * // outgoing direction only 
 * RoleOperator&lt;ConstMapping.OfInt&gt; outgoingRegularOp = RoleOperators.EQUIVALENCE.regular()
 *   .of(outgoingView).make();
 * // incoming direction only 
 * RoleOperator&lt;ConstMapping.OfInt&gt; incomingRegularOp = RoleOperators.EQUIVALENCE.regular()
 *   .of(incomingView).make();
 * RoleOperator&lt;ConstMapping.OfInt&gt; bidiRegularOp = Operators.parallel(
 *     // we apply the unidirectional operators in parallel
 *     // and combine them through intersection
 *     // (which is the meet of the equivalence lattice)
 *     Reducers.EQUIVALENCE.meet(), 
 *     outgoingRegularOp, // regular roles operator in outgoing direction only
 *     incomingRegularOp // regular roles operator in incoming direction only
 *   );
 * </pre>
 * 
 * <p>
 * Similarly, error-tolerant role structures can be defined by thresholding
 * deviations from the underlying notion of equivalence. For example, we can
 * define an error-tolerant notion of exact equivalence, which
 * <ul>
 * <li>still accepts a deviation from perfect exact equivalence by at most in
 * both directions of comparison as two nodes being equivalent,</li>
 * <li>but always considers isolates and non-isolates as non-equivalent,
 * and</li>
 * <li>converts the binary relation obtained after thresholding to an
 * equivalence by applying transitive closure.</li>
 * </ul>
 * The following code defines an operator describing this error-tolerant kind of
 * role equivalence for the outgoing edge direction:
 * 
 * <pre>
 * Network network = ...;
 * NetworkView&lt;?, ?&gt; outgoingView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
 * RoleOperator&lt;ConstMapping.OfInt&gt; errortolerantOp = Operators.parallel( //
 *     Reducers.EQUIVALENCE.meet(), //
 *     Operators.composeRoleOp( //
 *         Operators.composeOp( //
 *             Operators.composeOp( // threshold pairwise distances from equitable equivalence by one
 *                 DistanceOperators.EQUIVALENCE.equitable().of(outgoingView).make(),
 *                 Converters.thresholdDistances((i, j) -&gt; 1)),
 *             // symmetrize (at most distance one in both directions)
 *             RoleOperators.BINARYRELATION.basic().symmetrize()),
 *         Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
 *     RoleOperators.EQUIVALENCE.weak().of(networkView).make()); // and split off isolates
 * </pre>
 */
package ch.ethz.sn.visone3.roles.blocks;
