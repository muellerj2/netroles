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
package ch.ethz.sn.visone3.roles.test.blocks;

import static ch.ethz.sn.visone3.roles.test.blocks.OperatorTestUtilities.checkConverter;
import static ch.ethz.sn.visone3.roles.test.blocks.OperatorTestUtilities.checkOperator;
import static ch.ethz.sn.visone3.roles.test.blocks.OperatorTestUtilities.checkRoleOperator;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.Reducer;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;

import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OperatorCompositionTest {

  private static <T, U> Operator<T, U> createOperator(final Function<T, U> operator,
      final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, final Runnable onCacheRelease) {
    return new Operator<T, U>() {

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }

      @Override
      public U apply(T in) {
        return operator.apply(in);
      }

      @Override
      public void releaseCache() {
        onCacheRelease.run();
      }

    };
  }

  private static <T, U> RoleConverter<T, U> createConverter(final Function<T, U> converter,
      final BinaryOperator<U> coarseningCombiner, final BinaryOperator<U> refiningCombiner,
      final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, final Runnable onCacheRelease) {
    return new RoleConverter<T, U>() {

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }

      @Override
      public U convert(T in) {
        return converter.apply(in);
      }

      @Override
      public void releaseCache() {
        onCacheRelease.run();
      }

      @Override
      public U convertRefining(T in, U toRefine) {
        return refiningCombiner.apply(converter.apply(in), toRefine);
      }

      @Override
      public U convertCoarsening(T in, U toCoarsen) {
        return coarseningCombiner.apply(converter.apply(in), toCoarsen);
      }

    };
  }

  private static <T> RoleOperator<T> createRoleOperator(final Function<T, T> converter,
      final BinaryOperator<T> coarseningCombiner, final BinaryOperator<T> refiningCombiner,
      final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, final Runnable onCacheRelease) {
    return new RoleOperator<T>() {

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }

      @Override
      public T relative(T in) {
        return converter.apply(in);
      }

      @Override
      public void releaseCache() {
        onCacheRelease.run();
      }

      @Override
      public T relativeRefining(T in, T toRefine) {
        return refiningCombiner.apply(converter.apply(in), toRefine);
      }

      @Override
      public T relativeCoarsening(T in, T toCoarsen) {
        return coarseningCombiner.apply(converter.apply(in), toCoarsen);
      }

    };
  }

  private static <T> Reducer<T> createReducer(final BinaryOperator<T> operator,
      final boolean associative, final boolean commutative, final boolean isotone,
      final boolean constant, final boolean nonincreasing, final boolean nondecreasing,
      Runnable onCacheReleased) {
    return new Reducer<T>() {

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }

      @Override
      public boolean isAssociative() {
        return associative;
      }

      @Override
      public boolean isCommutative() {
        return commutative;
      }

      @Override
      public T combine(T first, T second) {
        return operator.apply(first, second);
      }

      @Override
      public void releaseCache() {
        Reducer.super.releaseCache();
        onCacheReleased.run();
      }
    };
  }

  private static <T> RoleReducer<T> createRoleReducer(final BinaryOperator<T> operator,
      final BinaryOperator<T> coarseningCombiner, final BinaryOperator<T> refiningCombiner,
      final boolean associative, final boolean commutative, final boolean isotone,
      final boolean constant, final boolean nonincreasing, final boolean nondecreasing,
      Runnable onCacheReleased) {
    return new RoleReducer<T>() {

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }

      @Override
      public boolean isAssociative() {
        return associative;
      }

      @Override
      public boolean isCommutative() {
        return commutative;
      }

      @Override
      public T combine(T first, T second) {
        return operator.apply(first, second);
      }

      @Override
      public void releaseCache() {
        RoleReducer.super.releaseCache();
        onCacheReleased.run();
      }

      @Override
      public T refiningCombine(T base, T first, T second) {
        return refine(operator.apply(first, second), base);
      }

      @Override
      public T coarseningCombine(T base, T first, T second) {
        return coarsen(operator.apply(first, second), base);
      }

      @Override
      public T refine(T base, T combined) {
        return refiningCombiner.apply(base, combined);
      }

      @Override
      public T coarsen(T base, T combined) {
        return coarseningCombiner.apply(base, combined);
      }
    };
  }

  @Test
  public void testComposeOp() {
    boolean[] cacheReleased = new boolean[2];
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 1 : 2;
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, true, false, true, false, () -> {
          cacheReleased[1] = true;
        })), false, 4L, false, false, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        });
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 1 : 2;
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return 2L;
        }, true, true, false, false, () -> {
          cacheReleased[1] = true;
        })), false, 2L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        });
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return 3;
        }, true, true, false, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), false, 6L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        });
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 2 : 1;
        }, true, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Math.abs(3L * x);
        }, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), true, 6L, true, false, false, true, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        });
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Long.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[1] = true;
        })), true, Long.MIN_VALUE, true, true, true, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        });
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, true, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    checkOperator(
        Operators.composeOp(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, false, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createOperator(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, false, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
  }

  @Test
  public void testComposeConv() {
    boolean[] cacheReleased = new boolean[2];
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 1 : 2;
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, Math::max, Math::min, true, false, true, false, () -> {
          cacheReleased[1] = true;
        })), false, 4L, false, false, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -5L, 20L, -5L, 4L, 4L, 20L);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 1 : 2;
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return 2L;
        }, Math::max, Math::min, true, true, false, false, () -> {
          cacheReleased[1] = true;
        })), false, 2L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -5L, 20L, -5L, 2L, 2L, 20L);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return 3;
        }, true, true, false, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, Math::max, Math::min, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), false, 6L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -5L, 20L, -5L, 6L, 6L, 20L);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return x ? 2 : 1;
        }, true, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(3L * x);
        }, Math::max, Math::min, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), true, 6L, true, false, false, true, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -8L, 25L, -8L, 6L, 6L, 25L);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MIN_VALUE;
        }, Math::max, Math::min, true, true, true, false, () -> {
          cacheReleased[1] = true;
        })), true, Long.MIN_VALUE, true, true, true, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, 5L, 20L, Long.MIN_VALUE, Long.MIN_VALUE, 5L, 20L);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, true, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null, 5L, 20L, 5L, 20L, Long.MAX_VALUE,
        Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    checkOperator(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, true, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    checkConverter(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, false, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, false, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null, -5L, 23L, -5L, 23L,
        Long.MAX_VALUE, Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    checkOperator(
        Operators.composeConv(OperatorCompositionTest.<Boolean, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, false, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, false, true, false, true, () -> {
          cacheReleased[1] = true;
        })), true, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
  }

  @Test
  public void testComposeRoleOp() {
    boolean[] cacheReleased = new boolean[2];
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Math.abs((int) (x / 3));
        }, true, false, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, Math::max, Math::min, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), 25L, 16L, true, false, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -55L, 20L, -55L, 16L, 16L, 20L, 0L, 25L);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return -Math.abs((int) (x * 3));
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return 2L;
        }, Math::max, Math::min, true, true, false, false, () -> {
          cacheReleased[1] = true;
        })), -17L, 2L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -5L, 20L, -5L, 2L, 2L, 20L, -17L, 2L);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return 3;
        }, true, true, false, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(2L * x);
        }, Math::max, Math::min, true, false, false, true, () -> {
          cacheReleased[1] = true;
        })), 10L, 6L, true, true, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -5L, 20L, -5L, 6L, 6L, 20L, 6L, 10L);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return (int) -Math.abs(x);
        }, false, false, false, true, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Math.abs(x) + Math.max((100L - Math.abs(x)) / 2, 0);
        }, Math::max, Math::min, true, false, true, false, () -> {
          cacheReleased[1] = true;
        })), 25L, 62L, false, false, false, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, -8L, 125L, -8L, 62L, 62L, 125L, 25L, 99L);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MIN_VALUE;
        }, Math::max, Math::min, true, true, true, false, () -> {
          cacheReleased[1] = true;
        })), 10L, Long.MIN_VALUE, true, true, true, false, () -> {
          assertTrue(cacheReleased[0]);
          assertTrue(cacheReleased[1]);
        }, 5L, 20L, Long.MIN_VALUE, Long.MIN_VALUE, 5L, 20L, Long.MIN_VALUE, 10L);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, true, true, false, true, () -> {
          cacheReleased[1] = true;
        })), 10L, Long.MAX_VALUE, true, true, false, true, null, 5L, 20L, 5L, 20L, Long.MAX_VALUE,
        Long.MAX_VALUE, 10L, Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    checkOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, true, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, true, true, false, true, () -> {
          cacheReleased[1] = true;
        })), 10L, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    checkRoleOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, false, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, false, true, false, true, () -> {
          cacheReleased[1] = true;
        })), 10L, Long.MAX_VALUE, true, true, false, true, null, -5L, 23L, -5L, 23L, Long.MAX_VALUE,
        Long.MAX_VALUE, 10L, Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    checkOperator(
        Operators.composeRoleOp(OperatorCompositionTest.<Long, Integer>createOperator(x -> {
          cacheReleased[0] = false;
          return Integer.MIN_VALUE;
        }, false, true, true, false, () -> {
          cacheReleased[0] = true;
        }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
          cacheReleased[1] = false;
          return Long.MAX_VALUE;
        }, Math::max, Math::min, false, true, false, true, () -> {
          cacheReleased[1] = true;
        })), 10L, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
  }

  @Test
  public void testAdapt() {

    boolean[] cacheReleased = new boolean[3];
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;

    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Math.abs((int) (x / 3));
    }, true, false, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Math.abs(2L * x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 25L, 20L, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -55L, 25L, -55L, 20L, 20L, 25L, 12L, 25L);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return -x - 2;
    }, Math::max, Math::min, false, false, false, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return -Math.abs((int) (x * 3));
    }, false, false, false, true, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return 2L;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[1] = true;
    })), -17L, 2L, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -5L, 20L, -5L, 2L, 2L, 20L, -17L, 2L);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Math.abs(2L * x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 13L, 10L, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -5L, 20L, -5L, 10L, 10L, 20L, 10L, 13L);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return Math.max(x - 2, -100);
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return (int) -Math.abs(x);
    }, false, false, false, true, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Math.abs(x) + Math.max((100L - Math.abs(x)) / 2, 0);
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[1] = true;
    })), 25L, 63L, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -8L, 125L, -8L, 63L, 63L, 125L, 25L, 100L);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Long.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[1] = true;
    })), 10L, Long.MIN_VALUE, true, true, true, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, 5L, 20L, Long.MIN_VALUE, Long.MIN_VALUE, 5L, 20L, Long.MIN_VALUE, 10L);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Long.MAX_VALUE;
    }, Math::max, Math::min, true, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10L, Long.MAX_VALUE, true, true, false, true, null, 5L, 20L, 5L, 20L, Long.MAX_VALUE,
        Long.MAX_VALUE, 10L, Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[2]);
    checkOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Long.MAX_VALUE;
    }, Math::max, Math::min, true, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10L, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    assertTrue(cacheReleased[2]);
    checkRoleOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, false, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Long.MAX_VALUE;
    }, Math::max, Math::min, false, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10L, Long.MAX_VALUE, true, true, false, true, null, -5L, 23L, -5L, 23L, Long.MAX_VALUE,
        Long.MAX_VALUE, 10L, Long.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[2]);
    checkOperator(Operators.adapt(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.<Long, Integer>createOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, false, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.<Integer, Long>createConverter(x -> {
      cacheReleased[1] = false;
      return Long.MAX_VALUE;
    }, Math::max, Math::min, false, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10L, Long.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    assertTrue(cacheReleased[2]);
  }

  @Test
  public void testSeries() {

    boolean[] cacheReleased = new boolean[3];
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Math.abs(x / 3);
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;

    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Math.abs(2 * x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 25, 20, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -55, 25, -55, 20, 20, 25, 12, 25);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return -Math.abs(x * 3);
    }, Math::max, Math::min, false, false, false, true, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return -x - 2;
    }, Math::max, Math::min, false, false, false, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return 2;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[1] = true;
    })), -17, 2, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -5, 20, -5, 2, 2, 20, -17, 2);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Math.abs(2 * x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 13, 10, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -5, 20, -5, 10, 10, 20, 10, 13);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return -Math.abs(x);
    }, Math::max, Math::min, false, false, false, true, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return Math.max(x - 2, -100);
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Math.abs(x) + Math.max((100 - Math.abs(x)) / 2, 0);
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[1] = true;
    })), 25, 63, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, -8, 125, -8, 63, 63, 125, 25, 100);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[1] = true;
    })), 10, Integer.MIN_VALUE, true, true, true, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
    }, 5, 20, Integer.MIN_VALUE, Integer.MIN_VALUE, 5, 20, Integer.MIN_VALUE, 10);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, true, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10, Integer.MAX_VALUE, true, true, false, true, null, 5, 20, 5, 20, Integer.MAX_VALUE,
        Integer.MAX_VALUE, 10, Integer.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[2]);
    checkOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, true, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10, Integer.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    assertTrue(cacheReleased[2]);
    checkRoleOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, false, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, false, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10, Integer.MAX_VALUE, true, true, false, true, null, -5, 23, -5, 23, Integer.MAX_VALUE,
        Integer.MAX_VALUE, 10, Integer.MAX_VALUE);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[2]);
    checkOperator(Operators.series(OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[0] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, false, true, true, false, () -> {
      cacheReleased[0] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, true, false, () -> {
      cacheReleased[2] = true;
    }), OperatorCompositionTest.createRoleOperator(x -> {
      cacheReleased[1] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, false, true, false, true, () -> {
      cacheReleased[1] = true;
    })), 10, Integer.MAX_VALUE, true, true, false, true, null);
    assertTrue(cacheReleased[0]);
    assertTrue(cacheReleased[1]);
    assertTrue(cacheReleased[2]);
  }

  @Test
  public void testParallelOperator() {
    boolean[] cacheReleased = new boolean[5];
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return Integer.MAX_VALUE;
    }, true, true, true, true, false, true, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return -x;
    }, false, false, false, false, () -> {
      cacheReleased[0] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 5;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[3] = true;
    })), 20, Integer.MAX_VALUE, true, true, false, true, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, true, true, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return -x;
    }, false, false, false, false, () -> {
      cacheReleased[0] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 5;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    })), 20, Integer.MIN_VALUE, true, true, true, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return Math.max(x, y);
    }, true, true, true, false, false, true, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, true, true, true, false, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return Integer.MAX_VALUE;
    }, true, true, false, true, () -> {
      cacheReleased[2] = true;
    })), 20, Integer.MAX_VALUE, true, true, false, true, () -> {
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return Math.max(x, y);
    }, true, true, true, false, false, true, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 20, 40, true, false, false, true, () -> {
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, true, true, false, false, () -> {
      cacheReleased[0] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 20, 40, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 40, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 3, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 3, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x * x + y;
    }, false, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return -x;
    }, false, false, false, false, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return x - 2;
    }, false, false, true, false, () -> {
      cacheReleased[0] = true;
    })), 20, 418, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x * x + y;
    }, false, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return x;
    }, true, false, true, true, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return x - 2;
    }, false, false, true, false, () -> {
      cacheReleased[0] = true;
    })), 20, 418, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    });
    checkOperator(Operators.<Integer, Integer>parallel(createReducer((x, y) -> {
      cacheReleased[4] = false;
      return x * x - y;
    }, false, false, false, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), createOperator(x -> {
      cacheReleased[0] = false;
      return x;
    }, true, false, true, true, () -> {
      cacheReleased[0] = true;
    })), 20, 464, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    });
  }

  @Test
  public void testParallelRoleOperator() {
    boolean[] cacheReleased = new boolean[5];
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, true, true, true, true, false, true, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return -x;
    }, Math::max, Math::min, false, false, false, false, () -> {
      cacheReleased[0] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 5;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[3] = true;
    })), 20, Integer.MAX_VALUE, true, true, false, true, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 1000, Integer.MAX_VALUE, Integer.MAX_VALUE, 20, Integer.MAX_VALUE);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, true, true, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return -x;
    }, Math::max, Math::min, false, false, false, false, () -> {
      cacheReleased[0] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 5;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    })), 20, Integer.MIN_VALUE, true, true, true, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, Integer.MIN_VALUE, Integer.MIN_VALUE, -50, 1000, Integer.MIN_VALUE, 20);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return Math.max(x, y);
    }, Math::max, Math::min, true, true, true, false, false, true, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return Integer.MIN_VALUE;
    }, Math::max, Math::min, true, true, true, false, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return Integer.MAX_VALUE;
    }, Math::max, Math::min, true, true, false, true, () -> {
      cacheReleased[2] = true;
    })), 20, Integer.MAX_VALUE, true, true, false, true, () -> {
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 1000, Integer.MAX_VALUE, Integer.MAX_VALUE, 20, Integer.MAX_VALUE);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return Math.max(x, y);
    }, Math::max, Math::min, true, true, true, false, false, true, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return x;
    }, Math::max, Math::min, true, false, true, true, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x;
    }, Math::max, Math::min, true, false, true, true, () -> {
      cacheReleased[2] = true;
    })), 20, 20, true, false, false, true, () -> {
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 20, 20, 1000, 20, 20);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return Math.max(x, y);
    }, Math::max, Math::min, true, true, true, false, false, true, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 20, 40, true, false, false, true, () -> {
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 40, 40, 1000, 20, null);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, Math::max, Math::min, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[0] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    })), 20, 40, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 40, 40, 1000, 20, null);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, Math::max, Math::min, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 40, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 40, 40, 1000, 20, null);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, Math::max, Math::min, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[3] = false;
      return 2 * Math.abs(x);
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[3] = true;
    }), createRoleOperator(x -> {
      cacheReleased[1] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[1] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 3, true, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[1]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[3]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 3, 3, 1000, 3, 20);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x;
    }, Math::max, Math::min, true, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return 3;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return 20;
    }, Math::max, Math::min, true, true, false, false, () -> {
      cacheReleased[0] = true;
    })), 20, 3, true, true, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 3, 3, 1000, 3, 20);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x*x + y;
    }, Math::max, Math::min, false, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return -x;
    }, Math::max, Math::min, false, false, false, false, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return x - 2;
    }, Math::max, Math::min, false, false, true, false, () -> {
      cacheReleased[0] = true;
    })), 20, 418, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 418, 418, 1000, 20, null);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x * x + y;
    }, Math::max, Math::min, false, false, true, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x;
    }, Math::max, Math::min, true, false, true, true, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return x - 2;
    }, Math::max, Math::min, false, false, true, false, () -> {
      cacheReleased[0] = true;
    })), 20, 418, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 418, 418, 1000, 20, null);
    checkRoleOperator(Operators.parallel(createRoleReducer((x, y) -> {
      cacheReleased[4] = false;
      return x * x - y;
    }, Math::max, Math::min, false, false, false, false, false, false, () -> {
      cacheReleased[4] = true;
    }), createRoleOperator(x -> {
      cacheReleased[2] = false;
      return x + 2;
    }, Math::max, Math::min, true, false, false, true, () -> {
      cacheReleased[2] = true;
    }), createRoleOperator(x -> {
      cacheReleased[0] = false;
      return x;
    }, Math::max, Math::min, true, false, true, true, () -> {
      cacheReleased[0] = true;
    })), 20, 464, false, false, false, false, () -> {
      assertTrue(cacheReleased[0]);
      assertTrue(cacheReleased[2]);
      assertTrue(cacheReleased[4]);
    }, -50, 1000, -50, 464, 464, 1000, 20, null);
  }
}
