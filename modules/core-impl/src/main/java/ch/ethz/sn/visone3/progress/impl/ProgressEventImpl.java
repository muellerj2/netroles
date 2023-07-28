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

package ch.ethz.sn.visone3.progress.impl;

import ch.ethz.sn.visone3.progress.ProgressEvent;
import ch.ethz.sn.visone3.progress.ProgressSource;

import java.util.List;

/**
 * Default implementation of {@link ProgressEvent}.
 */
public class ProgressEventImpl implements ProgressEvent {
  private final long timestamp;
  private final ProgressMonitorImpl monitor;
  private final ProgressSource active;

  /**
   * Constructs a new progress event.
   * 
   * @param timestamp
   *          event time stamp.
   * @param monitor
   *          the progress monitor.
   * @param active
   *          the triggering progress source.
   */
  public ProgressEventImpl(final long timestamp, final ProgressMonitorImpl monitor,
      final ProgressSource active) {
    this.timestamp = timestamp;
    this.monitor = monitor;
    this.active = active;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public List<ProgressSource> getSources() {
    return monitor.getSources();
  }

  @Override
  public ProgressSource getActive() {
    return active;
  }
}
