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

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;

class GenericToRoleOperatorAdapter<T, U> implements RoleOperator<T> {

  public GenericToRoleOperatorAdapter(Operator<T, ? extends U> innerOp,
      RoleConverter<U, T> translator) {
    this.innerOp = innerOp;
    this.translator = translator;
    this.constant = innerOp.isConstant() || translator.isConstant();
  }

  private Operator<T, ? extends U> innerOp;
  private RoleConverter<U, T> translator;
  private final boolean constant;
  private U innerCache = null;
  private T cache = null;

  @Override
  public boolean isIsotone() {
    return (innerOp.isIsotone() && translator.isIsotone()) || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (innerOp.isNonincreasing() || translator.isConstant()) && translator.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (innerOp.isNondecreasing() || translator.isConstant()) && translator.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  private U applyInner(T in) {
    if (innerCache != null) {
      return innerCache;
    }
    U innerResult = innerOp.apply(in);
    if (isConstant()) {
      innerCache = innerResult;
      innerOp.releaseCache();
    }
    return innerResult;
  }

  @Override
  public T relative(T in) {
    if (cache != null) {
      return cache;
    }
    T result = translator.convert(applyInner(in));
    if (isConstant()) {
      cache = result;
      translator.releaseCache();
    }
    return result;
  }

  @Override
  public T relativeRefining(T in, T toRefine) {
    return translator.convertRefining(applyInner(in), toRefine);
  }

  @Override
  public T relativeCoarsening(T in, T toCoarsen) {
    return translator.convertCoarsening(applyInner(in), toCoarsen);
  }

  @Override
  public void releaseCache() {
    innerCache = null;
    cache = null;
    innerOp.releaseCache();
    translator.releaseCache();
  }
}
