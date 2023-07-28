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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import java.util.Arrays;
import java.util.function.IntFunction;

public class NeighborhoodInclusion {

  private NeighborhoodInclusion() {

  }

  @FunctionalInterface
  public interface ToIntIntObjFunction<T> {
    int applyAsInt(int first, T second);
  }

  public interface EdgeDominationVisitor<T, U> {
    void startNode(int i);

    void finishNode(int i);

    boolean processEdge(T r);

    void matchEdge(int source, T edge, int middle, U matchedby);
  }

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

  public interface StructuralPreorderVisitor<T, U> {
    void startNode(int i);

    void finishNode(int i);

    void isolate(int i);

    boolean canMatch(int source, T edge, int middle, U matchedby);

    void addDomination(int i, int j);
  }

  public static <T, U> void structuralPreorder(int n,
      IntFunction<? extends Iterable<? extends T>> forwardEdges,
      IntFunction<? extends Iterable<? extends U>> backwardEdges, ToIntIntObjFunction<T> edgeTarget,
      ToIntIntObjFunction<U> edgeSource, StructuralPreorderVisitor<T, U> visitor) {

    iterateIncidentEdgeDominations(n, forwardEdges, backwardEdges, edgeTarget,
        new EdgeDominationVisitor<T, U>() {

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

  public interface VicinalPreorderVisitor<T, U> {
    void startNode(int i);

    void finishNode(int i);

    void isolate(int i);

    boolean canMatch(int source, T edge, int middle, U matchedby);

    boolean canMatchLoop(int firstSource, T firstLoop, int secondSource, T secondLoop);

    void addDomination(int i, int j);

    void visitLoop(int i, T loop, boolean onlyIncidentEdge);
  }

  public static <T, U> void vicinalPreorder(int n,
      IntFunction<? extends Iterable<? extends T>> forwardEdges,
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

    iterateIncidentEdgeDominations(n, forwardEdges, backwardEdges, edgeTarget,
        new EdgeDominationVisitor<T, U>() {

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
                  if (!hasLoop
                      || (loops[j] != null && visitor.canMatchLoop(i, loops[i], j, loops[j]))) {
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
