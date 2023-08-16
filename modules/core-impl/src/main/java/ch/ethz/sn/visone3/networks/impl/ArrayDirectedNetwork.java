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

package ch.ethz.sn.visone3.networks.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.AdjacencyListEntry;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.ReorderableDirectedGraph;
import ch.ethz.sn.visone3.networks.ReorderableNetwork;
import ch.ethz.sn.visone3.networks.ReorderableUndirectedGraph;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

/**
 * Implements a directed network by using a constant number of integer arrays.
 */
public class ArrayDirectedNetwork implements Network, Relation, DirectedGraph, Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(ArrayDirectedNetwork.class);
  private static final long serialVersionUID = 1931123468620173444L;

  /**
   * array containing the accumulated degree up to each node i, i.e., the sum over
   * the degrees from 0 to i-1.
   */
  protected final int[] accDegree;

  /**
   * array containing the indegree of each node.
   */
  protected final int[] inDegree;

  /**
   * list of neighbor nodes, sorted by the reference node and for each reference
   * node partitioned by direction..
   */
  protected int[] neighbors;

  /**
   * list of edge ids, sorted in a matching way to the neighbors.
   */
  protected int[] edgeIds;

  private ArrayDirectedNetwork(final int[] accDegree, final int[] inDegree, final int[] neighbors,
      final int[] edgeIds) {
    if (accDegree.length - 1 != inDegree.length) {
      throw new IllegalArgumentException("degree size " + accDegree.length + " " + inDegree.length);
    }
    if (neighbors.length != edgeIds.length) {
      throw new IllegalArgumentException("edge size " + neighbors.length + " " + edgeIds.length);
    }
    this.accDegree = accDegree;
    this.inDegree = inDegree;
    this.neighbors = neighbors;
    this.edgeIds = edgeIds;
  }

  private static Edge itrOutEdge(final int edge, final int self, final int opposite) {
    return new DirectedEdgeImpl(edge, self, opposite);
  }

  private static Edge itrInEdge(final int edge, final int self, final int opposite) {
    return new DirectedEdgeImpl(edge, opposite, self);
  }

  private static Relationship itrFrom(final int edge, final int self, final int opposite) {
    return new RelationshipImpl(edge, self, opposite);
  }

  private static Relationship itrTo(final int edge, final int self, final int opposite) {
    return new RelationshipImpl(edge, opposite, self);
  }

  @Override
  public int countVertices() {
    return accDegree.length - 1;
  }

  @Override
  public IntStream getInNeighborStream(final int target) {
    return Arrays.stream(neighbors, accDegree[target], accDegree[target] + inDegree[target]);
  }

  @Override
  public Iterable<Edge> getInEdges(final int target) {
    return new NeighborIterable<>(target, accDegree[target], accDegree[target] + inDegree[target],
        ArrayDirectedNetwork::itrInEdge);
  }

  @Override
  public int getInDegree(final int target) {
    return inDegree[target];
  }

  @Override
  public IntStream getOutNeighborStream(final int source) {
    return Arrays.stream(neighbors, accDegree[source] + inDegree[source], accDegree[source + 1]);
  }

  @Override
  public Iterable<Edge> getOutEdges(final int source) {
    return new NeighborIterable<>(source, accDegree[source] + inDegree[source],
        accDegree[source + 1], ArrayDirectedNetwork::itrOutEdge);
  }

  @Override
  public int getOutDegree(final int source) {
    return getDegree(source) - inDegree[source];
  }

  @Override
  public IntStream getNeighborStream(final int vertex) {
    return Arrays.stream(neighbors, accDegree[vertex], accDegree[vertex + 1]);
  }

  @Override
  public int countEdges() {
    return countRelationships();
  }

  @Override
  public Iterable<Edge> getEdges(final int vertex) {
    return Iterators.concat(getInEdges(vertex), getOutEdges(vertex));
  }

  @Override
  public Iterable<Edge> getEdges() {
    return AllEdgeIterator::new;
  }

  @Override
  public int getDegree(final int vertex) {
    return accDegree[vertex + 1] - accDegree[vertex];
  }

  @Override
  public boolean isDirected() {
    return true;
  }

  @Override
  public DirectedGraph asDirectedGraph() {
    return this;
  }

  @Override
  public UndirectedGraph asUndirectedGraph() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Relation asRelation() {
    return this;
  }

  @Override
  public NetworkBuilder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(final Object rhs) {
    return (rhs instanceof Network) && structureEquals((Network) rhs);
  }

  @Override
  public boolean isTwoMode() {
    return false;
  }

  @Override
  public int countMonadicIndices() {
    return countUnionDomain();
  }

  @Override
  public int countDyadicIndices() {
    return edgeIds.length / 2;
  }

  @Override
  public String toString() {
    return AsciiDumper.singleLine((Relation) this);
  }

  @Override
  public PrimitiveIterable.OfInt getLeftDomain() {
    return Mappings.intRange(0, countLeftDomain());
  }

  @Override
  public int countLeftDomain() {
    return countUnionDomain();
  }

  @Override
  public PrimitiveIterable.OfInt getRightDomain() {
    return Mappings.intRange(0, countRightDomain());
  }

  @Override
  public int countRightDomain() {
    return countUnionDomain();
  }

  @Override
  public PrimitiveIterable.OfInt getUnionDomain() {
    return Mappings.intRange(0, countUnionDomain());
  }

  @Override
  public int countUnionDomain() {
    return accDegree.length - 1;
  }

  @Override
  public int countRelationships() {
    return neighbors.length / 2;
  }

  @Override
  public Iterable<Relationship> getRelationshipsFrom(final int left) {
    return new NeighborIterable<>(left, accDegree[left] + inDegree[left], accDegree[left + 1],
        ArrayDirectedNetwork::itrFrom);
  }

  @Override
  public Iterable<Relationship> getRelationshipsTo(final int right) {
    return new NeighborIterable<>(right, accDegree[right], accDegree[right] + inDegree[right],
        ArrayDirectedNetwork::itrTo);
  }

  @Override
  public int countRelationshipsFrom(final int left) {
    return getOutDegree(left);
  }

  @Override
  public int countRelationshipsTo(final int right) {
    return getInDegree(right);
  }

  @Override
  public IntStream getPartnersStream(final int elemnt) {
    return Arrays.stream(neighbors, accDegree[elemnt], accDegree[elemnt + 1]);
  }

  // @Override
  // public IntStream getRelationsWithFrom(final int left) {
  // if ((accDegree[left + 1] - accDegree[left]) - inDegree[left] == 0) {
  // throw new IllegalArgumentException("Node has no out edges");
  // }
  // //TODO: correct indexing?
  // return Arrays.stream(neighbors, accDegree[left] + inDegree[left], accDegree[left + 1]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo(final int right) {
  // if (inDegree[right] == 0) {
  // throw new IllegalArgumentException("Node has no in edges");
  // }
  // return Arrays.stream(neighbors, accDegree[right], accDegree[right] + inDegree[right]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo0(final int right) {
  // return null;
  // }

  @Override
  public Iterable<Relationship> getRelationships(final int index) {
    return Iterators.concat(getRelationshipsTo(index), getRelationshipsFrom(index));
  }

  @Override
  public <T> T[][] asMatrix(T fill, T diagonal, ConstMapping<T> mapping) {
    return MatrixConstruction.toMatrix(this, fill, diagonal, mapping);
  }

  @Override
  public boolean structureEquals(Network network) {
    if (!(network instanceof ArrayDirectedNetwork)) {
      return false;
    }
    final ArrayDirectedNetwork other = (ArrayDirectedNetwork) network;
    return Arrays.equals(accDegree, other.accDegree) //
        && Arrays.equals(inDegree, other.inDegree) //
        && Arrays.equals(neighbors, other.neighbors) //
        && Arrays.equals(edgeIds, other.edgeIds);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(accDegree) //
        + Arrays.hashCode(inDegree) //
        + Arrays.hashCode(neighbors) //
        + Arrays.hashCode(edgeIds);
  }

  @Override
  public ReorderableNetwork reorderable() {
    return new Reorderable(accDegree, inDegree, neighbors, edgeIds, false);
  }

  /**
   * Convenience interface to reduce number of iterators.
   */
  @FunctionalInterface
  private interface ItrOutput<T> {
    T edge(int edge, int self, int opposite);
  }

  @FunctionalInterface
  private interface IntItrOutput {
    int edge(int edge, int self, int opposite);
  }

  /**
   * Builder class to produce a directed network of this implementation.
   */
  public static class Builder implements NetworkBuilder {
    final LongMap<Integer> hash = PrimitiveContainers.longTreeMap();
    final PrimitiveList.OfInt degrees = Mappings.newIntList(Magic.CAP_NODES); // n
    final PrimitiveList.OfInt indegrees = Mappings.newIntList(Magic.CAP_NODES); // n
    final PrimitiveList.OfInt sortKey = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt targets = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt edgeIds = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    int hits;
    int edgeCount;

    @Override
    public void ensureNode(final int node) {
      while (degrees.size() <= node) {
        degrees.addInt(0);
        indegrees.addInt(0);
      }
    }

    @Override
    public int addEdge(final int source, final int target) {
      final long set = IntPair.tuple(source, target);
      if (hash.contains(set)) {
        if (hits < 10) {
          LOG.warn("ignoring duplicate edge ({},{}) [{} hits]", source, target, hits);
        }
        ++hits;
        return -(hash.get(set) + 1);
      }
      hash.put(set, edgeCount);

      ensureNode(source);
      ensureNode(target);

      // count degrees
      degrees.arrayQuick()[source]++;
      degrees.arrayQuick()[target]++;
      indegrees.arrayQuick()[target]++;
      // add incoming
      sortKey.addInt(2 * target);
      targets.addInt(source);
      edgeIds.addInt(edgeCount);
      // add outgoing edge
      sortKey.addInt(2 * source + 1);
      targets.addInt(target);
      edgeIds.addInt(edgeCount);
      return edgeCount++;
    }

    @Override
    public boolean acceptsDirected() {
      return true;
    }

    @Override
    public boolean acceptsTwoModes() {
      return false;
    }

    @Override
    public Network build() {
      if (hits > 0) {
        LOG.warn("ignored {} reversed edges", hits);
      }
      final int[] pi = PrimitiveCollections.countingSort(sortKey.array(), 2 * degrees.size());
      final int[] ns = PrimitiveCollections.permute(targets.array(), pi);
      final int[] es = PrimitiveCollections.permute(edgeIds.array(), pi);
      final int[] ad = new int[degrees.size() + 1];
      for (int i = 0; i < ad.length - 1; i++) {
        ad[i + 1] = ad[i] + degrees.getInt(i);
      }
      return new ArrayDirectedNetwork(ad, indegrees.array(), ns, es);
    }
  }

  private class NeighborIterable<T> implements Iterable<T> {
    final int vertex;
    final int begin;
    final int end;
    final ItrOutput<T> fac;

    NeighborIterable(final int vertex, final int begin, final int end, final ItrOutput<T> fac) {
      this.vertex = vertex;
      this.fac = fac;
      this.begin = begin;
      this.end = end;
    }

    @Override
    public Iterator<T> iterator() {
      return new Itr();
    }

    private class Itr implements Iterator<T> {
      int it = begin;

      @Override
      public boolean hasNext() {
        return it < end;
      }

      @Override
      public T next() {
        if (hasNext()) {
          final int edge = edgeIds[it];
          final T r = fac.edge(edge, vertex, neighbors[it]);
          it++;
          return r;
        }
        throw new NoSuchElementException();
      }
    }
  }

  private class AllEdgeIterator implements Iterator<Edge> {
    int index = -1;
    int source = 0;
    int target = 0;

    AllEdgeIterator() {
      loadNext();
    }

    void loadNext() {
      do {
        ++index;
        if (index < neighbors.length) {
          while (index >= accDegree[source + 1]) {
            ++source;
          }
          target = neighbors[index];
        }
      } while (index < neighbors.length && index < accDegree[source] + inDegree[source]);
    }

    @Override
    public boolean hasNext() {
      return index < neighbors.length;
    }

    @Override
    public Edge next() {
      if (index >= neighbors.length) {
        throw new NoSuchElementException();
      }
      final int id = edgeIds[index];
      final Edge edge = new DirectedEdgeImpl(id, source, target);
      loadNext();
      return edge;
    }
  }

  private static class AdjacencyListEntryImpl implements AdjacencyListEntry {

    private final int index;
    private final int self;
    private final int opposite;
    private final Direction direction;

    public AdjacencyListEntryImpl(int index, int self, int opposite, Direction direction) {
      this.index = index;
      this.self = self;
      this.opposite = opposite;
      this.direction = direction;
    }

    @Override
    public int getListSource() {
      return self;
    }

    @Override
    public Edge getEdge() {
      return new DirectedEdgeImpl(index, direction == Direction.INCOMING ? opposite : self,
          direction == Direction.INCOMING ? self : opposite);
    }

  }

  private static class Reorderable extends ArrayDirectedNetwork
      implements ReorderableNetwork, ReorderableDirectedGraph {

    private static final long serialVersionUID = 9117384431817681298L;
    private boolean copyOnWrite = false;

    private Reorderable(final int[] accDegree, final int[] inDegree, final int[] neighbors,
        final int[] edgeIds, boolean copyOnWrite) {
      super(accDegree, inDegree, neighbors, edgeIds);
      this.copyOnWrite = copyOnWrite;
    }

    @Override
    public void swapNeighbors(int node, Direction direction, int n1, int n2) {
      if (!copyOnWrite) {
        neighbors = Arrays.copyOf(neighbors, neighbors.length);
        edgeIds = Arrays.copyOf(edgeIds, edgeIds.length);
        copyOnWrite = true;
      }
      final int offset = accDegree[node] + (direction == Direction.OUTGOING ? inDegree[node] : 0);
      int tmp = neighbors[offset + n1];
      neighbors[offset + n1] = neighbors[offset + n2];
      neighbors[offset + n2] = tmp;
      tmp = edgeIds[offset + n1];
      edgeIds[offset + n1] = edgeIds[offset + n2];
      edgeIds[offset + n2] = tmp;
    }

    @Override
    public void sortNeighborhoods(IntBinaryOperator nodeComparator) {
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      for (int i = 0; i < countVertices(); i++) {
        for (int j = 0; j < 2; j++) { // in/out
          final int begin = accDegree[i] + (j == 0 ? 0 : inDegree[i]);
          final int end = j == 0 ? begin + inDegree[i] : accDegree[i + 1];
          final int[] pi = IntStream.range(begin, end).boxed()
              .sorted((lhs, rhs) -> nodeComparator.applyAsInt(neighbors[lhs], neighbors[rhs]))
              .mapToInt(Integer::intValue).toArray();
          for (int k = begin; k < end; k++) {
            tmpNeighbors[pi[k - begin]] = neighbors[k];
            tmpEdgeIds[pi[k - begin]] = edgeIds[k];
          }
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
    }

    @Override
    public void sortNeighborhoods(Comparator<AdjacencyListEntry> comparator) {

      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      final int n = countVertices();
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < 2; j++) { // in/out
          final int begin = accDegree[i] + (j == 0 ? 0 : inDegree[i]);
          final int end = j == 0 ? begin + inDegree[i] : accDegree[i + 1];
          final int self = i;
          final Direction direction = j == 0 ? Direction.INCOMING : Direction.OUTGOING;
          final int[] pi = IntStream.range(begin, end).boxed()
              .sorted((lhs, rhs) -> comparator.compare(
                  new AdjacencyListEntryImpl(edgeIds[lhs], self, neighbors[lhs], direction),
                  new AdjacencyListEntryImpl(edgeIds[rhs], self, neighbors[rhs], direction)))
              .mapToInt(Integer::intValue).toArray();
          for (int k = begin; k < end; k++) {
            tmpNeighbors[pi[k - begin]] = neighbors[k];
            tmpEdgeIds[pi[k - begin]] = edgeIds[k];
          }
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public void sortNeighborhoods(ToIntFunction<AdjacencyListEntry> valueProducer,
        int universeSize) {
      final int[] values = new int[neighbors.length];
      final int n = countVertices();
      for (int i = 0; i < n; ++i) {
        final int begin = accDegree[i];
        final int outBegin = begin + inDegree[i];
        final int end = accDegree[i + 1];
        for (int j = begin; j < outBegin; ++j) {
          values[j] = valueProducer.applyAsInt(
              new AdjacencyListEntryImpl(edgeIds[j], i, neighbors[j], Direction.INCOMING));
        }
        for (int j = outBegin; j < end; ++j) {
          values[j] = valueProducer.applyAsInt(
              new AdjacencyListEntryImpl(edgeIds[j], i, neighbors[j], Direction.OUTGOING));
        }
      }

      // TODO: Use counting sort if universeSize in O(n+m)
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < 2; j++) { // in/out
          final int begin = accDegree[i] + (j == 0 ? 0 : inDegree[i]);
          final int end = j == 0 ? begin + inDegree[i] : accDegree[i + 1];
          final int[] pi = IntStream.range(begin, end).boxed()
              .sorted((lhs, rhs) -> Integer.compare(values[lhs], values[rhs]))
              .mapToInt(Integer::intValue).toArray();
          for (int k = begin; k < end; k++) {
            tmpNeighbors[pi[k - begin]] = neighbors[k];
            tmpEdgeIds[pi[k - begin]] = edgeIds[k];
          }
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public ReorderableNetwork reorderable() {
      return new Reorderable(accDegree, inDegree, Arrays.copyOf(neighbors, neighbors.length),
          Arrays.copyOf(edgeIds, edgeIds.length), true);
    }

    @Override
    public ReorderableDirectedGraph asDirectedGraph() {
      return this;
    }

    @Override
    public ReorderableUndirectedGraph asUndirectedGraph() {
      return (ReorderableUndirectedGraph) super.asUndirectedGraph();
    }

  }

}
