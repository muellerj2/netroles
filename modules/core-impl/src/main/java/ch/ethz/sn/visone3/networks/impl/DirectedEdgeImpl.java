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

// TODO merge
/**
 * Default directed edge implementation.
 */
public class DirectedEdgeImpl implements Edge {
  private final int index;
  private final int source;
  private final int target;

  /**
   * Constructs a representative of a directed edge.
   * 
   * @param index
   *          the edge index.
   * @param source
   *          the source of the edge.
   * @param target
   *          the target of the edge.
   */
  public DirectedEdgeImpl(final int index, final int source, final int target) {
    this.index = index;
    this.source = source;
    this.target = target;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public int getSource() {
    return source;
  }

  @Override
  public int getTarget() {
    return target;
  }

  @Override
  public String toString() {
    return String.format("((%d,%d),[%d])", source, target, index);
  }
}
