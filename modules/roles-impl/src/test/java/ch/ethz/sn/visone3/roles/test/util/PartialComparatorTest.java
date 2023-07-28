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

import ch.ethz.sn.visone3.roles.util.PartialComparator;

import org.junit.jupiter.api.Test;

public class PartialComparatorTest {

  @Test
  public void testComparatorToPartialComparator() {
    PartialComparator<Integer> comparator = PartialComparator.fromComparator(Integer::compare);
    assertEquals(PartialComparator.ComparisonResult.EQUAL, comparator.compare(1, 1));
    assertEquals(PartialComparator.ComparisonResult.LESS, comparator.compare(1, 2));
    assertEquals(PartialComparator.ComparisonResult.GREATER, comparator.compare(2, 1));
  }

}
