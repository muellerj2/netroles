# netroles

The netroles library offers tools for analyzing network data in terms of role equivalences and structures.

This library implements many common notions of role equivalences, such as structural, weak or regular equivalence in their standard formulation.  
While implementations in other software packages tend to be restricted to these standard equivalence concepts on unweighted networks, netroles has been deliberately designed to expand the scope of applicability for role equivalences. It facilitates the expression of alternative and more nuanced definitions of role, for example, notions that build on other mechanisms or those that are intended to accomodate and combine various actor and tie attributes as appropriate for the performed analysis.  
In this way, netroles generalizes prior tools and expands the scope of applicability of role equivalence concepts.

Netroles is a library implemented in and provided for Java, requiring Java 8 or newer. 

## Setup

To analyze networks in Java using the netroles library, the following artifacts should be added to your Java project (available on the Releases page):
* netroles-api-*.*.jar
* netroles-engine-*.*.jar (only needed at runtime)
* netroles-networks-api-*.*.jar
* netroles-networks-engine-*.*.jar (only needed at runtime)

In addition, netroles uses [SLF4J](https://www.slf4j.org/) for logging purposes, so you have to add SLF4J and an SLF4J binding to some logging library to your build path.

Netroles comes with its own implementation of network structures. Optionally, you can add the following artifacts to read networks into netroles' default network representation or write the network representation to files:
* netroles-io-api-*.*.jar
* netroles-io-engine-*.*.jar (only needed at runtime)

Alternatively, you can also use another network representation. By wrapping them in an adapter implementing the NetworkView interface in package ch.ethz.sn.visone3.roles.position, you can also supply such other networks to netroles without any further need for conversion.

## Basic concepts

The netroles library builds on the ideas and concepts in [Müller and Brandes (2022). The evolution of roles. Social Networks 68:195-208.](https://doi.org/10.1016/j.socnet.2021.02.001).

This section gives a very brief introduction of these cocnepts. For a more detailed treatise, please consider the linked paper. You can also skip this section if you prefer to see its concrete usage.

The design of the library builds on the following assumption: Two vertices are equivalent relative to some prior role structure if and only if all incident edges/neighbors of one vertex can be substituted or matched by incident edges/neighbors of the other, and vice versa.

Formally speaking, let $G=(V, E)$ denote a graph and let $\Sigma_{ij}(\sim) \subseteq \{ \sigma: V \to V \}$ denote admissible substitutions for the comparison of vertex $i$ by $j$ relative to a given equivalence $\sim$. A specific substitution $\sigma \in \Sigma_{ij}(\sim)$ means that given the prior equivalence $\sim$, a neighbor $k \in N(i)$ of vertex $i$ can be substituted by $\sigma(k)$, if $\sigma(k)$ is a neighbor of $j$.
In other words, the set of admissible substitutions establishes how the neighbors of one vertex can be substituted or matched by neighbors of the other.

Building on the idea of substitutions, we can formally define what it means for two vertices to be equivalent relative to some given equialence:  
Vertices $i$ and $j$ are equivalent relative to $\sim$ if and only if there are substitutions $\sigma_{ij} \in \Sigma_{ij}(\sim)$ and $\sigma_{ji} \in \Sigma_{ji}(\sim)$ such that we have $\sigma_{ij}(k) \in N(j)$ for each $k \in N(i)$ and $\sigma_{ji}(\ell) \in N(i)$ for each $\ell \in N(j)$.

We can then use the resulting equivalence to find the role equivalence relative to it, and keep repeating this to obtain a process of role evolution. This process moves within the lattice of equivalences in potentially any direction.  
Alternatively, we can also define and iterate two related operations, role restriction and extension, which determine respectively the greatest common refinement or the least common coarsening of relative role equivalence and its input equivalence, ensuring the process moves only in a single direction in the underlying lattice.

In any case, such processes of role evolution must enter a cycle at some point. Special among these are equivalences which are fixed points, which we refer to as stable role equivalences. These stable role equivalences correspond to classic notions of role equivalence.  
Of particular importance among the stable role equivalences are the interior, the greatest stable role equivalence under the operation of role restriction refining the given equivalence, and the closure, the least stable role equivalence under the operation of role extension coarsening the given equivalence.

As a concrete example, consider regular equivalence: An equivalence is regular if and only if two equivalent vertices must have the same equivalence classes in their neighborhood (but potentially in different numbers).
We can recover regular equivalence in the described framework as follows: We choose those substitutions as admissible where neighbors in the same class are substituted, i.e., $\Sigma_{ij}(\sim) = \{ \sigma \in (V \to V): \sigma(k) \sim k~\text{for all}~k \in V\}$.  
Then, the stable role equivalences under relative role equivalence correspond to the concept of perfect colorings in [Borgatti and Everett (1994)](https://doi.org/10.1016/0378-8733(94)90010-8), those under role restriction correspond to regular equivalence, and those under role extension have been called ecological equivalences (Borgatti and Everett, [1992](https://doi.org/10.1016/0378-8733(92)90006-S), [1994](https://doi.org/10.1016/0378-8733(94)90010-8)).  
Moreover, the interior then corresponds to the previously defined notion of regular interior [(Boyd and Everett, 1999)](https://doi.org/10.1016/S0378-8733(99)00006-4).

However, this conception is not restricted to regular equivalence. By defining substitutions suitably, we can devise other notions of role. In Furthermore, substitutions could also take vertex and edge attributes into account, allowing to incorporate more aspects of network data into role analysis.

### Implementation of concepts in this library

In practice, it is infeasible to design an efficient algorithm that can handle any potential choice of admissible substitutions.

Therefore, netroles only supports a large subclass of substitutions which adhere to the following assumption:
The substitutability of one neighbor/incident tie by another does not depend on how all other neighbors/ties are substituted, other than how often each neighbor can act as a substitute in the comparison.

Netroles is able to represent and compute all role notions that conform to this kind of independence assumption. Other role notions, however, are currently not implemented. In particular, automorphic equivalence violates this independence assumption and is therefore not provided.

## Example usage

By reference to some examples, this section describes library features and their usage. They mostly reproduce the role analysis examples in [Müller and Brandes (2022)](https://doi.org/10.1016/j.socnet.2021.02.001). The complete example code can also be found [here](examples/Examples.java).

### Classic role notions

Computing classic role notions using netroles is straightforward.

We illustrate its usage on the network [muellerbrandes_examplegraph](examples/muellerbrandes_examplegraph.graphml).

We first load the network data and an initial partition:
```java
import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.roles.position.NetworkView;

import java.io.File;

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
```

This loads the network, wraps it in the NetworkView adapter used by netroles (for comparison of neighborhoods in outgoing direction only, which is not relevant here because the underlying network is undirected).
It also extracts the initial equivalence we will use, represented as an integer array where vertices at indices i and j are equivalent if they are assigned the same value in the array:

```
{size=9,[0,1,2,2,2,0,1,1,0]}
```

Alternatively, we could also provide another equivalence like this:
```java
ConstMapping.OfInt otherEq = Mappings.wrapUnmodifiableInt(0, 0, 1, 1, 0, 0, 1, 1, 0);
```

Netroles typically expects integer arrays representing equivalences to be normalized, which means that in the array, the value at index 0 is 0 and the value at index i can be at most one more than any prior value in the array.

Next, we construct a role operator that represents the classic role notion of regular equivalence and related definitions like perfect and ecological colorings on the loaded network:

```java
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;

RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
    .of(network.asUndirectedGraph().countVertices(), networkView)
    .make();
```

Starting from this initial partition, we can now follow a role evolution process from the initial equivalence by repeatedly computing relative role equivalence:

```java
ConstMapping.OfInt prevEq;
ConstMapping.OfInt currEq = initialEq;
do {
  prevEq = currEq;
  currEq = regularOp.relative(currEq);
  System.out.println(currEq);
} while (!prevEq.equals(currEq));
```

which prints:
```
{size=9,[0,0,1,2,3,3,0,3,3]}
{size=9,[0,0,1,2,2,1,0,0,0]}
{size=9,[0,0,1,2,3,4,0,5,5]}
{size=9,[0,0,1,2,3,4,0,5,5]}
```

This is the process depicted in Figure 4 of Müller and Brandes (2022), and the last of these equivalences is a perfect, and thus also a regular, equivalence. 

Even more easily, we can compute the regular interior of the initial equivalence, as follows:
```java
ConstMapping.OfInt interior = regularOp.interior(initialEq);
System.out.println(interior);
```

Here, this prints the singleton equivalence:
```
{size=9,[0,1,2,3,4,5,6,7,8]}
```

Other kinds of role equivalences are supported similarly by replacing the call to regular() in the construction of the RoleOperator:
* equitable() represents equitable or exact equivalences [(Everett and Borgatti, 1996)](https://doi.org/10.1016/0378-8733(95)00286-3).
* weak() is used for weak role equivalence [(Winship and Mandel, 1983)](https://doi.org/10.2307/270911).
* strongStructural() results in the operator for (strong) structural equivalence [(Lorrain and White, 1971)](https://doi.org/10.1080/0022250X.1971.9989788).
* weakStructural() leads to the operator for weak structural equivalence [(Everett et al., 1990)](https://doi.org/10.1080/0022250X.1990.9990067).
* weaklyEquitable() is the equitable version of weak role equivalence, meaning each edge can match at most once. In the unweighted case, the weak equitable equivalence corresponds to degree equality.

### Introducing error tolerance

Strict application of classic role notions tends to result in trivial outcomes, often producing only the single-class or the singleton equivalence as the relevant stable role equivalences.

One strategy to deal with this is to use error-tolerant versions of the classic notions, adjusting those to allow for a bit of slack in the pairwise neighborhood comparisons.

Here, we use an error-tolerant variant of equitable equivalence and illustrate it on a [network of political actors in a US county](examples/doreianalbert_countypoliticans.graphml) originally published in [Doreian and Albert (1989)](https://www.ifip.com/Partitioning_Political_Actor.html).

Again, we first load the network and the initial equivalence:

```java
Network network;
int n;
NetworkView<?, ?> networkView;
ConstMapping.OfInt initialEq;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/doreianalbert_countypoliticians.graphml"))) {
  SourceFormat sourceData = source.parse();

  network = sourceData.incidence();
  n = network.asUndirectedGraph().countVertices();
  networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
  initialEq = (ConstMapping.OfInt) sourceData.monadic().get("initial");
  System.out.println(initialEq);
}
```

This prints:
```
{size=14,[0,1,0,0,0,0,0,1,1,1,2,2,1,0]}
```

If we now follow the role evolution process from this initial equivalence using the classic notion of regular equivalence, we end up in a trivial stable role equivalence:
```java
RoleOperator<ConstMapping.OfInt> regularOp = RoleOperators.EQUIVALENCE.regular()
    .of(n, networkView)
    .make();
ConstMapping.OfInt prevEq;
ConstMapping.OfInt currEq = initialEq;
do {
  prevEq = currEq;
  currEq = regularOp.relative(currEq);
  System.out.println(currEq);
} while (!prevEq.equals(currEq));
```

The process leads to an almost trivial fixed point:
```
{size=14,[0,1,2,3,2,3,2,1,4,4,5,6,6,2]}
{size=14,[0,1,2,3,4,0,2,1,1,1,5,6,7,8]}
{size=14,[0,1,2,3,4,5,6,7,8,8,9,10,11,12]}
{size=14,[0,1,2,3,4,5,6,7,8,8,9,10,11,12]}
```

Then we define a role operator which allows for up to one mis- or unmatched neighbor when substituting according to equitable equivalence in pairwise comparisons in both directions.
However, this would not yield an equivalence, but an asymmetric binary relation; to turn it into a similar equivalence again, we have to close transitively on the symmetrized binary relation.
Finally, we also split off isolates, as isolates can never be equivalent to non-isolates in terms of neighborhood substitutions, even if mismatches are allowed.

Such a role operator can be obtained as follows:
```
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.Reducers;

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
```

Starting again from the initial three-way density-based clustering, we follow the role evolution process:

```
currEq = initialEq;
do {
  prevEq = currEq;
  currEq = errortolerantOp.relative(currEq);
  System.out.println(currEq);
} while (!prevEq.equals(currEq));
```

Which prints the following interpretable equivalences in accordance with Figure 8 in Müller and Brandes (2022):
```
{size=14,[0,1,2,2,0,2,0,1,1,1,3,1,1,0]}
{size=14,[0,1,0,0,0,0,0,1,1,1,2,3,1,0]}
{size=14,[0,1,2,2,0,2,0,1,1,1,3,4,1,0]}
{size=14,[0,1,2,2,0,2,0,1,1,1,3,4,1,0]}
```

### Incorporating vertex and edge attributes

A strength of netroles is its ability to naturally incorporate vertex and edge attributes into role equivalences.

We illustrate this capability on a [marriage network among Florentine families](examples/muellerbrandes_examplegraph.graphml) originally collected by Padget ([Breiger and Pattison, 1986](https://doi.org/10.1016/0378-8733(86)90006-7); [Padgett and Ansell, 1994](https://doi.org/10.1086/230190)).

Again, we load the undirected network. This network also includes an additional variable on the vertices representing the wealth of the Florentine families.

```java
Network network;
int n;
NetworkView<Relationship, Relationship> networkView;
ConstMapping.OfInt wealth;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/doreianalbert_countypoliticians.graphml"))) {
  SourceFormat sourceData = source.parse();

  network = sourceData.incidence();
  n = network.asUndirectedGraph().countVertices();
  networkView = NetworkView.fromNetworkRelation(network, Direction.OUTGOING);
  wealth = (ConstMapping.OfInt) sourceData.monadic().get("wealth");
  System.out.println(wealth);
}
```

This prints portions of the wealth attribute associated with the families:
```
{size=16,[10448,35730,55351,44378,19691,32013,8127,41727,103140,48233,49313,2970(...   3 omitted)]}
```

Building on the notion of regular equivalence, we would like to incorporate family wealth into the comparison: A neighboring family should preferably be substituted by a wealthier family.
However, strict application of this comparison leads to trival outcomes, because there is only one wealthiest family only some are connected to, leading to rapid disintegration of any equivalence classes.
For this reason, we allow again one mismatch in pairwise neighborhood substitutions.

This is described by the error-tolerant role operator below, which augments the operator in the prior example by the comparison of wealth.
```java
import java.util.Comparator;

RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel( //
  Reducers.EQUIVALENCE.meet(), //
  Operators.composeRoleOp( //
    Operators.composeOp( //
      Operators.composeOp(
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
```

We determine the relative role equivalence on the following equivalence:
```java
ConstMapping.OfInt stableEquivalence =
  Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 0, 2, 0, 2, 0, 1, 3, 1, 0, 1, 0);
System.out.println(errortolerantOp.relative(stableEquivalence));
```
This yields:
```
{size=16,[0,0,0,1,1,0,2,0,2,0,1,3,1,0,1,0]}
```
and thus shows that it is a stable equivalence. It is coarsest such equivalence depicted in Figure 11 of Brandes and Müller (2022).

### Enumerating stable role equivalences

In the previous example, we guessed a stable role equivalence, but a more systematic approach would be more satisfactory.

The netroles library includes an algorithm to enumerate stable role equivalences under role restriction and extension (or more generally fixed points of isotone non-increasing and non-decreasing functions on lattices).
Since stable role equivalences under relative role equivalence are also stable under role restriction or extension, we can find those under relative role equivalence by filtering the output of the enumeration algorithm. 

The enumeration algorithm for stable role equivalences under role restriction searches stable role equivalences from coarser to finer equivalences.
To ensure speedy computation, we instruct the enumeration algorithm to skip those stable role equivalences under role restriction which do not coarsen the finest stable role equivalence under relative roles,
as any continued search from them cannot discover further stable role equivalences under relative roles.

The finest stable role equivalence under relative roles is the closure of the singletons equivalence. Thus, the following code defines the predicate to test whether the argument equivalence coarsens the least stable role equivalence or not:
```java
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
```

Then we can enumerate the stable role equivalences:
```java
import ch.ethz.sn.visone3.roles.lattice.StableRolesEnumeration;

for (ConstMapping.OfInt stableUnderRestriction : StableRolesEnumeration.EQUIVALENCE
    .stableRolesUnderRestriction(errortolerantOp,
	    Mappings.repeated(0, n)), skipIfNotCoarseningLowerBound) {
  if (stableUnderRestriction.equals(errortolerantOp.relative(stableUnderRestriction))) {
    System.out.println(stableUnderRestriction);
  }
}
```
This prints the following stable role equivalences, which are depicted in Figure 11 of Müller and Brandes (2022):
```
{size=16,[0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0]}
{size=16,[0,0,0,1,1,0,2,0,3,0,4,5,6,0,7,0]}
{size=16,[0,0,0,1,1,0,2,0,2,0,3,4,1,0,3,0]}
{size=16,[0,0,0,1,1,0,2,0,2,0,1,3,1,0,1,0]}
```

**Note:** In general, enumerating stable role equivalences is a very expensive operation and is only practically feasible on very small networks.  
While the implemented algorithm is more sophisticated than naive brute-force and gives much better runtime guarantees, this algorithm might still run with worst-case exponential time between produced outputs for stable role structures under role restriction.  
Furthermore, the number of stable role structures tends to grow fast with network size. This implies a large increase in total running time for full enumeration with network size.  
Hence, the algorithm is only practical for networks with only very few vertices. For example, the algorithm is usually only practical for enumerating regular equivalences up to about 40 vertices, while for more complex role notions, this limit is typically even lower.