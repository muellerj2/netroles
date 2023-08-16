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

/**
 * Service implementing a few basic algorithms related to networks.
 */
public interface AlgoService {
  /**
   * Provides algorithms to traverse networks.
   * 
   * @return provider of traversal algorithms.
   */
  Traversal traversals();

  /**
   * Provides algorithms to analyze connectedness of networks.
   * 
   * @return provider of connectedness algorithms.
   */
  Connectedness connectedness();

  /**
   * Provides algorithms for some simple statistics.
   * 
   * @return provider of simple statistical algorithms.
   */
  Stats stats();
}
