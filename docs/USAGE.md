# Usage examples

By reference to some examples, this section describes library features and their usage. They mostly reproduce the role analysis examples in [Müller and Brandes (2022)](https://doi.org/10.1016/j.socnet.2021.02.001). The complete example code can also be found [here](./../examples/Examples.java).

## Classic role notions

Computing classic role notions using netroles is straightforward.

We illustrate its usage on the network [muellerbrandes_examplegraph](./../examples/muellerbrandes_examplegraph.graphml).

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

import java.io.IOException;
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


> &#9432; ***Note***
>
> Equivalences are represented as integer mappings, which are interpreted as follows: Two nodes are equivalent if the values at the node indices are the same.
> 
> Methods typically assume that this representation as normalized: At any index in the integer mappings, the value can be at most one more than at any prior index in the mapping. In particular, this implies that the first value is always zero.
> 
> Some methods in netroles will throw exceptions if the equivalence argument is not normalized. On the other hand, all methods in netroles returning equivalences always return normalized representations.
> 
> Objects implementing the `ConstMapping.OfInt` interface are used to represent such integer mappings. The `Mappings` class provides methods to construct such objects, while the objects themselves provide operations such as conversion to integer arrays.


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
    .of(networkView)
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

Other kinds of role equivalences are supported similarly by replacing the call to regular() in the construction of the role operator:
* equitable() represents equitable or exact equivalences [(Everett and Borgatti, 1996)](https://doi.org/10.1016/0378-8733(95)00286-3).
* weak() is used for weak role equivalence [(Winship and Mandel, 1983)](https://doi.org/10.2307/270911).
* strongStructural() yields the operator for (strong) structural equivalence [(Lorrain and White, 1971)](https://doi.org/10.1080/0022250X.1971.9989788).
* weakStructural() produces the operator for weak structural equivalence [(Everett et al., 1990)](https://doi.org/10.1080/0022250X.1990.9990067).
* weaklyEquitable() is the equitable version of weak role equivalence, meaning each tie can be a substitute for at most one other. In the unweighted case, the weak equitable equivalence corresponds to degree equality.

## Handling network directedness

The steps above yield role operators that compare neighborhoods with respect to only one direction in the network: Either outgoing direction only (thus comparing out-neighborhoods) or incoming direction only (thus comparing in-neighborhoods). While this restriction does not matter on undirected networks, it does mean that one direction would not be considered on directed networks.

A common approach to extend role equivalences to directed networks is to require that the role equivalence must conform to the role equivalence definition in both directions. We could do this by computing the role equivalence in outgoing direction, then the role equivalence in incoming direction, and finally the intersection of these two equivalences. This intersection is then in line with the role notion in both directions (relative to some given equivalence). It is straightforward to specify a bidirectional role notion in this way in the netroles library.

We illustrate this approach on a [network of negative sanction among novices of a monastery](./../examples/sampson_negativesanction.graphml) that was originally collected in the following doctoral thesis: Sampson (1968). A novitiate in a period of change: An experimental and case study of social relationships.

We first load the network:

```java
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
```

We can now define the role operators for regular equivalence. We first do this in single directions only:

```java
RoleOperator<ConstMapping.OfInt> outgoingRegularOp = RoleOperators.EQUIVALENCE.regular()
  .of(outgoingNetworkView).make();
RoleOperator<ConstMapping.OfInt> incomingRegularOp = RoleOperators.EQUIVALENCE.regular()
  .of(incomingNetworkView).make();
```

We then combine these two unidirectional operators to obtain a bidirectional regular operator:

```java
import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.Reducers;

RoleOperator<ConstMapping.OfInt> bidiRegularOp = Operators.parallel(
  // we apply the unidirectional operators in parallel
  // and combine them through intersection
  // (which is the meet of the equivalence lattice)
  Reducers.EQUIVALENCE.meet(), 
  outgoingRegularOp, // regular roles operator in outgoing direction only
  incomingRegularOp // regular roles operator in incoming direction only
);
```

The three role operators produce different role equivalences relative to the single-class equivalence:
```java
ConstMapping.OfInt initialEq = Mappings.repeated(0, n);
    
System.out.println(outgoingRegularOp.relative(initialEq));
System.out.println(incomingRegularOp.relative(initialEq));
System.out.println(bidiRegularOp.relative(initialEq));
```

This prints:
```
{size=18,[0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0]}
{size=18,[0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,1,1]}
{size=18,[0,0,0,1,1,1,2,2,2,1,1,2,1,2,1,1,3,3]}
```

Moreover, they differ in the maximum stable (or regular) role equivalence, which is the regular interior of the single-class equivalence:
```java
System.out.println(outgoingRegularOp.interior(initialEq));
System.out.println(incomingRegularOp.interior(initialEq));
System.out.println(bidiRegularOp.interior(initialEq));
```

```
{size=18,[0,0,0,1,2,3,4,3,5,6,7,8,9,8,10,11,0,0]}
{size=18,[0,0,0,1,2,3,0,0,0,4,5,0,6,0,7,8,9,9]}
{size=18,[0,0,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]}
```

> &#9432; ***Note***
>
> This example exemplifies a general dataflow-based design of the netroles library: Rather than providing a special method or operator to define bidirectional role operators specifically, the library provides general tools that enable the composition of unidirectional role operators to derive more complex operators. This means that the library generally does not immediately provide complex methods for computing role equivalences with lots of settings but no easy way of extension. Instead, simple operator blocks are offered and these blocks can be composed in generic ways to obtain the desired complex operator. Such generic methods for composing operators in various ways can be found in the `Operators` class. 
> 
> The composed operators can be thought of as circuits with a single input and output, a line connecting two inner operator blocks whenever the output of one operator is processed as an input by the other. When such a composed operator takes a role structure and produces a role structure, it can be considered a role operator in a loose sense: We can compute the relative role equivalence by applying this operator, compute the intersection or union of its inputs and outputs, or look for its fixed points as the counterparts to classical role equivalence definitions.
> 
> For directed networks, this means that the library usually only provides immediate facilities to construct unidirectional role operators, since it is straightforward to obtain a bidirectional one through composition.
> 
> However, there is one exception from this rule: When building the role operator for weak structural equivalence, both directions are to be provided immediately. This has a simple reason: Applying the rules of weak structural equivalence in a single direction only does not necessarily yield an equivalence (or even a ranking).

## Beyond equivalences: Role structures of rankings and binary relations

In some sense, equivalences are a restrictive role representation: Partitioning the nodes into several role classes, they cannot represent hierarchies between the classes and force hard transitions at class boundaries. Rather, rankings and binary relations are more appropriate representations for representing such properties. For example, if one class of nodes can match the ties of two other classes of nodes, then this class could be considered to be on a higher hierarchical level, since its members can play the role of the members of the other two classes. Or we might have that some group of nodes can mostly match the ties of two other groups, while the members of the two other groups are already too different to be considered to play the same roles.

We return to the example of the novices in a monastery from the previous section. Instead of equivalence, we now use rankings to investigate the hierarchical role structure.

To do this, we specify ranked versions of the unidirectional and bidirectional role operators from the previous section:


```java
import ch.ethz.sn.visone3.roles.structures.Ranking;

RoleOperator<Ranking> outgoingRankedOp = RoleOperators.RANKING.regular()
  .of(outgoingNetworkView).make();
RoleOperator<Ranking> incomingRankedOp = RoleOperators.RANKING.regular()
  .of(incomingNetworkView).make();
RoleOperator<Ranking> bidiRankedOp = Operators.parallel(
  // combine undirectional ranked operators through intersection
  Reducers.RANKING.meet(), 
  outgoingRankedOp, // ranked regular roles operator in outgoing direction only
  incomingRankedOp // ranked regular roles operator in incoming direction only
);
```

We can now compute the role ranking relative to the single-class ranking:

```java
import ch.ethz.sn.visone3.roles.structures.Rankings;

Ranking initialRanking = Rankings.universal(n);
    
System.out.println(bidiRankedOp.relative(initialRanking).toString().replace("],", "],\n"));
```

This prints:
```
{18, [[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,1,1],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,1,1]]}
```

Comparing this with the corresponding result for the role equivalence relative to the single-class equivalence, which we already obtained in the previous section:
```
{size=18,[0,0,0,1,1,1,2,2,2,1,1,2,1,2,1,1,3,3]}
```
We see that the ranking implies the following ordering of these role classes: 0 &lt; 3 &lt; 2 &lt; 1.

Similarly, we can also inspect the maximum stable role ranking as the interior of the single-class ranking:
```java
System.out.println(bidiRankedOp.interior(initialRanking).toString().replace("],", "],\n"));
```

This again prints the same ranking:
```
{18, [[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,0,0],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,1,1],
 [0,0,0,1,1,1,0,0,0,1,1,0,1,0,1,1,1,1]]}
```

This is quite different from the maximum stable role equivalence in the previous section. Why is there this difference?

This is because the novices at the highest level of the hierarchy are considered to be able to play the roles of anyone else in the ranked setting. If two novices both have at least one neighbor from this role class at the highest level of the hierarchy, then all their ties can be matched and they are both considered equivalent and placed at the highest level of the hierarchy. This exemplifies a major change when we move from equivalences to rankings: In regular equivalence, all roles are always considered incomparable, meaning that novices from one role class can never be matched with novices from another role. In a regular role ranking, by contrast, role classes can be ordered, and novies from one role classes can be matched with novices in a role class at a higher level.


> &#9432; ***Note***
>
> Rankings and binary relations are represented by the `Ranking` and `BinaryRelation` interfaces. Factories for rankings and binary relations as well as common operations on them are provided by the `Rankings` and `BinaryRelations` classes. Moreover, the `RelationBuilders` class provides builder objects to construct rankings and binary relations. 


> &#9432; ***Note***
>
> The library usually offers all operators for equivalences, rankings and binary relations. The `RoleOperators` class implements the respective role notions and basic operations on role structures for all three kinds of role structures. The `Converters` class provides operators to convert between the different kinds of role structures.

## Error-tolerant role notions

Strict application of classic role notions tends to result in trivial outcomes, often producing only the single-class or the singleton equivalence as the relevant stable role equivalences.

One strategy to deal with this is to use error-tolerant versions of the classic notions, adjusting those to allow for a bit of slack in the pairwise neighborhood comparisons.

Here, we use an error-tolerant variant of equitable equivalence and illustrate it on a [network of political actors in a US county](./../examples/doreianalbert_countypoliticans.graphml) originally published in [Doreian and Albert (1989)](https://www.ifip.com/Partitioning_Political_Actor.html).

Again, we first load the network and the initial equivalence:

```java
Network network;
NetworkView<?, ?> networkView;
ConstMapping.OfInt initialEq;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/doreianalbert_countypoliticians.graphml"))) {
  SourceFormat sourceData = source.parse();

  network = sourceData.incidence();
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
    .of(networkView)
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

Instead a role operator which allows for up to one mismatched neighbor when substituting according to equitable equivalence in pairwise comparisons in both directions; i.e., one neighbor of a node is allowed to be matched with some non-equivalent or already matched neighbor of the other node. For this purpose, netroles contains functions to compute pairwise distances between nodes based on the selected role notion. In the simplest case, when no customized cost functions for substitutions/matchings are supplied, the distance from i to j is just the number of incident ties of i that could not be successfully matched with incident ties of j. Thus, thresholding by one produces the pairs of nodes (i, j) where i is has at most one incident tie not matched by j.

However, this does not yield an equivalence yet, as the set of pairs produced by thresholding is neither transitive nor symmetric. Rather, the pairs form some kind of asymmetric binary relations. To obtain an equivalence, we first symmetrize the relation, which ensures that there is at most one mismatched neighbor in both directions of comparison. Then, we can turn the relation into an equivalence by closing transitively on it, or equivalently by partitioning the binary relation into its connected components.

As a final step, we also split off isolates: Isolates can never be equivalent to non-isolates in terms of neighborhood substitutions, even if mismatches are allowed, since the isolate has no neighbor which a neighbor of a non-isolated node can even wrongly be matched with. Note here that the weak equivalence on an undirected an unweighted network is exactly the partition into isolates and non-isolates.

We can now construct an operator in accordance with the steps above as follows:
```
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;

RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel( //
  Reducers.EQUIVALENCE.meet(), //
  Operators.composeRoleOp( //
    Operators.composeOp( //
      Operators.composeOp( // threshold pairwise distances from equitable equivalence by one
        DistanceOperators.EQUIVALENCE.equitable().of(networkView).make(),
        Converters.thresholdDistances((i, j) -> 1)),
      // symmetrize (at most distance one in both directions)
      RoleOperators.BINARYRELATION.basic().symmetrize()),
    Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
  RoleOperators.EQUIVALENCE.weak().of(networkView).make()); // and split off isolates
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

Which now prints the following interpretable equivalences in accordance with Figure 8 in Müller and Brandes (2022):
```
{size=14,[0,1,2,2,0,2,0,1,1,1,3,1,1,0]}
{size=14,[0,1,0,0,0,0,0,1,1,1,2,3,1,0]}
{size=14,[0,1,2,2,0,2,0,1,1,1,3,4,1,0]}
{size=14,[0,1,2,2,0,2,0,1,1,1,3,4,1,0]}
```

## Incorporating node and tie attributes

Node and tie attributes can be naturally incorporated into role equivalences with this library. We illustrate this capability on a [marriage network among Florentine families](./../examples/padgett_marriages.graphml) originally collected by Padgett ([Breiger and Pattison, 1986](https://doi.org/10.1016/0378-8733(86)90006-7); [Padgett and Ansell, 1994](https://doi.org/10.1086/230190)).

Again, we load the undirected network. This network also includes an additional variable on the vertices representing the wealth of the Florentine families.

```java
Network network;
int n;
NetworkView<Relationship, Relationship> networkView;
ConstMapping.OfInt wealth;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/padgett_marriages.graphml"))) {
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

Building on the notion of regular equivalence, we would like to incorporate family wealth into the comparison: A neighboring family should preferably be substituted by a wealthier family. This substitution restriction be easily added by passing the corresponding comparator to the role operator builder:
```java
RoleOperator<ConstMapping.OfInt> wealthawareOp = RoleOperators.EQUIVALENCE.regular() //
  .of(networkView) // on this network
    // substituting neighbor should have larger wealth
  .comp(Comparator.comparingInt(rship -> wealth.getInt(rship.getRight()))) 
  .make();
```

We determine the coarsest stable role equivalence under this operator as follows:

```java
System.out.println(wealthawareOp.interior(Mappings.repeated(0, n)));
```

But unfortunately, it turns out to be trivial:
```
{size=16,[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]}
```


This is because there is only one wealthiest family in the dataset. Only some families are connected to it, thus the neighbors of this wealthest family get split into its own class. Further iterations then result in rapid disintegration of the equivalence classes.

We now allow for one mismatch in pairwise neighborhood comparisons again. Following the example of an error-tolerant role equivalence in the previous section, the error-tolerant role operator is specified by the code below:
```java
import java.util.Comparator;

RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel( //
  Reducers.EQUIVALENCE.meet(), //
  Operators.composeRoleOp( //
    Operators.composeOp( //
      Operators.composeOp(
        // distance from wealth-aware regular equivalence
        DistanceOperators.EQUIVALENCE.regular().of(networkView)
		  // substituting neighbor should have larger wealth
          .comp(Comparator.comparingInt(rship -> wealth.getInt(rship.getRight()))).make(),
		// thresholded by one
        Converters.thresholdDistances((i, j) -> 1)),
      // symmetrize (at most distance one in both directions)
      RoleOperators.BINARYRELATION.basic().symmetrize()),
    Converters.strongComponentsAsEquivalence()), // close on symmetric comparisons transitively
  RoleOperators.EQUIVALENCE.weak().of(networkView).make()); // and split off isolates
```

We determine the relative role equivalence on the following equivalence:
```java
ConstMapping.OfInt stableEquivalence =
  Mappings.wrapUnmodifiableInt(0, 0, 0, 1, 1, 0, 2, 0, 2, 0, 1, 3, 1, 0, 1, 0);
System.out.println(errortolerantOp.relative(stableEquivalence));
```
This yields
```
{size=16,[0,0,0,1,1,0,2,0,2,0,1,3,1,0,1,0]}
```
and thus shows that it is a stable equivalence. It is coarsest such equivalence depicted in Figure 11 of Brandes and Müller (2022).

## Enumerating stable role equivalences

In the previous example, we guessed a stable role equivalence, but a more systematic approach would be more satisfactory.

The netroles library includes an algorithm to enumerate stable role equivalences under role restriction and extension (or more generally fixed points of isotone non-increasing and non-decreasing functions on lattices).
Since stable role equivalences under relative role equivalence are also stable under role restriction or extension, we can find those under relative role equivalence by filtering the output of the enumeration algorithm. 

The enumeration algorithm for stable role equivalences under role restriction searches stable role equivalences from coarser to finer equivalences.
To ensure speedy computation, we instruct the enumeration algorithm to skip those stable role equivalences under role restriction which do not coarsen the finest stable role equivalence under relative roles,
as any continued search from them cannot discover further stable role equivalences under relative roles.

The finest stable role equivalence under relative roles is the closure of the singletons equivalence. Thus, the following code defines the predicate to test whether the argument equivalence coarsens the least stable role equivalence or not:
```java
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

> &#9432; ***Note***
>
> In general, enumerating stable role equivalences is a very expensive operation and is only practically feasible on very small networks.  
> While the implemented algorithm is more sophisticated than naive brute-force and gives much better runtime guarantees, this algorithm might still run with worst-case exponential time between any two stable role structures under role restriction it finds.  
> Furthermore, the number of stable role structures tends to grow fast with network size. This implies a large increase in total running time for full enumeration with network size. Similarly, the number of stable role structures tends to increase sharply when we move from role equivalences to role rankings or role structures represented by binary relations, just like there is a drastic difference between the number of equivalences on the one hand and the number of rankings or binary relations on the other hand.  
> Hence, the algorithm is only practical for networks with only very few vertices. For example, the algorithm is usually only practical for enumerating regular equivalences up to about 40 vertices, as long as there are not too many of them. For more complex role notions, the practical enumeration limit tends to be even lower.

## Multiple network relations

As part of a study, multiple network relations of various kinds among the study subjects might have been collected. An analysis task on this kind of data might involve role analysis for several or all of these network relations simultaneously.

The section on directed networks already described one possible approach and the corresponding code: Establish roles on each network relation (and direction if applicable) and then combine them by intersecting them or doing some other reduction operation.

However, this has the consequence that the determination of roles is largely independent for each network relation. Specifically, the matching of ties is independent between network relations: When establishing role equivalence between $i$ and $j$, a tie between $i$ and some neighbor $h$ could be matched with a tie between $j$ and some neighbor $k$ in one relation, but a parallel tie between $i$ and neighbor $h$ in another relation could be matched with a tie between $j$ and some other neighbor $\ell$. Or in other words: Parallel ties to one neighbor in different network relations can be matched by single ties to different neighbors and vice versa.

This has major implications for this approach: Parallel ties are strictly as valuable as the sum of their ties. Positive or negative interactions between parallel ties are neglected.

An alternative strategy is to enforce the same matching over all network relations in pairwise comparisons. This can be achieved by merging the selected network relations into a single network, but where the attributes of the composite ties accurately represent the origin in the individual network relations.

We return to the network data on medieval Florentine families originally collected by Padgett ([Breiger and Pattison, 1986](https://doi.org/10.1016/0378-8733(86)90006-7); [Padgett and Ansell, 1994](https://doi.org/10.1086/230190)). Besides [marriage ties](./../examples/padgett_marriages.graphml),
network data on the [business ties](./../examples/padgett_business.graphml) has also been made available.

We first load both the undirected marriage and business relations among the families:

```java
int n;
Network marriageNetwork;
ConstMapping.OfInt wealth;
ConstMapping<String> familyNamesMarriages;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/padgett_marriages.graphml"))) {
  SourceFormat sourceData = source.parse();

  marriageNetwork = sourceData.incidence();
  n = marriageNetwork.asUndirectedGraph().countVertices();
  wealth = (ConstMapping.OfInt) sourceData.monadic().get("wealth");
  familyNamesMarriages = Mappings.castExact(String.class, sourceData.monadic().get("id"));
}
Network businessNetwork;
ConstMapping<String> familyNamesBusiness;
try (Source<?> source = IoProvider.getService("graphml").newSource(
    new File("examples/padgett_business.graphml"))) {
  SourceFormat sourceData = source.parse();

  businessNetwork = sourceData.incidence();
  if (businessNetwork.asUndirectedGraph().countVertices() != n) {
    throw new IOException("mismatch in number of vertices in marriage and business network");
  }
  familyNamesBusiness = Mappings.castExact(String.class, sourceData.monadic().get("id"));
}
```

Next, we have to match the node indices based on the original family names to ensure that the merge does not mix different families into the same node. The following code creates an array `familiesBusinessToMarriage` which maps a node index in the business network to the corresponding node index in the marriage network.

```java
import java.util.Map;
import java.util.HashMap;

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
```

Now that we have matched the nodes in the two networks, we can merge the two networks. The `MultiplexNetworks` class provides methods for merging several network relations into one composite network. It expects a list of network relations as well as a function that takes the index of the network relation in the supplied list plus a node index in this network relation and returns the node index in the composite network.

The following code merges the two networks (in outgoing direction, which is of no significance here since both network relations are undirected). The lambda assigns the same node indices to families as in the marriage network, while it remaps the nodes in the business network accordingly.

```java
import ch.ethz.sn.visone3.lang.Pair;
import ch.ethz.sn.visone3.roles.util.MultiplexNetworks;

Pair<Network, Mapping.OfInt[]> result = MultiplexNetworks.multiplexDirected(
  Arrays.asList(new Pair<>(marriageNetwork, Direction.OUTGOING),
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
NetworkView<Relationship, Relationship> networkView = NetworkView
  .fromNetworkRelation(compositeNetwork,
    Direction.OUTGOING);
```

The result is the composite network plus an array of mappings that contains the tie indices of all ties merged into one composite tie had in the original network relations. We can use the latter to test if a tie was part of a specific original network relation: For example, if `tieIndexRemapping[0].get(tie)` is at least `0`, then a tie from the marriage network (network at position 0 in the list passed to `MultiplexNetworks.multiplexDirected()`) was merged into it, and if it is less than `0`, then no marriage tie was there.

We now have everything ready to define a role operator on the composite network. The role operator restricts matchings in two ways: Like in the previous example, a tie must again be matched with another one if the matching is in accordance with the wealth of the neighboring families.

Additionally, we take into account the different types of ties according to the following rules: A marriage tie can be matched by a marriage tie and a business tie can be matched by a business tie. But we also consider combinations: If a composite tie represents a marriage and business tie, then it should also be matched by a tie obtained from a marriage and a business relationship. Finally, we also allow a (single) business tie to be matched by a (single) marriage tie, because a marriage tie can be argued to be a stronger connection among families.

We specify the matching rules based on tie types by supplying an appropriate matching/substitution cost function: We assign a cost of one when a marriage or business tie cannot successfully be matched according to the rule above, and a cost of two if both cannot. Otherwise, we configure the role operator similar to the previous example: We supply the wealth restriction using a comparator, define the role operator in an error-tolerant way for up to two mismatches, and split isolates into different role classes. Following all these considerations, we can specify this role operator as follows:

```java
RoleOperator<ConstMapping.OfInt> compositeOp = Operators.parallel( //
  Reducers.EQUIVALENCE.meet(), //
  Operators.composeRoleOp( //
    Operators.composeOp( //
      Operators.composeOp(
        // distance from marriage- and business-ties-aware regular equivalence
        DistanceOperators.EQUIVALENCE.regular().of(networkView)
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
  RoleOperators.EQUIVALENCE.weak().of(networkView).make()); // and split off isolates
```

We determine the relative role equivalence on the following equivalence:
```java
ConstMapping.OfInt equivalence =
  Mappings.wrapUnmodifiableInt(0, 0, 1, 2, 2, 0, 2, 2, 0, 0, 2, 3, 0, 0, 4, 0);
System.out.println(composite.relative(equivalence));
```

This prints

```
{size=16,[0,0,1,2,2,0,2,2,0,0,2,3,0,0,4,0]}
```
Thus, this is a stable role equivalence. It is depicted in Figure 12 of Müller and Brandes (2002).

To find all the other stable role equivalences, we can follow the steps of the previous section again to apply the lattice search algorithm.