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

import ch.ethz.sn.visone3.lang.ConstMapping;

/**
 * Representation of a network.
 *
 */
public interface Network {
  /**
   * Determines if the network is directed.
   * 
   * @return True if it is directed, false otherwise.
   */
  boolean isDirected();

  /**
   * Determines if the network is two-mode.
   * 
   * @return True if it is two-mode, false otherwise.
   */
  boolean isTwoMode();

  /**
   * Returns the number of vertex indices.
   * 
   * @return the number of vertex indices.
   */
  int countMonadicIndices();

  /**
   * Returns the number of storage indices. This is different from the number of relations/edges
   * since some edges might share the weight.
   * 
   * @return the number of storage indices.
   */
  int countDyadicIndices();

  /**
   * Returns the graph perspective of this network.
   * 
   * @return the graph perspective.
   */
  default Graph asGraph() {
    if (isDirected()) {
      return asDirectedGraph();
    }
    return asUndirectedGraph();
  }

  /**
   * Returns the directed graph perspective of this network.
   * 
   * @return the directed graph perspective
   * @throws UnsupportedOperationException
   *           if the network does not have an immediate directed graph perspective.
   */
  DirectedGraph asDirectedGraph();

  /**
   * Returns the undirected graph perspective of this network.
   * 
   * @return the undirected graph perspective
   * @throws UnsupportedOperationException
   *           if the network does not have an immediate undirected graph perspective.
   */
  UndirectedGraph asUndirectedGraph();

  /**
   * Returns the relational interpretation of this network.
   * 
   * @return the network interpretd as a relation.
   */
  Relation asRelation();

  /**
   * Produces a copy of this network that allows reordering of neighborhoods.
   * 
   * @return reorderable copy of this network.
   */
  ReorderableNetwork reorderable();

  /**
   * Creates a copy of the adjacency matrix.
   *
   * <p>The element at position {@code i,j} will be
   * <ul>
   * <li>the observed value for observed entries,</li>
   * <li>the default value for the diagonal for unobserved diagonal entries,</li>
   * <li>the general default value if unobserved.</li>
   * </ul>
   *
   * <p>This provides very limited view on matrices. For more complex needs, and for directly
   * filling primitive matrices, use {@link Relation#getRelationships()} to walk over all
   * observations and fill you matrix structure.
   *
   * @param fill     Default value for all values.
   * @param diagonal Default value for the diagonal.
   * @return new adjacency matrix.
   */
  <T> T[][] asMatrix(final T fill, final T diagonal, final ConstMapping<T> mapping);

  /**
   * Returns a builder that can produce networks of the same kind as this network.
   * 
   * @return a builder compatible with this network.
   */
  NetworkBuilder builder();

  /**
   * Checks if two networks are from the same implementation and their incidence structure is
   * equal.
   *
   * @return if structure is equal
   */
  boolean structureEquals(Network network);
}
