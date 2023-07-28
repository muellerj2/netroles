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

package ch.ethz.sn.visone3.io.json;

import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads JSON graphs.
 *
 * <pre>
 * {
 *   "type": ""
 *   "nodes": [
 *     {...}
 *   ],
 *   "edges": [
 *     {"source": id, "target": id, ...}
 *   ]
 * }
 * </pre>
 */
public class JsonSink implements Sink {
  private final BufferedWriter out;
  private final Map<String, ConstMapping<?>> monadic;
  private final Map<String, ConstMapping<?>> dyadic;
  private Network incidence;
  private boolean open = true;

  /**
   * Constructs a new sink writing JSON to the output stream.
   * 
   * @param out
   *          the output stream
   * @throws IOException
   *           if an I/O error occurs
   */
  public JsonSink(final OutputStream out) throws IOException {
    this.out = new BufferedWriter(new OutputStreamWriter(out));
    monadic = new HashMap<>();
    dyadic = new HashMap<>();
  }

  @Override
  public void incidence(final Network network) {
    this.incidence = network;
  }

  @Override
  public void global(final String name, final Object value) {

  }

  @Override
  public <T> void node(final String name, final T def, final ConstMapping<T> monadic) {
    if (monadic == null) {
      this.monadic.remove(name);
    } else {
      this.monadic.put(name, monadic);
    }
  }

  @Override
  public <T> void link(final String name, final T def, final ConstMapping<T> dyadic) {
    if (dyadic == null) {
      this.dyadic.remove(name);
    } else {
      this.dyadic.put(name, dyadic);
    }
  }

  @Override
  public void close() throws IOException {
    if (open) {
      final int n = incidence.asRelation().countUnionDomain();
      final JSONArray nodes = new JSONArray();
      for (int i = 0; i < n; i++) {
        nodes.put(writeNode(i));
      }
      final JSONArray links = new JSONArray();
      final Iterable<Edge> itr = (incidence.isDirected() ? incidence.asDirectedGraph().getEdges()
          : incidence.asUndirectedGraph().getEdges());
      // int index = 0;
      for (final Edge e : itr) {
        // edges might not be ordered in their index order, that a problem?
        // if (e.getIndex() != index++) {
        // throw new IllegalStateException("storage order");
        // }
        links.put(writeEdge(e));
      }
      final JSONObject graph = new JSONObject();
      graph.put(JsonSource.TYPE, (incidence.isDirected() ? DyadType.DIRECTED
          : (incidence.isTwoMode() ? DyadType.TWO_MODE : DyadType.UNDIRECTED)).name());
      graph.put("nodes", nodes);
      graph.put("links", links);
      out.write(graph.toString());
      out.flush();
      out.close();
    }
    open = false;
  }

  private JSONObject writeNode(final int index) throws IOException {
    final JSONObject node = new JSONObject();
    node.put("id", index);
    for (final Map.Entry<String, ConstMapping<?>> e : monadic.entrySet()) {
      node.put(e.getKey(), e.getValue().get(index));
    }
    return node;
  }

  private JSONObject writeEdge(final Edge eg) throws IOException {
    final JSONObject edge = new JSONObject();
    edge.put("target", eg.getTarget());
    edge.put("source", eg.getSource());
    for (final Map.Entry<String, ConstMapping<?>> e : dyadic.entrySet()) {
      edge.put(e.getKey(), e.getValue().get(eg));
    }
    return edge;
  }
}
