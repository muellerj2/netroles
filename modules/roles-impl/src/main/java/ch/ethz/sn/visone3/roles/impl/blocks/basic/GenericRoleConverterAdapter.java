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

class GenericRoleConverterAdapter<T, U, V> implements RoleConverter<T, V> {

  public GenericRoleConverterAdapter(Operator<T, ? extends U> inner,
      RoleConverter<U, V> converter) {
    this.inner = inner;
    this.converter = converter;
    this.constant = inner.isConstant() || converter.isConstant();
  }

  private Operator<T, ? extends U> inner;
  private RoleConverter<U, V> converter;
  private final boolean constant;
  private U innerCache = null;
  private V cache = null;

  @Override
  public boolean isIsotone() {
    return (inner.isIsotone() && converter.isIsotone()) || isConstant();
  }

  @Override
  public boolean isNonincreasing() {
    return (inner.isNonincreasing() || converter.isConstant()) && converter.isNonincreasing();
  }

  @Override
  public boolean isNondecreasing() {
    return (inner.isNondecreasing() || converter.isConstant()) && converter.isNondecreasing();
  }

  @Override
  public boolean isConstant() {
    return constant;
  }

  private U applyInner(T in) {
    if (innerCache != null) {
      return innerCache;
    }
    U innerResult = inner.apply(in);
    if (isConstant()) {
      innerCache = innerResult;
    }
    return innerResult;
  }

  @Override
  public V convert(T in) {
    if (cache != null) {
      return cache;
    }
    V result = converter.convert(applyInner(in));
    if (isConstant()) {
      cache = result;
      inner.releaseCache();
      converter.releaseCache();
    }
    return result;
  }

  @Override
  public V convertRefining(T in, V toRefine) {
    return converter.convertRefining(applyInner(in), toRefine);
  }

  @Override
  public V convertCoarsening(T in, V toCoarsen) {
    return converter.convertCoarsening(applyInner(in), toCoarsen);
  }

  @Override
  public void releaseCache() {
    innerCache = null;
    cache = null;
    inner.releaseCache();
    converter.releaseCache();
  }

}
