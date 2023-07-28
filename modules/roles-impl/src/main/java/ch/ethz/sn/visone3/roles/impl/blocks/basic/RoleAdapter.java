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

package ch.ethz.sn.visone3.roles.impl.blocks.basic;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;

class RoleAdapter<T, U> implements RoleOperator<T> {

  public RoleAdapter(RoleOperator<U> inner, Operator<T, ? extends U> to,
      RoleConverter<? super U, T> back) {
    this.inner = inner;
    this.to = to;
    this.back = back;
    this.constant = to.isConstant() || inner.isConstant() || back.isConstant();
  }

  private RoleOperator<U> inner;
  private Operator<T, ? extends U> to;
  private RoleConverter<? super U, T> back;
  private final boolean constant;
  private U innerCache = null;
  private T cache = null;

  @Override
  public boolean isIsotone() {
    return inner.isIsotone() && to.isIsotone() && back.isIsotone() || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (to.isNonincreasing() || inner.isConstant() || back.isConstant())
        && (inner.isNonincreasing() || back.isConstant()) && back.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (to.isNondecreasing() || inner.isConstant() || back.isConstant())
        && (inner.isNondecreasing() || back.isConstant()) && back.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  private U applyInner(T in) {
    if (innerCache != null) {
      return innerCache;
    }
    U innerResult = inner.relative(to.apply(in));
    if (isConstant()) {
      innerCache = innerResult;
      to.releaseCache();
      inner.releaseCache();
    }
    return innerResult;
  }

  @Override
  public T relative(T in) {
    if (cache != null) {
      return cache;
    }
    T result = back.convert(applyInner(in));
    if (isConstant()) {
      cache = result;
      back.releaseCache();
    }
    return result;
  }

  @Override
  public T relativeRefining(T in, T toRefine) {
    return back.convertRefining(applyInner(in), toRefine);
  }

  @Override
  public T relativeCoarsening(T in, T toCoarsen) {
    return back.convertCoarsening(applyInner(in), toCoarsen);
  }

  @Override
  public void releaseCache() {
    innerCache = null;
    cache = null;
    to.releaseCache();
    inner.releaseCache();
    back.releaseCache();
  }

}
