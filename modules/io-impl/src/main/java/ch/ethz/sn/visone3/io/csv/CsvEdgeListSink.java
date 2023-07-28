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

package ch.ethz.sn.visone3.io.csv;

import ch.ethz.sn.visone3.io.AbstractSink;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CsvEdgeListSink extends AbstractSink implements AutoCloseable {
  public static final String SOURCE = "source";
  public static final String TARGET = "target";
  private final BufferedWriter out;
  private final Map<String, ConstMapping<?>> dyadic;
  private final char delimiter;
  private Network network;

  public CsvEdgeListSink(final OutputStream out, final char delimiter) {
    this.delimiter = delimiter;
    this.out = new BufferedWriter(new OutputStreamWriter(out));
    dyadic = new LinkedHashMap<>();
  }

  @Override
  public void incidence(final Network network) {
    this.network = Objects.requireNonNull(network);
  }

  @Override
  public <T> void link(final String name, final T def, final ConstMapping<T> dyadic) {
    // always dense, default ignored
    this.dyadic.put(name, dyadic);
  }

  @Override
  public void close() throws IOException {
    try {
      out.append(cell(SOURCE)).append(delimiter).append(cell(TARGET));
      for (final String name : dyadic.keySet()) {
        out.append(delimiter).append(cell(name));
      }
      out.append("\n");
      final Iterable<? extends Edge> edges = network.isDirected()
        ? network.asDirectedGraph().getEdges()
        : network.asUndirectedGraph().getEdges();
      for (final Edge e : edges) {
        out.append(cell(String.valueOf(e.getSource()))).append(delimiter);
        out.append(cell(String.valueOf(e.getTarget())));
        for (final ConstMapping<?> map : dyadic.values()) {
          out.append(delimiter).append(cell(String.valueOf(map.get(e.getIndex()))));
        }
        out.append("\n");
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  private String cell(final String value) {
    return value;
  }
}
