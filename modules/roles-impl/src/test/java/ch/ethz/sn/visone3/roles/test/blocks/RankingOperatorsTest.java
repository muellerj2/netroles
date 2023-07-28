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
import ch.ethz.sn.visone3.roles.impl.structures.BiIntPredicate;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;
import ch.ethz.sn.visone3.roles.test.structures.BinaryRelationsTest;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class RankingOperatorsTest {

  private void checkConstRoleOperator(RoleOperator<Ranking> operator, Ranking input, Ranking result,
      boolean isotone, boolean constant, boolean nonincreasing, boolean nondecreasing,
      Ranking smallerValue, Ranking largerValue) {
    OperatorTestUtilities.checkRoleOperator(operator, input, result, isotone, constant,
        nonincreasing, nondecreasing, () -> {
        }, smallerValue, largerValue, Rankings.infimum(smallerValue, result),
        Rankings.infimum(largerValue, result), Rankings.supremum(smallerValue, result),
        Rankings.supremum(largerValue, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));
  }

  private void checkNonconstRoleOperator(RoleOperator<Ranking> operator, Ranking input,
      Ranking result, boolean isotone, boolean constant, boolean nonincreasing,
      boolean nondecreasing, Ranking smallerValue, Ranking largerValue) {
    Ranking interior = input;
    Ranking prev;
    do {
      prev = interior;
      interior = Rankings.infimum(interior, operator.relative(interior));
    } while (!prev.equals(interior));
    Ranking closure = input;
    do {
      prev = closure;
      closure = Rankings.supremum(closure, operator.relative(closure));
    } while (!prev.equals(closure));

    OperatorTestUtilities.checkRoleOperator(operator, input, result, isotone, constant,
        nonincreasing, nondecreasing, () -> {
        }, smallerValue, largerValue, Rankings.infimum(smallerValue, result),
        Rankings.infimum(largerValue, result), Rankings.supremum(smallerValue, result),
        Rankings.supremum(largerValue, result), interior, closure);
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
  public void testStrongStructuralRankingBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);

    checkRoleOperator(RoleOperators.RANKING.strongStructural().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.strongStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);

    checkRoleOperator(
        RoleOperators.RANKING.strongStructural().of(n, transposingOutgoingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    checkRoleOperator(
        RoleOperators.RANKING.strongStructural().of(nWithLoops, incomingViewWithLoops).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.strongStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

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
        RoleOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        }), true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false, false,
                false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true, false,
                false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true, true, false,
                false, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                true, true, true }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, true }, //
        }), Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, true, true, true, false, false, false,
                false, false }, //
            { false, false, false, false, false, false, true, true, true, true, false, false, false,
                false, false }, //
            { false, false, false, false, false, false, false, false, false, false, true, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, false, false, false, true, true,
                false, false, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                true, true, true }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, true }, //
        }), Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false, false,
                false, false, false }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
        }), Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { true, true, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { true, true, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { true, true, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { true, true, true, true, true, true, false, false, false, false, false, false, false,
                false, false }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
        }), Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, true, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false, false,
                false, false, false }, //
            { false, false, false, false, false, false, false, true, true, true, true, false, false,
                false, false }, //
            { false, false, false, false, false, false, false, true, true, true, true, false, false,
                false, false }, //
            { false, false, false, false, false, false, false, true, true, true, true, false, false,
                false, false }, //
            { false, false, false, false, false, false, false, true, true, true, true, true, false,
                false, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                true, false, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, true }, //
        }), Rankings.fromMatrixUnsafe(new boolean[][] { //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                true }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, true }, //
            { false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, true }, //
        }));

    Ranking result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkRoleOperator(RoleOperators.RANKING.strongStructural().of(n2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)), result),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            result));
    result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkRoleOperator(RoleOperators.RANKING.strongStructural().of(n2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)), result),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            result));

    Ranking weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(
        RoleOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))

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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(
        RoleOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));
  }

  @Test
  public void testWeakStructuralRankingBlocks() {

    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().of(n, incomingView, outgoingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().unidirectional().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().of(n, incomingView, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.weakStructural()
            .of(n, (TransposableNetworkView<?, ?>) incomingView,
                (TransposableNetworkView<?, ?>) outgoingView)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().unidirectional()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    TransposableNetworkView<?, ?> transposingOutgoingView = swappingOutgoingView(network);
    TransposableNetworkView<?, ?> transposingOutgoingView2 = swappingOutgoingView(network);
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural()
            .of(n, transposingOutgoingView, transposingOutgoingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural()
            .of(n, transposingOutgoingView, transposingOutgoingView2).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().unidirectional().of(n, transposingOutgoingView)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { true, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));

    final Network networkWithLoops = createNetworkWithLoops();
    final int nWithLoops = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.INCOMING);
    final NetworkView<?, ?> outgoingViewWithLoops = NetworkView
        .fromNetworkRelation(networkWithLoops, Direction.OUTGOING);
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural()
            .of(nWithLoops, incomingViewWithLoops, outgoingViewWithLoops).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, true, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, true, true, true, true, true, false, false, true }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, false, false, true, false, false, true, true, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { false, false, false, false, true, true, true, true, true, true, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, true, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
            }));
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural()
            .of(nWithLoops, swappingOutgoingView(networkWithLoops),
                swappingOutgoingView(networkWithLoops))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
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
        }), true, true, false, false, () -> {
        }, Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, false, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, true, false, false, false, false, false, true }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, true, true, true, false, true, false, false, true }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, true, true, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { false, true, true, false, false, false, false, false, false, false, false }, //
            { true, false, false, true, false, false, false, false, false, false, true }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, false, false, false, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, true, true, true, true, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(new boolean[][] {
            { true, false, false, true, false, false, false, false, false, false, false }, //
            { false, true, false, false, false, false, false, false, false, false, false }, //
            { false, false, true, false, false, false, false, false, false, false, false }, //
            { false, false, false, true, false, false, false, false, false, false, false }, //
            { false, false, false, false, true, false, false, false, false, false, false }, //
            { false, false, false, false, false, true, false, false, false, false, false }, //
            { false, false, false, false, false, false, true, false, false, false, false }, //
            { false, false, false, false, false, false, false, true, false, false, false }, //
            { false, false, false, false, false, false, false, false, true, false, false }, //
            { false, false, false, false, false, false, false, false, false, true, false }, //
            { false, false, false, false, false, false, false, false, false, false, true }, //
        }),
        Rankings.fromMatrixUnsafe(
            new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { true, true, true, true, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, false, false, false, false }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
                { false, false, false, false, true, true, true, true, true, true, true }, //
            }));

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

    Ranking weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(
        RoleOperators.RANKING.weakStructural().of(n2, outgoingView2, outgoingView2)
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> 0)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));
    weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(RoleOperators.RANKING.weakStructural().of(n2, outgoingView2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    checkRoleOperator(RoleOperators.RANKING.weakStructural().unidirectional().of(n2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(RoleOperators.RANKING.weakStructural()
        .of(n2, outgoingView2,
            NetworkView.fromNetworkRelation(network2, Direction.INCOMING))
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    weakResult = Rankings.fromMatrixUnsafe(new boolean[][] {
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
    checkRoleOperator(RoleOperators.RANKING.weakStructural()
        .of(n2, outgoingView2,
            NetworkView.fromNetworkRelation(network2, Direction.INCOMING))
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    checkRoleOperator(RoleOperators.RANKING.weakStructural().of(n2, outgoingView2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    checkRoleOperator(RoleOperators.RANKING.weakStructural().unidirectional().of(n2, outgoingView2)
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        weakResult, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
            weakResult),
        Rankings.infimum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            weakResult));

    Ranking result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkRoleOperator(RoleOperators.RANKING.weakStructural()
        .of(n2, swappingOutgoingView(network2), swappingOutgoingView(network2))
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)), result),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            result));

    result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkRoleOperator(RoleOperators.RANKING.weakStructural()
        .of(n2, swappingOutgoingView(network2), swappingOutgoingView(network2))
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
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
        result, true, true, false, false, () -> {
        },
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 3, 3, 8, 8, 8)), result),
        Rankings.supremum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5)), result),
        Rankings.infimum(Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)), result),
        Rankings.supremum(
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3)),
            result));

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

    Ranking result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkConstRoleOperator(RoleOperators.RANKING.weak().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    Ranking actualResult = RoleOperators.RANKING.weak().of(n, incomingView).make().apply(
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weak().of(n, (TransposableNetworkView<?, ?>) incomingView)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.RANKING.weak()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weak().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.RANKING.weak()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weaklyEquitable().loose().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.RANKING.weak().of(n, incomingView).make().apply(
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weaklyEquitable().equitable().loose().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2)));
    actualResult = RoleOperators.RANKING.weak().of(n, incomingView).make().apply(
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    Ranking result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak().of(network2.countMonadicIndices(), outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weak()
            .of(network2.countMonadicIndices(),
                (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make()
        .apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    Ranking result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.weak().of(network2.countMonadicIndices(), outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak().of(network2.countMonadicIndices(), outgoingView2)
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(),
            (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 0, 2, 2, 3, 3, 3, 4, 5)));
    actualResult = RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(), swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> rshipi.getRight() == rshipj.getRight()
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    assertThrows(UnsupportedOperationException.class, () -> RoleOperators.RANKING.weak()
        .of(network2.countMonadicIndices(), outgoingView2).compPredicate((rshipi, rshipj) -> true));
  }

  @Test
  public void testWeaklyEquitableEquivalenceBlocks() {
    Network network = createNetwork();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    Ranking result = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    Ranking actualResult = RoleOperators.RANKING.weaklyEquitable().of(n, incomingView).make().apply(
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weaklyEquitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    checkConstRoleOperator(
        RoleOperators.RANKING.weaklyEquitable()
            .of(n, (TransposableNetworkView<?, ?>) swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<?, ?>) incomingView).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.hashCode(), actualResult.hashCode());
    assertEquals(result.toString(), actualResult.toString());

    Ranking result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    checkConstRoleOperator(
        RoleOperators.RANKING.weaklyEquitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable().of(n, incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result2, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result2::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.hashCode(), actualResult.hashCode());
    assertEquals(result2.toString(), actualResult.toString());

    Ranking result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable().of(n, incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getLeft() >= 4;
          boolean jgreat = rshipj.getLeft() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());

    checkConstRoleOperator(RoleOperators.RANKING.weaklyEquitable()
        .of(n, swappingOutgoingView(network)).compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3)),
        result3, true, true, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 3, 4, 5, 6, 6, 7)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 0, 1, 1, 0, 2, 2)));
    actualResult = RoleOperators.RANKING.weaklyEquitable().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = rshipi.getRight() >= 4;
          boolean jgreat = rshipj.getRight() >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make().apply(Rankings
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)));
    BinaryRelationsTest.assertBinaryRelation(actualResult, result3::contains, (kind, count) -> {
    });
    assertFalse(actualResult.equals(new Object()));
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.hashCode(), actualResult.hashCode());
    assertEquals(result3.toString(), actualResult.toString());
  }

  @Test
  public void testBasicOperators() {

    final int size = 30;
    final BiIntPredicate generator1 = (i, j) -> i <= j && (i % 2) >= (j % 2);
    final BiIntPredicate generator2 = (i, j) -> i == 0 ? j == 0 : j % i == 0 && j != 0;
    final BiIntPredicate generator3 = (i, j) -> i <= j
        && ((i % 3) == (j % 3) || ((i % 3 != 0) && (j % 3 == 0)));
    final BiIntPredicate generator4 = (i, j) -> (i % 3) >= (j % 3);

    final RelationBuilder<? extends Ranking> builder1 = RelationBuilders
        .denseSafeRankingBuilder(size);
    final RelationBuilder<? extends Ranking> builder2 = RelationBuilders
        .denseSafeRankingBuilder(size);
    final RelationBuilder<? extends Ranking> builder3 = RelationBuilders
        .denseSafeRankingBuilder(size);
    final RelationBuilder<? extends Ranking> builder4 = RelationBuilders
        .denseSafeRankingBuilder(size);

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (generator1.testInt(i, j)) {
          builder1.add(i, j);
        }
        if (generator2.testInt(i, j)) {
          builder2.add(i, j);
        }
        if (generator3.testInt(i, j)) {
          builder3.add(i, j);
        }
        if (generator4.testInt(i, j)) {
          builder4.add(i, j);
        }
      }
    }

    final Ranking ranking1 = builder1.build();
    final Ranking ranking2 = builder2.build();
    final Ranking ranking3 = builder3.build();
    final Ranking ranking4 = builder4.build();

    checkRoleOperator(RoleOperators.RANKING.basic().forward(), ranking1, ranking1, true, false,
        true, true, () -> {
        }, ranking2, ranking3, Rankings.infimum(ranking1, ranking2),
        Rankings.infimum(ranking1, ranking3), Rankings.supremum(ranking1, ranking2),
        Rankings.supremum(ranking1, ranking3), ranking1, ranking1);

    checkRoleOperator(RoleOperators.RANKING.basic().invert(), ranking1, ranking1.invert(), true,
        false, false, false, () -> {
        }, ranking2, ranking3, Rankings.infimum(ranking1.invert(), ranking2),
        Rankings.infimum(ranking1.invert(), ranking3),
        Rankings.supremum(ranking1.invert(), ranking2),
        Rankings.supremum(ranking1.invert(), ranking3),
        Rankings.infimum(ranking1, ranking1.invert()),
        Rankings.supremum(ranking1, ranking1.invert()));

    checkRoleOperator(RoleOperators.RANKING.basic().produceConstant(ranking1), ranking2, ranking1,
        true, true, false, false, () -> {
        }, ranking2, ranking3, Rankings.infimum(ranking1, ranking2),
        Rankings.infimum(ranking1, ranking3), Rankings.supremum(ranking1, ranking2),
        Rankings.supremum(ranking1, ranking3), Rankings.infimum(ranking1, ranking2),
        Rankings.supremum(ranking1, ranking2));

    checkRoleOperator(RoleOperators.RANKING.basic().meetWithConstant(ranking1), ranking2,
        Rankings.infimum(ranking1, ranking2), true, false, true, false, () -> {
        }, ranking3, ranking3.invert(),
        Rankings.infimum(Rankings.infimum(ranking1, ranking2), ranking3),
        Rankings.infimum(Rankings.infimum(ranking1, ranking2), ranking3.invert()),
        Rankings.supremum(Rankings.infimum(ranking1, ranking2), ranking3),
        Rankings.supremum(Rankings.infimum(ranking1, ranking2), ranking3.invert()),
        Rankings.infimum(ranking1, ranking2), ranking2);

    checkRoleOperator(RoleOperators.RANKING.basic().joinWithConstant(ranking1), ranking2,
        Rankings.supremum(ranking1, ranking2), true, false, false, true, () -> {
        }, ranking3, ranking3.invert(),
        Rankings.infimum(ranking3, Rankings.supremum(ranking1, ranking2)),
        Rankings.infimum(ranking3.invert(), Rankings.supremum(ranking1, ranking2)),
        Rankings.supremum(ranking3, Rankings.supremum(ranking1, ranking2)),
        Rankings.supremum(ranking3.invert(), Rankings.supremum(ranking1, ranking2)), ranking2,
        Rankings.supremum(ranking1, ranking2));

    checkRoleOperator(RoleOperators.RANKING.basic().symmetrize(), ranking4,
        Rankings.infimum(ranking4, ranking4.invert()), true, false, true, false, () -> {
        }, ranking1, ranking2,
        Rankings.infimum(ranking1, Rankings.infimum(ranking4, ranking4.invert())),
        Rankings.infimum(ranking2, Rankings.infimum(ranking4, ranking4.invert())),
        Rankings.supremum(ranking1, Rankings.infimum(ranking4, ranking4.invert())),
        Rankings.supremum(ranking2, Rankings.infimum(ranking4, ranking4.invert())),
        Rankings.infimum(ranking4, ranking4.invert()), ranking4);
  }

  @Test
  public void testRegularRankingBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    Ranking result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    Ranking result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    Ranking result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().equitable().loose().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().loose().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n2, outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n, (TransposableNetworkView<?, ?>) incomingView)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n, (TransposableNetworkView<?, ?>) incomingView)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n2, swappingOutgoingView(network2)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n2, swappingOutgoingView(network2))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().of(n2, outgoingView2).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.regular().of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    assertThrows(UnsupportedOperationException.class,
        () -> RoleOperators.RANKING.regular().of(n, incomingView).compPredicate((i, j) -> true));
  }

  @Test
  public void testEquitableRankingBlocks() {

    Network network = createNetwork2();
    Network network2 = createNetwork3();

    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);

    final int n = network.countMonadicIndices();
    final int n2 = network2.countMonadicIndices();

    Ranking result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    Ranking result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    Ranking result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().loose().equitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.regular().equitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n2, outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable()
            .of(n, (TransposableNetworkView<?, ?>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable()
            .of(n2, (TransposableNetworkView<?, ?>) outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n2, swappingOutgoingView(network2)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
        RoleOperators.RANKING.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n, incomingView).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n2, outgoingView2).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network))
        .compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n2, swappingOutgoingView(network2)).compWeak((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return Boolean.compare(igreat, jgreat);
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n, incomingView).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(
        RoleOperators.RANKING.equitable().of(n2, outgoingView2).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    result1 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result2 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    result3 = Rankings.fromMatrixUnsafe(new boolean[][] { //
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
    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)),
        result1, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable().of(n, swappingOutgoingView(network))
        .compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 3, 3, 3)),
        result2, true, false, false, false,
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3)),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2)));

    checkNonconstRoleOperator(RoleOperators.RANKING.equitable()
        .of(n2, swappingOutgoingView(network2)).compPartial((rshipi, rshipj) -> {
          boolean igreat = Math.max(rshipi.getLeft(), rshipi.getRight()) >= 4;
          boolean jgreat = Math.max(rshipj.getLeft(), rshipj.getRight()) >= 4;
          return igreat == jgreat ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3)),
        result3, true, false, false, false,
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4)),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 2)));

    assertThrows(UnsupportedOperationException.class,
        () -> RoleOperators.RANKING.equitable().of(n, incomingView).compPredicate((i, j) -> true));
  }

}
