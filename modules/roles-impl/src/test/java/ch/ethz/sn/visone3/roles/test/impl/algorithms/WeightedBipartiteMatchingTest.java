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
package ch.ethz.sn.visone3.roles.test.impl.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.UndirectedGraph;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.impl.algorithms.WeightedBipartiteMatching;

import org.junit.jupiter.api.Test;

public class WeightedBipartiteMatchingTest {

  public ConstMapping<Edge> checkMatching(int n, ConstMapping<Edge> edges) {
    boolean[] visited = new boolean[n];
    for (Edge e : edges) {
      assertFalse(visited[e.getSource()] || visited[e.getTarget()],
          edges.toString() + " is not a matching");
      visited[e.getSource()] = true;
      visited[e.getTarget()] = true;
    }
    return edges;
  }

  public void checkNetworkMatching(WeightedNetwork<Integer, Mapping.OfInt> network, int size,
      int weight, PrimitiveIterable.OfInt set) {
    UndirectedGraph graph = network.getNetwork().asUndirectedGraph();
    int n = graph.countVertices();
    Mapping.OfInt weights = network.getWeight();
    ConstMapping<Edge> matching = WeightedBipartiteMatching.maximumMatching(graph, weights, set);
    assertEquals(size, checkMatching(n, matching).size());
    assertEquals(weight,
        WeightedBipartiteMatching.maximumMatchingWeight(graph, weights, set));
    int totalWeight = 0;
    for (Edge e : matching) {
      totalWeight += weights.getInt(e.getIndex());
    }
    assertEquals(weight, totalWeight);
  }

  @Test
  public void testWeightedBipartiteMatching() {
    checkNetworkMatching(createNetwork(), 5, 5, Mappings.intRange(0, 5));
    checkNetworkMatching(createNetwork(), 5, 5, Mappings.intRange(5, 10));
    checkNetworkMatching(createNetworkWeighted(), 5, 10, Mappings.intRange(0, 5));
    checkNetworkMatching(createNetworkWeighted(), 5, 10, Mappings.intRange(5, 10));

    checkNetworkMatching(createNetwork2(), 4, 4, Mappings.intRange(0, 5));
    checkNetworkMatching(createNetwork2(), 4, 4, Mappings.intRange(5, 10));
    checkNetworkMatching(createNetwork2Weighted(), 4, 11, Mappings.intRange(0, 5));
    checkNetworkMatching(createNetwork2Weighted(), 4, 11, Mappings.intRange(5, 10));

    checkNetworkMatching(createNetwork3(), 4, 4, Mappings.intRange(0, 6));
    checkNetworkMatching(createNetwork3(), 4, 4, Mappings.intRange(6, 10));
    checkNetworkMatching(createNetwork3Weighted(), 4, 11, Mappings.intRange(0, 6));
    checkNetworkMatching(createNetwork3Weighted(), 4, 11, Mappings.intRange(6, 10));

    checkNetworkMatching(createNetwork4(), 4, 4, Mappings.wrapUnmodifiableInt(0, 1, 2, 4, 5, 6));
    checkNetworkMatching(createNetwork4(), 4, 4, Mappings.wrapUnmodifiableInt(3, 7, 8, 9));

  }
	
  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork() {

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
		PrimitiveList.OfInt weights = Mappings.newIntList();
		for (int i : s.getWeight()) {
		  weights.add(i);
		}
    return new WeightedNetwork<>(s.getNetwork(), weights);
	}

  public WeightedNetwork<Integer, Mapping.OfInt> createNetworkWeighted() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { 1, z, 3, z, z, z }, //
        { 1, z, z, 1, z, z, z }, //
        { 1, 4, z, 1, z, z, z, z }, //
        { 1, z, 1, z, 1, z, z, z, z }, //
        { 1, z, 2, z, z, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<>(s.getNetwork(), weights);
  }
  
  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork2() {

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
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<>(s.getNetwork(), weights);
	}
	

  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork2Weighted() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { 1, z, 1, z, z, z }, //
        { 4, z, 4, z, z, z, z }, //
        { 3, 2, z, 1, z, z, z, z }, //
        { 1, z, 4, z, 2, z, z, z, z }, //
        { 3, z, 1, z, z, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<>(s.getNetwork(), weights);
  }
  

  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork3() {

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
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<>(s.getNetwork(), weights);
	}

  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork3Weighted() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, z, z }, //
        { z, z, z, z, z }, //
        { z, z, z, z, z, z }, //
        { 3, z, 4, z, z, z, z }, //
        { z, 1, z, 1, z, 1, z, z }, //
        { 4, z, 3, z, 3, z, z, z, z }, //
        { 3, z, 3, z, 2, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<>(s.getNetwork(), weights);
  }
  

  public WeightedNetwork<Integer, Mapping.OfInt> createNetwork4() {

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
    PrimitiveList.OfInt weights = Mappings.newIntList();
    for (int i : s.getWeight()) {
      weights.add(i);
    }
    return new WeightedNetwork<Integer, Mapping.OfInt>(s.getNetwork(), weights);
	}
}
