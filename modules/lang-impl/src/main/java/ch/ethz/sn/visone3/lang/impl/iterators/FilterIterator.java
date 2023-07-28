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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Wrapping another iterator retuning only elements where the {@code predicate} evaluates to {@code
 * true}.
 */
final class FilterIterator<E> implements Iterator<E> {
  private final Iterator<E> iterator;
  private final Predicate<? super E> filter;
  private E next;

  public FilterIterator(final Iterator<E> iterator, final Predicate<? super E> filter) {
    this.iterator = Objects.requireNonNull(iterator);
    this.filter = Objects.requireNonNull(filter);
    findNext();
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    final E v = next;
    findNext();
    return v;
  }

  private void findNext() {
    while (iterator.hasNext()) {
      next = iterator.next();
      if (filter.test(next)) {
        return;
      }
    }
    next = null;
  }

  public static class OfInt implements PrimitiveIterator.OfInt {
    private final IntPredicate filter;
    private PrimitiveIterator.OfInt iterator;
    private int next;

    public OfInt(final OfInt iterator, final IntPredicate filter) {
      this.iterator = iterator;
      this.filter = filter;
      findNext();
    }

    @Override
    public int nextInt() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      final int v = next;
      findNext();
      return v;
    }

    @Override
    public boolean hasNext() {
      return iterator != null;
    }

    private void findNext() {
      while (iterator.hasNext()) {
        next = iterator.nextInt();
        if (filter.test(next)) {
          return;
        }
      }
      iterator = null;
    }
  }
}
