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

package ch.ethz.sn.visone3.lang.impl.iterators;

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

final class FilterIterable<E> implements Iterable<E> {
  private final Iterable<E> iterable;
  private final Predicate<? super E> filter;

  public FilterIterable(final Iterable<E> iterable, final Predicate<? super E> filter) {
    this.iterable = iterable;
    this.filter = filter;
  }

  @Override
  public Iterator<E> iterator() {
    return new FilterIterator<>(iterable.iterator(), filter);
  }

  public static class OfInt implements PrimitiveIterable.OfInt {
    private final PrimitiveIterable.OfInt iterable;
    private final IntPredicate filter;

    public OfInt(final OfInt iterable, final IntPredicate filter) {
      this.iterable = iterable;
      this.filter = filter;
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
      return new FilterIterator.OfInt(iterable.iterator(), filter);
    }
  }
}
