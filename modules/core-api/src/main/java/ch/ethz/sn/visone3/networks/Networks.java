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

package ch.ethz.sn.visone3.networks;

import ch.ethz.sn.visone3.lang.ConstMapping;

import java.util.Objects;

/**
 * Network utilities.
 */
public final class Networks {
  private Networks() {
  }

  // /**
  //  * @see #degrees(Network)
  //  */
  // public static int[] inDegrees(final Network network) {
  //   final DirectedGraph g = Networks.requireDirectedGraph(network);
  //   final IntStream degrees = IntStream.range(0, g.countVertices()).map(g::getInDegree);
  //   return Stats.hist0(degrees::iterator);
  // }
  //
  // /**
  //  * @see #degrees(Network)
  //  */
  // public static int[] outDegrees(final Network network) {
  //   final DirectedGraph g = Networks.requireDirectedGraph(network);
  //   final IntStream degrees = IntStream.range(0, g.countVertices()).map(g::getInDegree);
  //   return Stats.hist0(degrees::iterator);
  // }
  //
  // /**
  //  * Degree distribution of an undirected network.
  //  */
  // public static int[] degrees(final Network network) {
  //   final UndirectedGraph g = Networks.requireUndirectedGraph(network);
  //   final IntStream degrees = IntStream.range(0, g.countVertices()).map(g::getDegree);
  //   return Stats.hist0(degrees::iterator);
  // }

  /**
   * Fails with an {@link IllegalArgumentException} if the network is a 2-mode network.
   *
   * @return the relation view of the {@code network}.
   */
  public static Network requireOneMode(final Network network) {
    if (network.isTwoMode()) {
      throw new IllegalArgumentException("network is 2-mode");
    }
    return network;
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the network is a 1-mode network.
   *
   * @return the relation view of the {@code network}.
   */
  public static Network requireTwoMode(final Network network) {
    if (!network.isTwoMode()) {
      throw new IllegalArgumentException("network is 1-mode");
    }
    return network;
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the network is directed.
   *
   * @return the undirected graph view of the {@code network}.
   */
  public static UndirectedGraph requireUndirectedGraph(final Network network) {
    if (network.isDirected()) {
      throw new IllegalArgumentException("network is directed");
    }
    return network.asUndirectedGraph();
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the network is not directed.
   *
   * @return the directed graph view of the {@code network}.
   */
  public static DirectedGraph requireDirectedGraph(final Network network) {
    if (!network.isDirected()) {
      throw new IllegalArgumentException("network is undirected");
    }
    return network.asDirectedGraph();
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the mapping size does not match a vertex
   * mapping.
   *
   * @return the {@code mapping}
   */
  public static <T extends ConstMapping<?>> T requireVertexMapping(
    final Network network, final T mapping
  ) {
    return checkVertexMapSize(Objects.requireNonNull(network).asRelation().countUnionDomain(),
      mapping, null);
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the mapping size does not match a vertex
   * mapping.
   *
   * @return the {@code mapping}
   */
  public static <T extends ConstMapping<?>> T requireVertexMapping(
    final Network network, final T mapping, final String message
  ) {
    return checkVertexMapSize(Objects.requireNonNull(network).asRelation().countUnionDomain(),
      mapping, message);
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the mapping size does not match the specified
   * number of vertices.
   *
   * @return the {@code mapping}
   */
  public static <T extends ConstMapping<?>> T checkVertexMapSize(
    final int numVertices, final T mapping, final String message
  ) {
    if (numVertices != mapping.size()) {
      throw new IllegalArgumentException(String.format("vertices %s != %s %s",
        numVertices, mapping.size(), message != null ? message : "mapping size"));
    }
    return mapping;
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the mapping size does not match a link
   * mapping.
   *
   * @return the {@code mapping}
   */
  public static <T extends ConstMapping<?>> T requireLinkMapping(
    final Network network, final T mapping
  ) {
    if (Objects.requireNonNull(network, "network == null").countDyadicIndices()
      != Objects.requireNonNull(mapping, "mapping == null").size()) {
      throw new IllegalArgumentException(String.format("network dyads %s != %s mapping size ",
        network.countDyadicIndices(), mapping.size()));
    }
    return mapping;
  }

  /**
   * Fails with an {@link IllegalArgumentException} if the mapping size does not match a link
   * mapping.
   *
   * @return the {@code mapping}
   */
  public static <T extends ConstMapping<?>> T requireLinkMapping(
    final Graph graph, final T mapping
  ) {
    if (Objects.requireNonNull(graph, "graph == null").countDyadicIndices()
      != Objects.requireNonNull(mapping, "mapping == null").size()) {
      throw new IllegalArgumentException(String.format("graph dyads %s != %s mapping size ",
        graph.countDyadicIndices(), mapping.size()));
    }
    return mapping;
  }
}
