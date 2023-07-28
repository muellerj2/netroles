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

import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkService;

public class ArrayNetworkService implements NetworkService {

  @Override
  public String getName() {
    return "ch.ethz.sn.visone3.core-impl.networks";
  }

  @Override
  public boolean supports(final DyadType type) {
    switch (type) {
      case DIRECTED:
      case UNDIRECTED:
      case TWO_MODE:
        return true;
      default:
        return false;
    }
  }

  @Override
  public NetworkBuilder createBuilder(final DyadType type) {
    switch (type) {
      case DIRECTED:
        return new ArrayDirectedNetwork.Builder();
      case UNDIRECTED:
        return new UndirectedNetworkImpl.Builder();
      case TWO_MODE:
        return new TwoModeNetwork.Builder();
      default:
        throw new IllegalArgumentException("unknown dyad type");
    }
  }

}
