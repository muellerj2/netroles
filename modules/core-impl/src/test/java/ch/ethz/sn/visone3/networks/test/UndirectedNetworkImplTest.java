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

package ch.ethz.sn.visone3.networks.test;

import static ch.ethz.sn.visone3.test.NetworkAsserts.assertNetwork;
import static ch.ethz.sn.visone3.test.NetworkAsserts.boxed;
import static ch.ethz.sn.visone3.test.NetworkAsserts.diagonal2square;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.AdjacencyListEntry;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.ReorderableNetwork;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.networks.impl.AsciiDumper;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UndirectedNetworkImplTest {
  private static final Logger LOG = LoggerFactory.getLogger(UndirectedNetworkImplTest.class);

  @Test
  public void testZeroInt() {
    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 0, z }, //
        { 1, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, false);
    LOG.info("{}", AsciiDumper.multiLine(wn.getNetwork()));
    assertEquals(3, wn.getNetwork().asUndirectedGraph().countEdges());
    assertEquals(6, wn.getNetwork().asRelation().countRelationships());
    assertNetwork(diagonal2square(adj, (n) -> new Integer[n][n]), wn.getNetwork(),
        (Mapping.OfInt) wn.getWeight());

    Edge e = wn.getNetwork().asUndirectedGraph().getEdge(0, 1);
    assertEquals(0, e.getSource());
    assertEquals(1, e.getTarget());
    assertEquals((Object) 0, wn.getWeight().get(e));
    assertNull(wn.getNetwork().asUndirectedGraph().getEdge(1, 1));
    e = wn.getNetwork().asUndirectedGraph().getEdge(1, 2);
    assertEquals(1, e.getSource());
    assertEquals(2, e.getTarget());
    assertEquals((Object) 1, wn.getWeight().get(e));

    // wn = MatrixSource.fromAdjacency(IntRange.BINARY, adj);
    // LOG.info("{}", AsciiDumper.multiLine(wn.getNetwork()));
    // assertEquals(2, wn.getNetwork().asUndirectedGraph().countEdges());
    // assertEquals(4, wn.getNetwork().asRelation().countRelationships());
    // assertNetwork(diagonal2square(adj, (n) -> new Integer[n][n]), wn.getNetwork(),
    // wn.getWeight());
  }

  @Test
  public void testZeroDouble() {
    final double z = Double.NaN;
    final double[][] adj = new double[][] { //
        { z }, //
        { 0, z }, //
        { 1, 1, z }, //
    };

    final WeightedNetwork<Double, Mapping.OfDouble> s = MatrixSource.OfDouble.fromAdjacency(adj,
        false);
    assertEquals(3, s.getNetwork().asUndirectedGraph().countEdges());
    assertEquals(3, s.getNetwork().countMonadicIndices());
    assertEquals(6, s.getNetwork().asRelation().countRelationships());
    assertNetwork(diagonal2square(boxed(adj), (n) -> new Double[n][n]), s.getNetwork(),
        s.getWeight());
    LOG.info("{}", AsciiDumper.multiLine(s.getNetwork()));

    // s = DoubleMatrixSource.fromAdjacency(DoubleRange.REAL, adj);
    // assertEquals(2, s.getNetwork().asUndirectedGraph().countEdges());
    // assertEquals(4, s.getNetwork().asRelation().countRelationships());
    // assertNetwork(diagonal2square(boxed(adj), (n) -> new Double[n][n]), s.getNetwork(),
    // s.getWeight());
    // LOG.info("{}", AsciiDumper.multiLine(s.getNetwork()));
  }

  @Test
  public void testLoopFree() {
    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { z, 2, z }, //
        { z, 2, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, false);
    assertNetwork(diagonal2square(adj, (n) -> new Integer[n][n]), wn.getNetwork(),
        (Mapping.OfInt) wn.getWeight());

    Iterator<Edge> edgesAt0 = wn.getNetwork().asUndirectedGraph().getEdges(0).iterator();
    assertTrue(edgesAt0.hasNext());
    Edge e = edgesAt0.next();
    assertEquals(0, e.getSource());
    assertEquals(1, e.getTarget());
    assertFalse(edgesAt0.hasNext());
    assertThrows(NoSuchElementException.class, () -> edgesAt0.next());
    Edge[] edgeArrayAt0 = wn.getNetwork().asUndirectedGraph().getEdgeStream(0).toArray(Edge[]::new);
    assertEquals(1, edgeArrayAt0.length);
    assertEquals(0, edgeArrayAt0[0].getSource());
    assertEquals(1, edgeArrayAt0[0].getTarget());
    assertEquals(e.getIndex(), edgeArrayAt0[0].getIndex());
    assertArrayEquals(new int[] { 1, 2 },
        wn.getNetwork().asUndirectedGraph().getNeighborStream(3).toArray());
    assertArrayEquals(new int[] { 1, 2 },
        wn.getNetwork().asRelation().getPartnersStream(3).toArray());
    assertEquals(new HashSet<>(Arrays.asList(1, 3)),
        StreamSupport
            .stream(wn.getNetwork().asUndirectedGraph().getNeighbors(2).spliterator(), false)
            .collect(Collectors.toSet()));

    Iterator<Edge> allEdges = wn.getNetwork().asUndirectedGraph().getEdges().iterator();
    int count = 0;
    for (; allEdges.hasNext(); allEdges.next()) {
      ++count;
    }
    assertEquals(4, count);
    assertThrows(NoSuchElementException.class, () -> allEdges.next());

    Iterator<Relationship> relationships = wn.getNetwork().asRelation().getRelationships(0)
        .iterator();
    count = 0;
    int index = 0;
    while (relationships.hasNext() && count < 5) {
      Relationship rel = relationships.next();
      if (rel.getLeft() == 0) {
        assertEquals(1, rel.getRight());
        index += rel.getIndex();
      } else {
        assertEquals(0, rel.getRight());
        assertEquals(1, rel.getLeft());
        index -= rel.getIndex();
      }
      ++count;
    }
    assertEquals(0, index);

    assertTrue(wn.getNetwork().equals(MatrixSource.fromAdjacency(adj, false).getNetwork()));
    assertFalse(wn.getNetwork().equals(new Object()));
    assertEquals(wn.getNetwork().hashCode(),
        MatrixSource.fromAdjacency(adj, false).getNetwork().hashCode());
    assertNotNull(wn.getNetwork().toString());
    assertThrows(UnsupportedOperationException.class, () -> wn.getNetwork().asDirectedGraph());
    assertFalse(wn.getNetwork().equals(MatrixSource
        .fromAdjacency(diagonal2square(adj, (n) -> new Integer[n][n]), false).getNetwork()));
    assertFalse(MatrixSource.fromAdjacency(diagonal2square(adj, (n) -> new Integer[n][n]), false)
        .getNetwork().equals(wn.getNetwork()));
  }

  @Test
  public void testWithLoops() {
    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { z, 2, 1 }, //
        { z, 2, 1, 3 }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    assertNetwork(diagonal2square(adj, (n) -> new Integer[n][n]), wn.getNetwork(),
        (Mapping.OfInt) wn.getWeight());
    assertTrue(wn.getNetwork().equals(MatrixSource.fromAdjacency(adj, false).getNetwork()));
    assertEquals(wn.getNetwork().hashCode(),
        MatrixSource.fromAdjacency(adj, false).getNetwork().hashCode());

    NetworkBuilder builder = wn.getNetwork().builder();
    assertNotNull(builder);
    assertFalse(builder.acceptsTwoModes());
    assertFalse(builder.acceptsDirected());
    builder.ensureNode(3);
    builder.addEdge(0, 1);
    builder.addEdge(1, 2);
    builder.addEdge(2, 3);
    Network network = builder.build();
    assertFalse(network.isTwoMode());
    assertFalse(network.isDirected());
    assertNetwork(diagonal2square(new Integer[][] { //
        { z }, //
        { 1, z }, //
        { z, 1, z }, //
        { z, z, 1, z }, //
    }, (n) -> new Integer[n][n]), network, Mappings.wrapUnmodifiableInt(1, 1, 1));
  }

  @Test
  public void testReorderable() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { z, 2, z }, //
        { z, 2, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, false);
    
    ReorderableNetwork network = wn.getNetwork().reorderable();
    int[] neighborsOf1 = network.asUndirectedGraph().getNeighborStream(1).toArray();
    network.asUndirectedGraph().swapNeighbors(1, 0, 1);
    int temp = neighborsOf1[0];
    neighborsOf1[0] = neighborsOf1[1];
    neighborsOf1[1] = temp;
    assertArrayEquals(neighborsOf1, network.asUndirectedGraph().getNeighborStream(1).toArray());
    network.sortNeighborhoods((IntBinaryOperator) (x, y) -> {
      if (x % 2 == y % 2) {
        return Integer.compare(x, y);
      } else {
        return Integer.compare(x % 2, y % 2);
      }
    });
    assertArrayEquals(new int[] { 1, 3 },
        network.asUndirectedGraph().getNeighborStream(2).toArray());
    assertArrayEquals(new int[] { 2, 1 },
        network.asUndirectedGraph().getNeighborStream(3).toArray());
    network.sortNeighborhoods((Comparator<AdjacencyListEntry>) (x, y) -> {
      assertTrue(x.getListSource() == y.getListSource());
      assertTrue(x.getListSource() == x.getEdge().getSource());
      assertTrue(y.getListSource() == y.getEdge().getSource());
      return Integer.compare(x.getEdge().getTarget(), y.getEdge().getTarget());
    });
    assertArrayEquals(new int[] { 1, 3 },
        network.asUndirectedGraph().getNeighborStream(2).toArray());
    assertArrayEquals(new int[] { 1, 2 },
        network.asUndirectedGraph().getNeighborStream(3).toArray());
    network.sortNeighborhoods(x -> wn.getWeight().get(x.getEdge()), 3);
    assertArrayEquals(new int[] { 0, 2, 3 },
        network.asUndirectedGraph().getNeighborStream(1).toArray());
    assertArrayEquals(new int[] { 2, 1 },
        network.asUndirectedGraph().getNeighborStream(3).toArray());
    network.asUndirectedGraph().swapNeighbors(1, 2, 0);
    assertArrayEquals(new int[] { 3, 2, 0 },
        network.asUndirectedGraph().getNeighborStream(1).toArray());
    ReorderableNetwork rereorderable = network.reorderable();
    assertTrue(network != rereorderable && network.equals(rereorderable));
    assertThrows(UnsupportedOperationException.class, () -> network.asDirectedGraph());
  }
}
