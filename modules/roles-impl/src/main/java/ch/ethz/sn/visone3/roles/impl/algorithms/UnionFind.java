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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import java.util.Arrays;

/**
 * UnionFind on consecutive integers from 0 to n. That way no hashing is required.
 * <p>
 * <ul>
 * <li>Find: Determine which subset a particular element is in. This can be used for determining if
 * two elements are in the same subset.</li>
 * <li>Union: Join two subsets into a single subset.</li>
 * </ul>
 */
public class UnionFind {
  private final int[] parentMap;
  private final int[] rankMap;
  private int groups;

  /**
   * Create an empty union find data structure with isolated sets.
   */
  public UnionFind(final int universeSize) {
    parentMap = new int[universeSize];
    rankMap = new int[universeSize];
    for (int i = 0; i < universeSize; i++) {
      parentMap[i] = i;
    }
    Arrays.fill(rankMap, 0);
    groups = universeSize;
  }

  public int getGroups() {
    return groups;
  }

  /**
   * Return parent for {@code element}.
   */
  public int find(final int element) {
    if (element >= parentMap.length) {
      return -1;
    }
    final int parent = parentMap[element];
    if (parent == element) {
      return element;
    }

    final int newParent = find(parent);
    parentMap[element] = newParent;
    return newParent;
  }

  /**
   * merge components containing {@code element1} and {@code element2}.
   */
  public void union(final int element1, final int element2) {
    final int parent1 = find(element1);
    final int parent2 = find(element2);

    // check if the elements are already in the same set
    if (parent1 == parent2) {
      return;
    }

    groups--;
    final int rank1 = rankMap[parent1];
    final int rank2 = rankMap[parent2];
    if (rank1 > rank2) {
      parentMap[parent2] = parent1;
    } else if (rank1 < rank2) {
      parentMap[parent1] = parent2;
    } else {
      parentMap[parent2] = parent1;
      rankMap[parent1] = rank1 + 1;
    }
  }
}
