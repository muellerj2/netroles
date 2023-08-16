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
package ch.ethz.sn.visone3.io.impl;

import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.networks.Network;

import java.util.Collections;
import java.util.Map;

/**
 * Simple implementation of the {@link SourceFormat} class.
 */
public class SourceFormatImpl implements SourceFormat {
  private final Network incidence;
  private final Map<String, Mapping<?>> monadic;
  private final Map<String, Mapping<?>> dyadic;
  private final Map<?, Integer> nodeIds;

  /**
   * Constructs a network data container.
   * 
   * @param incidence
   *          the indicence structure.
   * @param monadic
   *          the monadic data.
   * @param dyadic
   *          the dyadic data.
   * @param nodeIds
   *          the mapping from node ids to the indices used in other mappings and the incidence
   *          structure.
   */
  public SourceFormatImpl(final Network incidence, final Map<String, Mapping<?>> monadic,
      final Map<String, Mapping<?>> dyadic, Map<?, Integer> nodeIds) {
    this.incidence = incidence;
    this.monadic = monadic;
    this.dyadic = dyadic;
    this.nodeIds = nodeIds;
  }

  @Override
  public Map<String, Mapping<?>> monadic() {
    return Collections.unmodifiableMap(monadic);
  }

  @Override
  public Map<String, Mapping<?>> dyadic() {
    return Collections.unmodifiableMap(dyadic);
  }

  @Override
  public Network incidence() {
    return incidence;
  }

  @Override
  public Map<?, Integer> nodeIds() {
    return nodeIds;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("incidence: ").append(incidence).append('\n');
    for (final Map.Entry<String, Mapping<?>> e : monadic.entrySet()) {
      sb.append(e.toString()).append('\n');
    }
    for (final Map.Entry<String, Mapping<?>> e : dyadic.entrySet()) {
      sb.append(e.toString()).append('\n');
    }
    return sb.toString();
  }
}
