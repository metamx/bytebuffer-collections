package com.metamx.collections.spatial.split;

import com.metamx.collections.spatial.Node;
import com.metamx.collections.spatial.RTreeUtils;
import com.metamx.collections.spatial.CompressedBitmaps.GenericBitmap;

import java.util.List;

/**
 */
public class QuadraticGutmanSplitStrategy extends GutmanSplitStrategy
{
  public QuadraticGutmanSplitStrategy(int minNumChildren, int maxNumChildren, GenericBitmap bitmap)
  {
    super(minNumChildren, maxNumChildren, bitmap);
  }

  @Override
  public Node[] pickSeeds(List<Node> nodes)
  {
    double highestCost = Double.MIN_VALUE;
    int[] highestCostIndices = new int[2];

    for (int i = 0; i < nodes.size() - 1; i++) {
      for (int j = i + 1; j < nodes.size(); j++) {
        double cost = RTreeUtils.getEnclosingArea(nodes.get(i), nodes.get(j)) -
                      nodes.get(i).getArea() - nodes.get(j).getArea();
        if (cost > highestCost) {
          highestCost = cost;
          highestCostIndices[0] = i;
          highestCostIndices[1] = j;
        }
      }
    }

    return new Node[]{nodes.remove(highestCostIndices[0]), nodes.remove(highestCostIndices[1] - 1)};
  }

  @Override
  public Node pickNext(List<Node> nodes, Node[] groups)
  {
    double highestCost = Double.MIN_VALUE;
    Node costlyNode = null;
    int counter = 0;
    int index = -1;
    for (Node node : nodes) {
      double group0Cost = RTreeUtils.getEnclosingArea(node, groups[0]);
      double group1Cost = RTreeUtils.getEnclosingArea(node, groups[1]);
      double cost = Math.abs(group0Cost - group1Cost);
      if (cost > highestCost) {
        highestCost = cost;
        costlyNode = node;
        index = counter;
      }
      counter++;
    }

    if (costlyNode != null) {
      nodes.remove(index);
    }

    return costlyNode;
  }
}
