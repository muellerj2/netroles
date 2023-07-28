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

package ch.ethz.sn.visone3.roles.impl.blocks.converters;

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.structures.RelationBase;

class EquivalenceFromWeakComponents<T extends RelationBase>
    implements RoleConverter<T, ConstMapping.OfInt> {

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
    return false;
  }

  private ConstMapping.OfInt translateImpl(T in) {

    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.DIRECTED);
    int n = in.domainSize();
    builder.ensureNode(n - 1);
    for (int i = 0; i < n; ++i) {
      for (int j : in.iterateInRelationFrom(i)) {
        builder.addEdge(i, j);
      }
    }

    return Equivalences.normalizePartition(AlgoProvider.getInstance().connectedness()
        .weakComponents(builder.build().asDirectedGraph()));
  }

  @Override
  public OfInt convert(T in) {
    return translateImpl(in);
  }

  @Override
  public OfInt convertRefining(T in, OfInt toRefine) {
    return Equivalences.infimum(translateImpl(in), toRefine);
  }

  @Override
  public OfInt convertCoarsening(T in, OfInt toCoarsen) {
    return Equivalences.supremum(translateImpl(in), toCoarsen);
  }

}
