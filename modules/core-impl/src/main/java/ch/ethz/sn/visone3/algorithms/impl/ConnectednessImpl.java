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

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.algorithms.Connectedness;
import ch.ethz.sn.visone3.algorithms.Traversal;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

/**
 * Implements methods establishing notions of connectedness.
 */
public final class ConnectednessImpl implements Connectedness {
  private static final Traversal TRAVERSAL = AlgoProvider.getInstance().traversals();

  ConnectednessImpl() {
  }

  @Override
  public List<PrimitiveList.OfInt> componentsToNodeLists(final ConstMapping.OfInt components) {
    Objects.requireNonNull(components);
    final ArrayList<PrimitiveList.OfInt> lists = new ArrayList<>();
    for (int i = 0; i < components.size(); i++) {
      final int c = components.getInt(i);
      while (lists.size() <= c) {
        lists.add(Mappings.newIntList());
      }
      lists.get(c).add(i);
    }
    return lists;
  }

  @Override
  public Mapping.OfInt components(final UndirectedGraph graph) {
    Objects.requireNonNull(graph);
    final Mapping.OfInt comp = Mappings.newIntList(-1, graph.countVertices());
    TRAVERSAL.dfs(graph.countVertices(), graph::getNeighbors, graph.getVertices(),
        new ConnectednessImpl.Label(comp));
    return comp;
  }

  @Override
  public Mapping.OfInt components(final UndirectedGraph graph, final IntPredicate filter) {
    Objects.requireNonNull(graph);
    final Mapping.OfInt comp = Mappings.newIntList(-1, graph.countVertices());
    TRAVERSAL.dfs(graph.countVertices(), vertex -> {
      if (!filter.test(vertex)) {
        return Iterators.emptyInt();
      }
      return Iterators.filter(graph.getNeighbors(vertex), filter);
    }, graph.getVertices(), new ConnectednessImpl.Label(comp));
    return comp;
  }

  @Override
  public Mapping.OfInt strongComponents(final DirectedGraph graph) {
    Objects.requireNonNull(graph);
    final int n = graph.countVertices();
    final Mapping.OfInt comp = Mappings.newIntList(-1, n);
    final Mapping.OfInt dfsns = Mappings.newIntList(-1, n);
    TRAVERSAL.dfs(graph::getOutNeighbors, graph.getVertices(), dfsns,
        new ConnectednessImpl.LabelStrongComponents(n, comp, dfsns));
    return comp;
  }

  @Override
  public Mapping.OfInt twoEdgeComponents(final UndirectedGraph graph) {
    Objects.requireNonNull(graph);
    final int n = graph.countVertices();
    final Mapping.OfInt comp = Mappings.newIntList(-1, n);
    final Mapping.OfInt dfsns = Mappings.newIntList(-1, n);
    TRAVERSAL.edgeDfs(graph.countEdges(), graph::getEdges, graph.getVertices(), dfsns,
        new ConnectednessImpl.LabelStrongComponents(n, comp, dfsns));
    return comp;
  }

  @Override
  public Mapping.OfInt biconnectedComponents(UndirectedGraph graph) {
    Objects.requireNonNull(graph);
    final int n = graph.countVertices();
    final int m = graph.countEdges();
    final Mapping.OfInt comp = Mappings.newIntList(-1, m);
    final Mapping.OfInt dfsns = Mappings.newIntList(-1, n);
    TRAVERSAL.edgeDfs(graph.countEdges(), graph::getEdges, graph.getVertices(), dfsns,
        new ConnectednessImpl.LabelBiconnectedComponents(n, m, comp, dfsns));
    return comp;
  }

  @Override
  public Mapping.OfInt weakComponents(final DirectedGraph graph) {
    Objects.requireNonNull(graph);
    return weakComponents(graph.countVertices(), graph::getNeighbors);
  }

  @Override
  public Mapping.OfInt weakComponents(final int numVertices,
      final IntFunction<PrimitiveIterable.OfInt> neighbors) {
    final Mapping.OfInt comp = Mappings.newIntList(-1, numVertices);
    TRAVERSAL.dfs(numVertices, neighbors, Mappings.intRange(0, numVertices),
        new ConnectednessImpl.Label(comp));
    return comp;
  }

  private static class Label implements TraversalImpl.Visitor {
    final Mapping.OfInt comp;
    int compn;

    Label(final Mapping.OfInt comp) {
      this.comp = comp;
    }

    @Override
    public void startSearch(final int node) {
    }

    @Override
    public void endSearch() {
      ++compn;
    }

    @Override
    public void visitEdge(final int source, final int target, int idx) {
    }

    @Override
    public void visitVertex(final int node) {
      comp.setInt(node, compn);
    }

    @Override
    public void backtrackVertex(int node) {
    }

  }

  private static class LabelStrongComponents implements Traversal.Visitor {
    // path-based strongly connected component algorithm
    // based on the implementation suggested in
    // the lectures notes of the lecture "Network Analysis"

    private ConstMapping.OfInt dfsns;
    private PrimitiveList.OfInt vertexStack = Mappings.newIntList();
    private PrimitiveList.OfInt componentsStack = Mappings.newIntList();
    private boolean[] onVertexStack;
    final Mapping.OfInt comp;
    int compn = 0;

    public LabelStrongComponents(int numVertices, Mapping.OfInt comp, ConstMapping.OfInt dfsns) {
      this.dfsns = dfsns;
      onVertexStack = new boolean[numVertices];
      this.comp = comp;
    }

    @Override
    public void startSearch(int node) {
    }

    @Override
    public void endSearch() {
    }

    @Override
    public void visitEdge(int source, int target, int idx) {
      if (dfsns.getInt(target) < 0) {
        // (future) tree or forward edge
        // nothing to do, it seems
      } else {
        int dfsTarget = dfsns.getInt(target);
        // it's either a cross, a forward or a back edge
        // we have to process cross or back edges
        if (dfsTarget <= dfsns.getInt(source) && source != target && onVertexStack[target]) {
          while (dfsns.getInt(componentsStack.getInt(componentsStack.size() - 1)) > dfsTarget) {
            componentsStack.removeIndex(componentsStack.size() - 1);
          }
        }
      }
    }

    @Override
    public void visitVertex(int node) {
      vertexStack.add(node);
      componentsStack.add(node);
      onVertexStack[node] = true;
    }

    @Override
    public void backtrackVertex(int node) {
      // when backtracking from the representative of the component
      // then assign each node in the component the component number
      if (componentsStack.getInt(componentsStack.size() - 1) == node) {
        componentsStack.removeIndex(componentsStack.size() - 1);
        int u;
        do {
          u = vertexStack.removeInt(vertexStack.size() - 1);
          comp.setInt(u, compn);
          onVertexStack[u] = false;
        } while (u != node);
        ++compn;
      }
    }

  }

  private static class LabelBiconnectedComponents implements Traversal.Visitor {
    private ConstMapping.OfInt dfsns;
    private PrimitiveList.OfInt edgeStack = Mappings.newIntList();
    private PrimitiveList.OfInt componentsEdgeStack = Mappings.newIntList();
    private PrimitiveList.OfInt componentsDfsStack = Mappings.newIntList();
    final Mapping.OfInt comp;
    int compn = 0;
    Mapping.OfInt incoming;

    public LabelBiconnectedComponents(int numVertices, int numEdges, Mapping.OfInt comp,
        ConstMapping.OfInt dfsns) {
      this.dfsns = dfsns;
      this.comp = comp;
      incoming = Mappings.newIntList(-1, numEdges);
    }

    @Override
    public void startSearch(int node) {
    }

    @Override
    public void endSearch() {
    }

    @Override
    public void visitEdge(int source, int target, int idx) {
      if (source == target) {
        // it's a loop
        comp.setInt(idx, compn);
        ++compn;
        return;
      }
      edgeStack.addInt(idx);
      if (dfsns.getInt(target) < 0) { // tree edge
        incoming.setInt(target, idx);
        componentsEdgeStack.addInt(idx);
        componentsDfsStack.addInt(dfsns.getInt(source));
      } else { // back edge
        int dfsTarget = dfsns.getInt(target);
        while (componentsDfsStack.getInt(componentsDfsStack.size() - 1) > dfsTarget) {
          componentsDfsStack.removeIndex(componentsDfsStack.size() - 1);
        }
        componentsEdgeStack.removeRange(componentsDfsStack.size(), componentsEdgeStack.size());
      }
    }

    @Override
    public void visitVertex(int node) {
    }

    @Override
    public void backtrackVertex(int node) {
      int idx = incoming.getInt(node);
      if (!componentsEdgeStack.isEmpty()
          && componentsEdgeStack.getInt(componentsEdgeStack.size() - 1) == idx) {
        componentsEdgeStack.removeIndex(componentsEdgeStack.size() - 1);
        componentsDfsStack.removeIndex(componentsDfsStack.size() - 1);
        int u;
        do {
          u = edgeStack.removeInt(edgeStack.size() - 1);
          comp.setInt(u, compn);
        } while (u != idx);
        ++compn;
      }
    }
  }
}
