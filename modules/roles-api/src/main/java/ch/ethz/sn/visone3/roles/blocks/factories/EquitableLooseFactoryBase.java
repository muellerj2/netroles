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
package ch.ethz.sn.visone3.roles.blocks.factories;

/**
 * This base interface defines the signatures of methods to configure whether
 * the role notion is of the ``loose'' or ``equitable'' type.
 * 
 * @param <T> the full type of the factory.
 */
public interface EquitableLooseFactoryBase<T extends EquitableLooseFactoryBase<T>> {

  /**
   * Sets that the configured role notion is supposed to be of the ``loose'' type
   * (i.e., it should not compare numbers of ties [of the same kind]).
   * 
   * @return this factory.
   */
  T loose();

  /**
   * Sets that the configured role notion is supposed to be of the ``equitable''
   * type (i.e., tie numbers must match exactly).
   * 
   * @return this factory.
   */
  T equitable();
}
