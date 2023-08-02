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

import java.util.ServiceLoader;

public final class NetworkProvider {
  private static NetworkProvider INSTANCE;

  /**
   * Gets the singleton.
   * 
   * @return the singleton
   */
  public static NetworkProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new NetworkProvider();
    }
    return INSTANCE;
  }

  private final ServiceLoader<NetworkService> loader;

  private NetworkProvider() {
    loader = ServiceLoader.load(NetworkService.class);
  }

  /**
   * Searches and returns a builder that supports the specified dyad type.
   * 
   * @param type
   *          the dyad type.
   * @return a builder if a network service supports that dyad type.
   * @throws IllegalStateException
   *           if no network service for this dyad type is registered.
   */
  public NetworkBuilder builder(final DyadType type) {
    for (final NetworkService s : loader) {
      if (s.supports(type)) {
        return s.createBuilder(type);
      }
    }
    throw new IllegalStateException("no network service for dyad type: " + type);
  }
}
