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
package ch.ethz.sn.visone3.roles.test.impl.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.impl.algorithms.BipartiteMatching;

import org.junit.jupiter.api.Test;

public class BipartiteMatchingTest {

  public Mapping<Edge> checkMatching(int n, Mapping<Edge> edges) {
    boolean[] visited = new boolean[n];
    for (Edge e : edges) {
      if (visited[e.getSource()] || visited[e.getTarget()]) {
        fail(edges.toString() + " is not a matching");
      }
      visited[e.getSource()] = true;
      visited[e.getTarget()] = true;
    }
    return edges;
  }

  @Test
  public void testBipartiteMatching() {
    assertEquals(5, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork().asUndirectedGraph(), Mappings.intRange(0, 5))).size());
    assertEquals(5, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork().asUndirectedGraph(), Mappings.intRange(5, 10))).size());
    assertEquals(5, BipartiteMatching.maximumMatchingSize(createNetwork().asUndirectedGraph(),
        Mappings.intRange(0, 5)));

    assertEquals(4, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork2().asUndirectedGraph(), Mappings.intRange(0, 5))).size());
    assertEquals(4, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork2().asUndirectedGraph(), Mappings.intRange(5, 10))).size());
    assertEquals(4, BipartiteMatching.maximumMatchingSize(createNetwork2().asUndirectedGraph(),
        Mappings.intRange(0, 5)));

    assertEquals(4, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork3().asUndirectedGraph(), Mappings.intRange(0, 6))).size());
    assertEquals(4, checkMatching(10, BipartiteMatching
        .maximumMatching(createNetwork3().asUndirectedGraph(), Mappings.intRange(6, 10))).size());

    assertEquals(4,
        checkMatching(10, BipartiteMatching.maximumMatching(createNetwork4().asUndirectedGraph(),
            Mappings.wrapUnmodifiableInt(0, 1, 2, 4, 5, 6))).size());
    assertEquals(4,
        checkMatching(10, BipartiteMatching.maximumMatching(createNetwork4().asUndirectedGraph(),
            Mappings.wrapUnmodifiableInt(3, 7, 8, 9))).size());
  }

  public Network createNetwork() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { 1, z, 1, z, z, z }, //
        { 1, z, z, 1, z, z, z }, //
        { 1, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, z, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }

  public Network createNetwork2() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { 1, z, 1, z, z, z }, //
        { 1, z, 1, z, z, z, z }, //
        { 1, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, z, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }

  public Network createNetwork3() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { z, z, z, z, z, z }, //
        { 1, z, 1, z, z, z, z }, //
        { z, 1, z, 1, z, 1, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }

  public Network createNetwork4() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { 1, z, 1, z }, //
        { z, z, z, z, z }, //
        { z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z }, //
        { z, 1, z, z, z, 1, 1, z }, //
        { 1, z, 1, z, 1, z, z, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }
}
