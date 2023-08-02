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
import java.util.Objects;
import java.util.function.Function;

final class MapIterable<E, T> implements Iterable<T> {
  private final Iterable<E> iterable;
  private final Function<? super E, T> map;

  public MapIterable(final Iterable<E> iterable, final Function<? super E, T> map) {
    this.iterable = Objects.requireNonNull(iterable);
    this.map = Objects.requireNonNull(map);
  }

  @Override
  public Iterator<T> iterator() {
    return new MapIterator<>(iterable.iterator(), map);
  }
}
