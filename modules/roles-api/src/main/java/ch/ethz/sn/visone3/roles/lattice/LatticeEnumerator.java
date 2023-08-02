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

package ch.ethz.sn.visone3.roles.lattice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class LatticeEnumerator {

  private LatticeEnumerator() {

  }

  public interface ImmediateChildEnumerator<U, T extends U> extends Iterator<T> {

    public boolean isThereAncestorWhichIsImmediateChildProducedBefore(U val,
        U mustBeProducedBefore);
  }

  public static <T> Iterable<T> enumerateLattice(UnaryOperator<T> closureOrInterior,
      Supplier<T> initial, Function<T, ImmediateChildEnumerator<T, ? extends T>> enumeratorFactory,
      Predicate<T> skipElement) {
    return () -> new Iterator<T>() {

      private boolean first = true;
      private T nextValue = null;

      List<T> activePath = new ArrayList<>();
      List<ImmediateChildEnumerator<T, ? extends T>> activePathEnumerators = new ArrayList<>();
      List<ImmediateChildEnumerator<T, ? extends T>> processedEnumeratorList = new ArrayList<>();
      List<T> processedBeforeList = new ArrayList<>();
      List<T> processingChildrenList = new ArrayList<>();

      @Override
      public boolean hasNext() {
        if (nextValue == null) {
          findNext();
        }
        return nextValue != null;
      }

      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        T result = nextValue;
        nextValue = null;
        return result;
      }

      private boolean isNewlyDiscoveredFixedPoint(T fixedPoint) {
        int length = processedEnumeratorList.size();
        for (int i = 0; i < length; ++i) {
          ImmediateChildEnumerator<T, ? extends T> enumerator = processedEnumeratorList.get(i);
          T processedBefore = processedBeforeList.get(i);
          if (enumerator.isThereAncestorWhichIsImmediateChildProducedBefore(fixedPoint,
              processedBefore)) {
            return false;
          }
        }
        return true;
      }

      private void findNext() {
        boolean forward = false;
        if (first) {
          activePath.add(closureOrInterior.apply(initial.get()));
          first = false;
          forward = true;
        }
        mainloop:
        for (;;) {
          if (activePath.isEmpty()) {
            return;
          }
          if (forward) {
            // setup enumerators for newly found fixed points and output these immediately
            T fixedPoint = activePath.get(activePath.size() - 1);
            activePathEnumerators.add(enumeratorFactory.apply(fixedPoint));
            processingChildrenList.add(null);
            nextValue = fixedPoint;
            return;
          }
          if (Thread.currentThread().isInterrupted()) {
            return;
          }
          ImmediateChildEnumerator<T, ? extends T> immediateChildEnumerator = activePathEnumerators
              .get(activePathEnumerators.size() - 1);
          while (immediateChildEnumerator.hasNext()) {
            T child = immediateChildEnumerator.next();
            T nearestFixedPoint = closureOrInterior.apply(child);
            processingChildrenList.set(processingChildrenList.size() - 1, child);
            boolean skip = false;
            if (isNewlyDiscoveredFixedPoint(nearestFixedPoint)
                && !(skip = skipElement.test(nearestFixedPoint))) {
              activePath.add(nearestFixedPoint);
              forward = true;
              continue mainloop;
            }
            if (skip) {
              if (!processedEnumeratorList.isEmpty() && processedEnumeratorList
                  .get(processedEnumeratorList.size() - 1) == immediateChildEnumerator) {
                processedBeforeList.set(processedBeforeList.size() - 1,
                    processingChildrenList.get(processingChildrenList.size() - 1));
              } else {
                processedEnumeratorList.add(immediateChildEnumerator);
                processedBeforeList
                    .add(processingChildrenList.get(processingChildrenList.size() - 1));
              }
            }
            if (Thread.currentThread().isInterrupted()) {
              return;
            }
          }
          // done processing immediate children of currently active fixed point
          // so bracktrack
          if (!processedEnumeratorList.isEmpty() && processedEnumeratorList
              .get(processedEnumeratorList.size() - 1) == activePathEnumerators
                  .get(activePathEnumerators.size() - 1)) {
            processedEnumeratorList.remove(processedEnumeratorList.size() - 1);
            processedBeforeList.remove(processedBeforeList.size() - 1);
          }
          activePath.remove(activePath.size() - 1);
          activePathEnumerators.remove(activePathEnumerators.size() - 1);
          processingChildrenList.remove(processingChildrenList.size() - 1);
          // correctly update the processed lattice regions
          if (!processingChildrenList.isEmpty()) {
            ImmediateChildEnumerator<T, ? extends T> previousEnumerator = activePathEnumerators
                .get(activePathEnumerators.size() - 1);
            if (!processedEnumeratorList.isEmpty() && processedEnumeratorList
                .get(processedEnumeratorList.size() - 1) == previousEnumerator) {
              processedBeforeList.set(processedBeforeList.size() - 1,
                  processingChildrenList.get(processingChildrenList.size() - 1));
            } else {
              processedEnumeratorList.add(previousEnumerator);
              processedBeforeList
                  .add(processingChildrenList.get(processingChildrenList.size() - 1));
            }
          }
        }
      }
    };
  }

}
