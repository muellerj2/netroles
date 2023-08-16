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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;

/**
 * Service offering methods to construct rankings and binary relations.
 */
public interface RelationBuilderService {

  /**
   * Produces a builder for constructing a binary relation backed by a dense
   * matrix representation.
   * 
   * @param size the size of the binary relation's domain.
   * @return the builder.
   */
  RelationBuilder<? extends BinaryRelation> denseRelationBuilder(int size);

  /**
   * Produces a builder for constructing a ranking backed by a dense matrix
   * representation. This builder returns the least ranking containing all the
   * specified pairs, i.e., it constructs the reflexive transitive closure of the
   * binary relation specified by the calls to builder methods.
   * 
   * @param size the size of the ranking's domain.
   * @return the builder.
   */
  RelationBuilder<? extends Ranking> denseSafeRankingBuilder(int size);

  /**
   * Produces a builder for constructing a ranking backed by a dense matrix
   * representation. This builder does not ensure that the produced ranking object
   * is transitive.
   * 
   * @param size the size of the ranking's domain.
   * @return the builder.
   */
  RelationBuilder<? extends Ranking> denseUnsafeRankingBuilder(int size);

  /**
   * Generates a binary relation representation from a matrix.
   * 
   * @param matrix the matrix.
   * @return the binary relation representation.
   */
  BinaryRelation relationFromMatrix(boolean[][] matrix);

  /**
   * Generates a ranking representation from a matrix. This method does not ensure
   * that the produced result actually represents a valid (reflexive and
   * transitive) ranking.
   * 
   * @param matrix the matrix.
   * @return the ranking representation.
   */
  Ranking rankingFromMatrixUnsafe(boolean[][] matrix);
}
