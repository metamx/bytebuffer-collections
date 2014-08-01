package com.metamx.collections.spatial.split;

import com.metamx.collections.spatial.RoaringNode;

/**
 */
public interface RoaringSplitStrategy
{
  public boolean needToSplit(RoaringNode node);

  public RoaringNode[] split(RoaringNode node);
}
