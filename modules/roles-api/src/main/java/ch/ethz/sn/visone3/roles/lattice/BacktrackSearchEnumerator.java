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
import java.util.function.Supplier;

/**
 * This enumeration algorithm searches fixed points of a monotone function on a
 * specific underlying lattice by exploiting that the elements of a lattice can
 * be decomposed into several dimensions, so that projections of a lattice
 * element to a given set of dimensions can be obtained as well as extensions of
 * a given projection to more dimensions, or the set of extremal extensions of a
 * projection to full lattice elements under the lattice ordering can be listed.
 * 
 * <p>
 * This enumeration algorithm thus needs at least the following ingredients
 * besides the monotone function f:
 * <ul>
 * <li>a way to decompose lattice elements into several dimensions, including an
 * easy way to project to a lower number of dimensions,</li>
 * <li>an iterable that produces all extensions of a projection by another
 * dimension, as well as</li>
 * <li>an iterable over all extremal extensions of a projection to full lattice
 * elements under the lattice ordering.</li>
 * </ul>
 * 
 * Fixed points are then essentially found using the following algorithm for
 * monotone function f:
 * 
 * <pre>
 * function search(projection, nextdim, maxdim)
 *   if nextdim &gt; maxdim then
 *     if f(projection) == projection then
 *       output projection;
 *     end if
 *   else
 *     for all extensions e of projection by dimension nextdim do
 *       if there is an extremal extension m of e to a full lattice element
 *           such that e is a projection of f(m) then
 *         search(e, nextdim + 1, maxdim);
 *       end if
 *     end for
 *   end if
 * end function
 * 
 * // initial call to start enumeration 
 * search(0-dimension projection, 1, n)
 * </pre>
 * 
 * Generally, the algorithm has to do little bookkeeping, and it performs well
 * under two conditions:
 * <ul>
 * <li>if it is easy to generate another extension of a projection by a single
 * dimension, and</li>
 * <li>if there are only few extremal extensions of a projection to a full
 * lattice element, and these are easy to generate.</li>
 * </ul>
 * 
 * These conditions tend to be satisfied if the meet (if f is increasing
 * monotone) or join (if f is decreasing monotone) act on the decomposition of
 * lattice elements into dimensions essentially dimension-wise. For example,
 * this is the case on the lattice of binary relations, where meet and join
 * correspond to set intersection/union (if binary relations are modeled as sets
 * of pairs) or component-wise minimum/maximum (if binary relations are
 * represented as binary matrices).
 */
public class BacktrackSearchEnumerator {

  private BacktrackSearchEnumerator() {

  }

  /**
   * Represents a function that accepts an object and an integer and produces a
   * result.
   * 
   * @param <T> the type of the first argument.
   * @param <R> the type of the result.
   */
  @FunctionalInterface
  public interface ObjIntFunction<T, R> {
    /**
     * Applies a function to the given arguments.
     * 
     * @param arg1 the first argument.
     * @param arg2 the second argument.
     * @return the result of the function.
     */
    public R apply(T arg1, int arg2);
  }

  /**
   * Represents a predicate that accepts an object and an integer.
   * 
   * @param <T> the type of the first argument.
   */
  @FunctionalInterface
  public interface ObjIntPredicate<T> {
    /**
     * Evaluates the predicate on the given arguments.
     * 
     * @param arg1 the first argument.
     * @param arg2 the second argument.
     * @return true if the arguments match the predicate, false otherwise.
     */
    public boolean test(T arg1, int arg2);
  }

  /**
   * Represents a predicate that accepts two object arguments and an integer.
   * 
   * @param <T> the type of the first and second argument.
   */
  @FunctionalInterface
  public interface BiObjIntPredicate<T> {
    /**
     * Evaluates the predicate on the given arguments.
     * 
     * @param arg1 the first argument.
     * @param arg2 the second argument.
     * @param arg3 the third argument.
     * @return true if the arguments match the predicate, false otherwise.
     */
    public boolean test(T arg1, T arg2, int arg3);
  }

  /**
   * Produces an iterable to enumerate the fixed point lattice of an increasing or
   * decreasing monotone function.
   * 
   * <p>
   * The iterator might interrupt its search for the next fixed point if the
   * thread is interrupted. In this case, the iterator's {@code hasNext()} method
   * returns false (and {@code next()} throws an exception if a prior call to
   * {@code hasNext()} after the previous call do {@code next()} has not returned
   * true). The thread's interrupted flag is not reset by the fixed point search
   * algorithm. To recover, you may clear the thread's interrupted flag and call
   * {@code hasNext()} again.
   * 
   * @param <T>                    the type of lattice elements.
   * @param <U>                    the type of projections.
   * @param monotoneFunction       increasing or decreasing monotone function that
   *                               always produces a fixed point after a single
   *                               invocation.
   * @param initialProjection      the supplier of the initial projection to zero
   *                               dimensions to start the search from.
   * @param dimensions             number of dimensions lattice elements can be
   *                               decomposed into.
   * @param partialExtender        function with arguments {@code (proj, nextdim)}
   *                               that produces all extensions of a projection by
   *                               adding another dimension at index
   *                               {@code nextdim}.
   * @param maxExtensionEnumerator function with arguments {@code (proj, projdim)}
   *                               that produces the maximal (if the monotone
   *                               function is decreasing) or minimal (if monotone
   *                               function is increasing) extensions of the
   *                               projection to full lattice elements.
   * @param toFullSolution         function with arguments {@code (proj, projdim)}
   *                               that produces a full lattice element from a
   *                               projection representation (of full dimension
   *                               count)
   * @param projector              function with arguments {@code (elem, projdim)}
   *                               that projects a full lattice element to
   *                               {@code projdim} dimensions.
   * @param projectionEqComparator predicate with arguments
   *                               {@code (proj1, proj2, projdim)} that says
   *                               whether the two projections with
   *                               {@code projdim} dimensions are equal.
   * @param skipProjection         predicate that says whether this projection and
   *                               all its extensions should be skipped.
   * @return an iterable that enumerates all fixed points of
   *         {@code monotoneFunction} which are not skipped according to the
   *         predicate.
   */
  public static <T, U> Iterable<T> enumerateLattice(Function<T, T> monotoneFunction, Supplier<U> initialProjection,
      int dimensions, ObjIntFunction<U, Iterable<U>> partialExtender,
      ObjIntFunction<U, Iterable<T>> maxExtensionEnumerator, ObjIntFunction<U, T> toFullSolution,
      ObjIntFunction<T, U> projector, BiObjIntPredicate<U> projectionEqComparator, ObjIntPredicate<U> skipProjection) {
    return () -> new Iterator<T>() {

      private T nextValue = null;
      private boolean first = true;

      private List<U> projectionPath = new ArrayList<>(dimensions + 1);
      private List<Iterator<U>> partialExtensionsPath = new ArrayList<>(dimensions);

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

      private void handleLeaf(U leaf) {
        T fullExtension = toFullSolution.apply(leaf, dimensions);
        if (fullExtension.equals(monotoneFunction.apply(fullExtension))) {
          nextValue = fullExtension;
        }
      }

      private boolean hasProjectedFixedPoint(U projection, int numDimProjection) {
        if (skipProjection != null && skipProjection.test(projection, numDimProjection)) {
          return false;
        }
        for (T maxExtension : maxExtensionEnumerator.apply(projection, numDimProjection)) {
          T fixedPoint = monotoneFunction.apply(maxExtension);
          if (projectionEqComparator.test(projection, projector.apply(fixedPoint, numDimProjection),
              numDimProjection)) {
            return true;
          }
        }
        return false;
      }

      private void findNext() {
        if (first) {
          U init = initialProjection.get();
          projectionPath.add(init);
          first = false;
        }
        loop: while (nextValue == null && !projectionPath.isEmpty()) {
          int nextDimension = projectionPath.size();
          int currentDimension = nextDimension - 1;
          if (currentDimension >= dimensions) {
            handleLeaf(projectionPath.remove(currentDimension));
          } else {
            U projection = projectionPath.get(currentDimension);
            if (partialExtensionsPath.size() <= currentDimension) {
              partialExtensionsPath.add(partialExtender.apply(projection, currentDimension).iterator());
            }
            Iterator<U> partialExtensions = partialExtensionsPath.get(currentDimension);
            while (partialExtensions.hasNext()) {
              U partialExtension = partialExtensions.next();
              if (hasProjectedFixedPoint(partialExtension, nextDimension)) {
                projectionPath.add(partialExtension);
                continue loop;
              }
              if (Thread.currentThread().isInterrupted()) {
                return;
              }
            }
            if (!partialExtensions.hasNext()) {
              partialExtensionsPath.remove(currentDimension);
              projectionPath.remove(currentDimension);
            }
          }
        }
      }
    };
  }
}
