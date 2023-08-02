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

package ch.ethz.sn.visone3.lang.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ch.ethz.sn.visone3.lang.Pair;

import org.junit.jupiter.api.Test;

public class PairTest {

  @Test
  void testPair() {
    Object first = new Object();
    Object second = new Object();
    Object third = new Object();
    Object fourth = new Object();
    assertEquals(new Pair<>(first, second), new Pair<>(first, second));
    assertEquals(new Pair<>(first, second).hashCode(), new Pair<>(first, second).hashCode());
    assertNotEquals(new Pair<>(first, second), new Pair<>(second, first));
    assertNotEquals(new Pair<>(first, second), new Pair<>(first, third));
    assertNotEquals(new Pair<>(first, second), new Pair<>(third, second));
    assertNotEquals(new Pair<>(first, second), new Pair<>(third, fourth));
    assertEquals(fourth, new Pair<>(fourth, third).getFirst());
    assertEquals(third, new Pair<>(first, third).getSecond());
    assertEquals(null, new Pair<>(null, first).getFirst());
    assertEquals(null, new Pair<>(first, null).getSecond());
    assertEquals(null, new Pair<>(null, null).getFirst());
    assertEquals(null, new Pair<>(null, null).getSecond());
    new Pair<>(null, null).hashCode();
    new Pair<>(first, null).hashCode();
    new Pair<>(null, second).hashCode();
    Pair<Object, Object> pair = new Pair<>(first, second);
    assertEquals(pair, pair);
    assertFalse(pair.equals(first));
    assertFalse(pair.equals(second));
    assertEquals(new Pair<>(null, null), new Pair<>(null, null));
    assertEquals(new Pair<>(first, null), new Pair<>(first, null));
    assertNotEquals(new Pair<>(first, null), new Pair<>(second, null));
    assertNotEquals(new Pair<>(first, null), new Pair<>(null, null));
    assertNotEquals(new Pair<>(first, null), new Pair<>(null, second));
    assertEquals(new Pair<>(null, second), new Pair<>(null, second));
    assertNotEquals(new Pair<>(null, first), new Pair<>(null, second));
    assertNotEquals(new Pair<>(null, first), new Pair<>(second, null));
    assertNotEquals(new Pair<>(null, first), new Pair<>(null, null));
    assertNotEquals(new Pair<>(null, null), new Pair<>(null, second));
    assertNotEquals(new Pair<>(null, null), new Pair<>(second, null));
  }
}
