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

package ch.ethz.sn.visone3.networks.test;

import static ch.ethz.sn.visone3.test.NetworkAsserts.assertNetwork;
import static ch.ethz.sn.visone3.test.NetworkAsserts.boxed;
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
import ch.ethz.sn.visone3.networks.Direction;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DirectedNetworkImplTest {

  @SuppressWarnings("deprecation")
  @Test
  public void testWithLoops() {
    final double z = Double.NaN;
    final double[][] adj = new double[][] { //
        { z, z, z, z }, //
        { 1, z, 2, 2 }, //
        { 2, 1, z, 4 }, //
        { 2, 1, 4, 4 }, //
    };

    final WeightedNetwork<Double, Mapping.OfDouble> src = MatrixSource.OfDouble.fromAdjacency(adj,
        false);
    System.out.println(AsciiDumper.multiLine(src.getNetwork()));
    assertNetwork(boxed(adj), src.getNetwork(), src.getWeight());
    assertEquals(4, src.getNetwork().asDirectedGraph().countVertices());
    assertEquals(4, src.getNetwork().countMonadicIndices());
    Edge e = src.getNetwork().asDirectedGraph().getEdge(2, 3);
    assertEquals(2, e.getSource());
    assertEquals(3, e.getTarget());
    assertEquals(Double.valueOf(4.), src.getWeight().get(e));
    assertEquals(Double.valueOf(4.), Double.valueOf(src.getWeight().getDouble(e)));
    assertNull(src.getNetwork().asDirectedGraph().getEdge(0, 2));


    Iterator<Edge> edgesAt0 = src.getNetwork().asDirectedGraph().getOutEdges(0).iterator();
    assertFalse(edgesAt0.hasNext());
    assertThrows(NoSuchElementException.class, () -> edgesAt0.next());

    Iterator<Edge> edgesAt1 = src.getNetwork().asDirectedGraph().getInEdges(1).iterator();
    assertTrue(edgesAt1.hasNext());
    e = edgesAt1.next();
    assertEquals(1, e.getTarget());
    assertTrue(e.getSource() == 2 || e.getSource() == 3);
    int prevSource = e.getSource();
    assertTrue(edgesAt1.hasNext());
    e = edgesAt1.next();
    assertEquals(1, e.getTarget());
    assertTrue(e.getSource() == 2 || e.getSource() == 3);
    assertTrue(e.getSource() != prevSource);
    assertFalse(edgesAt1.hasNext());

    assertThrows(NoSuchElementException.class, () -> edgesAt1.next());
    Iterator<Edge> edgesAt1Out = src.getNetwork().asDirectedGraph().getOutEdges(1).iterator();
    Set<Integer> edgesAt1OutTargets = new HashSet<>();
    int count = 0;
    while (edgesAt1Out.hasNext() && count < 5) {
      e = edgesAt1Out.next();
      assertEquals(1, e.getSource());
      assertTrue(edgesAt1OutTargets.add(e.getTarget()));
      ++count;
    }
    assertEquals(new HashSet<>(Arrays.asList(0, 2, 3)), edgesAt1OutTargets);
    assertThrows(NoSuchElementException.class, () -> edgesAt1Out.next());

    assertArrayEquals(new int[] { 2, 3 },
        src.getNetwork().asDirectedGraph().getInNeighborStream(1).toArray());
    assertArrayEquals(new int[] { 2, 3, 0, 2, 3 },
        src.getNetwork().asRelation().getPartnersStream(1).toArray());

    Iterator<Edge> allEdges = src.getNetwork().asDirectedGraph().getEdges().iterator();
    count = 0;
    for (; allEdges.hasNext(); allEdges.next()) {
      ++count;
    }
    assertEquals(10, count);
    assertThrows(NoSuchElementException.class, () -> allEdges.next());

    Iterator<Relationship> relationships = src.getNetwork().asRelation().getRelationships(2)
        .iterator();
    count = 0;
    Set<Integer> incoming = new HashSet<>();
    Set<Integer> outgoing = new HashSet<>();
    while (relationships.hasNext() && count < 8) {
      Relationship rel = relationships.next();
      if (rel.getLeft() == 2) {
        assertTrue(outgoing.add(rel.getRight()));
      } else {
        assertEquals(2, rel.getRight());
        assertTrue(incoming.add(rel.getLeft()));
      }
      ++count;
    }
    assertEquals(5, count);
    assertEquals(new HashSet<>(Arrays.asList(0, 1, 3)), outgoing);
    assertEquals(new HashSet<>(Arrays.asList(1, 3)), incoming);
    assertFalse(relationships.hasNext());
    assertThrows(NoSuchElementException.class, () -> relationships.next());

    count = 0;
    incoming = new HashSet<>();
    outgoing = new HashSet<>();
    Iterator<Edge> edgesAt2All = src.getNetwork().asDirectedGraph().getEdges(2).iterator();
    while (edgesAt2All.hasNext() && count < 8) {
      e = edgesAt2All.next();
      System.out.println(e);
      if (e.getSource() == 2) {
        assertTrue(outgoing.add(e.getTarget()));
      } else {
        assertEquals(2, e.getTarget());
        assertTrue(incoming.add(e.getSource()));
      }
      ++count;
    }
    assertEquals(5, count);
    assertEquals(new HashSet<>(Arrays.asList(0, 1, 3)), outgoing);
    assertEquals(new HashSet<>(Arrays.asList(1, 3)), incoming);
    assertFalse(edgesAt2All.hasNext());
    assertThrows(NoSuchElementException.class, () -> edgesAt2All.next());

    assertTrue(src.getNetwork()
        .equals(MatrixSource.OfDouble.fromAdjacency(adj, DyadType.DIRECTED).getNetwork()));
    assertFalse(src.getNetwork().equals(new Object()));
    assertEquals(src.getNetwork().hashCode(),
        MatrixSource.OfDouble.fromAdjacency(adj, false).getNetwork().hashCode());
    assertNotNull(src.getNetwork().toString());
    assertThrows(UnsupportedOperationException.class, () -> src.getNetwork().asUndirectedGraph());
    assertFalse(
        src.getNetwork().equals(MatrixSource.OfDouble.fromAdjacency(adj, true).getNetwork()));
    assertFalse(
        MatrixSource.OfDouble.fromAdjacency(adj, true).getNetwork().equals(src.getNetwork()));
    // assertFalse(MatrixSource.fromAdjacency(diagonal2square(adj, (n) -> new Integer[n][n]), false)
    // .getNetwork().equals(wn.getNetwork()));
  }

  @Test
  public void testWithoutLoops() {

    final double z = Double.NaN;
    final double[][] adj = new double[][] { //
        { z, 2, z, z }, //
        { 1, z, 2, z }, //
        { 2, 1, z, 4 }, //
        { z, 1, 4, z }, //
    };

    final WeightedNetwork<Double, Mapping.OfDouble> src = MatrixSource.OfDouble.fromAdjacency(adj,
        false);
    System.out.println(AsciiDumper.multiLine(src.getNetwork()));
    assertNetwork(boxed(adj), src.getNetwork(), src.getWeight());
    assertEquals(4, src.getNetwork().asDirectedGraph().countVertices());
    assertEquals(4, src.getNetwork().countMonadicIndices());
    assertTrue(
        src.getNetwork().equals(MatrixSource.OfDouble.fromAdjacency(adj, false).getNetwork()));
    assertEquals(src.getNetwork().hashCode(),
        MatrixSource.OfDouble.fromAdjacency(adj, false).getNetwork().hashCode());

    assertEquals(new HashSet<>(Arrays.asList(1, 3)),
        StreamSupport
            .stream(src.getNetwork().asDirectedGraph().getInNeighbors(2).spliterator(), false)
            .collect(Collectors.toSet()));
    assertEquals(new HashSet<>(Arrays.asList(0, 2)),
        StreamSupport
            .stream(src.getNetwork().asDirectedGraph().getOutNeighbors(1).spliterator(), false)
            .collect(Collectors.toSet()));

    NetworkBuilder builder = src.getNetwork().builder();
    assertNotNull(builder);
    assertFalse(builder.acceptsTwoModes());
    assertTrue(builder.acceptsDirected());
    builder.ensureNode(3);
    builder.addEdge(0, 1);
    builder.addEdge(1, 2);
    builder.addEdge(2, 3);
    Network network = builder.build();
    assertFalse(network.isTwoMode());
    assertTrue(network.isDirected());
    assertNetwork(boxed(new double[][] { //
        { z, 1, z, z }, //
        { z, z, 1, z }, //
        { z, z, z, 1 }, //
        { z, z, z, z } //
    }), network, Mappings.newDoubleListFrom(1, 1, 1));
  }

  @Test
  public void testReorderable() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z, z, 2, z }, //
        { 1, z, z, 1 }, //
        { z, 2, z, 1 }, //
        { 3, 2, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, false);

    ReorderableNetwork network = wn.getNetwork().reorderable();
    int[] neighborsOf1 = network.asDirectedGraph().getInNeighborStream(1).toArray();
    network.asDirectedGraph().swapNeighbors(1, Direction.INCOMING, 0, 1);
    int temp = neighborsOf1[0];
    neighborsOf1[0] = neighborsOf1[1];
    neighborsOf1[1] = temp;
    assertArrayEquals(neighborsOf1, network.asDirectedGraph().getInNeighborStream(1).toArray());

    network.sortNeighborhoods((IntBinaryOperator) (x, y) -> {
      if (x % 2 == y % 2) {
        return Integer.compare(x, y);
      } else {
        return Integer.compare(x % 2, y % 2);
      }
    });
    assertArrayEquals(new int[] { 0, 2, 1 },
        network.asDirectedGraph().getOutNeighborStream(3).toArray());
    assertArrayEquals(new int[] { 2, 1 },
        network.asDirectedGraph().getInNeighborStream(3).toArray());

    network.sortNeighborhoods((Comparator<AdjacencyListEntry>) (x, y) -> {
      assertTrue(x.getListSource() == y.getListSource());
      assertTrue(x.getListSource() == x.getEdge().getSource()
          || x.getListSource() == x.getEdge().getTarget());
      assertTrue(y.getListSource() == y.getEdge().getSource()
          || y.getListSource() == y.getEdge().getTarget());
      return Integer.compare(x.getEdge().getTarget(), y.getEdge().getTarget());
    });
    assertArrayEquals(new int[] { 0, 1, 2 },
        network.asDirectedGraph().getOutNeighborStream(3).toArray());
    assertArrayEquals(new int[] { 2, 1 },
        network.asDirectedGraph().getInNeighborStream(3).toArray());


    network.sortNeighborhoods(x -> wn.getWeight().get(x.getEdge()), 4);
    assertArrayEquals(new int[] { 2, 1, 0 },
        network.asDirectedGraph().getOutNeighborStream(3).toArray());
    assertArrayEquals(new int[] { 3, 0 },
        network.asDirectedGraph().getInNeighborStream(2).toArray());
    assertArrayEquals(new int[] { 1, 3 },
        network.asDirectedGraph().getInNeighborStream(0).toArray());
    network.asDirectedGraph().swapNeighbors(3, Direction.OUTGOING, 1, 2);
    assertArrayEquals(new int[] { 2, 0, 1 },
        network.asDirectedGraph().getOutNeighborStream(3).toArray());
    ReorderableNetwork rereorderable = network.reorderable();
    assertTrue(network != rereorderable && network.equals(rereorderable));
    assertThrows(UnsupportedOperationException.class, () -> network.asUndirectedGraph());
  }


}
