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

package ch.ethz.sn.visone3.networks;

import ch.ethz.sn.visone3.lang.PrimitiveIterable;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * Relational perspective on a network. This is a directed perspective distinguishing between
 * observed absence and missing observations.
 *
 * <p>
 * The relation distinguishes between one-mode and two-mode, but all elements are on the same
 * continuous index scheme. Thus, the right mode is generally <b>not</b> 0-based.
 * <ul>
 * <li>Methods assume indices from the left/right set and will throw an exception if out of bounds.
 * <li>The 0-suffixed methods are variants taking a 0-based right domain index.
 * </ul>
 */
public interface Relation {
  // TODO invert
  // TODO need data movement?
  // Relation invert();

  /**
   * Determines the number of used relationship indices.
   * 
   * @return Size of underlying relationship storage.
   */
  int countDyadicIndices();

  /**
   * Returns an iterable over the indices of the left domain.
   * 
   * @return Iterable over the left domain of the binary relation.
   */
  PrimitiveIterable.OfInt getLeftDomain();

  /**
   * Returns a stream over the indices of the left domain.
   * 
   * @return Stream over the left domain of the binary relation.
   */
  default IntStream getLeftDomainStream() {
    return IntStream.range(0, countLeftDomain());
  }

  /**
   * Returns the number of elements in the left domain.
   * 
   * @return Size of the left domain.
   */
  int countLeftDomain();

  /**
   * Returns an iterable over the indices of the right domain.
   * 
   * @return Iterable over the left domain of the binary relation.
   */
  PrimitiveIterable.OfInt getRightDomain();

  /**
   * Returns a stream over the indices of the right domain.
   * 
   * @return Stream over the right domain of the binary relation.
   */
  default IntStream getRightDomainStream() {
    if (isTwoMode()) {
      return IntStream.range(countLeftDomain(), countLeftDomain() + countRightDomain());
    }
    return getLeftDomainStream();
  }

  /**
   * Returns the number of elements in the right domain.
   * 
   * @return Size of the right domain.
   */
  int countRightDomain();

  /**
   * Returns an iterable over the union of the left and right domain.
   * 
   * @return Iterable over the left and right domain of the binary relation.
   */
  PrimitiveIterable.OfInt getUnionDomain();

  /**
   * Returns a stream over the union of the left and right domain.
   * 
   * @return Stream over the left and right domain of the binary relation.
   */
  default IntStream getUnionDomainStream() {
    return IntStream.range(0, countUnionDomain());
  }

  /**
   * Returns the number of elements in the left and right domains.
   * 
   * @return Size of the union of the left and right domain.
   */
  int countUnionDomain();

  /**
   * Returns whether this relation has two modes.
   * 
   * @return True if two-mode, false otherwise.
   */
  boolean isTwoMode();

  /**
   * Returns an iterable over all relationships in the relation.
   * 
   * @return Iterable over all relationships.
   */
  default Iterable<Relationship> getRelationships() {
    return () -> new Iterator<Relationship>() {
      final PrimitiveIterator.OfInt nodes = getLeftDomain().iterator();
      int node;
      Iterator<? extends Relationship> itr = Collections.emptyIterator();

      {
        prepareItr();
      }

      void prepareItr() {
        while (nodes.hasNext() && !itr.hasNext()) {
          node = nodes.next();
          itr = getRelationshipsFrom(node).iterator();
        }
      }

      @Override
      public boolean hasNext() {
        return itr.hasNext();
      }

      @Override
      public Relationship next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        final Relationship r = itr.next();
        prepareItr();
        return r;
      }
    };
  }

  /**
   * Get all relationships this element is involved in.
   *
   * @param element
   *          the focal element.
   * @return Iterable over incoming and outgoing relationships.
   */
  Iterable<Relationship> getRelationships(int element);

  /**
   * Determines the number of relationships in this relation.
   * 
   * @return Number of relationships.
   */
  int countRelationships();

  /**
   * Produces an iterable over the relationships the specified element of the left domain is
   * involved in.
   * 
   * @param left
   *          Element of left domain.
   * @return Iterable over ("outgoing") relationships to the right domain.
   */
  Iterable<Relationship> getRelationshipsFrom(int left);

  /**
   * Returns the number of ("outgoing") relationships that the specified element of the left domain
   * is involved in.
   * 
   * @param left
   *          Element of left domain.
   * @return Number of relationship with {@code left} on the left of the relationship.
   */
  int countRelationshipsFrom(int left);

  /**
   * Produces an iterable over the relationships the specified element of the right domain is
   * involved in.
   * 
   * @param right
   *          Element of right domain.
   * @return Iterable over ("incoming") relationships from the left domain.
   */
  Iterable<Relationship> getRelationshipsTo(int right);

  /**
   * Produces an iterable over the relationships the specified element of the right domain is
   * involved in. 0-based variant of {@link #getRelationshipsTo(int)}.
   * 
   * @param right0
   *          Element of right domain, specified as a 0-based index for the right domain.
   * @return Iterable over ("incoming") relationships from the right domain.
   */
  default Iterable<Relationship> getRelationshipsTo0(final int right0) {
    return !isTwoMode() ? getRelationshipsTo(right0)
        : getRelationshipsTo(countLeftDomain() + right0);
  }

  /**
   * Returns the number of ("incoming") relationships that the specified element of the right domain
   * is involved in.
   * 
   * @param right
   *          Element of right domain.
   * @return Number of relationship with {@code right} on the right of the relationship.
   */
  int countRelationshipsTo(int right);

  /**
   * Returns the number of ("incoming") relationships that the specified element of the right domain
   * is involved in. 0-based variant of {@link #countRelationshipsTo(int)}.
   * 
   * @param right0
   *          Element of right domain, specified as a 0-based index for the right domain.
   * @return Number of relationship with {@code right} on the right of the relationship.
   */
  default int countRelationshipsTo0(final int right0) {
    return !isTwoMode() ? countRelationshipsTo(right0)
        : countRelationshipsTo(countLeftDomain() + right0);
  }

  /**
   * Produces a stream over the elements opposite in the relationships of the specified element.
   * Note that in one-mode networks, the stream can contain relationship partners of relationships
   * in which the specified element is on the left or right.
   * 
   * @param element
   *          the element
   * @return the stream of partners in relationships.
   */
  IntStream getPartnersStream(int element);

  // IntStream getRelationsWithFrom(int left);
  // IntStream getRelationsWithTo(int right); // nodeCount +
  // IntStream getRelationsWithTo0(int right); // 0 +
  // default PrimitiveIterable.OfInt getPartners(final int elemnt) {
  // return () -> getPartnersStream(elemnt).iterator();
  // }

  /**
   * Returns the relationship between two specified elements.
   *
   * @param left
   *          the left element of the relationship
   * @param right
   *          the right element of the relationship
   * @return The {@link Relationship} between {@code left} and {@code right}, or null if no such
   *         relationship is contained in this relation.
   * @implNote Defaults to linear search, so better use {@link #getRelationships(int)} or related
   *           functions and search multiple at once if possible.
   */
  default Relationship getRelationship(final int left, final int right) {
    for (final Relationship r : getRelationshipsFrom(left)) {
      if (r.getRight() == right) {
        return r;
      }
    }
    return null;
  }
}
