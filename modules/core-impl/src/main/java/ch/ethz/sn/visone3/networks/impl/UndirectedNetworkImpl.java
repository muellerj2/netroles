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
 * Implements an undirected network by using a constant number of integer
 * arrays.
 */
public class UndirectedNetworkImpl
    implements Network, Relation, UndirectedGraph, Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(UndirectedNetworkImpl.class);
  private static final long serialVersionUID = 1931123468620173444L;

  /**
   * array containing the accumulated degree up to each node i, i.e., the sum over
   * the degrees from 0 to i-1.
   */
  protected final int[] accDegree;

  /**
   * list of neighbor nodes, sorted by the reference node.
   */
  protected int[] neighbors;

  /**
   * list of edge ids, sorted in a matching way to the neighbors.
   */
  protected int[] edgeIds;

  /**
   * number of loops in the network.
   */
  protected final int loopCount;

  private UndirectedNetworkImpl(final int[] accDegree, final int[] neighbors, final int[] edgeIds,
      final int loopCount) {
    this.accDegree = accDegree;
    this.neighbors = neighbors;
    this.edgeIds = edgeIds;
    this.loopCount = loopCount;
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
  public IntStream getNeighborStream(final int vertex) {
    return Arrays.stream(neighbors, accDegree[vertex], accDegree[vertex + 1]);
  }

  @Override
  public PrimitiveIterable.OfInt getNeighbors(int vertex) {
    return Mappings.wrapUnmodifiable(neighbors, accDegree[vertex], accDegree[vertex + 1]);
  }

  @Override
  public int countEdges() {
    return (countRelationships() + countLoops()) / 2;
  }

  @Override
  public int countLoops() {
    return loopCount;
  }

  @Override
  public Iterable<Edge> getEdges(final int vertex) {
    return new NeighborIterable<>(vertex, accDegree[vertex], accDegree[vertex + 1],
        UndirectedEdgeImpl::new);
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
    return false;
  }

  @Override
  public DirectedGraph asDirectedGraph() {
    throw new UnsupportedOperationException();
  }

  @Override
  public UndirectedGraph asUndirectedGraph() {
    return this;
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
  public int countDyadicIndices() {
    return countEdges();
  }

  @Override
  public Iterable<Relationship> getRelationshipsFrom(final int left) {
    return new NeighborIterable<>(left, accDegree[left], accDegree[left + 1],
        UndirectedNetworkImpl::itrFrom);
  }

  @Override
  public Iterable<Relationship> getRelationshipsTo(final int right) {
    return new NeighborIterable<>(right, accDegree[right], accDegree[right + 1],
        UndirectedNetworkImpl::itrTo);
  }

  @Override
  public Iterable<Relationship> getRelationships(final int index) {
    return Iterators.concat(getRelationshipsTo(index), getRelationshipsFrom(index));
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
  public boolean isTwoMode() {
    return false;
  }

  @Override
  public int countMonadicIndices() {
    return accDegree.length - 1;
  }

  @Override
  public int countRelationships() {
    return neighbors.length;
  }

  @Override
  public int countRelationshipsFrom(final int left) {
    return accDegree[left + 1] - accDegree[left];
  }

  @Override
  public int countRelationshipsTo(final int right) {
    return accDegree[right + 1] - accDegree[right];
  }

  @Override
  public IntStream getPartnersStream(final int elemnt) {
    return Arrays.stream(neighbors, accDegree[elemnt], accDegree[elemnt + 1]);
  }

  @Override
  public <T> T[][] asMatrix(T fill, T diagonal, ConstMapping<T> mapping) {
    return MatrixConstruction.toMatrix(this, fill, diagonal, mapping);
  }

  @Override
  public ReorderableNetwork reorderable() {
    return new Reorderable(accDegree, neighbors, edgeIds, loopCount, false);
  }
  // @Override
  // public IntStream getRelationsWithFrom(final int left) {
  // return Arrays.stream(neighbors, accDegree[left], accDegree[left + 1]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo(final int right) {
  // return Arrays.stream(neighbors, accDegree[right], accDegree[right + 1]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo0(final int right) {
  // return Arrays.stream(neighbors, accDegree[right], accDegree[right + 1]);
  // }

  @Override
  public boolean structureEquals(final Network network) {
    if (!(network instanceof UndirectedNetworkImpl)) {
      return false;
    }
    final UndirectedNetworkImpl other = (UndirectedNetworkImpl) network;
    return loopCount == other.loopCount //
        && Arrays.equals(accDegree, other.accDegree) //
        && Arrays.equals(neighbors, other.neighbors) //
        && Arrays.equals(edgeIds, other.edgeIds);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(loopCount) //
        + Arrays.hashCode(accDegree) //
        + Arrays.hashCode(neighbors) //
        + Arrays.hashCode(edgeIds);
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
   * Builder class to produce an undirected network of this implementation.
   */
  public static class Builder implements NetworkBuilder {
    final LongMap<Integer> hash = PrimitiveContainers.longTreeMap();
    final PrimitiveList.OfInt degrees = Mappings.newIntList(Magic.CAP_NODES); // n
    final PrimitiveList.OfInt egos = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt neighbors = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt edgeIds = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    int hits;
    int edgeCount;
    int loopCount;

    @Override
    public void ensureNode(final int node) {
      while (degrees.size() <= node) {
        degrees.addInt(0);
      }
    }

    @Override
    public int addEdge(final int source, final int target) {
      final long set = IntPair.set(source, target);
      final int existing = hash.getOrDefault(set, -1);
      if (existing >= 0) {
        if (hits < 10) {
          LOG.warn("edge already present {} ({},{}) -> {}", set, source, target, existing);
        }
        ++hits;
        return -(existing + 1);
      }
      hash.put(set, edgeCount);

      ensureNode(source);
      ensureNode(target);

      // add edges
      degrees.arrayQuick()[source]++;
      egos.addInt(source);
      neighbors.addInt(target);
      edgeIds.addInt(edgeCount);
      if (source != target) {
        degrees.arrayQuick()[target]++;
        egos.addInt(target);
        neighbors.addInt(source);
        edgeIds.addInt(edgeCount);
      } else {
        ++loopCount;
      }
      return edgeCount++;
    }

    @Override
    public boolean acceptsDirected() {
      return false;
    }

    @Override
    public boolean acceptsTwoModes() {
      return false;
    }

    @Override
    public Network build() {
      if (hits > 0) {
        LOG.warn("{} duplicated edges", hits);
      }
      final int[] pi = PrimitiveCollections.countingSort(egos.array(), degrees.size());
      final int[] ns = PrimitiveCollections.permute(neighbors.array(), pi);
      final int[] es = PrimitiveCollections.permute(edgeIds.array(), pi);
      final int[] ad = new int[degrees.size() + 1];
      for (int i = 0; i < ad.length - 1; i++) {
        ad[i + 1] = ad[i] + degrees.getInt(i);
      }
      final Network net = new UndirectedNetworkImpl(ad, ns, es, loopCount);
      return net;
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
      } while (index < neighbors.length && source > target);
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
      final Edge edge = new UndirectedEdgeImpl(id, source, target);
      loadNext();
      return edge;
    }
  }

  private static final class AdjacencyListEntryImpl implements AdjacencyListEntry {
    private final int self;
    private final int opposite;
    private final int index;

    public AdjacencyListEntryImpl(int index, int self, int opposite) {
      this.index = index;
      this.self = self;
      this.opposite = opposite;
    }

    public int getListSource() {
      return self;
    }

    public Edge getEdge() {
      return new UndirectedEdgeImpl(index, self, opposite);
    }
  }

  private static final class Reorderable extends UndirectedNetworkImpl
      implements ReorderableNetwork, ReorderableUndirectedGraph {

    private static final long serialVersionUID = 4214037512864881603L;
    private boolean copyOnWrite = false;

    private Reorderable(final int[] accDegree, final int[] neighbors, final int[] edgeIds,
        final int loopCount, boolean copyOnWrite) {
      super(accDegree, neighbors, edgeIds, loopCount);
      this.copyOnWrite = copyOnWrite;
    }

    @Override
    public void swapNeighbors(int node, int n1, int n2) {
      if (!copyOnWrite) {
        neighbors = Arrays.copyOf(neighbors, neighbors.length);
        edgeIds = Arrays.copyOf(edgeIds, edgeIds.length);
        copyOnWrite = true;
      }
      final int offset = accDegree[node];
      int tmp = neighbors[offset + n1];
      neighbors[offset + n1] = neighbors[offset + n2];
      neighbors[offset + n2] = tmp;
      tmp = edgeIds[offset + n1];
      edgeIds[offset + n1] = edgeIds[offset + n2];
      edgeIds[offset + n2] = tmp;
    }

    @Override
    public void sortNeighborhoods(final IntBinaryOperator nodeComparator) {
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      final int n = countVertices();
      for (int i = 0; i < n; i++) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        final int[] pi = IntStream.range(begin, end).boxed()
            .sorted((lhs, rhs) -> nodeComparator.applyAsInt(neighbors[lhs], neighbors[rhs]))
            .mapToInt(Integer::intValue).toArray();
        for (int k = begin; k < end; k++) {
          tmpNeighbors[pi[k - begin]] = neighbors[k];
          tmpEdgeIds[pi[k - begin]] = edgeIds[k];
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public void sortNeighborhoods(Comparator<AdjacencyListEntry> comparator) {
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      final int n = countVertices();
      for (int i = 0; i < n; i++) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        final int self = i;
        final int[] pi = IntStream.range(begin, end).boxed()
            .sorted((lhs, rhs) -> comparator.compare(
                new AdjacencyListEntryImpl(edgeIds[lhs], self, neighbors[lhs]),
                new AdjacencyListEntryImpl(edgeIds[rhs], self, neighbors[rhs])))
            .mapToInt(Integer::intValue).toArray();
        for (int k = begin; k < end; k++) {
          tmpNeighbors[pi[k - begin]] = neighbors[k];
          tmpEdgeIds[pi[k - begin]] = edgeIds[k];
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
        final int end = accDegree[i + 1];
        for (int j = begin; j < end; ++j) {
          values[j] = valueProducer
              .applyAsInt(new AdjacencyListEntryImpl(edgeIds[j], i, neighbors[j]));
        }
      }

      // TODO: Use counting sort if universeSize in O(n+m)
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      for (int i = 0; i < n; ++i) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        final int[] pi = IntStream.range(begin, end).boxed()
            .sorted((lhs, rhs) -> Integer.compare(values[lhs], values[rhs]))
            .mapToInt(Integer::intValue).toArray();
        for (int k = begin; k < end; k++) {
          tmpNeighbors[pi[k - begin]] = neighbors[k];
          tmpEdgeIds[pi[k - begin]] = edgeIds[k];
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public ReorderableNetwork reorderable() {
      return new Reorderable(accDegree, Arrays.copyOf(neighbors, neighbors.length),
          Arrays.copyOf(edgeIds, edgeIds.length), loopCount, true);
    }

    @Override
    public ReorderableDirectedGraph asDirectedGraph() {
      return (ReorderableDirectedGraph) super.asDirectedGraph();
    }

    @Override
    public ReorderableUndirectedGraph asUndirectedGraph() {
      return this;
    }

  }

}
