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
package example;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Predicate;

import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.lattice.StableRolesEnumeration;
import ch.ethz.sn.visone3.roles.position.NetworkView;

public class Examples {

  private Examples() {
  }

  public static void example1() throws IOException {

    Network network;
    NetworkView<?, ?> networkView;
    ConstMapping.OfInt initialEq;
    try (Source<?> source = IoProvider.getService("graphml").newSource(
        new File("examples/muellerbrandes_examplegraph.graphml"))) {
      SourceFormat sourceData = source.parse();
        
      network = sourceData.incidence();
      networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
      initialEq = (ConstMapping.OfInt) sourceData.monadic().get("initial");
      System.out.println(initialEq);
    }

    RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
        .of(network.asUndirectedGraph().countVertices(), networkView)
        .make();

    ConstMapping.OfInt prevEq;
    ConstMapping.OfInt currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = regularOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));

    ConstMapping.OfInt interior = regularOp.interior(initialEq);
    System.out.println(interior);
  }

  public static void example2() throws IOException {

    Network network;
    int n;
    NetworkView<?, ?> networkView;
    ConstMapping.OfInt initialEq;
    try (Source<?> source = IoProvider.getService("graphml").newSource( //
        new File("examples/doreianalbert_countypoliticians.graphml"))) {
      SourceFormat sourceData = source.parse();

      network = sourceData.incidence();
      n = network.asUndirectedGraph().countVertices();
      networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
      initialEq = (ConstMapping.OfInt) sourceData.monadic().get("initial");
      System.out.println(initialEq);
    }

    RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
        .of(network.asUndirectedGraph().countVertices(), networkView).make();

    ConstMapping.OfInt prevEq;
    ConstMapping.OfInt currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = regularOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));

    RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel( //
        Reducers.EQUIVALENCE.meet(), //
        Operators.composeRoleOp( //
            Operators.composeOp( //
                Operators.composeOp( // threshold pairwise distances from equitable equivalence by one
                    DistanceOperators.EQUIVALENCE.equitable().of(n, networkView).make(),
                    Converters.thresholdDistances((i, j) -> 1)),
                // symmetrize (at most distance one in both directions)
                RoleOperators.BINARYRELATION.basic().symmetrize()),
            Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
        RoleOperators.EQUIVALENCE.weak().of(n, networkView).make()); // and split off isolates

    currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = errortolerantOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));
  }

  public static void example3() throws IOException {
    Network network;
    int n;
    NetworkView<Relationship, Relationship> networkView;
    ConstMapping.OfInt wealth;
    try (Source<?> source = IoProvider.getService("graphml")
        .newSource(new File("examples/padgett_marriages.graphml"))) {
      SourceFormat sourceData = source.parse();

      network = sourceData.incidence();
      n = network.asUndirectedGraph().countVertices();
      networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
      wealth = (ConstMapping.OfInt) sourceData.monadic().get("wealth");
      System.out.println(wealth);
    }

    RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel( //
        Reducers.EQUIVALENCE.meet(), //
        Operators.composeRoleOp( //
            Operators.composeOp( //
                Operators.composeOp( //
                    // distance from wealth-aware regular equivalence
                    DistanceOperators.EQUIVALENCE.regular().of(n, networkView)
                        // substituting neighbor should have larger wealth
                        .comp(Comparator.comparingInt(rship -> wealth.getInt(rship.getRight()))).make(),
                    // thresholded by one
                    Converters.thresholdDistances((i, j) -> 1)),
                // symmetrize (at most distance one in both directions)
                RoleOperators.BINARYRELATION.basic().symmetrize()),
            Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
        RoleOperators.EQUIVALENCE.weak().of(n, networkView).make()); // and split off isolates

    ConstMapping.OfInt stableEquivalence = Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 0, 2, 0, 2, 0, 1, 3, 1, 0, 1, 0);
    System.out.println(errortolerantOp.relative(stableEquivalence));

    ConstMapping.OfInt lowerBound = errortolerantOp.closure(Mappings.intRange(0, n));
    ConstMapping.OfInt verticesByLowerBoundEqClass = PrimitiveCollections.countingSort(lowerBound);
    Predicate<ConstMapping.OfInt> skipIfNotCoarseningLowerBound = eq -> {
      for (int i = 1; i < verticesByLowerBoundEqClass.size(); ++i) {
        int currVertex = verticesByLowerBoundEqClass.getInt(i);
        int prevVertex = verticesByLowerBoundEqClass.getInt(i - 1);
        if (lowerBound.getInt(prevVertex) == lowerBound.getInt(currVertex)
            && eq.getInt(prevVertex) != eq.getInt(currVertex)) {
          return true;
        }
      }
      return false;
    };

    for (ConstMapping.OfInt stableUnderRestriction : StableRolesEnumeration.EQUIVALENCE
        .stableRolesUnderRestriction(errortolerantOp, Mappings.repeated(0, n), skipIfNotCoarseningLowerBound)) {
      if (stableUnderRestriction.equals(errortolerantOp.relative(stableUnderRestriction))) {
        System.out.println(stableUnderRestriction);
      }
    }
  }

  public static void main(String... args) throws IOException {
    example1();
    example2();
    example3();
  }
}
