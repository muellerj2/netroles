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

package ch.ethz.sn.visone3.roles.lattice;

import java.util.Arrays;
import java.util.NoSuchElementException;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.lattice.DepthFirstSearchEnumerator.CoverEnumerator;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

/**
 * Provides implements of enumerators for lower and upper covers on the lattices
 * of binary relations, equivalences and rankings.
 */
public class CoverEnumerators {

  private CoverEnumerators() {
  }

  /**
   * Returns an enumerator of upper covers of {@code rel} on the lattice of binary
   * relations.
   * 
   * @param rel the binary relation.
   * @return an enumerator of upper covers of {@code rel}.
   */
  public static CoverEnumerator<BinaryRelation, BinaryRelation> upperCoversBinaryRelations(
      BinaryRelation rel) {
    return new CoverEnumerator<BinaryRelation, BinaryRelation>() {

      BinaryRelation nextRelation = null;
      int pos = 0;
      int count = -1;

      @Override
      public boolean hasNext() {
        if (nextRelation == null) {
          generateNext();
        }
        return nextRelation != null;
      }

      @Override
      public BinaryRelation next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        BinaryRelation result = nextRelation;
        nextRelation = null;
        return result;
      }

      private void generateNext() {
        if (count >= 0 && pos >= count) {
          return;
        }
        int n = rel.domainSize();
        RelationBuilder<? extends BinaryRelation> builder = RelationBuilders
            .denseRelationBuilder(n);
        int count = 0;
        for (int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j) {
            if (rel.contains(i, j)) {
              builder.add(i, j);
            } else {
              if (count == pos) {
                builder.add(i, j);
              }
              ++count;
            }
          }
        }
        this.count = count;
        if (pos >= count) {
          return;
        }
        nextRelation = builder.build();
        ++pos;
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(BinaryRelation val,
          BinaryRelation mustBeProducedBefore) {
        final int n = rel.domainSize();
        boolean decisionMade = false;
        boolean decision = false;
        for (int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j) {
            boolean relContains = rel.contains(i, j);
            boolean valContains = val.contains(i, j);
            if (relContains && !valContains) {
              return false;
            }
            boolean producedBeforeContains = mustBeProducedBefore.contains(i, j);
            if (!decisionMade) {
              if (valContains && !producedBeforeContains) {
                decision = true;
                decisionMade = true;
              } else if (!valContains && producedBeforeContains) {
                decisionMade = true;
              }
            }
          }
        }
        return decision || !decisionMade;
      }

    };
  }

  /**
   * Returns an enumerator of lower covers of {@code rel} on the lattice of binary
   * relations.
   * 
   * @param rel the binary relation.
   * @return an enumerator of lower covers of {@code rel}.
   */
  public static CoverEnumerator<BinaryRelation, BinaryRelation> lowerCoversBinaryRelations(
      BinaryRelation rel) {
    return new CoverEnumerator<BinaryRelation, BinaryRelation>() {

      BinaryRelation nextRelation = null;
      int pos = 0;
      int count = -1;

      @Override
      public boolean hasNext() {
        if (nextRelation == null) {
          generateNext();
        }
        return nextRelation != null;
      }

      @Override
      public BinaryRelation next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        BinaryRelation result = nextRelation;
        nextRelation = null;
        return result;
      }

      private void generateNext() {
        if (count >= 0 && pos >= count) {
          return;
        }
        int n = rel.domainSize();
        RelationBuilder<? extends BinaryRelation> builder = RelationBuilders
            .denseRelationBuilder(n);
        int count = 0;
        for (int i = 0; i < n; ++i) {
          for (int j : rel.iterateInRelationFrom(i)) {
            if (count != pos) {
              builder.add(i, j);
            }
            ++count;
          }
        }
        this.count = count;
        if (pos >= count) {
          return;
        }
        nextRelation = builder.build();
        ++pos;
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(BinaryRelation val,
          BinaryRelation mustBeProducedBefore) {
        final int n = rel.domainSize();
        boolean decisionMade = false;
        boolean decision = false;
        for (int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j) {
            boolean relContains = rel.contains(i, j);
            boolean valContains = val.contains(i, j);
            if (!relContains && valContains) {
              return false;
            }
            boolean producedBeforeContains = mustBeProducedBefore.contains(i, j);
            if (!decisionMade) {
              if (!valContains && producedBeforeContains) {
                decision = true;
                decisionMade = true;
              } else if (valContains && !producedBeforeContains) {
                decisionMade = true;
              }
            }
          }
        }
        return decision || !decisionMade;
      }

    };
  }

  /**
   * Returns an enumerator of upper covers of {@code parent} on the lattice of
   * equivalences.
   * 
   * @param parent the equivalence.
   * @return an enumerator of upper covers of {@code parent}.
   */
  public static CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt> upperCoversEquivalences(
      ConstMapping.OfInt parent) {
    return new CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt>() {

      private int[] nextEquivalence = null;
      private int mergeClass1 = 0;
      private int mergeClass2 = 1;
      private int maxclass;
      {
        maxclass = -1;
        for (int k : parent) {
          maxclass = Math.max(maxclass, k);
        }
      }

      @Override
      public boolean hasNext() {
        if (nextEquivalence == null) {
          generateNext();
        }
        return nextEquivalence != null;
      }

      @Override
      public Mapping.OfInt next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        int[] result = nextEquivalence;
        nextEquivalence = null;
        return Mappings.wrapModifiableInt(result);
      }

      private void generateNext() {
        if (mergeClass2 <= maxclass && mergeClass1 >= mergeClass2) {
          ++mergeClass2;
          mergeClass1 = 0;
        }
        if (mergeClass2 > maxclass) {
          return;
        }
        int n = parent.size();
        int[] result = new int[n];
        for (int i = 0; i < n; ++i) {
          int oldclass = parent.getInt(i);
          if (oldclass == mergeClass2) {
            result[i] = mergeClass1;
          } else if (oldclass < mergeClass2) {
            result[i] = oldclass;
          } else {
            result[i] = oldclass - 1;
          }
        }
        nextEquivalence = result;
        ++mergeClass1;
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(ConstMapping.OfInt val,
          ConstMapping.OfInt mustBeProducedBefore) {
        final int n = val.size();
        int mergedClass1 = -1;
        int mergedClass2 = -1;
        int[] parentToValClass = new int[n];
        Arrays.fill(parentToValClass, -1);
        for (int i = 0; i < n; ++i) {
          int parentClass = parent.getInt(i);
          int valClass = val.getInt(i);
          if (parentClass != valClass && mergedClass1 == -1) {
            mergedClass1 = valClass;
            mergedClass2 = parentClass;
          }
          if (parentToValClass[parentClass] == -1) {
            parentToValClass[parentClass] = valClass;
          } else if (parentToValClass[parentClass] != valClass) {
            return false;
          }
        }
        if (mergedClass1 == -1) {
          return false;
        }
        for (int i = 0; i < n; ++i) {
          int parentClass = parent.getInt(i);
          int leastImmediateChildValue;
          if (parentClass < mergedClass2) {
            leastImmediateChildValue = parentClass;
          } else if (parentClass == mergedClass2) {
            leastImmediateChildValue = mergedClass1;
          } else {
            leastImmediateChildValue = parentClass - 1;
          }
          int mustBeProducedBeforeValue = mustBeProducedBefore.getInt(i);
          if (leastImmediateChildValue > mustBeProducedBeforeValue) {
            return false;
          } else if (leastImmediateChildValue < mustBeProducedBeforeValue) {
            return true;
          }
        }
        return true;
      }
    };
  }

  /**
   * Returns an enumerator of lower covers of {@code parent} on the lattice of
   * equivalences.
   * 
   * @param parent the equivalence.
   * @return an enumerator of lower covers of {@code parent}.
   */
  public static CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt> lowerCoversEquivalences(
      ConstMapping.OfInt parent) {
    return new CoverEnumerator<ConstMapping.OfInt, Mapping.OfInt>() {

      private int[] nextEquivalence = null;
      private int splitClass;
      private int[] classCounts = null;
      private boolean[] splitterAssignment = null;
      private int maxclass;
      {
        maxclass = -1;
        for (int k : parent) {
          maxclass = Math.max(maxclass, k);
        }
        classCounts = new int[maxclass + 1];
        for (int k : parent) {
          ++classCounts[k];
        }
        splitClass = maxclass;
      }

      @Override
      public boolean hasNext() {
        if (nextEquivalence == null) {
          generateNext();
        }
        return nextEquivalence != null;
      }

      @Override
      public Mapping.OfInt next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        int[] result = nextEquivalence;
        nextEquivalence = null;
        return Mappings.wrapModifiableInt(result);
      }

      private void generateNext() {
        if (splitClass < 0) {
          return;
        }
        while (classCounts[splitClass] == 1) {
          --splitClass;
          if (splitClass < 0) {
            return;
          }
        }
        if (splitterAssignment == null) {
          splitterAssignment = new boolean[classCounts[splitClass] - 1];
          splitterAssignment[classCounts[splitClass] - 2] = true;
        }
        int n = parent.size();
        int[] result = new int[n];
        int currentmaxclass = -1;
        int newlySplitClass = maxclass + 2;
        int splitClassCount = 0;
        for (int i = 0; i < n; ++i) {
          int oldclass = parent.getInt(i);
          currentmaxclass = Math.max(currentmaxclass, oldclass);
          if (oldclass == splitClass) {
            ++splitClassCount;
            if (splitClassCount > 1) {
              if (splitterAssignment[splitClassCount - 2] && newlySplitClass > maxclass + 1) {
                newlySplitClass = currentmaxclass + 1;
              }
              result[i] = splitterAssignment[splitClassCount - 2] ? newlySplitClass : oldclass;
            } else {
              result[i] = oldclass;
            }
          } else if (oldclass >= newlySplitClass) {
            result[i] = oldclass + 1;
          } else {
            result[i] = oldclass;
          }
        }
        nextEquivalence = result;
        boolean success = false;
        for (int i = splitterAssignment.length - 1; i >= 0; --i) {
          splitterAssignment[i] = !splitterAssignment[i];
          if (splitterAssignment[i]) {
            success = true;
            break;
          }
        }
        if (!success) {
          splitterAssignment = null;
          --splitClass;
        }
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(ConstMapping.OfInt val,
          ConstMapping.OfInt mustBeProducedBefore) {
        final int n = val.size();
        final int[] classSizeParent = new int[n];
        final int[] classSizeVal = new int[n];
        final int[] valToParent = new int[n];
        Arrays.fill(valToParent, -1);
        int maxClassVal = 0;
        int maxParentClassDiffering = -1;
        for (int i = 0; i < n; ++i) {
          int classVal = val.getInt(i);
          maxClassVal = Math.max(maxClassVal, classVal);
          int classParent = parent.getInt(i);
          int supposedParentClass = valToParent[classVal];
          if (supposedParentClass == -1) {
            valToParent[classVal] = classParent;
          } else if (supposedParentClass != classParent) {
            return false;
          }
          ++classSizeVal[classVal];
          ++classSizeParent[classParent];
          if (classSizeVal[classVal] != classSizeParent[classParent]) {
            maxParentClassDiffering = Math.max(maxParentClassDiffering, classParent);
          }
        }
        if (maxParentClassDiffering == -1) {
          return false;
        }
        int splitParentClassMustBeProducedBefore = -1;
        for (int i = 0; i < n; ++i) {
          if (mustBeProducedBefore.getInt(i) != parent.getInt(i)) {
            splitParentClassMustBeProducedBefore = parent.getInt(i);
            break;
          }
        }
        if (splitParentClassMustBeProducedBefore > maxParentClassDiffering) {
          return false;
        } else if (splitParentClassMustBeProducedBefore < maxParentClassDiffering) {
          return true;
        }

        int adoptedClassVal = -1;
        for (int i = maxClassVal; i > 0; --i) {
          if (valToParent[i] == maxParentClassDiffering) {
            adoptedClassVal = i;
            break;
          }
        }
        int adoptedClassParent = n + 1;

        int maxClassParent = -1;
        for (int i = 0; i < n; ++i) {
          int classVal = val.getInt(i);
          int classParent = parent.getInt(i);
          maxClassParent = Math.max(classParent, maxClassParent);
          int leastImmediatePredecessorValue;
          if (adoptedClassVal != classVal) {
            leastImmediatePredecessorValue = classParent < adoptedClassParent ? classParent
                : classParent + 1;
          } else {
            if (adoptedClassParent > n) {
              adoptedClassParent = maxClassParent + 1;
            }
            leastImmediatePredecessorValue = adoptedClassParent;
          }
          int mustBeProducedBeforeValue = mustBeProducedBefore.getInt(i);
          if (leastImmediatePredecessorValue > mustBeProducedBeforeValue) {
            return false;
          } else if (leastImmediatePredecessorValue < mustBeProducedBeforeValue) {
            return true;
          }
        }
        return true;
      }
    };
  }

  /**
   * Returns an enumerator of upper covers of {@code ranking} on the lattice of
   * rankings.
   * 
   * @param ranking the ranking.
   * @return an enumerator of upper covers of {@code ranking}.
   */
  public static CoverEnumerator<Ranking, Ranking> upperCoversRankings(Ranking ranking) {
    return new CoverEnumerator<Ranking, Ranking>() {

      private ConstMapping.OfInt equivalence;
      private ConstMapping.OfInt classStartOffsets;
      private ConstMapping.OfInt vertsOrderedByClass;
      private int maxclass;
      private int mergeClass1 = 0;
      private int mergeClass2 = 0;
      private Ranking nextRanking = null;
      private boolean[] linkClosesTransitively;
      {
        equivalence = Converters.strongComponentsAsEquivalence().apply(ranking.asBinaryRelation());
        maxclass = -1;
        for (int k : equivalence) {
          maxclass = Math.max(maxclass, k);
        }
        Mapping.OfInt offsets = Mappings.wrapModifiableInt(new int[maxclass + 1]);
        classStartOffsets = offsets;
        vertsOrderedByClass = PrimitiveCollections.countingSort(equivalence, 0, offsets, 0,
            equivalence.size());
        // we need to maintain information on whether a new ordering between two indifference
        // classes results in additional transitive links
        // this could be done on the fly during enumeration
        // however, doing it on the fly during the cover test would seriously deter
        // performance
        linkClosesTransitively = new boolean[maxclass * (maxclass + 1)];
        int pos = 0;
        for (int firstClass = 0; firstClass <= maxclass; ++firstClass) {
          for (int secondClass = 0; secondClass <= maxclass; ++secondClass) {
            if (firstClass == secondClass) {
              continue;
            }
            int rep1 = vertsOrderedByClass.getInt(classStartOffsets.getInt(firstClass));
            int rep2 = vertsOrderedByClass.getInt(classStartOffsets.getInt(secondClass));
            if (!ranking.lessEqualThan(rep1, rep2)) {
              for (int closeClass = 0; closeClass <= maxclass; ++closeClass) {
                if (firstClass == closeClass || secondClass == closeClass) {
                  continue;
                }
                int closeRep = vertsOrderedByClass.getInt(classStartOffsets.getInt(closeClass));
                if ((ranking.lessEqualThan(closeRep, rep1)
                    && !ranking.lessEqualThan(closeRep, rep2))
                    || (ranking.lessEqualThan(rep2, closeRep)
                        && !ranking.lessEqualThan(rep1, closeRep))) {
                  linkClosesTransitively[pos] = true;
                  break;
                }
              }
            }
            ++pos;
          }
        }
      }

      @Override
      public boolean hasNext() {
        if (nextRanking == null) {
          generateNext();
        }
        return nextRanking != null;
      }

      @Override
      public Ranking next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Ranking result = nextRanking;
        nextRanking = null;
        return result;
      }

      private void generateNext() {
        if (mergeClass1 > maxclass) {
          return;
        }
        loop: for (;;) {
          ++mergeClass2;
          if (mergeClass1 == mergeClass2) {
            ++mergeClass2;
          }
          if (mergeClass2 > maxclass) {
            mergeClass2 = 0;
            ++mergeClass1;
          }
          if (mergeClass1 > maxclass) {
            return;
          }
          int rep1 = vertsOrderedByClass.getInt(classStartOffsets.getInt(mergeClass1));
          int rep2 = vertsOrderedByClass.getInt(classStartOffsets.getInt(mergeClass2));
          if (ranking.lessEqualThan(rep1, rep2)) {
            continue;
          }
          // add mergeClass1 <= mergeClass2 ordering to ranking
          int n = ranking.domainSize();
          // we have to make sure that we do not have to close anything transitively
          // if we have to close transitively, this is not a direct predecessor!
          if (wouldCloseTransitively(mergeClass1, mergeClass2)) {
            continue loop;
          }
          RelationBuilder<? extends Ranking> builder = RelationBuilders
              .denseUnsafeRankingBuilder(n);
          for (int i = 0; i < n; ++i) {
            for (int j : ranking.iterateGreaterEqualThan(i)) {
              builder.add(i, j);
            }
          }
          for (int ipos = classStartOffsets.getInt(mergeClass1), iend = mergeClass1 == maxclass ? n
              : classStartOffsets.getInt(mergeClass1 + 1); ipos < iend; ++ipos) {
            int i = vertsOrderedByClass.getInt(ipos);
            for (int jpos = classStartOffsets.getInt(mergeClass2),
                jend = mergeClass2 == maxclass ? n
                    : classStartOffsets.getInt(mergeClass2 + 1); jpos < jend; ++jpos) {
              int j = vertsOrderedByClass.getInt(jpos);
              builder.add(i, j);
            }
          }
          nextRanking = builder.build();
          return;
        }
      }

      private boolean wouldCloseTransitively(int firstClass, int secondClass) {
        return linkClosesTransitively[firstClass * maxclass + secondClass
            + (firstClass <= secondClass ? -1 : 0)];
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(Ranking val,
          Ranking mustBeProducedBefore) {
        final int n = ranking.domainSize();
        for (int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j) {
            if (!val.lessEqualThan(i, j) && ranking.lessEqualThan(i, j)) {
              return false;
            }
          }
        }
        for (int firstClass = 0; firstClass <= maxclass; ++firstClass) {
          for (int secondClass = 0; secondClass <= maxclass; ++secondClass) {
            if (firstClass == secondClass) {
              continue;
            }

            int rep1 = vertsOrderedByClass.getInt(classStartOffsets.getInt(firstClass));
            int rep2 = vertsOrderedByClass.getInt(classStartOffsets.getInt(secondClass));
            boolean valLessEqualThan = val.lessEqualThan(rep1, rep2);
            boolean producedBeforeLessEqualThan = mustBeProducedBefore.lessEqualThan(rep1, rep2);

            if (valLessEqualThan != producedBeforeLessEqualThan
                && !wouldCloseTransitively(firstClass, secondClass)) {
              return valLessEqualThan;
            }
          }
        }
        return true;
      }

    };
  }

  /**
   * Returns an enumerator of lower covers of {@code ranking} on the lattice of
   * rankings.
   * 
   * @param ranking the ranking.
   * @return an enumerator of lower covers of {@code ranking}.
   */
  public static CoverEnumerator<Ranking, Ranking> lowerCoversRankings(Ranking ranking) {
    return new CoverEnumerator<Ranking, Ranking>() {

      private ConstMapping.OfInt equivalence;
      private ConstMapping.OfInt classStartOffsets;
      private ConstMapping.OfInt vertsOrderedByClass;
      private int[] classCounts = null;
      private int maxclass;
      private PrimitiveList.OfLong nontransitiveLinks;
      private int splitClass;
      private final RoleConverter<BinaryRelation, //
          ConstMapping.OfInt> indifferenceClassesEquivalenceConverter;
      {
        indifferenceClassesEquivalenceConverter = Converters.strongComponentsAsEquivalence();
        equivalence = indifferenceClassesEquivalenceConverter.apply(ranking.asBinaryRelation());
        maxclass = -1;
        int n = equivalence.size();
        for (int k : equivalence) {
          maxclass = Math.max(maxclass, k);
        }
        int[] classReps = new int[maxclass + 1];
        classCounts = new int[maxclass + 1];
        for (int i = 1; i < n; ++i) {
          int k = equivalence.getInt(i);
          classReps[k] = i;
        }
        Mapping.OfInt offsets = Mappings.wrapModifiableInt(new int[maxclass + 1]);
        classStartOffsets = offsets;
        vertsOrderedByClass = PrimitiveCollections.countingSort(equivalence, 0, offsets, 0,
            equivalence.size());
        for (int k = 0; k < maxclass; ++k) {
          classCounts[k] = offsets.getInt(k + 1) - offsets.getInt(k);
        }
        classCounts[maxclass] = n - offsets.getInt(maxclass);

        // determine non-transitive link
        // since a ranking is transitive, a link a->b
        // is transitive if there is a c such that a->c->b
        nontransitiveLinks = Mappings.newLongList();
        for (int i : classReps) {
          loop: for (int j : classReps) {
            if (i != j && ranking.lessEqualThan(i, j)) {
              for (int k : classReps) {
                if (i != k && j != k && ranking.lessEqualThan(i, k)
                    && ranking.lessEqualThan(k, j)) {
                  continue loop;
                }
              }
              nontransitiveLinks.addLong(IntPair.tuple(i, j));
            }
          }
        }
        splitClass = maxclass;
      }

      private boolean[] splitAssignment = null;
      private boolean splitDirection = false;
      private int nontransitivePos = 0;
      private Ranking nextRanking = null;

      @Override
      public boolean hasNext() {
        if (nextRanking == null) {
          generateNext();
        }
        return nextRanking != null;
      }

      @Override
      public Ranking next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Ranking result = nextRanking;
        nextRanking = null;
        return result;
      }

      private void generateNext() {
        final int n = ranking.domainSize();
        // first generate by removing one non-transitive ordering
        // between equivalence classes one at a time
        if (nontransitivePos < nontransitiveLinks.size()) {
          long link = nontransitiveLinks.getLong(nontransitivePos);
          int leftClass = equivalence.getInt(IntPair.first(link));
          int rightClass = equivalence.getInt(IntPair.second(link));
          RelationBuilder<? extends Ranking> builder = RelationBuilders
              .denseUnsafeRankingBuilder(ranking.domainSize());
          // remove ordering leftClass <= rightClass
          for (int i = 0; i < n; ++i) {
            if (equivalence.getInt(i) != leftClass) {
              for (int j : ranking.iterateGreaterEqualThan(i)) {
                builder.add(i, j);
              }
            } else {
              for (int j : ranking.iterateGreaterEqualThan(i)) {
                if (equivalence.getInt(j) != rightClass) {
                  builder.add(i, j);
                }
              }
            }
          }
          ++nontransitivePos;
          nextRanking = builder.build();
          return;
        }

        // next, split each of the classes as often as possible
        if (splitClass < 0) {
          return;
        }
        while (classCounts[splitClass] == 1) {
          --splitClass;
          if (splitClass < 0) {
            return;
          }
        }
        if (splitAssignment == null) {
          splitAssignment = new boolean[classCounts[splitClass] - 1];
          splitAssignment[classCounts[splitClass] - 2] = true;
        }

        RelationBuilder<? extends Ranking> builder = RelationBuilders
            .denseUnsafeRankingBuilder(ranking.domainSize());
        // first construct without ordering within splitClass
        for (int i = 0; i < ranking.domainSize(); ++i) {
          if (equivalence.getInt(i) != splitClass) {
            for (int j : ranking.iterateGreaterEqualThan(i)) {
              builder.add(i, j);
            }
          } else {
            for (int j : ranking.iterateGreaterEqualThan(i)) {
              if (equivalence.getInt(j) != splitClass) {
                builder.add(i, j);
              }
            }
          }
        }
        // then construct within splitClass
        // first, handle first vertex (with implicit splitAssignment value false)
        {
          int i = vertsOrderedByClass.getInt(classStartOffsets.getInt(splitClass));
          for (int jpos = classStartOffsets.getInt(splitClass) + 1,
              end = splitClass == maxclass ? n : classStartOffsets.getInt(splitClass + 1),
              splitPos = 0; jpos < end; ++jpos, ++splitPos) {
            int j = vertsOrderedByClass.getInt(jpos);
            if (!splitAssignment[splitPos]) {
              builder.add(i, j);
              builder.add(j, i);
            } else if (splitDirection) {
              builder.add(j, i);
            } else {
              builder.add(i, j);
            }
          }
        }
        // then handle the others
        for (int ipos = classStartOffsets.getInt(splitClass) + 1,
            end = splitClass == maxclass ? n : classStartOffsets.getInt(splitClass + 1),
            iSplitPos = 0; ipos < end; ++ipos, ++iSplitPos) {
          int i = vertsOrderedByClass.getInt(ipos);
          for (int jpos = ipos + 1, jSplitPos = iSplitPos + 1; jpos < end; ++jpos, ++jSplitPos) {
            int j = vertsOrderedByClass.getInt(jpos);
            if (splitAssignment[iSplitPos] == splitAssignment[jSplitPos]) {
              builder.add(i, j);
              builder.add(j, i);
              // splitAssignment[i] == false -> handling like first element
              // which means: if splitDirection is true, it has to be greater
              // than j in the result ranking, else it has to be less
              // it is the other way around when splitAssignment[i] == true
            } else if (splitAssignment[iSplitPos] != splitDirection) {
              builder.add(j, i);
            } else {
              builder.add(i, j);
            }
          }
        }
        nextRanking = builder.build();

        splitDirection = !splitDirection;
        if (!splitDirection) {
          for (int i = splitAssignment.length - 1; i >= 0; --i) {
            splitAssignment[i] = !splitAssignment[i];
            if (splitAssignment[i]) {
              return;
            }
          }
          splitAssignment = null;
          --splitClass;
        }
      }

      @Override
      public boolean isThereAncestorWhichIsCoverProducedBefore(Ranking val,
          Ranking mustBeProducedBefore) {
        final int n = ranking.domainSize();
        for (int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j) {
            if (val.lessEqualThan(i, j) && !ranking.lessEqualThan(i, j)) {
              return false;
            }
          }
        }
        for (long link : nontransitiveLinks) {
          int leftClass = equivalence.getInt(IntPair.first(link));
          int rightClass = equivalence.getInt(IntPair.second(link));
          boolean valSomeExisting = false;
          int istart = classStartOffsets.getInt(leftClass);
          int jstart = classStartOffsets.getInt(rightClass);
          loop: for (int ipos = istart, iend = leftClass == maxclass ? n
              : classStartOffsets.getInt(leftClass + 1); ipos < iend; ++ipos) {
            int i = vertsOrderedByClass.getInt(ipos);
            for (int jpos = jstart, jend = rightClass == maxclass ? n
                : classStartOffsets.getInt(rightClass + 1); jpos < jend; ++jpos) {
              int j = vertsOrderedByClass.getInt(jpos);
              boolean ijLinkExists = val.lessEqualThan(i, j);
              valSomeExisting |= ijLinkExists;
              if (ijLinkExists) {
                valSomeExisting = true;
                break loop;
              }
            }
          }
          boolean producedBeforeLinkExists = mustBeProducedBefore.lessEqualThan(
              vertsOrderedByClass.getInt(istart), vertsOrderedByClass.getInt(jstart));
          if (producedBeforeLinkExists && !valSomeExisting) {
            return true;
          } else if (!producedBeforeLinkExists) {
            return !valSomeExisting;
          }
        }

        ConstMapping.OfInt valEquivalence = indifferenceClassesEquivalenceConverter
            .apply(val.asBinaryRelation());

        final int[] classSizeParent = new int[n];
        final int[] classSizeVal = new int[n];
        final int[] valToParent = new int[n];
        Arrays.fill(valToParent, -1);
        int maxClassVal = 0;
        int maxParentClassDiffering = -1;
        for (int i = 0; i < n; ++i) {
          int classVal = valEquivalence.getInt(i);
          maxClassVal = Math.max(maxClassVal, classVal);
          int classParent = equivalence.getInt(i);
          int supposedParentClass = valToParent[classVal];
          if (supposedParentClass == -1) {
            valToParent[classVal] = classParent;
          } else if (supposedParentClass != classParent) {
            return false;
          }
          ++classSizeVal[classVal];
          ++classSizeParent[classParent];
          if (classSizeVal[classVal] != classSizeParent[classParent]) {
            maxParentClassDiffering = Math.max(maxParentClassDiffering, classParent);
          }
        }
        if (maxParentClassDiffering == -1) {
          return false;
        }

        ConstMapping.OfInt mustBeProducedBeforeEquivalence = indifferenceClassesEquivalenceConverter
            .apply(mustBeProducedBefore.asBinaryRelation());
        int splitParentClassMustBeProducedBefore = -1;
        for (int i = 0; i < n; ++i) {
          if (mustBeProducedBeforeEquivalence.getInt(i) != equivalence.getInt(i)) {
            splitParentClassMustBeProducedBefore = equivalence.getInt(i);
            break;
          }
        }
        if (splitParentClassMustBeProducedBefore > maxParentClassDiffering) {
          return false;
        } else if (splitParentClassMustBeProducedBefore < maxParentClassDiffering) {
          return true;
        }

        int adoptedClassVal = -1;
        for (int i = maxClassVal; i > 0; --i) {
          if (valToParent[i] == maxParentClassDiffering) {
            adoptedClassVal = i;
            break;
          }
        }
        int adoptedClassParent = n + 1;

        int maxClassParent = -1;
        for (int i = 0; i < n; ++i) {
          int classVal = valEquivalence.getInt(i);
          int classParent = equivalence.getInt(i);
          maxClassParent = Math.max(classParent, maxClassParent);
          int leastImmediatePredecessorValue;
          if (adoptedClassVal != classVal) {
            leastImmediatePredecessorValue = classParent < adoptedClassParent ? classParent
                : classParent + 1;
          } else {
            if (adoptedClassParent > n) {
              adoptedClassParent = maxClassParent + 1;
            }
            leastImmediatePredecessorValue = adoptedClassParent;
          }
          int mustBeProducedBeforeValue = mustBeProducedBeforeEquivalence.getInt(i);
          if (leastImmediatePredecessorValue > mustBeProducedBeforeValue) {
            return false;
          } else if (leastImmediatePredecessorValue < mustBeProducedBeforeValue) {
            return true;
          }
        }

        // at this point, we know: same indifference class structure in mustBeProducedBefore and the
        // least cover succeeding val and
        // what remains to test is the ordering between the one added class compared to the parent
        // ranking
        int parentStart = classStartOffsets.getInt(maxParentClassDiffering);
        int parentEnd = maxParentClassDiffering == maxclass ? n
            : classStartOffsets.getInt(maxParentClassDiffering + 1);
        boolean valAdoptedClassLessThan = false;
        boolean valAdoptedClassGreaterThan = false;
        boolean beforeAdoptedClassLessThan = false;
        for (int kpos = parentStart; kpos < parentEnd; ++kpos) {
          int k = vertsOrderedByClass.getInt(kpos);
          boolean kIsAdoptedClass = valEquivalence.getInt(k) == adoptedClassVal;
          for (int lpos = kpos + 1; lpos < parentEnd; ++lpos) {
            int l = vertsOrderedByClass.getInt(lpos);
            boolean lIsAdoptedClass = valEquivalence.getInt(l) == adoptedClassVal;
            if (kIsAdoptedClass == lIsAdoptedClass) {
              continue;
            }

            beforeAdoptedClassLessThan = mustBeProducedBefore.lessEqualThan(k,
                l) == kIsAdoptedClass;
            boolean kLessThanl = val.lessEqualThan(k, l);
            if (kLessThanl) {
              if (kIsAdoptedClass) {
                valAdoptedClassLessThan = true;
              } else {
                valAdoptedClassGreaterThan = true;
              }
            }

            boolean lLessThank = val.lessEqualThan(l, k);
            if (lLessThank) {
              if (lIsAdoptedClass) {
                valAdoptedClassLessThan = true;
              } else {
                valAdoptedClassGreaterThan = true;
              }
            }
            if (valAdoptedClassLessThan || valAdoptedClassGreaterThan) {
              break;
            }
          }
        }
        if (valAdoptedClassLessThan && !beforeAdoptedClassLessThan) {
          return false;
        }
        return true;
      }
    };
  }
}
