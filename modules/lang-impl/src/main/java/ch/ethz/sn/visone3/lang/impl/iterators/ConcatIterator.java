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

package ch.ethz.sn.visone3.lang.impl.iterators;

import java.util.Iterator;
import java.util.PrimitiveIterator;

final class ConcatIterator<T> implements Iterator<T> {
  private final Iterator<? extends T> first;
  private final Iterator<? extends T> second;

  public ConcatIterator(final Iterator<? extends T> first, final Iterator<? extends T> second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean hasNext() {
    return first.hasNext() || second.hasNext();
  }

  @Override
  public T next() {
    return first.hasNext() ? first.next() : second.next();
  }

  /**
   * Primitive integer version.
   */
  public static class OfInt implements PrimitiveIterator.OfInt {
    private final PrimitiveIterator.OfInt first;
    private final PrimitiveIterator.OfInt second;

    public OfInt(final PrimitiveIterator.OfInt first, final PrimitiveIterator.OfInt second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public boolean hasNext() {
      return first.hasNext() || second.hasNext();
    }

    @Override
    public int nextInt() {
      return first.hasNext() ? first.nextInt() : second.nextInt();
    }
  }
}
