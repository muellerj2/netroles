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

package ch.ethz.sn.visone3.networks.impl;

import ch.ethz.sn.visone3.networks.Edge;

/**
 * Default undirected edge implementation.
 */
public class UndirectedEdgeImpl implements Edge {
  private final int index;
  private final int self;
  private final int opposite;

  /**
   * Constructs a representative of an undirected edge.
   * 
   * @param index
   *          the edge index.
   * @param self
   *          the (search) source of the edge.
   * @param opposite
   *          the vertex at the other end of the edge.
   */
  public UndirectedEdgeImpl(final int index, final int self, final int opposite) {
    this.index = index;
    this.self = self;
    this.opposite = opposite;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public int getSource() {
    return self;
  }

  @Override
  public int getTarget() {
    return opposite;
  }

  @Override
  public String toString() {
    return String.format("({%d,%d},[%d])", self, opposite, index);
  }
}
