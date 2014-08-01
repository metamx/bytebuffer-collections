package com.metamx.collections.spatial.search;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.ImmutablePoint;
import com.metamx.collections.spatial.RoaringImmutableNode;
import com.metamx.collections.spatial.RoaringImmutablePoint;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

/**
 */
public class RoaringGutmanSearchStrategy implements RoaringSearchStrategy
{
  @Override
  public Iterable<ImmutableRoaringBitmap> search(RoaringImmutableNode node, RoaringBound bound)
  {
    if (bound.getLimit() > 0) {
      return Iterables.transform(
          breadthFirstSearch(node, bound),
          new Function<RoaringImmutableNode, ImmutableRoaringBitmap>()
          {
            @Override
            public ImmutableRoaringBitmap apply(RoaringImmutableNode immutableNode)
            {
              return immutableNode.getImmutableRoaringBitmap();
            }
          }
      );
    }

    return Iterables.transform(
        depthFirstSearch(node, bound),
        new Function<RoaringImmutablePoint, ImmutableRoaringBitmap>()
        {
          @Override
          public ImmutableRoaringBitmap apply(RoaringImmutablePoint immutablePoint)
          {
            return immutablePoint.getImmutableRoaringBitmap();
          }
        }
    );
  }

  public Iterable<RoaringImmutablePoint> depthFirstSearch(RoaringImmutableNode node, final RoaringBound bound)
  {
    if (node.isLeaf()) {
      return bound.filter(
          Iterables.transform(
              node.getChildren(),
              new Function<RoaringImmutableNode, RoaringImmutablePoint>()
              {
                @Override
                public RoaringImmutablePoint apply(RoaringImmutableNode tNode)
                {
                  return new RoaringImmutablePoint(tNode);
                }
              }
          )
      );
    } else {
      return Iterables.concat(
          Iterables.transform(
              Iterables.filter(
                  node.getChildren(),
                  new Predicate<RoaringImmutableNode>()
                  {
                    @Override
                    public boolean apply(RoaringImmutableNode child)
                    {
                      return bound.overlaps(child);
                    }
                  }
              ),
              new Function<RoaringImmutableNode, Iterable<RoaringImmutablePoint>>()
              {
                @Override
                public Iterable<RoaringImmutablePoint> apply(RoaringImmutableNode child)
                {
                  return depthFirstSearch(child, bound);
                }
              }
          )
      );
    }
  }

  public Iterable<RoaringImmutableNode> breadthFirstSearch(
      RoaringImmutableNode node,
      final RoaringBound bound
  )
  {
    if (node.isLeaf()) {
      return Iterables.filter(
          node.getChildren(),
          new Predicate<RoaringImmutableNode>()
          {
            @Override
            public boolean apply(RoaringImmutableNode immutableNode)
            {
              return bound.contains(immutableNode.getMinCoordinates());
            }
          }
      );
    }
    return breadthFirstSearch(node.getChildren(), bound, 0);
  }

  public Iterable<RoaringImmutableNode> breadthFirstSearch(
      Iterable<RoaringImmutableNode> nodes,
      final RoaringBound bound,
      int total
  )
  {
    Iterable<RoaringImmutableNode> points = Iterables.concat(
        Iterables.transform(
            Iterables.filter(
                nodes,
                new Predicate<RoaringImmutableNode>()
                {
                  @Override
                  public boolean apply(RoaringImmutableNode immutableNode)
                  {
                    return immutableNode.isLeaf();
                  }
                }
            ),
            new Function<RoaringImmutableNode, Iterable<RoaringImmutableNode>>()
            {
              @Override
              public Iterable<RoaringImmutableNode> apply(RoaringImmutableNode immutableNode)
              {
                return Iterables.filter(
                    immutableNode.getChildren(),
                    new Predicate<RoaringImmutableNode>()
                    {
                      @Override
                      public boolean apply(RoaringImmutableNode immutableNode)
                      {
                        return bound.contains(immutableNode.getMinCoordinates());
                      }
                    }
                );
              }
            }
        )
    );

    Iterable<RoaringImmutableNode> overlappingNodes = Iterables.filter(
        nodes,
        new Predicate<RoaringImmutableNode>()
        {
          @Override
          public boolean apply(RoaringImmutableNode immutableNode)
          {
            return !immutableNode.isLeaf() && bound.overlaps(immutableNode);
          }
        }
    );

    int totalPoints = Iterables.size(points);
    int totalOverlap = Iterables.size(overlappingNodes);

    if (totalOverlap == 0 || (totalPoints + totalOverlap + total) >= bound.getLimit()) {
      return Iterables.concat(
          points,
          overlappingNodes
      );
    } else {
      return Iterables.concat(
          points,
          breadthFirstSearch(
              Iterables.concat(
                  Iterables.transform(
                      overlappingNodes,
                      new Function<RoaringImmutableNode, Iterable<RoaringImmutableNode>>()
                      {
                        @Override
                        public Iterable<RoaringImmutableNode> apply(RoaringImmutableNode immutableNode)
                        {
                          return immutableNode.getChildren();
                        }
                      }
                  )
              ),
              bound,
              totalPoints
          )
      );
    }
  }
}