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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntBinaryOperator;
import java.util.stream.StreamSupport;

public class TwoModeNetworkImplTest {
  private static final Logger LOG = LoggerFactory.getLogger(TwoModeNetworkImplTest.class);

  @Test
  public void testZeroInt() {
    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z, 4, 5, z }, //
        { 1, 1, z, z }, //
    };
    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, true);
    LOG.info("{}", AsciiDumper.multiLine(wn.getNetwork()));
    assertEquals(6, wn.getNetwork().countMonadicIndices());
    assertEquals(Mappings.intRange(0, 6), wn.getNetwork().asRelation().getUnionDomain());
    assertEquals(2, wn.getNetwork().asUndirectedGraph().getDegree(1));
    assertEquals(1, wn.getNetwork().asUndirectedGraph().getDegree(2));
    assertEquals(0, wn.getNetwork().asUndirectedGraph().getDegree(5));
    assertEquals(0, wn.getNetwork().asUndirectedGraph().countLoops());
    assertNetwork(adj, wn.getNetwork(), (Mapping.OfInt) wn.getWeight());

    Iterator<Edge> edgesAt0 = wn.getNetwork().asUndirectedGraph().getEdges(0).iterator();
    assertTrue(edgesAt0.hasNext());
    Edge e = edgesAt0.next();
    assertEquals(0, e.getSource());
    assertEquals(3, e.getTarget());
    assertTrue(edgesAt0.hasNext());
    e = edgesAt0.next();
    assertEquals(0, e.getSource());
    assertEquals(4, e.getTarget());
    assertFalse(edgesAt0.hasNext());
    assertThrows(NoSuchElementException.class, () -> edgesAt0.next());
    assertArrayEquals(new int[] { 3, 4 },
        wn.getNetwork().asUndirectedGraph().getNeighborStream(0).toArray());
    assertArrayEquals(new int[] { 3, 4 },
        wn.getNetwork().asRelation().getPartnersStream(0).toArray());
    assertArrayEquals(new Integer[] { 0 },
        StreamSupport
            .stream(wn.getNetwork().asUndirectedGraph().getNeighbors(4).spliterator(), false)
            .toArray());

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
    while (relationships.hasNext() && count < 5) {
      Relationship rel = relationships.next();
      assertEquals(0, rel.getLeft());
      assertTrue(rel.getRight() == 3 || rel.getRight() == 4);
      ++count;
    }
    assertEquals(2, count);

    relationships = wn.getNetwork().asRelation().getRelationships(2).iterator();
    count = 0;
    while (relationships.hasNext() && count < 5) {
      Relationship rel = relationships.next();
      assertEquals(1, rel.getLeft());
      assertEquals(2, rel.getRight());
      ++count;
    }
    assertEquals(1, count);

    assertTrue(wn.getNetwork().equals(MatrixSource.fromAdjacency(adj, true).getNetwork()));
    assertFalse(wn.getNetwork().equals(new Object()));
    assertEquals(wn.getNetwork().hashCode(),
        MatrixSource.fromAdjacency(adj, true).getNetwork().hashCode());
    assertNotNull(wn.getNetwork().toString());
    assertThrows(UnsupportedOperationException.class, () -> wn.getNetwork().asDirectedGraph());
    Integer[][] adj2 = { adj[0], adj[1], adj[0], adj[1] };
    assertFalse(wn.getNetwork().equals(MatrixSource.fromAdjacency(adj2, false).getNetwork()));
    assertFalse(MatrixSource.fromAdjacency(adj2, false).getNetwork().equals(wn.getNetwork()));
  }

  @Test
  public void testZeroIn() {
    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z, 0 }, //
        { 1, 1 }, //
        { 0, z }, //
        { 1, z }, //
    };
    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, DyadType.TWO_MODE);
    LOG.info("{}", AsciiDumper.multiLine(wn.getNetwork()));
    assertNetwork(adj, wn.getNetwork(), (Mapping.OfInt) wn.getWeight());

    assertTrue(wn.getNetwork().equals(MatrixSource.fromAdjacency(adj, true).getNetwork()));
    assertEquals(wn.getNetwork().hashCode(),
        MatrixSource.fromAdjacency(adj, true).getNetwork().hashCode());

    NetworkBuilder builder = wn.getNetwork().builder();
    assertNotNull(builder);
    assertTrue(builder.acceptsTwoModes());
    assertFalse(builder.acceptsDirected());
    builder.ensureNode(2);
    builder.ensureAffiliation(3);
    builder.addEdge(0, 1);
    builder.addEdge(1, 2);
    builder.addEdge(2, 3);
    Network network = builder.build();
    assertTrue(network.isTwoMode());
    assertFalse(network.isDirected());
    assertNetwork(new Integer[][] { //
        { z, 1, z, z }, //
        { z, z, 1, z }, //
        { z, z, z, 1 }, //
    }, network, Mappings.wrapUnmodifiableInt(1, 1, 1));
    assertThrows(IllegalArgumentException.class,
        () -> network.asRelation().getRelationshipsFrom(-1));
    assertThrows(IllegalArgumentException.class,
        () -> network.asRelation().getRelationshipsFrom(3));
    assertThrows(IllegalArgumentException.class, () -> network.asRelation().getRelationshipsTo(0));
    assertThrows(IllegalArgumentException.class, () -> network.asRelation().getRelationshipsTo(7));
    // final WeightedNetwork<Mapping<Integer>> network = MatrixSource.fromAdjacency(adj);
    // LOG.info("{}", AsciiDumper.multiLine(network));
    // assertNetwork(adj, network);
  }

  @Test
  public void testReorderable() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z, 4, 5, z }, //
        { 1, 1, z, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> wn = MatrixSource
        .fromAdjacency(adj, true);

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
    assertArrayEquals(new int[] { 4, 3 },
        network.asUndirectedGraph().getNeighborStream(0).toArray());
    assertArrayEquals(new int[] { 2, 3 },
        network.asUndirectedGraph().getNeighborStream(1).toArray());
    network.sortNeighborhoods((Comparator<AdjacencyListEntry>) (x, y) -> {
      assertTrue(x.getListSource() == y.getListSource());
      assertTrue(x.getListSource() == x.getEdge().getSource()
          || x.getListSource() == x.getEdge().getTarget());
      assertTrue(y.getListSource() == y.getEdge().getSource()
          || y.getListSource() == y.getEdge().getTarget());
      return Integer.compare(x.getEdge().getTarget(), y.getEdge().getTarget());
    });
    assertArrayEquals(new int[] { 3, 4 },
        network.asUndirectedGraph().getNeighborStream(0).toArray());
    assertArrayEquals(new int[] { 2, 3 },
        network.asUndirectedGraph().getNeighborStream(1).toArray());
    network.sortNeighborhoods(x -> wn.getWeight().get(x.getEdge()), 3);
    assertArrayEquals(new int[] { 1, 0 },
        network.asUndirectedGraph().getNeighborStream(3).toArray());
    assertArrayEquals(new int[] { 3, 4 },
        network.asUndirectedGraph().getNeighborStream(0).toArray());
    network.asUndirectedGraph().swapNeighbors(0, 0, 1);
    assertArrayEquals(new int[] { 4, 3 },
        network.asUndirectedGraph().getNeighborStream(0).toArray());
    ReorderableNetwork rereorderable = network.reorderable();
    assertTrue(network != rereorderable && network.equals(rereorderable));
    assertThrows(UnsupportedOperationException.class, () -> network.asDirectedGraph());
  }
}
