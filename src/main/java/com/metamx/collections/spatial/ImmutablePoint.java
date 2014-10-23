package com.metamx.collections.spatial;

import java.nio.ByteBuffer;

import CompressedBitmaps.ImmutableGenericBitmap;

public class ImmutablePoint extends ImmutableNode
{
    public ImmutablePoint(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data, ImmutableGenericBitmap bitmap)
    {
        super(numDims, initialOffset, offsetFromInitial, (short) 0, true, data, bitmap);
    }

    public ImmutablePoint(ImmutableNode node)
    {
        super(node.getNumDims(), node.getInitialOffset(), node.getOffsetFromInitial(), (short) 0, true, node.getData(), node.bitmap);
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
