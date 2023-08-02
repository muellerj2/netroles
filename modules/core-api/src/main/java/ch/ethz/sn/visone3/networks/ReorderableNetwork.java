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

import java.util.Comparator;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

/**
 * A network that permits reordering of neighborhoods.
 */
public interface ReorderableNetwork extends Network {

  @Override
  ReorderableDirectedGraph asDirectedGraph();

  @Override
  ReorderableUndirectedGraph asUndirectedGraph();

  /**
   * Sort individual node neighborhoods.
   *
   * @param nodeComparator
   *          node comparator.
   */
  void sortNeighborhoods(IntBinaryOperator nodeComparator);

  /**
   * Sort adjacency list entries within neighborhoods.
   * 
   * @param comparator
   *          adjacency list entry comparator
   */
  void sortNeighborhoods(Comparator<AdjacencyListEntry> comparator);

  /**
   * Sort adjacency list entries within neighborhoods in ascending order based on some integer value
   * in range [0, universeSize).
   * 
   * @param valueProducer
   *          integer value used to sort neighborhoods
   * @param universeSize
   *          the size of the integer interval [0, univereSize) in which the integer values lie
   */
  void sortNeighborhoods(ToIntFunction<AdjacencyListEntry> valueProducer, int universeSize);
}
