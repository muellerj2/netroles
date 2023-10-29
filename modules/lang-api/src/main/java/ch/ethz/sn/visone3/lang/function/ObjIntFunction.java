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
package ch.ethz.sn.visone3.lang.function;

/**
 * Represents a function that accepts an object and an integer and produces a
 * result.
 * 
 * @param <T> the type of the first argument.
 * @param <R> the type of the result.
 */
@FunctionalInterface
public interface ObjIntFunction<T, R> {
  /**
   * Applies a function to the given arguments.
   * 
   * @param arg1 the first argument.
   * @param arg2 the second argument.
   * @return the result of the function.
   */
  public R apply(T arg1, int arg2);
}