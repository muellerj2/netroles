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

import java.util.ServiceLoader;

/**
 * Makes available network algorithms provided by a registered service.
 */
public final class AlgoProvider implements AlgoService {
  private static AlgoProvider INSTANCE;
  private static Traversal traversal;
  private static Stats stats;
  private static Connectedness connectedness;

  /**
   * Get singleton.
   * 
   * @return the singleton.
   */
  public static AlgoProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AlgoProvider();
    }
    return INSTANCE;
  }

  private AlgoProvider() {
    final ServiceLoader<AlgoService> loader = ServiceLoader.load(AlgoService.class);
    final AlgoService service = loader.iterator().next();
    traversal = service.traversals();
    connectedness = service.connectedness();
    stats = service.stats();
  }

  public Stats stats() {
    return stats;
  }

  public Connectedness connectedness() {
    return connectedness;
  }

  public Traversal traversals() {
    return traversal;
  }
}
