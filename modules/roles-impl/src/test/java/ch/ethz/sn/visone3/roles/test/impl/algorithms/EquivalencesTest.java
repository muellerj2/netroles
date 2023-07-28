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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

public class EquivalencesTest {

  @Test
  public void strongStructuralEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8),
        Equivalences.strongStructuralEquivalence(n, incomingView));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 8),
        Equivalences.strongStructuralEquivalence(n, outgoingView));
  }

  @Test
  public void weakStructuralEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.weakStructuralEquivalence(n, incomingView, null));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.weakStructuralEquivalence(n, outgoingView, null));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.weakStructuralEquivalence(n, incomingView, outgoingView));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.weakStructuralEquivalence(n, outgoingView, incomingView));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningWeakStructuralEquivalence(n, incomingView, null,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1, 1, 1, 1, 1)));

    assertThrows(IllegalArgumentException.class,
        () -> Equivalences.refiningWeakStructuralEquivalence(n, incomingView, null,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1, 1, 1, 1, 3)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningWeakStructuralEquivalence(n, outgoingView, null,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningWeakStructuralEquivalence(n, incomingView, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningWeakStructuralEquivalence(n, outgoingView, incomingView,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1, 1, 1, 1, 1)));
  }

  @Test
  public void relativeRegularEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 1, 1, 2, 2, 2),
        Equivalences.relativeRegularEquivalence(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 0, 1, 1, 1, 2, 2, 2),
        Equivalences.relativeRegularEquivalence(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));
  }

  @Test
  public void relativeExactEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.relativeExactEquivalence(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 0, 3, 4, 5, 6, 7, 7),
        Equivalences.relativeExactEquivalence(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)));
  }

  @Test
  public void refiningRelativeRegularEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 3, 3, 4, 4, 4),
        Equivalences.refiningRelativeRegularEquivalence(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 3, 3, 4, 4, 4),
        Equivalences.refiningRelativeRegularEquivalence(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1)));
  }

  @Test
  public void refiningRelativeExactEquivalenceTest() {
    final Network network = createNetwork();
    final int n = network.asRelation().countUnionDomain();
    final NetworkView<?, ?> incomingView = NetworkView.fromNetworkRelation(network,
        Direction.INCOMING);
    final NetworkView<?, ?> outgoingView = NetworkView.fromNetworkRelation(network,
        Direction.OUTGOING);

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningRelativeExactEquivalence(n, incomingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1)));

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 8),
        Equivalences.refiningRelativeExactEquivalence(n, outgoingView,
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1),
            Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 0, 1, 1, 1, 1, 1)));
  }

  @Test
  public void infimumTest() {

    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 3, 4, 2, 3, 5, 6, 5),
        Equivalences.infimum(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 2, 1, 1, 3, 3, 3),
            Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 2, 2, 3, 2)));
  }

  @Test
  public void supremumTest() {
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 1, 1, 1, 2, 3, 1, 4, 4),
        Equivalences.supremum(Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 3, 2, 2, 4, 5, 2, 6, 7),
            Mappings.wrapUnmodifiableInt(0, 1, 2, 2, 3, 3, 3, 4, 5, 6, 7, 7)));
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
     *       +-2      3-+ \|
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
        { z, z, z, z, z, 1, z, z, 1, z } //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, false);
    return s.getNetwork();
  }

  private static final boolean[][] adjComponents = new boolean[][] { //
      { false, true, false, false, false, false }, //
      { true, false, true, false, false, false }, //
      { false, true, false, false, false, false }, //
      { false, false, false, false, false, true }, //
      { false, false, false, false, false, true }, //
      { false, false, false, true, false, false }, //
  };

  @Test
  public void testEquivalenceFromComponents() {

    BinaryRelation relation = BinaryRelations.fromMatrix(adjComponents);
    ConstMapping.OfInt refinement = Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 0, 1);
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1),
        Converters.weakComponentsAsEquivalence().convert(relation));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1),
        Converters.weakComponentsAsEquivalence().apply(relation));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 0, 2, 3, 2),
        Converters.weakComponentsAsEquivalence().convertRefining(relation, refinement));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0),
        Converters.weakComponentsAsEquivalence().convertCoarsening(relation, refinement));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 1),
        Converters.weakComponentsAsEquivalence().convertCoarsening(relation,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 2, 3, 3)));
    assertTrue(Converters.weakComponentsAsEquivalence().isIsotone());
    assertFalse(Converters.weakComponentsAsEquivalence().isConstant());
    assertTrue(Converters.weakComponentsAsEquivalence().isNondecreasing());
    assertFalse(Converters.weakComponentsAsEquivalence().isNonincreasing());

    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 2, 1),
        Converters.strongComponentsAsEquivalence().convert(relation));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 2, 1),
        Converters.strongComponentsAsEquivalence().apply(relation));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 0, 2, 3, 2),
        Converters.strongComponentsAsEquivalence().convertRefining(relation, refinement));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0),
        Converters.strongComponentsAsEquivalence().convertCoarsening(relation, refinement));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 1, 0),
        Converters.strongComponentsAsEquivalence().convertCoarsening(relation,
            Mappings.wrapUnmodifiableInt(0, 1, 0, 1, 2, 1)));
    assertTrue(Converters.strongComponentsAsEquivalence().isIsotone());
    assertFalse(Converters.strongComponentsAsEquivalence().isConstant());
    assertFalse(Converters.strongComponentsAsEquivalence().isNondecreasing());
    assertFalse(Converters.strongComponentsAsEquivalence().isNonincreasing());
  }

  @Test
  public void testEquivalenceMeetAndJoin() {
    final int size = 20;
    final Random rand = new Random();
    int[] eq1arr = new int[size];
    int[] eq2arr = new int[size];
    int[] eq3arr = new int[size];
    for (int i = 1; i < size; ++i) {
      eq1arr[i] = rand.nextInt(2);
      eq2arr[i] = rand.nextInt(2);
      eq3arr[i] = rand.nextInt(2);
    }
    int[] eq4arr = IntStream.range(0, size).toArray();
    int[] eq5arr = IntStream.range(0, size).toArray();
    int[] eq6arr = IntStream.range(0, size).toArray();
    PrimitiveList.OfLong mergedPairs4 = Mappings.newLongList();
    PrimitiveList.OfLong mergedPairs5 = Mappings.newLongList();
    PrimitiveList.OfLong mergedPairs6 = Mappings.newLongList();
    for (int i = 0; i < 5; ++i) {
      mergedPairs4.add(IntPair.set(rand.nextInt(size), rand.nextInt(size)));
      mergedPairs5.add(IntPair.set(rand.nextInt(size), rand.nextInt(size)));
      mergedPairs6.add(IntPair.set(rand.nextInt(size), rand.nextInt(size)));
    }
    Comparator<Long> comparator = (lhs, rhs) -> {
      int smalllhs = IntPair.first(lhs);
      int smallrhs = IntPair.first(rhs);
      int comp = Long.compare(smalllhs, smallrhs);
      if (comp != 0) {
        return comp;
      }
      int greatlhs = IntPair.second(lhs);
      int greatrhs = IntPair.second(rhs);
      return Long.compare(greatlhs, greatrhs);
    };
    mergedPairs4.sort(comparator);
    mergedPairs5.sort(comparator);
    mergedPairs6.sort(comparator);
    for (long val : mergedPairs4) {
      eq4arr[IntPair.second(val)] = eq4arr[IntPair.first(val)];
    }
    for (long val : mergedPairs5) {
      eq5arr[IntPair.second(val)] = eq5arr[IntPair.first(val)];
    }
    for (long val : mergedPairs6) {
      eq6arr[IntPair.second(val)] = eq6arr[IntPair.first(val)];
    }
    eq4arr = Equivalences.normalizePartition(eq4arr);
    eq5arr = Equivalences.normalizePartition(eq5arr);
    eq6arr = Equivalences.normalizePartition(eq6arr);
    ConstMapping.OfInt eq1 = Mappings.wrapUnmodifiableInt(eq1arr);
    ConstMapping.OfInt eq2 = Mappings.wrapUnmodifiableInt(eq2arr);
    ConstMapping.OfInt eq3 = Mappings.wrapUnmodifiableInt(eq3arr);
    ConstMapping.OfInt eq4 = Mappings.wrapUnmodifiableInt(eq4arr);
    ConstMapping.OfInt eq5 = Mappings.wrapUnmodifiableInt(eq5arr);
    ConstMapping.OfInt eq6 = Mappings.wrapUnmodifiableInt(eq6arr);
    ConstMapping.OfInt meet = Reducers.EQUIVALENCE.meet().combine(eq1, eq2);
    String errorMessage = String.format("Equivalence %s is not infimum of %s and %s.", meet, eq1,
        eq2);
    for (int i = 0; i < size; ++i) {
      for (int j = i + 1; j < size; ++j) {
        assertEquals(eq1arr[i] == eq1arr[j] && eq2arr[i] == eq2arr[j],
            meet.getInt(i) == meet.getInt(j), errorMessage);
      }
    }
    assertEquals(meet, Reducers.EQUIVALENCE.meet().refine(eq1, eq2));
    assertEquals(Reducers.EQUIVALENCE.meet().combine(eq3, meet),
        Reducers.EQUIVALENCE.meet().refiningCombine(eq3, eq1, eq2));
    assertEquals(Equivalences.supremum(eq4, eq5), Reducers.EQUIVALENCE.meet().coarsen(eq4, eq5));
    assertEquals(Equivalences.supremum(eq4, Reducers.EQUIVALENCE.meet().combine(eq3, meet)),
        Reducers.EQUIVALENCE.meet().coarseningCombine(eq4, eq3, meet));
    assertTrue(Reducers.EQUIVALENCE.meet().isAssociative());
    assertTrue(Reducers.EQUIVALENCE.meet().isCommutative());
    assertTrue(Reducers.EQUIVALENCE.meet().isIsotone());
    assertTrue(Reducers.EQUIVALENCE.meet().isNonincreasing());
    assertFalse(Reducers.EQUIVALENCE.meet().isNondecreasing());
    assertFalse(Reducers.EQUIVALENCE.meet().isConstant());

    ConstMapping.OfInt join = Reducers.EQUIVALENCE.join().combine(eq4, eq5);
    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    builder.ensureNode(size - 1);
    int[] reps = new int[size];
    Arrays.fill(reps, -1);
    for (int i = 0; i < size; ++i) {
      int eqclass = eq4arr[i];
      if (reps[eqclass] < 0) {
        reps[eqclass] = i;
      } else {
        builder.addEdge(reps[eqclass], i);
      }
    }
    Arrays.fill(reps, -1);
    for (int i = 0; i < size; ++i) {
      int eqclass = eq5arr[i];
      if (reps[eqclass] < 0) {
        reps[eqclass] = i;
      } else {
        builder.addEdge(reps[eqclass], i);
      }
    }
    ConstMapping.OfInt components = Equivalences.normalizePartition(
        AlgoProvider.getInstance().connectedness().components(builder.build().asUndirectedGraph()));
    assertEquals(components, join);

    assertEquals(join, Reducers.EQUIVALENCE.join().coarsen(eq4, eq5));
    assertEquals(Reducers.EQUIVALENCE.join().combine(eq6, join),
        Reducers.EQUIVALENCE.join().coarseningCombine(eq6, eq4, eq5));
    assertEquals(meet, Reducers.EQUIVALENCE.join().refine(eq1, eq2));
    assertEquals(Equivalences.infimum(eq1, Reducers.EQUIVALENCE.join().combine(eq6, join)),
        Reducers.EQUIVALENCE.join().refiningCombine(eq1, eq6, join));
    assertTrue(Reducers.EQUIVALENCE.join().isAssociative());
    assertTrue(Reducers.EQUIVALENCE.join().isCommutative());
    assertTrue(Reducers.EQUIVALENCE.join().isIsotone());
    assertTrue(Reducers.EQUIVALENCE.join().isNondecreasing());
    assertFalse(Reducers.EQUIVALENCE.join().isNonincreasing());
    assertFalse(Reducers.EQUIVALENCE.join().isConstant());

  }

  @Test
  public void testSingleClassEquivalenceConverter() {
    final int size = 0;
    assertEquals(Mappings.repeated(0, size),
        Converters.singleClassEquivalence(size).apply(Mappings.intRange(0, size / 2)));
    assertEquals(Mappings.repeated(0, size),
        Converters.singleClassEquivalence(size).apply(new Object()));
    assertTrue(Converters.singleClassEquivalence(size).isConstant());
    assertTrue(Converters.singleClassEquivalence(size).isIsotone());
    assertTrue(Converters.singleClassEquivalence(size).isNondecreasing());
    assertFalse(Converters.singleClassEquivalence(size).isNonincreasing());
  }
}
