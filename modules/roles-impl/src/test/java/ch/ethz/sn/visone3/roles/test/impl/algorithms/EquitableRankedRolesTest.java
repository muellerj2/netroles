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

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.impl.algorithms.EquitableRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

import org.junit.jupiter.api.Test;

public class EquitableRankedRolesTest {

  @Test
  public void testRelativeExactRankedRoles() {
    Network network = createNetwork();

    int n = network.asRelation().countUnionDomain();
    NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);

    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, incomingView, BinaryRelations.universal(n)));
    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, outgoingView, BinaryRelations.universal(n)));

    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, false, false, false, false, false, false },
                { true, true, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, true, false, false, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, incomingView,
            BinaryRelations.fromMatrix(new boolean[][] {
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, })));
    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, false, false, false, false, false, false },
                { true, true, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, true, false, false, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, outgoingView,
            BinaryRelations.fromMatrix(new boolean[][] {
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true },
                { true, true, true, true, true, true, true, true, true, true }, })));

    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, incomingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, false, false, true, true, false, false },
                    { false, false, true, true, false, false, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, false, false, true, true, false, false },
                    { false, false, true, true, false, false, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));
    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, true, true, false, false, true, true, false, false },
                { true, true, false, false, true, true, false, false, true, true },
                { true, true, false, false, true, true, false, false, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, outgoingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, false, false, true, true, false, false },
                    { false, false, true, true, false, false, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, false, false, true, true, false, false },
                    { false, false, true, true, false, false, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));

    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, false, true, false, false, false, true, false, false },
                { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, false, true, false, false, false, true, false, false },
                { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, incomingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, false, false, false, true, false, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, false, false, false, true, false, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));
    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, false, true, false, false, false, true, false, false },
                { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true },
                { false, false, true, true, false, false, true, true, false, false },
                { false, false, false, true, false, false, false, true, false, false },
                { true, true, false, true, true, true, false, true, true, true },
                { true, true, false, true, true, true, false, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, outgoingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, false, false, false, true, false, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, false, false, false, true, false, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));

    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, false, false, true, false, false, false, false, false },
                { false, false, false, false, false, true, false, false, false, false },
                { false, false, false, false, false, false, true, true, false, false },
                { false, false, false, false, false, false, true, true, false, false },
                { false, false, false, false, false, true, true, true, true, true },
                { false, false, false, false, false, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, incomingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, true, false, false, false, false, false },
                    { false, false, true, true, true, false, false, false, false, false },
                    { false, false, false, false, true, false, false, false, false, false },
                    { false, false, false, false, false, true, false, false, false, false },
                    { false, false, false, false, false, true, true, true, false, false },
                    { false, false, false, false, false, true, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));
    assertEquals(
        BinaryRelations.fromMatrix(
            new boolean[][] { { true, true, true, true, true, false, false, false, false, false },
                { true, true, true, true, true, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, true, true, false, false, false, false, false, false },
                { false, false, false, false, true, false, false, false, false, false },
                { false, false, false, false, false, true, false, false, false, false },
                { false, false, false, false, false, false, true, true, false, false },
                { false, false, false, false, false, false, true, true, false, false },
                { false, false, false, false, false, true, true, true, true, true },
                { false, false, false, false, false, true, true, true, true, true }, }),
        EquitableRankedRoles.rankedExactRoles(n, outgoingView,
            BinaryRelations.fromMatrix(
                new boolean[][] { { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true },
                    { false, false, true, true, true, false, false, false, false, false },
                    { false, false, true, true, true, false, false, false, false, false },
                    { false, false, false, false, true, false, false, false, false, false },
                    { false, false, false, false, false, true, false, false, false, false },
                    { false, false, false, false, false, true, true, true, false, false },
                    { false, false, false, false, false, true, true, true, false, false },
                    { true, true, true, true, true, true, true, true, true, true },
                    { true, true, true, true, true, true, true, true, true, true }, })));
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
        .fromAdjacency(adj, false);
    return s.getNetwork();
  }

}
