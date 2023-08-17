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

import java.util.Arrays;
import java.util.function.IntFunction;

/**
 * Implements algorithms for computing neighborhood inclusion preorders.
 */
public class NeighborhoodInclusion {

  private NeighborhoodInclusion() {

  }

  /**
   * Represents a function taking an integer and an object argument and returning
   * an integer.
   * 
   * @param <T> type of the second argument.
   */
  @FunctionalInterface
  public interface ToIntIntObjFunction<T> {
    /**
     * Evaluates the function for the given arguments.
     * 
     * @param first  the first argument.
     * @param second the second argument.
     * @return the result of the evaluation.
     */
    int applyAsInt(int first, T second);
  }

  /**
   * Visitor informing about edge dominations on a network.
   * 
   * @param <T> (forward) edge type.
   * @param <U> (backward) edge type.
   */
  public interface EdgeDominationVisitor<T, U> {
    /**
     * Invoked when processing of dominations for node {@code i} is started.
     * 
     * @param i the node.
     */
    void startNode(int i);

    /**
     * Invoked when processing of dominations for node {@code i} has finished.
     * 
     * @param i the node.
     */
    void finishNode(int i);

    /**
     * Invoked when processing of dominations for edge {@code e} is started.
     * 
     * @param e the edge.
     * @return true if the edge should be processed and matching edges for it be
     *         searched, otherwise false.
     */
    boolean processEdge(T e);

    /**
     * Invoked when an edge {@code edge} from {@code source} to {@code middle} is
     * matched by another edge {@code matchedby} pointing to {@code middle}.
     * 
     * @param source    the currently processed source node.
     * @param edge      the currently processed edge.
     * @param middle    the node opposite {@code source} on the currently processed
     *                  edge.
     * @param matchedby an edge pointing to {@code matchedby} which {@code edge} is
     *                  matched with.
     */
    void matchEdge(int source, T edge, int middle, U matchedby);
  }

  /**
   * This algorithm iterates all incident edge dominations.
   * 
   * @param <T>           (forward) edge type.
   * @param <U>           (backward) edge type.
   * @param n             number of nodes.
   * @param forwardEdges  function producing forward edges for a given node.
   * @param backwardEdges function producing backward edges for a given node.
   * @param edgeTarget    function computing the target of a forward edge.
   * @param visitor       visitor informed by the algorithm about found edge
   *                      dominations.
   */
  public static <T, U> void iterateIncidentEdgeDominations(int n,
      IntFunction<? extends Iterable<? extends T>> forwardEdges,
      IntFunction<? extends Iterable<? extends U>> backwardEdges, ToIntIntObjFunction<T> edgeTarget,
      EdgeDominationVisitor<T, U> visitor) {

    for (int i = 0; i < n; ++i) {

      visitor.startNode(i);

      for (T ri : forwardEdges.apply(i)) {

        if (visitor.processEdge(ri)) {
          int k = edgeTarget.applyAsInt(i, ri);
          for (U rj : backwardEdges.apply(k)) {
            visitor.matchEdge(i, ri, k, rj);
          }
        }
      }

      visitor.finishNode(i);
    }
  }

  /**
   * Visitor informed about all information necessary to compute the structural
   * preorder.
   * 
   * @param <T> (forward) edge type.
   * @param <U> (backward) edge type.
   */
  public interface StructuralPreorderVisitor<T, U> {
    /**
     * Invoked when processing node {@code i} is started.
     * 
     * @param i the node.
     */
    void startNode(int i);

    /**
     * Invoked when processing node {@code i} has finished.
     * 
     * @param i the node.
     */
    void finishNode(int i);

    /**
     * Invoked when node {@code i} is isolate and thus being dominated by any other
     * node.
     * 
     * @param i the node.
     */
    void isolate(int i);

    /**
     * Invoked to query whether the edge {@code edge} from {@code source} to
     * {@code middle} can be matched by the edge {@code matchedby} pointing to
     * {@code middle}.
     * 
     * @param source    the node currently being processed.
     * @param edge      a forward edge incident to {@code source}.
     * @param middle    the node opposite of {@code source} on edge {@code edge}.
     * @param matchedby a backward edge pointing to {@code middle}.
     * @return true if edge {@code edge} can be matched by edge {@code matchedby},
     *         otherwise false.
     */
    boolean canMatch(int source, T edge, int middle, U matchedby);

    /**
     * Invoked when non-isolate node {@code i} is dominated by node {@code j}, i.e.,
     * each edge incident to {@code i} could be matched by an edge incident to
     * {@code j} with the same node at the opposite end.
     * 
     * @param i dominated node.
     * @param j dominating node.
     */
    void addDomination(int i, int j);
  }

  /**
   * Computes the (necessary information to construct the) structural preorder.
   * 
   * @param <T>           (forward) edge type.
   * @param <U>           (backward) edge type.
   * @param n             number of nodes.
   * @param forwardEdges  function producing the forward edges for a given node.
   * @param backwardEdges function producing the backward edges for a given node.
   * @param edgeTarget    function producing the opposite node of a forward edge.
   * @param edgeSource    function producing the opposite node of a backward edge.
   * @param visitor       visitor informed about all the necessary information to
   *                      construct the structural preorder.
   */
  public static <T, U> void structuralPreorder(int n, IntFunction<? extends Iterable<? extends T>> forwardEdges,
      IntFunction<? extends Iterable<? extends U>> backwardEdges, ToIntIntObjFunction<T> edgeTarget,
      ToIntIntObjFunction<U> edgeSource, StructuralPreorderVisitor<T, U> visitor) {

    iterateIncidentEdgeDominations(n, forwardEdges, backwardEdges, edgeTarget, new EdgeDominationVisitor<T, U>() {

      private int[] mark;
      private int[] t = new int[n];
      private int[] encounteredVertices = new int[n];
      private int lastEncounteredVertex;
      private int deg = 0;
      private int currNode;

      {
        mark = new int[n];
        Arrays.fill(mark, -1);
      }

      @Override
      public void startNode(int i) {
        deg = 0;
        currNode = i;
        lastEncounteredVertex = 0;
        visitor.startNode(i);
      }

      @Override
      public void finishNode(int i) {
        if (deg == 0) {
          visitor.isolate(i);
        } else
          for (int indexj = 0; indexj < lastEncounteredVertex; ++indexj) {
            int j = encounteredVertices[indexj];
            if (t[j] == deg) {
              visitor.addDomination(i, j);
            }
          }
        visitor.finishNode(i);
      }

      @Override
      public boolean processEdge(T r) {
        ++deg;
        return true;
      }

      @Override
      public void matchEdge(int source, T edge, int middle, U matchedby) {
        if (visitor.canMatch(source, edge, middle, matchedby)) {
          int j = edgeSource.applyAsInt(middle, matchedby);
          if (mark[j] != currNode) {
            mark[j] = currNode;
            t[j] = 0;
            encounteredVertices[lastEncounteredVertex++] = j;
          }
          ++t[j];
        }
      }
    });
  }

  /**
   * Visitor informed about all information necessary to compute the vicinal
   * preorder.
   * 
   * @param <T> (forward) edge type.
   * @param <U> (backward) edge type.
   */
  public interface VicinalPreorderVisitor<T, U> {
    /**
     * Invoked when processing node {@code i} is started.
     * 
     * @param i the node.
     */
    void startNode(int i);

    /**
     * Invoked when processing node {@code i} has finished.
     * 
     * @param i the node.
     */
    void finishNode(int i);

    /**
     * Invoked when node {@code i} is isolate and thus being dominated by any other
     * node.
     * 
     * @param i the node.
     */
    void isolate(int i);

    /**
     * Invoked to query whether the edge {@code edge} from {@code source} to
     * {@code middle} can be matched by the edge {@code matchedby} pointing to
     * {@code middle}.
     * 
     * @param source    the node currently being processed.
     * @param edge      a forward edge incident to {@code source}.
     * @param middle    the node opposite of {@code source} on edge {@code edge}.
     * @param matchedby a backward edge pointing to {@code middle}.
     * @return true if edge {@code edge} can be matched by edge {@code matchedby},
     *         otherwise false.
     */
    boolean canMatch(int source, T edge, int middle, U matchedby);

    /**
     * Invoked to query whether the loop {@code firstLoop} at {@code firstSource}
     * can be matched by the loop {@code secondLoop} at {@code secondSource}.
     * 
     * @param firstSource  the node incident of the first loop.
     * @param firstLoop    the first loop.
     * @param secondSource the node incident of the second loop.
     * @param secondLoop   the second loop.
     * @return true if {@code firstLoop} can be matched by {@code secondLoop}.
     */
    boolean canMatchLoop(int firstSource, T firstLoop, int secondSource, T secondLoop);

    /**
     * Invoked when non-isolate node {@code i} is dominated by node {@code j}, i.e.,
     * each edge incident to {@code i} could be matched by an edge incident to
     * {@code j} with the same node at the opposite end.
     * 
     * @param i dominated node.
     * @param j dominating node.
     */
    void addDomination(int i, int j);

    /**
     * Invoked when the currently processing node {@code i} has a loop.
     * 
     * @param i                the node.
     * @param loop             the loop.
     * @param onlyIncidentEdge true if {@code loop} is the only incident edge of
     *                         {@code i}, otherwise false.
     */
    void visitLoop(int i, T loop, boolean onlyIncidentEdge);
  }

  /**
   * Computes the (necessary information to construct the) vicinal preorder.
   * 
   * @param <T>           (forward) edge type.
   * @param <U>           (backward) edge type.
   * @param n             number of nodes.
   * @param forwardEdges  function producing the forward edges for a given node.
   * @param backwardEdges function producing the backward edges for a given node.
   * @param edgeTarget    function producing the opposite node of a forward edge.
   * @param edgeSource    function producing the opposite node of a backward edge.
   * @param visitor       visitor informed about all the necessary information to
   *                      construct the structural preorder.
   */
  public static <T, U> void vicinalPreorder(int n, IntFunction<? extends Iterable<? extends T>> forwardEdges,
      IntFunction<? extends Iterable<? extends U>> backwardEdges, ToIntIntObjFunction<T> edgeTarget,
      ToIntIntObjFunction<U> edgeSource, VicinalPreorderVisitor<T, U> visitor) {

    @SuppressWarnings("unchecked")
    T[] loops = (T[]) new Object[n];

    for (int i = 0; i < n; ++i) {
      for (T ri : forwardEdges.apply(i)) {
        if (edgeTarget.applyAsInt(i, ri) == i) {
          loops[i] = ri;
        }
      }
    }

    iterateIncidentEdgeDominations(n, forwardEdges, backwardEdges, edgeTarget, new EdgeDominationVisitor<T, U>() {

      private int[] mark;
      private int[] t = new int[n];
      private int[] encounteredVertices = new int[n];
      private int lastEncounteredVertex;
      private int deg = 0;
      private int currNode;
      @SuppressWarnings("unchecked")
      private U[] inverseTies = (U[]) new Object[n];
      private int[] encounteredInverseVertices = new int[n];
      private int lastEncounteredInverseVertex;

      {
        mark = new int[n];
        Arrays.fill(mark, -1);
      }

      @Override
      public void startNode(int i) {
        deg = 0;
        currNode = i;
        lastEncounteredVertex = 0;
        for (U inverseTie : backwardEdges.apply(i)) {
          int source = edgeSource.applyAsInt(i, inverseTie);
          inverseTies[source] = inverseTie;
          encounteredInverseVertices[lastEncounteredInverseVertex++] = source;
        }
        visitor.startNode(i);
      }

      @Override
      public void finishNode(int i) {
        if (deg == 0) {
          visitor.isolate(i);
        }
        boolean hasLoop = loops[i] != null;
        if (hasLoop) {
          --deg;
          visitor.visitLoop(i, loops[i], deg == 0);
        }
        if (deg != 0) {
          for (int indexj = 0; indexj < lastEncounteredVertex; ++indexj) {
            int j = encounteredVertices[indexj];
            if (t[j] == deg) {
              if (!hasLoop || (loops[j] != null && visitor.canMatchLoop(i, loops[i], j, loops[j]))) {
                visitor.addDomination(i, j);
              }
            }
          }
        }
        for (int indexj = 0; indexj < lastEncounteredInverseVertex; ++indexj) {
          inverseTies[encounteredInverseVertices[indexj]] = null;
        }
        lastEncounteredInverseVertex = 0;
        visitor.finishNode(i);
      }

      @Override
      public boolean processEdge(T r) {
        ++deg;
        // skip loops (for now)
        // handle them at node finish
        final int target = edgeTarget.applyAsInt(currNode, r);
        if (target == currNode) {
          return false;
        }

        // handle reciprocation
        final U inverseTie = inverseTies[target];
        if (inverseTie != null && visitor.canMatch(currNode, r, target, inverseTie)) {
          if (mark[target] != currNode) {
            mark[target] = currNode;
            t[target] = 0;
            encounteredVertices[lastEncounteredVertex++] = target;
          }
          ++t[target];
        }

        return true;
      }

      @Override
      public void matchEdge(int source, T edge, int middle, U matchedby) {
        int j = edgeSource.applyAsInt(middle, matchedby);
        int k = middle;
        // skip loops
        // handled at node finish
        if (k == j) {
          return;
        }
        if (visitor.canMatch(source, edge, middle, matchedby)) {
          if (mark[j] != currNode) {
            mark[j] = currNode;
            t[j] = 0;
            encounteredVertices[lastEncounteredVertex++] = j;
          }
          ++t[j];
        }
      }
    });
  }
}
