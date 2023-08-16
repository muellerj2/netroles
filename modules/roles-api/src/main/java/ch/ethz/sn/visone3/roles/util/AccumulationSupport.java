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
package ch.ethz.sn.visone3.roles.util;

import java.util.function.ToIntBiFunction;

import ch.ethz.sn.visone3.lang.ConstMapping;

/**
 * Provides some functions to simplify accumulating distances or penalties on
 * multiplex networks.
 *
 */
public class AccumulationSupport {

  private AccumulationSupport() {
  }

  /**
   * Represents a ternary function producing an int.
   * 
   * @param <T> first argument type.
   * @param <U> second argument type.
   * @param <V> third argument type.
   */
  public interface ToIntTriFunction<T, U, V> {
    /**
     * Applies this function to the given arguments.
     * 
     * @param t the first argument.
     * @param u the second argument.
     * @param v the third argument.
     * @return the function result.
     */
    public int applyAsInt(T t, U u, V v);
  }

  /**
   * Computes a sum of integers.
   * 
   * @param ri      the first argument to {@code f}.
   * @param rj      the second argument to {@code f}.
   * @param weights array of weights as third arguments to {@code f}.
   * @param f       the function the results of which are summed over all weights.
   * @param <T>     first and second argument type.
   * @param <U>     third argument/weight type.
   * @return the sum of {@code f.applyAsInt(ri, rj, weights[i])} over all i.
   */
  public static <T, U> int accumulate(T ri, T rj, U[] weights, ToIntTriFunction<T, T, U> f) {
    int sum = 0;
    for (U weight : weights) {
      sum += f.applyAsInt(ri, rj, weight);
    }
    return sum;
  }

  /**
   * Computes a sum of integers.
   * 
   * @param ri      the first argument to {@code f}.
   * @param rj      the second argument to {@code f}.
   * @param weights array of weights as third arguments to {@code f}.
   * @param f       the function the results of which are summed over all weights.
   * @param <T>     first and second argument type.
   * @param <U>     third argument/weight type.
   * @return the sum of {@code f.applyAsInt(ri, rj, weights.get(i))} over all i.
   */
  public static <T, U> int accumulate(T ri, T rj, ConstMapping<U> weights,
      ToIntTriFunction<T, T, U> f) {
    int sum = 0;
    for (U weight : weights) {
      sum += f.applyAsInt(ri, rj, weight);
    }
    return sum;
  }

  /**
   * Computes a sum of integers.
   * 
   * @param ri      the first argument to {@code f}.
   * @param weights array of weights as third arguments to {@code f}.
   * @param f       the function the results of which are summed over all weights.
   * @param <T>     first type.
   * @param <U>     second argument/weight type.
   * @return the sum of {@code f.applyAsInt(ri, weights[i])} over all i.
   */
  public static <T, U> int accumulate(T ri, U[] weights, ToIntBiFunction<T, U> f) {
    int sum = 0;
    for (U weight : weights) {
      sum += f.applyAsInt(ri, weight);
    }
    return sum;
  }

  /**
   * Computes a sum of integers.
   * 
   * @param ri      the first argument to {@code f}.
   * @param weights array of weights as third arguments to {@code f}.
   * @param f       the function the results of which are summed over all weights.
   * @param <T>     first type.
   * @param <U>     second argument/weight type.
   * @return the sum of {@code f.applyAsInt(ri, weights.get(i))} over all i.
   */
  public static <T, U> int accumulate(T ri, ConstMapping<U> weights, ToIntBiFunction<T, U> f) {
    int sum = 0;
    for (U weight : weights) {
      sum += f.applyAsInt(ri, weight);
    }
    return sum;
  }
}
