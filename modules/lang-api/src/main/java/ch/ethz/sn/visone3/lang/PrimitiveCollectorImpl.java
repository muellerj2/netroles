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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/**
 * Simple primitive collector implementations that are used by the
 * {@code PrimitiveCollector(.OfInt/Long/Double).of()} family of static methods.
 * 
 * @param <T>
 *          the type of input elements to the reduction operation
 * @param <A>
 *          the mutable accumulation type of the reduction operation (often hidden as an
 *          implementation detail)
 * @param <R>
 *          the result type of the reduction operation
 */
final class PrimitiveCollectorImpl<T, A, R> implements PrimitiveCollector<T, A, R> {

  private final Supplier<A> supplier;
  private final BiConsumer<A, T> accumulator;
  private final BiConsumer<A, A> combiner;
  private final Function<A, R> finisher;
  private final Class<R> resultType;
  private final Set<Characteristics> characteristics;

  public PrimitiveCollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator,
      BiConsumer<A, A> combiner, Function<A, R> finisher, Class<R> resultType,
      Set<Characteristics> characteristics) {
    this.supplier = supplier;
    this.accumulator = accumulator;
    this.combiner = combiner;
    this.finisher = finisher;
    this.resultType = resultType;
    this.characteristics = characteristics;
  }

  @Override
  public Supplier<A> supplier() {
    return supplier;
  }

  @Override
  public BiConsumer<A, T> accumulator() {
    return accumulator;
  }

  @Override
  public Function<A, R> finisher() {
    return finisher;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return characteristics;
  }

  @Override
  public BiConsumer<A, A> consumingCombiner() {
    return combiner;
  }

  @Override
  public Class<R> resultType() {
    return resultType;
  }

  static final class OfInt<A, R> implements PrimitiveCollector.OfInt<A, R> {

    private final Supplier<A> supplier;
    private final ObjIntConsumer<A> accumulator;
    private final BiConsumer<A, A> combiner;
    private final Function<A, R> finisher;
    private final Class<R> resultType;
    private final Set<Characteristics> characteristics;

    public OfInt(Supplier<A> supplier, ObjIntConsumer<A> accumulator, BiConsumer<A, A> combiner,
        Function<A, R> finisher, Class<R> resultType, Set<Characteristics> characteristics) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
      this.finisher = finisher;
      this.resultType = resultType;
      this.characteristics = characteristics;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public ObjIntConsumer<A> intAccumulator() {
      return accumulator;
    }

    @Override
    public Function<A, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }

    @Override
    public BiConsumer<A, A> consumingCombiner() {
      return combiner;
    }

    @Override
    public Class<R> resultType() {
      return resultType;
    }
  }

  static final class OfLong<A, R> implements PrimitiveCollector.OfLong<A, R> {

    private final Supplier<A> supplier;
    private final ObjLongConsumer<A> accumulator;
    private final BiConsumer<A, A> combiner;
    private final Function<A, R> finisher;
    private final Class<R> resultType;
    private final Set<Characteristics> characteristics;

    public OfLong(Supplier<A> supplier, ObjLongConsumer<A> accumulator, BiConsumer<A, A> combiner,
        Function<A, R> finisher, Class<R> resultType, Set<Characteristics> characteristics) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
      this.finisher = finisher;
      this.resultType = resultType;
      this.characteristics = characteristics;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public ObjLongConsumer<A> longAccumulator() {
      return accumulator;
    }

    @Override
    public Function<A, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }

    @Override
    public BiConsumer<A, A> consumingCombiner() {
      return combiner;
    }

    @Override
    public Class<R> resultType() {
      return resultType;
    }
  }

  static final class OfDouble<A, R> implements PrimitiveCollector.OfDouble<A, R> {

    private final Supplier<A> supplier;
    private final ObjDoubleConsumer<A> accumulator;
    private final BiConsumer<A, A> combiner;
    private final Function<A, R> finisher;
    private final Class<R> resultType;
    private final Set<Characteristics> characteristics;

    public OfDouble(Supplier<A> supplier, ObjDoubleConsumer<A> accumulator,
        BiConsumer<A, A> combiner, Function<A, R> finisher, Class<R> resultType,
        Set<Characteristics> characteristics) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
      this.finisher = finisher;
      this.resultType = resultType;
      this.characteristics = characteristics;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public ObjDoubleConsumer<A> doubleAccumulator() {
      return accumulator;
    }

    @Override
    public Function<A, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }

    @Override
    public BiConsumer<A, A> consumingCombiner() {
      return combiner;
    }

    @Override
    public Class<R> resultType() {
      return resultType;
    }
  }

  static Set<Characteristics> toSet(Characteristics[] characteristics) {
    if (characteristics.length == 0) {
      return Collections.emptySet();
    } else {
      return Collections.unmodifiableSet(EnumSet.of(characteristics[0], characteristics));
    }
  }

  static Set<Characteristics> toSet(Characteristics first, Characteristics[] characteristics) {
    return Collections.unmodifiableSet(EnumSet.of(first, characteristics));
  }
}
