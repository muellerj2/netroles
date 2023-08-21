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
package ch.ethz.sn.visone3.roles.test.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import org.junit.jupiter.api.Test;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.MatrixSource;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.OperatorTraits;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.blocks.factories.DistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

public class GenericOperatorsTest {

  private Network createNetwork3() {

    final Integer z = null;
    final Integer[][] adj = new Integer[][] { //
        { z }, //
        { 1, z }, //
        { 1, 1, z }, //
        { 1, 1, 1, z }, //
        { z, z, 1, 1, z }, //
        { z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, z }, //
        { z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, z }, //
        { z, z, z, z, z, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, 1, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, 1, 1, 1, z }, //
        { z, z, z, z, z, z, 1, 1, 1, 1, 1, 1, 1, z }, //
    };

    final WeightedNetwork<? extends Integer, ? extends Mapping<? extends Integer>> s = MatrixSource
        .fromAdjacency(adj, DyadType.UNDIRECTED);
    return s.getNetwork();
  }

  private static TransposableNetworkView<Relationship, Relationship> swappingOutgoingView(
      Network network) {
    return new TransposableNetworkView<Relationship, Relationship>() {

      private Relation rel = network.asRelation();

      @Override
      public int countNodes() {
        return rel.countUnionDomain();
      }

      @Override
      public Iterable<? extends Relationship> ties(int lhsComparison, int rhsComparison, int node) {
        return rel.getRelationshipsFrom(node);
      }

      @Override
      public int tieTarget(int lhsComparison, int rhsComparison, int node, Relationship tie) {
        int target = tie.getRight();
        if (node == rhsComparison) {
          if (target == lhsComparison) {
            return rhsComparison;
          } else if (target == rhsComparison) {
            return lhsComparison;
          }
        }
        return target;
      }

      @Override
      public int tieIndex(int lhsComparison, int rhsComparison, int node, Relationship tie) {
        return tie.getIndex();
      }

      @Override
      public int countTies(int lhsComparison, int rhsComparison, int node) {
        return rel.countRelationshipsFrom(node);
      }
    };
  }

  @Test
  public void testGenericEquivalenceLooseOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<ConstMapping.OfInt> roleOp = RoleOperators.EQUIVALENCE.generic()
        .of(outgoingView).traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    ConstMapping.OfInt input = Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2,
        3);
    ConstMapping.OfInt structure1 = Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4,
        5, 4);
    ConstMapping.OfInt structure2 = Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4,
        5, 4);
    ConstMapping.OfInt result = RoleOperators.EQUIVALENCE.weak().of(outgoingView).make()
        .apply(input);

    roleOp = RoleOperators.EQUIVALENCE.generic().of(outgoingView).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    roleOp = RoleOperators.EQUIVALENCE.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    roleOp = RoleOperators.EQUIVALENCE.generic().of(swappingView).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    result = RoleOperators.EQUIVALENCE
        .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(input.getInt(rshipi.getRight()), input.getInt(rshipj.getRight())))
        .make().apply(input);

    ConstMapping.OfInt interior = Equivalences.infimum(input, result);
    ConstMapping.OfInt prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      ConstMapping.OfInt curr = interior;
      interior = Equivalences.infimum(RoleOperators.EQUIVALENCE
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(curr.getInt(rshipi.getRight()), curr.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    ConstMapping.OfInt closure = Equivalences.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      ConstMapping.OfInt curr = closure;
      closure = Equivalences.supremum(RoleOperators.EQUIVALENCE
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(curr.getInt(rshipi.getRight()), curr.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.EQUIVALENCE.generic().of(outgoingView).compWeak(in -> {
      return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
          in.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.EQUIVALENCE.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
              in.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    result = RoleOperators.EQUIVALENCE
        .weak().of(swappingView).compWeak((rshipi, rshipj) -> Integer
            .compare(input.getInt(rshipi.getRight()), input.getInt(rshipj.getRight())))
        .make().apply(input);

    roleOp = RoleOperators.EQUIVALENCE.generic().of(swappingView).compWeak(in -> {
      return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
          in.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    RoleOperator<ConstMapping.OfInt> expected = RoleOperators.EQUIVALENCE.regular()
        .of(outgoingView).make();
    roleOp = RoleOperators.EQUIVALENCE.generic().of(outgoingView).compPartial(in -> {
      return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight())
          ? PartialComparator.ComparisonResult.EQUAL
          : PartialComparator.ComparisonResult.INCOMPARABLE;
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.EQUIVALENCE.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight())
              ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    assertThrows(UnsupportedOperationException.class,
        () -> RoleOperators.EQUIVALENCE.generic().of(swappingView).compPredicate(in -> {
          return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight());
        }));
    expected = RoleOperators.EQUIVALENCE.regular().of(swappingView).make();
    roleOp = RoleOperators.EQUIVALENCE.generic().of(swappingView).compPartial(in -> {
      return (rshipi, rshipj) -> in.getInt(swappingView.tieTarget(rshipi.getLeft(),
          rshipj.getLeft(), rshipi.getLeft(), rshipi)) == in.getInt(
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj))
                  ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));
  }

  @Test
  public void testGenericEquivalenceEquitableOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<ConstMapping.OfInt> roleOp = RoleOperators.EQUIVALENCE.generic().equitable()
        .of(outgoingView).traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    ConstMapping.OfInt input = Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2,
        3);
    ConstMapping.OfInt structure1 = Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4,
        5, 4);
    ConstMapping.OfInt structure2 = Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4,
        5, 4);
    ConstMapping.OfInt result = RoleOperators.EQUIVALENCE.weak().equitable().of(outgoingView)
        .make().apply(input);

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(swappingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), Equivalences.infimum(input, result),
        Equivalences.supremum(input, result));

    result = RoleOperators.EQUIVALENCE
        .weak().equitable().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(input.getInt(rshipi.getRight()), input.getInt(rshipj.getRight())))
        .make().apply(input);

    ConstMapping.OfInt interior = Equivalences.infimum(input, result);
    ConstMapping.OfInt prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      ConstMapping.OfInt curr = interior;
      interior = Equivalences.infimum(RoleOperators.EQUIVALENCE.weak().equitable()
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(curr.getInt(rshipi.getRight()), curr.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    ConstMapping.OfInt closure = Equivalences.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      ConstMapping.OfInt curr = closure;
      closure = Equivalences.supremum(RoleOperators.EQUIVALENCE.weak().equitable()
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(curr.getInt(rshipi.getRight()), curr.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(outgoingView).compWeak(in -> {
      return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
          in.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
              in.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(swappingView).compWeak(in -> {
      return (rshipi, rshipj) -> Integer.compare(in.getInt(rshipi.getRight()),
          in.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), interior, closure);

    RoleOperator<ConstMapping.OfInt> expected = RoleOperators.EQUIVALENCE.regular().equitable()
        .of(outgoingView).make();
    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(outgoingView).compPartial(in -> {
      return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight())
          ? PartialComparator.ComparisonResult.EQUAL
          : PartialComparator.ComparisonResult.INCOMPARABLE;
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.EQUIVALENCE.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight())
              ? PartialComparator.ComparisonResult.EQUAL
              : PartialComparator.ComparisonResult.INCOMPARABLE;
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    assertThrows(UnsupportedOperationException.class, () -> RoleOperators.EQUIVALENCE.generic()
        .equitable().of(swappingView).compPredicate(in -> {
          return (rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight());
        }));

    expected = RoleOperators.EQUIVALENCE.regular().equitable().of(swappingView).make();
    roleOp = RoleOperators.EQUIVALENCE.generic().equitable().of(swappingView).compPartial(in -> {
      return (rshipi, rshipj) -> in.getInt(swappingView.tieTarget(rshipi.getLeft(),
          rshipj.getLeft(), rshipi.getLeft(), rshipi)) == in.getInt(
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj))
                  ? PartialComparator.ComparisonResult.EQUAL
                  : PartialComparator.ComparisonResult.INCOMPARABLE;
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Equivalences.infimum(structure1, result),
        Equivalences.infimum(structure2, result), Equivalences.supremum(structure1, result),
        Equivalences.supremum(structure1, result), expected.interior(input),
        expected.closure(input));
  }

  @Test
  public void testGenericRankingLooseOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<Ranking> roleOp = RoleOperators.RANKING.generic().of(outgoingView)
        .traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    Ranking input = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));
    Ranking structure1 = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    Ranking structure2 = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    Ranking result = RoleOperators.RANKING.weak().of(outgoingView).make().apply(input);

    roleOp = RoleOperators.RANKING.generic().of(outgoingView).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    roleOp = RoleOperators.RANKING.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    roleOp = RoleOperators.RANKING.generic().of(swappingView).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    ConstMapping.OfInt eq = Converters.strongComponentsAsEquivalence()
        .apply(input.asBinaryRelation());
    result = RoleOperators.RANKING
        .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(eq.getInt(rshipi.getRight()), eq.getInt(rshipj.getRight())))
        .make().apply(input);

    Ranking interior = Rankings.infimum(input, result);
    Ranking prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      Ranking curr = interior;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence()
          .apply(curr.asBinaryRelation());
      interior = Rankings.infimum(RoleOperators.RANKING
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    Ranking closure = Rankings.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      Ranking curr = closure;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence()
          .apply(curr.asBinaryRelation());
      closure = Rankings.supremum(RoleOperators.RANKING
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.RANKING.generic().of(outgoingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
          .apply(in.asBinaryRelation());
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.RANKING.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
              .apply(in.asBinaryRelation());
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.RANKING.generic().of(swappingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
          .apply(in.asBinaryRelation());
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    RoleOperator<Ranking> expected = RoleOperators.RANKING.regular().of(outgoingView).make();
    roleOp = RoleOperators.RANKING.generic().of(outgoingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        if (in.contains(rshipi.getRight(), rshipj.getRight())) {
          ++val;
        }
        if (in.contains(rshipj.getRight(), rshipi.getRight())) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));

    roleOp = RoleOperators.RANKING.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));

    assertThrows(UnsupportedOperationException.class,
        () -> RoleOperators.RANKING.generic().of(swappingView).compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }));
    expected = RoleOperators.RANKING.regular().of(swappingView).make();
    roleOp = RoleOperators.RANKING.generic().of(swappingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        int itarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(),
            rshipi);
        int jtarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(),
            rshipj);
        if (in.contains(itarget, jtarget)) {
          ++val;
        }
        if (in.contains(jtarget, itarget)) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));
  }

  @Test
  public void testGenericRankingEquitableOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<Ranking> roleOp = RoleOperators.RANKING.generic().equitable().of(outgoingView)
        .traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    Ranking input = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));
    Ranking structure1 = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    Ranking structure2 = Rankings
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    Ranking result = RoleOperators.RANKING.weak().equitable().of(outgoingView).make()
        .apply(input);

    roleOp = RoleOperators.RANKING.generic().equitable().of(outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    roleOp = RoleOperators.RANKING.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    roleOp = RoleOperators.RANKING.generic().equitable().of(swappingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), Rankings.infimum(input, result),
        Rankings.supremum(input, result));

    ConstMapping.OfInt eq = Converters.strongComponentsAsEquivalence()
        .apply(input.asBinaryRelation());
    result = RoleOperators.RANKING
        .weak().equitable().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(eq.getInt(rshipi.getRight()), eq.getInt(rshipj.getRight())))
        .make().apply(input);

    Ranking interior = Rankings.infimum(input, result);
    Ranking prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      Ranking curr = interior;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence()
          .apply(curr.asBinaryRelation());
      interior = Rankings.infimum(RoleOperators.RANKING
          .weak().equitable().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    Ranking closure = Rankings.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      Ranking curr = closure;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence()
          .apply(curr.asBinaryRelation());
      closure = Rankings.supremum(RoleOperators.RANKING
          .weak().equitable().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.RANKING.generic().equitable().of(outgoingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
          .apply(in.asBinaryRelation());
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.RANKING.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
              .apply(in.asBinaryRelation());
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.RANKING.generic().equitable().of(swappingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence()
          .apply(in.asBinaryRelation());
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), interior, closure);

    RoleOperator<Ranking> expected = RoleOperators.RANKING.regular().equitable().of(outgoingView)
        .make();
    roleOp = RoleOperators.RANKING.generic().equitable().of(outgoingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        if (in.contains(rshipi.getRight(), rshipj.getRight())) {
          ++val;
        }
        if (in.contains(rshipj.getRight(), rshipi.getRight())) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));

    roleOp = RoleOperators.RANKING.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));

    assertThrows(UnsupportedOperationException.class,
        () -> RoleOperators.RANKING.generic().equitable().of(swappingView).compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }));
    expected = RoleOperators.RANKING.regular().equitable().of(swappingView).make();
    roleOp = RoleOperators.RANKING.generic().equitable().of(swappingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        int itarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(),
            rshipi);
        int jtarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(),
            rshipj);
        if (in.contains(itarget, jtarget)) {
          ++val;
        }
        if (in.contains(jtarget, itarget)) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, Rankings.infimum(structure1, result),
        Rankings.infimum(structure2, result), Rankings.supremum(structure1, result),
        Rankings.supremum(structure1, result), expected.interior(input), expected.closure(input));
  }

  @Test
  public void testGenericBinaryRelationLooseOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<BinaryRelation> roleOp = RoleOperators.BINARYRELATION.generic().of(outgoingView)
        .traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    BinaryRelation input = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));
    BinaryRelation structure1 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation structure2 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation result = RoleOperators.BINARYRELATION.weak().of(outgoingView).make()
        .apply(input);

    roleOp = RoleOperators.BINARYRELATION.generic().of(outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic().of(swappingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    ConstMapping.OfInt eq = Converters.strongComponentsAsEquivalence().apply(input);
    result = RoleOperators.BINARYRELATION
        .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(eq.getInt(rshipi.getRight()), eq.getInt(rshipj.getRight())))
        .make().apply(input);

    BinaryRelation interior = BinaryRelations.infimum(input, result);
    BinaryRelation prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      BinaryRelation curr = interior;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      interior = BinaryRelations.infimum(RoleOperators.BINARYRELATION
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    BinaryRelation closure = BinaryRelations.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      BinaryRelation curr = closure;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      closure = BinaryRelations.supremum(RoleOperators.BINARYRELATION
          .weak().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.BINARYRELATION.generic().of(outgoingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic().of(swappingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    RoleOperator<BinaryRelation> expected = RoleOperators.BINARYRELATION.regular()
        .of(outgoingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().of(outgoingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        if (in.contains(rshipi.getRight(), rshipj.getRight())) {
          ++val;
        }
        if (in.contains(rshipj.getRight(), rshipi.getRight())) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().of(outgoingView).compPredicate(in -> {
      return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    expected = RoleOperators.BINARYRELATION.regular().of(swappingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().of(swappingView).compPartial(in -> {
      return (rshipi, rshipj) -> {
        int val = 0;
        int itarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(),
            rshipi);
        int jtarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(),
            rshipj);
        if (in.contains(itarget, jtarget)) {
          ++val;
        }
        if (in.contains(jtarget, itarget)) {
          val += 2;
        }
        switch (val) {
          case 0:
            return PartialComparator.ComparisonResult.INCOMPARABLE;
          case 1:
            return PartialComparator.ComparisonResult.LESS;
          case 2:
            return PartialComparator.ComparisonResult.GREATER;
          case 3:
            return PartialComparator.ComparisonResult.EQUAL;
          default:
            throw new IllegalStateException();
        }
      };
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().of(swappingView).compPredicate(in -> {
      return (rshipi, rshipj) -> in.contains(
          swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(), rshipi),
          swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj));
    }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));
  }

  @Test
  public void testGenericBinaryRelationEquitableOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<BinaryRelation> roleOp = RoleOperators.BINARYRELATION.generic().equitable()
        .of(outgoingView).traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    BinaryRelation input = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));
    BinaryRelation structure1 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation structure2 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation result = RoleOperators.BINARYRELATION.weak().equitable().of(outgoingView)
        .make().apply(input);

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(swappingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    ConstMapping.OfInt eq = Converters.strongComponentsAsEquivalence().apply(input);
    result = RoleOperators.BINARYRELATION
        .weak().equitable().of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(eq.getInt(rshipi.getRight()), eq.getInt(rshipj.getRight())))
        .make().apply(input);

    BinaryRelation interior = BinaryRelations.infimum(input, result);
    BinaryRelation prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      BinaryRelation curr = interior;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      interior = BinaryRelations.infimum(RoleOperators.BINARYRELATION.weak().equitable()
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    BinaryRelation closure = BinaryRelations.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      BinaryRelation curr = closure;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      closure = BinaryRelations.supremum(RoleOperators.BINARYRELATION.weak().equitable()
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(outgoingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(swappingView).compWeak(in -> {
      ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
      return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
          inEq.getInt(rshipj.getRight()));
    }).traits(new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    RoleOperator<BinaryRelation> expected = RoleOperators.BINARYRELATION.regular().equitable()
        .of(outgoingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(outgoingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    expected = RoleOperators.BINARYRELATION.regular().equitable().of(swappingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().equitable().of(swappingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            int itarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
                rshipi.getLeft(), rshipi);
            int jtarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
                rshipj.getLeft(), rshipj);
            if (in.contains(itarget, jtarget)) {
              ++val;
            }
            if (in.contains(jtarget, itarget)) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().equitable().equitable().of(swappingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(), rshipi),
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj));
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));
  }

  @Test
  public void testGenericBinaryRelation2EquitableOperator() {
    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    RoleOperator<BinaryRelation> roleOp = RoleOperators.BINARYRELATION.generic().strictness(2)
        .of(outgoingView).traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    BinaryRelation input = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2, 3));
    BinaryRelation structure1 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation structure2 = BinaryRelations
        .fromEquivalence(Mappings.wrapUnmodifiableInt(0, 1, 1, 2, 1, 1, 3, 4, 5, 3, 3, 4, 5, 4));
    BinaryRelation result = RoleOperators.BINARYRELATION.weak().strictness(2).of(outgoingView)
        .make().apply(input);

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2)
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(swappingView)
        .traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return true;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, true, false, false, () -> {
    }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), BinaryRelations.infimum(input, result),
        BinaryRelations.supremum(input, result));

    ConstMapping.OfInt eq = Converters.strongComponentsAsEquivalence().apply(input);
    result = RoleOperators.BINARYRELATION
        .weak().strictness(2).of(outgoingView).compWeak((rshipi, rshipj) -> Integer
            .compare(eq.getInt(rshipi.getRight()), eq.getInt(rshipj.getRight())))
        .make().apply(input);

    BinaryRelation interior = BinaryRelations.infimum(input, result);
    BinaryRelation prev = input;
    while (!prev.equals(interior)) {
      prev = interior;
      BinaryRelation curr = interior;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      interior = BinaryRelations.infimum(RoleOperators.BINARYRELATION.weak().strictness(2)
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }
    BinaryRelation closure = BinaryRelations.supremum(input, result);
    prev = input;
    while (!prev.equals(closure)) {
      prev = closure;
      BinaryRelation curr = closure;
      ConstMapping.OfInt currEq = Converters.strongComponentsAsEquivalence().apply(curr);
      closure = BinaryRelations.supremum(RoleOperators.BINARYRELATION.weak().strictness(2)
          .of(outgoingView).compWeak((rshipi, rshipj) -> Integer
              .compare(currEq.getInt(rshipi.getRight()), currEq.getInt(rshipj.getRight())))
          .make().apply(input), curr);
    }

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2)
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(swappingView)
        .compWeak(in -> {
          ConstMapping.OfInt inEq = Converters.strongComponentsAsEquivalence().apply(in);
          return (rshipi, rshipj) -> Integer.compare(inEq.getInt(rshipi.getRight()),
              inEq.getInt(rshipj.getRight()));
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), interior, closure);

    RoleOperator<BinaryRelation> expected = RoleOperators.BINARYRELATION.regular().strictness(2)
        .of(outgoingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2)
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            if (in.contains(rshipi.getRight(), rshipj.getRight())) {
              ++val;
            }
            if (in.contains(rshipj.getRight(), rshipi.getRight())) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(outgoingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2)
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(rshipi.getRight(), rshipj.getRight());
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    expected = RoleOperators.BINARYRELATION.regular().strictness(2).of(swappingView).make();
    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(swappingView)
        .compPartial(in -> {
          return (rshipi, rshipj) -> {
            int val = 0;
            int itarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
                rshipi.getLeft(), rshipi);
            int jtarget = swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
                rshipj.getLeft(), rshipj);
            if (in.contains(itarget, jtarget)) {
              ++val;
            }
            if (in.contains(jtarget, itarget)) {
              val += 2;
            }
            switch (val) {
              case 0:
                return PartialComparator.ComparisonResult.INCOMPARABLE;
              case 1:
                return PartialComparator.ComparisonResult.LESS;
              case 2:
                return PartialComparator.ComparisonResult.GREATER;
              case 3:
                return PartialComparator.ComparisonResult.EQUAL;
              default:
                throw new IllegalStateException();
            }
          };
        }).traits(new OperatorTraits() {

          @Override
          public boolean isNonincreasing() {
            return false;
          }

          @Override
          public boolean isNondecreasing() {
            return false;
          }

          @Override
          public boolean isIsotone() {
            return true;
          }

          @Override
          public boolean isConstant() {
            return false;
          }
        }).make();

    result = expected.apply(input);
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));

    roleOp = RoleOperators.BINARYRELATION.generic().strictness(2).of(swappingView)
        .compPredicate(in -> {
          return (rshipi, rshipj) -> in.contains(
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipi.getLeft(), rshipi),
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj));
        }).make();
    OperatorTestUtilities.checkRoleOperator(roleOp, input, result, true, false, false, false,
        () -> {
        }, structure1, structure2, BinaryRelations.infimum(structure1, result),
        BinaryRelations.infimum(structure2, result), BinaryRelations.supremum(structure1, result),
        BinaryRelations.supremum(structure1, result), expected.interior(input),
        expected.closure(input));
  }

  private void testGenericDistanceOperatorImpl(
      Supplier<GenericDistanceBuilderFactory<ConstMapping.OfInt>> genericFactory,
      Supplier<DistanceBuilderFactory<ConstMapping.OfInt>> weakFactory,
      Supplier<DistanceBuilderFactory<ConstMapping.OfInt>> regularFactory) {

    final Random rand = new Random();
    final boolean isotone = rand.nextBoolean();
    final boolean constant = rand.nextBoolean();
    final boolean nonincreasing = rand.nextBoolean();
    final boolean nondecreasing = rand.nextBoolean();
    final OperatorTraits randomTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return nonincreasing;
      }

      @Override
      public boolean isNondecreasing() {
        return nondecreasing;
      }

      @Override
      public boolean isIsotone() {
        return isotone;
      }

      @Override
      public boolean isConstant() {
        return constant;
      }
    };

    Network network = createNetwork3();
    NetworkView<Relationship, Relationship> outgoingView = NetworkView
        .fromNetworkRelation(network, Direction.OUTGOING);
    TransposableNetworkView<Relationship, Relationship> swappingView = swappingOutgoingView(
        network);
    Operator<ConstMapping.OfInt, IntDistanceMatrix> roleOp = genericFactory.get()
        .of(outgoingView).traits(randomTraits).make();
    assertEquals(isotone, roleOp.isIsotone());
    assertEquals(constant, roleOp.isConstant());
    assertEquals(nonincreasing, roleOp.isNonincreasing());
    assertEquals(nondecreasing, roleOp.isNondecreasing());

    ConstMapping.OfInt input = Mappings.wrapUnmodifiableInt(0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1, 2,
        3);

    Function<ConstMapping.OfInt, ToIntFunction<? super Relationship>> failureCost = in -> Relationship::getRight;
    Function<ConstMapping.OfInt, ToIntBiFunction<? super Relationship, ? super Relationship>> substCost = in -> {
      return (rshipi, rshipj) -> rshipj == null ? rshipi.getRight()
          : Math.max(0, rshipi.getRight() - rshipj.getRight() / 2);
    };

    OperatorTraits constantTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return true;
      }
    };

    OperatorTraits isotoneTraits = new OperatorTraits() {

      @Override
      public boolean isNonincreasing() {
        return false;
      }

      @Override
      public boolean isNondecreasing() {
        return false;
      }

      @Override
      public boolean isIsotone() {
        return true;
      }

      @Override
      public boolean isConstant() {
        return false;
      }
    };

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).traits(constantTraits).make(), input,
        weakFactory.get().of(outgoingView).make().apply(input), true, true,
            false, false, () -> {
            });

    OperatorTestUtilities.checkOperator(genericFactory.get()
        .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
        .traits(constantTraits).make(), input,
        weakFactory.get().of(outgoingView).make().apply(input), true, true,
        false, false, () -> {
        });

    OperatorTestUtilities
        .checkOperator(genericFactory.get().of(swappingView).traits(constantTraits).make(), input,
            weakFactory.get().of(swappingView).make().apply(input), true, true,
            false, false, () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            genericFactory.get().of(outgoingView).traits(constantTraits).failCost(failureCost)
                .make(),
            input,
            weakFactory.get().of(outgoingView).failCost(failureCost.apply(input)).make()
                .apply(input),
            true, true, false, false,
            () -> {
            });

    OperatorTestUtilities
        .checkOperator(genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .traits(constantTraits).failCost(failureCost).make(), input,
            weakFactory.get().of(outgoingView)
            .failCost(failureCost.apply(input)).make().apply(input), true, true, false, false,
            () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            genericFactory.get().of(swappingView).traits(constantTraits).failCost(failureCost)
                .make(),
            input, weakFactory.get().of(swappingView)
            .failCost(failureCost.apply(input)).make().apply(input), true, true, false, false,
            () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            genericFactory.get().of(outgoingView).traits(constantTraits).substCost(substCost)
                .make(),
            input, weakFactory.get().of(outgoingView)
            .substCost(substCost.apply(input)).make().apply(input), true, true, false, false,
            () -> {
            });

    OperatorTestUtilities
        .checkOperator(genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .traits(constantTraits).substCost(substCost).make(), input,
            weakFactory.get().of(outgoingView)
            .substCost(substCost.apply(input)).make().apply(input), true, true, false, false,
            () -> {
            });

    OperatorTestUtilities
        .checkOperator(
            genericFactory.get().of(swappingView).traits(constantTraits).substCost(substCost)
                .make(),
            input, weakFactory.get().of(swappingView)
            .substCost(substCost.apply(input)).make().apply(input), true, true, false, false,
            () -> {
            });

    Function<ConstMapping.OfInt, Comparator<? super Relationship>> comparator = in -> (rshipi,
        rshipj) -> Integer.compare(rshipi.getRight(), rshipj.getRight());

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compWeak(comparator).traits(isotoneTraits).make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input)).make().apply(input),
        true,
        false, false, false,
        () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compWeak(comparator).traits(isotoneTraits).make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input)).make().apply(input),
        true, false, false, false,
        () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compWeak(comparator).traits(isotoneTraits).make(),
        input,
        weakFactory.get().of(swappingView).compWeak(comparator.apply(input)).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compWeak(comparator).traits(isotoneTraits)
            .failCost(failureCost)
            .make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input))
            .failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .traits(isotoneTraits).failCost(failureCost).compWeak(comparator).make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input))
            .failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compWeak(comparator).traits(isotoneTraits)
            .failCost(failureCost)
            .make(),
        input,
        weakFactory.get().of(swappingView).compWeak(comparator.apply(input))
            .failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compWeak(comparator).traits(isotoneTraits)
            .substCost(substCost).make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input))
            .substCost(substCost.apply(input)).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compWeak(comparator).traits(isotoneTraits).substCost(substCost).make(),
        input,
        weakFactory.get().of(outgoingView).compWeak(comparator.apply(input))
            .substCost(substCost.apply(input)).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compWeak(comparator).traits(isotoneTraits)
            .substCost(substCost).make(),
        input,
        weakFactory.get().of(swappingView).compWeak(comparator.apply(input))
            .substCost(substCost.apply(input)).make().apply(input),
        true, false, false, false, () -> {
        });

    Function<ConstMapping.OfInt, PartialComparator<? super Relationship>> partialComparator = in -> (
        rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight())
            ? PartialComparator.ComparisonResult.EQUAL
            : PartialComparator.ComparisonResult.INCOMPARABLE;
    Function<ConstMapping.OfInt, PartialComparator<? super Relationship>> swapPartialComparator = in -> {
      return (rshipi,
          rshipj) -> in.getInt(swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
            rshipi.getLeft(), rshipi)) == in
                  .getInt(swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(),
                      rshipj.getLeft(), rshipj)) ? PartialComparator.ComparisonResult.EQUAL
                        : PartialComparator.ComparisonResult.INCOMPARABLE;
    };

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPartial(partialComparator)
            .traits(isotoneTraits).make(),
        input, regularFactory.get().of(outgoingView).make().apply(input), true, false, false,
        false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compPartial(partialComparator).traits(isotoneTraits).make(),
        input,
        regularFactory.get().of(outgoingView).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPartial(swapPartialComparator)
            .traits(isotoneTraits).make(),
        input,
        regularFactory.get().of(swappingView).make().apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPartial(partialComparator)
            .traits(isotoneTraits).failCost(failureCost).make(),
        input,
        regularFactory.get().of(outgoingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .traits(isotoneTraits).failCost(failureCost).compPartial(partialComparator).make(),
        input,
        regularFactory.get().of(outgoingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPartial(swapPartialComparator)
            .traits(isotoneTraits)
            .failCost(failureCost).make(),
        input,
        regularFactory.get().of(swappingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPartial(partialComparator)
            .traits(isotoneTraits).substCost(substCost).make(),
        input,
        regularFactory.get().of(outgoingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compPartial(partialComparator).traits(isotoneTraits).substCost(substCost).make(),
        input,
        regularFactory.get().of(outgoingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPartial(swapPartialComparator)
            .traits(isotoneTraits).substCost(substCost).make(),
        input,
        regularFactory.get().of(swappingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    Function<ConstMapping.OfInt, BiPredicate<? super Relationship, ? super Relationship>> predicate = in -> (
        rshipi, rshipj) -> in.getInt(rshipi.getRight()) == in.getInt(rshipj.getRight());
    Function<ConstMapping.OfInt, BiPredicate<? super Relationship, ? super Relationship>> swapPredicate = in -> {
      return (rshipi, rshipj) -> in.getInt(swappingView.tieTarget(rshipi.getLeft(),
          rshipj.getLeft(), rshipi.getLeft(), rshipi)) == in.getInt(
              swappingView.tieTarget(rshipi.getLeft(), rshipj.getLeft(), rshipj.getLeft(), rshipj));
    };

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPredicate(predicate)
            .traits(isotoneTraits).make(),
        input, regularFactory.get().of(outgoingView).make().apply(input), true, false, false,
        false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compPredicate(predicate).traits(isotoneTraits).make(),
        input, regularFactory.get().of(outgoingView).make().apply(input), true, false, false,
        false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPredicate(swapPredicate)
            .traits(isotoneTraits).make(),
        input, regularFactory.get().of(swappingView).make().apply(input), true, false, false,
        false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPredicate(predicate)
            .traits(isotoneTraits).failCost(failureCost).make(),
        input, regularFactory.get().of(outgoingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .traits(isotoneTraits).failCost(failureCost).compPredicate(predicate).make(),
        input, regularFactory.get().of(outgoingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPredicate(swapPredicate)
            .traits(isotoneTraits).failCost(failureCost).make(),
        input, regularFactory.get().of(swappingView).failCost(failureCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(outgoingView).compPredicate(predicate)
            .traits(isotoneTraits).substCost(substCost).make(),
        input, regularFactory.get().of(outgoingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get()
            .of((TransposableNetworkView<Relationship, Relationship>) outgoingView)
            .compPredicate(predicate).traits(isotoneTraits).substCost(substCost).make(),
        input, regularFactory.get().of(outgoingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

    OperatorTestUtilities.checkOperator(
        genericFactory.get().of(swappingView).compPredicate(swapPredicate)
            .traits(isotoneTraits).substCost(substCost).make(),
        input, regularFactory.get().of(swappingView).substCost(substCost.apply(input)).make()
            .apply(input),
        true, false, false, false, () -> {
        });

  }

  @Test
  public void testGenericDistanceOperator() {
    testGenericDistanceOperatorImpl(() -> DistanceOperators.GENERIC.factory(),
        () -> DistanceOperators.EQUIVALENCE.weak(), () -> DistanceOperators.EQUIVALENCE.regular());
    testGenericDistanceOperatorImpl(
        () -> DistanceOperators.GENERIC.<ConstMapping.OfInt>factory().equitable(),
        () -> DistanceOperators.EQUIVALENCE.weak().equitable(),
        () -> DistanceOperators.EQUIVALENCE.regular().equitable());
    testGenericDistanceOperatorImpl(
        () -> DistanceOperators.GENERIC.<ConstMapping.OfInt>factory().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.weak().strictness(2),
        () -> DistanceOperators.EQUIVALENCE.regular().strictness(2));
  }
}
