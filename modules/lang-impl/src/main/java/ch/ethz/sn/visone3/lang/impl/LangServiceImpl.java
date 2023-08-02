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
package ch.ethz.sn.visone3.lang.impl;

import ch.ethz.sn.visone3.lang.impl.algorithms.AlgorithmsFacadeImpl;
import ch.ethz.sn.visone3.lang.impl.containers.ContainerFacadeImpl;
import ch.ethz.sn.visone3.lang.impl.iterators.IteratorFacadeImpl;
import ch.ethz.sn.visone3.lang.impl.mappings.MappingsFacadeImpl;
import ch.ethz.sn.visone3.lang.spi.AlgorithmsFacade;
import ch.ethz.sn.visone3.lang.spi.ContainersFacade;
import ch.ethz.sn.visone3.lang.spi.IteratorFacade;
import ch.ethz.sn.visone3.lang.spi.LangService;
import ch.ethz.sn.visone3.lang.spi.MappingsFacade;

public final class LangServiceImpl implements LangService {

  private final MappingsFacade mapFacade = new MappingsFacadeImpl();
  private final ContainersFacade containerFacade = new ContainerFacadeImpl();
  private final IteratorFacade iteratorFacade = new IteratorFacadeImpl();
  private final AlgorithmsFacade algoFacade = new AlgorithmsFacadeImpl();

  public LangServiceImpl() {
  }

  @Override
  public String getName() {
    return "ch.ethz.sn.visone.lang-impl";
  }

  @Override
  public MappingsFacade mappings() {
    return mapFacade;
  }

  @Override
  public AlgorithmsFacade algos() {
    return algoFacade;
  }

  @Override
  public ContainersFacade containers() {
    return containerFacade;
  }

  @Override
  public IteratorFacade iterators() {
    return iteratorFacade;
  }

}
