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

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;

public class OperatorTestUtilities {

  private OperatorTestUtilities() {
  }

  public static <T, U> void checkOperator(final Operator<T, U> op, final T input, final U result,
      final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, Runnable checkAfterCacheReleased) {
    assertEquals(result, op.apply(input));
    assertEquals(isotone, op.isIsotone());
    assertEquals(isotone, op.isIsotone());
    assertEquals(nonincreasing, op.isNonincreasing());
    assertEquals(nonincreasing, op.isNonincreasing());
    assertEquals(nondecreasing, op.isNondecreasing());
    assertEquals(nondecreasing, op.isNondecreasing());
    assertEquals(constant, op.isConstant());
    assertEquals(constant, op.isConstant());
    assertEquals(result, op.apply(input));
    if (checkAfterCacheReleased != null) {
      op.releaseCache();
      checkAfterCacheReleased.run();
      assertEquals(result, op.apply(input));
    }
  }

  public static <T, U> void checkConverter(RoleConverter<T, U> converter, final T input,
      final U result, final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, Runnable checkAfterCacheReleased, final U lowerValue,
      final U largerValue, final U refinedLowResult, final U refinedLargeResult,
      final U coarsenedLowResult, final U coarsenedLargeResult) {
    checkOperator(converter, input, result, isotone, constant, nonincreasing, nondecreasing,
        checkAfterCacheReleased);
    assertEquals(result, converter.convert(input));
    assertEquals(refinedLowResult, converter.convertRefining(input, lowerValue));
    assertEquals(refinedLargeResult, converter.convertRefining(input, largerValue));
    assertEquals(coarsenedLowResult, converter.convertCoarsening(input, lowerValue));
    assertEquals(coarsenedLargeResult, converter.convertCoarsening(input, largerValue));

  }

  public static <U> void checkRoleOperator(RoleOperator<U> operator, final U input, final U result,
      final boolean isotone, final boolean constant, final boolean nonincreasing,
      final boolean nondecreasing, Runnable checkAfterCacheReleased, final U lowerValue,
      final U largerValue, final U refinedLowResult, final U refinedLargeResult,
      final U coarsenedLowResult, final U coarsenedLargeResult, final U interiorResult,
      final U closureResult) {
    checkConverter(operator, input, result, isotone, constant, nonincreasing, nondecreasing,
        checkAfterCacheReleased, lowerValue, largerValue, refinedLowResult, refinedLargeResult,
        coarsenedLowResult, coarsenedLargeResult);
    assertEquals(result, operator.relative(input));
    assertEquals(refinedLowResult, operator.relativeRefining(input, lowerValue));
    assertEquals(refinedLargeResult, operator.relativeRefining(input, largerValue));
    assertEquals(coarsenedLowResult, operator.relativeCoarsening(input, lowerValue));
    assertEquals(coarsenedLargeResult, operator.relativeCoarsening(input, largerValue));
    assertEquals(operator.relativeRefining(input, input), operator.restrict(input));
    assertEquals(operator.relativeCoarsening(input, input), operator.extend(input));
    if (interiorResult != null) {
      assertEquals(interiorResult, operator.interior(input));
    }
    if (closureResult != null) {
      assertEquals(closureResult, operator.closure(input));
    }
  }

}
