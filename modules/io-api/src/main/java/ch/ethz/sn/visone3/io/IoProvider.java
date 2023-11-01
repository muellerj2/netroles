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

package ch.ethz.sn.visone3.io;

import java.util.ServiceLoader;

/**
 * Provides registered IO services.
 *
 * @implNote The default implementation of the IO package provides services for
 *           GraphML (file type "graphml"), node and edge list CSV (file
 *           type "nodelist.csv" and "edgelist.csv") as well as a JSON-based
 *           format (file type "json").
 */
public final class IoProvider {
  private static IoProvider INSTANCE;
  private final ServiceLoader<IoService> loader;

  /**
   * Gets the singleton object of the provider.
   * 
   * @return the singleton
   */
  public static IoProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new IoProvider();
    }
    return INSTANCE;
  }

  private IoProvider() {
    loader = ServiceLoader.load(IoService.class);
  }

  /**
   * Searches a service supporting the specified file type.
   * 
   * @param fileType the file type.
   * @return the service supporting the file type.
   * @throws UnsupportedOperationException if no service supporting this file type
   *                                       is registered.
   */
  public static synchronized IoService getService(final String fileType) {
    for (final IoService io : getInstance().loader) {
      // LOG.info("check {} for {}", io.getClass().getSimpleName(), fileType);
      if (io.supportFileType(fileType)) {
        return io;
      }
    }
    throw new UnsupportedOperationException(
        String.format("no %s found for: %s", IoService.class.getSimpleName(), fileType));
  }
}
