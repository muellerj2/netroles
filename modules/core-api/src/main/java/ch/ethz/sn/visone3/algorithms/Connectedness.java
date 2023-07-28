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

package ch.ethz.sn.visone3.algorithms;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

/**
 * Provides tools for establishing various notions of connectedness in a graph.
 */
public interface Connectedness {
  /**
   * Translates a mapping of nodes to component ids to a list of nested mappings containing the
   * nodes of each component.
   * 
   * @param components
   *          Mapping node id to component id.
   * @return Nested mappings with nodes separated by component.
   */
  List<PrimitiveList.OfInt> componentsToNodeLists(ConstMapping.OfInt components);

  /**
   * Determines the connected components of an undirected graph.
   * 
   * @param graph
   *          Undirected graph.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt components(UndirectedGraph graph);

  /**
   * Determines the connected components of a (node-)filtered undirected graph.
   * 
   * @param graph
   *          Undirected graph.
   * @param filter
   *          Node filter. Nodes where the filter returns false are treated as isolates.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt components(UndirectedGraph graph, IntPredicate filter);

  /**
   * Determines the 2-edge connected components of an undirected graph.
   * 
   * @param graph
   *          Directed graph.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt twoEdgeComponents(UndirectedGraph graph);

  /**
   * Determines the biconnected components of an undirected graph.
   * 
   * @param graph
   *          Directed graph.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt biconnectedComponents(UndirectedGraph graph);

  /**
   * Determines the strong connected components of a directed graph.
   * 
   * @param graph
   *          Undirected graph.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt strongComponents(DirectedGraph graph);

  /**
   * Determines the weak components of a directed graph.
   * 
   * @param graph
   *          Undirected graph.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt weakComponents(DirectedGraph graph);

  /**
   * Determines the weak connected components of an implicitly given directed graph.
   * 
   * @param numVertices
   *          Number of vertices in the graph.
   * @param neighbors
   *          Neighborhood function.
   * @return Mapping of nodes to non-negative representative component number.
   */
  Mapping.OfInt weakComponents(int numVertices, IntFunction<PrimitiveIterable.OfInt> neighbors);
}
