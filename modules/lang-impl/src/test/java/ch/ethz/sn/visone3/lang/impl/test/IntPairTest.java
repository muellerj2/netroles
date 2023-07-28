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

package ch.ethz.sn.visone3.lang.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ch.ethz.sn.visone3.lang.IntPair;

import org.junit.jupiter.api.Test;

public class IntPairTest {
  @Test
  public void testSetPermute() {
    assertEquals(IntPair.set(1, 0), IntPair.set(0, 1));
    assertEquals(IntPair.set(1, 2), IntPair.set(2, 1));
    assertEquals(IntPair.set(-1, -2), IntPair.set(-2, -1));

    assertEquals(1, IntPair.first(IntPair.tuple(1, 0)));
    assertEquals(0, IntPair.second(IntPair.tuple(1, 0)));
    assertEquals(1, IntPair.first(IntPair.tuple(1, 2)));
    assertEquals(2, IntPair.second(IntPair.tuple(1, 2)));
    assertEquals(-1, IntPair.first(IntPair.tuple(-1, 0)));
    assertEquals(0, IntPair.second(IntPair.tuple(-1, 0)));
    assertEquals(0, IntPair.first(IntPair.tuple(0, -2)));
    assertEquals(-2, IntPair.second(IntPair.tuple(0, -2)));
    assertEquals(-1, IntPair.first(IntPair.tuple(-1, -2)));
    assertEquals(-2, IntPair.second(IntPair.tuple(-1, -2)));

    assertEquals(0, IntPair.first(IntPair.set(1, 0)));
    assertEquals(1, IntPair.second(IntPair.set(1, 0)));
    assertEquals(0, IntPair.first(IntPair.set(0, 1)));
    assertEquals(1, IntPair.second(IntPair.set(0, 1)));
    assertEquals(2, IntPair.first(IntPair.set(2, 2)));
    assertEquals(2, IntPair.second(IntPair.set(2, 2)));

    assertEquals(new IntPair(2, 0), new IntPair(2, 0));
    assertNotEquals(new IntPair(2, 0), new IntPair(0, 2));
    assertEquals(new IntPair(2, 5).hashCode(), new IntPair(2, 5).hashCode());
    assertEquals(2, new IntPair(2, 5).getFirst());
    assertEquals(5, new IntPair(2, 5).getSecond());
  }
}
