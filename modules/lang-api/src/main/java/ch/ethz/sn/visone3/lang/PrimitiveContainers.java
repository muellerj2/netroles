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

package ch.ethz.sn.visone3.lang;

import ch.ethz.sn.visone3.lang.spi.ContainersFacade;
import ch.ethz.sn.visone3.lang.spi.LangProvider;

/**
 * Provides factory methods to produce some kinds of containers.
 */
public class PrimitiveContainers {

  private PrimitiveContainers() {
  }

  private static ContainersFacade facade() {
    return LangProvider.getInstance().containers();
  }

  /**
   * Creates a long map backed by hashing.
   * 
   * @param <T> type of mapped values.
   * @return the new and empty map.
   */
  public static <T> LongMap<T> longHashMap() {
    return facade().longHashMap();
  }

  /**
   * Creates a long map backed by hashing.
   * 
   * @param initialCapacity initial capacity of the map
   * @param <T>             type of mapped values.
   * @return the new and empty map.
   */
  public static <T> LongMap<T> longHashMap(int initialCapacity) {
    return facade().longHashMap(initialCapacity);
  }

  /**
   * Creates a long map backed by a tree.
   * 
   * @param <T> type of mapped values.
   * @return the new and empty map.
   */
  public static <T> LongMap<T> longTreeMap() {
    return facade().longTreeMap();
  }

  /**
   * Creates a long map backed by a tree.
   * 
   * @param minDegree minimum degree of tree node after splitting.
   * @param <T>       type of mapped values.
   * @return the new and empty map.
   */
  public static <T> LongMap<T> longTreeMap(int minDegree) {
    return facade().longTreeMap(minDegree);
  }

  /**
   * Creates a long set backed by hashing.
   * 
   * @return the new and empty set.
   */
  public static LongSet longHashSet() {
    return facade().longHashSet();
  }

  /**
   * Creates a long set backed by a tree.
   * 
   * @return the new and empty set.
   */
  public static LongSet longTreeSet() {
    return facade().longTreeSet();
  }

  /**
   * Creates an integer queue.
   * 
   * @return the new and empty queue.
   */
  public static PrimitiveQueue.OfInt intQueue() {
    return facade().intQueue();
  }

  /**
   * Creates an int-double min heap for a fixed universe size.
   * 
   * @param universeSize the universe size.
   * @return the new and empty min heap.
   */
  public static IntDoubleHeap fixedUniverseIntDoubleMinHeap(int universeSize) {
    return facade().fixedUniverseIntDoubleMinHeap(universeSize);
  }
}
