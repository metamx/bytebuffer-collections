package com.metamx.collections.spatial.search;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.ImmutablePoint;
import com.metamx.collections.spatial.bitmap.ImmutableGenericBitmap;

/**
 */
public class GutmanSearchStrategy implements SearchStrategy
{
  @Override
  public Iterable<ImmutableGenericBitmap> search(ImmutableNode node, Bound bound)
  {
    if (bound.getLimit() > 0) {
      return Iterables.transform(
          breadthFirstSearch(node, bound),
          new Function<ImmutableNode, ImmutableGenericBitmap>()
          {
            @Override
            public ImmutableGenericBitmap apply(ImmutableNode immutableNode)
            {
              return immutableNode.getImmutableBitmap();
            }
          }
      );
    }

    return Iterables.transform(
        depthFirstSearch(node, bound),
        new Function<ImmutablePoint, ImmutableGenericBitmap>()
        {
          @Override
          public ImmutableGenericBitmap apply(ImmutablePoint immutablePoint)
          {
            return immutablePoint.getImmutableBitmap();
          }
        }
    );
  }

  public Iterable<ImmutablePoint> depthFirstSearch(ImmutableNode node, final Bound bound)
  {
    if (node.isLeaf()) {
      return bound.filter(
          Iterables.transform(
              node.getChildren(),
              new Function<ImmutableNode, ImmutablePoint>()
              {
                @Override
                public ImmutablePoint apply(ImmutableNode tNode)
                {
                  return new ImmutablePoint(tNode);
                }
              }
          )
      );
    } else {
      return Iterables.concat(
          Iterables.transform(
              Iterables.filter(
                  node.getChildren(),
                  new Predicate<ImmutableNode>()
                  {
                    @Override
                    public boolean apply(ImmutableNode child)
                    {
                      return bound.overlaps(child);
                    }
                  }
              ),
              new Function<ImmutableNode, Iterable<ImmutablePoint>>()
              {
                @Override
                public Iterable<ImmutablePoint> apply(ImmutableNode child)
                {
                  return depthFirstSearch(child, bound);
                }
              }
          )
      );
    }
  }

  public Iterable<ImmutableNode> breadthFirstSearch(
      ImmutableNode node,
      final Bound bound
  )
  {
    if (node.isLeaf()) {
      return Iterables.filter(
          node.getChildren(),
          new Predicate<ImmutableNode>()
          {
            @Override
            public boolean apply(ImmutableNode immutableNode)
            {
              return bound.contains(immutableNode.getMinCoordinates());
            }
          }
      );
    }
    return breadthFirstSearch(node.getChildren(), bound, 0);
  }

  public Iterable<ImmutableNode> breadthFirstSearch(
      Iterable<ImmutableNode> nodes,
      final Bound bound,
      int total
  )
  {
    Iterable<ImmutableNode> points = Iterables.concat(
        Iterables.transform(
            Iterables.filter(
                nodes,
                new Predicate<ImmutableNode>()
                {
                  @Override
                  public boolean apply(ImmutableNode immutableNode)
                  {
                    return immutableNode.isLeaf();
                  }
                }
            ),
            new Function<ImmutableNode, Iterable<ImmutableNode>>()
            {
              @Override
              public Iterable<ImmutableNode> apply(ImmutableNode immutableNode)
              {
                return Iterables.filter(
                    immutableNode.getChildren(),
                    new Predicate<ImmutableNode>()
                    {
                      @Override
                      public boolean apply(ImmutableNode immutableNode)
                      {
                        return bound.contains(immutableNode.getMinCoordinates());
                      }
                    }
                );
              }
            }
        )
    );

    Iterable<ImmutableNode> overlappingNodes = Iterables.filter(
        nodes,
        new Predicate<ImmutableNode>()
        {
          @Override
          public boolean apply(ImmutableNode immutableNode)
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
                      new Function<ImmutableNode, Iterable<ImmutableNode>>()
                      {
                        @Override
                        public Iterable<ImmutableNode> apply(ImmutableNode immutableNode)
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