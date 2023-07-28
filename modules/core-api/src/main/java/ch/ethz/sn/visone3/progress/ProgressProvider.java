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

package ch.ethz.sn.visone3.progress;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Provides an interface to the installed progress monitor.
 */
public final class ProgressProvider {
  private static ProgressProvider INSTANCE;
  private final ServiceLoader<ProgressMonitor> sources = ServiceLoader.load(ProgressMonitor.class);
  private ProgressMonitor monitor;

  /**
   * Gets the singleton instance of the provider.
   * 
   * @return the provider
   */
  public static ProgressProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ProgressProvider();
    }
    return INSTANCE;
  }

  /**
   * Gets the installed progress monitor.
   * 
   * @return the monitor
   */
  public static ProgressMonitor getMonitor() {
    return getInstance().monitor;
  }

  ProgressProvider() {
    rescan();
  }

  void rescan() {
    sources.reload();
    final Iterator<ProgressMonitor> monitors = sources.iterator();
    // what does the user want?
    final String want = Props.load(ProgressProvider.class).getProperty("monitor");
    monitor = ProgressMonitor.NULL;
    if (want == null) {
      // unspecified, take first
      monitor = monitors.hasNext() ? monitors.next() : ProgressMonitor.NULL;
    } else if (!"null".equals(want)) {
      // specified by class name, search
      while (monitors.hasNext()) {
        final ProgressMonitor m = monitors.next();
        if (m.getClass().getCanonicalName().equals(want)) {
          monitor = m;
          break;
        }
      }
    }
    // else specified as "null"
  }
}
