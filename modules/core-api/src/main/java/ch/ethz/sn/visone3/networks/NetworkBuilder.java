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

/**
 * Stateful network builder. Collects node/edge information and returns one network. The produced
 * network is binary, i.e., weights are not directly represented in the network structure.
 */
public interface NetworkBuilder {
  /**
   * Ensure this (and all lower) nodes exist.
   *
   * @param node Node id.
   */
  void ensureNode(int node);

  /**
   * Ensure this (and all lower) affiliations exist.
   *
   * @param node Node id.
   * @implNote Defaults to {@link #ensureNode(int)}.
   */
  default void ensureAffiliation(final int node) {
    ensureNode(node);
  }

  /**
   * Adds a new edge or looks up the index of an existing edge.
   *
   * @return A non-negative id if this is first occurrence of the edge. If the edge is already
   * present returns {@code -id-1)}. So already existing edge return a negative id and {@code -id-1}
   * is the real edge id.
   */
  int addEdge(int source, int target);

  /**
   * Returns true if this accepts directed network information.
   * 
   * @return true if it accepts directed network information, false othewise.
   */
  boolean acceptsDirected();

  /**
   * Returns true if this accepts information on a two-mode network.
   * 
   * @return true if it accepts information on a two-mode network, false othewise.
   */
  boolean acceptsTwoModes();

  /**
   * Constructs the network from the supplied node and edge information.
   * 
   * @return Pure binary incidence network.
   */
  Network build();
}
