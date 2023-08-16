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

package ch.ethz.sn.visone3.io.csv;

import ch.ethz.sn.visone3.io.AbstractSink;
import ch.ethz.sn.visone3.io.impl.IoStreams;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Networks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CSV edge list sink. First column represents the nodes via their associated
 * integer indices.
 */
public class CsvNodeListSink extends AbstractSink implements AutoCloseable {
  /**
   * The name of the node column.
   */
  public static final String NODE = "node";
  private final BufferedWriter out;
  private final Map<String, ConstMapping<?>> monadic;
  private final char delimiter;

  /**
   * Constructs the sink.
   * 
   * @param out       the stream to output to.
   * @param delimiter the delimiter used in the CSV.
   */
  public CsvNodeListSink(final OutputStream out, final char delimiter) {
    this.delimiter = delimiter;
    this.out = IoStreams.writer(out);
    monadic = new LinkedHashMap<>();
  }

  @Override
  public <T> void node(final String name, final T def, final ConstMapping<T> monadic) {
    // default ignored, is always dense storage
    if (monadic != null) {
      this.monadic.put(name, monadic);
    } else {
      this.monadic.remove(name);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      out.append(cell(NODE));
      final int n = monadic.isEmpty() ? 0 : monadic.values().iterator().next().size();
      for (final Map.Entry<String, ConstMapping<?>> e : monadic.entrySet()) {
        Networks.checkVertexMapSize(n, e.getValue(), e.getKey());
        out.append(delimiter).append(cell(e.getKey()));
      }
      out.append("\n");
      for (int i = 0; i < n; i++) {
        out.append(cell(String.valueOf(i)));
        for (final ConstMapping<?> map : monadic.values()) {
          out.append(delimiter).append(cell(String.valueOf(map.get(i))));
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
