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

import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// Handles the GraphMLTags
class GraphmlHandler extends NestedHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GraphmlHandler.class);
  private final Map<String, Integer> nodeIds = new HashMap<>();
  final PrimitiveList.OfInt sources = Mappings.newIntList();
  final PrimitiveList.OfInt targets = Mappings.newIntList();
  private final Map<String, UnknownKey> unknownKeys = new HashMap<>();
  final Map<String, Key<?>> keys = new HashMap<>();
  private final LongMap<Integer> edgeIds = PrimitiveContainers.longHashMap();
  private final Key<Integer> nested = new Key<>(GraphmlSource.NESTED,
    GraphmlSource.NESTED, ElementType.NODE, DataType.INT, -1);
  private final Key<Integer> multiCount = new Key<>(GraphmlSource.MULTIPLICITY,
    GraphmlSource.MULTIPLICITY, ElementType.EDGE, DataType.INT, 1);
  private boolean globalDirected = false;

  GraphmlHandler() {
    super("  ");
    childs.put(GraphmlTokens.KEY, KeyHandler::new);
    childs.put(GraphmlTokens.GRAPH, GraphHandler::new);
    keys.put(nested.getId(), nested);
    keys.put(multiCount.getId(), multiCount);
  }

  /**
   * Returns the integer id of a node. If the node has no integer id assigned yet, a new one is
   * assigned.
   */
  private int nodeId(final String id) {
    return nodeIds.computeIfAbsent(id, (newId) -> nodeIds.size()).intValue();
  }

  public Map<String, Integer> getNodeIds() {
    return nodeIds;
  }

  /**
   * Switch from undirected to directed mode.
   * Copy all the non-loop undirected edges.
   *
   * @param numUndirected number of undirected edges.
   */
  private void makeDirected(final int numUndirected) {
    // pad all edge keys to the current number of edges
    for (final UnknownKey key : unknownKeys.values()) {
      if (key.getElmntType() == ElementType.EDGE) {
        key.growToSize(numUndirected);
      }
    }
    for (final Key<?> key : keys.values()) {
      if (key.getElmntType() == ElementType.EDGE) {
        key.growToSize(numUndirected);
      }
    }
    for (int i = 0; i < numUndirected; i++) {
      // get edge flipped
      final int source = targets.getInt(i);
      final int target = sources.getInt(i);
      final long hash = IntPair.tuple(source, target);
      if (source == target) {
        // TODO multiply loop on direction change?
      } else if (edgeIds.contains(hash)) {
        // this happens only if the previous undirected edge was added without sorted endpoints
        throw new IllegalStateException("hash already contained");
      } else {
        final int id = sources.size();
        sources.addInt(source);
        targets.addInt(target);
        edgeIds.put(hash, id);
        // copy the data entries of all edge keys
        for (final UnknownKey key : unknownKeys.values()) {
          if (key.getElmntType() == ElementType.EDGE) {
            key.set(id, key.values.get(i));
          }
        }
        for (final Key<?> key : keys.values()) {
          if (key.getElmntType() == ElementType.EDGE) {
            transferKeyValue(key, i, id);
          }
        }
      }
    }
  }
  
  private static <T> void transferKeyValue(Key<T> key, int source, int target) {
    key.set(target, key.values.get(source));
  }

  @Override
  public void endDocument() {
    if (!unknownKeys.isEmpty()) {
      LOG.warn("keys used but never declared: {}", unknownKeys.keySet());
    }
    // kill empty keys (declared ALL but never used)
    for (final String k : new HashSet<>(keys.keySet())) {
      if (keys.get(k).values.isEmpty()) {
        keys.remove(k);
        LOG.warn("key never used but declared: {}", k);
      }
    }
  }

  @Override
  public String toString() {
    return "<graphml>";
  }

  public boolean isGlobalDirected() {
    return globalDirected;
  }

  static final class UnknownKey {
    private final String id;
    private final ElementType elmntType;
    private final PrimitiveList<String> values;

    UnknownKey(final String id, final ElementType elmntType) {
      this.id = id;
      this.elmntType = elmntType;
      values = Mappings.newList(String.class);
    }

    <T> Key<T> declared(final String name, final DataType<T> type, final T def) {
      final Key<T> key = new Key<>(id, name, elmntType, type, def);
      for (int i = 0; i < values.size(); i++) {
        key.setFromString(i, values.get(i));
      }
      return key;
    }

    public void set(final int id, final String value) {
      growToSize(id + 1);
      values.set(id, value);
    }

    void growToSize(int size) {
      if (size > values.size()) {
        values.setSize(null, size);
      }
    }

    ElementType getElmntType() {
      return elmntType;
    }
  }

  static final class Key<T> {
    private final String id;
    private final String name;
    private final ElementType elmntType;
    private final DataType<T> dataType;
    private final T defaultValue;
    private final PrimitiveList<T> values;

    Key(
      final String id, final String name, final ElementType elmntType, final DataType<T> dataType,
      final T defaultValue
    ) {
      if (elmntType == ElementType.ALL) {
        throw new IllegalArgumentException("split ALL before key creation");
      }
      this.id = id;
      this.name = name;
      this.elmntType = elmntType;
      this.dataType = dataType;
      this.defaultValue = defaultValue;
      values = Mappings.newList(dataType.getComponentType());
    }

    public void set(final int index, final T value) {
      growToSize(index + 1);
      values.set(index, value);
      // System.out.printf("set %s_%d to %s [%s]%n", name, index, value, values);
    }

    public void setFromString(final int index, final String value) {
      set(index, value != null ? dataType.convert(value) : defaultValue);
    }

    public void growToSize(int size) {
      if (size > values.size()) {
        values.setSize(defaultValue, size);
      }
    }

    public ElementType getElmntType() {
      return elmntType;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    /**
     * Export the final mapping. Only after parsing all the final size is known.
     *
     * @param size Mapping size.
     * @return Mapping.
     */
    public Mapping<T> mapping(final int size) {
      values.setSize(defaultValue, size);
      return values;
    }

    @Override
    public String toString() {
      return String.format("{%s,%s,on=%s,data=%s,%s}", id, name, elmntType, dataType, values);
    }
  }

  public class KeyHandler extends NestedHandler {
    private boolean valid;
    private String id;
    private String name;
    private String defaultValue;
    private ElementType elmntType;
    private DataType<?> dataType;

    KeyHandler(final NestedHandler parent) {
      super(GraphmlTokens.KEY, parent);
    }

    @Override
    public void init(
      final String uri, final String localName, final String qName, final Attributes attributes
    ) {
      name = attributes.getValue("", GraphmlTokens.ATTR_NAME);
      final String type = attributes.getValue("", GraphmlTokens.ATTR_TYPE);
      defaultValue = attributes.getValue("", GraphmlTokens.ATTR_DEFAULT);
      valid = name != null && type != null;
      if (valid) {
        elmntType = ElementType.valueOf(attributes.getValue("", GraphmlTokens.FOR).toUpperCase());
        id = attributes.getValue("", GraphmlTokens.ID);
        dataType = DataType.getByGraphMlName(type);
      }
    }

    @Override
    public void exit(final String uri, final String localName, final String qName) {
      if (valid) {
        if (elmntType == ElementType.ALL) {
          for (final ElementType e : ElementType.values()) {
            if (e != ElementType.ALL) {
              declareKey(e.name() + id, e);
            }
          }
        } else {
          declareKey(elmntType.name() + id, elmntType);
        }
      }
    }

    private <T> void declareKeyInternal(final String id, final ElementType elmntType,
        DataType<T> dataType) {
      T defaultVal = defaultValue != null ? dataType.convert(defaultValue) : null;
      if (unknownKeys.containsKey(id)) {
        final UnknownKey oldKey = unknownKeys.remove(id);
        if (oldKey.elmntType != elmntType) {
          throw new IllegalStateException(String.format("id %s: old type %s vs. new type %s",
            id, oldKey.elmntType, elmntType));
        }
        keys.put(id, oldKey.declared(name, dataType, defaultVal));
      } else {
        keys.put(id, new Key<>(id, name, elmntType, dataType, defaultVal));
      }
    }
    
    private void declareKey(final String id, final ElementType elmntType) {
      declareKeyInternal(id, elmntType, dataType);
    }

    @Override
    public String toString() {
      return "<key," + id + "," + name + "," + valid + ">";
    }
  }

  public class DataHandler extends NestedHandler {
    private final StringBuilder sb = new StringBuilder();
    private String key;

    DataHandler(final NestedHandler parent) {
      super(GraphmlTokens.DATA, parent);
    }

    @Override
    public void init(
      final String uri, final String localName, final String qName, final Attributes attributes
    ) {
      key = attributes.getValue("", GraphmlTokens.KEY);
      sb.setLength(0);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
      sb.append(ch, start, length);
    }

    @Override
    public void exit(final String uri, final String localName, final String qName) {
      if (parent instanceof NodeHandler) {
        final NodeHandler p = (NodeHandler) parent;
        setData(p.id, ElementType.NODE, key);
      } else if (parent instanceof EdgeHandler) {
        final EdgeHandler p = (EdgeHandler) parent;
        for (final int id : p.ids) {
          setData(id, ElementType.EDGE, key);
        }
      } else if (parent instanceof GraphHandler) {
        setData(0, ElementType.GRAPH, key); // TODO overwrite data?
      } else {
        throw new IllegalStateException();
      }
    }

    private void setData(final int id, final ElementType elmntType, final String key) {
      String value = sb.toString().trim();
      if (value.isEmpty()) {
        value = null;
      }
      final Key<?> k = keys.get(elmntType.name() + key);
      if (k != null) {
        k.setFromString(id, value);
      } else {
        final UnknownKey u = unknownKeys.computeIfAbsent(elmntType.name() + key,
          (newKey) -> new UnknownKey(newKey, elmntType));
        u.set(id, value);
      }
    }

    @Override
    public String toString() {
      return "<data," + key + ">";
    }
  }

  public class GraphHandler extends NestedHandler {
    private int id;

    GraphHandler(final NestedHandler parent) {
      super(GraphmlTokens.GRAPH, parent);
      childs.put(GraphmlTokens.NODE, NodeHandler::new);
      childs.put(GraphmlTokens.EDGE, EdgeHandler::new);
      childs.put(GraphmlTokens.KEY, KeyHandler::new);
      childs.put(GraphmlTokens.DATA, DataHandler::new);
    }

    @Override
    public void init(
      final String uri, final String localName, final String qName, final Attributes attributes
    ) {
      final boolean graphDir = GraphmlTokens.DIRECTED.equals(
        attributes.getValue("", GraphmlTokens.EDGEDEFAULT));
      if (graphDir && !globalDirected) {
        makeDirected(sources.size());
        globalDirected = true;
      }
      if (parent instanceof NodeHandler) {
        id = ((NodeHandler) parent).id;
      } else {
        id = -1;
      }
    }

    @Override
    public void exit(final String uri, final String localName, final String qName) {
    }

    @Override
    public String toString() {
      return "<graph," + id + ">";
    }
  }

  public class NodeHandler extends NestedHandler {
    private String idString;
    private int id;

    NodeHandler(final NestedHandler parent) {
      super(GraphmlTokens.NODE, parent);
      childs.put(GraphmlTokens.GRAPH, GraphHandler::new);
      childs.put(GraphmlTokens.DATA, DataHandler::new);
    }

    @Override
    public void init(
      final String uri, final String localName, final String qName, final Attributes attributes
    ) {
      idString = attributes.getValue("", GraphmlTokens.ID);
      id = nodeId(idString);
    }

    @Override
    public void exit(final String uri, final String localName, final String qName) {
      nested.set(id, ((GraphHandler) parent).id);
      // System.out.println(nested);
      // System.out.println(nodeIds);
    }

    @Override
    public String toString() {
      return "<node," + id + ">";
    }
  }

  public class EdgeHandler extends NestedHandler {
    private String idString;
    private boolean directed;
    private int[] ids;
    private int source;
    private int target;

    EdgeHandler(final NestedHandler parent) {
      super(GraphmlTokens.EDGE, parent);
      childs.put(GraphmlTokens.DATA, DataHandler::new);
    }

    @Override
    public void init(
      final String uri, final String localName, final String qName, final Attributes attributes
    ) {
      idString = attributes.getValue("", GraphmlTokens.ID);
      final String edgeDirectedString = attributes.getValue("", GraphmlTokens.DIRECTED);
      if (edgeDirectedString == null) {
        directed = globalDirected;
      } else {
        directed = Boolean.parseBoolean(edgeDirectedString);
      }

      if (directed && !globalDirected) {
        makeDirected(sources.size());
        globalDirected = true;
      }

      source = nodeId(attributes.getValue("", GraphmlTokens.SOURCE));
      target = nodeId(attributes.getValue("", GraphmlTokens.TARGET));
      if (!globalDirected && !directed) {
        // undirected mode
        if (source > target) {
          final int tmp = source;
          source = target;
          target = tmp;
        }
        // all undirected
        ids = new int[] {addEdge(source, target)};
        // System.out.println(source + "," + target + " ---> " + Arrays.toString(ids));
      } else if (globalDirected && !directed) {
        if (source != target) {
          ids = new int[] {addEdge(source, target), addEdge(target, source)};
          // System.out.println(source + "," + target + " -->- " + Arrays.toString(ids));
        } else {
          ids = new int[] {addEdge(source, target)};
          // System.out.println(source + "," + target + " -->- " + Arrays.toString(ids));
        }
      } else if (globalDirected && directed) {
        ids = new int[] {addEdge(source, target)};
        // System.out.println(source + "," + target + " -->> " + Arrays.toString(ids));
      } else {
        ids = null;
      }
      // System.out.println(this);
    }

    private int addEdge(final int source, final int target) {
      final long hash = IntPair.tuple(source, target);
      final int existing = edgeIds.getOrDefault(hash, -1);
      if (existing >= 0) {
        multiCount.growToSize(existing + 1);
        multiCount.set(existing, multiCount.values.get(existing) + 1);
        return existing;
      } else {
        final int id = sources.size();
        edgeIds.put(hash, id);
        multiCount.set(id, 1);
        sources.addInt(source);
        targets.addInt(target);
        return id;
      }
    }

    @Override
    public String toString() {
      return String.format("<edge,%d,%d,%s,%s>", source, target, directed, Arrays.toString(ids));
    }
  }
}
