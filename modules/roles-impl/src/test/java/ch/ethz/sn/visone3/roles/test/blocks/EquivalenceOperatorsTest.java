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
package ch.ethz.sn.visone3.roles.test.blocks;

import static ch.ethz.sn.visone3.roles.test.blocks.OperatorTestUtilities.checkRoleOperator;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class EquivalenceOperatorsTest {

  private Network createNetwork() {
    /*-
     * Constructs this network:
     *             8
     *             |       9
     *       +-----7----+ /|
     *       |          |/ |
     *       5 +---1--+ 6  |
     *       | |      | |\ |
     *       +-2--11--3-+ \|
     *         |      |   10
     *         +---4--+
     */

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { 1, z, z }, //
        { z, 1, 1, z }, //
        { z, 1, z, z, z }, //
        { z, z, 1, z, z, z }, //
        { z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, z, z, z }, //
        { z, z, z, z, z, 1, z, z, 1, z }, //
        { z, 1, 1, z, z, z, z, z, z, z, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, false);
    return s.getNetwork();
  }

  private Network createNetworkWithLoops() {
    /*-
     * Constructs this network:
     *             8
     *             |       9
     *       +-----7----+ /|
     *       |          |/ |
     *       5 +---1--+ 6  |
     *       | | +--+ | |\ |
     *       | | |  | | | \|
     *       +-2--11--3-+ 10
     *         |      |
     *         +---4--+
     *            | |
     *            +-+
     */

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { 1, z, z }, //
        { z, 1, 1, 1 }, //
        { z, 1, z, z, z }, //
        { z, z, 1, z, z, z }, //
        { z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, z, z, z }, //
        { z, z, z, z, z, 1, z, z, 1, z }, //
        { z, 1, 1, z, z, z, z, z, z, z, 1 } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, false);
    return s.getNetwork();
  }

  private Network createNetwork2() {
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

  private Network createNetwork3() {

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
        { z, z, z, z, z, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, 1, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, 1, 1, 1, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }

  private static TransposableNetworkView<Relationship, Relationship> swappingOutgoingView(
      Network network) {
    return new TransposableNetworkView<Relationship, Relationship>() {

      private Relation rel = network.asRelation();

      @Override
      public Iterable<? extends Relationship> ties(int lhsComparison, int rhsComparison, int node) {
        return rel.getRelationshipsFrom(node);
      }

      @Override
      public int tieTarget(int lhsComparison, int rhsComparison, int node, Relationship tie) {
        int target = tie.getRight();
        if (node == rhsComparison) {
          if (target == lhsComparison) {
            return rhsComparison;
          } else if (target == rhsComparison) {
            return lhsComparison;
          }
        }
        return target;
      }

      @Override
      public int tieIndex(int lhsComparison, int rhsComparison, int node, Relationship tie) {
        return tie.getIndex();
      }

      @Override
      public int countTies(int lhsComparison, int rhsComparison, int node) {
        return rel.countRelationshipsFrom(node);
      }
    };
  }

  @Test
  public void testStrongStructuralEquivalenceBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    checkRoleOperator(RoleOperators.EQUIVALENCE.strongStructural().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural().of(n, transposingOutgoingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 6, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural().of(nWithLoops, incomingViewWithLoops).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 3), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    final Integer z = null;
    final Integer[][] adj = { //
        { z }, //
        { 1, z }, //
        { 1, 2, z }, //
        { 1, 2, z, z }, //
        { 1, 2, z, 3, z }, //
        { 1, 2, z, z, z, z }, //
        { 1, z, z, z, z, z, 1 }, //
        { 1, z, z, z, z, z, 1, 1 }, //
        { 1, z, z, z, z, z, z, z, z }, //
        { 1, z, z, z, z, z, z, z, z, z }, //
        { 1, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z, 1 }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z, z, 1 }, //
    };
    Network network2 = MatrixSource.fromAdjacency(adj, false).getNetwork();
    NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);
    int n2 = network2.countMonadicIndices();

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 2, 5, 5, 6, 6, 6, 7, 7, 8, 9), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 2, 4, 4, 3, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
        .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
            lhsworth = (rshipi.getLeft() - 2) / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
            rhsworth = (rshipj.getLeft() - 2) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 11), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
        .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
            lhsworth = (rshipi.getLeft() - 2) / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
            rhsworth = (rshipj.getLeft() - 2) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 11), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0) {
                lhsworth = rshipi.getLeft() / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0) {
                rhsworth = rshipj.getLeft() / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 10), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0) {
                lhsworth = rshipi.getLeft() / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0) {
                rhsworth = rshipj.getLeft() / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 10), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
  }

  @Test
  public void testWeakStructuralEquivalenceBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().of(n, incomingView, outgoingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 6, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().unidirectional().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 6, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView,
                (TransposableNetworkView<?, ?>) outgoingView)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 6, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().unidirectional()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 6, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);
    TransposableNetworkView<?, ?> transposingOutgoingView2 = swappingOutgoingView(network);
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural()
            .of(n, transposingOutgoingView, transposingOutgoingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));
    checkRoleOperator(RoleOperators.EQUIVALENCE.weakStructural()
            .of(n, transposingOutgoingView, transposingOutgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().unidirectional().of(n, transposingOutgoingView)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    final NetworkView<?, ?> outgoingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.OUTGOING);
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural()
            .of(nWithLoops, incomingViewWithLoops, outgoingViewWithLoops).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 3), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops),
                swappingOutgoingView(networkWithLoops))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2));

    final Integer z = null;
    final Integer[][] adj = { //
        { z }, //
        { 1, z }, //
        { 1, 2, z }, //
        { 1, 2, z, z }, //
        { 1, 2, z, 3, z }, //
        { 1, 2, z, z, z, z }, //
        { 1, z, z, z, z, z, 1 }, //
        { 1, z, z, z, z, z, 1, 1 }, //
        { 1, z, z, z, z, z, z, z, z }, //
        { 1, z, z, z, z, z, z, z, z, z }, //
        { 1, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z, 1 }, //
        { z, z, z, z, z, z, z, z, z, z, z, z, z, z, 1 }, //
    };
    Network network2 = MatrixSource.fromAdjacency(adj, false).getNetwork();
    NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);
    int n2 = network2.countMonadicIndices();

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2, outgoingView2)
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 2, 4, 4, 5, 5, 5, 6, 6, 7, 7), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 2, 4, 4, 3, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2, outgoingView2)
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0) {
                lhsworth = rshipi.getLeft() / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0) {
                rhsworth = rshipj.getLeft() / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 10), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weakStructural()
        .of(n2, outgoingView2,
            NetworkView.fromNetworkRelation(network2, Direction.INCOMING))
        .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0) {
            lhsworth = rshipi.getLeft() / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0) {
            rhsworth = rshipj.getLeft() / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 10), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weakStructural()
        .of(n2, outgoingView2,
            NetworkView.fromNetworkRelation(network2, Direction.INCOMING))
        .compPartial((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0) {
            lhsworth = rshipi.getLeft() / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0) {
            rhsworth = rshipj.getLeft() / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;

        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 10), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 11),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 12),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weakStructural()
        .of(n2, swappingOutgoingView(network2), swappingOutgoingView(network2))
        .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
            lhsworth = (rshipi.getLeft() - 2) / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
            rhsworth = (rshipj.getLeft() - 2) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 11), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weakStructural()
        .of(n2, swappingOutgoingView(network2), swappingOutgoingView(network2))
        .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
            lhsworth = (rshipi.getLeft() - 2) / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
            rhsworth = (rshipj.getLeft() - 2) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 10, 11), true, true,
        false, false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 10, 11, 12, 13),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

  }

  @Test
  public void testWeakEquivalenceBlocks() {

    final Integer z = null;
    final Integer[][] adj = { //
        { z }, //
        { z, z }, //
        { z, z, z }, //
        { z, z, 1, 1 }, //
        { z, z, z, z, 1 }, //
        { z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, z }, //
        { z, z, z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, z, z, 1, 1, z }, //
    };

    Network network = MatrixSource.fromAdjacency(adj, DyadType.UNDIRECTED).getNetwork();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final int n = network.countMonadicIndices();

    checkRoleOperator(RoleOperators.EQUIVALENCE.weak().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 1, 2, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak().of(n, (TransposableNetworkView<?, ?>) incomingView)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 1, 2, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(RoleOperators.EQUIVALENCE.weak().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 1, 2, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weaklyEquitable().loose().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 1, 2, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak().equitable().loose().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 1, 2, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 2, 3, 5, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 2, 3, 0, 2, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 2, 2, 2, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(RoleOperators.EQUIVALENCE.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 2, 3, 5, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 2, 3, 0, 2, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 2, 2, 2, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 2, 3, 5, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 2, 3, 0, 2, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 2, 2, 2, 3, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));
    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weak()
            .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 0, 3, 4, 5, 6, 7, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0));

    assertThrows(UnsupportedOperationException.class, () -> RoleOperators.EQUIVALENCE.weak()
        .of(network2.countMonadicIndices(), outgoingView2).compPredicate((rshipi, rshipj) -> true));
  }

  @Test
  public void testWeaklyEquitableEquivalenceBlocks() {
    Network network = createNetwork();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 1, 2, 3, 0, 0, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 1, 4, 5, 0, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 2, 3, 4, 5, 6, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weaklyEquitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 1, 2, 3, 0, 0, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 1, 4, 5, 0, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 2, 3, 4, 5, 6, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.weaklyEquitable().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 0, 1, 2, 3, 0, 0, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 1, 4, 5, 0, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 2, 3, 4, 5, 6, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 3, 4, 5, 6, 6, 0), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0));
  }

  @Test
  public void testBasicOperators() {

    checkRoleOperator(RoleOperators.EQUIVALENCE.basic().forward(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5), true, false, true, true,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 6, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 1, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 0, 2, 1, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.basic()
            .produceConstant(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5)),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 0, 0, 1, 2, 3, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5), true, true, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 6, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 1, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 0, 2, 1, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 0, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.basic()
            .meetWithConstant(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 0, 2, 1, 2, 2, 3)),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 2, 1, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5), true, false, true, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 6, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 1, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 0, 2, 1, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 2, 1, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.basic()
            .joinWithConstant(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 3, 0, 4, 5, 6, 7, 8)),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 4, 5, 3, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5), true, false, false, true,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 6, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 4, 5),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 1, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 0, 2, 1, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 4, 5, 3, 6, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 1, 0, 3, 2, 4, 4, 5));
  }

  @Test
  public void testRegularEquivalenceBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 3, 4, 5, 6, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().equitable().loose().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 3, 4, 5, 6, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().loose().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 3, 4, 5, 6, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n2, outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 2, 3, 3, 2, 2, 3, 3), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 3, 4, 5, 6, 7, 4, 5, 6, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 3, 4, 5, 6, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 2, 3, 3, 2, 2, 3, 3), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 3, 4, 5, 6, 7, 4, 5, 6, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 1, 1, 1), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 3, 3, 4, 5, 6, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 3, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n2, swappingOutgoingView(network2)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 2, 2, 3, 3, 2, 2, 3, 3), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 3, 4, 5, 6, 7, 4, 5, 6, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 3, 4, 4, 3, 3, 4, 4), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 3, 4, 4, 3, 3, 4, 4), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 3, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n2, swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 3, 4, 4, 3, 3, 4, 4), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 2, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.regular().of(n2, outgoingView2).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 4, 5, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 4, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 4, 5, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 4, 5, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 3, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 3, 4, 4, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 4, 5, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    assertThrows(UnsupportedOperationException.class, () -> RoleOperators.EQUIVALENCE.regular()
        .of(n, incomingView).compPredicate((i, j) -> true));
  }

  @Test
  public void testEquitableEquivalenceBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.regular().equitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().loose().equitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n2, outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 2, 3, 3, 4, 5, 3, 3, 4, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 1, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 2, 3, 3, 4, 5, 3, 3, 4, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 1, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n2, swappingOutgoingView(network2)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 2, 3, 3, 4, 4, 3, 3, 4, 4), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 1, 4, 5, 6, 7, 8, 5, 6, 7, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n2, swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 4, 5, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(
        RoleOperators.EQUIVALENCE.equitable().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 4, 4), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 6, 7, 4),
        Mappings.wrapUnmodifiableInt(0, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 2, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 3, 3, 2, 2), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 3, 3), true, false, false, false,
        () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 4, 5, 5, 6, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 2, 2, 2, 2, 2));

    checkRoleOperator(RoleOperators.EQUIVALENCE.equitable().of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 5, 4, 4, 5, 5), true, false, false,
        false, () -> {
        }, Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2),
        Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 10),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 3, 4, 4, 5, 6, 4, 4, 5, 6),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2));

    assertThrows(UnsupportedOperationException.class, () -> RoleOperators.EQUIVALENCE.equitable()
        .of(n, incomingView).compPredicate((i, j) -> true));
  }
}
