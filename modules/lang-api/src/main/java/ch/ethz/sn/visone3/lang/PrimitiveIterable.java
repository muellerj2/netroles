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

package ch.ethz.sn.visone3.lang;

import java.util.PrimitiveIterator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Iterable interface supporting primitive classes.
 * 
 * @param <T>
 *          Element type.
 * @param <C>
 *          Consumer type.
 * @param <I>
 *          PrimitiveIterator type.
 */
public interface PrimitiveIterable<T, C, I extends PrimitiveIterator<T, C>> extends Iterable<T> {

  /**
   * Returns an iterator over the elements of type {@code T}.
   * 
   * @return the iterator
   */
  @Override
  I iterator();

  /**
   * Performs the action for each element.
   * 
   * @param action
   *          the action
   */
  void forEach(C action);

  /**
   * PrimitiveIterable interface specifically for integers.
   */
  interface OfInt extends PrimitiveIterable<Integer, IntConsumer, PrimitiveIterator.OfInt> {
    @Override
    default void forEach(final IntConsumer action) {
      iterator().forEachRemaining(action);
    }
  }

  /**
   * PrimitiveIterable interface specifically for longs.
   */
  interface OfLong extends PrimitiveIterable<Long, LongConsumer, PrimitiveIterator.OfLong> {
    @Override
    default void forEach(final LongConsumer action) {
      iterator().forEachRemaining(action);
    }
  }

  /**
   * PrimitiveIterable interface specifically for doubles.
   */
  interface OfDouble extends PrimitiveIterable<Double, DoubleConsumer, PrimitiveIterator.OfDouble> {
    @Override
    default void forEach(final DoubleConsumer action) {
      iterator().forEachRemaining(action);
    }
  }
}
