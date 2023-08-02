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
package ch.ethz.sn.visone3.lang.impl.mappings;

import ch.ethz.sn.visone3.lang.ConstMapping;

import java.util.PrimitiveIterator;
import java.util.stream.Stream;

abstract class IntMappingBase implements ConstMapping.OfInt {
  private static final long serialVersionUID = -5140749317114506964L;

  @Override
  public Integer get(int index) {
    return getInt(index);
  }

  @Override
  public Stream<Integer> stream() {
    return intStream().boxed();
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (final int i : this) {
      hashCode = 31 * hashCode + Integer.hashCode(i);
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof ConstMapping.OfInt)) {
      return false;
    }
    final PrimitiveIterator.OfInt e1 = iterator();
    final PrimitiveIterator.OfInt e2 = ((ConstMapping.OfInt) other).iterator();
    while (e1.hasNext() && e2.hasNext()) {
      if (e1.nextInt() != e2.nextInt()) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());

  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{size=").append(size()).append(",[");
    FormatterUtility.limited(sb, iterator(), 80);
    sb.append("]}");
    return sb.toString();
  }
}
