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

package ch.ethz.sn.visone3.roles.impl.blocks.factories.dist;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.builders.DistanceOperatorBuilderBase;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.blocks.factories.AbstractOperatorBuilderBase;

abstract class AbstractDistanceOperatorBuilderBase<T, U, V extends DistanceOperatorBuilderBase<T, U, V, W, X, Y, Z, AA>, W, X, Y, Z, AA>
    extends AbstractOperatorBuilderBase<T, Operator<U, IntDistanceMatrix>, V, W, X, Y>
    implements DistanceOperatorBuilderBase<T, U, V, W, X, Y, Z, AA> {

  protected Z cost;
  protected AA penalty;

  @SuppressWarnings("unchecked")
  @Override
  public V substCost(Z substitutionCost) {
    penalty = null;
    cost = substitutionCost;
    return (V) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V failCost(AA substitutionCost) {
    penalty = substitutionCost;
    cost = null;
    return (V) this;
  }

  @Override
  public Operator<U, IntDistanceMatrix> make() {
    if (cost != null) {
      if (weakComp != null) {
        return makeConcreteSubstWeak(weakComp, cost);
      } else if (partialComp != null) {
        return makeConcreteSubstPartial(partialComp, cost);
      } else if (biPred != null) {
        return makeConcreteSubstPredicate(biPred, cost);
      } else {
        return makeConcreteSubst(cost);
      }
    } else if (penalty != null) {
      if (weakComp != null) {
        return makeConcreteFailWeak(weakComp, penalty);
      } else if (partialComp != null) {
        return makeConcreteFailPartial(partialComp, penalty);
      } else if (biPred != null) {
        return makeConcreteFailPredicate(biPred, penalty);
      } else {
        return makeConcreteFail(penalty);
      }
    } else {
      if (weakComp != null) {
        return makeConcreteWeak(weakComp);
      } else if (partialComp != null) {
        return makeConcretePartial(partialComp);
      } else if (biPred != null) {
        return makeConcretePredicate(biPred);
      } else {
        return makeConcrete();
      }
    }
  }

  abstract Operator<U, IntDistanceMatrix> makeConcrete();

  abstract Operator<U, IntDistanceMatrix> makeConcreteWeak(W comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcretePartial(X comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcretePredicate(Y comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcreteFail(AA penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcreteFailWeak(W comparator, AA penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcreteFailPartial(X comparator,
      AA penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcreteFailPredicate(Y comparator,
      AA penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcreteSubst(Z substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcreteSubstWeak(W comparator,
      Z substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcreteSubstPartial(X comparator,
      Z substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcreteSubstPredicate(Y comparator,
      Z substitutionCost);
}
