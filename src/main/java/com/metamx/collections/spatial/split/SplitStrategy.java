package com.metamx.collections.spatial.split;

import com.metamx.collections.spatial.Node;

/**
 */
public interface SplitStrategy
{
  public boolean needToSplit(Node node);

  public Node[] split(Node node);
}
