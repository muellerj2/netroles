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
package ch.ethz.sn.visone3.lang.spi;

/**
 * Service an implementation should provide. Consists of several subcomponents related to mappings,
 * algorithms, containers and iterators.
 */
public interface LangService {

  /**
   * The name of the service.
   * 
   * @return the name
   */
  String getName();

  /**
   * The support facade for mappings.
   * 
   * @return the mappings facade
   */
  MappingsFacade mappings();

  /**
   * The support facade for some basic algorithms.
   * 
   * @return the algorithms facade
   */
  AlgorithmsFacade algos();

  /**
   * The support facade for some basic containers.
   * 
   * @return the containers facade
   */
  ContainersFacade containers();

  /**
   * The support facade for some iterator utilities.
   * 
   * @return the iterator utilities facade
   */
  IteratorFacade iterators();
}
