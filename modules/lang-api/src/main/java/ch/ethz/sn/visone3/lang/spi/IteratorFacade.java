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
package ch.ethz.sn.visone3.lang.spi;

import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Produces some common kinds of derived iterators and iterables. The methods should follow the
 * contract of the corresponding methods in {@link Iterators}.
 *
 */
public interface IteratorFacade {

  <T> Iterator<T> filter(Iterator<T> iterator, Predicate<? super T> filter);

  <T> Iterable<T> filter(Iterable<T> iterable, Predicate<? super T> filter);

  PrimitiveIterator.OfInt filter(PrimitiveIterator.OfInt iterator, IntPredicate filter);

  PrimitiveIterable.OfInt filter(PrimitiveIterable.OfInt iterable, IntPredicate filter);

  <T> Iterator<T> concat(Iterator<? extends T> first, Iterator<? extends T> second);

  <T> Iterable<T> concat(Iterable<? extends T> first, Iterable<? extends T> second);

  PrimitiveIterator.OfInt concat(PrimitiveIterator.OfInt first, PrimitiveIterator.OfInt second);

  PrimitiveIterable.OfInt concat(PrimitiveIterable.OfInt first, PrimitiveIterable.OfInt second);

  <T, R> Iterator<R> map(Iterator<T> iterator, Function<? super T, R> map);

  <T, R> Iterable<R> map(Iterable<T> iterable, Function<? super T, R> map);
}
