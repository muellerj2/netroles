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

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

/**
 * Directed graph perspective on a network.
 */
public interface DirectedGraph extends Graph {

  /**
   * Returns a stream over the in-neighbors.
   * 
   * @param target
   *          the focal vertex.
   * @return stream over in-neighbors.
   */
  IntStream getInNeighborStream(int target);

  /**
   * Returns an iterable over the in-neighbors.
   * 
   * @param target
   *          the focal vertex.
   * @return iterable over in-neighbors.
   */
  default PrimitiveIterable.OfInt getInNeighbors(final int target) {
    return () -> getInNeighborStream(target).iterator();
  }

  /**
   * Returns an iterable over the incoming incident edges of a vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return iterable over incoming incident edges.
   */
  Iterable<Edge> getInEdges(int vertex);

  /**
   * Determines the in-degree of the specified vertex.
   * 
   * @param target
   *          the specified vertex.
   * @return the in-degree of the vertex.
   */
  int getInDegree(int target);

  /**
   * Returns a stream over the out-neighbors.
   * 
   * @param source
   *          the focal vertex.
   * @return stream over out-neighbors.
   */
  IntStream getOutNeighborStream(int source);

  /**
   * Returns an iterable over the out-neighbors.
   * 
   * @param source
   *          the focal vertex.
   * @return iterable over out-neighbors.
   */
  default PrimitiveIterable.OfInt getOutNeighbors(final int source) {
    return () -> getOutNeighborStream(source).iterator();
  }

  /**
   * Returns an iterable over the outgoing incident edges of a vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return iterable over outgoing incident edges.
   */
  Iterable<Edge> getOutEdges(int vertex);

  /**
   * Determines the out-degree of the specified vertex.
   * 
   * @param source
   *          the specified vertex.
   * @return the out-degree of the vertex.
   */
  int getOutDegree(int source);

  /**
   * Returns a stream over the in- and out-neighbors of the specified vertex. Note that a vertex can
   * appear twice if it is both an in- and an out-neighbor.
   * 
   * @param vertex
   *          the focal vertex.
   * @return stream over out-neighbors.
   */
  IntStream getNeighborStream(int vertex);

  /**
   * Returns an iterable over in- and out-neighbors.
   *
   * @param vertex focal vertex.
   * @return iterable over the neighbours.
   */
  default PrimitiveIterable.OfInt getNeighbors(final int vertex) {
    return () -> getNeighborStream(vertex).iterator();
  }

  /**
   * Returns an iterable over the incident incoming and outgoing edges of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return an iterable overt the incident edges.
   */
  Iterable<Edge> getEdges(int vertex);

  /**
   * Returns the degree (=in- plus out-degree) of the specified vertex.
   * 
   * @param vertex
   *          the vertex.
   * @return the degree of the vertex
   */
  int getDegree(int vertex);

  /**
   * Returns the number of edges.
   * 
   * @return number of edges
   */
  int countEdges();

  /**
   * Returns a single edge connection {@code source} with {@code target}.
   *
   * @return an edge object representing an edge from {@code source} to {@code target}, or null if
   *         no such edge exists.
   * @implNote Defaults to linear search, so better use {@link #getEdges(int)} and search multiple
   *           at once if possible.
   */
  default Edge getEdge(final int source, final int target) {
    for (final Edge e : getEdges(source)) {
      if (e.getTarget() == target) {
        return e;
      }
    }
    return null;
  }
}
