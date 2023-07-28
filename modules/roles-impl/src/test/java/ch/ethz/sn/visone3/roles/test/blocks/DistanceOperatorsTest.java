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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.DistanceMatrices;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.structures.LazyIntDistanceMatrixImpl;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.StreamSupport;

public class DistanceOperatorsTest {

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
  public void testStrongStructuralDistanceBlocks() {

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

    IntDistanceMatrix result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 6, 9, 8, 8, 9, 8, 8, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 1, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 5, 8, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 2, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 0, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 40, 52, 47, 47, 49, 42, 42, 47, 46, 45, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 10, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 2, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 1, 1, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 9, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 2, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 0, 1, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 1, 2, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 54, 52, 51, 50, 49, 49, 48, 47, 46, 45, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result,
        true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.strongStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 10, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
        { 5, 0, 5, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 1, 2, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, //
        { 2, 2, 1, 0, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, //
        { 2, 2, 2, 2, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, //
        { 1, 2, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, //
        { 3, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3 }, //
        { 3, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 9, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10 },
        { 4, 0, 4, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
        { 0, 1, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
        { 1, 1, 1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3 },
        { 1, 1, 2, 1, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3 },
        { 0, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
        { 2, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 2, 2 },
        { 2, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 2, 2 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 } });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
            true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 54, 52, 51, 50, 49, 49, 48, 47, 46, 45, 55, 55, 55, 55 }, //
        { 14, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 13, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.strongStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 6, 9, 8, 8, 9, 8, 8, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 1, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 2;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 5, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 40, 52, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.strongStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 13, 13 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.strongStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.strongStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
  }

  @Test
  public void testWeakStructuralDistanceBlocks() {

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

    IntDistanceMatrix result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 5, 8, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 2, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 0, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 6, 9, 8, 8, 9, 8, 8, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 1, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2)).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 40, 52, 47, 47, 49, 42, 42, 47, 46, 45, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 9, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 2, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 0, 1, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 1, 2, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 2, 2 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 10, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 2, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 1, 1, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 3, 3, 3, 3, 3, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 54, 52, 51, 50, 49, 49, 48, 47, 46, 45, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result,
        true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weakStructural()
        .of(n2, outgoingView2).compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return Integer.compare(lhsworth, rhsworth);
        }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compWeak(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((Comparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return Integer.compare(lhsworth, rhsworth);
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 9, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10 },
        { 4, 0, 4, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
        { 0, 1, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
        { 1, 1, 1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3 },
        { 1, 1, 2, 1, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3 },
        { 0, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
        { 2, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 2, 2 },
        { 2, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 2, 2 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 } });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 10, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
        { 5, 0, 5, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 1, 2, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, //
        { 2, 2, 1, 0, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, //
        { 2, 2, 2, 2, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, //
        { 1, 2, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, //
        { 3, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3 }, //
        { 3, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 54, 52, 51, 50, 49, 49, 48, 47, 46, 45, 55, 55, 55, 55 }, //
        { 14, 0, 12, 7, 7, 9, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 7, 7 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 0, 0, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
            true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 13, 9, 10, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 0, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 7, 7 }, //
        { 13, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 6, 6 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 7, 6, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 8, 7, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, outgoingView2).comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
          int lhsworth = 0;
          if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
            lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
          }
          int rhsworth = 0;
          if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
            rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
          }
          return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2)
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
            BinaryRelations.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 55, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55 }, //
        { 14, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 4, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 3, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 13, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weakStructural().of(n2, swappingOutgoingView(network2))
            .compPartial(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((PartialComparator<? super Relationship>) (rshipi, rshipj) -> {
              int lhsworth = 0;
              if (Math.min(rshipi.getLeft(), rshipi.getRight()) == 0) {
                lhsworth = Math.max(rshipi.getLeft(), rshipi.getRight()) / 2;
              }
              int rhsworth = 0;
              if (Math.min(rshipj.getLeft(), rshipj.getRight()) == 0) {
                rhsworth = Math.max(rshipj.getLeft(), rshipj.getRight()) / 2;
              }
              return lhsworth == rhsworth ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 5, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10 }, //
        { 0, 0, 3, 2, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 0, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 0, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 6, 9, 8, 8, 9, 8, 8, 10, 10, 10, 10, 10, 10, 10 }, //
        { 1, 0, 4, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 1, 1, 0, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 3, 3, 3, 3 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 }, //
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 40, 52, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 12, 7, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 7, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            })).failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 48, 49, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 13, 9, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 0, 4, 0, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 0, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 6, 13, 13, 13, 13, 13, 0, 1, 13, 13, 13, 13, 13, 13, 13 }, //
        { 7, 13, 13, 13, 13, 13, 1, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 0 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 1, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.weakStructural().of(n2, outgoingView2)
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
            Rankings.fromEquivalence(
                Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, outgoingView2).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 41, 54, 50, 51, 54, 42, 42, 55, 55, 55, 55, 55, 55, 55 }, //
        { 0, 0, 14, 10, 11, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 1, 4, 0, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //
        { 0, 1, 3, 3, 0, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //
        { 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 13, 13, 13, 13, 13, 0, 0, 13, 13, 13, 13, 13, 13, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 0, 13 }, //
        { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4), result, true,
        true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weakStructural()
        .of(n2, swappingOutgoingView(network2)).compPredicate(
            ((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
        .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
            : Math.max(0, rshipi.getRight() - rshipj.getRight()))
        .make(),
        Rankings.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weakStructural().of(n2, swappingOutgoingView(network2))
            .comp(((BiPredicate<? super Relationship, ? super Relationship>) (rshipi, rshipj) -> {
              return Math.abs(rshipi.getRight() - rshipj.getRight()) < 3;
            }))
            .substCost((rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
                : Math.max(0, rshipi.getRight() - rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4)),
        result, true, true, false, false, () -> {
        });
  }

  @Test
  public void testWeakDistanceBlocks() {

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
    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return incomingView.countTies(j) > 0 ? 0 : incomingView.countTies(i);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return network.asRelation().countRelationshipsFrom(j) > 0 ? 0
              : network.asRelation().countRelationshipsFrom(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return network.asRelation().countRelationshipsFrom(j) > 0 ? 0
              : network.asRelation().countRelationshipsFrom(i);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n, swappingOutgoingView(network)).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return network.asRelation().countRelationshipsFrom(j) > 0 ? 0
              : network.asRelation().countRelationshipsFrom(i);
        }), true, true, false, false, () -> {
        });

    IntDistanceMatrix result = new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (incomingView.countTies(j) > 0) {
        return 0;
      }
      int count = 0;
      for (Relationship r : incomingView.ties(i)) {
        count += r.getLeft();
      }
      return count;
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, incomingView).failCost(Relationship::getLeft)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n, incomingView).failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n, incomingView).failCost(Relationship::getLeft)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      if (network.asRelation().countRelationshipsFrom(j) > 0) {
        return 0;
      }
      int count = 0;
      for (Relationship r : network.asRelation().getRelationshipsFrom(i)) {
        count += r.getRight();
      }
      return count;
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix( //
        new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 3, 3, 0, 0, 0, 3, 3, 0, 0, 0 }, //
            { 5, 5, 0, 0, 0, 5, 5, 0, 0, 0 }, //
            { 4, 4, 1, 1, 0, 4, 4, 0, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
            { 17, 17, 11, 11, 9, 17, 17, 0, 0, 1 }, //
            { 16, 16, 10, 10, 8, 16, 16, 0, 0, 1 }, //
            { 15, 15, 9, 9, 7, 15, 15, 0, 0, 0 }, //
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, incomingView)
            .substCost(
                (ri, rj) -> rj == null ? ri.getLeft() : Math.max(ri.getLeft() - rj.getLeft(), 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n, swappingOutgoingView(network))
            .substCost(
                (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight(), 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);
    final int n2 = network2.countMonadicIndices();

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 3, 0, 0, 3, 1, 1, 1, 1, 1, 1, 3 }, //
        { 3, 0, 0, 3, 1, 1, 1, 1, 1, 1, 3 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 3, 0, 0, 3, 2, 0, 2, 2, 0, 1, 3 }, //
        { 3, 0, 0, 3, 1, 0, 0, 1, 0, 0, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 1, 2 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().of(n2, outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 17, 0, 0, 17, 10, 10, 10, 10, 10, 10, 17 }, //
        { 18, 0, 0, 18, 10, 10, 10, 10, 10, 10, 18 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 23, 0, 0, 23, 17, 0, 17, 17, 0, 9, 23 }, //
        { 16, 0, 0, 16, 7, 0, 0, 7, 0, 0, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 9, 0, 9, 9, 0, 9, 14 }, //
        { 13, 0, 0, 13, 8, 0, 8, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 17, 5, 5, 17, 11, 10, 11, 11, 10, 10, 17 }, //
        { 18, 5, 5, 18, 12, 11, 12, 12, 11, 11, 18 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 24, 8, 8, 24, 20, 11, 20, 20, 11, 15, 24 }, //
        { 16, 2, 2, 16, 10, 4, 7, 10, 4, 4, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 4, 4, 14, 11, 6, 11, 11, 6, 10, 14 }, //
        { 13, 3, 3, 13, 10, 5, 10, 10, 5, 5, 13 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1 }, //
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 0, 0, 2, 2, 2, 2, 2 }, //
        { 2, 0, 0, 2, 0, 0, 2, 2, 2, 2, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 0, 0, 3, 0, 0, 1, 1, 1, 1, 3 }, //
        { 3, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2 }, //
        { 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION
            .weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 0, 0, 3, 3, 3, 3, 14 }, //
        { 15, 0, 0, 15, 0, 0, 3, 3, 3, 3, 15 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 6, 0, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 0, 0, 23, 0, 0, 2, 2, 2, 2, 23 }, //
        { 16, 0, 0, 16, 0, 0, 0, 0, 0, 0, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 0, 0, 0, 0, 0, 0, 14 }, //
        { 13, 0, 0, 13, 0, 0, 0, 0, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 16, 7, 7, 16, 11, 8, 11, 11, 9, 9, 16 }, //
        { 17, 7, 7, 17, 12, 9, 12, 12, 10, 10, 17 }, //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 6, 1, 1, 6, 4, 2, 4, 4, 3, 3, 6 }, //
        { 24, 9, 9, 24, 16, 12, 16, 16, 13, 13, 24 }, //
        { 16, 2, 2, 16, 7, 4, 7, 7, 4, 4, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 4, 4, 14, 8, 6, 8, 8, 6, 6, 14 }, //
        { 13, 3, 3, 13, 7, 5, 7, 7, 5, 5, 13 }, //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak()
        .of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak()
        .of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak()
        .of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 3, 2, 2, 4, 2, 3, 2 }, //
        { 2, 0, 0, 2, 2, 1, 2, 3, 2, 3, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 2, 1, 3, 2, 0, 2, 3, 1, 1, 3 }, //
        { 3, 1, 1, 3, 1, 1, 0, 1, 1, 0, 3 }, //
        { 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 2, 1, 1, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 17, 4, 10, 17, 3, 13, 14 }, //
        { 15, 0, 0, 15, 13, 0, 10, 13, 3, 13, 15 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 6, 6, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 14, 8, 23, 17, 0, 11, 19, 2, 2, 23 }, //
        { 16, 7, 7, 16, 4, 4, 0, 4, 7, 0, 16 }, //
        { 6, 6, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 9, 0, 9, 9, 0, 0, 14 }, //
        { 13, 8, 8, 13, 8, 0, 0, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 16, 8, 9, 16, 17, 12, 13, 17, 11, 15, 16 }, //
        { 17, 9, 10, 17, 15, 10, 14, 15, 12, 16, 17 }, //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 6, 7, 5, 6, 4, 3, 4, 4, 5, 5, 6 }, //
        { 24, 19, 17, 24, 22, 13, 19, 22, 15, 15, 24 }, //
        { 16, 12, 12, 16, 10, 9, 9, 10, 12, 8, 16 }, //
        { 6, 6, 4, 6, 3, 3, 3, 3, 4, 4, 6 }, //
        { 14, 7, 7, 14, 11, 7, 12, 11, 8, 8, 14 }, //
        { 13, 11, 11, 13, 10, 6, 8, 10, 7, 7, 13 }, //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
  }

  @Test
  public void testWeaklyEquitableDistanceBlocks() {

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
    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weaklyEquitable()
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, swappingOutgoingView(network)).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, swappingOutgoingView(network))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });

    IntDistanceMatrix result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 3, 3, 0, 0, 0, 3, 3, 0, 0, 0 }, //
        { 5, 5, 2, 0, 2, 5, 5, 0, 0, 0 }, //
        { 4, 4, 0, 0, 0, 4, 4, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 17, 17, 8, 0, 8, 17, 17, 0, 0, 0 }, //
        { 16, 16, 7, 0, 7, 16, 16, 0, 0, 0 }, //
        { 15, 15, 7, 0, 7, 15, 15, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView)
            .failCost(Relationship::getLeft).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n, incomingView)
            .failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
            .failCost(Relationship::getLeft).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 3, 3, 0, 0, 0, 3, 3, 0, 0, 0 }, //
        { 5, 5, 2, 0, 2, 5, 5, 0, 0, 0 }, //
        { 4, 4, 1, 1, 0, 4, 4, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 17, 17, 14, 12, 13, 17, 17, 0, 1, 2 }, //
        { 16, 16, 13, 11, 12, 16, 16, 0, 0, 1 }, //
        { 15, 15, 12, 10, 11, 15, 15, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, incomingView)
            .substCost(
                (ri, rj) -> rj == null ? ri.getLeft() : Math.max(ri.getLeft() - rj.getLeft(), 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable()
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight(), 0))
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities.checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight(), 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable().of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight(), 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);
    final int n2 = network2.countMonadicIndices();

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
        { 3, 0, 0, 3, 2, 1, 1, 3, 2, 2, 3 }, //
        { 3, 1, 0, 3, 2, 1, 1, 3, 2, 2, 3 }, //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
        { 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1 }, //
        { 3, 2, 2, 3, 3, 0, 2, 3, 2, 2, 3 }, //
        { 3, 1, 1, 3, 2, 0, 0, 2, 1, 1, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 1, 0, 2, 1, 0, 1, 1, 0, 1, 2 }, //
        { 2, 1, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
        { 17, 0, 0, 17, 13, 10, 10, 13, 10, 10, 17 }, //
        { 18, 5, 0, 18, 13, 10, 10, 13, 10, 10, 18 }, //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
        { 6, 0, 0, 6, 0, 0, 0, 1, 0, 0, 6 }, //
        { 23, 14, 14, 23, 19, 0, 17, 19, 14, 15, 23 }, //
        { 16, 5, 4, 16, 11, 0, 0, 11, 4, 4, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 5, 0, 14, 9, 0, 9, 9, 0, 9, 14 }, //
        { 13, 5, 0, 13, 8, 0, 8, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, //
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 2, 0, 0, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 17, 9, 9, 17, 14, 10, 12, 14, 11, 11, 17 }, //
        { 18, 11, 10, 18, 15, 11, 13, 15, 12, 12, 18 }, //
        { 2, 0, 0, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 6, 1, 1, 6, 4, 2, 3, 4, 2, 2, 6 }, //
        { 24, 18, 18, 24, 22, 13, 20, 22, 19, 19, 24 }, //
        { 16, 9, 9, 16, 13, 5, 9, 13, 10, 10, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 9, 7, 14, 11, 6, 11, 11, 8, 10, 14 }, //
        { 13, 8, 6, 13, 10, 5, 10, 10, 7, 7, 13 }, //
        { 2, 0, 0, 2, 1, 0, 0, 1, 0, 0, 2 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 2, 1, 2, 3, 2, 2, 2 }, //
        { 2, 0, 0, 2, 2, 1, 2, 3, 2, 2, 2 }, //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
        { 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 1, 1, 3, 2, 0, 1, 3, 2, 2, 3 }, //
        { 3, 1, 1, 3, 2, 0, 0, 2, 1, 1, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 2, 0, 0, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 4, 0, 3, 7, 3, 3, 14 }, //
        { 15, 0, 0, 15, 5, 0, 3, 8, 3, 3, 15 }, //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
        { 6, 0, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 6, 6, 23, 14, 0, 2, 16, 8, 8, 23 }, //
        { 16, 4, 4, 16, 9, 0, 0, 9, 4, 4, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 5, 0, 0, 5, 0, 0, 14 }, //
        { 13, 0, 0, 13, 5, 0, 0, 5, 0, 0, 13 }, //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 }, //
        { 16, 9, 9, 16, 14, 8, 12, 14, 11, 11, 16 }, //
        { 17, 10, 10, 17, 15, 9, 13, 15, 12, 12, 17 }, //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 }, //
        { 6, 1, 1, 6, 4, 2, 4, 4, 3, 3, 6 }, //
        { 24, 17, 17, 24, 22, 13, 18, 22, 19, 19, 24 }, //
        { 16, 9, 9, 16, 13, 5, 9, 13, 10, 10, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 7, 7, 14, 11, 6, 9, 11, 8, 8, 14 }, //
        { 13, 6, 6, 13, 10, 5, 8, 10, 7, 7, 13 }, //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weaklyEquitable()
        .of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weaklyEquitable()
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 3, 2, 2, 4, 2, 3, 2 }, //
        { 2, 0, 0, 2, 2, 1, 2, 3, 2, 3, 2 }, //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
        { 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 2, 1, 3, 2, 0, 2, 3, 2, 2, 3 }, //
        { 3, 1, 1, 3, 2, 1, 0, 2, 2, 1, 3 }, //
        { 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 2, 1, 1, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 17, 4, 10, 17, 3, 13, 14 }, //
        { 15, 0, 0, 15, 13, 0, 10, 13, 3, 13, 15 }, //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
        { 6, 6, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 14, 8, 23, 17, 0, 11, 19, 10, 10, 23 }, //
        { 16, 7, 7, 16, 9, 4, 0, 9, 11, 4, 16 }, //
        { 6, 6, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 9, 0, 9, 9, 0, 0, 14 }, //
        { 13, 8, 8, 13, 8, 0, 0, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable()
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weaklyEquitable()
                .of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 }, //
        { 16, 9, 9, 16, 17, 12, 13, 17, 11, 15, 16 }, //
        { 17, 10, 10, 17, 15, 10, 14, 15, 12, 16, 17 }, //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 }, //
        { 6, 7, 5, 6, 4, 3, 4, 4, 5, 5, 6 }, //
        { 24, 19, 17, 24, 22, 13, 20, 22, 19, 19, 24 }, //
        { 16, 13, 13, 16, 13, 9, 9, 13, 14, 10, 16 }, //
        { 6, 6, 4, 6, 3, 3, 3, 3, 4, 4, 6 }, //
        { 14, 7, 7, 14, 11, 7, 12, 11, 8, 8, 14 }, //
        { 13, 11, 11, 13, 10, 6, 8, 10, 7, 7, 13 }, //
        { 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 2 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable()
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weaklyEquitable().of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

  }

  @Test
  public void test2WeaklyEquitableDistanceBlocks() {

    Network network = createNetwork();
    final NetworkView<Relationship, Relationship> incomingView = NetworkView
        .fromNetworkRelation(network, Direction.INCOMING);
    final int n = network.countMonadicIndices();

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n, incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak().strictness(2)
        .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(incomingView.countTies(i) - 2 * incomingView.countTies(j), 0);
        }), true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, swappingOutgoingView(network))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - 2 * network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n, swappingOutgoingView(network)).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - 2 * network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n, swappingOutgoingView(network))
            .make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        new LazyIntDistanceMatrixImpl(n, (i, j) -> {
          return Math.max(network.asRelation().countRelationshipsFrom(i)
              - 2 * network.asRelation().countRelationshipsFrom(j), 0);
        }), true, true, false, false, () -> {
        });

    IntDistanceMatrix result = new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      return StreamSupport.stream(incomingView.ties(i).spliterator(), false)
          .map(Relationship::getLeft).sorted(Collections.reverseOrder())
          .skip(2 * incomingView.countTies(j)).mapToInt(Integer::intValue).sum();
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, incomingView)
            .failCost(Relationship::getLeft)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n, incomingView)
            .failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView)
            .failCost(Relationship::getLeft)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
            .failCost(Relationship::getLeft).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = new LazyIntDistanceMatrixImpl(n, (i, j) -> {
      return StreamSupport.stream(network.asRelation().getRelationshipsFrom(i).spliterator(), false)
          .map(Relationship::getRight).sorted(Collections.reverseOrder())
          .skip(2 * network.asRelation().countRelationshipsFrom(j)).mapToInt(Integer::intValue)
          .sum();
    });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n, swappingOutgoingView(network))
            .failCost(Relationship::getRight).make(),
        BinaryRelations
            .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 15, 6, 6, 15, 11, 6, 9, 11, 7, 7, 15 }, //
        { 16, 6, 6, 16, 12, 7, 10, 12, 8, 8, 16 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 5, 1, 1, 5, 3, 2, 3, 3, 2, 2, 5 }, //
        { 23, 11, 11, 23, 19, 11, 15, 19, 13, 13, 23 }, //
        { 14, 4, 4, 14, 10, 4, 8, 10, 6, 6, 14 }, //
        { 5, 1, 1, 5, 3, 2, 3, 3, 2, 2, 5 }, //
        { 12, 4, 4, 12, 8, 6, 8, 8, 6, 6, 12 }, //
        { 11, 3, 3, 11, 7, 5, 7, 7, 5, 5, 11 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, incomingView)
            .substCost(
                (ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2).of(n, incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2)
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true,
            false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2)
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n, (TransposableNetworkView<Relationship, Relationship>) incomingView)
                .substCost((ri, rj) -> rj == null ? ri.getLeft()
                    : Math.max(ri.getLeft() - rj.getLeft() / 2, 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n, swappingOutgoingView(network))
            .substCost(
                (ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight() / 2, 0))
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n, swappingOutgoingView(network))
                .substCost((ri, rj) -> rj == null ? ri.getRight()
                    : Math.max(ri.getRight() - rj.getRight() / 2, 0))
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    Network network2 = createNetwork();
    final NetworkView<Relationship, Relationship> outgoingView2 = NetworkView
        .fromNetworkRelation(network2, Direction.OUTGOING);
    final int n2 = network2.countMonadicIndices();

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 3, 0, 0, 3, 1, 1, 1, 2, 1, 1, 3 }, //
        { 3, 0, 0, 3, 1, 1, 1, 2, 1, 1, 3 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 3, 1, 1, 3, 2, 0, 2, 2, 1, 1, 3 }, //
        { 3, 0, 0, 3, 1, 0, 0, 1, 0, 0, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 1, 2 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().strictness(2)
        .of(n2, outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().strictness(2)
        .of(n2, outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight())).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 17, 0, 0, 17, 10, 10, 10, 10, 10, 10, 17 }, //
        { 18, 0, 0, 18, 10, 10, 10, 10, 10, 10, 18 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 23, 6, 6, 23, 17, 0, 17, 17, 6, 9, 23 }, //
        { 16, 0, 0, 16, 7, 0, 0, 7, 0, 0, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 9, 0, 9, 9, 0, 9, 14 }, //
        { 13, 0, 0, 13, 8, 0, 8, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 17, 6, 6, 17, 11, 10, 11, 11, 10, 10, 17 }, //
        { 18, 6, 6, 18, 12, 11, 12, 12, 11, 11, 18 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 24, 13, 13, 24, 20, 11, 20, 20, 15, 15, 24 }, //
        { 16, 4, 4, 16, 10, 4, 8, 10, 6, 6, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 4, 4, 14, 11, 6, 11, 11, 6, 10, 14 }, //
        { 13, 3, 3, 13, 10, 5, 10, 10, 5, 5, 13 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compWeak((rshipi, rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight()))
            .substCost((ri,
                rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 0, 0, 2, 2, 2, 2, 2 }, //
        { 2, 0, 0, 2, 0, 0, 2, 2, 2, 2, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 0, 0, 3, 1, 0, 1, 2, 1, 1, 3 }, //
        { 3, 0, 0, 3, 1, 0, 0, 1, 0, 0, 3 }, //
        { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2 }, //
        { 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION
            .weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 0, 0, 3, 3, 3, 3, 14 }, //
        { 15, 0, 0, 15, 0, 0, 3, 3, 3, 3, 15 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 6, 0, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 0, 0, 23, 6, 0, 2, 8, 2, 2, 23 }, //
        { 16, 0, 0, 16, 4, 0, 0, 4, 0, 0, 16 }, //
        { 6, 0, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 0, 0, 0, 0, 0, 0, 14 }, //
        { 13, 0, 0, 13, 0, 0, 0, 0, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .failCost(Relationship::getRight).make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 16, 7, 7, 16, 11, 8, 11, 11, 9, 9, 16 }, //
        { 17, 7, 7, 17, 12, 9, 12, 12, 10, 10, 17 }, //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 6, 1, 1, 6, 4, 2, 4, 4, 3, 3, 6 }, //
        { 24, 12, 12, 24, 19, 12, 17, 19, 15, 15, 24 }, //
        { 16, 4, 4, 16, 10, 4, 8, 10, 6, 6, 16 }, //
        { 6, 1, 1, 6, 3, 2, 3, 3, 2, 2, 6 }, //
        { 14, 4, 4, 14, 8, 6, 8, 8, 6, 6, 14 }, //
        { 13, 3, 3, 13, 7, 5, 7, 7, 5, 5, 13 }, //
        { 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1 }, //
    });

    OperatorTestUtilities
        .checkOperator(DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak().strictness(2)
        .of(n2, outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.EQUIVALENCE.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(), Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true,
        false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak().strictness(2)
        .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
                ? PartialComparator.ComparisonResult.EQUAL
                : PartialComparator.ComparisonResult.INCOMPARABLE)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.RANKING.weak().strictness(2)
        .of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BINARYRELATION.weak().strictness(2)
        .of(n2, swappingOutgoingView(network2))
        .compPartial((rshipi, rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE)
        .substCost(
            (ri, rj) -> rj == null ? ri.getRight() : Math.max(ri.getRight() - rj.getRight() / 2, 0))
        .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 2, 0, 0, 2, 3, 2, 2, 4, 2, 3, 2 }, //
        { 2, 0, 0, 2, 2, 1, 2, 3, 2, 3, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
        { 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1 }, //
        { 3, 2, 1, 3, 2, 0, 2, 3, 1, 1, 3 }, //
        { 3, 1, 1, 3, 1, 1, 0, 1, 1, 0, 3 }, //
        { 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, //
        { 2, 0, 0, 2, 1, 0, 1, 1, 0, 0, 2 }, //
        { 2, 1, 1, 2, 1, 0, 0, 1, 0, 0, 2 }, //
        { 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2)
                .of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 14, 0, 0, 14, 17, 4, 10, 17, 3, 13, 14 }, //
        { 15, 0, 0, 15, 13, 0, 10, 13, 3, 13, 15 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
        { 6, 6, 0, 6, 0, 0, 1, 1, 1, 1, 6 }, //
        { 23, 14, 8, 23, 17, 0, 11, 19, 2, 2, 23 }, //
        { 16, 7, 7, 16, 4, 4, 0, 4, 7, 0, 16 }, //
        { 6, 6, 0, 6, 0, 0, 0, 0, 0, 0, 6 }, //
        { 14, 0, 0, 14, 9, 0, 9, 9, 0, 0, 14 }, //
        { 13, 8, 8, 13, 8, 0, 0, 8, 0, 0, 13 }, //
        { 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0 }, //
    });

    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.EQUIVALENCE.weak().strictness(2)
                .of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
            false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });
    OperatorTestUtilities
        .checkOperator(
            DistanceOperators.BINARYRELATION.weak().strictness(2)
                .of(n2, swappingOutgoingView(network2))
                .compPredicate(
                    (rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
                .failCost(Relationship::getRight).make(),
            BinaryRelations
                .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
            result, true, true, false, false, () -> {
            });

    result = DistanceMatrices.fromMatrix(new int[][] { //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 16, 8, 9, 16, 17, 12, 13, 17, 11, 15, 16 }, //
        { 17, 9, 10, 17, 15, 10, 14, 15, 12, 16, 17 }, //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 }, //
        { 6, 7, 5, 6, 4, 3, 4, 4, 5, 5, 6 }, //
        { 24, 19, 17, 24, 22, 13, 19, 22, 15, 15, 24 }, //
        { 16, 12, 12, 16, 10, 9, 9, 10, 12, 8, 16 }, //
        { 6, 6, 4, 6, 3, 3, 3, 3, 4, 4, 6 }, //
        { 14, 7, 7, 14, 11, 7, 12, 11, 8, 8, 14 }, //
        { 13, 11, 11, 13, 10, 6, 8, 10, 7, 7, 13 }, //
        { 1, 2, 2, 1, 3, 1, 3, 3, 3, 3, 1 } //
    });

    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2)
            .of(n2, (TransposableNetworkView<Relationship, Relationship>) outgoingView2)
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.EQUIVALENCE.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2), result, true, true, false,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.RANKING.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        Rankings.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        DistanceOperators.BINARYRELATION.weak().strictness(2).of(n2, swappingOutgoingView(network2))
            .compPredicate((rshipi, rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2)
            .substCost((ri, rj) -> rj == null ? ri.getRight()
                : Math.max(ri.getRight() - rj.getRight() / 2, 0))
            .make(),
        BinaryRelations.fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 1, 1, 2, 2, 2)),
        result, true, true, false, false, () -> {
        });
  }

  private <T> void testRegularKindDistanceBlocks(
      Supplier<VariableDistanceBuilderFactory<T>> regularTypeBlockBuilder,
      Supplier<VariableDistanceBuilderFactory<T>> weakTypeBlockBuilder, T input,
      Function<TransposableNetworkView<Relationship, Relationship>, BiPredicate<Relationship, Relationship>> inputBasedTest) {

    Network network = createNetwork3();
    int n = network.countMonadicIndices();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);

    ToIntFunction<Relationship> failCostOp = Relationship::getRight;
    ToIntBiFunction<Relationship, Relationship> substCostOp = (ri, rj) -> rj == null ? ri.getRight()
        : Math.max(ri.getRight() - rj.getRight() / 2, 0);

    OperatorTestUtilities.checkOperator(regularTypeBlockBuilder.get().of(n, outgoingView).make(),
        input, weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).failCost(failCostOp).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView).comp(inputBasedTest.apply(outgoingView))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).substCost(substCostOp).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView).comp(inputBasedTest.apply(outgoingView))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(regularTypeBlockBuilder.get().of(n, swappingView).make(),
        input, weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).failCost(failCostOp).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView).comp(inputBasedTest.apply(swappingView))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).substCost(substCostOp).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView).comp(inputBasedTest.apply(swappingView))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    Comparator<Relationship> comparator = (rshipi, rshipj) -> Boolean
        .compare(rshipi.getRight() >= 4, rshipj.getRight() >= 4);

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(comparator).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).compWeak(comparator).failCost(failCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(comparator).substCost(substCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(comparator).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).compWeak(comparator).failCost(failCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(comparator).substCost(substCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    PartialComparator<Relationship> partialFromComparator = (rshipi, rshipj) -> {
      int result = comparator.compare(rshipi, rshipj);
      if (result < 0) {
        return PartialComparator.ComparisonResult.LESS;
      } else if (result == 0) {
        return PartialComparator.ComparisonResult.EQUAL;
      } else {
        return PartialComparator.ComparisonResult.GREATER;
      }
    };

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(partialFromComparator).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(regularTypeBlockBuilder
        .get().of(n, outgoingView).compPartial(partialFromComparator).failCost(failCostOp).make(),
        input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder
            .get().of(n, outgoingView).comp(partialFromComparator).substCost(substCostOp).make(),
        input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(partialFromComparator).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(regularTypeBlockBuilder
        .get().of(n, swappingView).compPartial(partialFromComparator).failCost(failCostOp).make(),
        input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder
            .get().of(n, swappingView).comp(partialFromComparator).substCost(substCostOp).make(),
        input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView)
                .and((rshipi, rshipj) -> comparator.compare(rshipi, rshipj) <= 0))
            .substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    PartialComparator<Relationship> partialComparator = (rshipi,
        rshipj) -> (rshipi.getRight() >= 4) == (rshipj.getRight() >= 4)
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE;

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(partialComparator).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).compPartial(partialComparator)
            .failCost(failCostOp).make(),
        input, weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(partialComparator)
            .substCost(substCostOp).make(),
        input, weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(partialComparator).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).compPartial(partialComparator)
            .failCost(failCostOp).make(),
        input, weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).failCost(failCostOp).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(partialComparator)
            .substCost(substCostOp).make(),
        input, weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView).and((rshipi, rshipj) -> {
              PartialComparator.ComparisonResult result = partialComparator.compare(rshipi, rshipj);
              return result == PartialComparator.ComparisonResult.EQUAL
                  || result == PartialComparator.ComparisonResult.LESS;
            })).substCost(substCostOp).make().apply(input),
        true, false, false, false, () -> {
        });

    BiPredicate<Relationship, Relationship> predicate = (rshipi,
        rshipj) -> Math.abs(rshipi.getRight() - rshipj.getRight()) < 2;
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(predicate).make(), input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView).and(predicate)).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            regularTypeBlockBuilder
                .get().of(n, outgoingView).compPredicate(predicate).failCost(failCostOp).make(),
            input,
            weakTypeBlockBuilder.get().of(n, outgoingView)
                .comp(inputBasedTest.apply(outgoingView).and(predicate)).failCost(failCostOp).make()
                .apply(input),
            true, false, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, outgoingView).comp(predicate).substCost(substCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, outgoingView)
            .comp(inputBasedTest.apply(outgoingView).and(predicate)).substCost(substCostOp).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(predicate).make(), input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView).and(predicate)).make().apply(input),
        true, false, false, false, () -> {
        });
    OperatorTestUtilities
        .checkOperator(
            regularTypeBlockBuilder
                .get().of(n, swappingView).compPredicate(predicate).failCost(failCostOp).make(),
            input,
            weakTypeBlockBuilder.get().of(n, swappingView)
                .comp(inputBasedTest.apply(swappingView).and(predicate)).failCost(failCostOp).make()
                .apply(input),
            true, false, false, false, () -> {
            });
    OperatorTestUtilities.checkOperator(
        regularTypeBlockBuilder.get().of(n, swappingView).comp(predicate).substCost(substCostOp)
            .make(),
        input,
        weakTypeBlockBuilder.get().of(n, swappingView)
            .comp(inputBasedTest.apply(swappingView).and(predicate)).substCost(substCostOp).make()
            .apply(input),
        true, false, false, false, () -> {
        });
  }

  @Test
  public void testRegularDistanceBlocks() {

    ConstMapping.OfInt eqInput = Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3);
    Ranking rankInput = Rankings.fromEquivalence(eqInput);
    BinaryRelation relInput = BinaryRelations.fromEquivalence(eqInput);
    Function<TransposableNetworkView<Relationship, Relationship>, BiPredicate<Relationship, Relationship>> predEqInputView = (outView) -> (rshipi, rshipj) ->
    eqInput.getInt(
        outView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(), rshipi)) == eqInput
            .getInt(
                outView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj));
    Function<TransposableNetworkView<Relationship, Relationship>, BiPredicate<Relationship, Relationship>> predRankInputView = predEqInputView;
    Function<TransposableNetworkView<Relationship, Relationship>, BiPredicate<Relationship, Relationship>> predRelInputView = predEqInputView;

    // test regular distances
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.regular(),
        () -> DistanceOperators.EQUIVALENCE.weak(), eqInput, predEqInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.regular().equitable().loose(),
        () -> DistanceOperators.EQUIVALENCE.weak(), eqInput, predEqInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.equitable().loose(),
        () -> DistanceOperators.EQUIVALENCE.weak(), eqInput, predEqInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.regular(),
        () -> DistanceOperators.RANKING.weak(), rankInput, predRankInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.regular().equitable().loose(),
        () -> DistanceOperators.RANKING.weak(), rankInput, predRankInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.equitable().loose(),
        () -> DistanceOperators.RANKING.weak(), rankInput, predRankInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.regular(),
        () -> DistanceOperators.BINARYRELATION.weak(), relInput, predRelInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.BINARYRELATION.regular().equitable().loose(),
        () -> DistanceOperators.BINARYRELATION.weak(), relInput, predRelInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.equitable().loose(),
        () -> DistanceOperators.BINARYRELATION.weak(), relInput, predRelInputView);

    // test (regular) equitable distances
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.equitable(),
        () -> DistanceOperators.EQUIVALENCE.weaklyEquitable(), eqInput, predEqInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.EQUIVALENCE.equitable().loose().equitable(),
        () -> DistanceOperators.EQUIVALENCE.weak().equitable(), eqInput, predEqInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.regular().equitable(),
        () -> DistanceOperators.EQUIVALENCE.weaklyEquitable(), eqInput, predEqInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.equitable(),
        () -> DistanceOperators.RANKING.weak().equitable(), rankInput, predRankInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.regular().loose().equitable(),
        () -> DistanceOperators.RANKING.weaklyEquitable(), rankInput, predRankInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.regular().equitable(),
        () -> DistanceOperators.RANKING.weaklyEquitable(), rankInput, predRankInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.equitable(),
        () -> DistanceOperators.BINARYRELATION.weaklyEquitable(), relInput, predRelInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.BINARYRELATION.regular().loose().equitable(),
        () -> DistanceOperators.BINARYRELATION.weaklyEquitable(), relInput, predRelInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.regular().equitable(),
        () -> DistanceOperators.BINARYRELATION.weak().equitable(), relInput, predRelInputView);

    // test (regular) 2-equitable distances
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.equitable().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.weaklyEquitable().strictness(2), eqInput,
        predEqInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.EQUIVALENCE.regular().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.weak().strictness(2), eqInput, predEqInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.EQUIVALENCE.regular().equitable().loose().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.weak().strictness(2), eqInput, predEqInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.equitable().strictness(2),
        () -> DistanceOperators.RANKING.weak().strictness(2), rankInput, predRankInputView);
    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.regular().strictness(2),
        () -> DistanceOperators.RANKING.weaklyEquitable().strictness(2), rankInput,
        predRankInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.RANKING.regular().equitable().loose().strictness(2),
        () -> DistanceOperators.RANKING.weak().strictness(2), rankInput, predRankInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.equitable().strictness(2),
        () -> DistanceOperators.BINARYRELATION.weak().strictness(2), relInput, predRelInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.BINARYRELATION.regular().strictness(2),
        () -> DistanceOperators.BINARYRELATION.weak().strictness(2), relInput, predRelInputView);
    testRegularKindDistanceBlocks(
        () -> DistanceOperators.BINARYRELATION.regular().equitable().loose().strictness(2),
        () -> DistanceOperators.BINARYRELATION.weaklyEquitable().strictness(2), relInput,
        predRelInputView);

    // test (regular) 3-equitable distances
    testRegularKindDistanceBlocks(() -> DistanceOperators.EQUIVALENCE.equitable().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.weaklyEquitable().strictness(2), eqInput,
        predEqInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.RANKING.equitable().strictness(2),
        () -> DistanceOperators.RANKING.weak().strictness(2), rankInput, predRankInputView);

    testRegularKindDistanceBlocks(() -> DistanceOperators.BINARYRELATION.equitable().strictness(2),
        () -> DistanceOperators.BINARYRELATION.weak().strictness(2), relInput, predRelInputView);
  }

  @Test
  public void testBasicOperators() {
    final int size = 20;
    final int[][] mat = new int[20][20];
    final Random rand = new Random();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        mat[i][j] = rand.nextInt(30);
      }
    }
    IntDistanceMatrix input = DistanceMatrices.fromMatrix(mat);
    OperatorTestUtilities.checkOperator(DistanceOperators.BASIC.symmetrizeAdd(), input,
        new LazyIntDistanceMatrixImpl(size, (i, j) -> mat[i][j] + mat[j][i]), true, false, true,
        false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BASIC.symmetrizeMax(), input,
        new LazyIntDistanceMatrixImpl(size, (i, j) -> Math.max(mat[i][j], mat[j][i])), true,
        false, true, false, () -> {
        });
    OperatorTestUtilities.checkOperator(DistanceOperators.BASIC.symmetrizeMin(), input,
        new LazyIntDistanceMatrixImpl(size, (i, j) -> Math.min(mat[i][j], mat[j][i])), true,
        false, false, true, () -> {
        });
  }
}
