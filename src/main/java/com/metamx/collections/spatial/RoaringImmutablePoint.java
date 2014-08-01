package com.metamx.collections.spatial;

import java.nio.ByteBuffer;

public class RoaringImmutablePoint extends RoaringImmutableNode
{
  public RoaringImmutablePoint(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data)
  {
    super(numDims, initialOffset, offsetFromInitial, (short) 0, true, data);
  }

  public RoaringImmutablePoint(RoaringImmutableNode node)
  {
    super(node.getNumDims(), node.getInitialOffset(), node.getOffsetFromInitial(), (short) 0, true, node.getData());
  }

  public float[] getCoords()
  {
    return super.getMinCoordinates();
  }

  @Override
  public Iterable<RoaringImmutableNode> getChildren()
  {
    // should never get here
    throw new UnsupportedOperationException();
  }
}
