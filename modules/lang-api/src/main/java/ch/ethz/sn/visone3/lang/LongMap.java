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

package ch.ethz.sn.visone3.lang;

/**
 * A map from long keys to arbitrary values.
 * 
 * @param <T>
 *          value type
 */
public interface LongMap<T> {

  /**
   * Sets the value for the given key.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the old value assigned to the key, or null if no prior value had been assigned.
   */
  T put(long key, T value);

  /**
   * Returns the value at the key, or null if no value is assigned.
   * 
   * @param key
   *          the key
   * @return the value at the key, or null if no value has been assigned to the key
   */
  T get(long key);

  /**
   * Returns the value at the key, or the default alue if no value is assigned.
   * 
   * @param key
   *          the key
   * @return the value at the key, or the default if no value has been assigned to the key
   */
  T getOrDefault(long key, T def);

  /**
   * Removes the value at the key.
   * 
   * @param key
   *          the key
   * @return the removed value at the key
   */
  T remove(long key);

  /**
   * Returns true if the map contains a mapping for the key.
   * 
   * @param key
   *          the key
   * @return true if the map contains a mapping for the key, false otherwise
   */
  boolean contains(long key);

  /**
   * Returns the number of mapped keys.
   * 
   * @return the number of mapped keys
   */
  int size();
}
