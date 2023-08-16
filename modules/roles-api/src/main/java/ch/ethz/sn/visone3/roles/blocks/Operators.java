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

package ch.ethz.sn.visone3.roles.blocks;

import ch.ethz.sn.visone3.roles.spi.CompositionLoader;
import ch.ethz.sn.visone3.roles.spi.CompositionService;

/**
 * Provides various methods to compose several operators to derive more complex kinds of operations.
 *
 */
public class Operators {

  private Operators() {
  }

  private static final CompositionService SERVICE = CompositionLoader.getInstance().getService();

  /**
   * Derives a role operator on one type of role structure from another role
   * operator on another role structure type, converting between the different
   * kinds of structural representations.
   * 
   * @param inner the role operator that should be encapsulated.
   * @param to    converts the role structure of the outer role operator to the
   *              role structure of the inner one.
   * @param back  convert from the role structure of the inner role operator to
   *              the role structure of the outer one.
   * @param <T>   the role structure type of the inner role operator.
   * @param <U>   the role structure type of the derived role operator.
   * @return the derived role operator.
   */
  public static <T, U> RoleOperator<T> adapt(RoleOperator<U> inner,
      Operator<T, ? extends U> to, RoleConverter<? super U, T> back) {
    return SERVICE.adapt(inner, to, back);
  }

  /**
   * Derives a role operator by converting the output of one operator to the input
   * role representation.
   * 
   * @param operator   the inner operator to apply
   * @param translator converts the result of the inner operator to the input role
   *                   representation
   * @param <T>        the role structure type.
   * @param <U>        the type of the intermediate data passed from operator to
   *                   converter.
   * @return the derived role operator.
   */
  public static <T, U> RoleOperator<T> composeRoleOp(Operator<T, ? extends U> operator,
      RoleConverter<U, T> translator) {
    return SERVICE.adapt(operator, translator);
  }

  /**
   * Derives a generic operator by applying two operators in succession.
   * 
   * @param first  the first operator to apply.
   * @param second the second operator to apply to the first operator's result.
   * @param <T>    the type of the input to the composed (and first) operator.
   * @param <U>    the type of the intermediate data passed from the first to the
   *               second operator.
   * @param <V>    the type of the result of the composed (and second) operator.
   * @return the derived generic operator.
   */
  public static <T, U, V> Operator<T, V> composeOp(Operator<T, ? extends U> first,
      Operator<U, V> second) {
    return SERVICE.adaptOperator(first, second);
  }

  /**
   * Derives a role converter that applies a generic operator followed by a given
   * role converter.
   * 
   * @param first  the generic operator to apply first.
   * @param second the role converter to apply second.
   * @param <T>    the input of the composed converter (and operator to apply
   *               first).
   * @param <U>    the type of the intermediate data passed from the operator to
   *               the converter.
   * @param <V>    the type of the result of the composed (and original) role
   *               converter.
   * @return the derived composed role converter
   */
  public static <T, U, V> RoleConverter<T, V> composeConv(Operator<T, ? extends U> first,
      RoleConverter<U, V> second) {
    return SERVICE.adaptConverter(first, second);
  }

  /**
   * Applies several role operators in succession.
   * 
   * @param first the first role operator to apply.
   * @param rest  the remaining ones to apply in succession.
   * @param <T>   the role structure type.
   * @return the composed role operator.
   */
  @SafeVarargs
  public static <T> RoleOperator<T> series(RoleOperator<T> first, RoleOperator<T>... rest) {
    return SERVICE.series(first, rest);
  }

  /**
   * Applies several role operators in parallel, i.e., computes
   * {@code op(...op(op(R1(in), R2(in)), R3(in))..., Rn(in))}, where {@code in}
   * denotes the input role structure.
   * 
   * <p>
   * Note that the resulting operator might bracket, order and combine the base
   * role operators differently for efficiency reasons, if the properties of the
   * reducer allow for it.
   * 
   * @param reducer used to combine the results of the parallel executions into
   *                one result.
   * @param first   the first role operator to apply.
   * @param rest    the remaining ones to apply in parallel.
   * @param <T>     the role structure type.
   * @return the composed role operator.
   */
  @SafeVarargs
  public static <T> RoleOperator<T> parallel(RoleReducer<T> reducer, RoleOperator<T> first,
      RoleOperator<T>... rest) {
    return SERVICE.parallel(reducer, first, rest);
  }

  /**
   * Applies several generic operators in parallel, i.e., computes
   * {@code op(...op(op(G1(in), G2(in)), G3(in))..., Gn(in))}, where {@code in}
   * denotes the common input.
   * 
   * <p>
   * Note that the resulting operator might bracket, order and combine the base
   * operators differently for efficiency reasons, if the properties of the
   * combiner allow for it.
   * 
   * @param reducer used to combine the results of the parallel executions into
   *                one result.
   * @param first   the first operator to apply.
   * @param rest    the remaining ones to apply in parallel.
   * @param <T>     the input type of the operators.
   * @param <U>     the result type of the composed operator and the individual
   *                operators as well as the type of input the reducer acts on.
   * @return the composed generic operator.
   */
  @SafeVarargs
  public static <T, U> Operator<T, U> parallel(Reducer<U> reducer,
      Operator<T, U> first, Operator<T, U>... rest) {
    return SERVICE.parallel(reducer, first, rest);
  }
}
