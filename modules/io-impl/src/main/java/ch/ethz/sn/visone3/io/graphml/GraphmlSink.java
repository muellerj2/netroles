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

package ch.ethz.sn.visone3.io.graphml;

import ch.ethz.sn.visone3.io.AbstractSink;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Networks;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Writes a network as GraphML file using the standards described by the GraphML Primer:
 * (http://graphml.graphdrawing.org/primer/graphml-primer.html)
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;graphml ...&gt;
 *  &lt;key ...&gt;
 *    &lt;default &gt;
 *    ...
 *    &lt;default .../&gt;
 *  &lt;key/&gt;
 *   ...
 *   &lt;graph id="..." edgedefault="..." ...&gt;
 *     &lt;node id="..."&gt;
 *       &lt;data key="..."&gt;...&lt;/data&gt;
 *       ...
 *     &lt;/node&gt;
 *     ...
 *     &lt;edge id="..." source="..." target="..."&gt;
 *       &lt;data key="..."&gt;...&lt;/data&gt;
 *       ...
 *     &lt;/edge&gt;
 *     ...
 *     &lt;data key="..."&gt;...&lt;/data&gt;
 *     ...
 *   &lt;/graph&gt;
 *   &lt;data key="..."&gt;...&lt;/data&gt;
 *   ...
 * &lt;/graphml&gt;
 * </pre>
 */

public class GraphmlSink extends AbstractSink implements AutoCloseable {
  public static final String PREFIX_NODE = "n";
  public static final String PREFIX_EDGE = "e";
  public static final String PREFIX_KEY = "k";

  public enum Hint {
    PARSE_INFO
  }

  private final OutputStream out;
  private final Document doc;
  private final Map<String, Object> graphDefault;
  private final Map<String, Object> nodeDefault;
  private final Map<String, Object> edgeDefault;
  private final Map<String, ConstMapping<?>> edgeAttr;
  private final Map<String, ConstMapping<?>> nodeAttr;
  /**
   * Mapping global, monadic and dyadic mapping names to unique data keys.
   */
  private final Map<String, String> keyToId = new LinkedHashMap<>();
  private final UnaryOperator<String> keyIdGenerator = (name) -> PREFIX_KEY + keyToId.size();
  private boolean writeParseInfo = false;
  private Network graph;

  public GraphmlSink(final OutputStream out) throws ParserConfigurationException {
    this.out = out;
    graphDefault = new LinkedHashMap<>();
    nodeDefault = new LinkedHashMap<>();
    edgeDefault = new LinkedHashMap<>();
    edgeAttr = new LinkedHashMap<>();
    nodeAttr = new LinkedHashMap<>();
    final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    doc = docBuilder.newDocument();
  }

  @Override
  public void hint(final String key, final String value) {
    final Hint hint = Hint.valueOf(key);
    switch (hint) {
      case PARSE_INFO:
        writeParseInfo = Boolean.parseBoolean(value);
        break;
      default:
        throw new IllegalArgumentException("unknown hint " + key);
    }
  }

  // public void setWriteParseInfo(final boolean writeParseInfo) {
  //   this.writeParseInfo = writeParseInfo;
  // }
  //
  // public void setKeyIdGenerator(final UnaryOperator<String> keyIdGenerator) {
  //   this.keyIdGenerator = keyIdGenerator;
  // }

  @Override
  public void close() throws IOException {
    try {
      // check attribute dimensions
      Objects.requireNonNull(graph);
      for (final Map.Entry<String, ConstMapping<?>> e : nodeAttr.entrySet()) {
        Networks.requireVertexMapping(graph, e.getValue());
      }
      for (final Map.Entry<String, ConstMapping<?>> e : edgeAttr.entrySet()) {
        Networks.requireLinkMapping(graph, e.getValue());
      }

      write();
      // write the content into xml file
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      //initialize StreamResult with File object to save to file
      final StreamResult result = new StreamResult(out);
      final DOMSource source = new DOMSource(doc);
      transformer.transform(source, result);
    } catch (final TransformerException te) {
      throw new IOException(te);
    } finally {
      out.flush();
      out.close();
    }
  }

  private void write() {
    final Element root = writeHead(doc);
    // writeKeysForDefaults(root);
    writeGlobal(root);
    writeKeys(root, GraphmlTokens.EDGE, edgeDefault, edgeAttr);
    writeKeys(root, GraphmlTokens.NODE, nodeDefault, nodeAttr);
    // graphstream (http://graphstream-project.org/) chokes on parse info
    final Element graphElmnt = writeGraph(root);
    writeNodes(graphElmnt);
    writeEdges(graphElmnt);
  }

  private void writeEdges(final Element graphElmnt) {
    int index = 0;
    Element edge;
    final Iterable<Edge> rel = graph.isDirected()
      ? graph.asDirectedGraph().getEdges()
      : graph.asUndirectedGraph().getEdges();

    for (final Edge r : rel) {
      edge = graphElmnt.getOwnerDocument().createElement(GraphmlTokens.EDGE);
      graphElmnt.appendChild(edge);
      edge.setAttribute(GraphmlTokens.ID, PREFIX_EDGE + index);
      edge.setAttribute(GraphmlTokens.SOURCE, PREFIX_NODE + r.getSource());
      edge.setAttribute(GraphmlTokens.TARGET, PREFIX_NODE + r.getTarget());
      writeDataFor(edge, GraphmlTokens.EDGE, edgeDefault, edgeAttr, index);
      index++;
    }
  }

  private void writeNodes(final Element graphElmnt) {
    final int n = graph.asRelation().countUnionDomain();
    Element node;
    for (int i = 0; i < n; i++) {
      node = graphElmnt.getOwnerDocument().createElement(GraphmlTokens.NODE);
      graphElmnt.appendChild(node);
      node.setAttribute(GraphmlTokens.ID, PREFIX_NODE + i);
      writeDataFor(node, GraphmlTokens.NODE, nodeDefault, nodeAttr, i);
    }
  }

  /**
   * Writes attribute data.
   * @param token The parent token type.
   * @param index Index of the parent element.
   */
  private void writeDataFor(
    final Element elem, final String token, final Map<String, Object> def,
    final Map<String, ConstMapping<?>> map, final int index
  ) {
    for (final Map.Entry<String, ConstMapping<?>> e : map.entrySet()) {
      final Object defValue = def.get(e.getKey());
      final Object value = e.getValue().get(index);
      // write if it exists and is not the default value
      if (value != null && (!def.containsKey(e.getKey()) || !Objects.equals(value, defValue))) {
        final Element data = doc.createElement(GraphmlTokens.DATA);
        elem.appendChild(data);
        data.setAttribute(GraphmlTokens.KEY, keyToId.get(e.getKey() + token));
        data.appendChild(doc.createTextNode(value.toString()));
      }
    }
  }

  /**
   * Writes the graph tag with the corresponding data.
   */
  private Element writeGraph(final Element parent) {
    final Element graphElmnt = parent.getOwnerDocument().createElement(GraphmlTokens.GRAPH);
    parent.appendChild(graphElmnt);

    final Attr attr = parent.getOwnerDocument().createAttribute(GraphmlTokens.EDGEDEFAULT);
    if (graph.isDirected()) {
      attr.setValue(GraphmlTokens.DIRECTED);
    } else {
      attr.setValue(GraphmlTokens.UNDIRECTED);
    }
    graphElmnt.setAttributeNode(attr);

    // parse.nodes and parse.edges
    if (writeParseInfo) {
      final int n = graph.asRelation().countUnionDomain();
      final int m = graph.countDyadicIndices();
      graphElmnt.setAttribute(GraphmlTokens.PARSE_NODES, String.valueOf(n));
      graphElmnt.setAttribute(GraphmlTokens.PARSE_EDGES, String.valueOf(m));
      //parse.nodeids parse.edgeids
      graphElmnt.setAttribute(GraphmlTokens.PARSE_EDGEIDS, GraphmlTokens.CANONICAL);
      graphElmnt.setAttribute(GraphmlTokens.PARSE_NODEIDS, GraphmlTokens.CANONICAL);
      //parse.order
      graphElmnt.setAttribute(GraphmlTokens.PARSE_ORDER, GraphmlTokens.NODESFIRST);
    }
    return graphElmnt;
  }

  /**
   * Graphml root element.
   */
  private Element writeHead(final Document parent) {
    final Element root = parent.createElement(GraphmlTokens.GRAPHML);
    parent.appendChild(root);
    root.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
    root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    root.setAttribute("xsi:schemaLocation",
      "http://graphml.graphdrawing.org/xmlns "
        + "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    return root;
  }

  private void writeGlobal(final Element parent) {
    for (final Map.Entry<String, Object> e : graphDefault.entrySet()) {
      final Element key = doc.createElement(GraphmlTokens.KEY);
      parent.appendChild(key);
      final Class<?> ct = e.getValue().getClass();
      key.setAttribute(GraphmlTokens.ATTR_TYPE, classToToken(ct));
      key.setAttribute(GraphmlTokens.ID,
        keyToId.computeIfAbsent(e.getKey() + GraphmlTokens.GRAPH, keyIdGenerator));
      key.setAttribute(GraphmlTokens.FOR, GraphmlTokens.GRAPH);
      key.setAttribute(GraphmlTokens.ATTR_NAME, e.getKey());
      final Element def = doc.createElement(GraphmlTokens.DEFAULT);
      key.appendChild(def);
      def.appendChild(doc.createTextNode(e.getValue().toString()));
    }
  }

  /**
   * Write the data key definitions.
   */
  private void writeKeys(
    final Element parent, final String token, final Map<String, Object> def,
    final Map<String, ConstMapping<?>> map
  ) {
    // union over keys
    final Set<String> keys = new LinkedHashSet<>();
    keys.addAll(def.keySet());
    keys.addAll(map.keySet());

    for (final String ek : keys) {
      final Element key = doc.createElement(GraphmlTokens.KEY);
      parent.appendChild(key);
      key.setAttribute(GraphmlTokens.ID, keyToId.computeIfAbsent(ek + token, keyIdGenerator));
      key.setAttribute(GraphmlTokens.FOR, token);
      key.setAttribute(GraphmlTokens.ATTR_NAME, ek);

      if (def.containsKey(ek)) {
        final Class<?> ct = def.get(ek).getClass();
        key.setAttribute(GraphmlTokens.ATTR_TYPE, classToToken(ct));
        // put fix default values in key tags.
        final Element defaultValue = doc.createElement(GraphmlTokens.DEFAULT);
        key.appendChild(defaultValue);
        final Object val = def.get(ek);
        defaultValue.appendChild(doc.createTextNode(val.toString()));
      } else {
        final Class<?> ct = map.get(ek).getComponentType();
        key.setAttribute(GraphmlTokens.ATTR_TYPE, classToToken(ct));
      }
    }
  }

  @Override
  public void global(final String name, final Object value) {
    graphDefault.put(name, value);
  }

  @Override
  public void incidence(final Network network) {
    graph = network;
  }

  @Override
  public <T> void node(final String name, final T def, final ConstMapping<T> monadic) {
    if (def != null) {
      nodeDefault.put(name, def);
    } else {
      nodeDefault.remove(name);
    }
    if (monadic != null) {
      nodeAttr.put(name, monadic);
    } else {
      nodeAttr.remove(name);
    }
  }

  @Override
  public <T> void link(final String name, final T def, final ConstMapping<T> dyadic) {
    if (def != null) {
      edgeDefault.put(name, def);
    } else {
      edgeDefault.remove(name);
    }
    if (dyadic != null) {
      edgeAttr.put(name, dyadic);
    } else {
      edgeAttr.remove(name);
    }
  }

  private String classToToken(final Class<?> componentType) {
    if (componentType == Integer.class || componentType == int.class) {
      return GraphmlTokens.INT;
    }
    if (componentType == String.class) {
      return GraphmlTokens.STRING;
    }
    if (componentType == Double.class || componentType == double.class) {
      return GraphmlTokens.DOUBLE;
    }
    return GraphmlTokens.STRING;
  }
}
