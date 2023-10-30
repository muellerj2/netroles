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
 * This package provides network representations.
 * 
 * <p>
 * Network representations of a specific type can be created by obtaining a
 * corresponding builder from
 * {@link ch.ethz.sn.visone3.networks.NetworkProvider} and using it to specify
 * the network structure. This results in the following typical invocation
 * sequence:
 * 
 * <pre>
 * // get builder
 * NetworkBuilder builder = NetworkProvider.builder(dyadType);
 * // add nodes and edges
 * builder.ensureNode(node);
 * builder.addEdge(source, target);
 * // obtain network
 * Network network = builder.build();
 * </pre>
 * 
 * <p>
 * Network representations can support four different kinds of perspectives on
 * networks:
 * <ul>
 * <li>undirected graph perspective specified by
 * {@link ch.ethz.sn.visone3.networks.UndirectedGraph},</li>
 * <li>directed graph perspective specified by
 * {@link ch.ethz.sn.visone3.networks.DirectedGraph},</li>
 * <li>relational perspective specified by
 * {@link ch.ethz.sn.visone3.networks.Relation}, and</li>
 * <li>matrix perspective supported by method
 * {@link ch.ethz.sn.visone3.networks.Network#asMatrix(Object, Object, ch.ethz.sn.visone3.lang.ConstMapping)}.</li>
 * </ul>
 * However, network representations are not required to support all of these
 * perspectives.
 */
package ch.ethz.sn.visone3.networks;
