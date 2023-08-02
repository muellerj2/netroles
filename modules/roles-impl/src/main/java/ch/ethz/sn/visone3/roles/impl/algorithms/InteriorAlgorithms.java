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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntDoubleHeap;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.roles.position.NetworkView;

/**
 * Implements algorithms to compute role interiors for equivalences.
 * 
 */
public class InteriorAlgorithms {

  /*
   * Implementation of most interior algorithms is based on the Paige-Tarjan algorithm.
   * 
   * The algorithm computes the regular interior of an unweighted network in O(m log n) time. A
   * variant of it computes the exact interior of an unweighted network in O(m log n) time.
   * Furthermore, the regular interior of a weighted network with categorical edge weights and the
   * exact interior of a weighted network with categorical or ordinal scale is computed in O(m log^2
   * n) time. However, extension of this algorithm to the regular interior of a weighted network
   * with ordinal edge weights is unclear. Instead, it is implemented via the general iteration
   * scheme, which yields a runtime of O(m n log n).
   */

  int[] counts;
  FixedSizeSlotAllocator countsalloc;

  int nxcolors = 1;
  int[] qtox;
  int[] qcolortoxpos;
  PrimitiveList.OfInt[] xtoqs;
  int[] qstart;
  int[] qend;
  int[] vertidtoqpos;
  int[] qtovertids;
  FixedSizeSlotAllocator qalloc;

  int[] countstobpointer;
  FixedCapacityIntegerList verticestomove;
  int[] verticestomovecounts;

  int[] newqblockmap;
  int[] newqblockmap2;
  boolean[] alllinkstob;

  FixedCapacityIntegerList oldqblocklist;

  FixedCapacityIntegerList clist;

  private static void swap(PrimitiveList.OfInt a, int i, int j) {
    int tmp = a.getInt(i);
    a.setInt(i, a.getInt(j));
    a.setInt(j, tmp);
  }

  private InteriorAlgorithms(int n, int nRelationships, int nColorsAlreadyInUse, boolean incoming,
      boolean outgoing) {

    int relationshipFactor = 1;
    // initializes internal state
    if (incoming && outgoing) {
      relationshipFactor = 2;
    }
    counts = new int[relationshipFactor * nRelationships + n];
    countsalloc = new FixedSizeSlotAllocator(relationshipFactor * nRelationships + n, 1);

    qtox = new int[2 * n];
    qcolortoxpos = new int[2 * n];
    xtoqs = new PrimitiveList.OfInt[n + 1];
    qstart = new int[2 * n];
    qend = new int[2 * n];
    vertidtoqpos = new int[n];
    qtovertids = new int[n];
    xtoqs[0] = Mappings.newIntList();
    qalloc = new FixedSizeSlotAllocator(2 * n, nColorsAlreadyInUse);

    countstobpointer = new int[n];
    alllinkstob = new boolean[n];
    Arrays.fill(countstobpointer, -1);

    verticestomove = new FixedCapacityIntegerList(n);
    verticestomovecounts = new int[verticestomove.capacity()];

    newqblockmap = new int[2 * n];
    newqblockmap2 = new int[2 * n];
    Arrays.fill(newqblockmap, -1);
    Arrays.fill(newqblockmap2, -1);
    oldqblocklist = new FixedCapacityIntegerList(n);

    clist = new FixedCapacityIntegerList(n);
    if (nColorsAlreadyInUse > 1) {
      clist.addInt(0);
    }

  }

  private InteriorAlgorithms(int n, int nRelationships, int nColorsAlreadyInUse) {

    counts = new int[nRelationships + n];
    countsalloc = new FixedSizeSlotAllocator(nRelationships + n, 1);

    qtox = new int[2 * n];
    qcolortoxpos = new int[2 * n];
    xtoqs = new PrimitiveList.OfInt[n + 1];
    qstart = new int[2 * n];
    qend = new int[2 * n];
    vertidtoqpos = new int[n];
    qtovertids = new int[n];
    xtoqs[0] = Mappings.newIntList();
    qalloc = new FixedSizeSlotAllocator(2 * n, nColorsAlreadyInUse);

    countstobpointer = new int[n];
    alllinkstob = new boolean[n];
    Arrays.fill(countstobpointer, -1);

    verticestomove = new FixedCapacityIntegerList(n);
    verticestomovecounts = new int[verticestomove.capacity()];

    newqblockmap = new int[2 * n];
    newqblockmap2 = new int[2 * n];
    Arrays.fill(newqblockmap, -1);
    Arrays.fill(newqblockmap2, -1);
    oldqblocklist = new FixedCapacityIntegerList(n);

    clist = new FixedCapacityIntegerList(n);
    if (nColorsAlreadyInUse > 1) {
      clist.addInt(0);
    }

  }

  private void initializeQBlocks(int n, int[] equivalence, int nColors) {

    // construct the q blocks
    // this is basically counting sort, but we keep the whole internal state
    // for later

    for (int c : equivalence) {
      ++qend[c];
    }
    for (int i = 1; i < nColors; ++i) {
      qstart[i] = qend[i - 1];
      qend[i] += qstart[i];
    }
    for (int i = 0; i < n; ++i) {
      int pos = qstart[equivalence[i]]++;
      vertidtoqpos[i] = pos;
      qtovertids[pos] = i;
    }
    qstart[0] = 0;
    for (int i = 1; i < nColors; ++i) {
      qstart[i] = qend[i - 1];
    }

    // register the q blocks correctly in the only x block
    for (int i = 0; i < nColors; ++i) {
      xtoqs[0].addInt(i);
      qcolortoxpos[i] = i;
    }
  }

  private int popBBlock() {

    // get one of the X blocks there -> this is S
    // find the smaller of two Q blocks in S in there
    // and call it B
    // then remove B from S and create its own X block for it

    int sblock = clist.getInt(clist.size() - 1);
    int sblocksize = xtoqs[sblock].size();
    int blockb = xtoqs[sblock].getInt(sblocksize - 1);

    if (sblocksize > 1) {
      int blockb1size = qend[blockb] - qstart[blockb];
      int blockb2 = xtoqs[sblock].getInt(sblocksize - 2);
      int blockb2size = qend[blockb2] - qstart[blockb2];
      if (blockb1size > blockb2size) {
        qcolortoxpos[blockb] = sblocksize - 2;
        blockb = blockb2;
        swap(xtoqs[sblock], sblocksize - 1, sblocksize - 2);
      }
    }
    int newxblockcolor = nxcolors++;
    xtoqs[newxblockcolor] = Mappings.newIntList(blockb, 1);
    xtoqs[sblock].removeIndex(sblocksize - 1);
    qtox[blockb] = newxblockcolor;
    qcolortoxpos[blockb] = 0;

    if (sblocksize <= 2) {
      clist.poplast();
    }

    return blockb;
  }

  private void countLinksToB(PrimitiveIterable.OfInt bblock, int[] countspointer,
      IntFunction<? extends Iterable<? extends Relationship>> relationships,
      ToIntFunction<Relationship> relationshipTarget, ToIntFunction<Relationship> indexer) {

    // Split according to B:
    // Any vertex connected to B in some way is moved to a new block
    for (int v : bblock) {
      for (Relationship r : relationships.apply(v)) {
        int u = relationshipTarget.applyAsInt(r);
        if (countstobpointer[u] < 0) {
          int countspos = countsalloc.allocate();
          countstobpointer[u] = countspos;
          counts[countspos] = 1;
          verticestomove.addInt(u);
        } else {
          ++counts[countstobpointer[u]];
        }
        if (counts[countstobpointer[u]] == counts[countspointer[indexer.applyAsInt(r)]]) {
          alllinkstob[u] = true;
        }
      }
    }
  }

  private <U> void countLinksToB(PrimitiveIterable.OfInt bblock, int[] countspointer,
      NetworkView<?, U> view) {

    // Split according to B:
    // Any vertex connected to B in some way is moved to a new block
    for (int v : bblock) {
      for (U r : view.inverseTies(v)) {
        int u = view.inverseTieTarget(v, r);
        if (countstobpointer[u] < 0) {
          int countspos = countsalloc.allocate();
          countstobpointer[u] = countspos;
          counts[countspos] = 1;
          verticestomove.addInt(u);
        } else {
          ++counts[countstobpointer[u]];
        }
        if (counts[countstobpointer[u]] == counts[countspointer[view.uniqueInverseTieIndex(v,
            r)]]) {
          alllinkstob[u] = true;
        }
      }
    }
  }

  private void refineBlocksThreeway(int[] equivalence) {

    // does three-way split directly
    // split each color into three blocks: no link to B, all links to B
    // among links to X, and the rest
    // at the end, do clean-up (removing empty blocks) and decide which X
    // blocks have to be additionally processed

    for (int u : verticestomove) {
      int oldcolor = equivalence[u];

      boolean alltob = alllinkstob[u];
      alllinkstob[u] = false;

      int[] map = alltob ? newqblockmap2 : newqblockmap;

      int newcolor = map[oldcolor];

      if (newcolor < 0) {
        newcolor = qalloc.allocate();
        int xblock = qtox[oldcolor];
        qtox[newcolor] = xblock;
        qcolortoxpos[newcolor] = xtoqs[xblock].size();
        xtoqs[xblock].addInt(newcolor);

        qstart[newcolor] = qend[newcolor] = alltob ? qend[oldcolor] : qstart[oldcolor];
        if (newqblockmap[oldcolor] < 0 && newqblockmap2[oldcolor] < 0) {
          oldqblocklist.addInt(oldcolor);
        }
        map[oldcolor] = newcolor;
      }

      int newpos = alltob ? --qstart[newcolor] : qend[newcolor]++;
      int upos = vertidtoqpos[u];
      vertidtoqpos[u] = newpos;
      int swapvertex = qtovertids[newpos];
      vertidtoqpos[swapvertex] = upos;
      qtovertids[upos] = swapvertex;
      qtovertids[newpos] = u;
      equivalence[u] = newcolor;
    }

    for (int oldcolor : oldqblocklist) {
      int newcolor = newqblockmap[oldcolor];
      int newcolor2 = newqblockmap2[oldcolor];
      if (newcolor >= 0) {
        qstart[oldcolor] = qend[newcolor];
      }
      if (newcolor2 >= 0) {
        qend[oldcolor] = qstart[newcolor2];
      }

      int xblock = qtox[oldcolor];

      newqblockmap[oldcolor] = -1;
      newqblockmap2[oldcolor] = -1;

      if (qstart[oldcolor] == qend[oldcolor]) {
        int oldcolorxpos = qcolortoxpos[oldcolor];
        assert (xtoqs[xblock].getInt(oldcolorxpos) == oldcolor);

        int x_rightmostqpos = xtoqs[xblock].size() - 1;
        int x_rightmostq = xtoqs[xblock].getInt(x_rightmostqpos);
        xtoqs[xblock].setInt(oldcolorxpos, x_rightmostq);
        qcolortoxpos[x_rightmostq] = oldcolorxpos;
        xtoqs[xblock].removeIndex(x_rightmostqpos);
        qalloc.free(oldcolor);

        if (newcolor >= 0 && newcolor2 >= 0 && xtoqs[xblock].size() == 2) {
          clist.addInt(xblock);
        }
      } else if (xtoqs[xblock].size() == (newcolor >= 0 && newcolor2 >= 0 ? 3 : 2)) {
        clist.addInt(xblock);
      }
    }
    oldqblocklist.clear();
  }

  private void refineBlocksCounts(int[] equivalence) {

    // sort vertices to be split into new blocks according to the number of
    // links to B
    // make sure that counting sort only uses up to the maximum count on the
    // vertices to move and not on the whole n, otherwise we break the
    // guarantees and cannot achieve O(m log n) runtime
    int maxCount = -1;
    for (int i = 0; i < verticestomove.size(); ++i) {
      int count = counts[countstobpointer[verticestomove.getInt(i)]];
      verticestomovecounts[i] = count;
      maxCount = Integer.max(count, maxCount);
    }

    int[] permverttomove = PrimitiveCollections.countingSort(verticestomovecounts, 0, maxCount + 1,
        0, verticestomove.size());

    // split vertices into the new blocks
    // storage:
    // newqblockmap[oldcolor] keeps the new color to split to for the count
    // stored in newqblockmap2[oldcolor]
    // newqblockmap[newcolor] stores the current number of non-empty blocks
    // we have split oldcolor into
    // whenever the count no longer matches newqblockmap2[oldcolor] or when
    // we have no new block yet, we create a new block and udpate
    // newqblockmap[oldcolor], newqblockmap[newcolor] and
    // newqblockmap2[oldcolor] accordingly
    for (int pu : permverttomove) {
      int u = verticestomove.getInt(pu);
      int count = verticestomovecounts[pu];
      int oldcolor = equivalence[u];

      int newcolor = newqblockmap[oldcolor];
      int colcount = newqblockmap2[oldcolor];

      int splitcount = 0;

      if (newcolor > 0 && count != colcount) {
        qstart[oldcolor] = qend[newcolor];
        splitcount = newqblockmap[newcolor];
        newqblockmap[newcolor] = -1;
        newcolor = -1;
      }

      if (newcolor < 0) {
        newcolor = qalloc.allocate();
        int xblock = qtox[oldcolor];
        qtox[newcolor] = xblock;
        qcolortoxpos[newcolor] = xtoqs[xblock].size();
        xtoqs[xblock].addInt(newcolor);

        qstart[newcolor] = qend[newcolor] = qstart[oldcolor];

        newqblockmap[oldcolor] = newcolor;
        newqblockmap2[oldcolor] = count;
        newqblockmap[newcolor] = splitcount + 1;

        if (colcount < 0) {
          oldqblocklist.addInt(oldcolor);
        }
      }

      int newpos = qend[newcolor]++;
      int upos = vertidtoqpos[u];
      vertidtoqpos[u] = newpos;
      int swapvertex = qtovertids[newpos];
      vertidtoqpos[swapvertex] = upos;
      qtovertids[upos] = swapvertex;
      qtovertids[newpos] = u;
      equivalence[u] = newcolor;
    }

    // do clean-up:
    // remove empty old blocks
    // and add the x block to the ones that have to be processed if
    // necessary
    for (int oldcolor : oldqblocklist) {
      int newcolor = newqblockmap[oldcolor];
      int splitcount = newqblockmap[newcolor] + 1;
      qstart[oldcolor] = qend[newcolor];

      int xblock = qtox[oldcolor];

      newqblockmap[oldcolor] = -1;
      newqblockmap2[oldcolor] = -1;
      newqblockmap[newcolor] = -1;

      if (qstart[oldcolor] == qend[oldcolor]) {
        int oldcolorxpos = qcolortoxpos[oldcolor];
        assert (xtoqs[xblock].getInt(oldcolorxpos) == oldcolor);

        int x_rightmostqpos = xtoqs[xblock].size() - 1;
        int x_rightmostq = xtoqs[xblock].getInt(x_rightmostqpos);
        xtoqs[xblock].setInt(oldcolorxpos, x_rightmostq);
        qcolortoxpos[x_rightmostq] = oldcolorxpos;
        xtoqs[xblock].removeIndex(x_rightmostqpos);
        qalloc.free(oldcolor);

        --splitcount;
      }

      // if we split into at least two classes (so number of classes
      // increases by at least 1)
      // and the number of classes in the x block coincides with the
      // number of times we split the old block into new blocks
      // (so previously, the old q block was the only block in the x
      // block)
      // then we have to add the x block to the c list, because the
      // equivalence might not be stable with respect to some of the new q
      // blocks and the x block wasn't previously in the c list yet
      if (splitcount > 1 && xtoqs[xblock].size() == splitcount) {
        clist.addInt(xblock);
      }
    }
    oldqblocklist.clear();
  }

  private void updateCounts(PrimitiveIterable.OfInt bblock, int[] countspointer,
      IntFunction<? extends Iterable<? extends Relationship>> relationships,
      ToIntFunction<Relationship> relationshipTarget, ToIntFunction<Relationship> indexer) {

    // set the counts on the relationships incident to B to the number of
    // edges incident to B
    for (int v : bblock) {
      for (Relationship r : relationships.apply(v)) {
        int u = relationshipTarget.applyAsInt(r);
        int index = indexer.applyAsInt(r);
        int oldcountspointer = countspointer[index];
        if (--counts[oldcountspointer] == 0) {
          countsalloc.free(oldcountspointer);
        }
        countspointer[index] = countstobpointer[u];
      }
    }

    for (int u : verticestomove) {
      countstobpointer[u] = -1;
    }
    verticestomove.clear();
  }

  private <U> void updateCounts(PrimitiveIterable.OfInt bblock, int[] countspointer,
      NetworkView<?, U> view) {

    // set the counts on the relationships incident to B to the number of
    // edges incident to B
    for (int v : bblock) {
      for (U r : view.inverseTies(v)) {
        int u = view.inverseTieTarget(v, r);
        int index = view.uniqueInverseTieIndex(v, r);
        int oldcountspointer = countspointer[index];
        if (--counts[oldcountspointer] == 0) {
          countsalloc.free(oldcountspointer);
        }
        countspointer[index] = countstobpointer[u];
      }
    }

    for (int u : verticestomove) {
      countstobpointer[u] = -1;
    }
    verticestomove.clear();
  }

  private void splitByBThreeway(PrimitiveIterable.OfInt bblock, int[] equivalence,
      int[] countspointer,
      IntFunction<? extends Iterable<? extends Relationship>> splitRevDirRelationships,
      ToIntFunction<Relationship> splitRevDirRelationshipTarget,
      ToIntFunction<Relationship> indexer) {

    // compute new counts and pre-determine split of blocks according to B
    // afterwards, actually refine the blocks
    countLinksToB(bblock, countspointer, splitRevDirRelationships, splitRevDirRelationshipTarget,
        indexer);
    refineBlocksThreeway(equivalence);

    // update counts for the new x block that consists only of B
    updateCounts(bblock, countspointer, splitRevDirRelationships, splitRevDirRelationshipTarget,
        indexer);
  }

  private void splitByBThreeway(PrimitiveIterable.OfInt bblock, int[] equivalence,
      int[] countspointer, NetworkView<?, ?> view) {

    // compute new counts and pre-determine split of blocks according to B
    // afterwards, actually refine the blocks
    countLinksToB(bblock, countspointer, view);
    refineBlocksThreeway(equivalence);

    // update counts for the new x block that consists only of B
    updateCounts(bblock, countspointer, view);
  }

  private void splitByBExact(PrimitiveIterable.OfInt bblock, int[] equivalence, int[] countspointer,
      IntFunction<? extends Iterable<? extends Relationship>> splitRevDirRelationships,
      ToIntFunction<Relationship> splitRevDirRelationshipTarget,
      ToIntFunction<Relationship> indexer) {

    // compute new counts and pre-determine split of blocks according to B
    // afterwards, actually refine the blocks
    countLinksToB(bblock, countspointer, splitRevDirRelationships, splitRevDirRelationshipTarget,
        indexer);
    refineBlocksCounts(equivalence);

    // update counts for the new x block that consists only of B
    updateCounts(bblock, countspointer, splitRevDirRelationships, splitRevDirRelationshipTarget,
        indexer);
  }

  private void splitByBExact(PrimitiveIterable.OfInt bblock, int[] equivalence, int[] countspointer,
      NetworkView<?, ?> view) {

    // compute new counts and pre-determine split of blocks according to B
    // afterwards, actually refine the blocks
    countLinksToB(bblock, countspointer, view);
    refineBlocksCounts(equivalence);

    // update counts for the new x block that consists only of B
    updateCounts(bblock, countspointer, view);
  }

  private static interface RefinementStep {

    public void apply(InteriorAlgorithms state, PrimitiveIterable.OfInt bblock, int[] equivalence,
        int[] countspointer,
        IntFunction<? extends Iterable<? extends Relationship>> splitRevDirRelationships,
        ToIntFunction<Relationship> splitRevDirRelationshipTarget,
        ToIntFunction<Relationship> indexer);
  }

  private static interface NewRefinementStep {

    public void apply(InteriorAlgorithms state, PrimitiveIterable.OfInt bblock, int[] equivalence,
        int[] countspointer, NetworkView<?, ?> positionView);
  }

  /**
   * Computes the regular interior on the given network. Runs in O(m log n + n) time and needs O(m +
   * n) additional space.
   * 
   * @param n
   *          number of nodes
   * @param positionView
   *          network from the point of view of the individual nodes
   * @param equivalence
   *          an equivalence on the vertices
   * @return the regular interior of the given equivalence on the network.
   */
  public static Mapping.OfInt computeRegularInterior(int n, NetworkView<?, ?> positionView,
      ConstMapping.OfInt equivalence) {
    return computeInterior(n, new NetworkView[] { positionView }, equivalence,
        InteriorAlgorithms::splitByBThreeway);
  }

  /**
   * Computes the exact interior on the given unweighted network. Runs in O(m log n + n) time and
   * needs O(m + n) additional space.
   * 
   * @param n
   *          number of nodes
   * @param positionView
   *          network from the point of view of the individual nodes
   * @param equivalence
   *          an equivalence on the vertices
   * @return the exact interior of the given equivalence on the network.
   */
  public static Mapping.OfInt computeExactInterior(int n, NetworkView<?, ?> positionView,
      ConstMapping.OfInt equivalence) {
    return computeInterior(n, new NetworkView[] { positionView }, equivalence,
        InteriorAlgorithms::splitByBExact);
  }

  private static Mapping.OfInt computeInterior(int n, NetworkView<?, ?>[] views,
      ConstMapping.OfInt equivalence, NewRefinementStep refStep) {
    int[] result = equivalence.intStream().toArray();
    computeInteriorImpl(n, views, result, refStep);
    return Mappings.wrapModifiableInt(result);
  }

  private static void computeInteriorImpl(Network network, int[] equivalence, Direction dir,
      ToIntFunction<Relationship> indexer, RefinementStep refStep) {
    computeInteriorImpl(network, equivalence, dir == Direction.INCOMING, dir == Direction.OUTGOING,
        indexer, refStep);
  }

  private static void computeInteriorImpl(Network network, int[] equivalence, boolean incoming,
      boolean outgoing, ToIntFunction<Relationship> indexer, RefinementStep refStep) {

    // Initialization
    int n = network.asRelation().countUnionDomain();
    int nRelationships = network.asRelation().countRelationships();

    if (nRelationships == 0) {
      return;
    }

    int maxColor = 0;
    for (int c : equivalence) {
      maxColor = Math.max(maxColor, c);
    }

    // pointer counters for incoming and outgoing relationships
    // will be used depending on direction
    int[] countsinpointer = incoming ? new int[nRelationships] : null;
    int[] countsoutpointer = outgoing ? new int[nRelationships] : null;

    InteriorAlgorithms state = new InteriorAlgorithms(n, nRelationships, maxColor + 1, incoming,
        outgoing);

    state.initializeQBlocks(n, equivalence, maxColor + 1);

    state.counts[0] = Integer.MAX_VALUE;

    {
      // initial b block is whole equivalence
      PrimitiveIterable.OfInt bblock = Mappings.wrapUnmodifiable(state.qtovertids, 0, n);

      if (incoming) {
        refStep.apply(state, bblock, equivalence, countsinpointer,
            network.asRelation()::getRelationshipsFrom, (r) -> r.getRight(), indexer);
      }
      if (outgoing) {
        refStep.apply(state, bblock, equivalence, countsoutpointer,
            network.asRelation()::getRelationshipsTo, (r) -> r.getLeft(), indexer);
      }

      state.countsalloc.free(0);
    }

    while (!state.clist.isEmpty()) {

      // Get next block which we have to test stability of the equivalence
      // on
      int blockb = state.popBBlock();

      // Split according to this block, such that the remaining blocks are
      // stable with respect to it
      // Do it for both directions

      // Trick: The algorithm might shuffle the nodes around, so the order
      // is not guaranteed
      // but it will never move nodes from block B outside of
      // [bblockstart, bblockend)
      int bblockstart = state.qstart[blockb];
      int bblockend = state.qend[blockb];
      PrimitiveIterable.OfInt bblock = Mappings.wrapUnmodifiable(state.qtovertids, bblockstart,
          bblockend);

      if (incoming) {
        refStep.apply(state, bblock, equivalence, countsinpointer,
            network.asRelation()::getRelationshipsFrom, (r) -> r.getRight(), indexer);
      }
      if (outgoing) {
        refStep.apply(state, bblock, equivalence, countsoutpointer,
            network.asRelation()::getRelationshipsTo, (r) -> r.getLeft(), indexer);
      }
    }

    // dirty! breaks the state of the RegularInterior object
    // but we don't use it anymore, so let's just reuse the memory space
    int[] colorstore = state.qtox;
    Arrays.fill(colorstore, -1);
    Equivalences.normalizePartition(equivalence, colorstore);

  }

  private static void computeInteriorImpl(int n, NetworkView<?, ?>[] views,
      int[] equivalence, NewRefinementStep refStep) {

    // Initialization
    int nRelationships = 0;
    int[] indexCounts = new int[views.length];
    for (int k = 0; k < views.length; ++k) {
      indexCounts[k] = views[k].maxUniqueTieIndex();
      nRelationships += views[k].countAllTies();
    }

    if (nRelationships == 0) {
      return;
    }

    int maxColor = 0;
    for (int c : equivalence) {
      maxColor = Math.max(maxColor, c);
    }

    // pointer counters for incoming and outgoing relationships
    // will be used depending on direction
    int[][] countspointer = new int[views.length][];
    for (int k = 0; k < views.length; ++k) {
      countspointer[k] = new int[indexCounts[k]];
    }

    InteriorAlgorithms state = new InteriorAlgorithms(n, nRelationships, maxColor + 1);

    state.initializeQBlocks(n, equivalence, maxColor + 1);

    state.counts[0] = Integer.MAX_VALUE;

    {
      // initial b block is whole equivalence
      PrimitiveIterable.OfInt bblock = Mappings.wrapUnmodifiable(state.qtovertids, 0, n);

      for (int k = 0; k < views.length; ++k) {
        refStep.apply(state, bblock, equivalence, countspointer[k], views[k]);
      }

      state.countsalloc.free(0);
    }

    while (!state.clist.isEmpty()) {

      // Get next block which we have to test stability of the equivalence
      // on
      int blockb = state.popBBlock();

      // Split according to this block, such that the remaining blocks are
      // stable with respect to it
      // Do it for both directions

      // Trick: The algorithm might shuffle the nodes around, so the order
      // is not guaranteed
      // but it will never move nodes from block B outside of
      // [bblockstart, bblockend)
      int bblockstart = state.qstart[blockb];
      int bblockend = state.qend[blockb];
      PrimitiveIterable.OfInt bblock = Mappings.wrapUnmodifiable(state.qtovertids, bblockstart,
          bblockend);

      for (int k = 0; k < views.length; ++k) {
        refStep.apply(state, bblock, equivalence, countspointer[k], views[k]);
      }
    }

    // dirty! breaks the state of the RegularInterior object
    // but we don't use it anymore, so let's just reuse the memory space
    int[] colorstore = state.qtox;
    Arrays.fill(colorstore, -1);
    Equivalences.normalizePartition(equivalence, colorstore);
  }

  /**
   * Computes the regular interior on the given network with categorical edge weights. Runs in O(m
   * log^2 n + n) time and needs O(m + n) additional space.
   * 
   * @param network
   *          the network
   * @param equivalence
   *          an equivalence on the vertices
   * @param dir
   *          the link directions to consider
   * @param edgeclasses
   *          the edge classes associated to the edges
   * @return the regular interior of the given equivalence on the network.
   */
  public static int[] computeRegularInterior(Network network, int[] equivalence, Direction dir,
      int[] edgeclasses) {
    return computeInterior(network, equivalence, dir, edgeclasses,
        InteriorAlgorithms::splitByBThreeway);
  }

  /**
   * Computes the exact interior on the given network with categorical edge weights. Runs in O(m
   * log^2 n + n) time and needs O(m + n) additional space.
   * 
   * @param network
   *          the network
   * @param equivalence
   *          an equivalence on the vertices
   * @param dir
   *          the link directions to consider
   * @param edgeclasses
   *          the edge classes associated to the edges
   * @return the regular interior of the given equivalence on the network.
   */
  public static int[] computeExactInterior(Network network, int[] equivalence, Direction dir,
      int[] edgeclasses) {
    return computeInterior(network, equivalence, dir, edgeclasses,
        InteriorAlgorithms::splitByBExact);
  }

  private static int[] computeInterior(Network network, int[] equivalence, Direction dir,
      int[] edgeclasses, RefinementStep refStep) {
    int[] result = Arrays.copyOf(equivalence, equivalence.length);
    if (network.isDirected()) {
      computeInteriorImpl(network, result, dir, edgeclasses, (r) -> r.getIndex(), refStep);
    } else {
      computeInteriorImpl(network, result, dir, edgeclasses,
          (r) -> 2 * r.getIndex() + (r.getRight() > r.getLeft() ? 1 : 0), refStep);
    }
    return result;
  }

  private static void computeInteriorImpl(Network network, int[] equivalence, Direction dir,
      int[] edgeclasses, ToIntFunction<Relationship> indexer, RefinementStep refStep) {

    int n = network.asRelation().countUnionDomain();
    EdgeClassManager manager = new EdgeClassManager(n, edgeclasses);

    computeInteriorImpl(network, equivalence, dir, indexer,
        (state, bblock, innerequivalence, countspointer, splitRevDirRelationships,
            splitRevDirRelationshipTarget, innerindexer) -> edgeClassRefinement(state, bblock,
                innerequivalence, countspointer, splitRevDirRelationships,
                splitRevDirRelationshipTarget, innerindexer, refStep, manager));
  }

  private static void edgeClassRefinement(InteriorAlgorithms state, PrimitiveIterable.OfInt bblock,
      int[] equivalence, int[] countspointer,
      IntFunction<? extends Iterable<? extends Relationship>> splitRevDirRelationships,
      ToIntFunction<Relationship> splitRevDirRelationshipTarget,
      ToIntFunction<Relationship> indexer, RefinementStep innerStep, EdgeClassManager manager) {

    manager.setBBlock(bblock, splitRevDirRelationships);
    PrimitiveIterable.OfInt subblock;
    while ((subblock = manager.getEdgeClassBlock()) != null) {
      innerStep.apply(state, subblock, equivalence, countspointer,
          manager::getEdgeClassRelationships, splitRevDirRelationshipTarget, indexer);
    }
  }

  private static class EdgeClassIterator implements Iterator<Relationship> {
    private Iterator<? extends Relationship> baseiterator_;
    private Relationship currentrelationship;
    private int currentedgeclass;
    private int[] edgeclasses_;

    public EdgeClassIterator(Iterator<? extends Relationship> iterator, int[] edgeclasses) {
      baseiterator_ = iterator;
      edgeclasses_ = edgeclasses;
      moveNext();
    }

    public void setClass(int edgeclass) {
      currentedgeclass = edgeclass;
    }

    public Relationship peekNext() {
      return currentrelationship;
    }

    @Override
    public boolean hasNext() {
      return currentrelationship != null
          && currentedgeclass == edgeclasses_[currentrelationship.getIndex()];
    }

    @Override
    public Relationship next() {
      if (!hasNext())
        throw new NoSuchElementException();
      Relationship curr = currentrelationship;
      moveNext();
      return curr;
    }

    public void moveNext() {
      currentrelationship = baseiterator_.hasNext() ? baseiterator_.next() : null;
    }
  }

  private static class EdgeClassManager {
    private EdgeClassIterator[] firstPassIterators;
    private EdgeClassIterator[] secondPassIterators;

    private int[] bblock;
    private int bblocksize;

    private IntDoubleHeap heap;

    private final int[] edgeclasses_;

    private int passcount;

    public EdgeClassManager(int n, int[] edgeclasses) {
      bblock = new int[n];
      firstPassIterators = new EdgeClassIterator[n];
      secondPassIterators = new EdgeClassIterator[n];
      heap = PrimitiveContainers.fixedUniverseIntDoubleMinHeap(n);
      edgeclasses_ = edgeclasses;
    }

    public void setBBlock(PrimitiveIterable.OfInt bblock,
        IntFunction<? extends Iterable<? extends Relationship>> relationships) {
      for (int u : bblock) {
        Iterable<? extends Relationship> relIterable = relationships.apply(u);

        firstPassIterators[u] = new EdgeClassIterator(relIterable.iterator(), edgeclasses_);
        if (firstPassIterators[u].peekNext() != null) {
          secondPassIterators[u] = new EdgeClassIterator(relIterable.iterator(), edgeclasses_);
          heap.upsert(u, edgeclasses_[firstPassIterators[u].peekNext().getIndex()]);
        }
      }
    }

    public PrimitiveIterable.OfInt getEdgeClassBlock() {

      for (int i = 0; i < bblocksize; ++i) {
        int u = bblock[i];
        if (firstPassIterators[u].peekNext() != null) {
          heap.upsert(u, edgeclasses_[firstPassIterators[u].peekNext().getIndex()]);
        }
      }

      if (heap.isEmpty()) {
        return null;
      }
      int curredgeclass = edgeclasses_[firstPassIterators[heap.peek()].peekNext().getIndex()];
      bblocksize = 0;
      do {
        int u = heap.pop();
        bblock[bblocksize++] = u;
        firstPassIterators[u].setClass(curredgeclass);
        secondPassIterators[u].setClass(curredgeclass);
      } while (!heap.isEmpty()
          && edgeclasses_[firstPassIterators[heap.peek()].peekNext().getIndex()] == curredgeclass);

      passcount = 0;

      return () -> {
        ++passcount;
        return Mappings.wrapUnmodifiable(bblock, 0, bblocksize).iterator();
      };
    }

    public Iterable<? extends Relationship> getEdgeClassRelationships(int u) {
      return () -> passcount < 2 ? firstPassIterators[u] : secondPassIterators[u];
    }

  }

}
