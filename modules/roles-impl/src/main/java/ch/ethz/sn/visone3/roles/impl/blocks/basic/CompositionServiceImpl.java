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
import ch.ethz.sn.visone3.roles.blocks.Reducer;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;
import ch.ethz.sn.visone3.roles.spi.CompositionService;

/**
 * Provides various methods to compose several operators together and derive more complex kinds of
 * operators.
 *
 */
public final class CompositionServiceImpl implements CompositionService {

  @Override
  public <T, U> RoleOperator<T> adapt(RoleOperator<U> inner, Operator<T, ? extends U> to,
      RoleConverter<? super U, T> back) {
    return new RoleAdapter<>(inner, to, back);
  }

  @Override
  public <T, U> RoleOperator<T> adapt(Operator<T, ? extends U> operator,
      RoleConverter<U, T> translator) {
    return new GenericToRoleOperatorAdapter<>(operator, translator);
  }

  @Override
  public <T, U, V> Operator<T, V> adaptOperator(Operator<T, ? extends U> first,
      Operator<U, V> second) {
    return new GenericOperatorAdapter<>(first, second);
  }

  @Override
  public <T, U, V> RoleConverter<T, V> adaptConverter(Operator<T, ? extends U> first,
      RoleConverter<U, V> second) {
    return new GenericRoleConverterAdapter<>(first, second);
  }

  @Override
  public <T> RoleOperator<T> series(RoleOperator<T> first, RoleOperator<T>[] rest) {
    RoleOperator<T> current = first;
    for (RoleOperator<T> op : rest) {
      current = new RoleSequence<>(current, op);
    }
    return current;
  }

  @Override
  public <T> RoleOperator<T> parallel(RoleReducer<T> combiner, RoleOperator<T> first,
      RoleOperator<T>[] rest) {
    if (combiner.isAssociative()) {
      return parallelAssociative(combiner, first, rest);
    }
    RoleOperator<T> current = first;
    for (RoleOperator<T> op : rest) {
      current = new RoleFork<>(current, op, combiner);
    }
    return current;

  }

  @SafeVarargs
  private static <T> RoleOperator<T> parallelAssociative(RoleReducer<T> combiner,
      RoleOperator<T> first, RoleOperator<T>... rest) {
    if (combiner.isCommutative()) {
      return parallelCommutative(combiner, first, rest);
    }
    RoleOperator<T> constantOp = null, nonconstantOp = null;
    if (first.isConstant()) {
      constantOp = first;
    } else {
      nonconstantOp = first;
    }
    int nOperatorsLeft = rest.length;
    for (RoleOperator<T> op : rest) {
      --nOperatorsLeft;
      if (op.isConstant()) {
        constantOp = (constantOp != null) ? new LazyRoleFork<>(constantOp, op, combiner) : op;
      } else {
        if (constantOp != null) {
          nonconstantOp = (nonconstantOp != null)
              ? new LazyRoleFork<>(nonconstantOp, constantOp, combiner)
              : constantOp;
          constantOp = null;
        }
        if (nOperatorsLeft > 0) {
          nonconstantOp = new LazyRoleFork<>(nonconstantOp, op, combiner);
        } else {
          nonconstantOp = new RoleFork<>(nonconstantOp, op, combiner);
        }
      }
    }
    if (constantOp != null) {
      nonconstantOp = (nonconstantOp != null) ? new RoleFork<>(nonconstantOp, constantOp, combiner)
          : constantOp;
    }
    return nonconstantOp;
  }

  @SafeVarargs
  private static <T> RoleOperator<T> parallelCommutative(RoleReducer<T> combiner,
      RoleOperator<T> first, RoleOperator<T>... rest) {
    RoleOperator<T> constantOp = null, nonconstantOp = null;
    if (first.isConstant()) {
      constantOp = first;
    } else {
      nonconstantOp = first;
    }
    int nOperatorsLeft = rest.length;
    for (RoleOperator<T> op : rest) {
      --nOperatorsLeft;
      if (op.isConstant()) {
        constantOp = (constantOp != null) ? new RoleFork<>(constantOp, op, combiner) : op;
      } else {
        if (nOperatorsLeft != 0 || constantOp != null) {
          nonconstantOp = (nonconstantOp != null) ? new LazyRoleFork<>(nonconstantOp, op, combiner)
              : op;
        } else {
          nonconstantOp = new RoleFork<>(nonconstantOp, op, combiner);
        }
      }
    }
    if (nonconstantOp == null) {
      nonconstantOp = constantOp;
      constantOp = null;
    }
    if (constantOp != null) {
      return new RoleFork<>(constantOp, nonconstantOp, combiner);
    }
    return nonconstantOp;
  }

  @Override
  public <T, U> Operator<T, U> parallel(Reducer<U> combiner, Operator<T, U> first,
      Operator<T, U>[] rest) {
    if (combiner.isAssociative()) {
      return parallelAssociative(combiner, first, rest);
    }
    Operator<T, U> current = first;
    for (Operator<T, U> op : rest) {
      current = new GenericFork<>(current, op, combiner);
    }
    return current;
  }

  @SafeVarargs
  private static <T, U> Operator<T, U> parallelAssociative(Reducer<U> combiner,
      Operator<T, U> first, Operator<T, U>... rest) {
    if (combiner.isCommutative()) {
      return parallelCommutative(combiner, first, rest);
    }
    Operator<T, U> constantOp = null, nonconstantOp = null;
    if (first.isConstant()) {
      constantOp = first;
    } else {
      nonconstantOp = first;
    }
    for (Operator<T, U> op : rest) {
      if (op.isConstant()) {
        constantOp = (constantOp != null) ? new GenericFork<>(constantOp, op, combiner) : op;
      } else {
        if (constantOp != null) {
          nonconstantOp = (nonconstantOp != null)
              ? new GenericFork<>(nonconstantOp, constantOp, combiner)
              : constantOp;
          constantOp = null;
        }
        nonconstantOp = new GenericFork<>(nonconstantOp, op, combiner);
      }
    }
    if (constantOp != null) {
      nonconstantOp = (nonconstantOp != null)
          ? new GenericFork<>(nonconstantOp, constantOp, combiner)
          : constantOp;
    }
    return nonconstantOp;
  }

  @SafeVarargs
  private static <T, U> Operator<T, U> parallelCommutative(Reducer<U> combiner,
      Operator<T, U> first, Operator<T, U>... rest) {
    Operator<T, U> constantOp = null, nonconstantOp = null;
    if (first.isConstant()) {
      constantOp = first;
    } else {
      nonconstantOp = first;
    }
    for (Operator<T, U> op : rest) {
      if (op.isConstant()) {
        constantOp = (constantOp != null) ? new GenericFork<>(constantOp, op, combiner) : op;
      } else {
        nonconstantOp = (nonconstantOp != null) ? new GenericFork<>(nonconstantOp, op, combiner)
            : op;
      }
    }
    if (nonconstantOp == null) {
      nonconstantOp = constantOp;
      constantOp = null;
    }
    if (constantOp != null) {
      return new GenericFork<>(constantOp, nonconstantOp, combiner);
    }
    return nonconstantOp;
  }

}
