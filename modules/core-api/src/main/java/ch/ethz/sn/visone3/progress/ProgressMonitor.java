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

import java.util.Collections;
import java.util.List;

/**
 * Manages the progress sources and listeners, directing events from sources to the listeners.
 */
public interface ProgressMonitor {
  /**
   * Progress monitor that does nothing.
   */
  ProgressMonitor NULL = new ProgressMonitor() {

    @Override
    public ProgressSource newSource() {
      return ProgressSource.NULL;
    }

    @Override
    public List<ProgressSource> getSources() {
      return Collections.emptyList();
    }

    @Override
    public void addListener(final ProgressListener listener) {
    }

    @Override
    public void removeListener(final ProgressListener listener) {
    }
  };

  /**
   * Returns a new progress source for the purpose of reporting progress.
   * 
   * @return the new progress source
   */
  ProgressSource newSource();

  /**
   * Lists the currently registered, non-closed sources.
   * 
   * @return the sources.
   */
  List<ProgressSource> getSources();

  /**
   * Registers a listener on progress updates.
   * 
   * @param listener
   *          listener.
   */
  void addListener(ProgressListener listener);

  /**
   * Unregisters a listener from progress updates.
   * 
   * @param listener
   *          listener.
   */
  void removeListener(ProgressListener listener);
}
