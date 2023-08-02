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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import java.util.Comparator;
import java.util.function.BiPredicate;

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationOrRanking;
import ch.ethz.sn.visone3.roles.impl.structures.RelationBuilderServiceImpl;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * Provides algorithms to compute relative roles, role closure and role interior for exact ranked
 * roles.
 *
 */
public class EquitableRankedRoles {

  private EquitableRankedRoles() {
  }

  /**
   * Computes the maximum ranking that is exact-relational-roles-consistent relative to the given
   * network. Runs in O(n m log n) time and O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @return the maximum ranking that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static Ranking rankedExactRoles(int n, TransposableNetworkView<?, ?> positionView,
      Ranking rankingRelativeTo) {
    return refiningRankedExactRoles(n, positionView, rankingRelativeTo, Rankings.universal(n));
  }

  /**
   * Computes the maximum relation that is exact-relational-roles-consistent relative to the given
   * network. Runs in O(n m log n) time and O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @return the maximum relation that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static BinaryRelation rankedExactRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation relationRelativeTo) {
    return refiningRankedExactRoles(n, positionView, relationRelativeTo,
        BinaryRelations.universal(n));
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is
   * exact-relational-roles-consistent relative to the given unweighted network. Runs in O(m n) time
   * and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param refinedRanking
   *          the ranking that is refined by the result
   * @return the maximum ranking that refines refinedRanking and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static Ranking refiningRankedExactRoles(int n,
      TransposableNetworkView<?, ?> positionView, Ranking rankingRelativeTo,
      Ranking refinedRanking) {
    return rankedExactRolesImpl(n, positionView, rankingRelativeTo, refinedRanking);
  }

  /**
   * Computes the maximum relation that refines a given relation and is
   * exact-relational-roles-consistent relative to the given unweighted network. Runs in O(m*m) time
   * and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param refinedRelation
   *          the relation that is refined by the result
   * @return the maximum relation that refines refinedRelation and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static BinaryRelation refiningRankedExactRoles(int n,
      TransposableNetworkView<?, ?> positionView, BinaryRelation relationRelativeTo,
      BinaryRelation refinedRelation) {
    return rankedExactRolesImpl(n, positionView, relationRelativeTo, refinedRelation);
  }

  private static <T> BinaryRelationOrRanking rankedExactRolesImpl(int n,
      TransposableNetworkView<T, ?> positionView, RelationBase structureRelativeTo,
      RelationBase refinedRelation) {

    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    // TODO: make lazy

    // Strategy: To test whether one node is dominated by another, a
    // bipartite graph consisting of the nodes in the neighborhoods of both
    // current nodes and including edges whenever a node from the first
    // neighborhood can be substituted for by a node from the latter. If a
    // maximum bipartite matching between these neighborhoods with the size
    // of the first neighborhood exists in this graph, then the first node
    // is dominated by the second.
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (i == j) {
          continue;
        }
        int neighboricount = positionView.countTies(i, j, i);
        int neighborjcount = positionView.countTies(i, j, j);
        if (neighboricount <= neighborjcount && (i != j)) {
          NetworkBuilder networkBuilder = NetworkProvider.getInstance()
              .builder(DyadType.UNDIRECTED);
          networkBuilder.ensureNode(neighboricount + neighborjcount - 1);
          int nicount = 0;
          for (T ri : positionView.ties(i, j, i)) {
            int ni = positionView.tieTarget(i, j, i, ri);
            int njcount = 0;
            for (T rj : positionView.ties(i, j, j)) {
              int nj = positionView.tieTarget(i, j, j, rj);
              if (structureRelativeTo.contains(ni, nj)) {
                networkBuilder.addEdge(nicount, njcount + neighboricount);
              }
              ++njcount;
            }
            ++nicount;
          }
          if (BipartiteMatching.maximumMatching(networkBuilder.build().asUndirectedGraph(),
              Mappings.intRange(0, neighboricount)).size() == neighboricount) {
            builder.add(i, j);
          }
        }
      }
    }

    return builder.build();
  }

  /**
   * Computes the maximum ranking that is exact-relational-roles-consistent relative to the given
   * network with weakly ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param comparator
   *          a comparator that weakly orders relationships
   * @return the maximum ranking that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static <V> Ranking rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Comparator<? super V> comparator) {
    return rankedExactRoles(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that is exact-relational-roles-consistent relative to the given
   * network with weakly ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param comparator
   *          a comparator that weakly orders relationships
   * @return the maximum relation that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static <V> BinaryRelation rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, Comparator<? super V> comparator) {

    return rankedExactRoles(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is
   * exact-relational-roles-consistent relative to the given network with weakly ordered edge
   * weights. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param refinedRanking
   *          the ranking that is refined by the result
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum ranking that refines refinedRanking and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static <V> Ranking refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking, Comparator<? super V> comparator) {

    return refiningRankedExactRoles(n, positionView, rankingRelativeTo, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that refines a given relation and is
   * exact-relational-roles-consistent relative to the given network with weakly ordered edge
   * weights. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param refinedRelation
   *          the relation that is refined by the result
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum relation that refines refinedRelation and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static <V> BinaryRelation refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      Comparator<? super V> comparator) {
    return refiningRankedExactRoles(n, positionView, relationRelativeTo, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that is exact-relational-roles-consistent relative to the given
   * network with partially ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum ranking that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static <V> Ranking rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, PartialComparator<? super V> comparator) {
    return rankedExactRoles(n, positionView, rankingRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that is exact-relational-roles-consistent relative to the given
   * network with partially ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum relation that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static <V> BinaryRelation rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, PartialComparator<? super V> comparator) {
    return rankedExactRoles(n, positionView, relationRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is
   * exact-relational-roles-consistent relative to the given network with partially ordered
   * relationships. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param refinedRanking
   *          the ranking that is refined by the result
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum ranking that refines refinedRanking and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static <V> Ranking refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking, PartialComparator<? super V> comparator) {
    return refiningRankedExactRoles(n, positionView, rankingRelativeTo, refinedRanking,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relation that refines a given relation and is
   * exact-relational-roles-consistent relative to the given network with partially ordered
   * relationships. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param refinedRelation
   *          the relation that is refined by the result
   * @param comparator
   *          a comparator that partially orders relationships
   * @return the maximum relation that refines refinedRelation and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static <V> BinaryRelation refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      PartialComparator<? super V> comparator) {
    return refiningRankedExactRoles(n, positionView, relationRelativeTo, refinedRelation,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum ranking that is exact-relational-roles-consistent relative to the given
   * network with partially ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param comparator
   *          a binary predicate that says whether the first relationship's value is compatible with
   *          the second one
   * @return the maximum ranking that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  private static <V> Ranking rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    return refiningRankedExactRoles(n, positionView, rankingRelativeTo, Rankings.universal(n),
        comparator);
  }

  /**
   * Computes the maximum relation that is exact-relational-roles-consistent relative to the given
   * network with partially ordered relationships. Runs in O(m^2 sqrt(n)) time and O(n^2) additional
   * space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param comparator
   *          a binary predicate that says whether the first relationship's value is compatible with
   *          the second one
   * @return the maximum relation that is exact-relational-roles-consistent relative to the given
   *         network.
   */
  public static <V> BinaryRelation rankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    return refiningRankedExactRoles(n, positionView, relationRelativeTo,
        BinaryRelations.universal(n), comparator);
  }

  /**
   * Computes the maximum ranking that refines a given ranking and is
   * exact-relational-roles-consistent relative to the given network with partially ordered
   * relationships. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * <p>
   * Note that this method is deliberately made private, since the result is not guaranteed to be a
   * ranking if the relation underlying the comparator argument is not transitive.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param rankingRelativeTo
   *          represents the ranking the output ranking should be exact-relational-roles-consistent
   *          relative to.
   * @param refinedRanking
   *          the ranking that is refined by the result
   * @param comparator
   *          a binary predicate that says whether the first relationship's value is compatible with
   *          the second one
   * @return the maximum ranking that refines refinedRanking and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  private static <V> Ranking refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Ranking rankingRelativeTo, Ranking refinedRanking,
      BiPredicate<? super V, ? super V> comparator) {
    return rankedExactRolesImpl(n, positionView, rankingRelativeTo, refinedRanking, comparator);
  }

  /**
   * Computes the maximum relation that refines a given relation and is
   * exact-relational-roles-consistent relative to the given network with partially ordered
   * relationships. Runs in O(m^2 sqrt(n)) time and needs O(n^2) additional space.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param relationRelativeTo
   *          represents the relation the output relation should be
   *          exact-relational-roles-consistent relative to.
   * @param refinedRelation
   *          the relation that is refined by the result
   * @param comparator
   *          a binary predicate that says whether the first relationship's value is compatible with
   *          the second one
   * @return the maximum relation that refines refinedRelation and is
   *         exact-relational-roles-consistent relative to the given network.
   */
  public static <V> BinaryRelation refiningRankedExactRoles(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      BinaryRelation relationRelativeTo, BinaryRelation refinedRelation,
      BiPredicate<? super V, ? super V> comparator) {
    return rankedExactRolesImpl(n, positionView, relationRelativeTo, refinedRelation, comparator);
  }

  private static <V, T extends V> BinaryRelationOrRanking rankedExactRolesImpl(int n,
      TransposableNetworkView<T, ? extends V> positionView,
      RelationBase structureRelativeTo, RelationBase refinedRelation,
      BiPredicate<? super V, ? super V> comparator) {

    // TODO: make lazy
    RelationBuilder<? extends BinaryRelationOrRanking> builder = RelationBuilderServiceImpl
        .denseReducibleRelationOrRankingBuilder(n);
    for (int i = 0; i < n; ++i) {
      builder.add(i, i);
    }

    // Strategy: To test whether one node is dominated by another, a
    // bipartite graph consisting of the nodes in the neighborhoods of both
    // current nodes and including edges whenever a node from the first
    // neighborhood can be substituted for by a node from the latter. If a
    // maximum bipartite matching between these neighborhoods with the size
    // of the first neighborhood exists in this graph, then the first node
    // is dominated by the second.
    for (int i = 0; i < n; ++i) {
      for (int j : refinedRelation.iterateInRelationFrom(i)) {
        if (i == j) {
          continue;
        }
        int neighboricount = positionView.countTies(i, j, i);
        int neighborjcount = positionView.countTies(i, j, j);
        if (neighboricount <= neighborjcount) {
          NetworkBuilder networkBuilder = NetworkProvider.getInstance()
              .builder(DyadType.UNDIRECTED);
          networkBuilder.ensureNode(neighboricount + neighborjcount - 1);
          int nicount = 0;
          boolean canDominate = true;
          for (T ri : positionView.ties(i, j, i)) {
            int ni = positionView.tieTarget(i, j, i, ri);
            int njcount = neighboricount;
            boolean anyMatching = false;
            for (T rj : positionView.ties(i, j, j)) {
              int nj = positionView.tieTarget(i, j, j, rj);

              if (structureRelativeTo.contains(ni, nj)) {
                if (comparator.test(ri, rj)) {
                  anyMatching = true;
                  networkBuilder.addEdge(nicount, njcount);
                }
              }
              ++njcount;
            }
            ++nicount;
            if (!anyMatching) {
              canDominate = false;
              break;
            }
          }
          if (canDominate
              && BipartiteMatching.maximumMatching(networkBuilder.build().asUndirectedGraph(),
                  Mappings.intRange(0, neighboricount)).size() == neighboricount) {
            builder.add(i, j);
          }
        }
      }
    }

    return builder.build();
  }
}
