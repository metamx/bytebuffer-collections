package com.metamx.collections.spatial.split;

import com.metamx.collections.spatial.Node;
import com.metamx.collections.spatial.Point;
import com.metamx.collections.spatial.RTree;
import com.metamx.collections.spatial.RoaringNode;
import com.metamx.collections.spatial.RoaringPoint;
import com.metamx.collections.spatial.RoaringRTree;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Random;

/**
 */
public class RoaringLinearGutmanSplitStrategyTest
{
  @Test
  public void testPickSeeds() throws Exception
  {
    RoaringLinearGutmanSplitStrategy strategy = new RoaringLinearGutmanSplitStrategy(0, 50);
    RoaringNode node = new RoaringNode(new float[2], new float[2], true);

    node.addChild(new RoaringPoint(new float[]{3, 7}, 1));
    node.addChild(new RoaringPoint(new float[]{1, 6}, 1));
    node.addChild(new RoaringPoint(new float[]{9, 8}, 1));
    node.addChild(new RoaringPoint(new float[]{2, 5}, 1));
    node.addChild(new RoaringPoint(new float[]{4, 4}, 1));
    node.enclose();

    RoaringNode[] groups = strategy.split(node);
    Assert.assertEquals(groups[0].getMinCoordinates()[0], 1.0f);
    Assert.assertEquals(groups[0].getMinCoordinates()[1], 4.0f);
    Assert.assertEquals(groups[1].getMinCoordinates()[0], 9.0f);
    Assert.assertEquals(groups[1].getMinCoordinates()[1], 8.0f);
  }

  @Test
  public void testNumChildrenSize()
  {
    RoaringRTree tree = new RoaringRTree(2, new RoaringLinearGutmanSplitStrategy(0, 50));
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      tree.insert(new float[]{rand.nextFloat(), rand.nextFloat()}, i);
    }

    Assert.assertTrue(getNumPoints(tree.getRoot()) >= tree.getSize());
  }

  private int getNumPoints(RoaringNode node)
  {
    int total = 0;
    if (node.isLeaf()) {
      total += node.getChildren().size();
    } else {
      for (RoaringNode child : node.getChildren()) {
        total += getNumPoints(child);
      }
    }
    return total;
  }
}
