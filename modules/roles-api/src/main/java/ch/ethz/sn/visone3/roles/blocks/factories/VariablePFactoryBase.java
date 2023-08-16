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
 * This base interface defines the signature of the methods to configure an
 * intermediate level of strictness between the ``loose'' and ``equitable''
 * types of matching.
 * 
 * @param <T> the full type of the factory.
 */
public interface VariablePFactoryBase<T extends VariablePFactoryBase<T>>
    extends EquitableLooseFactoryBase<T> {

  /**
   * Sets that the configured role notion is supposed to be of the specified
   * degree of strictness. A degree of strictness p means that k ties of one node
   * can match up to p*k ties of the other node without resulting in any
   * substitution/matching error, as long as these ties could be matched based on
   * all other criteria.
   *
   * @param p the degree of strictness.
   * @return this factory.
   */
  T strictness(int p);
}
