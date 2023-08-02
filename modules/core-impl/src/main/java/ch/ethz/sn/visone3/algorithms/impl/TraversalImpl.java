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

package ch.ethz.sn.visone3.algorithms.impl;

import ch.ethz.sn.visone3.algorithms.Traversal;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Edge;

import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Stack;
import java.util.function.IntFunction;

/**
 * An implementation of {@link Traversal}.
 */
public class TraversalImpl implements Traversal {

  TraversalImpl() {
  }

  /**
   * Multi source breadth-first-search.
   *
   * @param numVertices
   *          Number of nodes.
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new BFS is started from each of these nodes if the BFS number is still
   *          negative.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   * @return the BFS number mapping.
   */
  @Override
  public Mapping.OfInt bfs(final int numVertices,
      final IntFunction<PrimitiveIterable.OfInt> neighbors, final PrimitiveIterable.OfInt starts,
      final Visitor visitor) {
    Objects.requireNonNull(neighbors);
    Objects.requireNonNull(starts);
    Objects.requireNonNull(visitor);
    final Mapping.OfInt bfsns = Mappings.newIntList(-1, numVertices);
    for (final int i : starts) {
      if (bfsns.getInt(i) < 0) {
        bfs(neighbors, bfsns, i, visitor);
      }
    }
    return bfsns;
  }

  /**
   * Single source breadth-first-search.
   *
   * @param neighbors
   *          Neighbourhood function.
   * @param bfsns
   *          BFS number mapping, will be filled on visiting nodes.
   * @param start
   *          The starting node.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  private static void bfs(final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final Mapping.OfInt bfsns, final int start, final Visitor visitor) {
    visitor.startSearch(start);
    bfsns.setInt(start, 0);
    visitor.visitVertex(start);

    final PrimitiveList.OfInt queue = Mappings.newIntList(); // manually managed fifo
    queue.addInt(start);
    int queueHead = 0;
    while (queueHead != queue.size()) {
      final int source = queue.getInt(queueHead++);
      final int sourceBfsn1 = bfsns.getInt(source) + 1;

      final PrimitiveIterator.OfInt out = neighbors.apply(source).iterator();
      while (out.hasNext()) {
        final int target = out.nextInt();
        if (bfsns.getInt(target) < 0) {
          bfsns.setInt(target, sourceBfsn1);
          queue.addInt(target);
          visitor.visitEdge(source, target, -1);
          visitor.visitVertex(target);
        }
      }

      // keep queue store small
      if (2 * queueHead > queue.size()) {
        queue.removeRange(0, queueHead);
        queueHead = 0;
      }
    }
    visitor.endSearch();
  }

  /**
   * Multi source depth-first-search.
   *
   * @param numVertices
   *          Number of nodes.
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new DFS is started from each of these nodes if the DFS number is still
   *          negative.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   * @return the DFS number mapping.
   */
  @Override
  public Mapping.OfInt dfs(final int numVertices,
      final IntFunction<PrimitiveIterable.OfInt> neighbors, final PrimitiveIterable.OfInt starts,
      final Visitor visitor) {
    Objects.requireNonNull(neighbors);
    Objects.requireNonNull(starts);
    Objects.requireNonNull(visitor);
    final Mapping.OfInt dfsns = Mappings.newIntList(-1, numVertices);
    dfsInternal(neighbors, starts, dfsns, visitor);
    return dfsns;
  }

  /**
   * Multi source depth-first-search.
   *
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new DFS is started from each of these nodes if the DFS number is still
   *          negative.
   * @param dfsns
   *          Mapping as storage for DFS numbers.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  @Override
  public void dfs(final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final PrimitiveIterable.OfInt starts, final Mapping.OfInt dfsns, final Visitor visitor) {
    Objects.requireNonNull(neighbors);
    Objects.requireNonNull(starts);
    Objects.requireNonNull(visitor);
    Objects.requireNonNull(dfsns);

    dfsInternal(neighbors, starts, dfsns, visitor);
  }

  @Override
  public void edgeDfs(final int numEdges, IntFunction<Iterable<Edge>> edges,
      final PrimitiveIterable.OfInt starts, final Mapping.OfInt dfsns, final Visitor visitor) {
    Objects.requireNonNull(edges);
    Objects.requireNonNull(starts);
    Objects.requireNonNull(visitor);
    final boolean[] edgeMark = new boolean[numEdges];

    for (final int i : starts) {
      if (dfsns.getInt(i) < 0) {
        edgeDfsInternal(edges, dfsns, i, visitor, edgeMark);
      }
    }
  }

  /**
   * Multi source depth-first-search.
   *
   * @param neighbors
   *          Neighbourhood function.
   * @param starts
   *          Start points. A new DFS is started from each of these nodes if the DFS number is still
   *          negative.
   * @param dfsns
   *          Mapping as storage for DFS numbers.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  private static void dfsInternal(final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final PrimitiveIterable.OfInt starts, final Mapping.OfInt dfsns, final Visitor visitor) {

    for (final int i : starts) {
      if (dfsns.getInt(i) < 0) {
        dfsInternal(neighbors, dfsns, i, visitor);
      }
    }
  }

  /**
   * Single source depth-first-search.
   *
   * @param neighbors
   *          Neighbourhood function.
   * @param dfsns
   *          DFS number mapping, will be filled on visiting nodes.
   * @param start
   *          The starting node.
   * @param visitor
   *          A traversal visitor. Use {@link Visitor#NULL} instead of {@code null}.
   */
  private static Mapping.OfInt dfsInternal(final IntFunction<PrimitiveIterable.OfInt> neighbors,
      final Mapping.OfInt dfsns, final int start, final Visitor visitor) {
    visitor.startSearch(start);
    final PrimitiveList.OfInt vertexStack = Mappings.newIntList();
    final Stack<PrimitiveIterator.OfInt> neighborTraversalStack = new Stack<>();
    int dfsn = 0;
    vertexStack.addInt(start);
    neighborTraversalStack.push(neighbors.apply(start).iterator());

    while (!vertexStack.isEmpty()) {
      final int source = vertexStack.getInt(vertexStack.size() - 1);
      PrimitiveIterator.OfInt neighborIterator = neighborTraversalStack.peek();

      if (dfsns.getInt(source) < 0) {
        dfsns.setInt(source, dfsn++);
        visitor.visitVertex(source);
      }
      if (!neighborIterator.hasNext()) {
        visitor.backtrackVertex(source);

        vertexStack.removeIndex(vertexStack.size() - 1);
        neighborTraversalStack.pop();
      } else {
        int target = neighborIterator.nextInt();
        if (dfsns.getInt(target) < 0) {
          vertexStack.addInt(target);
          neighborTraversalStack.push(neighbors.apply(target).iterator());
        }
        visitor.visitEdge(source, target, -1);
      }
    }
    visitor.endSearch();
    return dfsns;
  }

  private static Mapping.OfInt edgeDfsInternal(final IntFunction<Iterable<Edge>> edges,
      final Mapping.OfInt dfsns, final int start, final Visitor visitor, boolean[] edgeMark) {
    visitor.startSearch(start);
    final PrimitiveList.OfInt vertexStack = Mappings.newIntList();
    final Stack<Iterator<Edge>> neighborTraversalStack = new Stack<>();

    int dfsn = 0;
    vertexStack.addInt(start);
    neighborTraversalStack.push(edges.apply(start).iterator());

    while (!vertexStack.isEmpty()) {
      final int source = vertexStack.getInt(vertexStack.size() - 1);
      Iterator<Edge> edgeIterator = neighborTraversalStack.peek();

      if (dfsns.getInt(source) < 0) {
        dfsns.setInt(source, dfsn++);
        visitor.visitVertex(source);
      }
      if (!edgeIterator.hasNext()) {
        visitor.backtrackVertex(source);

        vertexStack.removeIndex(vertexStack.size() - 1);
        neighborTraversalStack.pop();
      } else {
        Edge edge = edgeIterator.next();
        int idx = edge.getIndex();
        if (!edgeMark[idx]) {
          edgeMark[idx] = true;
          int target = edge.getTarget();
          if (dfsns.getInt(target) < 0) {
            vertexStack.addInt(target);
            neighborTraversalStack.push(edges.apply(target).iterator());
          }
          visitor.visitEdge(source, target, idx);
        }
      }
    }
    visitor.endSearch();
    return dfsns;
  }
}
