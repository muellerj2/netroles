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

import java.io.Serializable;

/**
 * Represents a network with some edge weight/attribute in a type-safe way.
 */
public class WeightedNetwork<T, M extends ConstMapping<T>> implements Serializable {
  private static final long serialVersionUID = 3279601926700689594L;
  private final Network network;
  private final M weight;

  /**
   * Constructs a container for a network with one associated edge weight/attribute.
   * 
   * @param network
   *          An incidence structure.
   * @param weight
   *          Weight associated with links.
   * @throws IllegalArgumentException
   *           if size of weight does not match the number of links.
   */
  public WeightedNetwork(final Network network, final M weight) {
    if (network.countDyadicIndices() != weight.size()) {
      throw new IllegalArgumentException("size mismatch");
    }
    this.network = network;
    this.weight = weight;
  }

  /**
   * Returns the incidence structure of the weighted network.
   * 
   * @return the incidence structure
   */
  public Network getNetwork() {
    return network;
  }

  /**
   * Returns the weight associated with the edges.
   * 
   * @return the weight
   */
  public M getWeight() {
    return weight;
  }
}
