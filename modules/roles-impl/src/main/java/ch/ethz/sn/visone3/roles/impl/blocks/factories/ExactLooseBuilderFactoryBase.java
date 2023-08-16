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
package ch.ethz.sn.visone3.roles.impl.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseFactoryBase;

abstract class ExactLooseBuilderFactoryBase<T extends EquitableLooseFactoryBase<T>>
    implements EquitableLooseFactoryBase<T> {

  private boolean exact = false;

  @SuppressWarnings("unchecked")
  @Override
  public T loose() {
    exact = false;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T equitable() {
    exact = true;
    return (T) this;
  }

  protected boolean isExact() {
    return exact;
  }
}
