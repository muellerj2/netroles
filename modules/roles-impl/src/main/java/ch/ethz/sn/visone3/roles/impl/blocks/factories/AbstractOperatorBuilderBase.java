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

import ch.ethz.sn.visone3.roles.blocks.builders.OperatorBuilderBase;

/**
 * Implements builder functions handling comparator(-like) refinements to the
 * constructed role operator.
 * 
 * @param <T> network incidence/dyad type
 * @param <U> role structure type
 * @param <V> subclass builder type
 * @param <W> type for weak comparator
 * @param <X> type for partial order comparator
 * @param <Y> type for comparison bipredicate
 */
public abstract class AbstractOperatorBuilderBase<T, U, V extends OperatorBuilderBase<T, U, V, W, X, Y>, W, X, Y>
    implements OperatorBuilderBase<T, U, V, W, X, Y> {

  /**
   * Reference to specified weak comparator object.
   */
  protected W weakComp;
  /**
   * Reference to specified partial order comparator object.
   */
  protected X partialComp;
  /**
   * Reference to specified comparison bipredicate.
   */
  protected Y biPred;
  
  @SuppressWarnings("unchecked")
  @Override
  public V compWeak(W comp) {
    partialComp = null;
    biPred = null;
    weakComp = comp;
    return (V) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V compPartial(X comp) {
    partialComp = comp;
    biPred = null;
    weakComp = null;
    return (V) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V compPredicate(Y comp) {
    partialComp = null;
    biPred = comp;
    weakComp = null;
    return (V) this;
  }

}
