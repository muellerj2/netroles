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
package ch.ethz.sn.visone3.roles.test.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.Pair;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.util.MultiplexNetworks;

import org.junit.jupiter.api.Test;

import java.util.function.IntBinaryOperator;
import java.util.stream.Stream;

public class MultiplexNetworkSupportTest {

  @Test
  public void testMultiplexDirected() {
    final Integer z = null;
    final Integer[][] directedOne = { //
        { z, z, 1, z, 2 }, //
        { z, 3, z, 4, z }, //
        { z, z, 5, z, 6 }, //
        { 7, z, z, 8, z }, //
        { z, z, z, z, z }, //
    };
    final Integer[][] directedTwo = { //
        { z, 0, z, z }, //
        { z, z, z, 1 }, //
        { z, z, 2, z }, //
        { z, 3, z, z }, //
    };
    final Integer[][] directedThree = { //
        { z, z, z, 0 }, //
        { z, z, 1, z }, //
        { z, 2, z, z }, //
        { z, 3, z, z }, //
    };
    final Integer[][] undirectedOne = { //
        { 0 }, //
        { z, z }, //
        { 1, 2, z }, //
        { z, z, z, z }, //
    };

    PrimitiveList.OfLong[] pairsMaps = new PrimitiveList.OfLong[] { Mappings.newLongList(),
        Mappings.newLongList(), Mappings.newLongList(), Mappings.newLongList() };
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netOne = MatrixSource
        .fromAdjacency(directedOne, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netTwo = MatrixSource
        .fromAdjacency(directedTwo, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netThree = MatrixSource
        .fromAdjacency(directedThree, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netFour = MatrixSource
        .fromAdjacency(undirectedOne, DyadType.UNDIRECTED);
    for (Edge edge : netOne.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[0].size() <= edge.getIndex()) {
        pairsMaps[0].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[0].setLong(edge, IntPair.tuple(edge.getSource(), edge.getTarget()));
    }
    for (Edge edge : netTwo.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[1].size() <= edge.getIndex()) {
        pairsMaps[1].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[1].setLong(edge, IntPair.tuple(edge.getTarget(), edge.getSource()));
    }
    for (Edge edge : netThree.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[2].size() <= edge.getIndex()) {
        pairsMaps[2].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[2].setLong(edge, IntPair.tuple(edge.getSource(), edge.getTarget()));
    }
    for (Edge edge : netFour.getNetwork().asUndirectedGraph().getEdges()) {
      if (pairsMaps[3].size() <= edge.getIndex()) {
        pairsMaps[3].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[3].setLong(edge, IntPair.set(edge.getSource(), edge.getTarget()));
    }

    IntBinaryOperator indexMapper = (netIndex, nodeIndex) -> {
      switch (netIndex) {
        case 0:
          return nodeIndex + 1;
        case 1:
          return nodeIndex;
        case 2:
          return 5 - nodeIndex;
        case 3:
          return nodeIndex + 2;
      }
      return nodeIndex;
    };
    Pair<Network, Mapping.OfInt[]> compositeNetwork = MultiplexNetworks
        .multiplexDirected(() -> Stream.of(new Pair<>(netOne.getNetwork(), Direction.OUTGOING),
            new Pair<>(netTwo.getNetwork(), Direction.INCOMING),
            new Pair<>(netThree.getNetwork(), Direction.OUTGOING),
            new Pair<>(netFour.getNetwork(), Direction.OUTGOING)).iterator(), indexMapper);
    int[] counts = new int[4];
    for (Edge edge : compositeNetwork.getFirst().asDirectedGraph().getEdges()) {
      for (int i = 0; i < pairsMaps.length; ++i) {
        int oldIndex = compositeNetwork.getSecond()[i].getInt(edge);
        if (oldIndex >= 0) {
          ++counts[i];
          long pair = pairsMaps[i].getLong(oldIndex);
          assertTrue(pair >= 0);
          if (i < 3) {
            assertEquals(edge.getSource(), indexMapper.applyAsInt(i, IntPair.first(pair)));
            assertEquals(edge.getTarget(), indexMapper.applyAsInt(i, IntPair.second(pair)));
          } else {
            assertEquals(IntPair.set(edge.getSource(), edge.getTarget()),
                IntPair.set(indexMapper.applyAsInt(i, IntPair.first(pair)),
                    indexMapper.applyAsInt(i, IntPair.second(pair))));
          }
        }
      }
    }
    assertArrayEquals(new int[] { netOne.getNetwork().countDyadicIndices(),
        netTwo.getNetwork().countDyadicIndices(), netThree.getNetwork().countDyadicIndices(),
        netFour.getNetwork().countDyadicIndices() * 2 - 1 }, counts);
  }

  @Test
  public void testMultiplexUndirected() {
    final Integer z = null;
    final Integer[][] directedOne = { //
        { z, z, 1, z, 2 }, //
        { z, 3, z, 4, z }, //
        { z, z, 5, z, 6 }, //
        { 7, z, z, 8, z }, //
        { z, z, z, z, z }, //
    };
    final Integer[][] directedTwo = { //
        { z, 0, z, z }, //
        { z, z, z, 1 }, //
        { z, z, 2, z }, //
        { z, 3, z, z }, //
    };
    final Integer[][] directedThree = { //
        { z, z, z, 0 }, //
        { z, z, 1, z }, //
        { z, 2, z, z }, //
        { z, 3, z, z }, //
    };
    final Integer[][] undirectedOne = { //
        { 0 }, //
        { z, z }, //
        { 1, 2, z }, //
        { z, z, z, z }, //
    };

    PrimitiveList.OfLong[] pairsMaps = new PrimitiveList.OfLong[] { Mappings.newLongList(),
        Mappings.newLongList(), Mappings.newLongList(), Mappings.newLongList() };
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netOne = MatrixSource
        .fromAdjacency(directedOne, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netTwo = MatrixSource
        .fromAdjacency(directedTwo, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netThree = MatrixSource
        .fromAdjacency(directedThree, DyadType.DIRECTED);
    WeightedNetwork<? extends Integer, ? extends ConstMapping<? extends Integer>> netFour = MatrixSource
        .fromAdjacency(undirectedOne, DyadType.UNDIRECTED);
    for (Edge edge : netOne.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[0].size() <= edge.getIndex()) {
        pairsMaps[0].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[0].setLong(edge, IntPair.tuple(edge.getSource(), edge.getTarget()));
    }
    for (Edge edge : netTwo.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[1].size() <= edge.getIndex()) {
        pairsMaps[1].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[1].setLong(edge, IntPair.tuple(edge.getTarget(), edge.getSource()));
    }
    for (Edge edge : netThree.getNetwork().asDirectedGraph().getEdges()) {
      if (pairsMaps[2].size() <= edge.getIndex()) {
        pairsMaps[2].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[2].setLong(edge, IntPair.tuple(edge.getSource(), edge.getTarget()));
    }
    for (Edge edge : netFour.getNetwork().asUndirectedGraph().getEdges()) {
      if (pairsMaps[3].size() <= edge.getIndex()) {
        pairsMaps[3].setSize(-1L, edge.getIndex() + 1);
      }
      pairsMaps[3].setLong(edge, IntPair.set(edge.getSource(), edge.getTarget()));
    }

    IntBinaryOperator indexMapper = (netIndex, nodeIndex) -> {
      switch (netIndex) {
        case 0:
          return nodeIndex + 1;
        case 1:
          return nodeIndex;
        case 2:
          return 5 - nodeIndex;
        case 3:
          return nodeIndex + 2;
      }
      return nodeIndex;
    };
    Pair<Network, Mapping.OfLong[]> compositeNetwork = MultiplexNetworks
        .multiplexUndirected(() -> Stream.of(new Pair<>(netOne.getNetwork(), Direction.OUTGOING),
            new Pair<>(netTwo.getNetwork(), Direction.INCOMING),
            new Pair<>(netThree.getNetwork(), Direction.OUTGOING),
            new Pair<>(netFour.getNetwork(), Direction.OUTGOING)).iterator(), indexMapper);
    int[] counts = new int[4];
    for (Edge edge : compositeNetwork.getFirst().asUndirectedGraph().getEdges()) {
      for (int i = 0; i < pairsMaps.length; ++i) {
        long oldIndices = compositeNetwork.getSecond()[i].getLong(edge);
        int smallerNode = edge.getSource() <= edge.getTarget() ? edge.getSource()
            : edge.getTarget();
        int largerNode = edge.getSource() == smallerNode ? edge.getTarget() : edge.getSource();

        int firstOldIndex = IntPair.first(oldIndices);
        int secondOldIndex = IntPair.second(oldIndices);
        if (firstOldIndex >= 0) {
          ++counts[i];
          long pair = pairsMaps[i].getLong(firstOldIndex);
          assertTrue(pair >= 0);
          if (i < 3) {
            assertEquals(smallerNode, indexMapper.applyAsInt(i, IntPair.first(pair)));
            assertEquals(largerNode, indexMapper.applyAsInt(i, IntPair.second(pair)));
          } else {
            assertEquals(IntPair.set(edge.getSource(), edge.getTarget()),
                IntPair.set(indexMapper.applyAsInt(i, IntPair.first(pair)),
                    indexMapper.applyAsInt(i, IntPair.second(pair))));
          }
        }
        if (secondOldIndex >= 0) {
          ++counts[i];
          long pair = pairsMaps[i].getLong(secondOldIndex);
          assertTrue(pair >= 0);
          if (i < 3) {
            assertEquals(largerNode, indexMapper.applyAsInt(i, IntPair.first(pair)));
            assertEquals(smallerNode, indexMapper.applyAsInt(i, IntPair.second(pair)));
          } else {
            assertEquals(IntPair.set(edge.getSource(), edge.getTarget()),
                IntPair.set(indexMapper.applyAsInt(i, IntPair.first(pair)),
                    indexMapper.applyAsInt(i, IntPair.second(pair))));
          }
        }
      }
    }
    assertArrayEquals(new int[] { netOne.getNetwork().countDyadicIndices(),
        netTwo.getNetwork().countDyadicIndices(), netThree.getNetwork().countDyadicIndices(),
        netFour.getNetwork().countDyadicIndices() * 2 - 1 }, counts);
  }

}
