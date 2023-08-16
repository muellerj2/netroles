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

package ch.ethz.sn.visone3.networks;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

/**
 * Undirected graph perspective on a network.
 * 
 * <p>
 * Edges of an undirected graph are directed by context. This generally means that whenever an edge
 * is generated, then the edge's {@code source} is the node a function was called with, and the
 * {@code target} is the node opposite.
 */
public interface UndirectedGraph extends Graph {
  /**
   * Produces a stream over the neighbors of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return a stream over the neighbors.
   */
  IntStream getNeighborStream(int vertex);

  /**
   * Produces an iterable over the neighbors of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return an iterable over the neighbors.
   */
  default PrimitiveIterable.OfInt getNeighbors(final int vertex) {
    return () -> getNeighborStream(vertex).iterator();
  }

  /**
   * Returns an iterable over the incident edges of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return an iterable over the incident edges.
   */
  Iterable<Edge> getEdges(int vertex);

  /**
   * Returns a stream over the incident edges of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return an stream over the incident edges.
   */
  default Stream<Edge> getEdgeStream(final int vertex) {
    return StreamSupport.stream(getEdges(vertex).spliterator(), false);
  }

  /**
   * Determines the degree of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return the degree of the vertex.
   */
  int getDegree(int vertex);

  /**
   * Determines the number of edges in the graph.
   * 
   * @return the number of edges.
   */
  int countEdges();

  /**
   * Determines the number of loops in the graph.
   * 
   * @return the number of loops.
   */
  int countLoops();

  /**
   * Return a single edge connection {@code vertex1} with {@code vertex2}.
   *
   * @param vertex1 the first vertex.
   * @param vertex2 the second vertex.
   * @return an edge object representing an edge between {@code vertex1} and
   *         {@code vertex2}, or null if no such edge exists.
   * 
   * @implNote Defaults to linear search, so better use {@link #getEdges(int)} and
   *           to multiple at once if possible.
   */
  default Edge getEdge(final int vertex1, final int vertex2) {
    for (final Edge e : getEdges(vertex1)) {
      if (e.getTarget() == vertex2) {
        return e;
      }
    }
    return null;
  }
}
