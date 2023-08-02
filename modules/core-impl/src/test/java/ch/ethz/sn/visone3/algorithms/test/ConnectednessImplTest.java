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

package ch.ethz.sn.visone3.algorithms.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.algorithms.Connectedness;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource.OfDouble;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.networks.impl.UndirectedNetworkImpl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ConnectednessImplTest {
  private static final Connectedness CONNECTEDNESS = AlgoProvider.getInstance().connectedness();
  private static final double z = Double.NaN;
  private static final double[][] adjUndirected = new double[][] { //
      { z }, //
      { 1, z }, //
      { z, z, z }, //
      { 1, z, z, z }, //
      { z, 1, z, z, z }, //
      { z, z, 0, z, z, z }, //
      { z, z, z, z, z, z, z }, //
      { z, z, z, z, z, 2, z, z }, //
      { z, z, z, z, z, z, 1, z, z }, //
  };

  private static final double[][] adjDirected = new double[][] { //
    {z, 0, z, z, z, z}, //
    {1, z, 1, z, z, z}, //
    {z, 1, z, z, z, z}, //
    {z, z, z, z, z, 1}, //
    {z, z, z, z, z, 1}, //
    {z, z, z, 1, z, z}, //
  };

  @Test
  public void testUndirectedComponents() {
    final WeightedNetwork<Double, Mapping.OfDouble> wn = OfDouble.fromAdjacency(adjUndirected,
        false);
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 0, 1, 2, 1, 2),
        CONNECTEDNESS.components(wn.getNetwork().asUndirectedGraph()));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 2, 4, 2, 4),
        CONNECTEDNESS.components(wn.getNetwork().asUndirectedGraph(), x -> x != 1));
  }

  @Test
  public void testDirectedComponents() {
    final WeightedNetwork<Double, Mapping.OfDouble> wn = OfDouble
        .fromAdjacency(adjDirected, false);
    final Mapping.OfInt weak = CONNECTEDNESS.weakComponents(wn.getNetwork().asDirectedGraph());
    assertArrayEquals(new int[] {0, 0, 0, 1, 1, 1}, weak.array());
    final Mapping.OfInt strong = CONNECTEDNESS.strongComponents(wn.getNetwork().asDirectedGraph());
    assertArrayEquals(new int[] {0, 0, 0, 1, 2, 1}, strong.array());
    assertEquals(Arrays.asList(Mappings.wrapUnmodifiableInt(0, 1, 2),
        Mappings.wrapUnmodifiableInt(3, 5), Mappings.wrapUnmodifiableInt(4)),
        CONNECTEDNESS.componentsToNodeLists(strong));
  }

  @Test
  public void testTwoEdgeComponents() {
    double[][] adj = new double[][]{
      {z}, //
      {1, z}, //
      {1, 1, z}, //
      {z, z, z, z}, //
      {z, z, z, 1, z}, //
      {z, z, 1, 1, 1, z}, //
      {z, z, z, z, z, z, z}, //
      {z, z, z, z, z, z, 1, z}, //
      {z, z, z, z, z, 1, 1, 1, z}, //
      {z, z, z, z, z, z, z, z, z, z}, //
    };
    //      3-----6-----9    10
    //     / \   / \   / \
    //    1--2  4--5  7--8

    WeightedNetwork<Double, Mapping.OfDouble> wn = OfDouble.fromAdjacency(adj, false);
    Mapping.OfInt comp = CONNECTEDNESS.twoEdgeComponents(wn.getNetwork().asUndirectedGraph());
    assertEqualComponents(new int[]{0, 0, 0, 1, 1, 1, 2, 2, 2, 3}, comp.array());

    adj[5][1] = 1;
    wn = OfDouble.fromAdjacency(adj, DyadType.UNDIRECTED);
    comp = CONNECTEDNESS.twoEdgeComponents(wn.getNetwork().asUndirectedGraph());
    assertEqualComponents(new int[]{0, 0, 0, 0, 0, 0, 2, 2, 2, 3}, comp.array());

    adj[5][1] = 0;
    adj[8][1] = 1;
    wn = OfDouble.fromAdjacency(adj, DyadType.UNDIRECTED);
    comp = CONNECTEDNESS.twoEdgeComponents(wn.getNetwork().asUndirectedGraph());
    assertEqualComponents(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 3}, comp.array());
  }

  @Test
  public void testTwoComponents() {
    UndirectedNetworkImpl.Builder builder = new UndirectedNetworkImpl.Builder();
    builder.addEdge(0, 1);
    builder.addEdge(1, 2);
    builder.addEdge(0, 2);
    builder.addEdge(2, 5);
    builder.addEdge(3, 5);
    builder.addEdge(4, 5);
    builder.addEdge(3, 4);
    builder.addEdge(5, 8);
    builder.addEdge(6, 8);
    builder.addEdge(6, 7);
    builder.addEdge(7, 8);
    builder.addEdge(9, 9);

    //      2-----5-----8    c9
    //     / \   / \   / \
    //    0--1  3--4  6--7

    Network network;
    Mapping.OfInt comp;
    network = builder.build();

    comp = CONNECTEDNESS.biconnectedComponents(network.asUndirectedGraph());
    assertEqualComponents(new int[]{0, 0, 0, 1, 2, 2, 2, 3, 4, 4, 4, 5}, comp.array());

    builder.addEdge(0, 7);
    network = builder.build();
    comp = CONNECTEDNESS.biconnectedComponents(network.asUndirectedGraph());
    assertEqualComponents(new int[]{0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 1, 0}, comp.array());
  }

  private void assertEqualComponents(int[] expecteds, int[] actuals) {
    int n = expecteds.length;
    assertEquals(n, actuals.length);
    for (int i = 0; i < n; i++)
      for (int j = i + 1; j < n; j++)
        assertEquals(expecteds[i] == expecteds[j], actuals[i] == actuals[j]);
  }

}
