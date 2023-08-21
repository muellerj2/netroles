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

/**
 * This class implements an algorithm to search a lattice for fixed points of a
 * non-increasing or non-decreasing monotone function. By non-increasing, we
 * mean that {@code f(x) &lt;= x} for all x, and by non-decreasing, we man
 * {@code f(x) &gt;= x} for all x.
 * 
 * <p>
 * The algorithm performs significantly better than conventional brute force on
 * the lattices of binary relations, equivalences and rankings, as long as the
 * fixed points are few among the elements in the lattice. It also provides
 * better worst-case guarantees on the time spent until finding the next fixed
 * point than brute force.
 * 
 * <p>
 * For a description of the algorithm, see
 * 
 * <p>
 * Julian Müller (2023). Enumerating Tarski fixed points of binary relations.
 * arXiv:2308.07923.
 * 
 * <p>
 * This algorithm is an optimized and improved version of the algorithm
 * described in:
 * 
 * <p>
 * Federico Echenique (2007). Finding all equilibria in games of strategic
 * complements. Journal of Economic Theory 135(1):514-532.
 * doi:10.1016/j.jet.2006.06.001
 */
public class FixedPointEnumerator {

  private FixedPointEnumerator() {

  }

  /**
   * Enhanced enumerator for (lower or upper) covers of a lattice element. Besides
   * enumeration of such covers, it also allows to check for any descendant of the
   * element whether it is a descendant of a cover of the lattice element that has
   * been enumerated earlier.
   * 
   * @param <U> cover type needed for checking.
   * @param <T> cover type produced by iteration (subtype of the type for
   *            checking).
   */
  public interface CoverEnumerator<U, T extends U> extends Iterator<T> {

    /**
     * Checks if the lattice element {@code val} is the descendant of a cover that
     * is produced by the enumeration before {@code mustBeProducedBefore}.
     * 
     * @param val                  a descendant of the parent of enumerated covers.
     * @param mustBeProducedBefore a cover of the parent.
     * @return true if {@code val} is a descendant of a cover of the parent which is
     *         produced by this enumeration before {@code mustBeProducedBefore}.
     */
    public boolean isThereAncestorWhichIsCoverProducedBefore(U val,
        U mustBeProducedBefore);
  }

  /**
   * Constructs an iterable to enumerate the fixed point lattice of a
   * non-increasing or non-decreasing monotone function.
   * 
   * @param <T>               the type of lattice elements.
   * @param monotoneFunction  non-increasing or non-decreasing monotone function.
   * @param initial           the initial lattice element to start the search for
   *                          fixed points among its descendants from.
   * @param enumeratorFactory factory used to construct enumerators for covers
   *                          (lower covers if function is non-increasing, upper
   *                          covers if non-decreasing) of lattice elements.
   * @param skipElement       predicate that says whether this element and its
   *                          descendants should be skipped during the fixed point
   *                          search.
   * @return an iterable that enumerates all fixed points of
   *         {@code monotoneFunction} on the lattices that are descendants of
   *         {@code initial} in the lattice (according to the orientation of the
   *         monotone function) and which are not skipped according to the
   *         predicate.
   */
  public static <T> Iterable<T> enumerateLattice(UnaryOperator<T> monotoneFunction,
      Supplier<T> initial, Function<T, CoverEnumerator<T, ? extends T>> enumeratorFactory,
      Predicate<T> skipElement) {
    return () -> new Iterator<T>() {

      private boolean first = true;
      private T nextValue = null;

      List<T> activePath = new ArrayList<>();
      List<CoverEnumerator<T, ? extends T>> activePathEnumerators = new ArrayList<>();
      List<CoverEnumerator<T, ? extends T>> processedEnumeratorList = new ArrayList<>();
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
          CoverEnumerator<T, ? extends T> enumerator = processedEnumeratorList.get(i);
          T processedBefore = processedBeforeList.get(i);
          if (enumerator.isThereAncestorWhichIsCoverProducedBefore(fixedPoint,
              processedBefore)) {
            return false;
          }
        }
        return true;
      }

      private void findNext() {
        boolean forward = false;
        if (first) {
          activePath.add(monotoneFunction.apply(initial.get()));
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
          CoverEnumerator<T, ? extends T> coverEnumerator = activePathEnumerators
              .get(activePathEnumerators.size() - 1);
          while (coverEnumerator.hasNext()) {
            T child = coverEnumerator.next();
            T nearestFixedPoint = monotoneFunction.apply(child);
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
                  .get(processedEnumeratorList.size() - 1) == coverEnumerator) {
                processedBeforeList.set(processedBeforeList.size() - 1,
                    processingChildrenList.get(processingChildrenList.size() - 1));
              } else {
                processedEnumeratorList.add(coverEnumerator);
                processedBeforeList
                    .add(processingChildrenList.get(processingChildrenList.size() - 1));
              }
            }
            if (Thread.currentThread().isInterrupted()) {
              return;
            }
          }
          // done processing covers of currently active fixed point
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
            CoverEnumerator<T, ? extends T> previousEnumerator = activePathEnumerators
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
