---
title: 'netroles: A Java library for role equivalence analysis in networks'
tags:
  - Java
  - network analysis
  - role analysis
authors:
  - name: Julian Müller
    orcid: 0000-0001-6440-8046
    affiliation: "1, 2"
affiliations:
 - name: Social Networks Lab, ETH Zürich, Switzerland
   index: 1
 - name: Institute of Computing, Università della Svizzera italiana, Switzerland
   index: 2
date: 29 August 2023
bibliography: paper.bib
---

# Summary

An important problem in the analysis of networks is structural similarity. It has commonly been expressed in terms of role equivalences, which have often been considered to formalize the concepts of social role and position as discussed by @l-sm-36, @m-stss-57 and @n-tss-57 in their analyses of social structure [@be-npsna-92]. Role equivalences are based on the idea that actors are equivalent or play the same role if they form ties to similar others in similar ways. For example, the role of "doctor" is characterized by a set of ties to others playing related roles like "nurses", "patients" and "colleagues". However, this equivalence idea has been interpreted by different authors in different ways, resulting in the proposition of diverse definitions of role equivalence such as structural [@lw-seisn-71] or regular equivalence [@wr-gshnr-83]. The `netroles` library provides implementations of many established notions of role equivalence, but more importantly offers a unified approach to role equivalence analysis that generalizes beyond the classic role equivalences, allowing users to express more complex kinds of role notions suitable for networks with multiple relations and attributes.

# Statement of need

The general-purpose network analysis software `UCINET` [@bef-uw-02], `visone` [@bbbcgklw-vsvsna-02; @bw-vavsn-04], and the R package `sna` [@b-snas-08] provide some methods to compute structural, regular or regular equitable [@eb-ecgd-96] equivalence as specific notions of role. More wide-spread are software tools that implement two kinds of relaxations of role equivalence:

* Distance or similarity measures based on structural, regular or equitable equivalence are offered by software such as `blockmodeling` [@zc-pb-23], `Pajek` [@bm-pavln-04], `sna` [@b-snas-08] or `UCINET` [@bef-uw-02].
* *Blockmodeling* aims to find equivalences that globally approximate a notion of role equivalence best on a given network. Software like `graph-tool` [@p-gpl-14], `sbm` [@cdb-s-23], `blockmodels` [@lbc-b-21], `StochBlock` [@sz-sbln-22; @zt-sboln-23] or `dynsbm` [@mm-d-20] offer *stochastic blockmodeling* methods derived from stochastic versions of structural equivalence. Packages such as `blockmodeling` [@z-gbvn-07; @zc-pb-23], `dBlockmodeling` [@bds-dbstmn-21; @b-d-23], `Pajek` [@bm-pavln-04] or `signnet` [@s-s-23] implement *optimizational blockmodeling* minimizing some criterion function based on one or more role equivalence concepts.

Nonetheless, only few tools offer partial support to compute a small selection of non-relaxed role equivalences, yet these solutions do not generalize to other kinds of role equivalences and are restricted to specific kinds of networks, such as unweighted networks or networks with categorical tie weights.

The `netroles` library is intended to close this gap and aims to extend the aim of applicability of role equivalences. It provides a unified framework for role equivalences based on the formalization of role equivalence proposed in @mb-er-22. The library implements established notions of role, but its main goal is to enable the expression of new role concepts, incorporating vertex and edge attributes as well as multiple relations according to the requirements of the network study. This also facilities the specification of error-tolerant role equivalences that allow for minor deviations from the ideal case. The example analyses in @mb-er-22 were conducted using `netroles`.

# Background

The conceptual idea of role equivalence can be restated as a matching problem: Two vertices $i$ and $j$ are role-equivalent if all neighbors or incident edges of $i$ can be matched with neighbors or incident edges of $j$ according to some set of matching rules and vice versa. These matching rules in turn frequently depend on some equivalence among the neighbors. Put differently, this defines an operation "relative role equivalence" that produces an equivalence by applying the matching rules relative to some given equivalence. @mb-er-22 showed that established role equivalences can be obtained as fixed points of relative role equivalence and the derived operations "role restriction" and "role extension", if suitable matching rules are chosen. The table below arranges established role equivalences supported by `netroles` by the underlying matching rules and the operations they are fixed points of.


| matching type | matching rules for comparing $i$ and $j$    | relative role equiv. | role restriction | role extension |
| ------------ | ------------------------ | ------------------------- | ------------------- | ------------------- |
| weak        | no restrictions      | weak equiv. [@wm-rp-83] | refinements of weak equiv. | coarsenings of weak equiv. |
| weakly equitable  | each edge matches at most once | degree equality | refinements of degree equal. | coarsenings of degree equal. |
| strong structural | same neighbors       | structural equiv.  [@lw-seisn-71]| refinements of struct. equiv. | coarsenings of struct. equiv. |
| weak structural   | tie $(i, j)$ matched with tie $(j, i)$ and $(i,i)$ with $(j, j)$, else same neighbors | weak structural equiv. [@ebb-eclr-90] | refinements of weak struct. equiv. | coarsenings of weak struct. equiv. |
| regular           | equivalent neighbors | perfect equiv. [@be-epc-94] | regular equiv.  [@wr-gshnr-83] | ecological equiv. [@be-gcpeen-92] | 
| equitable         | equivalent neighbors, each edge matches at most once | equitable perfect equiv.  [@eb-ecgd-96] | equitable regular equiv.  [@eb-ecgd-96] | equitable ecological equiv.  [@eb-ecgd-96] |
Table: Established role equivalences as fixed points of the operations relative role equivalence, role restriction and role extension by type of matching rules.

# Design

The `netroles` library enables users to specify the relative role equivalence operation as appropriate. Inspired by dataflow and functional programming paradigms, users can define the relative role equivalence operation through composition of simple operators in a declarative style. All other role operations described in @mb-er-22 are derived automatically. The focus on composition greatly increases the library's expressive power and improves its extensibility, as complex scenarios can be handled by breaking down the role definition into simpler suboperations.

To exemplify this compositional design, we define an error-tolerant version of equitable equivalence, which considers two vertices role-equivalent if there is at most one deviation from the underlying matching rules for outgoing edges in each direction of comparison, as long as the compared vertices are not isolates. The resulting binary relation is transitively closed to regain an equivalence. The example in Section 5 of @mb-er-22 is based on this error-tolerant equivalence.

The dataflow diagram below describes this role operator graphically. Note that some intermediate results are not equivalences.

![Dataflow diagram of error-tolerant role operator](./errortolerant-role-dataflow.pdf)

The following code expresses this role operator using `netroles`:

```java
RoleOperator<ConstMapping.OfInt> errortolerantOp = Operators.parallel(
  Reducers.EQUIVALENCE.meet(), // combine by intersecting equivalences
  Operators.composeRoleOp(
    Operators.composeOp(
      Operators.composeOp(
	    // threshold pairwise deviations from equitable equivalence by one
        DistanceOperators.EQUIVALENCE.equitable()
		  .of(NetworkView.fromNetworkRelation(network, Direction.OUTGOING))
		  .make(),
        Converters.thresholdDistances((i, j) -> 1)),
      // symmetrize (at most one deviation in each directions)
      RoleOperators.BINARYRELATION.basic().symmetrize()),
	// close over pairs of role-equivalent vertices transitively
    Converters.strongComponentsAsEquivalence()), 
  // and separate isolates
  RoleOperators.EQUIVALENCE.weak()
    .of(n, NetworkView.fromNetworkRelation(network, Direction.OUTGOING))
	.make()); 
```

# References