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
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.distances.DistanceMatrices;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.structures.LazyIntDistanceMatrixImpl;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntBinaryOperator;

public class DistanceMatricesTest {

  @Test
  public void testLazyDistanceMatrix() {
    final int size = 15;
    final IntBinaryOperator generator = (i, j) -> 20 * i * i + 3 * j;
    int[] invocationCounter = new int[1];
    final IntBinaryOperator countingGenerator = (i, j) -> {
      ++invocationCounter[0];
      return generator.applyAsInt(i, j);
    };
    LazyIntDistanceMatrixImpl lazyDistanceMatrix = new LazyIntDistanceMatrixImpl(size,
        countingGenerator);
    assertEquals(size, lazyDistanceMatrix.getDomainSize());
    assertEquals(0, invocationCounter[0]);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(i, j), lazyDistanceMatrix.getDistance(i, j),
            String.format("getDistance(%s, %s)", i, j));
      }
    }
    assertEquals(size * size, invocationCounter[0]);
    int[][] matrix = lazyDistanceMatrix.asMatrix();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(i, j), matrix[i][j],
            String.format("asMatrix()[%s, %s]", i, j));
      }
    }
    lazyDistanceMatrix = new LazyIntDistanceMatrixImpl(size, countingGenerator);
    matrix = lazyDistanceMatrix.asMatrix();
    assertEquals(2 * size * size, invocationCounter[0]);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(i, j), matrix[i][j],
            String.format("asMatrix()[%s, %s]", i, j));
      }
    }
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(j, i), lazyDistanceMatrix.getDistance(j, i),
            String.format("getDistance(%s, %s)", j, i));
      }
    }
    IntDistanceMatrix lazyDistanceMatrix2 = new LazyIntDistanceMatrixImpl(size,
        countingGenerator);
    for (int i = 5; i >= 2; --i) {
      for (int j = 3; j < 10; ++j) {
        assertEquals(generator.applyAsInt(i, j), lazyDistanceMatrix2.getDistance(i, j),
            String.format("getDistance(%s, %s)", i, j));
      }
    }
    assertTrue(invocationCounter[0] >= 2 * size * size + (5 - 2 + 1) * (10 - 3));
    matrix = lazyDistanceMatrix2.asMatrix();
    assertEquals(3 * size * size, invocationCounter[0]);
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(i, j), matrix[i][j],
            String.format("asMatrix()[%s, %s]", i, j));
      }
    }
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(j, i), lazyDistanceMatrix2.getDistance(j, i),
            String.format("getDistance(%s, %s)", j, i));
      }
    }
    assertEquals(lazyDistanceMatrix, lazyDistanceMatrix2);
    assertEquals(lazyDistanceMatrix.hashCode(), lazyDistanceMatrix2.hashCode());
    assertNotEquals(new LazyIntDistanceMatrixImpl(size, (i, j) -> 5), lazyDistanceMatrix);
    assertNotEquals(new LazyIntDistanceMatrixImpl(size / 2, generator), lazyDistanceMatrix);
    assertFalse(lazyDistanceMatrix.equals(new Object()));
    assertFalse(lazyDistanceMatrix.equals(null));
  }

  @Test
  public void testEagerDistanceMatrix() {
    final int size = 15;
    final IntBinaryOperator generator = (i, j) -> 20 * i * i + 3 * j;
    final int[][] mat = new int[size][size];
    final int[][] mat2 = new int[size][size];
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int val = generator.applyAsInt(i, j);
        mat[i][j] = mat2[i][j] = val;
      }
    }
    IntDistanceMatrix distMatrix = DistanceMatrices.fromMatrix(mat);
    assertEquals(size, distMatrix.getDomainSize());
    assertTrue(Arrays.deepEquals(mat, mat2));
    assertTrue(Arrays.deepEquals(distMatrix.asMatrix(), mat2));
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        assertEquals(generator.applyAsInt(i, j), distMatrix.getDistance(i, j),
            String.format("getDistance(%s, %s)", i, j));
      }
    }
    assertTrue(Arrays.deepEquals(mat, mat2));
    assertTrue(Arrays.deepEquals(distMatrix.asMatrix(), mat2));
    assertEquals(new LazyIntDistanceMatrixImpl(size, (i, j) -> mat2[i][j]), distMatrix);
    assertEquals(distMatrix, new LazyIntDistanceMatrixImpl(size, (i, j) -> mat2[i][j]));
    assertEquals(new LazyIntDistanceMatrixImpl(size, (i, j) -> mat2[i][j]).hashCode(),
        distMatrix.hashCode());
    assertNotEquals(new LazyIntDistanceMatrixImpl(size / 2, (i, j) -> mat2[i][j]), distMatrix);
    assertNotEquals(new LazyIntDistanceMatrixImpl(size, (i, j) -> 5), distMatrix);
    assertFalse(distMatrix.equals(new Object()));
    assertFalse(distMatrix.equals(null));
  }

  @Test
  public void testDistanceAdd() {
    final int size = 20;
    final int[][] mat1 = new int[size][size];
    final int[][] mat2 = new int[size][size];
    final int[][] sum = new int[size][size];
    final Random rand = new Random();

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        mat1[i][j] = rand.nextInt(1024);
        mat2[i][j] = rand.nextInt(1024);
        sum[i][j] = mat1[i][j] + mat2[i][j];
      }
    }
    IntDistanceMatrix distMat1 = DistanceMatrices.fromMatrix(mat1);
    IntDistanceMatrix distMat2 = DistanceMatrices.fromMatrix(mat2);
    IntDistanceMatrix distSumMat = DistanceMatrices.fromMatrix(sum);

    assertEquals(distSumMat, Reducers.DISTANCE.add().combine(distMat1, distMat2));
    assertEquals(distSumMat, Reducers.DISTANCE.add().combineLazily(distMat1, distMat2));
    assertTrue(Reducers.DISTANCE.add().isAssociative());
    assertTrue(Reducers.DISTANCE.add().isCommutative());
    assertTrue(Reducers.DISTANCE.add().isIsotone());
    assertTrue(Reducers.DISTANCE.add().isNondecreasing());
    assertFalse(Reducers.DISTANCE.add().isNonincreasing());
    assertFalse(Reducers.DISTANCE.add().isConstant());
  }

  @Test
  public void testDistanceThresholderConverter() {
    final int size = 20;
    final Random rand = new Random();
    final int[][] distances = new int[20][20];
    final boolean[][] isLessEqual = new boolean[size][size];
    final boolean[][] binrel2mat = new boolean[size][size];
    IntBinaryOperator thresholds = (i, j) -> i * size + j;
    for (int i = 0; i < distances.length; ++i) {
      for (int j = 0; j < distances.length; ++j) {
        distances[i][j] = rand.nextInt((2 * i + 1) * size);
        isLessEqual[i][j] = distances[i][j] <= thresholds.applyAsInt(i, j);
        binrel2mat[i][j] = rand.nextBoolean();
      }
    }
    IntDistanceMatrix distMat = DistanceMatrices.fromMatrix(distances);
    final BinaryRelation result = BinaryRelations.fromMatrix(isLessEqual);
    final BinaryRelation binrel2 = BinaryRelations.fromMatrix(binrel2mat);

    assertEquals(result,
        Converters.thresholdDistances(thresholds).convert(distMat));
    assertEquals(result,
        Converters.thresholdDistances(thresholds).apply(distMat));
    assertEquals(BinaryRelations.infimum(binrel2, result),
        Converters.thresholdDistances(thresholds).convertRefining(distMat, binrel2));
    assertEquals(BinaryRelations.supremum(binrel2, result),
        Converters.thresholdDistances(thresholds).convertCoarsening(distMat, binrel2));
    assertTrue(Converters.thresholdDistances(thresholds).isIsotone());
    assertFalse(Converters.thresholdDistances(thresholds).isConstant());
    assertFalse(Converters.thresholdDistances(thresholds).isNonincreasing());
    assertFalse(Converters.thresholdDistances(thresholds).isNondecreasing());
  }
}
