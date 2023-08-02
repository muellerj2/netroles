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

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.algorithms.Traversal;
import ch.ethz.sn.visone3.algorithms.impl.TraversalImpl;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource.OfDouble;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.networks.impl.AsciiDumper;

import org.junit.jupiter.api.Test;

public class TraversalImplTest {
  private static final Traversal TRAVERSAL = AlgoProvider.getInstance().traversals();

  @Test
  public void create() {
    final double z = Double.NaN;
    final double[][] adj = new double[][] { //
      {z, z, z, 1, z, z}, //
      {1, z, z, z, z, z}, //
      {z, z, z, z, z, z}, //
      {z, z, z, z, 1, z}, //
      {z, 1, 1, 1, z, z}, //
      {z, z, z, 1, z, z}, //
    };

    final WeightedNetwork<Double, Mapping.OfDouble> src = OfDouble.fromAdjacency(adj,
        DyadType.DIRECTED);
    final Network network = src.getNetwork();

    final DirectedGraph graph = network.asDirectedGraph();
    System.out.println(AsciiDumper.multiLine(network));

    final int n = graph.countVertices();
    final Mapping.OfInt bfsn = TRAVERSAL.bfs(n, graph::getOutNeighbors, graph.getVertices(), TraversalImpl.Visitor.NULL);
    assertArrayEquals(new int[] { 0, 3, 3, 1, 2, 0 }, bfsn.array());
    final Mapping.OfInt dfsn = TRAVERSAL.dfs(n, graph::getOutNeighbors, graph.getVertices(), TraversalImpl.Visitor.NULL);
    assertArrayEquals(new int[] { 0, 3, 4, 1, 2, 0 }, dfsn.array());
  }
}

