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

import ch.ethz.sn.visone3.networks.impl.Format;
import ch.ethz.sn.visone3.progress.ProgressEvent;
import ch.ethz.sn.visone3.progress.ProgressListener;
import ch.ethz.sn.visone3.progress.ProgressMonitor;
import ch.ethz.sn.visone3.progress.ProgressSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default {@link ProgressMonitor} implementation.
 */
public final class ProgressMonitorImpl implements ProgressMonitor {
  private static final Logger LOG = LoggerFactory.getLogger(ProgressMonitorImpl.class);
  private static final long MIN_INTERVAL = 1_000; // ms
  private final Object lock = new Object();
  private final List<ProgressSource> sources = new ArrayList<>();
  private final List<ProgressListener> listeners = new ArrayList<>();
  // number of missed event due to time restriction
  private int hits;
  // timestamp since last message
  private long tsLastUpdate;

  /**
   * Default listener. Spawned if nothing else is registered.
   */
  private final ProgressListener def = event -> {
    System.out.print(mem());
    for (ProgressSource s : event.getSources()) {
      System.out.print(" " + source(event.getTimestamp(), s));
    }
    System.out.println();
  };

  private String mem() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    final Runtime rt = Runtime.getRuntime();
    sb.append(Format.formatBytes("%3.0f%-2s", rt.totalMemory() - rt.freeMemory()));
    sb.append(Format.formatBytes("+%3.0f%-2s", rt.freeMemory()));
    sb.append(String.format(", %5.0e", (double) hits));
    sb.append("]");
    return sb.toString();
  }

  private String source(final long timestamp, final ProgressSource source) {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    final long elapsed = timestamp - source.getTimeStampCreate();
    if (source.getExpected() >= 0) {
      // percent
      final double done = source.getLast() * 100d / source.getExpected();
      sb.append(String.format("%5.1f%%", done));
      // time till finish
      if (10 < elapsed && 0.1 <= done) {
        final long remaining = (long) ((100d - done) * (elapsed / done));
        sb.append(Format.formatMillis(", %6.1f%4s", -remaining));
      } else {
        sb.append(String.format(", %10s", "..."));
      }
    } else {
      sb.append(String.format("%,10d", source.getLast()));
      sb.append(Format.formatMillis(",  %5.1f%4s", elapsed));
    }
    sb.append(',');
    final String name = source.getMessage();
    sb.append(String.format("%16s", name.substring(Math.max(0, name.length() - 16))));
    sb.append("]");
    return sb.toString();
  }

  @Override
  public ProgressSource newSource() {
    final ProgressSource source = new ProgressSourceImpl(this);
    synchronized (lock) {
      if (listeners.isEmpty()) {
        LOG.warn("adding default listener");
        addListener(def);
      }
      sources.add(source);
    }
    return source;
  }

  @Override
  public List<ProgressSource> getSources() {
    return Collections.unmodifiableList(sources);
  }

  void delete(final ProgressSource source) {
    synchronized (lock) {
      sources.remove(source);
    }
  }

  @Override
  public void addListener(final ProgressListener listener) {
    synchronized (lock) {
      listeners.add(listener);
    }
  }

  @Override
  public void removeListener(final ProgressListener listener) {
    synchronized (lock) {
      listeners.remove(listener);
    }
  }

  void fire(final ProgressSource src) {
    final long timeStamp = System.currentTimeMillis();
    if (timeStamp - tsLastUpdate >= MIN_INTERVAL || src.getLast() == src.getExpected()) {
      final ProgressEvent ev = new ProgressEventImpl(timeStamp, this, src);
      synchronized (lock) {
        for (final ProgressListener l : listeners) {
          l.onProgress(ev);
        }
      }
      tsLastUpdate = timeStamp;
      hits = 0;
    } else {
      hits++;
    }
  }
}
