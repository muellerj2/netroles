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

import ch.ethz.sn.visone3.lang.ConstMapping;

import java.util.function.ToIntBiFunction;

/**
 * Provides some functions to simplify accumulating distances or penalties on
 * multiplex networks.
 *
 */
public class AccumulationSupport {

  private AccumulationSupport() {
  }

  public interface ToIntTriFunction<T, U, V> {
    public int applyAsInt(T t, U u, V v);
  }

  /**
   * Computes a sum of integers.
   * 
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
