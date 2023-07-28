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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntDoubleHeap;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

/**
 * Provides algorithms to compute maximum matchings on bipartite graphs.
 * 
 * Implementation derived from Chapter 7, pp. 144--149, in
 * 
 * K. Mehlhorn and St. Näher, The LEDA Platform of Combinatorial and Geometric Computing, Cambridge
 * University Press, 1999. Link: https://people.mpi-inf.mpg.de/~mehlhorn/LEDAbook.html
 * 
 */
public class WeightedBipartiteMatching {

  private WeightedBipartiteMatching() {
  }
  private static int[] initializePotential(UndirectedGraph graph, ConstMapping.OfInt weights,
      PrimitiveIterable.OfInt uvertices, Edge[] vertexmatches) {

    int[] potential = new int[graph.countVertices()];

    // Naive heuristic
    // boolean weightFirst = false;
    // int maxWeight = 0;
    // for (Edge e : graph.getEdges()) {
    // if (!weightFirst) {
    // weightFirst = true;
    // maxWeight = weights.getInt(e.getIndex());
    // } else {
    // maxWeight = Math.max(maxWeight, weights.getInt(e.getIndex()));
    // }
    // }
    // for (int u : uvertices) {
    // potential[u] = maxWeight;
    // }

    // Simple heuristic
    for (int u : uvertices) {
      Edge maxWeightEdge = null;
      int maxWeight = 0;
      for (Edge e : graph.getEdges(u)) {
        int weight = weights.getInt(e.getIndex());
        if (maxWeightEdge == null || weight > maxWeight) {
          maxWeightEdge = e;
          maxWeight = weight;
        }
      }
      potential[u] = maxWeight;
      if (maxWeightEdge != null && vertexmatches[maxWeightEdge.getTarget()] == null) {
        vertexmatches[u] = vertexmatches[maxWeightEdge.getTarget()] = maxWeightEdge;
      }
    }
    return potential;
  }

  private static void augmentPathTo(int dest, Edge[] pred, Edge[] vertexmatches, boolean matched) {
    Edge e = pred[dest];
    while (e != null) {
      if (matched) {
        e = pred[e.getTarget()];
      } else {
        vertexmatches[e.getSource()] = vertexmatches[e.getTarget()] = e;
        e = pred[e.getSource()];
      }
      matched = !matched;
    }
  }

  private static void relaxEdges(UndirectedGraph graph, int src, ConstMapping.OfInt weights,
      int[] potential, Edge[] vertexmatches, Edge[] pred, int[] dist, IntDoubleHeap heap,
      PrimitiveList.OfInt processedlist) {

    for (Edge e : graph.getEdges(src)) {
      if (vertexmatches[src] != null && vertexmatches[src].getIndex() == e.getIndex()) {
        continue;
      }
      int trgt = e.getTarget();
      int db = dist[src] + (potential[src] + potential[trgt] - weights.getInt(e.getIndex()));
      if (pred[trgt] == null) {
        processedlist.add(trgt);
      }
      if (pred[trgt] == null || db < dist[trgt]) {
        dist[trgt] = db;
        pred[trgt] = e;
        heap.upsert(trgt, db);
      }
    }
  }

  private static void augment(UndirectedGraph graph, int src, ConstMapping.OfInt weights,
      int[] potential, Edge[] vertexmatches, Edge[] pred, int[] dist, IntDoubleHeap heap,
      PrimitiveList.OfInt processedu, PrimitiveList.OfInt processedv) {
    int best_vert_uvertices = src;
    int min_pot_uvertices = potential[src];
    int delta;

    dist[src] = 0;

    processedu.add(src);

    relaxEdges(graph, src, weights, potential, vertexmatches, pred, dist, heap, processedv);

    while (true) {
      int trgt = -1;
      int db;
      if (heap.isEmpty()) {
        db = min_pot_uvertices;
      } else {
        trgt = heap.pop();
        db = dist[trgt];
      }
      if (db >= min_pot_uvertices) {
        delta = min_pot_uvertices;
        augmentPathTo(best_vert_uvertices, pred, vertexmatches, true);
        vertexmatches[best_vert_uvertices] = null;
        break;
      } else if (vertexmatches[trgt] == null) {
        delta = db;
        augmentPathTo(trgt, pred, vertexmatches, false);
        break;
      } else {
        Edge matchede = vertexmatches[trgt];
        int u2 = matchede.getSource();
        pred[u2] = matchede;
        processedu.add(u2);
        dist[u2] = db;
        if (db + potential[u2] < min_pot_uvertices) {
          best_vert_uvertices = u2;
          min_pot_uvertices = db + potential[u2];
        }

        relaxEdges(graph, u2, weights, potential, vertexmatches, pred, dist, heap, processedv);
      }
    }

    for (int i : processedu) {
      pred[i] = null;
      int pot_change = delta - dist[i];
      if (pot_change > 0) {
        potential[i] -= pot_change;
      }
    }
    for (int i : processedv) {
      pred[i] = null;
      int pot_change = delta - dist[i];
      if (pot_change > 0) {
        potential[i] += pot_change;
      }
    }

    processedu.clear();
    processedv.clear();
    heap.clear();
  }

  private static void maximumWeightMatchingImpl(UndirectedGraph graph, ConstMapping.OfInt weights,
      PrimitiveIterable.OfInt uvertices, Edge[] vertexmatches) {
    int[] potential = initializePotential(graph, weights, uvertices, vertexmatches);
    int n = graph.countVertices();
    int[] dist = new int[n];
    IntDoubleHeap heap = PrimitiveContainers.fixedUniverseIntDoubleMinHeap(n);
    Edge[] pred = new Edge[n];
    PrimitiveList.OfInt processedu = Mappings.newIntList(n), processedv = Mappings.newIntList(n);

    for (int i : uvertices) {
      if (vertexmatches[i] == null) {
        augment(graph, i, weights, potential, vertexmatches, pred, dist, heap, processedu,
            processedv);
      }
    }
  }

  /**
   * Computes a maximum weighted matching on the given undirected, weighted and bipartite graph.
   * Runs in O(m n log n) time and O(n) additional space.
   * 
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param weights
   *          the edge weights
   * @param uvertices
   *          one set in the vertex bipartition of the graph
   * @return a maximum matching on the graph
   */

  public static Mapping<Edge> maximumMatching(UndirectedGraph graph, ConstMapping.OfInt weights,
      PrimitiveIterable.OfInt uvertices) {
    int n = graph.countVertices();
    Edge[] vertexmatches = new Edge[n];

    maximumWeightMatchingImpl(graph, weights, uvertices, vertexmatches);

    PrimitiveList<Edge> matchings = Mappings.newList(Edge.class);
    for (int u : uvertices) {
      if (vertexmatches[u] != null) {
        matchings.add(vertexmatches[u]);
      }
    }

    return matchings;
  }

  /**
   * Computes the maximum weight of a matching on the given undirected, weighted and bipartite
   * graph. Runs in O(m n log n) time and needs O(n) additional space.
   * 
   * @param graph
   *          the undirected and bipartite graph
   * @param weights
   *          the edge weights
   * @param uvertices
   *          one set in the vertex bipartition of the graph
   * @return the size of a maximum matching on the graph
   */
  public static int maximumMatchingWeight(UndirectedGraph graph, ConstMapping.OfInt weights,
      PrimitiveIterable.OfInt uvertices) {
    int n = graph.countVertices();
    Edge[] vertexmatches = new Edge[n];

    maximumWeightMatchingImpl(graph, weights, uvertices, vertexmatches);

    int weight = 0;
    for (int u : uvertices) {
      if (vertexmatches[u] != null) {
        weight += weights.getInt(vertexmatches[u].getIndex());
      }
    }

    return weight;
  }
}
