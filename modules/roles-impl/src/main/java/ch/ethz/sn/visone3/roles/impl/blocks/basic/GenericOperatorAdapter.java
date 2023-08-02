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

class GenericOperatorAdapter<T, U, V> implements Operator<T, V> {

  public GenericOperatorAdapter(Operator<T, ? extends U> inner,
      Operator<U, V> transform) {
    this.inner = inner;
    this.translator = transform;
    this.constant = inner.isConstant() || translator.isConstant();
  }

  private Operator<T, ? extends U> inner;
  private Operator<U, V> translator;
  private final boolean constant;
  private V cache = null;

  @Override
  public boolean isIsotone() {
    return (inner.isIsotone() && translator.isIsotone()) || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (inner.isNonincreasing() || translator.isConstant()) && translator.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (inner.isNondecreasing() || translator.isConstant()) && translator.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  @Override
  public V apply(T in) {
    if (cache != null) {
      return cache;
    }
    V result = translator.apply(inner.apply(in));
    if (isConstant()) {
      cache = result;
      inner.releaseCache();
      translator.releaseCache();
    }
    return result;
  }

  @Override
  public void releaseCache() {
    cache = null;
    inner.releaseCache();
    translator.releaseCache();
  }
}
