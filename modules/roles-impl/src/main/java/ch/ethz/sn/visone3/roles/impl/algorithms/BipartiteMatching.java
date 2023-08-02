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
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.PrimitiveQueue;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

/**
 * Provides algorithms to compute maximum matchings on bipartite graphs.
 * 
 */
public class BipartiteMatching {

  private BipartiteMatching() {
  }

  /**
   * BFS search from specified vertices in one set of the vertex bipartition along alternating
   * paths.
   * 
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param bfsns
   *          array that should be initialized to -1; afterwards, it contains the BFS numbers of
   *          vertices from the same bipartition as the starting vertices
   * @param vertexmatches
   *          assigns vertices to matched edges
   * @param startU
   *          the vertices to start the BFS search from
   * @return the length of the shortest alternating path from one of the starting vertices in one
   *         bipartition set to a free vertex in the other bipartition set
   */
  private static int bfs(UndirectedGraph graph, Mapping.OfInt bfsns, Edge[] vertexmatches,
      PrimitiveIterable.OfInt startU) {
    int shortestlength = -1;

    final PrimitiveQueue.OfInt queue = PrimitiveContainers.intQueue();
    for (int u : startU) {
      queue.push(u);
      bfsns.setInt(u, 0);
    }
    while (queue.size() > 0) {
      final int source = queue.popInt();
      final int sourceBfsn = bfsns.getInt(source);

      final PrimitiveIterator.OfInt out = graph.getNeighbors(source).iterator();
      while (out.hasNext()) {
        final int target = out.nextInt();
        if (vertexmatches[target] != null) {
          int nextutarget = vertexmatches[target].getSource();
          if (bfsns.getInt(nextutarget) < 0) {
            bfsns.setInt(nextutarget, sourceBfsn + 1);
            queue.push(nextutarget);
          }
        } else if (shortestlength == -1) {
          shortestlength = sourceBfsn;
        }
      }
    }

    return shortestlength;
  }

  /**
   * Search for a shortest augmenting path via DFS search along alternating paths.
   * 
   * @param u
   *          the current vertex to search from
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param bfsns
   *          BFS numbers of vertices in the bipartition U; the BFS number of an encountered vertex
   *          is set to -1 to remove it from the set of considered vertices in later DFS passes
   * @param vertexmatches
   *          assigns vertices to incident matched edges; is updated if an augmenting path is found
   * @return whether an augmenting path was found from U and the matching was augmented along his
   *         path
   */
  private static boolean dfs(int u, UndirectedGraph graph, Mapping.OfInt bfsns,
      Edge[] vertexmatches, int shortestlength) {
    int uBfsn = bfsns.getInt(u);

    bfsns.setInt(u, -1);
    for (Edge e : graph.getEdges(u)) {
      int v = e.getTarget();
      boolean newmatchededge = vertexmatches[v] == null && uBfsn == shortestlength;
      if (vertexmatches[v] != null) {
        int nextu = vertexmatches[v].getSource();
        if (bfsns.getInt(nextu) == uBfsn + 1) {
          if (dfs(nextu, graph, bfsns, vertexmatches, shortestlength)) {
            newmatchededge = true;
          }
        }
      }
      if (newmatchededge) {
        vertexmatches[u] = vertexmatches[v] = e;
        return true;
      }
    }
    return false;
  }

  /**
   * Implements the Hopcroft-Karp algorithm to compute a maximum matching on an undirected,
   * unweighted and bipartite graph.
   * 
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param uvertices
   *          one set in the vertex bipartition of the graph
   * @param vertexmatches
   *          assigns vertices to incident matched edges
   * @return the size of a maximum matching on the graph
   */
  private static int maximumMatchingImpl(UndirectedGraph graph, PrimitiveIterable.OfInt uvertices,
      Edge[] vertexmatches) {

    int n = graph.countVertices();
    PrimitiveList.OfInt bfsns = Mappings.newIntList(-1, n);

    PrimitiveIterable.OfInt freeUVertices = () -> StreamSupport
        .intStream(Spliterators.spliteratorUnknownSize(uvertices.iterator(), 0), false)
        .filter(u -> vertexmatches[u] == null).iterator();

    // Algorithm strategy
    // first, start a BFS from all free vertices in the set U of the
    // bipartition
    // Afterwards, compute a DFS from each free vertex of U consistent with
    // the BFS to find a shortest augmenting path from U, augment the
    // matching along this path, and "remove" the vertices along this path
    // for following DFS searches in this round
    // This strategy guarantees that the outer while loop is executed at
    // most sqrt(n) times -> running time O(m sqrt(n)).
    int count = 0;
    int shortestlength;
    while ((shortestlength = bfs(graph, bfsns, vertexmatches, freeUVertices)) >= 0) {

      for (int u : freeUVertices) {
        if (dfs(u, graph, bfsns, vertexmatches, shortestlength)) {
          ++count;
        }
      }

      Arrays.fill(bfsns.arrayQuick(), -1);
    }

    return count;

  }

  /**
   * Computes a maximum matching on the given undirected, unweighted and bipartite graph. Implements
   * the Hopcroft-Karp algorithm in O(m sqrt(n)) time and O(n) additional space.
   * 
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param uvertices
   *          one set in the vertex bipartition of the graph
   * @return a maximum matching on the graph
   */
  public static Mapping<Edge> maximumMatching(UndirectedGraph graph,
      PrimitiveIterable.OfInt uvertices) {
    int n = graph.countVertices();
    Edge[] vertexmatches = new Edge[n];

    int count = maximumMatchingImpl(graph, uvertices, vertexmatches);

    PrimitiveList<Edge> matchings = Mappings.newList(Edge.class, count);
    for (int u : uvertices) {
      if (vertexmatches[u] != null) {
        matchings.add(vertexmatches[u]);
      }
    }

    return matchings;
  }

  /**
   * Computes the size of a maximum matching on the given undirected, unweighted and bipartite
   * graph. Implements the Hopcroft-Karp algorithm in O(m sqrt(n)) time and O(n) additional space.
   * 
   * @param graph
   *          the undirected, unweighted and bipartite graph
   * @param uvertices
   *          one set in the vertex bipartition of the graph
   * @return the size of a maximum matching on the graph
   */
  public static int maximumMatchingSize(UndirectedGraph graph, PrimitiveIterable.OfInt uvertices) {
    int n = graph.countVertices();
    Edge[] vertexmatches = new Edge[n];

    return maximumMatchingImpl(graph, uvertices, vertexmatches);

  }

}
