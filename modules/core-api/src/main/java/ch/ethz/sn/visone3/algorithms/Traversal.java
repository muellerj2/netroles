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
package ch.ethz.sn.visone3.algorithms;

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.networks.Edge;

import java.util.function.IntFunction;

/**
 * Full and limited length graph traversals.
 *
 * <p>
 * Most of the function are independent of the graph implementation and require only a
 * {@code IntFunction<PrimitiveIterable.OfInt>} as neighbourhood function.
 */
public interface Traversal {

  /**
   * Multi source breadth-first-search.
   *
   * @param numVertices
   *          Number of nodes.
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new BFS is started from each of these nodes if the BFS number is still
   *          negative.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   * @return the BFS number mapping.
   */
  Mapping.OfInt bfs(final int numVertices, final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final PrimitiveIterable.OfInt starts, final Visitor visitor);

  /**
   * Multi source depth-first-search.
   *
   * @param numVertices
   *          Number of nodes.
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new DFS is started from each of this nodes if the DFS number is still
   *          negative.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   * @return the DFS number mapping.
   */
  Mapping.OfInt dfs(final int numVertices, final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final PrimitiveIterable.OfInt starts, final Visitor visitor);

  /**
   * Multi source depth-first-search.
   *
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new DFS is started from each of these nodes if the DFS number is still
   *          negative.
   * @param dfsns
   *          Mapping as storage for DFS numbers.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  void dfs(final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final PrimitiveIterable.OfInt starts, final Mapping.OfInt dfsns, final Visitor visitor);

  /**
   * Multi-source depth-first search. This variant should be used if the visitor needs more
   * information on the edges, especially the edge indices.
   * 
   * @param numEdges
   *          Number of edges.
   * @param edges
   *          Function of incident edges.
   * @param starts
   *          Start points. A new DFS is started from each of these nodes if the DFS number is still
   *          negative.
   * @param dfsns
   *          Mapping as storage for DFS numbers.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  void edgeDfs(final int numEdges, final IntFunction<Iterable<Edge>> edges,
      final PrimitiveIterable.OfInt starts, final Mapping.OfInt dfsns, final Visitor visitor);

  /**
   * Traversal listener.
   */
  interface Visitor {
    Visitor NULL = new Visitor() {
      @Override
      public void startSearch(final int node) {
      }

      @Override
      public void endSearch() {
      }

      @Override
      public void visitEdge(final int source, final int target, int idx) {
      }

      @Override
      public void visitVertex(final int node) {
      }

      @Override
      public void backtrackVertex(final int node) {
      }

    };

    /**
     * Invoked when a new DFS search is started from the specified node, which was not reachable
     * from any of the prior nodes this method has been invoked on. Thus, this typically indicates
     * the exploration of a previously unidentified connected component (of some kind).
     * 
     * @param node
     *          Start node of the search.
     */
    void startSearch(int node);

    /**
     * Invoked when the DFS search from the previously notified start node ends. Thus, all nodes
     * reachable from the start node have been found, so the invocation indicates that the
     * exploration of the connected component (of some kind) has completed.
     */
    void endSearch();

    /**
     * Invoked when the search algorithm encounters an edge.
     * 
     * @param source
     *          Edge traversal source.
     * @param target
     *          Edge traversal target.
     * @param idx
     *          Edge index (if information available to the algorithm).
     */
    void visitEdge(int source, int target, int idx);

    /**
     * Invoked when the search algorithm encounters a vertex for the first time.
     * 
     * @param node
     *          Encountered node.
     */
    void visitVertex(int node);

    /**
     * Invoked when the search algorithm has traversed all of the node's incident edges and is
     * returning to the parent node along the edge that the backtracked node was originally found at
     * (DFS only).
     * 
     * @param node
     *          Backtracked node.
     */
    void backtrackVertex(int node);

  }
}
