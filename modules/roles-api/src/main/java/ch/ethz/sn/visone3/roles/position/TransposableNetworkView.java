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

/**
 * Represents a perspective on a network and the positions of the nodes,
 * expressed by some kind of ties, in it. Allows to transpose the network or
 * adjust the node position based on the pair of nodes involved in the pairwise
 * comparison.
 * 
 * @param <T> type of (forward) ties from the nodes.
 * @param <U> type of backward ties pointing to the nodes.
 */
public interface TransposableNetworkView<T, U> {

  /**
   * Returns the number of nodes in the network view.
   * 
   * @return the number of nodes.
   */
  int countNodes();

  /**
   * Returns an iterable over the incident (forward) ties of node {@code node}
   * relevant for the comparison of node {@code lhsComparison} by
   * {@code rhsComparison}.
   * 
   * @param lhsComparison the node on the left side of the positional comparison.
   * @param rhsComparison the node on the right side of the positional comparison.
   * @param node          the node whose incident ties are requested for the
   *                      comparison.
   * @return the incident ties of node {@code node}.
   */
  Iterable<? extends T> ties(int lhsComparison, int rhsComparison, int node);

  /**
   * Determines the target of the forward tie {@code tie} incident to {@code node}
   * for the comparison of node {@code lhsComparison} by {@code rhsComparison}.
   * 
   * @param lhsComparison the node on the left side of the positional comparison.
   * @param rhsComparison the node on the right side of the positional comparison.
   * @param node          the source node of the incident tie.
   * @param tie           the incident tie.
   * @return the target of {@code tie}.
   */
  int tieTarget(int lhsComparison, int rhsComparison, int node, T tie);

  /**
   * Determines the index of the forward tie {@code tie} incident to {@code node}
   * for the comparison of node {@code lhsComparison} by {@code rhsComparison}.
   * 
   * @param lhsComparison the node on the left side of the positional comparison.
   * @param rhsComparison the node on the right side of the positional comparison.
   * @param node          the source node of the incident tie.
   * @param tie           the incident tie.
   * @return the index of {@code tie}.
   */
  int tieIndex(int lhsComparison, int rhsComparison, int node, T tie);

  /**
   * Counts the number of forward ties incident to {@code node} for the comparison
   * of node {@code lhsComparison} by {@code rhsComparison}.
   * 
   * @param lhsComparison the node on the left side of the positional comparison.
   * @param rhsComparison the node on the right side of the positional comparison.
   * @param node          the node for which the number of incident ties is
   *                      requested.
   * @return the number of incident ties of node {@code node} as relevant for the
   *         comparison of {@code lhsComparison} and {@code rhsComparison}.
   */
  int countTies(int lhsComparison, int rhsComparison, int node);

}
