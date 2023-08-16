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

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector interface for primitive type. See {@link Collector} for details on
 * the basic interface.
 * 
 * @param <T> the type of input elements to the reduction operation
 * @param <A> the mutable accumulation type of the reduction operation (often
 *            hidden as an implementation detail)
 * @param <R> the result type of the reduction operation
 */
public interface PrimitiveCollector<T, A, R> extends Collector<T, A, R> {

  /**
   * A function that accepts two partial results and merges the second into the first.
   * 
   * @return A function that combines two partial results into a combined result
   */
  BiConsumer<A, A> consumingCombiner();

  @Override
  default BinaryOperator<A> combiner() {
    return (left, right) -> {
      consumingCombiner().accept(left, right);
      return left;
    };
  }

  /**
   * Returns the class object for the result type.
   * 
   * @return the type of the result
   */
  Class<R> resultType();

  /**
   * Returns a new primitive collector described by the given {@code supplier},
   * {@code accumlator}, {@code combiner}, and {@code finisher} functions.
   * 
   * @param <T>             the type of input elements for the collector
   * @param <A>             the intermediate accumulation type of the collector
   * @param <R>             the final result type of the collector
   * @param supplier        the supplier function for the collector
   * @param accumulator     the accumulator function for the collector
   * @param combiner        the (consuming) combiner function for the collector
   * @param finisher        the finisher function for the collector
   * @param resultType      the class object describing the result type
   * @param characteristics the collector characteristics
   * @return the new primitive collector.
   */
  static <T, A, R> PrimitiveCollector<T, A, R> of(Supplier<A> supplier,
      BiConsumer<A, T> accumulator, BiConsumer<A, A> combiner, Function<A, R> finisher,
      Class<R> resultType, Characteristics... characteristics) {
    return new PrimitiveCollectorImpl<>(supplier, accumulator, combiner, finisher, resultType,
        PrimitiveCollectorImpl.toSet(characteristics));
  }

  /**
   * Returns a new primitive collector described by the given {@code supplier},
   * {@code accumlator}, and {@code combiner} functions. The collector has the
   * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
   * 
   * @param <T>             the type of input elements for the collector.
   * @param <A>             the intermediate accumulation and result type of the
   *                        collector.
   * @param supplier        the supplier function for the collector.
   * @param accumulator     the accumulator function for the collector.
   * @param combiner        the (consuming) combiner function for the collector.
   * @param resultType      the class object describing the result type.
   * @param characteristics the collector characteristics.
   * @return the new primitive collector.
   */
  static <T, A> PrimitiveCollector<T, A, A> of(Supplier<A> supplier, BiConsumer<A, T> accumulator,
      BiConsumer<A, A> combiner, Class<A> resultType, Characteristics... characteristics) {
    return new PrimitiveCollectorImpl<>(supplier, accumulator, combiner, Function.identity(),
        resultType, PrimitiveCollectorImpl.toSet(Characteristics.IDENTITY_FINISH, characteristics));
  }

  /**
   * Primitive collector interface specifically for integer input elements.
   * 
   * @param <A> the mutable accumulation type of the reduction operation (often
   *            hidden as an implementation detail)
   * @param <R> the result type of the reduction operation
   */
  interface OfInt<A, R> extends PrimitiveCollector<Integer, A, R> {

    /**
     * A function that folds an integer into a mutable accumulation container.
     * 
     * @return function that folds an integer into a mutable accumulation container
     */
    ObjIntConsumer<A> intAccumulator();

    @Override
    default BiConsumer<A, Integer> accumulator() {
      return intAccumulator()::accept;
    }

    /**
     * Returns a new integer collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation of the collector.
     * @param <R>             the result type of the collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param finisher        the finishing function for the collector producing the
     *                        result.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A, R> PrimitiveCollector.OfInt<A, R> of(Supplier<A> supplier,
        ObjIntConsumer<A> accumulator, BiConsumer<A, A> combiner, Function<A, R> finisher,
        Class<R> resultType, Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfInt<>(supplier, accumulator, combiner, finisher,
          resultType, PrimitiveCollectorImpl.toSet(characteristics));
    }

    /**
     * Returns a new integer collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation and result type of the
     *                        collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A> PrimitiveCollector.OfInt<A, A> of(Supplier<A> supplier,
        ObjIntConsumer<A> accumulator, BiConsumer<A, A> combiner, Class<A> resultType,
        Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfInt<>(supplier, accumulator, combiner,
          Function.identity(), resultType,
          PrimitiveCollectorImpl.toSet(Characteristics.IDENTITY_FINISH, characteristics));
    }
  }

  /**
   * Primitive collector interface specifically for long input elements.
   * 
   * @param <A> the mutable accumulation type of the reduction operation (often
   *            hidden as an implementation detail)
   * @param <R> the result type of the reduction operation
   */
  interface OfLong<A, R> extends PrimitiveCollector<Long, A, R> {

    /**
     * A function that folds a long integer into a mutable accumulation container.
     * 
     * @return function that folds a long integer into a mutable accumulation container
     */
    ObjLongConsumer<A> longAccumulator();

    @Override
    default BiConsumer<A, Long> accumulator() {
      return longAccumulator()::accept;
    }

    /**
     * Returns a new long integer collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation type of the collector.
     * @param <R>             the result type of the collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param finisher        the finishing function for the collector producing the
     *                        result.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A, R> PrimitiveCollector.OfLong<A, R> of(Supplier<A> supplier,
        ObjLongConsumer<A> accumulator, BiConsumer<A, A> combiner, Function<A, R> finisher,
        Class<R> resultType, Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfLong<>(supplier, accumulator, combiner, finisher,
          resultType, PrimitiveCollectorImpl.toSet(characteristics));
    }

    /**
     * Returns a new long integer collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation and result type of the
     *                        collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A> PrimitiveCollector.OfLong<A, A> of(Supplier<A> supplier,
        ObjLongConsumer<A> accumulator, BiConsumer<A, A> combiner, Class<A> resultType,
        Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfLong<>(supplier, accumulator, combiner,
          Function.identity(), resultType,
          PrimitiveCollectorImpl.toSet(Characteristics.IDENTITY_FINISH, characteristics));
    }
  }

  /**
   * Primitive collector interface specifically for long input elements.
   * 
   * @param <A> the mutable accumulation type of the reduction operation (often
   *            hidden as an implementation detail)
   * @param <R> the result type of the reduction operation
   */
  interface OfDouble<A, R> extends PrimitiveCollector<Double, A, R> {

    /**
     * A function that folds a double into a mutable accumulation container.
     * 
     * @return function that folds a double into a mutable accumulation container
     */
    ObjDoubleConsumer<A> doubleAccumulator();

    @Override
    default BiConsumer<A, Double> accumulator() {
      return doubleAccumulator()::accept;
    }

    /**
     * Returns a new double collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation of the collector.
     * @param <R>             the result type of the collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param finisher        the finishing function for the collector producing the
     *                        result.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A, R> PrimitiveCollector.OfDouble<A, R> of(Supplier<A> supplier,
        ObjDoubleConsumer<A> accumulator, BiConsumer<A, A> combiner, Function<A, R> finisher,
        Class<R> resultType, Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfDouble<>(supplier, accumulator, combiner, finisher,
          resultType, PrimitiveCollectorImpl.toSet(characteristics));
    }

    /**
     * Returns a new double collector described by the given {@code supplier},
     * {@code accumlator}, and {@code combiner} functions. The collector has the
     * {@code Collector.Characteristics.IDENTITY_FINISH} characteristic.
     * 
     * @param <A>             the intermediate accumulation and result type of the
     *                        collector.
     * @param supplier        the supplier function for the collector.
     * @param accumulator     the accumulator function for the collector.
     * @param combiner        the (consuming) combiner function for the collector.
     * @param resultType      the class object describing the result type.
     * @param characteristics the collector characteristics.
     * @return the new primitive collector.
     */
    static <A> PrimitiveCollector.OfDouble<A, A> of(Supplier<A> supplier,
        ObjDoubleConsumer<A> accumulator, BiConsumer<A, A> combiner, Class<A> resultType,
        Characteristics... characteristics) {
      return new PrimitiveCollectorImpl.OfDouble<>(supplier, accumulator, combiner,
          Function.identity(), resultType,
          PrimitiveCollectorImpl.toSet(Characteristics.IDENTITY_FINISH, characteristics));
    }
  }

}
