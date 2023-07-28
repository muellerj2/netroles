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
package ch.ethz.sn.visone3.roles.test.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.impl.structures.BiIntPredicate;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.impl.structures.BinaryRelationOrRanking;
import ch.ethz.sn.visone3.roles.impl.structures.CommonBinRelationRankingUtils;
import ch.ethz.sn.visone3.roles.impl.structures.LazyCachedBinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.impl.structures.LazyUncachedBinaryRelationMatrixImpl;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BinaryRelationsTest {

  public enum OrderKind {
    EQUAL, GREATEREQUAL
  }

  private static void assertBinaryRelationNoInverse(RelationBase binrel, BiIntPredicate generator,
      ObjIntConsumer<OrderKind> checkCounter) {

    Ranking ranking = null;
    if (binrel instanceof Ranking) {
      ranking = (Ranking) binrel;
    }

    if (ranking != null) {
      assertSame(binrel, ranking.asBinaryRelation());
    }

    final int size = binrel.domainSize();

    final int[] inCounters = new int[size];
    final int[] outCounters = new int[size];
    final int[] bothCounters = new int[size];
    int totalCount = 0;

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.testInt(i, j),
            binrel.contains(i, j), String.format("contains(%s, %s)", i, j));
        if (ranking != null) {
          assertEquals(generator.testInt(i, j), ranking.lessEqualThan(i, j),
              String.format("lessEqualThan(%s, %s)", i, j));
        }
        if (generator.testInt(i, j)) {
          ++inCounters[j];
          ++outCounters[i];
          if (generator.testInt(j, i)) {
            ++bothCounters[i];
          }
          ++totalCount;
        }
      }
    }

    checkCounter.accept(OrderKind.EQUAL, (1 + (ranking != null ? 1 : 0)) * size * size);

    for (int i = size - 1; i >= 0; --i) {
      assertEquals(outCounters[i], binrel.countInRelationFrom(i),
          String.format("countInRelationFrom(%s)", i));
      assertEquals(inCounters[i], binrel.countInRelationTo(i),
          String.format("countInRelationTo(%s)", i));
      assertEquals(bothCounters[i], binrel.countSymmetricRelationPairs(i),
          String.format("countSymmetricRelationPairs(%s)", i));
      if (ranking != null) {
        assertEquals(outCounters[i], ranking.countGreaterEqualThan(i),
            String.format("countGreaterEqualThan(%s)", i));
        assertEquals(inCounters[i], ranking.countLessEqualThan(i),
            String.format("countLessEqualThan(%s)", i));
        assertEquals(bothCounters[i], ranking.countEqual(i), String.format("countEqual(%s)", i));
      }
    }
    assertEquals(totalCount, binrel.countRelationPairs());

    checkCounter.accept(OrderKind.GREATEREQUAL, (5 + (ranking != null ? 4 : 0)) * size * size);
    for (int i = 0; i < size; ++i) {
      PrimitiveIterator.OfInt iterator = binrel.iterateInRelationFrom(i).iterator();
      int count = 0;
      while (iterator.hasNext()) {
        ++count;
        int j = iterator.nextInt();
        assertTrue(binrel.contains(i, j), String.format("iterateInRelationFrom(%s):%s", i, j));
      }
      assertEquals(outCounters[i], count);
      assertThrows(NoSuchElementException.class, () -> iterator.next());
      if (ranking != null) {
        PrimitiveIterator.OfInt iterator2 = ranking.iterateGreaterEqualThan(i).iterator();
        count = 0;
        while (iterator2.hasNext()) {
          ++count;
          int j = iterator2.nextInt();
          assertTrue(binrel.contains(i, j), String.format("iterateGreaterEqualThan(%s):%s", i, j));
        }
        assertEquals(outCounters[i], count);
        assertThrows(NoSuchElementException.class, () -> iterator2.next());
      }
      PrimitiveIterator.OfInt iterator3 = binrel.iterateInRelationTo(i).iterator();
      count = 0;
      while (iterator3.hasNext()) {
        ++count;
        int j = iterator3.nextInt();
        assertTrue(binrel.contains(j, i), String.format("iterateLessEqualThan(%s):%s", i, j));
      }
      assertEquals(inCounters[i], count);
      assertThrows(NoSuchElementException.class, () -> iterator3.next());
      if (ranking != null) {
        PrimitiveIterator.OfInt iterator4 = ranking.iterateLessEqualThan(i).iterator();
        count = 0;
        while (iterator4.hasNext()) {
          ++count;
          int j = iterator4.nextInt();
          assertTrue(binrel.contains(j, i), String.format("iterateLessEqualThan(%s):%s", i, j));
        }
        assertEquals(inCounters[i], count);
        assertThrows(NoSuchElementException.class, () -> iterator4.next());
      }
    }

    assertEquals(totalCount, binrel.countRelationPairs());
    assertEquals(binrel, binrel);
  }

  public static void assertBinaryRelation(RelationBase binrel, BiIntPredicate generator,
      ObjIntConsumer<OrderKind> checkCounter) {

    assertBinaryRelationNoInverse(binrel, generator, checkCounter);
    RelationBase invertedRel = binrel.invert();
    BiIntPredicate invertedGenerator = (i, j) -> generator.testInt(j, i);
    assertBinaryRelationNoInverse(invertedRel, invertedGenerator, (kind, count) -> {
    });
    assertEquals(binrel.isLazilyEvaluated(), invertedRel.isLazilyEvaluated());
    assertEquals(binrel.isRandomAccess(), invertedRel.isRandomAccess());

    LazyUncachedBinaryRelationMatrixImpl invertedUncached = new LazyUncachedBinaryRelationMatrixImpl(
        binrel.domainSize(), invertedGenerator);
    assertEquals(invertedUncached, invertedRel);
    assertEquals(invertedRel, invertedUncached);
    assertEquals(invertedUncached.hashCode(), invertedRel.hashCode());
    assertEquals(invertedUncached.hashCode(), invertedRel.hashCode());
    assertEquals(invertedUncached.toString(), invertedRel.toString());
    assertEquals(invertedUncached.toString(), invertedRel.toString());
    assertFalse(invertedRel.equals(new Object()));
    assertFalse(invertedRel.equals((Object) null));
    assertFalse(invertedRel.equals((RelationBase) null));
  }

  @Test
  public void testLazyUncachedBinaryRelation() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 3 == 1 && j > 5 && j % 2 == 0);
    final int[] invocationCounter = new int[1];
    final BiIntPredicate countingGenerator = (i, j) -> {
      ++invocationCounter[0];
      return generator.testInt(i, j);
    };
    final BinaryRelationOrRanking binrel = new LazyUncachedBinaryRelationMatrixImpl(size,
        countingGenerator);

    assertEquals(0, invocationCounter[0]);
    assertTrue(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
      if (kind == OrderKind.EQUAL) {
        assertEquals(minCount, invocationCounter[0]);
      } else if (kind == OrderKind.GREATEREQUAL) {
        assertTrue(minCount <= invocationCounter[0]);
      }
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);
    assertEquals(binrel2, binrel);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));
  }

  @Test
  public void testLazyCachedBinaryRelation() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 3 == 1 && j > 5 && j % 2 == 0);
    final int[] invocationCounter = new int[1];
    final BiIntPredicate countingGenerator = (i, j) -> {
      ++invocationCounter[0];
      return generator.testInt(i, j);
    };
    final BinaryRelationOrRanking binrel = new LazyCachedBinaryRelationMatrixImpl(size,
        countingGenerator);

    assertEquals(0, invocationCounter[0]);
    assertTrue(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
      if (kind == OrderKind.EQUAL) {
        assertEquals(Math.min(size * size, minCount), invocationCounter[0]);
      } else if (kind == OrderKind.GREATEREQUAL) {
        assertTrue(Math.min(size * size, minCount) <= invocationCounter[0]);
        assertTrue(invocationCounter[0] <= size * size);
      }
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final BinaryRelationOrRanking binrel3 = new LazyCachedBinaryRelationMatrixImpl(size, generator);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testEagerBinaryRelation() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 3 == 1 && j > 5 && j % 2 == 0);

    boolean[][] mat = new boolean[size][size];
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        mat[i][j] = generator.testInt(i, j);
      }
    }

    final BinaryRelationOrRanking binrel = new BinaryRelationMatrixImpl(mat);

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final BinaryRelationOrRanking binrel3 = new LazyCachedBinaryRelationMatrixImpl(size, generator);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testEagerBinaryRelation2() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 3 == 1 && j > 5 && j % 2 == 0);

    boolean[][] mat = new boolean[size][size];
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        mat[i][j] = generator.testInt(i, j);
      }
    }

    final BinaryRelationOrRanking binrel = CommonBinRelationRankingUtils.fromMatrix(mat);

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final BinaryRelationOrRanking binrel3 = new LazyCachedBinaryRelationMatrixImpl(size, generator);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  private void testRelationBuilderInternal(int size, Supplier<RelationBase> supplier,
      BiIntPredicate generator) {

    final RelationBase binrel = supplier.get();

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final BinaryRelationOrRanking binrel3 = new LazyCachedBinaryRelationMatrixImpl(size, generator);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testRelationBuilder() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 3 == 1 && j > 5 && j % 2 == 0);

    BiFunction<RelationBuilder<? extends RelationBase>, BiIntPredicate, RelationBase> generateFromRelationBuilder = (
        relationBuilder, thisGenerator) -> {
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (thisGenerator.testInt(i, j)) {
            relationBuilder.add(i, j);
          }
        }
      }
      return relationBuilder.build();
    };

    testRelationBuilderInternal(size,
        () -> generateFromRelationBuilder.apply(new BinaryRelationMatrixImpl.Builder(), generator),
        generator);
    testRelationBuilderInternal(size,
        () -> generateFromRelationBuilder.apply(new BinaryRelationMatrixImpl.Builder(size),
            generator),
        generator);
    testRelationBuilderInternal(size,
        () -> generateFromRelationBuilder
            .apply(RelationBuilders.denseRelationBuilder(size), generator),
        generator);
    testRelationBuilderInternal(size,
        () -> generateFromRelationBuilder.apply(RelationBuilders.denseUnsafeRankingBuilder(size),
            generator),
        generator);
    testRelationBuilderInternal(size,
        () -> generateFromRelationBuilder.apply(RelationBuilders.denseUnsafeRankingBuilder(size),
            (i, j) -> i != j && generator.testInt(i, j)),
        generator);
  }

  private void testIdentityInternal(IntFunction<RelationBase> producer) {

    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j;

    final RelationBase binrel = producer.apply(size);

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final RelationBase binrel3 = producer.apply(size);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testIdentity() {
    testIdentityInternal(CommonBinRelationRankingUtils::identity);
    testIdentityInternal(BinaryRelations::identity);
    testIdentityInternal(Rankings::identity);
  }

  private void testUniversalInternal(final int size, IntFunction<RelationBase> producer) {
    final BiIntPredicate generator = (i, j) -> true;
    final RelationBase binrel = producer.apply(size);

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> i == j), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> i <= j));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final RelationBase binrel3 = producer.apply(size);
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testUniversal() {
    final int size = 20;
    testUniversalInternal(size, CommonBinRelationRankingUtils::universal);
    testUniversalInternal(size, BinaryRelations::universal);
    testUniversalInternal(size, Rankings::universal);
    testUniversalInternal(size, x -> Converters.universalRelation(x).apply(new Object()));
    assertTrue(Converters.universalRelation(size).isConstant());
    assertTrue(Converters.universalRelation(size).isIsotone());
    assertTrue(Converters.universalRelation(size).isNondecreasing());
    assertFalse(Converters.universalRelation(size).isNonincreasing());
    testUniversalInternal(size, x -> Converters.allEqualRanking(x).apply(new Object()));
    assertTrue(Converters.allEqualRanking(size).isConstant());
    assertTrue(Converters.allEqualRanking(size).isIsotone());
    assertTrue(Converters.allEqualRanking(size).isNondecreasing());
    assertFalse(Converters.allEqualRanking(size).isNonincreasing());
  }

  private BinaryRelationOrRanking fakeNotRandomAccess(RelationBase inner) {
    return new BinaryRelationOrRanking() {

      @Override
      public OfInt iterateInRelationTo(int i) {
        return inner.iterateInRelationTo(i);
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return inner.iterateInRelationFrom(i);
      }

      @Override
      public int countInRelationTo(int i) {
        return inner.countInRelationTo(i);
      }

      @Override
      public int countInRelationFrom(int i) {
        return inner.countInRelationFrom(i);
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return inner.countSymmetricRelationPairs(i);
      }

      @Override
      public int countRelationPairs() {
        return inner.countRelationPairs();
      }

      @Override
      public int domainSize() {
        return inner.domainSize();
      }

      @Override
      public boolean contains(int i, int j) {
        return inner.contains(i, j);
      }

      @Override
      public boolean isRandomAccess() {
        return false;
      }

      @Override
      public boolean isLazilyEvaluated() {
        return inner.isLazilyEvaluated();
      }

      @Override
      public boolean equals(Object obj) {
        return inner.equals(obj);
      }

      @Override
      public int hashCode() {
        return inner.hashCode();
      }

      @Override
      public String toString() {
        return inner.toString();
      }
    };
  }

  private void testIntersectInternal(BinaryOperator<RelationBase> operator, int size,
      RelationBase first, RelationBase second, BiIntPredicate combinedGenerator, boolean lazy) {

    final RelationBase binrel = operator.apply(first, second);

    assertEquals(lazy, binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, combinedGenerator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(size,
        combinedGenerator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size / 2, combinedGenerator), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size / 2, combinedGenerator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> i == j), binrel);
    assertNotEquals(binrel, new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> i <= j));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final RelationBase binrel3 = operator.apply(first, second);

    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());

    assertBinaryRelation(fakeNotRandomAccess(first), first::contains,
        (OrderKind kind, int minCount) -> {
        });

    final RelationBase binrel4 = operator.apply(fakeNotRandomAccess(first),
        fakeNotRandomAccess(second));

    assertBinaryRelation(binrel4, combinedGenerator, (OrderKind kind, int minCount) -> {
    });

    final RelationBase binrel5 = operator.apply(first, fakeNotRandomAccess(second));

    assertBinaryRelation(binrel5, combinedGenerator, (OrderKind kind, int minCount) -> {
    });

    final RelationBase binrel6 = operator.apply(fakeNotRandomAccess(first), second);

    assertBinaryRelation(binrel6, combinedGenerator, (OrderKind kind, int minCount) -> {
    });

    assertEquals(binrel, binrel4);
    assertEquals(binrel.hashCode(), binrel4.hashCode());
    assertEquals(binrel.toString(), binrel4.toString());
    assertEquals(binrel, binrel5);
    assertEquals(binrel.hashCode(), binrel5.hashCode());
    assertEquals(binrel.toString(), binrel5.toString());
    assertEquals(binrel, binrel6);
    assertEquals(binrel.hashCode(), binrel6.hashCode());
    assertEquals(binrel.toString(), binrel6.toString());

  }

  @Test
  public void testIntersect() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == j || (i % 2 == 0 && j % 3 == 0);
    final BiIntPredicate generator2 = (i, j) -> i == j || (i % 5 == 0 && j % 4 == 0);
    final BiIntPredicate combinedGenerator = (i, j) -> generator.testInt(i, j)
        && generator2.testInt(i, j);

    final RelationBase first = new LazyUncachedBinaryRelationMatrixImpl(size, generator);
    final RelationBase second = new LazyUncachedBinaryRelationMatrixImpl(size, generator2);

    testIntersectInternal(CommonBinRelationRankingUtils::intersect, size, first, second,
        combinedGenerator, false);
    testIntersectInternal(CommonBinRelationRankingUtils::intersectLazily, size, first, second,
        combinedGenerator, true);
    testIntersectInternal((x, y) -> BinaryRelations.infimum((BinaryRelation) x, (BinaryRelation) y),
        size, first, second, combinedGenerator, false);
    testIntersectInternal(
        (x, y) -> BinaryRelations.lazyInfimum((BinaryRelation) x, (BinaryRelation) y), size, first,
        second, combinedGenerator, true);
    testIntersectInternal((x, y) -> Rankings.infimum((Ranking) x, (Ranking) y), size, first, second,
        combinedGenerator, false);
    testIntersectInternal((x, y) -> Rankings.lazyInfimum((Ranking) x, (Ranking) y), size, first,
        second, combinedGenerator, true);
    testIntersectInternal(
        (x, y) -> Reducers.BINARYRELATION.meet().combine((BinaryRelation) x, (BinaryRelation) y),
        size, first, second, combinedGenerator, false);
    testIntersectInternal(
        (x, y) -> Reducers.BINARYRELATION.meet().refine((BinaryRelation) x, (BinaryRelation) y),
        size, first, second, combinedGenerator, false);
  }

  private void testTransitiveClosureInternal(Supplier<RelationBase> applier,
      BiIntPredicate generator) {

    RelationBase closure = applier.get();

    assertBinaryRelation(closure, generator, (OrderKind kind, int minCount) -> {
    });
  }

  @Test
  public void testTransitiveClosure() {

    final int size = 20;

    Function<RelationBuilder<? extends RelationBase>, RelationBase> generateRelationFromBuilder = (
        relationBuilder) -> {
      relationBuilder.add(0, 1);
      relationBuilder.add(1, 5);
      relationBuilder.add(5, 4);
      relationBuilder.add(4, 5);
      relationBuilder.add(5, 0);
      relationBuilder.add(2, 8);
      relationBuilder.add(8, 6);
      relationBuilder.add(6, 9);
      relationBuilder.add(7, 9);
      return relationBuilder.build();
    };

    Supplier<RelationBuilder<? extends RelationBase>> reflexiveRelationBuilder = () -> {
      RelationBuilder<? extends RelationBase> builder = RelationBuilders.denseRelationBuilder(size);
      for (int i = 0; i < size; ++i) {
        builder.add(i, i);
      }
      return builder;
    };

    BinaryRelation binrel = (BinaryRelation) generateRelationFromBuilder
        .apply(reflexiveRelationBuilder.get());
    BinaryRelation nonreflexiveBinrel = (BinaryRelation) generateRelationFromBuilder
        .apply(RelationBuilders.denseRelationBuilder(size));

    BiIntPredicate generator = (i, j) -> (i == j)
        || (i <= 5 && i != 2 && i != 3 && j <= 5 && j != 2 && j != 3)
        || (i == 2 && (j == 9 || j == 6 || j == 8)) || (i == 8 && (j == 6 || j == 9))
        || (i == 6 && j == 9) || (i == 7 && j == 9);

    testTransitiveClosureInternal(
        () -> CommonBinRelationRankingUtils.closeTransitively(binrel, false), generator);
    testTransitiveClosureInternal(
        () -> CommonBinRelationRankingUtils.closeTransitively(nonreflexiveBinrel, true), generator);
    testTransitiveClosureInternal(() -> BinaryRelations.closeTransitively(binrel), generator);
    testTransitiveClosureInternal(
        () -> RoleOperators.BINARYRELATION.basic().closeTransitively().relative(binrel), generator);
    testTransitiveClosureInternal(() -> RoleOperators.BINARYRELATION.basic().closeTransitively()
        .relativeRefining(binrel, CommonBinRelationRankingUtils.universal(size)), generator);
    testTransitiveClosureInternal(() -> RoleOperators.BINARYRELATION.basic().closeTransitively()
        .relativeCoarsening(binrel, CommonBinRelationRankingUtils.identity(size)), generator);
    testTransitiveClosureInternal(() -> Converters.transitiveClosureAsRanking().convert(binrel),
        generator);
    testTransitiveClosureInternal(() -> Converters.transitiveClosureAsRanking().apply(binrel),
        generator);
    testTransitiveClosureInternal(() -> Converters.transitiveClosureAsRanking()
        .convertRefining(binrel, CommonBinRelationRankingUtils.universal(size)), generator);
    testTransitiveClosureInternal(() -> Converters.transitiveClosureAsRanking()
        .convertCoarsening(binrel, CommonBinRelationRankingUtils.identity(size)), generator);
    testTransitiveClosureInternal(
        () -> generateRelationFromBuilder.apply(RelationBuilders.denseSafeRankingBuilder(size)),
        generator);
    assertEquals(Rankings.identity(size), RelationBuilders.denseSafeRankingBuilder(size).build());
    assertEquals(Rankings.identity(size), Rankings
        .finestCoarseningRanking(new LazyUncachedBinaryRelationMatrixImpl(size, (i, j) -> false)));

    assertTrue(Converters.transitiveClosureAsRanking().isIsotone());
    assertFalse(Converters.transitiveClosureAsRanking().isConstant());
    assertTrue(Converters.transitiveClosureAsRanking().isNondecreasing());
    assertFalse(Converters.transitiveClosureAsRanking().isNonincreasing());

  }

  private void testFromEquivalenceInternal(Supplier<RelationBase> producer,
      BiIntPredicate generator) {

    RelationBase binrel = producer.get();

    assertFalse(binrel.isLazilyEvaluated());
    assertTrue(binrel.isRandomAccess());

    assertBinaryRelation(binrel, generator, (OrderKind kind, int minCount) -> {
    });

    final BinaryRelationOrRanking binrel2 = new LazyUncachedBinaryRelationMatrixImpl(
        binrel.domainSize(), generator);

    assertEquals(binrel2, binrel);
    assertEquals(binrel, binrel2);
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.hashCode(), binrel.hashCode());
    assertEquals(binrel2.toString(), binrel.toString());
    assertEquals(binrel2.toString(), binrel.toString());
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(binrel.domainSize() / 2, generator),
        binrel);
    assertNotEquals(binrel,
        new LazyUncachedBinaryRelationMatrixImpl(binrel.domainSize() / 2, generator));
    assertNotEquals(new LazyUncachedBinaryRelationMatrixImpl(binrel.domainSize(), (i, j) -> true),
        binrel);
    assertNotEquals(binrel,
        new LazyUncachedBinaryRelationMatrixImpl(binrel.domainSize(), (i, j) -> true));
    assertFalse(binrel.equals(new Object()));
    assertFalse(binrel.equals((Object) null));
    assertFalse(binrel.equals((RelationBase) null));

    final RelationBase binrel3 = producer.get();
    assertEquals(binrel2.countRelationPairs(), binrel3.countRelationPairs());
  }

  @Test
  public void testRandomEquivalenceArray() {

    final int size = 40;

    // generate random equivalence
    Random rand = new Random();
    int[] array = new int[size];
    do {
      for (int i = 0; i < size; ++i) {
        array[i] = rand.nextInt(size / 2);
      }

      array = Equivalences.normalizePartition(array);
    } while (Arrays.equals(array, new int[size]));

    int[] partition = array;
    final BiIntPredicate generator = (i, j) -> partition[i] == partition[j];
    ConstMapping.OfInt partitionMapping = Mappings.wrapUnmodifiableInt(partition);

    testFromEquivalenceInternal(() -> CommonBinRelationRankingUtils.fromEquivalence(partition),
        generator);
    testFromEquivalenceInternal(
        () -> CommonBinRelationRankingUtils.fromEquivalence(partitionMapping), generator);
    testFromEquivalenceInternal(() -> BinaryRelations.fromEquivalence(partitionMapping), generator);
    testFromEquivalenceInternal(() -> Rankings.fromEquivalence(partitionMapping), generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRelation().convert(partitionMapping),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRelation().apply(partitionMapping),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRelation()
        .convertRefining(partitionMapping, CommonBinRelationRankingUtils.universal(size)),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRelation()
        .convertCoarsening(partitionMapping, CommonBinRelationRankingUtils.identity(size)),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRanking().convert(partitionMapping),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRanking().apply(partitionMapping),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRanking()
        .convertRefining(partitionMapping, CommonBinRelationRankingUtils.universal(size)),
        generator);
    testFromEquivalenceInternal(() -> Converters.equivalenceAsRanking()
        .convertCoarsening(partitionMapping, CommonBinRelationRankingUtils.identity(size)),
        generator);

    int firstOne = IntStream.range(0, size).filter(x -> partition[x] == 1).findFirst().getAsInt();
    BiIntPredicate otherPredicate = (i, j) -> i == j
        || ((i == 0 || j == 0) && (i == firstOne || j == firstOne));
    BiIntPredicate supremumRankingGenerator = (i, j) -> partition[i] == partition[j]
        || ((partition[i] == 0 || partition[i] == 1) && (partition[j] == 0 || partition[j] == 1));
    assertEquals(
        new LazyUncachedBinaryRelationMatrixImpl(size,
            (i, j) -> generator.testInt(i, j) || otherPredicate.testInt(i, j)),
        Converters.equivalenceAsRelation().convertCoarsening(partitionMapping,
            new LazyUncachedBinaryRelationMatrixImpl(size, otherPredicate)));
    assertEquals(new LazyUncachedBinaryRelationMatrixImpl(size, supremumRankingGenerator),
        Converters.equivalenceAsRanking().convertCoarsening(partitionMapping,
            new LazyUncachedBinaryRelationMatrixImpl(size, otherPredicate)));

    boolean[] bipartition = new boolean[size];
    for (int i = 0; i < 40; ++i) {
      bipartition[i] = rand.nextBoolean();
    }

    assertEquals(
        new LazyUncachedBinaryRelationMatrixImpl(size,
            (i, j) -> generator.testInt(i, j) && bipartition[i] == bipartition[j]),
        Converters.equivalenceAsRelation().convertRefining(partitionMapping,
            new LazyUncachedBinaryRelationMatrixImpl(size,
                (i, j) -> bipartition[i] == bipartition[j])));
    assertEquals(
        new LazyUncachedBinaryRelationMatrixImpl(size,
            (i, j) -> generator.testInt(i, j) && bipartition[i] == bipartition[j]),
        Converters.equivalenceAsRanking().convertRefining(partitionMapping,
            new LazyUncachedBinaryRelationMatrixImpl(size,
                (i, j) -> bipartition[i] == bipartition[j])));

    assertTrue(Converters.equivalenceAsRelation().isIsotone());
    assertFalse(Converters.equivalenceAsRelation().isConstant());
    assertTrue(Converters.equivalenceAsRelation().isNondecreasing());
    assertTrue(Converters.equivalenceAsRelation().isNonincreasing());
    assertTrue(Converters.equivalenceAsRanking().isIsotone());
    assertFalse(Converters.equivalenceAsRanking().isConstant());
    assertTrue(Converters.equivalenceAsRanking().isNondecreasing());
    assertTrue(Converters.equivalenceAsRanking().isNonincreasing());
  }

  @Test
  public void testRelationToRanking() {
    final int size = 20;
    final BiIntPredicate generator = (i, j) -> i == 0 ? j == 0 : j % i == 0;
    final Ranking ranking = new LazyUncachedBinaryRelationMatrixImpl(size, generator);

    assertSame(ranking, Converters.rankingAsRelation().convert(ranking));
    assertSame(ranking, Converters.rankingAsRelation().apply(ranking));
    assertEquals(ranking,
        Converters.rankingAsRelation().convertRefining(ranking, BinaryRelations.universal(size)));
    assertEquals(BinaryRelations.identity(size),
        Converters.rankingAsRelation().convertRefining(ranking, BinaryRelations.identity(size)));
    assertEquals(ranking,
        Converters.rankingAsRelation().convertCoarsening(ranking, BinaryRelations.identity(size)));
    assertEquals(BinaryRelations.universal(size),
        Converters.rankingAsRelation().convertCoarsening(ranking, BinaryRelations.universal(size)));

    assertTrue(Converters.rankingAsRelation().isIsotone());
    assertFalse(Converters.rankingAsRelation().isConstant());
    assertTrue(Converters.rankingAsRelation().isNondecreasing());
    assertTrue(Converters.rankingAsRelation().isNonincreasing());

  }

  @Test
  public void testSupremumBinaryRelation() {
    final Random rand = new Random();
    final int size = 20;

    RelationBuilder<? extends BinaryRelation> builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation first = builder.build();
    builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation second = builder.build();

    builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation third = builder.build();

    BinaryRelation sup = BinaryRelations.supremum(first, second);
    assertBinaryRelation(sup, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    assertFalse(sup.isLazilyEvaluated());

    BinaryRelation sup2 = BinaryRelations.supremum(fakeNotRandomAccess(first), second);
    assertBinaryRelation(sup2, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    assertFalse(sup2.isLazilyEvaluated());

    BinaryRelation sup3 = BinaryRelations.supremum(fakeNotRandomAccess(first),
        fakeNotRandomAccess(second));
    assertBinaryRelation(sup3, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    assertFalse(sup3.isLazilyEvaluated());

    BinaryRelation sup4 = BinaryRelations.supremum(first, fakeNotRandomAccess(second));
    assertBinaryRelation(sup4, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    assertFalse(sup4.isLazilyEvaluated());

    BinaryRelation lazysup = BinaryRelations.lazySupremum(first, second);
    assertBinaryRelation(lazysup, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });

    BinaryRelation lazysup2 = BinaryRelations.lazySupremum(fakeNotRandomAccess(first), second);
    assertBinaryRelation(lazysup2, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });

    BinaryRelation lazysup3 = BinaryRelations.lazySupremum(fakeNotRandomAccess(first),
        fakeNotRandomAccess(second));
    assertBinaryRelation(lazysup3, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });

    BinaryRelation lazysup4 = BinaryRelations.lazySupremum(first, fakeNotRandomAccess(second));
    assertBinaryRelation(lazysup4, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });

    BinaryRelation reducersup = Reducers.BINARYRELATION.join().combine(first, second);
    assertBinaryRelation(reducersup, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    assertFalse(reducersup.isLazilyEvaluated());
    BinaryRelation reducersup2 = Reducers.BINARYRELATION.join().combineLazily(first, second);
    assertBinaryRelation(reducersup2, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    BinaryRelation reducersup3 = Reducers.BINARYRELATION.join().coarsen(first, second);
    assertBinaryRelation(reducersup3, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    BinaryRelation reducer_refined = Reducers.BINARYRELATION.join().refine(first, second);
    assertBinaryRelation(reducer_refined, (i, j) -> first.contains(i, j) && second.contains(i, j),
        (t, value) -> {
        });
    BinaryRelation reducer_coarseningsup = Reducers.BINARYRELATION.join().coarseningCombine(third,
        first, second);
    assertBinaryRelation(reducer_coarseningsup,
        (i, j) -> third.contains(i, j) || first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    BinaryRelation reducer_refiningsup = Reducers.BINARYRELATION.join().refiningCombine(third,
        first, second);
    assertBinaryRelation(reducer_refiningsup,
        (i, j) -> third.contains(i, j) && (first.contains(i, j) || second.contains(i, j)),
        (t, value) -> {
        });
    assertTrue(Reducers.BINARYRELATION.join().isAssociative());
    assertTrue(Reducers.BINARYRELATION.join().isCommutative());
    assertTrue(Reducers.BINARYRELATION.join().isIsotone());
    assertTrue(Reducers.BINARYRELATION.join().isNondecreasing());
    assertFalse(Reducers.BINARYRELATION.join().isNonincreasing());
    assertFalse(Reducers.BINARYRELATION.join().isConstant());
  }

  @Test
  public void testBinaryRelationMeet() {
    final Random rand = new Random();
    final int size = 20;

    RelationBuilder<? extends BinaryRelation> builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation first = builder.build();
    builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation second = builder.build();

    builder = RelationBuilders.denseRelationBuilder(size);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rand.nextBoolean()) {
          builder.add(i, j);
        }
      }
    }
    BinaryRelation third = builder.build();

    BiIntPredicate combined = (i, j) -> first.contains(i, j) && second.contains(i, j);

    BinaryRelation reducerinf = Reducers.BINARYRELATION.meet().combine(first, second);
    assertBinaryRelation(reducerinf, combined, (t, value) -> {
    });
    assertFalse(reducerinf.isLazilyEvaluated());
    BinaryRelation reducerinf2 = Reducers.BINARYRELATION.meet().combineLazily(first, second);
    assertBinaryRelation(reducerinf2, combined, (t, value) -> {
    });
    BinaryRelation reducer_coarsen = Reducers.BINARYRELATION.meet().coarsen(first, second);
    assertBinaryRelation(reducer_coarsen, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });
    BinaryRelation reducerinf3 = Reducers.BINARYRELATION.meet().refine(first, second);
    assertBinaryRelation(reducerinf3, combined, (t, value) -> {
    });
    BinaryRelation reducer_coarseninginf = Reducers.BINARYRELATION.meet().coarseningCombine(third,
        first, second);
    assertBinaryRelation(reducer_coarseninginf,
        (i, j) -> third.contains(i, j) || combined.testInt(i, j), (t, value) -> {
        });
    BinaryRelation reducer_refininginf = Reducers.BINARYRELATION.meet().refiningCombine(third,
        first, second);
    assertBinaryRelation(reducer_refininginf,
        (i, j) -> third.contains(i, j) && combined.testInt(i, j),
        (t, value) -> {
        });
    assertTrue(Reducers.BINARYRELATION.meet().isAssociative());
    assertTrue(Reducers.BINARYRELATION.meet().isCommutative());
    assertTrue(Reducers.BINARYRELATION.meet().isIsotone());
    assertTrue(Reducers.BINARYRELATION.meet().isNonincreasing());
    assertFalse(Reducers.BINARYRELATION.meet().isNondecreasing());
    assertFalse(Reducers.BINARYRELATION.meet().isConstant());
  }

  private void assertTransitive(Ranking ranking) {
    final int size = ranking.domainSize();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        for (int k = 0; k < size; ++k) {
          if (ranking.lessEqualThan(i, j) && ranking.lessEqualThan(j, k)) {
            assertTrue(ranking.lessEqualThan(i, k));
          }
        }
      }
    }
  }

  @Test
  public void testSupremumRankings() {
    final int size = 20;
    final BiIntPredicate generator1 = (i, j) -> i <= j && (i % 2) >= (j % 2);
    final BiIntPredicate generator2 = (i, j) -> i == 0 ? j == 0 : j % i == 0 && j != 0;
    final BiIntPredicate generator3 = (i, j) -> i <= j
        && ((i % 3) == (j % 3) || ((i % 3 != 0) && (j % 3 == 0)));
    final BiIntPredicate combined = (i, j) -> generator1.testInt(i, j) || generator2.testInt(i, j);

    Ranking first = new LazyCachedBinaryRelationMatrixImpl(size, generator1);
    Ranking second = new LazyCachedBinaryRelationMatrixImpl(size, generator2);
    Ranking third = new LazyCachedBinaryRelationMatrixImpl(size, generator3);

    assertTransitive(first);
    assertTransitive(second);
    assertTransitive(third);

    Ranking sup = Rankings.supremum(first, second);
    assertBinaryRelation(sup, combined, (t, value) -> {
    });
    assertFalse(sup.isLazilyEvaluated());

    Ranking sup2 = Rankings.supremum(fakeNotRandomAccess(first), second);
    assertBinaryRelation(sup2, combined, (t, value) -> {
    });
    assertFalse(sup2.isLazilyEvaluated());

    Ranking sup3 = Rankings.supremum(fakeNotRandomAccess(first), fakeNotRandomAccess(second));
    assertBinaryRelation(sup3, combined, (t, value) -> {
    });
    assertFalse(sup3.isLazilyEvaluated());

    Ranking sup4 = Rankings.supremum(first, fakeNotRandomAccess(second));
    assertBinaryRelation(sup4, combined, (t, value) -> {
    });
    assertFalse(sup4.isLazilyEvaluated());

    Ranking reducersup = Reducers.RANKING.join().combine(first, second);
    assertBinaryRelation(reducersup, combined, (t, value) -> {
    });
    assertFalse(reducersup.isLazilyEvaluated());

    Ranking reducersup2 = Reducers.RANKING.join().combineLazily(first, second);
    assertBinaryRelation(reducersup2, combined, (t, value) -> {
    });

    Ranking reducersup3 = Reducers.RANKING.join().coarsen(first, second);
    assertBinaryRelation(reducersup3, combined, (t, value) -> {
    });

    Ranking reducersup4 = Reducers.RANKING.join().refine(first, third);
    assertBinaryRelation(reducersup4, (i, j) -> first.contains(i, j) && third.contains(i, j),
        (t, value) -> {
        });

    Ranking reducersup5 = Reducers.RANKING.join().refiningCombine(third, first, second);
    assertBinaryRelation(reducersup5, (i, j) -> combined.testInt(i, j) && third.contains(i, j),
        (t, value) -> {
        });

    Ranking reducersup6 = Reducers.RANKING.join().coarseningCombine(third, first, second);
    assertBinaryRelation(reducersup6,
        (i, j) -> i <= j && (i != j - 1 || (i % 3 == 2) || (i % 2 == 1)), (t, value) -> {
        });

    assertTrue(Reducers.RANKING.join().isAssociative());
    assertTrue(Reducers.RANKING.join().isCommutative());
    assertTrue(Reducers.RANKING.join().isIsotone());
    assertTrue(Reducers.RANKING.join().isNondecreasing());
    assertFalse(Reducers.RANKING.join().isNonincreasing());
    assertFalse(Reducers.RANKING.join().isConstant());

  }

  @Test
  public void testRankingMeet() {
    final int size = 20;
    final BiIntPredicate generator1 = (i, j) -> i <= j && (i % 2) >= (j % 2);
    final BiIntPredicate generator2 = (i, j) -> i == 0 ? j == 0 : j % i == 0 && j != 0;
    final BiIntPredicate generator3 = (i, j) -> i <= j
        && ((i % 3) == (j % 3) || ((i % 3 != 0) && (j % 3 == 0)));
    final BiIntPredicate combined = (i, j) -> generator1.testInt(i, j) && generator2.testInt(i, j);

    Ranking first = new LazyCachedBinaryRelationMatrixImpl(size, generator1);
    Ranking second = new LazyCachedBinaryRelationMatrixImpl(size, generator2);
    Ranking third = new LazyCachedBinaryRelationMatrixImpl(size, generator3);

    assertTransitive(first);
    assertTransitive(second);
    assertTransitive(third);

    Ranking reducerinf = Reducers.RANKING.meet().combine(first, second);
    assertBinaryRelation(reducerinf, combined, (t, value) -> {
    });
    assertFalse(reducerinf.isLazilyEvaluated());

    Ranking reducerinf2 = Reducers.RANKING.meet().combineLazily(first, second);
    assertBinaryRelation(reducerinf2, combined, (t, value) -> {
    });

    Ranking reducerinf3 = Reducers.RANKING.meet().refine(first, second);
    assertBinaryRelation(reducerinf3, combined, (t, value) -> {
    });

    Ranking reducer_coarsen = Reducers.RANKING.meet().coarsen(first, second);
    assertBinaryRelation(reducer_coarsen, (i, j) -> first.contains(i, j) || second.contains(i, j),
        (t, value) -> {
        });

    Ranking reducerinf4 = Reducers.RANKING.meet().refiningCombine(third, first, second);
    assertBinaryRelation(reducerinf4, (i, j) -> combined.testInt(i, j) && third.contains(i, j),
        (t, value) -> {
        });

    Ranking reducerinf_coarsened = Reducers.RANKING.meet().coarseningCombine(third, first, second);
    assertBinaryRelation(reducerinf_coarsened,
        (i, j) -> i <= j && (i % 3 == 0 ? j % 3 == 0 : ((j + i) % 3) != 0 || (j - i) / 3 >= i / 3),
        (t, value) -> {
        });

    assertTrue(Reducers.RANKING.meet().isAssociative());
    assertTrue(Reducers.RANKING.meet().isCommutative());
    assertTrue(Reducers.RANKING.meet().isIsotone());
    assertTrue(Reducers.RANKING.meet().isNonincreasing());
    assertFalse(Reducers.RANKING.meet().isNondecreasing());
    assertFalse(Reducers.RANKING.meet().isConstant());

  }
}
