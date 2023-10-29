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
 * Represents a predicate that accepts two object arguments and an integer.
 * 
 * @param <T> the type of the first and second argument.
 */
@FunctionalInterface
public interface BiObjIntPredicate<T> {
  /**
   * Evaluates the predicate on the given arguments.
   * 
   * @param arg1 the first argument.
   * @param arg2 the second argument.
   * @param arg3 the third argument.
   * @return true if the arguments match the predicate, false otherwise.
   */
  public boolean test(T arg1, T arg2, int arg3);
}