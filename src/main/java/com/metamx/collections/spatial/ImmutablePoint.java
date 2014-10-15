package com.metamx.collections.spatial;

import com.google.common.collect.Lists;

import java.nio.ByteBuffer;

public class ImmutablePoint extends ImmutableNode
{
  public ImmutablePoint(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data)
  {
    super(numDims, initialOffset, offsetFromInitial, (short) 0, true, data);
  }

  public ImmutablePoint(ImmutableNode node)
  {
    super(node.getNumDims(), node.getInitialOffset(), node.getOffsetFromInitial(), (short) 0, true, node.getData());
  }

  public float[] getCoords()
  {
    return super.getMinCoordinates();
  }

  @Override
  public Iterable<ImmutableNode> getChildren()
  {
    // should never get here
    throw new UnsupportedOperationException();
  }

}
