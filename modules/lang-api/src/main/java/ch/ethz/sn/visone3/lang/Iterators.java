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

package ch.ethz.sn.visone3.lang;

import ch.ethz.sn.visone3.lang.spi.IteratorFacade;
import ch.ethz.sn.visone3.lang.spi.LangProvider;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Provides some commonly required types of iterators and iterables.
 *
 */
public final class Iterators {

  private Iterators() {
  }

  private static IteratorFacade facade() {
    return LangProvider.getInstance().iterators();
  }

  // public static <T> Iterator<T> emptyIterator() {
  // return Collections.emptyIterator();
  // }

  /**
   * Returns an empty integer iterator.
   * 
   * @return the empty integer iterator.
   */
  public static PrimitiveIterator.OfInt emptyIteratorInt() {
    return EmptyIntIterator.EMPTY_ITR;
  }

  /**
   * Returns an empty integer iterable.
   * 
   * @return the integer iterable.
   */
  public static PrimitiveIterable.OfInt emptyInt() {
    return EmptyIntIterator.EMPTY_ITL;
  }

  /**
   * Returns an iterator over a single integer.
   * 
   * @param value
   *          the integer value
   * @return iterator over the integer value
   */
  public static PrimitiveIterator.OfInt singletonItrInt(final int value) {
    return new PrimitiveIterator.OfInt() {
      boolean hasNext = true;

      @Override
      public int nextInt() {
        if (!hasNext) {
          throw new NoSuchElementException();
        }
        hasNext = false;
        return value;
      }

      @Override
      public boolean hasNext() {
        return hasNext;
      }
    };
  }

  /**
   * Returns an iterable over a single integer.
   * 
   * @param value
   *          the integer value
   * @return iterable over the integer value
   */
  public static PrimitiveIterable.OfInt singletonInt(final int value) {
    return () -> singletonItrInt(value);
  }

  /**
   * Empty iterator implementation.
   */
  private static class EmptyIntIterator implements PrimitiveIterator.OfInt {

    private static final PrimitiveIterator.OfInt EMPTY_ITR = new EmptyIntIterator();
    private static final PrimitiveIterable.OfInt EMPTY_ITL = () -> EMPTY_ITR;

    @Override
    public int nextInt() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean hasNext() {
      return false;
    }
  }

  /**
   * Derives a view of an iterator retaining all elements for which {@code filter} produces true.
   * 
   * @param iterator
   *          the iterator
   * @param filter
   *          the filter predicate
   * @return the filtered iterator.
   */
  public static <T> Iterator<T> filter(Iterator<T> iterator, Predicate<? super T> filter) {
    return facade().filter(iterator, filter);
  }

  /**
   * Derives a view of an iterable retaining all elements for which {@code filter} produces true.
   * 
   * @param iterable
   *          the iterable
   * @param filter
   *          the filter predicate
   * @return the filtering view
   */
  public static <T> Iterable<T> filter(Iterable<T> iterable, Predicate<? super T> filter) {
    return facade().filter(iterable, filter);
  }

  /**
   * Derives a view of an iterator retaining all elements for which {@code filter} produces true.
   * 
   * @param iterator
   *          the iterator
   * @param filter
   *          the filter predicate
   * @return the filtering view
   */
  public static <T> PrimitiveIterator.OfInt filter(PrimitiveIterator.OfInt iterator,
      IntPredicate filter) {
    return facade().filter(iterator, filter);
  }

  /**
   * Derives a view of an iterable retaining all elements for which {@code filter} produces true.
   * 
   * @param iterable
   *          the iterable
   * @param filter
   *          the filter predicate
   * @return the filtering view
   */
  public static <T> PrimitiveIterable.OfInt filter(PrimitiveIterable.OfInt iterable,
      IntPredicate filter) {
    return facade().filter(iterable, filter);
  }

  /**
   * Derives a view of an iterator mapping each of its element with the supplied function.
   * 
   * @param iterator
   *          the iterator
   * @param map
   *          the mapping function
   * @return the mapping view of the iterator
   */
  public static <T, R> Iterator<R> map(Iterator<T> iterator, Function<? super T, R> map) {
    return facade().map(iterator, map);
  }

  /**
   * Derives a view of an iterable mapping each of its element with the supplied function.
   * 
   * @param iterable
   *          the iterable
   * @param map
   *          the mapping function
   * @return the mapping view of the iterable
   */
  public static <T, R> Iterable<R> map(Iterable<T> iterable, Function<? super T, R> map) {
    return facade().map(iterable, map);
  }

  /**
   * Concatenates two iterators into a single one.
   * 
   * @param first
   *          the first iterator
   * @param second
   *          the second iterator
   * @return the concatenated iterator
   */
  public static <T> Iterator<T> concat(Iterator<? extends T> first, Iterator<? extends T> second) {
    return facade().concat(first, second);
  }

  /**
   * Concatenates two iterables into a single one.
   * 
   * @param first
   *          the first iterable
   * @param second
   *          the second iterable
   * @return the concatenated iterable
   */
  public static <T> Iterable<T> concat(Iterable<? extends T> first, Iterable<? extends T> second) {
    return facade().concat(first, second);
  }

  /**
   * Concatenates two integer iterators into a single one.
   * 
   * @param first
   *          the first iterator
   * @param second
   *          the second iterator
   * @return the concatenated iterator
   */
  public static PrimitiveIterator.OfInt concat(PrimitiveIterator.OfInt first,
      PrimitiveIterator.OfInt second) {
    return facade().concat(first, second);
  }

  /**
   * Concatenates two integer iterables into a single one.
   * 
   * @param first
   *          the first iterable
   * @param second
   *          the second iterable
   * @return the concatenated iterable
   */
  public static PrimitiveIterable.OfInt concat(PrimitiveIterable.OfInt first,
      PrimitiveIterable.OfInt second) {
    return facade().concat(first, second);
  }

}
