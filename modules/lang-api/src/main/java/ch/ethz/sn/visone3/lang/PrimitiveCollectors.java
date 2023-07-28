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

import ch.ethz.sn.visone3.lang.spi.LangProvider;
import ch.ethz.sn.visone3.lang.spi.MappingsFacade;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Provides standard implementations for {@link PrimitiveCollector}, as well as methods to apply a
 * {@link PrimitiveCollector} to streams.
 *
 */
public final class PrimitiveCollectors {
  private PrimitiveCollectors() {
  }

  private static MappingsFacade facade() {
    return LangProvider.getInstance().mappings();
  }

  /**
   * Works like {@link Stream#collect(java.util.stream.Collector)}, but for
   * {@link PrimitiveCollector} on {@link Stream}.
   */
  public static <T, A, R> R collect(Stream<T> stream, PrimitiveCollector<T, A, R> collector) {
    return collector.finisher().apply(stream.collect(collector.supplier(), collector.accumulator(),
        collector.consumingCombiner()));
  }

  /**
   * Works like {@link Stream#collect(java.util.stream.Collector)}, but for
   * {@link PrimitiveCollector.OfInt} on {@link IntStream}.
   */
  public static <A, R> R collect(IntStream stream, PrimitiveCollector.OfInt<A, R> collector) {
    return collector.finisher().apply(stream.collect(collector.supplier(),
        collector.intAccumulator(), collector.consumingCombiner()));
  }

  /**
   * Works like {@link Stream#collect(java.util.stream.Collector)}, but for
   * {@link PrimitiveCollector.OfLong} on {@link LongStream}.
   */
  public static <A, R> R collect(LongStream stream, PrimitiveCollector.OfLong<A, R> collector) {
    return collector.finisher().apply(stream.collect(collector.supplier(),
        collector.longAccumulator(), collector.consumingCombiner()));
  }

  /**
   * Works like {@link Stream#collect(java.util.stream.Collector)}, but for
   * {@link PrimitiveCollector.OfDouble} on {@link DoubleStream}.
   */
  public static <A, R> R collect(DoubleStream stream, PrimitiveCollector.OfDouble<A, R> collector) {
    return collector.finisher().apply(stream.collect(collector.supplier(),
        collector.doubleAccumulator(), collector.consumingCombiner()));
  }

  /**
   * Returns a {@link PrimitiveCollector.OfInt} that collects the input integers into a new
   * {@link PrimitiveList.OfInt}.
   * 
   * @return a {@link PrimitiveCollector.OfInt} which collects all the input integers into a
   *         {@link PrimitiveList.OfInt} in encounter order
   */
  public static PrimitiveCollector.OfInt<?, PrimitiveList.OfInt> toIntList() {
    return facade().toIntList();
  }

  /**
   * Returns a {@link PrimitiveCollector.OfLong} that collects the input longs into a new
   * {@link PrimitiveList.OfLong}.
   * 
   * @return a {@link PrimitiveCollector.OfLong} which collects all the input longs into a
   *         {@link PrimitiveList.OfLong} in encounter order
   */
  public static PrimitiveCollector.OfLong<?, PrimitiveList.OfLong> toLongList() {
    return facade().toLongList();
  }

  /**
   * Returns a {@link PrimitiveCollector.OfDouble} that collects the input integers into a new
   * {@link PrimitiveList.OfDouble}.
   * 
   * @return a {@link PrimitiveCollector.OfDouble} which collects all the input integers into a
   *         {@link PrimitiveList.OfDouble} in encounter order
   */
  public static PrimitiveCollector.OfDouble<?, PrimitiveList.OfDouble> toDoubleList() {
    return facade().toDoubleList();
  }

  /**
   * Returns a {@link PrimitiveCollector} that collects the input elements into a new
   * {@link PrimitiveList} for the supplied component type.
   * 
   * <p>
   * If the supplied component type is one of the primitive types {@code int}, {@code long}, or
   * {@code double}, returns a {@link PrimitiveCollector.OfInt}, {@link PrimitiveCollector.OfLong}
   * or {@link PrimitiveCollector.OfDouble} that collect into a {@link PrimitiveList.OfInt},
   * {@link PrimitiveList.OfLong} or {@link PrimitiveList.OfDouble}, respectively. If the component
   * type is any other primitive type, throws an exception. For any other type, returns a
   * {@link PrimitiveCollector} that collects into a {@link PrimitiveList} for that component type.
   * 
   * <p>
   * Note that the list of supported primitive component types might be extended in the future.
   * 
   * @param componentType
   *          the component type of the {@link PrimitiveList} to collect into
   * @return a {@link PrimitiveCollector} which collects all the input elements into a
   *         {@link PrimitiveList} in encounter order
   * @throws UnsupportedOperationException
   *           if the component type is primitive and not one of the supported primitive component
   *           types
   */
  public static <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toList(
      Class<T> componentType) {
    return facade().toList(componentType);
  }

  /**
   * Returns a {@link PrimitiveCollector} that collects the input elements into a new
   * {@link PrimitiveList} for the supplied component type.
   * 
   * <p>
   * If the supplied component type is one of the primitive types {@code int}, {@code long}, or
   * {@code double}, returns a {@link PrimitiveCollector.OfInt}, {@link PrimitiveCollector.OfLong}
   * or {@link PrimitiveCollector.OfDouble} that collect into a {@link PrimitiveList.OfInt},
   * {@link PrimitiveList.OfLong} or {@link PrimitiveList.OfDouble}, respectively. If the component
   * type is any other primitive type, it produces a collector for the wrapper type. For any other
   * type, returns a {@link PrimitiveCollector} that collects into a {@link PrimitiveList} for that
   * component type.
   * 
   * <p>
   * Note that the list of primitive component types with specialized primitive lists and collectors
   * might be extended in the future.
   * 
   * @param componentType
   *          the component type of the {@link PrimitiveList} to collect into
   * @return a {@link PrimitiveCollector} which collects all the input elements into a
   *         {@link PrimitiveList} in encounter order
   */
  public static <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toListAutoboxing(
      Class<T> componentType) {
    return facade().toListAutoboxing(componentType);
  }
}
