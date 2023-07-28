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
import java.util.Objects;
import java.util.function.Function;

final class MapIterator<E, T> implements Iterator<T> {
  private final Iterator<E> iterator;
  private final Function<? super E, T> map;

  MapIterator(final Iterator<E> iterator, Function<? super E, T> map) {
    this.iterator = Objects.requireNonNull(iterator);
    this.map = map;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return map.apply(iterator.next());
  }

}