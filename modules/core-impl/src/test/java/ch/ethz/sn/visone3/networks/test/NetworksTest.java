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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Networks;
import ch.ethz.sn.visone3.networks.WeightedNetwork;

import org.junit.jupiter.api.Test;

public class NetworksTest {

  @Test
  public void testNetworks() {

    final Integer z = null;
    final Integer[][] twomodeAdj = new Integer[][] { //
        { z, 4, 5, z }, //
        { 1, 1, z, z }, //
    };
    final Integer[][] undirectedAdj = new Integer[][] { //
        { z }, //
        { 0, z }, //
        { 1, 1, z }, //
    };
    final Integer[][] directedAdj = new Integer[][] { //
        { z, z, 2, z }, //
        { 1, z, z, 1 }, //
        { z, 2, z, 1 }, //
        { 3, 2, 1, z }, //
    };

    final WeightedNetwork<?, ?> wTwoMode = MatrixSource.fromAdjacency(twomodeAdj,
        DyadType.TWO_MODE);
    final Network twomode = wTwoMode.getNetwork();
    final WeightedNetwork<?, ?> wUndirected = MatrixSource.fromAdjacency(undirectedAdj,
        DyadType.UNDIRECTED);
    final Network undirected = wUndirected.getNetwork();
    final WeightedNetwork<?, ?> wDirected = MatrixSource.fromAdjacency(directedAdj,
        DyadType.DIRECTED);
    final Network directed = wDirected.getNetwork();

    assertEquals(directed, Networks.requireOneMode(directed));
    assertEquals(undirected, Networks.requireOneMode(undirected));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireOneMode(twomode));

    assertEquals(twomode, Networks.requireTwoMode(twomode));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireTwoMode(undirected));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireTwoMode(directed));

    assertEquals(twomode.asUndirectedGraph(), Networks.requireUndirectedGraph(twomode));
    assertEquals(undirected.asUndirectedGraph(), Networks.requireUndirectedGraph(undirected));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireUndirectedGraph(directed));

    assertEquals(directed.asDirectedGraph(), Networks.requireDirectedGraph(directed));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireDirectedGraph(undirected));
    assertThrows(IllegalArgumentException.class, () -> Networks.requireDirectedGraph(twomode));

    assertEquals(wTwoMode.getWeight(), Networks.requireLinkMapping(twomode, wTwoMode.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(twomode, Mappings.wrapUnmodifiableInt()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(twomode, wUndirected.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(twomode.asGraph(), Mappings.wrapUnmodifiableInt()));
    assertEquals(wUndirected.getWeight(),
        Networks.requireLinkMapping(undirected, wUndirected.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(undirected, Mappings.wrapUnmodifiableInt()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(undirected, wDirected.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(undirected.asGraph(), Mappings.wrapUnmodifiableInt()));
    assertEquals(wDirected.getWeight(),
        Networks.requireLinkMapping(directed, wDirected.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(directed, Mappings.wrapUnmodifiableInt()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(directed, wTwoMode.getWeight()));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireLinkMapping(directed.asGraph(), Mappings.wrapUnmodifiableInt()));

    ConstMapping.OfInt twomodeNodeMap = Mappings.repeated(0, twomode.countMonadicIndices());
    ConstMapping.OfInt directedNodeMap = Mappings.repeated(0, directed.countMonadicIndices());
    ConstMapping.OfInt undirectedNodeMap = Mappings.repeated(0, undirected.countMonadicIndices());

    assertEquals(twomodeNodeMap, Networks.requireVertexMapping(twomode, twomodeNodeMap));
    assertEquals(twomodeNodeMap, Networks.requireVertexMapping(twomode, twomodeNodeMap, "tests"));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(directed, twomodeNodeMap));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(directed, twomodeNodeMap, "tests"));
    assertEquals(directedNodeMap, Networks.requireVertexMapping(directed, directedNodeMap));
    assertEquals(directedNodeMap,
        Networks.requireVertexMapping(directed, directedNodeMap, "tests"));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(undirected, directedNodeMap));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(undirected, directedNodeMap, "tests"));
    assertEquals(undirectedNodeMap, Networks.requireVertexMapping(undirected, undirectedNodeMap));
    assertEquals(undirectedNodeMap,
        Networks.requireVertexMapping(undirected, undirectedNodeMap, "tests"));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(twomode, undirectedNodeMap));
    assertThrows(IllegalArgumentException.class,
        () -> Networks.requireVertexMapping(twomode, undirectedNodeMap, "tests"));

  }
}
