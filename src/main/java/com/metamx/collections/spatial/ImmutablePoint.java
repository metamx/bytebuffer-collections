package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.metamx.collections.spatial.bitmap.BitmapFactory;

import java.nio.ByteBuffer;

public class ImmutablePoint extends ImmutableNode
{
  public ImmutablePoint(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data, BitmapFactory bf)
  {
    super(numDims, initialOffset, offsetFromInitial, (short) 0, true, data, bf);
  }

  public ImmutablePoint(ImmutableNode node)
  {
    super(node.getNumDims(), node.getInitialOffset(), node.getOffsetFromInitial(), (short) 0, true, node.getData(), node.bf);
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
