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
package ch.ethz.sn.visone3.lang.spi;

import ch.ethz.sn.visone3.lang.IntDoubleHeap;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.LongSet;
import ch.ethz.sn.visone3.lang.PrimitiveQueue;

/**
 * Constructs requested containers.
 */
public interface ContainersFacade {

  /**
   * Creates a long map backed by hashing.
   * 
   * @return the empty map.
   */
  <T> LongMap<T> longHashMap();

  /**
   * Creates a long map backed by hashing.
   * 
   * @param initialCapacity
   *          initial capacity of the map.
   * @return the empty map.
   */
  <T> LongMap<T> longHashMap(int initialCapacity);

  /**
   * Creates a long map backed by a tree.
   * 
   * @return the empty map.
   */
  <T> LongMap<T> longTreeMap();

  /**
   * Creates a long map backed by a tree.
   * 
   * @param minDegree
   *          minimum degree of tree node after splitting.
   * @return the empty map.
   */
  <T> LongMap<T> longTreeMap(int minDegree);

  /**
   * Creates a long set backed by hashing.
   * 
   * @return the empty set.
   */
  LongSet longHashSet();

  /**
   * Creates a long set backed by a tree.
   * 
   * @return the empty set.
   */
  LongSet longTreeSet();

  /**
   * Creates an integer queue.
   * 
   * @return the empty queue.
   */
  PrimitiveQueue.OfInt intQueue();

  /**
   * Creates an int-double min heap for a fixed universe size.
   * 
   * @param size
   *          the universe size.
   * @return the empty min heap.
   */
  IntDoubleHeap fixedUniverseIntDoubleMinHeap(int size);
}
