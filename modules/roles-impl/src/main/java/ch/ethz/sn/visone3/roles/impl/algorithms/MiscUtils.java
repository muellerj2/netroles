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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import ch.ethz.sn.visone3.roles.util.PartialComparator;
import ch.ethz.sn.visone3.roles.util.PartialComparator.ComparisonResult;

import java.util.Comparator;
import java.util.function.BiPredicate;

class MiscUtils {

  private MiscUtils() {
  }

  public static <T> BiPredicate<T, T> lessEqualPredicate(Comparator<T> comparator) {
    return (a, b) -> comparator.compare(a, b) <= 0;
  }
  

  public static <T> BiPredicate<T, T> lessEqualPredicate(PartialComparator<T> comparator) {
    return (a, b) -> {
      PartialComparator.ComparisonResult result = comparator.compare(a, b);
      return result == ComparisonResult.EQUAL || result == ComparisonResult.LESS;
    };
  }
  
  public static <T> BiPredicate<T, T> alwaysTrue() {
    return (a, b) -> true;
  }
}
