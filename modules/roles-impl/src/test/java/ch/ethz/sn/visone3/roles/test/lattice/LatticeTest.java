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
package ch.ethz.sn.visone3.roles.test.lattice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.lattice.CoverEnumerators;
import ch.ethz.sn.visone3.roles.lattice.DepthFirstSearchEnumerator.CoverEnumerator;
import ch.ethz.sn.visone3.roles.lattice.ProjectionEnumerators;
import ch.ethz.sn.visone3.roles.lattice.StableRolesEnumeration;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

public class LatticeTest {

  // Political actors network from Doreian and Albert - Partitioning Political
  // Actor Networks: Some
  // Quantitative Tools for Analyzing Qualitative Networks

  private static final Integer z = null;
  private static final Integer[][] politicalActorsNetwork = { //
      { z }, //
      { z, z }, //
      { 1, z, z }, //
      { 1, z, 1, z }, //
      { z, z, z, 1, z }, //
      { z, z, 1, 1, 1, z }, //
      { 1, z, 1, 1, z, z, z }, //
      { z, 1, z, z, z, z, z, z }, //
      { z, 1, z, z, z, z, z, 1, z }, //
      { z, 1, z, z, z, z, z, 1, 1, z }, //
      { z, z, z, z, z, z, z, z, z, z, z }, //
      { z, 1, z, 1, z, 1, z, 1, z, z, 1, z }, //
      { 1, z, z, z, z, z, z, 1, 1, 1, z, 1, z }, //
      { z, z, 1, z, z, 1, z, z, z, z, z, z, z, z }, //
  };

  private static final Integer[][] cycleNetwork = { //
      { z }, //
      { 1, z }, //
      { z, 1, z }, //
      { z, z, 1, z }, //
      { z, z, z, 1, z }, //
      { z, z, z, z, 1, z }, //
      { 1, z, z, z, z, 1, z }, //
  };

  @Test
  public void testRegularEquivalencePoliticalActors() throws IOException {
    Network net = MatrixSource.fromAdjacency(politicalActorsNetwork, false).getNetwork();

    Iterable<ConstMapping.OfInt> fixedPointsIterable = StableRolesEnumeration.EQUIVALENCE.stableRolesUnderRestriction(
        RoleOperators.EQUIVALENCE.regular().of(NetworkView.fromNetworkRelation(net, Direction.OUTGOING)).make(),
        Converters.singleClassEquivalence(net.countMonadicIndices()).apply(null));

    int count = 0;
    Iterator<ConstMapping.OfInt> iterator = fixedPointsIterable.iterator();
    while (iterator.hasNext()) {
      ConstMapping.OfInt fixedPoint = iterator.next();
      assertNotNull(fixedPoint);
      ++count;
    }
    assertEquals(29, count);
    assertThrows(NoSuchElementException.class, () -> iterator.next());
  }

  @Test
  @Disabled // test takes about an hour, so do not run it automatically
  public void testErrorTolerantExactEquivalencePoliticalActors() throws IOException {
    Network net = MatrixSource.fromAdjacency(politicalActorsNetwork, false).getNetwork();

    RoleOperator<ConstMapping.OfInt> roleOp = Operators.composeRoleOp(
        Operators.composeConv(Operators.composeConv(
            DistanceOperators.EQUIVALENCE.regular().equitable()
                .of(NetworkView.fromNetworkRelation(net, Direction.OUTGOING)).make(),
            Converters.thresholdDistances((i, j) -> 1)), RoleOperators.BINARYRELATION.basic().symmetrize()),
        Converters.weakComponentsAsEquivalence());

    Iterable<ConstMapping.OfInt> fixedPointsIterable = StableRolesEnumeration.EQUIVALENCE
        .stableRolesUnderRestriction(roleOp, Converters.singleClassEquivalence(net.countMonadicIndices()).apply(null));

    int count = 0;
    int countRealFixedPoints = 0;
    Set<ConstMapping.OfInt> set = new HashSet<>();
    for (ConstMapping.OfInt fixedPoint : fixedPointsIterable) {
      assertNotNull(fixedPoint);
      assertTrue(set.add(fixedPoint));
      if (roleOp.apply(fixedPoint).equals(fixedPoint)) {
        ++countRealFixedPoints;
      }
      ++count;
    }
    assertEquals(566886, count);
    assertEquals(countRealFixedPoints, 56);
  }

  @Test
  public void testEquivalencePredAndSuccessorIterators() {
    final int elemsPerClass = 6;
    final int numClasses = 15;
    IntStream stream = IntStream.empty();
    for (int i = 0; i < elemsPerClass; ++i) {
      stream = IntStream.concat(stream, IntStream.range(0, numClasses));
    }
    ConstMapping.OfInt equivalence = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(
        stream.boxed().collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
          Collections.shuffle(list);
          return list.stream();
        })).mapToInt(x -> x).toArray()));
    Random rand = new Random();
    int eqclazz1tosplit = 3 + rand.nextInt(numClasses - 4);
    int eqclazz2tosplittemp = 3 + rand.nextInt(numClasses - 5);
    int eqclazz2tosplit = eqclazz2tosplittemp + (eqclazz2tosplittemp >= eqclazz1tosplit ? 1 : 0);
    int[] refinedEq = equivalence.toUnboxedArray();
    for (int i = 0; i < refinedEq.length; ++i) {
      if (refinedEq[i] == eqclazz1tosplit) {
        refinedEq[i] = numClasses + rand.nextInt(3);
      } else if (refinedEq[i] == eqclazz2tosplit) {
        refinedEq[i] = numClasses + 3 + rand.nextInt(3);
      }
    }
    ConstMapping.OfInt refinedEquivalence = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(refinedEq));
    ConstMapping.OfInt coarsenedEquivalence = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(equivalence
        .intStream().map(x -> x == eqclazz1tosplit || x == eqclazz2tosplit ? eqclazz1tosplit : x).toArray()));

    Set<ConstMapping.OfInt> foundPredecessors = new HashSet<>();
    CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt> predEnumerator = CoverEnumerators
        .lowerCoversEquivalences(equivalence);
    int count = 0;
    boolean previouslyIteratedAncestor = false;

    while (predEnumerator.hasNext()) {
      Mapping.OfInt predecessor = predEnumerator.next();
      assertEquals(numClasses, predecessor.intStream().max().getAsInt());
      assertEquals(equivalence.size(), predecessor.size());
      assertTrue(foundPredecessors.add(predecessor));
      for (int i = 0; i < equivalence.size(); ++i) {
        for (int j = 0; j < equivalence.size(); ++j) {
          assertTrue(predecessor.getInt(i) != predecessor.getInt(j) || equivalence.getInt(i) == equivalence.getInt(j));
        }
      }
      ++count;
      boolean isRefiningCurrentPredecessor = true;
      for (int i = 0; i < equivalence.size(); ++i) {
        for (int j = 0; j < equivalence.size(); ++j) {
          if (predecessor.getInt(i) != predecessor.getInt(j)
              && refinedEquivalence.getInt(i) == refinedEquivalence.getInt(j)) {
            isRefiningCurrentPredecessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor |= isRefiningCurrentPredecessor;
      boolean hasIteratedAncestor = predEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedEquivalence,
          predecessor);
      assertEquals(previouslyIteratedAncestor, hasIteratedAncestor);
      assertFalse(predEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedEquivalence, predecessor));
    }
    assertEquals(numClasses * ((1 << (elemsPerClass - 1)) - 1), count);
    assertThrows(NoSuchElementException.class, () -> predEnumerator.next());

    Set<ConstMapping.OfInt> foundSuccessors = new HashSet<>();
    CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt> succEnumerator = CoverEnumerators
        .upperCoversEquivalences(equivalence);
    count = 0;
    previouslyIteratedAncestor = false;
    while (succEnumerator.hasNext()) {
      Mapping.OfInt successor = succEnumerator.next();
      assertEquals(numClasses - 2, successor.intStream().max().getAsInt());
      assertEquals(equivalence.size(), successor.size());
      assertTrue(foundSuccessors.add(successor));
      for (int i = 0; i < equivalence.size(); ++i) {
        for (int j = 0; j < equivalence.size(); ++j) {
          assertTrue(successor.getInt(i) == successor.getInt(j) || equivalence.getInt(i) != equivalence.getInt(j));
        }
      }
      boolean isCoarseningCurrentSuccessor = true;
      for (int i = 0; i < equivalence.size(); ++i) {
        for (int j = 0; j < equivalence.size(); ++j) {
          if (successor.getInt(i) == successor.getInt(j)
              && coarsenedEquivalence.getInt(i) != coarsenedEquivalence.getInt(j)) {
            isCoarseningCurrentSuccessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor |= isCoarseningCurrentSuccessor;
      boolean hasIteratedAncestor = succEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedEquivalence,
          successor);
      assertEquals(previouslyIteratedAncestor, hasIteratedAncestor);
      assertFalse(succEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedEquivalence, successor));
      ++count;
    }
    assertEquals(numClasses * (numClasses - 1) / 2, count);
    assertThrows(NoSuchElementException.class, () -> succEnumerator.next());
  }

  @Test
  public void testBinaryRelationsPredAndSuccessorIterators() {
    final int size = 40;
    boolean[][] relArray = new boolean[40][40];
    Random rand = new Random();
    BinaryRelation binrel;
    do {
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (i == j) {
            relArray[i][i] = true;
          } else {
            relArray[i][j] = rand.nextBoolean();
          }
        }
      }
      binrel = BinaryRelations.fromMatrix(relArray);
    } while (binrel.countRelationPairs() <= size * size / 5 && binrel.countRelationPairs() >= 4 * size * size / 5);

    boolean[][] coarsenedArray = Stream.of(relArray).map(x -> Arrays.copyOf(x, x.length)).toArray(boolean[][]::new);
    int numAdditions = 0;
    do {
      int i = rand.nextInt(size);
      int j = rand.nextInt(size);
      if (i != j && !coarsenedArray[i][j]) {
        coarsenedArray[i][j] = true;
        ++numAdditions;
      }
    } while (numAdditions < 5);
    BinaryRelation coarsenedRelation = BinaryRelations.fromMatrix(coarsenedArray);

    boolean[][] refinedArray = Stream.of(relArray).map(x -> Arrays.copyOf(x, x.length)).toArray(boolean[][]::new);
    numAdditions = 0;
    do {
      int i = rand.nextInt(size);
      int j = rand.nextInt(size);
      if (i != j && refinedArray[i][j]) {
        refinedArray[i][j] = false;
        ++numAdditions;
      }
    } while (numAdditions < 5);
    BinaryRelation refinedRelation = BinaryRelations.fromMatrix(refinedArray);

    Set<BinaryRelation> foundPredecessors = new HashSet<>();
    CoverEnumerator<BinaryRelation, BinaryRelation> predEnumerator = CoverEnumerators
        .lowerCoversBinaryRelations(binrel);
    int count = 0;
    boolean previouslyIteratedAncestor = false;

    while (predEnumerator.hasNext()) {
      BinaryRelation predecessor = predEnumerator.next();
      assertEquals(binrel.countRelationPairs() - 1, predecessor.countRelationPairs());
      assertEquals(binrel.domainSize(), predecessor.domainSize());
      assertTrue(foundPredecessors.add(predecessor));
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          assertTrue(binrel.contains(i, j) || !predecessor.contains(i, j));
        }
      }
      ++count;
      boolean isRefiningCurrentPredecessor = true;
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (!predecessor.contains(i, j) && refinedRelation.contains(i, j)) {
            isRefiningCurrentPredecessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor |= isRefiningCurrentPredecessor;
      boolean hasIteratedAncestor = predEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRelation,
          predecessor);
      assertEquals(previouslyIteratedAncestor, hasIteratedAncestor);
      assertFalse(predEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRelation, predecessor));
    }
    assertEquals(binrel.countRelationPairs(), count);
    assertThrows(NoSuchElementException.class, () -> predEnumerator.next());

    Set<BinaryRelation> foundSuccessors = new HashSet<>();
    CoverEnumerator<BinaryRelation, BinaryRelation> succEnumerator = CoverEnumerators
        .upperCoversBinaryRelations(binrel);
    count = 0;
    previouslyIteratedAncestor = false;
    while (succEnumerator.hasNext()) {
      BinaryRelation successor = succEnumerator.next();
      assertEquals(binrel.countRelationPairs() + 1, successor.countRelationPairs());
      assertEquals(binrel.domainSize(), successor.domainSize());
      assertTrue(foundSuccessors.add(successor));
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          assertTrue(successor.contains(i, j) || !binrel.contains(i, j));
        }
      }
      boolean isCoarseningCurrentSuccessor = true;
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (successor.contains(i, j) && !coarsenedRelation.contains(i, j)) {
            isCoarseningCurrentSuccessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor |= isCoarseningCurrentSuccessor;
      boolean hasIteratedAncestor = succEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRelation,
          successor);
      assertEquals(previouslyIteratedAncestor, hasIteratedAncestor);
      assertFalse(succEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRelation, successor));
      ++count;
    }
    assertEquals(size * size - binrel.countRelationPairs(), count);
    assertThrows(NoSuchElementException.class, () -> succEnumerator.next());
  }

  private static Ranking rankingFromOrderedPartition(ConstMapping.OfInt partition, boolean[][] ordering) {
    RelationBuilder<? extends Ranking> builder = RelationBuilders.denseSafeRankingBuilder(partition.size());
    for (int i = 0; i < partition.size(); ++i) {
      for (int j = 0; j < partition.size(); ++j) {
        if (partition.getInt(i) == partition.getInt(j) || ordering[partition.getInt(i)][partition.getInt(j)]) {
          builder.add(i, j);
        }
      }
    }
    return builder.build();
  }

  @Test
  public void testRankingPredAndSuccessorIterators() {

    final int elemsPerClass = 6;
    final int numClasses = 15;
    IntStream stream = IntStream.empty();
    for (int i = 0; i < elemsPerClass; ++i) {
      stream = IntStream.concat(stream, IntStream.range(0, numClasses));
    }
    ConstMapping.OfInt equivalence = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(
        stream.boxed().collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
          Collections.shuffle(list);
          return list.stream();
        })).mapToInt(x -> x).toArray()));

    boolean[][] relArray = new boolean[numClasses][numClasses];
    Random rand = new Random();
    int numAdditions = 0;
    do {
      int i = rand.nextInt(numClasses);
      int j = rand.nextInt(numClasses - 1);
      if (j >= i) {
        ++j;
      }
      if (!relArray[i][j] && !relArray[j][i]) {
        for (int k = 0; k < numClasses; ++k) {
          if (k != j && (relArray[k][i] || (k == i)) && !relArray[k][j]) {
            relArray[k][j] = true;
            for (int l = 0; l < numClasses; ++l) {
              if (!relArray[k][l] && relArray[j][l]) {
                relArray[k][l] = true;
              }
            }
          }
        }
        ++numAdditions;
      }
    } while (numAdditions < 8);
    int nontransitivePairs = 0;
    int nontransitivelyImplyingUnsetPairs = 0;
    for (int i = 0; i < numClasses; ++i) {
      for (int j = 0; j < numClasses; ++j) {
        if (relArray[i][j]) {
          boolean transitivelyImplied = false;
          for (int k = 0; k < numClasses; ++k) {
            if (i != k && j != k && relArray[i][k] && relArray[k][j]) {
              transitivelyImplied = true;
              break;
            }
          }
          if (!transitivelyImplied) {
            ++nontransitivePairs;
          }
        } else if (i != j) {

          boolean transitivelyImplying = false;
          for (int k = 0; k < numClasses; ++k) {
            if (i != k && j != k && ((!relArray[i][k] && relArray[j][k]) || (!relArray[k][j] && relArray[k][i]))) {
              transitivelyImplying = true;
              break;
            }
          }
          if (!transitivelyImplying) {
            ++nontransitivelyImplyingUnsetPairs;
          }
        }
      }
    }

    Ranking ranking = rankingFromOrderedPartition(equivalence, relArray);

    boolean[][] coarsenedArray = Stream.of(relArray).map(x -> Arrays.copyOf(x, x.length)).toArray(boolean[][]::new);

    numAdditions = 0;
    do {
      int i = rand.nextInt(numClasses);
      int j = rand.nextInt(numClasses - 1);
      if (j >= i) {
        ++j;
      }
      if (i != j && !coarsenedArray[i][j] && !coarsenedArray[j][i]) {
        for (int k = 0; k < numClasses; ++k) {
          if (k != j && (coarsenedArray[k][i] || (k == i)) && !coarsenedArray[k][j]) {
            coarsenedArray[k][j] = true;
            for (int l = 0; l < numClasses; ++l) {
              if (!coarsenedArray[k][l] && coarsenedArray[j][l]) {
                coarsenedArray[k][l] = true;
              }
            }
          }
        }
        ++numAdditions;
      }
    } while (numAdditions < 3);
    Ranking coarsenedRanking1 = rankingFromOrderedPartition(equivalence, coarsenedArray);

    int mergeClass1 = rand.nextInt(numClasses);
    int temp = rand.nextInt(numClasses - 1);
    int mergeClass2 = temp + (mergeClass1 <= temp ? 1 : 0);
    ConstMapping.OfInt coarsenedEq = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(
        equivalence.intStream().map(x -> x == mergeClass1 || x == mergeClass2 ? mergeClass1 : x).toArray()));
    int largerMergedClass = Math.max(mergeClass1, mergeClass2);
    int smallerMergedClass = Math.min(mergeClass1, mergeClass2);
    boolean[][] coarsenedArray2 = new boolean[numClasses - 1][numClasses - 1];
    for (int i = 0; i < numClasses; ++i) {
      for (int j = 0; j < numClasses; ++j) {
        coarsenedArray2[i == largerMergedClass ? smallerMergedClass
            : (i > largerMergedClass ? i - 1 : i)][j == largerMergedClass ? smallerMergedClass
                : (j > largerMergedClass ? j - 1 : j)] = relArray[i][j];
      }
    }
    Ranking coarsenedRanking2 = rankingFromOrderedPartition(coarsenedEq, coarsenedArray2);

    boolean[][] refinedArray = Stream.of(relArray).map(x -> Arrays.copyOf(x, x.length)).toArray(boolean[][]::new);
    numAdditions = 0;
    loop: do {
      int i = rand.nextInt(numClasses);
      int j = rand.nextInt(numClasses - 1);
      if (j >= i) {
        ++j;
      }
      if (refinedArray[i][j]) {
        for (int k = 0; k < numClasses; ++k) {
          if (i != k && j != k && refinedArray[i][k] && refinedArray[k][i]) {
            continue loop;
          }
        }
        refinedArray[i][j] = false;
        ++numAdditions;
      }
    } while (numAdditions < 2);
    Ranking refinedRanking1 = rankingFromOrderedPartition(equivalence, refinedArray);
    boolean[][] refinedArray2 = new boolean[numClasses + 2][numClasses + 2];
    int[] refinedEq = Arrays.copyOf(equivalence.toUnboxedArray(), equivalence.size());

    for (int i = 0; i < refinedEq.length; ++i) {
      if (refinedEq[i] == mergeClass1) {
        refinedEq[i] = numClasses + rand.nextInt(3);
      }
    }
    ConstMapping.OfInt refinedEquivalence = Equivalences.normalizePartition(Mappings.wrapUnmodifiableInt(refinedEq));
    for (int i = 0; i < refinedEquivalence.size(); ++i) {
      int originalClassi = equivalence.getInt(i);
      int refinedClassi = refinedEquivalence.getInt(i);
      for (int j = 0; j < refinedEquivalence.size(); ++j) {
        int originalClassj = equivalence.getInt(j);
        int refinedClassj = refinedEquivalence.getInt(j);
        refinedArray2[refinedClassi][refinedClassj] = relArray[originalClassi][originalClassj];
        if (originalClassi == mergeClass1 && originalClassj == mergeClass1) {
          refinedArray2[refinedClassi][refinedClassj] = refinedClassi < refinedClassj;
        }
      }
    }
    Ranking refinedRanking2 = rankingFromOrderedPartition(refinedEquivalence, refinedArray2);

    Set<Ranking> foundPredecessors = new HashSet<>();
    CoverEnumerator<Ranking, Ranking> predEnumerator = CoverEnumerators.lowerCoversRankings(ranking);
    int count = 0;
    boolean previouslyIteratedAncestor1 = false;
    boolean previouslyIteratedAncestor2 = false;

    while (predEnumerator.hasNext()) {
      Ranking predecessor = predEnumerator.next();
      assertEquals(ranking.domainSize(), predecessor.domainSize());
      // assertEquals(ranking.countRelationPairs() - 1,
      // predecessor.countRelationPairs());

      assertTrue(foundPredecessors.add(predecessor));
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          assertTrue(ranking.contains(i, j) || !predecessor.contains(i, j));
        }
      }
      ++count;
      boolean isRefiningCurrentPredecessor = true;
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          if (!predecessor.contains(i, j) && refinedRanking1.contains(i, j)) {
            isRefiningCurrentPredecessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor1 |= isRefiningCurrentPredecessor;
      boolean hasIteratedAncestor = predEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRanking1,
          predecessor);
      assertEquals(previouslyIteratedAncestor1, hasIteratedAncestor);

      isRefiningCurrentPredecessor = true;
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          if (!predecessor.contains(i, j) && refinedRanking2.contains(i, j)) {
            isRefiningCurrentPredecessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor2 |= isRefiningCurrentPredecessor;
      hasIteratedAncestor = predEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRanking2, predecessor);
      assertEquals(previouslyIteratedAncestor2, hasIteratedAncestor);

      assertFalse(predEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRanking1, predecessor));
      assertFalse(predEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRanking2, predecessor));
    }
    assertEquals(numClasses * ((1 << elemsPerClass) - 2) + nontransitivePairs, count);
    assertThrows(NoSuchElementException.class, () -> predEnumerator.next());

    Set<Ranking> foundSuccessors = new HashSet<>();
    CoverEnumerator<Ranking, Ranking> succEnumerator = CoverEnumerators.upperCoversRankings(ranking);
    count = 0;
    previouslyIteratedAncestor1 = false;
    previouslyIteratedAncestor2 = false;
    while (succEnumerator.hasNext()) {
      Ranking successor = succEnumerator.next();
      // assertEquals(binrel.countRelationPairs() + 1,
      // successor.countRelationPairs());
      assertEquals(ranking.domainSize(), successor.domainSize());
      assertTrue(foundSuccessors.add(successor));
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          assertTrue(successor.contains(i, j) || !ranking.contains(i, j));
        }
      }
      boolean isCoarseningCurrentSuccessor = true;
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          if (successor.contains(i, j) && !coarsenedRanking1.contains(i, j)) {
            isCoarseningCurrentSuccessor = false;
            break;
          }
        }
      }
      if (isCoarseningCurrentSuccessor) {
        int val = 0;
        val = val + 1;
      }
      previouslyIteratedAncestor1 |= isCoarseningCurrentSuccessor;
      boolean hasIteratedAncestor = succEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRanking1,
          successor);
      assertEquals(previouslyIteratedAncestor1, hasIteratedAncestor);

      isCoarseningCurrentSuccessor = true;
      for (int i = 0; i < ranking.domainSize(); ++i) {
        for (int j = 0; j < ranking.domainSize(); ++j) {
          if (successor.contains(i, j) && !coarsenedRanking2.contains(i, j)) {
            isCoarseningCurrentSuccessor = false;
            break;
          }
        }
      }
      previouslyIteratedAncestor2 |= isCoarseningCurrentSuccessor;
      hasIteratedAncestor = succEnumerator.isThereAncestorWhichIsCoverProducedBefore(coarsenedRanking2, successor);
      assertEquals(previouslyIteratedAncestor2, hasIteratedAncestor);
      assertFalse(succEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRanking1, successor));
      assertFalse(succEnumerator.isThereAncestorWhichIsCoverProducedBefore(refinedRanking2, successor));
      ++count;
    }
    assertEquals(nontransitivelyImplyingUnsetPairs, count);
    assertThrows(NoSuchElementException.class, () -> succEnumerator.next());
  }

  @Test
  public void testBinaryRelationProjections() {
    assertTrue(ProjectionEnumerators
        .projectionEquals(ProjectionEnumerators.projectRelation(BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, true }, //
            { false, true, true, false }, //
            { false, false, true, true }, //
            { true, false, true, false } }), 5), new boolean[] { true, false, false, true, false }, 5));
    assertFalse(ProjectionEnumerators
        .projectionEquals(ProjectionEnumerators.projectRelation(BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, true }, //
            { false, true, true, false }, //
            { false, false, true, true }, //
            { true, false, true, false } }), 5), new boolean[] { true, false, true, true, false }, 5));
    assertTrue(ProjectionEnumerators
        .projectionEquals(ProjectionEnumerators.projectRelation(BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, true }, //
            { false, true, true, false }, //
            { false, false, true, true }, //
            { true, false, true, false } }), 6), new boolean[] { true, false, false, true, false, false }, 5));
    assertTrue(ProjectionEnumerators
        .projectionEquals(ProjectionEnumerators.projectRelation(BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, true }, //
            { false, true, true, false }, //
            { false, false, true, true }, //
            { true, false, true, false } }), 13), new boolean[] { true, false, false, true, false, false }, 5));

    assertThrows(IllegalArgumentException.class,
        () -> ProjectionEnumerators.projectRelation(BinaryRelations.fromMatrix(new boolean[][] { //
            { true, false, false, true }, //
            { false, true, true, false }, //
            { false, false, true, true }, //
            { true, false, true, false } }), 17));

    assertEquals(Collections.singletonList(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, true }, //
        { true, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    })), StreamSupport
        .stream(ProjectionEnumerators.extremalExtensionBinaryRelations(new boolean[] { true, false, false, true, true,
            true, true, false, true, false, true, true, true, false, false, false }, 5, 16, false).spliterator(), false)
        .collect(Collectors.toList()));
    assertEquals(Collections.singletonList(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, false, true }, //
        { false, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    })), StreamSupport
        .stream(
            ProjectionEnumerators.extremalExtensionBinaryRelations(new boolean[] { true, false, false, true, false,
                true, true, false, true, false, true, true, true, false, false, false }, 5, 16, true).spliterator(),
            false)
        .collect(Collectors.toList()));
    assertThrows(NoSuchElementException.class, () -> {
      Iterator<BinaryRelation> it = ProjectionEnumerators.extremalExtensionBinaryRelations(new boolean[] { true, false,
          false, true, true, true, true, false, true, false, true, true, true, false, false, false }, 5, 16, false)
          .iterator();
      it.next();
      it.next();
    });
    assertThrows(IllegalArgumentException.class,
        () -> ProjectionEnumerators.projectionToBinaryRelation(new boolean[16], 5));
    assertThrows(IndexOutOfBoundsException.class,
        () -> ProjectionEnumerators.projectionToBinaryRelation(new boolean[5], 16));

    Set<ArrayList<Boolean>> result = new HashSet<>();
    for (boolean[] extension : ProjectionEnumerators
        .generateExtensionsBinaryRelations(new boolean[] { false, true, false, false, true, false, false }, 4)) {
      ArrayList<Boolean> list = new ArrayList<>();
      for (int i = 0; i < 5; ++i) {
        list.add(extension[i]);
      }
      result.add(list);
    }
    assertEquals(
        Stream.of(Arrays.asList(false, true, false, false, true), Arrays.asList(false, true, false, false, false))
            .collect(Collectors.toSet()),
        result);

    assertThrows(NoSuchElementException.class, () -> {
      Iterator<boolean[]> it = ProjectionEnumerators
          .generateExtensionsBinaryRelations(new boolean[] { false, true, false, false, true, false, false }, 4)
          .iterator();
      it.next();
      it.next();
      it.next();
    });

    assertTrue(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, true }, 5));
    assertTrue(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, false }, 5));
    assertFalse(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, true, true, false, true }, 5));
    assertTrue(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, false }, 5));
    assertTrue(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, true, true, true }, //
        { true, true, false, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, false }, 5));
    assertTrue(ProjectionEnumerators.someExtensionPrecedesRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    }), new boolean[] { true, false, true, false, false }, 5));

    assertTrue(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    }), new boolean[] { true, false, true, false, true }, 5));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    }), new boolean[] { true, true, true, false, true }, 5));
    assertFalse(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { true, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    }), new boolean[] { true, false, true, false, false }, 5));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
        { false, false, false, false }, //
    }), new boolean[] { true, false, true, false, true }, 5));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, true, true, true }, //
        { true, true, false, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, true }, 5));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsRelation(BinaryRelations.fromMatrix(new boolean[][] { //
        { true, false, true, false }, //
        { false, true, true, true }, //
        { true, true, true, true }, //
        { true, true, true, true }, //
    }), new boolean[] { true, false, true, false, true }, 5));
  }

  @Test
  public void testEquivalenceProjections() {
    assertTrue(ProjectionEnumerators.projectionEquals(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2),
        ProjectionEnumerators.projectEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1, 3, 2, 3, 4), 7),
        7));
    assertTrue(ProjectionEnumerators.projectionEquals(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2),
        ProjectionEnumerators.projectEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2, 3, 2, 3, 4), 7),
        7));
    assertFalse(ProjectionEnumerators.projectionEquals(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 1, 2),
        ProjectionEnumerators.projectEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1, 3, 2, 3, 4), 7),
        7));

    assertEquals(Collections.singleton(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3, 4, 5, 6, 7, 8)),
        StreamSupport.stream(ProjectionEnumerators
            .minimalExtensionEquivalences(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1, 3, 2, 3, 4), 7, 13)
            .spliterator(), false).collect(Collectors.toSet()));

    assertEquals(Stream
        .of(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 0), Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1),
            Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2), Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3))
        .collect(Collectors.toSet()),
        StreamSupport.stream(ProjectionEnumerators
            .generateExtensionsEquivalences(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1, 3, 2, 3, 4), 7)
            .spliterator(), false).collect(Collectors.toSet()));

    assertThrows(NoSuchElementException.class, () -> {
      Iterator<ConstMapping.OfInt> it = ProjectionEnumerators
          .generateExtensionsEquivalences(Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 1, 3, 2, 3, 4), 7)
          .iterator();
      for (int i = 0; i < 5; ++i) {
        it.next();
      }
    });

    assertTrue(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2, 1, 0, 2, 3, 2), 7));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2), 7));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 1, 3, 3, 4, 5, 6, 7, 8, 9),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2, 1, 0, 2, 3, 2), 7));
    assertFalse(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 0, 2, 3, 4, 5, 6, 7, 8),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2, 1, 0, 2, 3, 2), 7));
    assertFalse(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2, 1, 0, 2, 3, 2), 7));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3, 4, 5, 6, 5, 7),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 2), 7));
    assertTrue(ProjectionEnumerators.someExtensionSucceedsEquivalence(
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2, 3, 4, 5, 1, 6, 7),
        Mappings.wrapUnmodifiableInt(0, 1, 0, 0, 1, 2, 2), 7));
  }

  @Test
  public void testEconomicalEquivalencePoliticalActors() throws IOException {
    Network net = MatrixSource.fromAdjacency(politicalActorsNetwork, false).getNetwork();

    Iterable<ConstMapping.OfInt> fixedPointsIterable = StableRolesEnumeration.EQUIVALENCE.stableRolesUnderExtension(
        RoleOperators.EQUIVALENCE.regular().of(NetworkView.fromNetworkRelation(net, Direction.OUTGOING)).make(),
        Converters.singleClassEquivalence(net.countMonadicIndices()).apply(null));

    int count = 0;
    Iterator<ConstMapping.OfInt> iterator = fixedPointsIterable.iterator();
    while (iterator.hasNext()) {
      ConstMapping.OfInt fixedPoint = iterator.next();
      assertNotNull(fixedPoint);
      ++count;
    }
    assertEquals(1, count);
    assertThrows(NoSuchElementException.class, () -> iterator.next());
  }

  @Test
  public void testEconomicalEquivalenceCycleNetwork() throws IOException {
    Network net = MatrixSource.fromAdjacency(cycleNetwork, false).getNetwork();

    RoleOperator<ConstMapping.OfInt> roleOp = RoleOperators.EQUIVALENCE.regular()
        .of(NetworkView.fromNetworkRelation(net, Direction.OUTGOING)).make();
    Iterable<ConstMapping.OfInt> fixedPointsIterable = StableRolesEnumeration.EQUIVALENCE
        .stableRolesUnderExtension(roleOp, Mappings.intRange(0, net.countMonadicIndices()));

    int count = 0;
    Iterator<ConstMapping.OfInt> iterator = fixedPointsIterable.iterator();
    while (iterator.hasNext()) {
      ConstMapping.OfInt fixedPoint = iterator.next();
      assertNotNull(fixedPoint);
      assertEquals(fixedPoint, roleOp.extend(fixedPoint));
      ++count;
    }
    assertEquals(142, count);
    assertThrows(NoSuchElementException.class, () -> iterator.next());
  }
}
