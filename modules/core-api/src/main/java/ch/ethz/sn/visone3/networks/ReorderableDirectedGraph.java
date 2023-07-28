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

/**
 * A directed graph that permits reordering of neighborhoods.
 */
public interface ReorderableDirectedGraph extends DirectedGraph {

  /**
   * Swap the neighbors at position {@code n1} and {@code n2} in the in- or out-neighborhood of the
   * node {@code node}.
   * 
   * @param node
   *          the node in whose neighborhood neighbors are supposed to be swapped
   * @param direction
   *          sets whether neighbors in the in- or out-neighborhood are to be swapped.
   * @param n1
   *          the position of the first neighbor in the neighborhood
   * @param n2
   *          the position of the second neighbor in the neighborhood
   */
  void swapNeighbors(int node, Direction direction, int n1, int n2);
}
