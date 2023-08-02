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

import ch.ethz.sn.visone3.lang.Indexed;

/**
 * Edge in a graph.
 *
 */
public interface Edge extends Indexed {
  /**
   * Returns the edge storage index.
   * 
   * @return edge index.
   */
  @Override
  int getIndex();

  /**
   * Returns the source of the edge.
   * 
   * @return the edge's source.
   */
  int getSource();

  /**
   * Returns the target of the edge.
   * 
   * @return the edge's target.
   */
  int getTarget();
}

