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

import java.lang.reflect.Array;
import java.util.Arrays;

final class LongTreeMap<T> implements LongMap<T> {
  private static final int DUMP_WIDTH = 3;
  private static final String DUMP_SPACE;
  private static final String DUMP_LINE;

  static {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < DUMP_WIDTH; i++) {
      sb.append(" ");
    }
    for (int i = 0; i < 3; i++) {
      sb.append(sb.toString());
    }
    DUMP_SPACE = sb.toString();
    DUMP_LINE = DUMP_SPACE.replace(' ', '-');
  }

  private final int minDegree;
  private final int maxDegree;
  private Node root;
  private int size;

  public LongTreeMap() {
    this(64);
  }

  public LongTreeMap(final int minDegree) {
    this.minDegree = minDegree;
    maxDegree = minDegree << 1;
    root = new Node();
  }

  @Override
  public T put(final long key, final T value) {
    Node node = root;
    int idx = node.find(key);
    while (canDecend(key, node, idx)) {
      node = node.nodes[idx];
      idx = node.find(key);
    }
    if (idx < node.size && node.keys[idx] == key) {
      T oldValue = node.values[idx];
      node.values[idx] = value;
      return oldValue;
    }
    size++;
    node.add(key, value, null);
    return null;
  }

  private boolean canDecend(final long key, final Node node, final int idx) {
    return ((idx < node.size && node.keys[idx] != key) || idx == node.size) && node.nodes != null;
  }

  @Override
  public T get(final long key) {
    Node node = root;
    int idx = node.find(key);
    while (canDecend(key, node, idx)) {
      node = node.nodes[idx];
      idx = node.find(key);
    }
    // System.out.println(Arrays.toString(Arrays.stream(node.keys).limit(node.size).toArray()));
    // System.out.println(Arrays.toString(Arrays.stream(node.values).limit(node.size).toArray()));
    if (idx < node.size && node.keys[idx] == key) {
      return node.values[idx];
    }
    return null;
  }

  @Override
  public T getOrDefault(final long key, final T def) {
    Node node = root;
    int idx = node.find(key);
    while (canDecend(key, node, idx)) {
      node = node.nodes[idx];
      idx = node.find(key);
    }
    if (idx < node.size && node.keys[idx] == key) {
      return node.values[idx];
    }
    return def;
  }

  @Override
  public T remove(final long key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(final long key) {
    Node node = root;
    int idx = node.find(key);
    while (canDecend(key, node, idx)) {
      node = node.nodes[idx];
      idx = node.find(key);
    }
    return idx < node.size && node.keys[idx] == key;
  }

  @Override
  public int size() {
    return size;
  }

  public void dump(final StringBuilder sb) {
    dump(sb, root, 0);
  }

  private void dump(final StringBuilder sb, final Node node, final int indent) {
    final String ind = DUMP_SPACE.substring(0, (DUMP_WIDTH + 1) * indent)
        + ((node.parent == null) ? ">>>" : dumpNodeHash(node.parent)) + "-" + dumpNodeHash(node)
        + DUMP_LINE.substring((DUMP_WIDTH + 1) * indent);
    for (int i = 0; i < node.size; i++) {
      if (node.nodes != null) {
        dump(sb, node.nodes[i], indent + 1);
      }
      sb.append(String.format("%-10s (%d=%s)%n", ind, node.keys[i], node.values[i]));
    }
    if (node.nodes != null) {
      dump(sb, node.nodes[node.size], indent + 1);
    }
  }

  private String dumpNodeHash(final Node node) {
    return Integer.toString(node.hashCode(), Character.MAX_RADIX).substring(0, DUMP_WIDTH);
  }

  class Node {
    private final long[] keys = new long[maxDegree];
    @SuppressWarnings("unchecked")
    private final T[] values = (T[]) new Object[maxDegree];
    private Node parent;
    private int size = 0;
    private Node[] nodes;

    void add(final long key, final T value, final Node node) {
      final int i = find(key);
      System.arraycopy(keys, i, keys, i + 1, size - i);
      System.arraycopy(values, i, values, i + 1, size - i);
      keys[i] = key;
      values[i] = value;
      if (nodes != null && node != null) {
        System.arraycopy(nodes, i + 1, nodes, i + 2, size - i);
        nodes[i + 1] = node;
      }
      ++size;
      if (size == maxDegree) {
        split();
      }
    }

    int find(final long key) {
      int idx;
      for (idx = 0; idx < size; idx++) {
        if (keys[idx] >= key) {
          return idx;
        }
      }
      return idx;
    }

    @SuppressWarnings("unchecked")
    private void split() {
      final Node right = new Node();
      if (this == root) {
        root = new Node();
        parent = root;
        root.nodes = (Node[]) Array.newInstance(Node.class, maxDegree + 1);
        root.nodes[0] = this;
      }
      right.parent = parent;
      size = minDegree - 1;
      right.size = minDegree;
      System.arraycopy(keys, minDegree, right.keys, 0, minDegree);
      System.arraycopy(values, minDegree, right.values, 0, minDegree);
      Arrays.fill(keys, minDegree, maxDegree, 0);
      Arrays.fill(values, minDegree, maxDegree, null);
      if (nodes != null) {
        right.nodes = (Node[]) Array.newInstance(Node.class, maxDegree + 1);
        System.arraycopy(nodes, minDegree, right.nodes, 0, minDegree + 1);
        Arrays.fill(nodes, minDegree, maxDegree + 1, null);
        for (int i = 0; i <= right.size; i++) {
          right.nodes[i].parent = right;
        }
      }
      parent.add(keys[minDegree - 1], values[minDegree - 1], right);
    }
  }
}
