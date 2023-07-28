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
package ch.ethz.sn.visone3.roles.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.roles.util.AccumulationSupport;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class AccumulationSupportTest {

  @Test
  public void testAccumulateArrayTriFunction() {
    Short[] weights = new Short[] { 23, 48, 2, 10 };
    String arg1 = "woof";
    String arg2 = "bark";
    assertEquals(
        Arrays.asList(weights).stream().mapToInt(Short::intValue).reduce(0, (x, y) -> x + y * y),
        AccumulationSupport.accumulate(arg1, arg2, weights,
            (x, y, z) -> arg1.equals(x) && arg2.equals(y) ? z * z : 0));
  }

  @Test
  public void testAccumulateMappingTriFunction() {
    ConstMapping<Short> weights = Mappings.newListFrom(Short.class, (short) 23, (short) 48,
        (short) 2, (short) 10);
    String arg1 = "woof";
    String arg2 = "bark";
    assertEquals(weights.stream().mapToInt(Short::intValue).reduce(0, (x, y) -> x + y * y),
        AccumulationSupport.accumulate(arg1, arg2, weights,
            (x, y, z) -> arg1.equals(x) && arg2.equals(y) ? z * z : 0));
  }

  @Test
  public void testAccumulateArrayBiFunction() {
    Short[] weights = new Short[] { 23, 48, 2, 10 };
    String arg1 = "woof";
    assertEquals(
        Arrays.asList(weights).stream().mapToInt(Short::intValue).reduce(0, (x, y) -> x + y * y),
        AccumulationSupport.accumulate(arg1, weights, (x, z) -> arg1.equals(x) ? z * z : 0));
  }

  @Test
  public void testAccumulateMappingBiFunction() {
    ConstMapping<Short> weights = Mappings.newListFrom(Short.class, (short) 23, (short) 48,
        (short) 2, (short) 10);
    String arg1 = "woof";
    assertEquals(weights.stream().mapToInt(Short::intValue).reduce(0, (x, y) -> x + y * y),
        AccumulationSupport.accumulate(arg1, weights, (x, z) -> arg1.equals(x) ? z * z : 0));
  }

}
