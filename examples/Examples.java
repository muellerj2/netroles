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
package examples;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.Pair;
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
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.util.MultiplexNetworks;

/***
 * Full code of usage examples described in USAGE.md.
 */
public class Examples {

  private Examples() {
  }

  /**
   * Example: Representing and analyzing with classical notions of role
   * equivalence
   * 
   * @throws IOException if an IO problem occurs
   */
  public static void example1() throws IOException {

    System.out.println("Example 1: Classical notions of role equivalence");

    // load network
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

    // create role operator for regular roles
    RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
        .of(network.asUndirectedGraph().countVertices(), networkView)
        .make();

    // follow the sequence of relative regular equivalence starting from the initial equivalence
    ConstMapping.OfInt prevEq;
    ConstMapping.OfInt currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = regularOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));

    // compute the regular interior from the initial equivalence
    ConstMapping.OfInt interior = regularOp.interior(initialEq);
    System.out.println(interior);
  }

  /**
   * Example: Dealing with directness, and role structures other than equivalences
   * 
   * @throws IOException if an IO problem occurs
   */
  public static void example2() throws IOException {

    System.out.println("Example 2: Network directness and role structures beyond equivalences");

    // load network
    int n;
    Network network;
    try (Source<?> source = IoProvider.getService("graphml").newSource( //
        new File("examples/sampson_negativesanction.graphml"))) {
      SourceFormat sourceData = source.parse();

      network = sourceData.incidence();
      n = network.asDirectedGraph().countVertices();
    }
    NetworkView<?, ?> outgoingNetworkView = NetworkView. //
        fromNetworkRelation(network, Direction.OUTGOING);
    NetworkView<?, ?> incomingNetworkView = NetworkView. //
        fromNetworkRelation(network, Direction.INCOMING);

    // create role operators for regular roles
    RoleOperator<ConstMapping.OfInt> outgoingRegularOp = RoleOperators.EQUIVALENCE.regular()
        .of(n, outgoingNetworkView).make();
    RoleOperator<ConstMapping.OfInt> incomingRegularOp = RoleOperators.EQUIVALENCE.regular()
        .of(n, incomingNetworkView).make();
    RoleOperator<ConstMapping.OfInt> bidiRegularOp = Operators.parallel(
        // we apply the undicrectional operators in parallel
        // and combine them through intersection
        // (which is the meet of the equivalence lattice)
        Reducers.EQUIVALENCE.meet(), //
        outgoingRegularOp, // regular roles operator in outgoing direction only
        incomingRegularOp // regular roles operator in incoming direction only
      );

    // follow the sequence of relative regular equivalence starting from the initial
    // equivalence
    ConstMapping.OfInt initialEq = Mappings.repeated(0, n);
    
    System.out.println(outgoingRegularOp.relative(initialEq));
    System.out.println(incomingRegularOp.relative(initialEq));
    System.out.println(bidiRegularOp.relative(initialEq));

    System.out.println(outgoingRegularOp.interior(initialEq));
    System.out.println(incomingRegularOp.interior(initialEq));
    System.out.println(bidiRegularOp.interior(initialEq));

    RoleOperator<Ranking> outgoingRankedOp = RoleOperators.RANKING.regular() //
        .of(n, outgoingNetworkView).make();
    RoleOperator<Ranking> incomingRankedOp = RoleOperators.RANKING.regular() //
        .of(n, incomingNetworkView).make();
    RoleOperator<Ranking> bidiRankedOp = Operators.parallel(
        // combine undirectional ranked operators through intersection
        Reducers.RANKING.meet(), //
        outgoingRankedOp, // ranked regular roles operator in outgoing direction only
        incomingRankedOp // ranked regular roles operator in incoming direction only
    );

    Ranking initialRanking = Rankings.universal(n);
    // compute the role ranking relative to the single-class ranking
    System.out.println(bidiRankedOp.relative(initialRanking).toString().replace("],", "],\n"));

    // compute the maximum stable role equivalence, which is the interior of the
    // single-class ranking
    System.out.println(bidiRankedOp.interior(initialRanking).toString().replace("],", "],\n"));
  }

  /**
   * Example: Defining and working with error-tolerant role equivalences
   * 
   * @throws IOException if an IO problem occurs
   */
  public static void example3() throws IOException {

    System.out.println("Example 3: Working with error-tolerant role equivalences");

    // load network
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

    // role operator for regular roles
    RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
        .of(network.asUndirectedGraph().countVertices(), networkView).make();

    // follow the sequence of relative role equivalences
    // for the usual regular roles operator
    ConstMapping.OfInt prevEq;
    ConstMapping.OfInt currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = regularOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));

    // role operator for error-tolerant equitable roles
    // allows for up to one mismatch in pairwise comparisons in both directions
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

    // follow the sequence of relative role equivalences
    // for the error-tolerant regular roles operator
    currEq = initialEq;
    do {
      prevEq = currEq;
      currEq = errortolerantOp.relative(currEq);
      System.out.println(currEq);
    } while (!prevEq.equals(currEq));
  }

  /**
   * Example: Defining role notions handling node and tie attributes, and
   * enumerating stable role structures defined by role operators
   * 
   * @throws IOException if an IO problem occurs
   */
  public static void example4() throws IOException {

    System.out.println("Example 4: Incorporating attributes and stable role structure enumeration");

    // load the network
    Network network;
    int n;
    NetworkView<Relationship, Relationship> networkView;
    ConstMapping.OfInt wealth;
    try (Source<?> source = IoProvider.getService("graphml") //
        .newSource(new File("examples/padgett_marriages.graphml"))) {
      SourceFormat sourceData = source.parse();

      network = sourceData.incidence();
      n = network.asUndirectedGraph().countVertices();
      networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
      wealth = (ConstMapping.OfInt) sourceData.monadic().get("wealth");
      System.out.println(wealth);
    }

    // wealth-aware regular roles without error tolerance
    RoleOperator<ConstMapping.OfInt> wealthawareOp = RoleOperators.EQUIVALENCE.regular() //
        .of(n, networkView) // on this network
        // substituting neighbor should have larger wealth
        .comp(Comparator.comparingInt(rship -> wealth.getInt(rship.getRight()))).make();

    // print coarsest stable role equivalence without error tolerance
    System.out.println(wealthawareOp.interior(Mappings.repeated(0, n)));

    // define the error-tolerant wealth-aware role operator
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

    // try one equivalence
    ConstMapping.OfInt stableEquivalence = Mappings.wrapUnmodifiableInt(
        0, 0, 0, 1, 1, 0, 2, 0, 2, 0, 1, 3, 1, 0, 1, 0);
    System.out.println(errortolerantOp.relative(stableEquivalence));

    // search all stable role equivalences under this
    // error-tolerant wealth-aware notion of regular roles
    
    // determine which fixed points of the interior operator can be fixed
    // (namely all that are refining the minimum stable role equivalence,
    // which is the closure of the equivalence of singleton classes)
    // the predicate below returns true
    // if an equivalence refines the minimum role equivalence
    ConstMapping.OfInt lowerBound = errortolerantOp.closure(Mappings.intRange(0, n));
    ConstMapping.OfInt nodesByLowerBoundEqClass = PrimitiveCollections.countingSort(lowerBound);
    Predicate<ConstMapping.OfInt> skipIfNotCoarseningLowerBound = eq -> {
      for (int i = 1; i < nodesByLowerBoundEqClass.size(); ++i) {
        int currNode = nodesByLowerBoundEqClass.getInt(i);
        int prevNode = nodesByLowerBoundEqClass.getInt(i - 1);
        if (lowerBound.getInt(prevNode) == lowerBound.getInt(currNode)
            && eq.getInt(prevNode) != eq.getInt(currNode)) {
          return true;
        }
      }
      return false;
    };

    // then start the search for fixed points from the single-class equivalence
    for (ConstMapping.OfInt stableUnderRestriction : StableRolesEnumeration.EQUIVALENCE
        .stableRolesUnderRestriction(errortolerantOp, Mappings.repeated(0, n), skipIfNotCoarseningLowerBound)) {
      if (stableUnderRestriction.equals(errortolerantOp.relative(stableUnderRestriction))) {
        System.out.println(stableUnderRestriction);
      }
    }
  }

  /**
   * Example: Networks with multiple relations
   * 
   * @throws IOException if an IO problem occurs
   */
  public static void example5() throws IOException {

    System.out.println("Example 5: Handling multiple network relations");
    int n;
    Network marriageNetwork;
    ConstMapping.OfInt wealth;
    ConstMapping<String> familyNamesMarriages;
    try (Source<?> source = IoProvider.getService("graphml").newSource( //
        new File("examples/padgett_marriages.graphml"))) {
      SourceFormat sourceData = source.parse();

      marriageNetwork = sourceData.incidence();
      n = marriageNetwork.asUndirectedGraph().countVertices();
      wealth = (ConstMapping.OfInt) sourceData.monadic().get("wealth");
      familyNamesMarriages = Mappings.castExact(String.class, sourceData.monadic().get("id"));
    }
    Network businessNetwork;
    ConstMapping<String> familyNamesBusiness;
    try (Source<?> source = IoProvider.getService("graphml").newSource( //
        new File("examples/padgett_business.graphml"))) {
      SourceFormat sourceData = source.parse();

      businessNetwork = sourceData.incidence();
      if (businessNetwork.asUndirectedGraph().countVertices() != n) {
        throw new IOException("mismatch in number of vertices in marriage and business network");
      }
      familyNamesBusiness = Mappings.castExact(String.class, sourceData.monadic().get("id"));
    }

    // match family nodes in marriage and business relations based on family names
    int[] familiesBusinessToMarriage = new int[n];
    Map<String, Integer> marriageFamilyNamesToIndices = new HashMap<>();
    for (int i = 0; i < n; ++i) {
      if (marriageFamilyNamesToIndices.put(familyNamesMarriages.get(i), i) != null) {
        throw new IOException("family names not unique!");
      }
    }
    for (int i = 0; i < n; ++i) {
      Integer marriageIndex = marriageFamilyNamesToIndices.remove(familyNamesBusiness.get(i));
      if (marriageIndex == null) {
        throw new IOException("different family names in marriage and business network");
      }
      familiesBusinessToMarriage[i] = marriageIndex;
    }

    // construct the composite network from the marriage and business relations
    Pair<Network, Mapping.OfInt[]> result = MultiplexNetworks.multiplexDirected(
        Arrays.asList(new Pair<>(marriageNetwork, Direction.OUTGOING), //
            new Pair<>(businessNetwork, Direction.OUTGOING)),
        (netIndex, nodeIndex) -> {
          if (netIndex == 0) {
            return nodeIndex;
          } else if (netIndex == 1) {
            return familiesBusinessToMarriage[nodeIndex];
          }
          throw new IllegalArgumentException("Illegal network index");
        });
    Network compositeNetwork = result.getFirst();
    Mapping.OfInt[] tieIndexRemapping = result.getSecond();
    NetworkView<Relationship, Relationship> networkView = NetworkView.fromNetworkRelation(compositeNetwork,
        Direction.OUTGOING);

    // specify the error-tolerant role operator aware of wealth, marriage and
    // business ties
    RoleOperator<ConstMapping.OfInt> compositeOp = Operators.parallel( //
        Reducers.EQUIVALENCE.meet(), //
        Operators.composeRoleOp( //
            Operators.composeOp( //
                Operators.composeOp(
                    // distance from wealth-, marriage- and business-ties-aware regular equivalence
                    DistanceOperators.EQUIVALENCE.regular().of(n, networkView)
                        // substituting neighbor should have larger wealth
                        .comp(Comparator.comparingInt(tie -> wealth.getInt(tie.getRight())))
                        // substitution cost describing matching rules for marriage and business ties
                        .substCost((tiei, tiej) -> {
                          boolean tieiIsMarriage = tieIndexRemapping[0].getInt(tiei) >= 0;
                          boolean tieiIsBusiness = tieIndexRemapping[1].getInt(tiei) >= 0;
                          // marriage and business tie should be matched
                          int costEstimate = (tieiIsMarriage ? 1 : 0) + (tieiIsBusiness ? 1 : 0);
                          if (tiej == null) { // cost if unmatched/matching fails
                            return costEstimate;
                          }
                          // matching cost for substitution by tiej
                          boolean tiejIsMarriage = tieIndexRemapping[0].getInt(tiej) >= 0;
                          boolean tiejIsBusiness = tieIndexRemapping[1].getInt(tiej) >= 0;
                          // match with marriage tie if beneficial
                          if (tiejIsMarriage && costEstimate > 0) {
                            --costEstimate;
                          }
                          // match business with business tie if beneficial
                          if (tiejIsBusiness && tieiIsBusiness && costEstimate > 0) {
                            --costEstimate;
                          }
                          return costEstimate;
                        }).make(),
                    // thresholded by two
                    Converters.thresholdDistances((i, j) -> 2)),
                // symmetrize (at most distance two in both directions)
                RoleOperators.BINARYRELATION.basic().symmetrize()),
            Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
        RoleOperators.EQUIVALENCE.weak().of(n, networkView).make()); // and split off isolates

    ConstMapping.OfInt equivalence = Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 0, 2, 2, 0, 0, 2, 3, 0, 0, 4, 0);
    System.out.println(compositeOp.relative(equivalence));

    // search all stable role equivalences under this
    // error-tolerant notion of regular roles accounting for marriage and business
    // ties

    // determine which fixed points of the interior operator can be fixed
    // (namely all that are refining the minimum stable role equivalence,
    // which is the closure of the equivalence of singleton classes)
    // the predicate below returns true
    // if an equivalence refines the minimum role equivalence
    ConstMapping.OfInt lowerBound = compositeOp.closure(Mappings.intRange(0, n));
    ConstMapping.OfInt nodesByLowerBoundEqClass = PrimitiveCollections.countingSort(lowerBound);
    Predicate<ConstMapping.OfInt> skipIfNotCoarseningLowerBound = eq -> {
      for (int i = 1; i < nodesByLowerBoundEqClass.size(); ++i) {
        int currNode = nodesByLowerBoundEqClass.getInt(i);
        int prevNode = nodesByLowerBoundEqClass.getInt(i - 1);
        if (lowerBound.getInt(prevNode) == lowerBound.getInt(currNode)
            && eq.getInt(prevNode) != eq.getInt(currNode)) {
          return true;
        }
      }
      return false;
    };

    // then start the search for fixed points from the single-class equivalence
    for (ConstMapping.OfInt stableUnderRestriction : StableRolesEnumeration.EQUIVALENCE
        .stableRolesUnderRestriction(compositeOp, Mappings.repeated(0, n), skipIfNotCoarseningLowerBound)) {
      if (stableUnderRestriction.equals(compositeOp.relative(stableUnderRestriction))) {
        System.out.println(stableUnderRestriction);
      }
    }
  }
  public static void main(String... args) throws IOException {
    example1();
    example2();
    example3();
    example4();
    example5();
  }
}
