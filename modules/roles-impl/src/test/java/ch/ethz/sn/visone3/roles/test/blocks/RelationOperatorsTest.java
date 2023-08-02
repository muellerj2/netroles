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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;
import ch.ethz.sn.visone3.roles.test.structures.BinaryRelationsTest;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Random;
import java.util.function.BiPredicate;

public class RelationOperatorsTest {

  private void checkConstRoleOperator(RoleOperator<BinaryRelation> operator, BinaryRelation input,
      BinaryRelation result, boolean isotone, boolean constant, boolean nonincreasing,
      boolean nondecreasing, BinaryRelation smallerValue, BinaryRelation largerValue) {
    OperatorTestUtilities.checkRoleOperator(operator, input, result, isotone, constant,
        nonincreasing, nondecreasing, () -> {
        }, smallerValue, largerValue, BinaryRelations.infimum(smallerValue, result),
        BinaryRelations.infimum(largerValue, result),
        BinaryRelations.supremum(smallerValue, result),
        BinaryRelations.supremum(largerValue, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));
  }

  private void checkNonconstRoleOperator(RoleOperator<BinaryRelation> operator,
      BinaryRelation input, BinaryRelation result, boolean isotone, boolean constant,
      boolean nonincreasing, boolean nondecreasing, BinaryRelation smallerValue,
      BinaryRelation largerValue) {
    BinaryRelation interior = input;
    BinaryRelation prev;
    do {
      prev = interior;
      interior = BinaryRelations.infimum(interior, operator.relative(interior));
    } while (!prev.equals(interior));
    BinaryRelation closure = input;
    do {
      prev = closure;
      closure = BinaryRelations.supremum(closure, operator.relative(closure));
    } while (!prev.equals(closure));

    OperatorTestUtilities.checkRoleOperator(operator, input, result, isotone, constant,
        nonincreasing, nondecreasing, () -> {
        }, smallerValue, largerValue, BinaryRelations.infimum(smallerValue, result),
        BinaryRelations.infimum(largerValue, result),
        BinaryRelations.supremum(smallerValue, result),
        BinaryRelations.supremum(largerValue, result), interior, closure);
  }

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
  public void testStrongStructuralBinaryRelationBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(n, transposingOutgoingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(nWithLoops, incomingViewWithLoops)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops)).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

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

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { false, true, true, true, true, true, true, true, true, true, true, false, false,
                false, false }, //
            { false, true, true, true, true, true, true, true, true, true, true, false, false,
                false, false }, //
            { false, true, true, true, true, true, true, true, true, true, true, false, false,
                false, false }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, true }, //
        }), true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    BinaryRelation result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
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
        })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));
    result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
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
        })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, true, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, true, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
        .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
          return Math.abs(rshipi.getLeft() - rshipj.getLeft()) <= rshipi.getRight() + 1;
        })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    BinaryRelation weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0) {
                lhsworth = rshipi.getLeft() / 2;
              }
              if (rshipi.getLeft() == 0) {
                lhsworth = rshipi.getRight() / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0) {
                rhsworth = rshipj.getLeft() / 2;
              }
              if (rshipj.getLeft() == 0) {
                rhsworth = rshipj.getRight() / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .compPartial((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0) {
                lhsworth = rshipi.getLeft() / 2;
              }
              if (rshipi.getLeft() == 0) {
                lhsworth = rshipi.getRight() / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0) {
                rhsworth = rshipj.getLeft() / 2;
              }
              if (rshipj.getLeft() == 0) {
                rhsworth = rshipj.getRight() / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;

            }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, true, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, true, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.strongStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getLeft() - rshipj.getLeft()) <= rshipi.getRight() + 1;
            }))
        .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

  }

  @Test
  public void testWeakStructuralBinaryRelationBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weakStructural().of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weakStructural().of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n, transposingOutgoingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(nWithLoops, incomingViewWithLoops).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, false, false, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops)).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        BinaryRelations.fromMatrix(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, true, true, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }), true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));

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

    BinaryRelation weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, false, false, false,
            false }, //
        { true, true, true, true, true, true, true, true, true, true, true, false, false, false,
            false }, //
        { true, true, true, true, true, true, true, true, true, true, true, false, false, false,
            false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));
    weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
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
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { true, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
        .compPartial((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (rshipi.getRight() == 0) {
            lhsworth = rshipi.getLeft() / 2;
          }
          if (rshipi.getLeft() == 0) {
            lhsworth = rshipi.getRight() / 2;
          }
          int rhsworth = 0;
          if (rshipj.getRight() == 0) {
            rhsworth = rshipj.getLeft() / 2;
          }
          if (rshipj.getLeft() == 0) {
            rhsworth = rshipj.getRight() / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;

        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    weakResult = BinaryRelations.fromMatrix(new boolean[][] {
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { true, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, true, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, true, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            true, true } });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getLeft() - rshipj.getLeft()) <= rshipi.getRight() + 1;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    BinaryRelation result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
                lhsworth = (rshipi.getLeft() - 2) / 2;
              }
              if (rshipi.getLeft() == 0 && rshipi.getRight() >= 4) {
                lhsworth = (rshipi.getRight() - 2) / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
                rhsworth = (rshipj.getLeft() - 2) / 2;
              }
              if (rshipj.getLeft() == 0 && rshipj.getRight() >= 4) {
                rhsworth = (rshipj.getRight() - 2) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, false, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (rshipi.getRight() == 0 && rshipi.getLeft() >= 4) {
                lhsworth = (rshipi.getLeft() - 2) / 2;
              }
              if (rshipi.getLeft() == 0 && rshipi.getRight() >= 4) {
                lhsworth = (rshipi.getRight() - 2) / 2;
              }
              int rhsworth = 0;
              if (rshipj.getRight() == 0 && rshipj.getLeft() >= 4) {
                rhsworth = (rshipj.getLeft() - 2) / 2;
              }
              if (rshipj.getLeft() == 0 && rshipj.getRight() >= 4) {
                rhsworth = (rshipj.getRight() - 2) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

    result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, true, false, false, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, true, false, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, true, true, false, false, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, true, true, true, false, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false, false }, //
        { false, false, false, false, false, false, false, false, false, true, true, false, false,
            false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true, false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getLeft() - rshipj.getLeft()) <= rshipi.getRight() + 1;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)));

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

    BinaryRelation result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
        { false, false, true, true, true, false, false, true, true, true }, //
    });
    checkConstRoleOperator(RoleOperators.BINARYRELATION.weak().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    BinaryRelation actualResult = RoleOperators.BINARYRELATION.weak().of(n, incomingView).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().loose().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().of(n, incomingView).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().equitable().loose().of(n, incomingView)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().of(n, incomingView).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, false, false, true, false, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, false, false, true, false, false }, //
        { false, true, true, false, false, true, false, false, true, true, false }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, true, false, false, false, false, false, false, true }, //
        { false, true, false, false, false, false, false, false, false, false, false }, //
        { false, false, true, false, false, false, false, false, false, false, false }, //
        { true, false, false, true, false, false, false, false, false, false, true }, //
        { false, false, false, false, true, false, false, false, false, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, false, true, false, false, false, false }, //
        { false, false, false, false, true, true, false, true, false, false, false }, //
        { false, false, false, false, false, false, false, false, true, false, false }, //
        { false, false, false, false, false, false, false, false, false, true, false }, //
        { true, false, false, true, false, false, false, false, false, false, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), outgoingView2)
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    BinaryRelation result4 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, false, true, false, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, false, true, false, false, true, false }, //
        { false, false, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, false, false, true, true, false }, //
        { false, false, false, false, false, true, true, false, true, true, false }, //
        { true, true, true, true, true, true, false, false, false, false, true }, //
    });
    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), outgoingView2)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak()
            .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.BINARYRELATION.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());
  }

  @Test
  public void testWeaklyEquitableEquivalenceBlocks() {
    Network network = createNetwork();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    BinaryRelation result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { false, true, true, false, false, true, true, false, false, false, false }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
    });

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    BinaryRelation actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable()
            .of(n, (TransposableNetworkView<?, ?>) swappingOutgoingView(network)).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { false, true, true, false, true, true, true, false, true, true, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
    });

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, true }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false, true }, //
        { false, true, true, false, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { true, true, true, true, false, false, false, false, false, false, true }, //
    });

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    BinaryRelation result4 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, true }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false, true }, //
        { false, false, true, false, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, false, true, false, false, false, false }, //
        { false, false, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, false, false, true, true, false }, //
        { false, false, false, false, false, true, true, false, true, true, false }, //
        { true, true, true, true, false, false, false, false, false, false, true }, //
    });

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().of(n, swappingOutgoingView(network))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable()
        .of(n, swappingOutgoingView(network))
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

  }

  @Test
  public void test2WeaklyEquitableEquivalenceBlocks() {
    Network network = createNetwork();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    BinaryRelation result = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, false, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
    });

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    BinaryRelation actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().strictness(2)
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().strictness(2)
            .of(n, (TransposableNetworkView<?, ?>) swappingOutgoingView(network)).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { false, true, true, false, true, true, true, false, true, true, false }, //
        { false, true, true, false, true, true, true, false, true, true, false }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { true, true, true, true, true, true, true, true, true, true, true }, //
    });

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weak().strictness(2)
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, true, true, false, true, true, false, false, false, false, false }, //
        { false, true, true, false, true, true, false, false, false, false, false }, //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, true, true, false, true, true, false, false, false, false, false }, //
        { false, true, true, false, false, true, false, false, false, false, false }, //
        { false, true, true, false, false, true, true, false, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, true, true, true, true, true, true, false }, //
        { true, true, true, true, true, true, false, false, false, false, true }, //
    });

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().strictness(2)
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    BinaryRelation result4 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false, false }, //
        { true, true, true, true, true, true, false, false, false, false, true }, //
        { false, false, true, false, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, false, true, false, false, true, false }, //
        { false, false, true, false, true, true, true, true, true, true, false }, //
        { false, true, true, false, false, true, false, false, true, true, false }, //
        { false, false, false, false, false, true, true, false, true, true, false }, ///
        { true, true, true, true, true, true, false, false, false, false, true }, //
    });

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2).of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getLeft() - rshipj.getLeft()) < 2).make()
        .apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.BINARYRELATION.weak().strictness(2).of(n, swappingOutgoingView(network))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result4, true, true, false, false,
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.BINARYRELATION.weaklyEquitable().strictness(2)
        .of(n, swappingOutgoingView(network))
        .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
        .make().apply(BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result4::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.hashCode(), actualResult.hashCode());
    assertEquals(result4.toString(), actualResult.toString());

    assertThrows(IllegalArgumentException.class,
        () -> RoleOperators.BINARYRELATION.weak().strictness(0));
  }

  @Test
  public void testBasicOperators() {

    final int size = 30;
    final Random rand = new Random();
    final RelationBuilder<? extends BinaryRelation> builder1 = RelationBuilders
        .denseRelationBuilder(size);
    final RelationBuilder<? extends BinaryRelation> builder2 = RelationBuilders
        .denseRelationBuilder(size);
    final RelationBuilder<? extends BinaryRelation> builder3 = RelationBuilders
        .denseRelationBuilder(size);

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder1.add(i, j);
        }
        if (rand.nextBoolean()) {
          builder2.add(i, j);
        }
        if (rand.nextInt(4) == 0) {
          builder3.add(i, j);
        }
      }
    }

    final BinaryRelation binrel1 = builder1.build();
    final BinaryRelation binrel2 = builder2.build();
    final BinaryRelation binrel3 = builder3.build();

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().forward(), binrel1, binrel1, true, false,
        true, true, () -> {
        }, binrel2, binrel3, BinaryRelations.infimum(binrel1, binrel2),
        BinaryRelations.infimum(binrel1, binrel3), BinaryRelations.supremum(binrel1, binrel2),
        BinaryRelations.supremum(binrel1, binrel3), binrel1, binrel1);

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().invert(), binrel1, binrel1.invert(),
        true, false, false, false, () -> {
        }, binrel2, binrel3, BinaryRelations.infimum(binrel1.invert(), binrel2),
        BinaryRelations.infimum(binrel1.invert(), binrel3),
        BinaryRelations.supremum(binrel1.invert(), binrel2),
        BinaryRelations.supremum(binrel1.invert(), binrel3),
        BinaryRelations.infimum(binrel1, binrel1.invert()),
        BinaryRelations.supremum(binrel1, binrel1.invert()));

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().produceConstant(binrel1), binrel2,
        binrel1, true, true, false, false, () -> {
        }, binrel2, binrel3, BinaryRelations.infimum(binrel1, binrel2),
        BinaryRelations.infimum(binrel1, binrel3), BinaryRelations.supremum(binrel1, binrel2),
        BinaryRelations.supremum(binrel1, binrel3), BinaryRelations.infimum(binrel1, binrel2),
        BinaryRelations.supremum(binrel1, binrel2));

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().meetWithConstant(binrel1), binrel2,
        BinaryRelations.infimum(binrel1, binrel2), true, false, true, false, () -> {
        }, binrel3, binrel3.invert(),
        BinaryRelations.infimum(BinaryRelations.infimum(binrel1, binrel2), binrel3),
        BinaryRelations.infimum(BinaryRelations.infimum(binrel1, binrel2), binrel3.invert()),
        BinaryRelations.supremum(BinaryRelations.infimum(binrel1, binrel2), binrel3),
        BinaryRelations.supremum(BinaryRelations.infimum(binrel1, binrel2), binrel3.invert()),
        BinaryRelations.infimum(binrel1, binrel2), binrel2);

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().joinWithConstant(binrel1), binrel2,
        BinaryRelations.supremum(binrel1, binrel2), true, false, false, true, () -> {
        }, binrel3, binrel3.invert(),
        BinaryRelations.infimum(binrel3, BinaryRelations.supremum(binrel1, binrel2)),
        BinaryRelations.infimum(binrel3.invert(), BinaryRelations.supremum(binrel1, binrel2)),
        BinaryRelations.supremum(binrel3, BinaryRelations.supremum(binrel1, binrel2)),
        BinaryRelations.supremum(binrel3.invert(), BinaryRelations.supremum(binrel1, binrel2)),
        binrel2, BinaryRelations.supremum(binrel1, binrel2));

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().symmetrize(), binrel1,
        BinaryRelations.infimum(binrel1, binrel1.invert()), true, false, true, false, () -> {
        }, binrel2, binrel3,
        BinaryRelations.infimum(binrel2, BinaryRelations.infimum(binrel1, binrel1.invert())),
        BinaryRelations.infimum(binrel3, BinaryRelations.infimum(binrel1, binrel1.invert())),
        BinaryRelations.supremum(binrel2, BinaryRelations.infimum(binrel1, binrel1.invert())),
        BinaryRelations.supremum(binrel3, BinaryRelations.infimum(binrel1, binrel1.invert())),
        BinaryRelations.infimum(binrel1, binrel1.invert()), binrel1);

    checkRoleOperator(RoleOperators.BINARYRELATION.basic().closeTransitively(), binrel3,
        BinaryRelations.closeTransitively(binrel3), true, false, false, true, () -> {
        }, binrel1, binrel2,
        BinaryRelations.infimum(binrel1, BinaryRelations.closeTransitively(binrel3)),
        BinaryRelations.infimum(binrel2, BinaryRelations.closeTransitively(binrel3)),
        BinaryRelations.supremum(binrel1, BinaryRelations.closeTransitively(binrel3)),
        BinaryRelations.supremum(binrel2, BinaryRelations.closeTransitively(binrel3)), binrel3,
        BinaryRelations.closeTransitively(binrel3));
  }

  @Test
  public void testRegularBinaryRelationBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    BinaryRelation result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
    });
    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });
    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().equitable().loose().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().loose().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n2, outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n2, swappingOutgoingView(network2)).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { true, true, true, true, true, true, true, true, false, false, true, true, false, false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, swappingOutgoingView(network2)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });

    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
        { false, false, true, true, true, true, true, true, true, true }, //
    });

    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, swappingOutgoingView(network2)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    // TODO test BiPredicate

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, true, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, true, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, true, true, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().of(n2, outgoingView2)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular()
        .of(n2, swappingOutgoingView(network2)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

  }

  @Test
  public void testEquitableBinaryRelationBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    BinaryRelation result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
    });
    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false }, //
        { false, true, false, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
    });

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().loose().equitable().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().equitable().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n2, outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().of(n2, swappingOutgoingView(network2)).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false }, //
        { false, true, false, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n2, outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, swappingOutgoingView(network2)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, false, false, false, false, false, false, false, false }, //
        { true, true, false, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false }, //
        { false, true, false, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, swappingOutgoingView(network2)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, false, false, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, false, false, false, false, false, false, false }, //
        { false, true, false, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, false, false, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, true, false, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, false,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n, incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().of(n2, outgoingView2)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, false, false, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, false, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, true, true, true, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, false, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable()
        .of(n2, swappingOutgoingView(network2)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));
  }

  @Test
  public void test2EquitableBinaryRelationBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    BinaryRelation result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
        { true, true, true, true, true, true, true, true, true, true }, //
    });
    BinaryRelation result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    BinaryRelation result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().strictness(2).of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().loose().equitable().strictness(2)
            .of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().equitable().strictness(2).of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().strictness(2).of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().strictness(2).of(n2, outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().strictness(2)
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().strictness(2)
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().strictness(2)
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().strictness(2).of(n, swappingOutgoingView(network))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.regular().strictness(2).of(n, swappingOutgoingView(network))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.BINARYRELATION.equitable().strictness(2)
            .of(n2, swappingOutgoingView(network2)).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
        { false, false, false, false, true, true, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n2, swappingOutgoingView(network2)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, true, false, false, false, false, false, false }, //
        { false, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n2, outgoingView2).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
        { false, false, false, false, false, true, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
        { false, false, false, false, false, false, true, true, true, true, true, true, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n2, swappingOutgoingView(network2)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, true, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false, false, false, false, false, false, false }, //
        { false, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, true, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, true, true, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, false, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, incomingView).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, incomingView).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n2, outgoingView2).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false }, //
        { true, true, true, false, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, true, true, false, false, false, false, false, false }, //
        { false, false, false, false, true, false, false, false, false, false }, //
        { false, false, false, false, false, true, true, false, false, false }, //
        { false, false, false, false, false, false, true, true, false, false }, //
        { false, false, false, false, false, false, true, true, true, false }, //
        { false, false, false, false, false, false, true, true, true, true }, //
        { false, false, false, false, false, false, true, true, true, true }, //
    });
    result3 = BinaryRelations.fromMatrix(new boolean[][] { //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, false, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, false, false, false, false, false, false, false, false, false,
            false }, //
        { true, true, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, true, true, true, false, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, true, false, false, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, false, false, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, false, false, false,
            false }, //
        { false, false, false, false, false, false, true, true, true, true, true, false, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, true, true, false,
            false }, //
        { false, false, false, false, false, false, false, false, false, false, false, true, true,
            true }, //
        { false, false, false, false, false, false, false, false, false, false, false, false, true,
            true }, //
    });
    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.equitable().strictness(2)
        .of(n, swappingOutgoingView(network)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(n2, swappingOutgoingView(network2)).compPredicate((rshipi, rshipj) -> {
          return Math.abs(Math.max(rshipi.getLeft(), rshipi.getRight())
              - Math.max(rshipj.getLeft(), rshipj.getRight())) < 2;
        }).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));
  }

}
