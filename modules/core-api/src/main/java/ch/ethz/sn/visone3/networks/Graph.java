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

package ch.ethz.sn.visone3.networks;

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

import java.util.stream.IntStream;

/**
 * Graph perspective on a network.
 */
public interface Graph {

  /**
   * Determines the number of used edge indices.
   * 
   * @return Size of underlying edge storage.
   */
  int countDyadicIndices();

  /**
   * Returns a stream over the set of vertices.
   * 
   * @return a stream over the vertices.
   */
  default IntStream getVertexStream() {
    return IntStream.range(0, countVertices());
  }

  /**
   * Returns an iterable over the set of vertices.
   * 
   * @return an iterable over the vertices.
   */
  default PrimitiveIterable.OfInt getVertices() {
    return () -> getVertexStream().iterator();
  }

  /**
   * Determines the number of vertices.
   * 
   * @return the number of vertices.
   */
  int countVertices();

  /**
   * Produces an iterable over the set of edges in the graph.
   * 
   * @return an iterable over all edges.
   */
  Iterable<Edge> getEdges();
}
