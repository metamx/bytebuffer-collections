package com.metamx.collections.spatial.split;

import com.google.common.collect.Lists;
import com.metamx.collections.spatial.Node;
import com.metamx.collections.spatial.RTreeUtils;
import com.metamx.collections.spatial.RoaringNode;
import com.metamx.collections.spatial.RoaringRTreeUtils;

import it.uniroma3.mat.extendedset.intset.ConciseSet;

import java.util.Arrays;
import java.util.List;

import org.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 */
public abstract class RoaringGutmanSplitStrategy implements RoaringSplitStrategy
{
  private final int minNumChildren;
  private final int maxNumChildren;

  protected RoaringGutmanSplitStrategy(int minNumChildren, int maxNumChildren)
  {
    this.minNumChildren = minNumChildren;
    this.maxNumChildren = maxNumChildren;
  }

  @Override
  public boolean needToSplit(RoaringNode node)
  {
    return (node.getChildren().size() > maxNumChildren);
  }

  /**
   * This algorithm is from the original paper.
   *
   * Algorithm Split. Divide a set of M+1 index entries into two groups.
   *
   * S1. [Pick first entry for each group]. Apply Algorithm {@link #pickSeeds(java.util.List)} to choose
   * two entries to be the first elements of the groups. Assign each to a group.
   *
   * S2. [Check if done]. If all entries have been assigned, stop. If one group has so few entries that all the rest
   * must be assigned to it in order for it to have the minimum number m, assign them and stop.
   *
   * S3. [Select entry to assign]. Invoke Algorithm {@link #(java.util.List, com.metamx.collections.spatial.Node[])}
   * to choose the next entry to assign. Add it to the group whose covering rectangle will have to be enlarged least to
   * accommodate it. Resolve ties by adding the entry to the group smaller area, then to the one with fewer entries, then
   * to either. Repeat from S2.
   */
  @Override
  public RoaringNode[] split(RoaringNode node)
  {
    List<RoaringNode> children = Lists.newArrayList(node.getChildren());
    RoaringNode[] seeds = pickSeeds(children);

    node.clear();
    node.addChild(seeds[0]);
    node.addToRoaringBitmap(seeds[0]);

    RoaringNode group1 = new RoaringNode(
        Arrays.copyOf(seeds[1].getMinCoordinates(), seeds[1].getMinCoordinates().length),
        Arrays.copyOf(seeds[1].getMaxCoordinates(), seeds[1].getMaxCoordinates().length),
        Lists.newArrayList(seeds[1]),
        node.isLeaf(),
        node.getParent(),
        new MutableRoaringBitmap()
    );
    group1.addToRoaringBitmap(seeds[1]);
    if (node.getParent() != null) {
      node.getParent().addChild(group1);
    }
    RoaringNode[] groups = new RoaringNode[]{
        node, group1
    };

    RoaringRTreeUtils.enclose(groups);

    while (!children.isEmpty()) {
      for (RoaringNode group : groups) {
        if (group.getChildren().size() + children.size() <= minNumChildren) {
          for (RoaringNode child : group.getChildren()) {
            group.addToRoaringBitmap(child);
            group.addChild(child);
          }
          RoaringRTreeUtils.enclose(groups);
          return groups;
        }
      }

      RoaringNode nextToAssign = pickNext(children, groups);
      double group0ExpandedArea = RoaringRTreeUtils.getEnclosingArea(groups[0], nextToAssign);
      double group1ExpandedArea = RoaringRTreeUtils.getEnclosingArea(groups[1], nextToAssign);

      RoaringNode optimal;
      if (group0ExpandedArea < group1ExpandedArea) {
        optimal = groups[0];
      } else if (group0ExpandedArea == group1ExpandedArea) {
        if (groups[0].getArea() < groups[1].getArea()) {
          optimal = groups[0];
        } else {
          optimal = groups[1];
        }
      } else {
        optimal = groups[1];
      }

      optimal.addToRoaringBitmap(nextToAssign);
      optimal.addChild(nextToAssign);
      optimal.enclose();
    }

    return groups;
  }

  public abstract RoaringNode[] pickSeeds(List<RoaringNode> children);

  public abstract RoaringNode pickNext(List<RoaringNode> children, RoaringNode[] groups);
}
