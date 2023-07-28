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

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.impl.algorithms.InteriorAlgorithms;
import ch.ethz.sn.visone3.roles.position.NetworkView;

import org.junit.jupiter.api.Test;

public class InteriorAlgorithmsTest {

  @Test
  public void testRegularInterior() {
    Network network = createNetwork();
    int n = network.asRelation().countUnionDomain();
    NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        InteriorAlgorithms.computeRegularInterior(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        InteriorAlgorithms.computeRegularInterior(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));

    Network network2 = createNetwork2();
    n = network2.asRelation().countUnionDomain();
    incomingView = NetworkView.fromNetworkRelation(network2, Direction.INCOMING);
    outgoingView = NetworkView.fromNetworkRelation(network2, Direction.OUTGOING);
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 5, 5, 4, 5, 5),
        InteriorAlgorithms.computeRegularInterior(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 5, 5, 4, 5, 5),
        InteriorAlgorithms.computeRegularInterior(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)));
  }

  @Test
  public void testExactInterior() {

    Network network = createNetwork();
    int n = network.asRelation().countUnionDomain();
    NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 0, 1, 1, 0, 0),
        InteriorAlgorithms.computeExactInterior(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 0, 1, 1, 0, 0),
        InteriorAlgorithms.computeExactInterior(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        InteriorAlgorithms.computeExactInterior(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        InteriorAlgorithms.computeExactInterior(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));

    Network network2 = createNetwork2();
    n = network2.asRelation().countUnionDomain();
    incomingView = NetworkView.fromNetworkRelation(network2, Direction.INCOMING);
    outgoingView = NetworkView.fromNetworkRelation(network2, Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 6, 7, 7, 6, 7, 7),
        InteriorAlgorithms.computeExactInterior(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 6, 7, 7, 6, 7, 7),
        InteriorAlgorithms.computeExactInterior(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)));
  }
  
	private Network createNetwork() {
    /*-
     * Constructs this network:
     * 0----2          6----8
     * |\  /|\        /|\  /|
     * | \/ | \4----5/ | \/ |
     * | /\ | /      \ | /\ |
     * |/  \|/        \|/  \|
     * 1----3          7----9
     */

		final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { 1, 1, z }, //
        { 1, 1, 1, z }, //
        { z, z, 1, 1, z }, //
        { z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, z } //
		};

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
		return s.getNetwork();
	}

	private Network createNetwork2() {
    /*-
     * Constructs this network:
     *           14----15
     *            |\  /|
     *            | \/ |
     *            | /\ |
     *            |/  \|
     * 0----2    13----6----8
     * |\  /|\     \  /|\  /|
     * | \/ | \4----5/ | \/ |
     * | /\ | /     /\ | /\ |
     * |/  \|/     /  \|/  \|
     * 1----3    10----7----9
     *            |\  /|
     *            | \/ |
     *            | /\ |
     *            |/  \|
     *           11----12
     */

		final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { 1, 1, z }, //
        { 1, 1, 1, z }, //
        { z, z, 1, 1, z }, //
        { z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, z }, //
        { z, z, z, z, z, 1, z, 1, z, z, z }, //
        { z, z, z, z, z, z, z, 1, z, z, 1, z }, //
        { z, z, z, z, z, z, z, 1, z, z, 1, 1, z }, //
        { z, z, z, z, z, 1, 1, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, 1, z, z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, z, 1, z, z, z, z, z, z, 1, 1, z } //
		};

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
		return s.getNetwork();
	}
}
