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

import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.impl.IdMapper;
import ch.ethz.sn.visone3.io.impl.RangedList;
import ch.ethz.sn.visone3.io.impl.SourceFormatImpl;
import ch.ethz.sn.visone3.lang.ClassUtils;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
public class JsonSource implements Source<String> {
  private static final Logger LOG = LoggerFactory.getLogger(JsonSource.class);
  static final String TYPE = "type"; // dyad type

  public enum Hint {
    // input key names
    ID, NODES, EDGES, SOURCE, TARGET
  }

  /**
   * Column names (hint values).
   */
  private final String[] names = new String[Hint.values().length];
  private final JSONObject root;
  private final Map<String, Mapping<?>> monadic;
  private final Map<String, Mapping<?>> dyadic;
  private IdMapper<?> nodeIds;
  private NetworkBuilder builder;
  private Network incidence;

  /**
   * Constructs a new JSON network source.
   * 
   * @param in
   *          the input stream
   * @throws IOException
   *           if some I/O error occurs
   */
  public JsonSource(final InputStream in) throws IOException {
    try (Reader r = new InputStreamReader(in)) {
      final JSONTokener tokener = new JSONTokener(r);
      root = new JSONObject(tokener);
    }
    monadic = new HashMap<>();
    dyadic = new HashMap<>();
    autoDetect();
  }

  @Override
  public boolean isAutoconfig() {
    return true;
  }

  /**
   * Look at some object keys to figure out how stuff is named.
   */
  private void autoDetect() {
    hint(Hint.ID.name(), "id");
    for (final String key : root.keySet()) {
      switch (key) {
        case "nodes":
        case "vertices":
          hint(Hint.NODES.name(), key);
          break;
        case "links":
        case "edges":
          hint(Hint.EDGES.name(), key);
          break;
        default:
          // ignore, hint not valid for this object
          break;
      }
    }
    if (key(Hint.EDGES) == null) {
      return;
    }
    final JSONArray edges = root.getJSONArray(key(Hint.EDGES));
    if (edges.length() == 0) {
      return;
    }
    final JSONObject firstEdge = edges.getJSONObject(0);
    for (final String key : firstEdge.keySet()) {
      switch (key) {
        case "from":
        case "source":
          hint(Hint.SOURCE.name(), key);
          break;
        case "to":
        case "target":
          hint(Hint.TARGET.name(), key);
          break;
        default:
          // ignore, hint not valid for this object
          break;
      }
    }
  }

  @Override
  public void hint(final String key, final Object value) {
    final Hint hint = Hint.valueOf(key);
    names[hint.ordinal()] = (String) value;
  }

  private static <T> Mapping<T> copyIdMapping(IdMapper<T> idMapper) {
    Class<T> cls = idMapper.getComponentType();
    Mapping<T> ids = Mappings.newListOfSizeAutoboxing(ClassUtils.unwrap(cls), idMapper.size());
    idMapper.fillMapping(0, ids);
    return ids;
  }

  @Override
  public SourceFormat parse() throws IOException {
    // set directionality
    DyadType dt = DyadType.DIRECTED;
    if (root.has(TYPE)) {
      dt = DyadType.valueOf(root.getString(TYPE).toUpperCase());
    }
    builder = NetworkProvider.getInstance().builder(dt);
    LOG.info("json hints: {}", Arrays.toString(names));
    // parse the graph
    parseVertices(root);
    parseEdges(root);
    // verify
    if (!monadic.containsKey(names[Hint.ID.ordinal()])) {
      monadic.put(names[Hint.ID.ordinal()], copyIdMapping(nodeIds));
    }
    // for (final Mapping m : monadic.values()) {
    // Networks.requireVertexMapping(incidence, m);
    // }
    // for (final Mapping m : dyadic.values()) {
    // Networks.requireLinkMapping(incidence, m);
    // }
    return new SourceFormatImpl(incidence, monadic, dyadic, nodeIds.getMapping());
  }

  @Override
  public void close() {
  }

  /**
   * From R names.
   */
  private static Source.Range<?> componentType(final String name) {
    switch (name) {
      case "double":
        return Source.Range.DOUBLE;
      case "integer":
        return Source.Range.INT;
      case "character":
        return Source.Range.STRING;
      default:
        throw new IllegalStateException("unknown type for " + name);
    }
  }

  /**
   * Create mappings for each attribute by finding the most specific type for each attribute.
   * 
   * @param map
   *          a map to store the attribute mappings e.g. monadic or dyadic maps.
   * @param attributeList
   *          list of attributes for all nodes/edges.
   */
  private void createAttributeMappings(Map<String, Mapping<?>> map,
      List<Map<String, Object>> attributeList) {
    Map<String, Class<?>> attributeTypes = new HashMap<>();
    Map<String, Integer> attributeCounts = new HashMap<>();
    for (Map<String, Object> attrs : attributeList) {
      for (final Map.Entry<String, Object> e : attrs.entrySet()) {
        Class<?> common = e.getValue().getClass();
        if (common == BigDecimal.class) {
          common = Double.class;
        }
        if (attributeTypes.containsKey(e.getKey())) {
          common = findClosestCommonSuper(attributeTypes.get(e.getKey()), common);
          attributeCounts.put(e.getKey(), attributeCounts.get(e.getKey()) + 1);
        } else {
          attributeCounts.put(e.getKey(), 1);
        }
        attributeTypes.put(e.getKey(), common);
      }
    }
    for (Map.Entry<String, Class<?>> entry : attributeTypes.entrySet()) {
      Class<?> cls = entry.getValue();
      if (attributeCounts.get(entry.getKey()) == attributeList.size()) {
        // the attribute was present in all items, so we can use the primitive type
        cls = ClassUtils.unwrap(cls);
      }
      map.put(entry.getKey(), Mappings.newListOfSizeAutoboxing(cls, attributeList.size()));
    }
  }

  private static <T> void setMapAt(Mapping<T> mapping, ObjectMapper mapper, int pos, Object value) {
    mapping.set(pos, mapper.convertValue(value, mapping.getComponentType()));
  }

  private void fillAttributeMappings(Map<String, Mapping<?>> map,
      List<Map<String, Object>> attributeList) {
    ObjectMapper mapper = new ObjectMapper();
    for (int i = 0; i < attributeList.size(); i++) {
      Map<String, Object> attrs = attributeList.get(i);
      for (final Map.Entry<String, Object> e : attrs.entrySet()) {
        final Mapping<?> mapping = map.get(e.getKey());
        setMapAt(mapping, mapper, i, e.getValue());
      }
    }
  }

  private void parseVertices(final JSONObject root) {
    if (root.has(names[Hint.NODES.ordinal()])) {
      final JSONArray nodes = root.getJSONArray(names[Hint.NODES.ordinal()]);
      final int n = nodes.length();
      List<Map<String, Object>> attributeList = new ArrayList<>(nodes.length());
      for (int i = 0; i < nodes.length(); i++) {
        attributeList.add(nodes.getJSONObject(i).toMap());
      }
      if (root.has("node_types")) { // used in R client
        final Map<String, Object> nt = root.getJSONObject("node_types").toMap();
        for (final Map.Entry<String, Object> e : nt.entrySet()) {
          final Range<?> to = componentType((String) e.getValue());
          monadic.put(e.getKey(), new RangedList<>(to, n).getList());
        }
      } else {
        createAttributeMappings(monadic, attributeList);
      }
      fillAttributeMappings(monadic, attributeList);
      // parse all node attributes
      for (int i = 0; i < nodes.length(); i++) {
        builder.ensureNode(i);
      }
      // create id mapper
      final Mapping<?> ids = monadic.get(names[Hint.ID.ordinal()]);
      if (ids == null) {
        LOG.info("no {} attribute, mapping array position to id", names[Hint.ID.ordinal()]);
        nodeIds = IdMapper.identity();
      } else {
        LOG.info("using fixed id pool: {}", ids);
        nodeIds = IdMapper.fixed(ids);
      }
    } else {
      LOG.info("no {} object, mapping continuously to id", names[Hint.NODES.ordinal()]);
      nodeIds = IdMapper.continous(Object.class);
    }
  }

  private String key(Hint hint) {
    return names[hint.ordinal()];
  }

  static Class<?> findClosestCommonSuper(Class<?> first, Class<?> second) {
    if (!Number.class.isAssignableFrom(first) || !Number.class.isAssignableFrom(second)) {
      return Object.class;
    }
    boolean firstIsInteger = first == Integer.class || first == Short.class || first == Byte.class;
    boolean secondIsInteger = second == Integer.class || second == Short.class
        || second == Byte.class;
    if (firstIsInteger && secondIsInteger) {
      return Integer.class;
    } else if ((firstIsInteger || first == Long.class)
        && (secondIsInteger || second == Long.class)) {
      return Long.class;
    } else {
      return Double.class;
    }
  }

  private static <T> int mapKeyToIndex(IdMapper<T> idMapper, ObjectMapper converter, Object key) {
    return idMapper.map(converter.convertValue(key, idMapper.getComponentType()));
  }

  private void parseEdges(final JSONObject root) {
    if (!root.has(key(Hint.EDGES))) {
      throw new IllegalStateException(String.format("no such key: %s, top level keys are: %s",
          key(Hint.EDGES), String.join(", ", root.keySet())));
    }
    final JSONArray edges = root.getJSONArray(key(Hint.EDGES));
    final String nf = key(Hint.SOURCE);
    final String nt = key(Hint.TARGET);
    ObjectMapper mapper = new ObjectMapper();
    if (edges.length() > 0) {
      // parse all edges
      int maxEdgeId = 0;
      List<Map<String, Object>> attributeList = new ArrayList<>(
          Collections.nCopies(edges.length(), null));
      for (int i = 0; i < edges.length(); i++) {
        final JSONObject iJson = edges.getJSONObject(i);
        // add structure
        final int source = mapKeyToIndex(nodeIds, mapper, iJson.get(nf));
        final int target = mapKeyToIndex(nodeIds, mapper, iJson.get(nt));
        // LOG.info("{} {} -> {} {}", iJson.get(nf), iJson.get(nt), source, target);
        int edgeId = builder.addEdge(source, target);
        if (edgeId < 0) {
          throw new IllegalStateException("No support for multigraph and multiedges");
        }
        if (edgeId > maxEdgeId) {
          maxEdgeId = edgeId;
        }
        // add edge values
        final Map<String, Object> iMap = iJson.toMap();
        iMap.remove(nf);
        iMap.remove(nt);
        attributeList.set(edgeId, iMap);
      }
      createAttributeMappings(dyadic, attributeList);
      fillAttributeMappings(dyadic, attributeList);
      // some edges might have been dropped because of mapper/duplicates,
      // resize to actual number of edges
      for (final Map.Entry<String, Mapping<?>> e : dyadic.entrySet()) {
        final PrimitiveList<?> list = (PrimitiveList<?>) e.getValue();
        list.setSize(null, maxEdgeId + 1);
      }
    }
    incidence = builder.build();
  }
}
