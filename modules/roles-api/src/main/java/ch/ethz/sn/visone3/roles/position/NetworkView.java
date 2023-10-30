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

package ch.ethz.sn.visone3.roles.position;

import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;

/**
 * Represents a perspective on a network and the positions of the nodes,
 * expressed by some kind of ties, in it.
 * 
 * <p>
 * This interface can be implemented to allow this library to work directly on
 * network data structures offered by other libraries, without having to convert
 * the network data into the library's standard network data format defined by
 * {@link Network}.
 * 
 * @param <T> type of (forward) ties from the nodes.
 * @param <U> type of backward ties pointing to the nodes.
 */
public interface NetworkView<T, U> extends TransposableNetworkView<T, U> {

  /**
   * Returns an iterable over the incident (forward) ties of the specified node.
   * 
   * @param node the node whose incident ties are requested.
   * @return the incident ties of {@code node}.
   */
  Iterable<? extends T> ties(int node);

  @Override
  default Iterable<? extends T> ties(int lhsComparison, int rhsComparison, int node) {
    return ties(node);
  }

  /**
   * Returns an iterable over the incident (backward) ties of the specified node.
   * 
   * @param node the node whose incident backward ties are requested.
   * @return the incident backward ties of {@code node}.
   */
  Iterable<? extends U> inverseTies(int node);

  /**
   * Determines the target of a (forward) tie incident to the specified source
   * node.
   * 
   * @param node the source node of the incident tie.
   * @param tie  the incident tie.
   * @return the target of {@code tie}.
   */
  int tieTarget(int node, T tie);

  @Override
  default int tieTarget(int lhsComparison, int rhsComparison, int node, T tie) {
    return tieTarget(node, tie);
  }

  /**
   * Determines the target of a backward tie incident to the specified source
   * node.
   * 
   * @param node       the node whose incident ties are requested
   * @param inverseTie the incident tie
   * @return the target of {@code tie}.
   */
  int inverseTieTarget(int node, U inverseTie);

  /**
   * Determines the index of the forward tie {@code tie} incident to {@code node}
   * 
   * @param node the source node of the incident tie.
   * @param tie  the incident tie.
   * @return the index of {@code tie}.
   */
  int tieIndex(int node, T tie);

  @Override
  default int tieIndex(int lhsComparison, int rhsComparison, int node, T tie) {
    return tieIndex(node, tie);
  }

  /**
   * Determines the index of the backward tie {@code tie} incident to {@code node}
   * 
   * @param node       the source node of the incident tie.
   * @param inverseTie the incident tie.
   * @return the index of {@code tie}.
   */
  int inverseTieIndex(int node, U inverseTie);

  /**
   * Determines a unique (but not necessarily consecutive) index of the (forward)
   * tie {@code tie} incident to {@code node}
   * 
   * @param node the source node of the incident tie.
   * @param tie  the incident tie.
   * @return the unique index of {@code tie}.
   */
  int uniqueTieIndex(int node, T tie);

  /**
   * Determines a unique (but not necessarily consecutive) index of the backward
   * tie {@code tie} incident to {@code node}
   * 
   * @param node       the source node of the incident tie.
   * @param inverseTie the incident tie.
   * @return the unique index of {@code tie}.
   */
  int uniqueInverseTieIndex(int node, U inverseTie);

  /**
   * Returns a maximum (exclusive) on the unique tie indices of ties.
   * 
   * @return the maximum.
   */
  int maxUniqueTieIndex();

  /**
   * Determines the number of (forward) ties at {@code node}.
   * 
   * @param node the node.
   * @return the number of incident forward ties.
   */
  int countTies(int node);

  @Override
  default int countTies(int lhsComparison, int rhsComparison, int node) {
    return countTies(node);
  }

  /**
   * Determines the number of backward ties at {@code node}.
   * 
   * @param node the node.
   * @return the number of incident forward ties.
   */
  int countInverseTies(int node);

  /**
   * Determines the number of ties over all nodes.
   * 
   * @return number of ties.
   */
  int countAllTies();

  /**
   * Constructs a view on the network positions from a network's relational
   * perspective.
   * 
   * @param network   the network.
   * @param direction the direction used to view the position of nodes.
   * @return the view on the node positions for the given direction.
   */
  public static NetworkView<Relationship, Relationship> fromNetworkRelation(Network network, Direction direction) {
    final Relation rel = network.asRelation();
    boolean directed = network.isDirected();
    int uniqueTiesCount = directed ? rel.countRelationships() : 2 * network.asUndirectedGraph().countEdges();
    int numNodes = rel.countUnionDomain();
    switch (direction) {
    case INCOMING:
      return new NetworkView<Relationship, Relationship>() {

        @Override
        public int countNodes() {
          return numNodes;
        }

        @Override
        public Iterable<Relationship> ties(int node) {
          return rel.getRelationshipsTo(node);
        }

        @Override
        public Iterable<Relationship> inverseTies(int node) {
          return rel.getRelationshipsFrom(node);
        }

        @Override
        public int tieTarget(int node, Relationship tie) {
          return tie.getLeft();
        }

        @Override
        public int inverseTieTarget(int node, Relationship inverseTie) {
          return inverseTie.getRight();
        }

        @Override
        public int tieIndex(int node, Relationship tie) {
          return tie.getIndex();
        }

        @Override
        public int inverseTieIndex(int node, Relationship inverseTie) {
          return inverseTie.getIndex();
        }

        @Override
        public int uniqueTieIndex(int node, Relationship tie) {
          if (directed) {
            return tie.getIndex();
          } else {
            return 2 * tie.getIndex() + (tie.getLeft() > tie.getRight() ? 1 : 0);
          }
        }

        @Override
        public int uniqueInverseTieIndex(int node, Relationship inverseTie) {
          return uniqueTieIndex(node, inverseTie);
        }

        @Override
        public int countTies(int node) {
          return rel.countRelationshipsTo(node);
        }

        @Override
        public int countInverseTies(int node) {
          return rel.countRelationshipsFrom(node);
        }

        @Override
        public int countAllTies() {
          return rel.countRelationships();
        }

        @Override
        public int maxUniqueTieIndex() {
          return uniqueTiesCount;
        }

      };
    case OUTGOING:
      return new NetworkView<Relationship, Relationship>() {

        @Override
        public int countNodes() {
          return numNodes;
        }

        @Override
        public Iterable<Relationship> ties(int node) {
          return rel.getRelationshipsFrom(node);
        }

        @Override
        public Iterable<Relationship> inverseTies(int node) {
          return rel.getRelationshipsTo(node);
        }

        @Override
        public int tieTarget(int node, Relationship tie) {
          return tie.getRight();
        }

        @Override
        public int inverseTieTarget(int node, Relationship inverseTie) {
          return inverseTie.getLeft();
        }

        @Override
        public int tieIndex(int node, Relationship tie) {
          return tie.getIndex();
        }

        @Override
        public int inverseTieIndex(int node, Relationship inverseTie) {
          return inverseTie.getIndex();
        }

        @Override
        public int uniqueTieIndex(int node, Relationship tie) {
          if (directed) {
            return tie.getIndex();
          } else {
            return 2 * tie.getIndex() + (tie.getLeft() > tie.getRight() ? 1 : 0);
          }
        }

        @Override
        public int uniqueInverseTieIndex(int node, Relationship inverseTie) {
          return uniqueTieIndex(node, inverseTie);
        }

        @Override
        public int countTies(int node) {
          return rel.countRelationshipsFrom(node);
        }

        @Override
        public int countInverseTies(int node) {
          return rel.countRelationshipsTo(node);
        }

        @Override
        public int countAllTies() {
          return rel.countRelationships();
        }

        @Override
        public int maxUniqueTieIndex() {
          return uniqueTiesCount;
        }

      };
    default:
      throw new UnsupportedOperationException("Unsupported direction type");
    }
  }
}
