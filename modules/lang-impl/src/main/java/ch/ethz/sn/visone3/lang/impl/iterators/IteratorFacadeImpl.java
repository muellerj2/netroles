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

import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.spi.IteratorFacade;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class IteratorFacadeImpl implements IteratorFacade {

  @Override
  public <T> Iterator<T> filter(Iterator<T> iterator, Predicate<? super T> filter) {
    return new FilterIterator<>(iterator, filter);
  }

  @Override
  public <T> Iterable<T> filter(Iterable<T> iterable, Predicate<? super T> filter) {
    return new FilterIterable<>(iterable, filter);
  }

  @Override
  public PrimitiveIterator.OfInt filter(PrimitiveIterator.OfInt iterator, IntPredicate filter) {
    return new FilterIterator.OfInt(iterator, filter);
  }

  @Override
  public PrimitiveIterable.OfInt filter(PrimitiveIterable.OfInt iterator, IntPredicate filter) {
    return new FilterIterable.OfInt(iterator, filter);
  }

  @Override
  public <T> Iterator<T> concat(Iterator<? extends T> first, Iterator<? extends T> second) {
    return new ConcatIterator<>(first, second);
  }

  @Override
  public <T> Iterable<T> concat(Iterable<? extends T> first, Iterable<? extends T> second) {
    return () -> new ConcatIterator<>(first.iterator(), second.iterator());
  }

  @Override
  public PrimitiveIterator.OfInt concat(PrimitiveIterator.OfInt first,
      PrimitiveIterator.OfInt second) {
    return new ConcatIterator.OfInt(first, second);
  }

  @Override
  public PrimitiveIterable.OfInt concat(PrimitiveIterable.OfInt first,
      PrimitiveIterable.OfInt second) {
    return () -> new ConcatIterator.OfInt(first.iterator(), second.iterator());
  }

  @Override
  public <T, R> Iterator<R> map(Iterator<T> iterator, Function<? super T, R> map) {
    return new MapIterator<>(iterator, map);
  }

  @Override
  public <T, R> Iterable<R> map(Iterable<T> iterable, Function<? super T, R> map) {
    return new MapIterable<>(iterable, map);
  }

}
