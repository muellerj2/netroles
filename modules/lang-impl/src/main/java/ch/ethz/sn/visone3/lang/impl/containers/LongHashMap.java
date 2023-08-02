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

package ch.ethz.sn.visone3.lang.impl.containers;

import ch.ethz.sn.visone3.lang.LongMap;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Hash map with quadratic probing. The probing scheme ensures every position is probed if the size
 * is a power of 2.
 */
final class LongHashMap<T> implements LongMap<T> {
  @SuppressWarnings("unused")
  private static final byte FREE = 0b0_00; // 0
  private static final byte DIRT = 0b0_10; // 2
  private static final byte FULL = 0b0_11; // 3
  private static final byte MASK_INSERT = 0b0_01;
  private static final byte MASK_SEARCH = 0b0_10;
  private final float minLoad;
  private final float maxLoad;
  private long[] keys;
  private T[] values;
  private byte[] state;
  private int size;

  public LongHashMap() {
    this(1 << 4);
  }

  public LongHashMap(final int size) {
    this(size, .4f, .9f);
  }

  /**
   * Constructs a new long hash map with initial size/capacity.
   * 
   * @param size
   *          Initial size.
   */
  @SuppressWarnings("unchecked")
  public LongHashMap(final int size, final float minLoad, final float maxLoad) {
    this.minLoad = minLoad;
    this.maxLoad = maxLoad;
    keys = new long[size];
    values = (T[]) new Object[size];
    state = new byte[size];
  }

  @SuppressWarnings("unchecked")
  private void rehash(final int size) {
    final long[] keys = this.keys;
    final T[] values = this.values;
    final byte[] state = this.state;
    this.keys = new long[size];
    this.values = (T[]) new Object[size];
    this.state = new byte[size];
    this.size = 0;
    for (int i = 0; i < keys.length; i++) {
      if (state[i] == FULL) {
        put(keys[i], values[i]);
      }
    }
  }

  @Override
  public T put(final long key, final T value) {
    if (size >= (int) (maxLoad * keys.length)) {
      rehash(keys.length << 1);
    }
    // search the value, a FREE or a DIRT position
    final int i = search(key, MASK_INSERT);
    if (i >= 0 && state[i] != FULL) {
      keys[i] = key;
      values[i] = value;
      state[i] = FULL;
      ++size;
      return null;
    } else if (i < 0 || keys[i] != key) {
      throw new IllegalStateException("probing failed to find position " + this);
    } else {
      T oldValue = values[i];
      values[i] = value;
      return oldValue;
    }
  }

  @Override
  public T get(final long key) {
    // search the value or a FREE position
    final int i = search(key, MASK_SEARCH);
    if (i >= 0 && state[i] == FULL) {
      return values[i];
    }
    return null;
  }

  @Override
  public T getOrDefault(final long key, final T def) {
    // search the value or a FREE position
    final int i = search(key, MASK_SEARCH);
    if (i >= 0 && state[i] == FULL) {
      return values[i];
    }
    return def;
  }

  @Override
  public T remove(final long key) {
    // search the value or a FREE position
    final int i = search(key, MASK_SEARCH);
    if (i >= 0 && state[i] == FULL) {
      final T val = values[i];
      values[i] = null;
      state[i] = DIRT;
      --size;
      if (size < (int) (minLoad * keys.length)) {
        rehash(keys.length >>> 1);
      }
      return val;
    }
    return null;
  }

  @Override
  public boolean contains(final long key) {
    // search the key or a FREE position
    final int i = search(key, MASK_SEARCH);
    return i >= 0 && state[i] == FULL;
  }

  /**
   * Search the index for {@code value}.
   *
   * @return The first index in the probing sequence where either {@code value} is located or the
   *         masked state is 0.
   */
  protected int search(final long value, final byte mask) {
    int h = ((int) value >>> 1) % keys.length;
    int i = 0;
    while (keys[h] != value && (state[h] & mask) != 0 && i < keys.length) {
      ++i;
      h = (h + i) % keys.length;
    }
    if (i >= keys.length) {
      return -1;
    }
    return h;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return IntStream.range(0, keys.length).mapToObj(i -> "{" + keys[i] + "|" + state[i] + "}") //
        .collect(Collectors.joining(","));
  }
}
