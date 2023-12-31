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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Helper class to hold id mappings.
 */
public class IdMapper<T> {
  private static final Logger LOG = LoggerFactory.getLogger(IdMapper.class);
  private final Map<T, Integer> ids;
  private final Function<T, Integer> generator;
  private final Class<T> componentType;
  int generated = 0;

  /**
   * Returns a mapper constructed from the index -&gt; id relationship specified
   * by the provided mapping.
   * 
   * @param ids the underlying mapping.
   * @param <T> the id type.
   * @return the id mapper.
   */
  public static <T> IdMapper<T> fixed(final ConstMapping<T> ids) {
    return fixed(ids.getComponentType(), inverseMap(ids));
  }

  /**
   * Returns a mapper constructed from the index -&gt; id relationship specified
   * by the provided map.
   * 
   * @param <T>  the id type.
   * @param type the class object describing the id type.
   * @param ids  the underlying map.
   * @return the id mapper.
   */
  public static <T> IdMapper<T> fixed(Class<T> type, final Map<T, Integer> ids) {
    final int max = ids.values().stream().mapToInt(Integer::intValue).distinct().max().orElse(0);
    if (max >= ids.size()) {
      throw new IllegalArgumentException("maximum id exceeds size");
    }
    return new IdMapper<>(type, ids, (id) -> null);
  }

  /**
   * Returns a mapper representing the identity mapping index -&gt; index.
   * @return the id mapper.
   */
  public static IdMapper<Integer> identity() {
    return new IdMapper<>(Integer.class, new HashMap<>(), (id) -> id);
  }

  /**
   * Returns a mapper sequentially assigning new indices for new id values.
   * 
   * @param componentType the class object representing the id type.
   * @param <T>           the id type.
   * @return the id mapper.
   */
  public static <T> IdMapper<T> continous(final Class<T> componentType) {
    final Map<T, Integer> ids = new HashMap<>();
    return new IdMapper<>(componentType, ids, (id) -> ids.size());
  }

  private IdMapper(
    final Class<T> componentType, final Map<T, Integer> ids, final Function<T, Integer> generator
  ) {
    this.componentType = componentType;
    this.ids = ids;
    this.generator = generator;
  }

  /**
   * Gets the type for ids.
   * @return the id type.
   */
  public Class<T> getComponentType() {
    return componentType;
  }

  /**
   * Create the inverse mapping, warn if not ambiguous.
   *
   * @param source Integer to object mapping.
   * @return Object to integer mapping.
   */
  private static <T> Map<T, Integer> inverseMap(final ConstMapping<T> source) {
    final Map<T, Integer> reverse = new HashMap<>();
    for (int i = 0; i < source.size(); i++) {
      final T key = source.get(i);
      if (!reverse.containsKey(key)) {
        reverse.put(key, i);
      } else {
        reverse.put(key, null);
        LOG.warn("ambiguous reverse mapping: '" + source.get(i) + "'");
      }
    }
    return reverse;
  }

  /**
   * Returns the number of assigned indices.
   * @return the number of assigned indices.
   */
  public int size() {
    return ids.size();
  }

  /**
   * Returns all id to index assignments.
   * @return set of all id to index assignments.
   */
  public Set<Map.Entry<T, Integer>> entrySet() {
    return ids.entrySet();
  }

  /**
   * Exports the gathered ids to a node mapping.
   *
   * @param offset  Where to start filling.
   * @param mapping Id map.
   */
  public void fillMapping(final int offset, final Mapping<T> mapping) {
    for (final Map.Entry<T, Integer> e : ids.entrySet()) {
      mapping.set(offset + e.getValue().intValue(), e.getKey());
    }
  }

  /**
   * Returns the map from ids to indices.
   * @return the map.
   */
  public Map<T, Integer> getMapping() {
    return ids;
  }

  /**
   * Returns an index assigned to the specified id.
   * @param key the id.
   * @return the index assigned to this id.
   */
  public int map(final T key) {
    if (key == null) {
      // input is invalid
      return -1;
    }
    Integer id = ids.get(key);
    if (id == null) {
      // not present, try to generate
      id = generator.apply(key);
      if (id == null) {
        // generator returned null, cannot merge with existing
        return -1;
      }
      ids.put(key, id);
      generated++;
    }
    return id.intValue();
  }
}
