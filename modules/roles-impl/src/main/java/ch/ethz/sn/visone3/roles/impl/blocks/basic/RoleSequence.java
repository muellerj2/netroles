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

class RoleSequence<T> implements RoleOperator<T> {

  public RoleSequence(RoleOperator<T> first, RoleOperator<T> second) {
    this.first = first;
    this.second = second;
    this.constant = first.isConstant() || second.isConstant();
  }

  private RoleOperator<T> first, second;
  private final boolean constant;
  private T innerCache = null, cache = null;

  @Override
  public boolean isIsotone() {
    return (first.isIsotone() && second.isIsotone()) || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (first.isNonincreasing() || second.isConstant()) && second.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (first.isNondecreasing() || second.isConstant()) && second.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  private T applyInner(T in) {
    if (innerCache != null) {
      return innerCache;
    }
    T innerResult = first.relative(in);
    if (isConstant()) {
      innerCache = innerResult;
      first.releaseCache();
    }
    return innerResult;
  }

  @Override
  public T relative(T in) {
    if (cache != null) {
      return cache;
    }
    T result = second.relative(applyInner(in));
    if (isConstant()) {
      cache = result;
      second.releaseCache();
    }
    return result;
  }

  @Override
  public T relativeRefining(T in, T toRefine) {
    return second.relativeRefining(applyInner(in), toRefine);
  }

  @Override
  public T relativeCoarsening(T in, T toCoarsen) {
    return second.relativeCoarsening(applyInner(in), toCoarsen);
  }

  @Override
  public void releaseCache() {
    cache = null;
    innerCache = null;
    first.releaseCache();
    second.releaseCache();
  }
}
