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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.Reducer;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;

public interface CompositionService {

  /**
   * Derives a role operator on one type of role structure from another role operator on another
   * role structure type, converting between the different kinds of structural representations.
   * 
   * @param inner
   *          the role operator that should be encapsulated
   * @param to
   *          converts the role structure of the outer role operator to the role structure of the
   *          inner one.
   * @param back
   *          convert from the role structure of the inner role operator to the role structure of
   *          the outer one.
   * @return the derived role operator
   */
  <T, U> RoleOperator<T> adapt(RoleOperator<U> inner, Operator<T, ? extends U> to,
      RoleConverter<? super U, T> back);

  /**
   * Derives a role operator by converting the output of one operator to the input role
   * representation.
   * 
   * @param operator
   *          the inner operator to apply
   * @param translator
   *          converts the result of the inner operator to the input role representation
   * @return the derived role operator
   */
  <T, U> RoleOperator<T> adapt(Operator<T, ? extends U> operator,
      RoleConverter<U, T> translator);

  /**
   * Derives a generic operator by applying two operators in succession.
   * 
   * @param first
   *          the first operator to apply
   * @param second
   *          the second operator to apply to the first operator's result
   * @return the derived generic operator
   */
  <T, U, V> Operator<T, V> adaptOperator(Operator<T, ? extends U> first,
      Operator<U, V> second);

  /**
   * Derives a role converter that applies a generic operator followed by a given role converter.
   * 
   * @param first
   *          the generic operator to apply first
   * @param second
   *          the role converter to apply second
   * @return the derived composed role converter
   */
  <T, U, V> RoleConverter<T, V> adaptConverter(Operator<T, ? extends U> first,
      RoleConverter<U, V> second);

  /**
   * Applies several role operators in succession.
   * 
   * @param first
   *          the first role operator to apply
   * @param rest
   *          the remaining ones to apply in succession
   * @return the composed role operator
   */
  <T> RoleOperator<T> series(RoleOperator<T> first, RoleOperator<T>[] rest);

  /**
   * Applies several role operators in parallel, i.e., computes
   * {@code op(...op(op(R1(in), R2(in)), R3(in))..., Rn(in))}, where {@code in} denotes the input
   * role structure.
   * 
   * <p>
   * Note that the resulting operator might bracket, order and combine the base role operators
   * differently for efficiency reasons, if the properties of the reducer allow for it.
   * 
   * @param reducer
   *          used to combine the results of the parallel executions into one result
   * @param first
   *          the first role operator to apply
   * @param rest
   *          the remaining ones to apply in parallel
   * @return the composed role operator
   */
  <T> RoleOperator<T> parallel(RoleReducer<T> reducer, RoleOperator<T> first,
      RoleOperator<T>[] rest);

  /**
   * Applies several generic operators in parallel, i.e., computes
   * {@code op(...op(op(G1(in), G2(in)), G3(in))..., Gn(in))}, where {@code in} denotes the common
   * input.
   * 
   * <p>
   * Note that the resulting operator might bracket, order and combine the base operators
   * differently for efficiency reasons, if the properties of the combiner allow for it.
   * 
   * @param combiner
   *          used to combine the results of the parallel executions into one result
   * @param first
   *          the first operator to apply
   * @param rest
   *          the remaining ones to apply in parallel
   * @return the composed generic operator
   */
  <T, U> Operator<T, U> parallel(Reducer<U> combiner,
      Operator<T, U> first, Operator<T, U>[] rest);
}
