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

import java.util.function.IntBinaryOperator;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.spi.ConverterLoader;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;

/**
 * Provides some common converters between various types of role relations.
 *
 */
public class Converters {

  private Converters() {
  }

  /**
   * Searches for a register converter from the source to the destination class for the specified
   * argument.
   * 
   * @param <T>
   *          the source type.
   * @param <U>
   *          the destination type.
   * @param source
   *          the source class object.
   * @param destination
   *          the destination class object.
   * @param argument
   *          an additional argument to the converter affecting the conversion result.
   * @return the registered converter
   */
  public static <T, U> RoleConverter<T, U> convert(Class<T> source, Class<U> destination,
      Object argument) {
    return ConverterLoader.getInstance().getConverter(source, destination, argument);
  }

  /**
   * Converts an equivalence into the corresponding ranking.
   * 
   * @return the converter
   */
  public static RoleConverter<ConstMapping.OfInt, Ranking> equivalenceAsRanking() {
    return convert(ConstMapping.OfInt.class, Ranking.class, null);
  }

  /**
   * Converts an equivalence into the corresponding binary relation.
   * 
   * @return the converter
   */
  public static RoleConverter<ConstMapping.OfInt, BinaryRelation> equivalenceAsRelation() {
    return convert(ConstMapping.OfInt.class, BinaryRelation.class, null);
  }

  /**
   * Converts a ranking into the corresponding binary relation.
   * 
   * @return the converter
   */
  public static RoleConverter<Ranking, BinaryRelation> rankingAsRelation() {
    return convert(Ranking.class, BinaryRelation.class, null);
  }

  /**
   * Converts a binary relation into an equivalence consisting of the strongly connected components
   * within the binary relation.
   * 
   * @return the converter
   */
  public static RoleConverter<BinaryRelation, ConstMapping.OfInt> strongComponentsAsEquivalence() {
    return convert(BinaryRelation.class, ConstMapping.OfInt.class, null);
  }

  /**
   * Converts a binary relation into an equivalence consisting of the weakly connected components
   * within the binary relation.
   * 
   * @return the converter
   */
  public static RoleConverter<BinaryRelation, ConstMapping.OfInt> weakComponentsAsEquivalence() {
    return convert(BinaryRelation.class, ConstMapping.OfInt.class, "weak");
  }

  /**
   * Converts a binary relation into the finest coarsening ranking by transitively closing over the
   * binary relation.
   * 
   * @return the converter
   */
  public static RoleConverter<BinaryRelation, Ranking> transitiveClosureAsRanking() {
    return convert(BinaryRelation.class, Ranking.class, null);
  }

  /**
   * Converts distances into a binary relation by thresholding distances.
   * 
   * @param threshold
   *          a mapping such that node i is considered to be in relation with j if their distance
   *          doesn't exceed {@code threshold(i, j)}
   * @return the converter
   */
  public static RoleConverter<IntDistanceMatrix, BinaryRelation> thresholdDistances(
      IntBinaryOperator threshold) {
    return convert(IntDistanceMatrix.class, BinaryRelation.class, threshold);
  }

  /**
   * Always produces the single-class equivalence.
   * 
   * @param domainSize the size of the underlying domain.
   * @param <T>        the type of the (ignored) converter input.
   * @return the converter.
   */
  public static <T> Operator<T, ConstMapping.OfInt> singleClassEquivalence(int domainSize) {
    return new Operator<T, ConstMapping.OfInt>() {

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }

      @Override
      public ConstMapping.OfInt apply(T in) {
        return Mappings.repeated(0, domainSize);
      }
    };
  }

  /**
   * Always produces the all-equivalent ranking.
   * 
   * @param domainSize the size of the underlying domain.
   * @param <T>        the type of the (ignored) converter input.
   * @return the converter.
   */
  public static <T> Operator<T, Ranking> allEqualRanking(int domainSize) {
    return new Operator<T, Ranking>() {

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }

      @Override
      public Ranking apply(T in) {
        return Rankings.universal(domainSize);
      }
    };
  }

  /**
   * Always produces the universal relation.
   * 
   * @param domainSize the size of the underlying domain.
   * @param <T>        the type of the (ignored) converter input.
   * @return the converter.
   */
  public static <T> Operator<T, BinaryRelation> universalRelation(int domainSize) {
    return new Operator<T, BinaryRelation>() {

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }

      @Override
      public BinaryRelation apply(T in) {
        return BinaryRelations.universal(domainSize);
      }
    };
  }

}
