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

package ch.ethz.sn.visone3.roles.impl.blocks.basic;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;

class LazyRoleFork<T> implements RoleOperator<T> {

  public LazyRoleFork(RoleOperator<T> first, RoleOperator<T> second, RoleReducer<T> combinator) {
    this.first = first;
    this.second = second;
    this.combinator = combinator;
    this.constant = (first.isConstant() && second.isConstant()) || combinator.isConstant();
  }

  private RoleOperator<T> first, second;
  private RoleReducer<T> combinator;
  private final boolean constant;

  private T cache = null;

  @Override
  public boolean isIsotone() {
    return (first.isIsotone() && second.isIsotone() && combinator.isIsotone()) || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (first.isNonincreasing() || second.isNonincreasing() || combinator.isConstant())
        && combinator.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (first.isNondecreasing() || second.isNondecreasing() || combinator.isConstant())
        && combinator.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  private T getLeft(T in) {
    boolean ownsCache = !isConstant() && first.isConstant();
    if (ownsCache && cache != null) {
      return cache;
    }
    T leftResult = first.relative(in);
    if (ownsCache) {
      cache = leftResult;
      first.releaseCache();
    }
    return leftResult;
  }

  private T getRight(T in) {
    boolean ownsCache = !isConstant() && second.isConstant();
    if (ownsCache && cache != null) {
      return cache;
    }
    T rightResult = second.relative(in);
    if (ownsCache) {
      cache = rightResult;
      second.releaseCache();
    }
    return rightResult;
  }

  @Override
  public T relative(T in) {
    if (isConstant() && cache != null) {
      return cache;
    }
    T result = combinator.combineLazily(getLeft(in), getRight(in));
    if (isConstant()) {
      cache = result;
      first.releaseCache();
      second.releaseCache();
      combinator.releaseCache();
    }
    return result;
  }

  @Override
  public T relativeRefining(T in, T toRefine) {
    if (isConstant()) {
      return combinator.refine(toRefine, relative(in));
    }
    return combinator.refiningCombine(toRefine, getLeft(in), getRight(in));
  }

  @Override
  public T relativeCoarsening(T in, T toCoarsen) {
    if (isConstant()) {
      return combinator.coarsen(toCoarsen, relative(in));
    }
    return combinator.coarseningCombine(toCoarsen, getLeft(in), getRight(in));
  }

  @Override
  public void releaseCache() {
    cache = null;
    first.releaseCache();
    second.releaseCache();
    combinator.releaseCache();
  }
}
