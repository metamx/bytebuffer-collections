package com.metamx.collections.spatial;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Byte layout:
 * Header
 * 0 to 1 : the MSB is a boolean flag for isLeaf, the next 15 bits represent the number of children of a node
 * Body
 * 2 to 2 + numDims * Floats.BYTES : minCoordinates
 * 2 + numDims * Floats.BYTES to 2 + 2 * numDims * Floats.BYTES : maxCoordinates
 * concise set
 * rest (children) : Every 4 bytes is storing an offset representing the position of a child.
 * <p/>
 * The child offset is an offset from the initialOffset
 */
public class ImmutableNode
{
  public static final int HEADER_NUM_BYTES = 2;

  private final int numDims;
  private final int initialOffset;
  private final int offsetFromInitial;

  private final short numChildren;
  private final boolean isLeaf;
  private final int conciseSetSize;
  private final int childrenOffset;

  private final ByteBuffer data;

  public ImmutableNode(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data)
  {
    this.numDims = numDims;
    this.initialOffset = initialOffset;
    this.offsetFromInitial = offsetFromInitial;
    short header = data.getShort(initialOffset + offsetFromInitial);
    this.isLeaf = (header & 0x8000) != 0;
    this.numChildren = (short) (header & 0x7FFF);
    final int sizePosition = initialOffset + offsetFromInitial + HEADER_NUM_BYTES + 2 * numDims * Floats.BYTES;
    this.conciseSetSize = data.getInt(sizePosition);
    this.childrenOffset = initialOffset
                          + offsetFromInitial
                          + HEADER_NUM_BYTES
                          + 2 * numDims * Floats.BYTES
                          + Ints.BYTES
                          + conciseSetSize;

    this.data = data;
  }

  public ImmutableNode(
      int numDims,
      int initialOffset,
      int offsetFromInitial,
      short numChildren,
      boolean leaf,
      ByteBuffer data
  )
  {
    this.numDims = numDims;
    this.initialOffset = initialOffset;
    this.offsetFromInitial = offsetFromInitial;
    this.numChildren = numChildren;
    this.isLeaf = leaf;
    final int sizePosition = initialOffset + offsetFromInitial + HEADER_NUM_BYTES + 2 * numDims * Floats.BYTES;
    this.conciseSetSize = data.getInt(sizePosition);
    this.childrenOffset = initialOffset
                          + offsetFromInitial
                          + HEADER_NUM_BYTES
                          + 2 * numDims * Floats.BYTES
                          + Ints.BYTES
                          + conciseSetSize;

    this.data = data;
  }

  public int getInitialOffset()
  {
    return initialOffset;
  }

  public int getOffsetFromInitial()
  {
    return offsetFromInitial;
  }

  public int getNumDims()
  {
    return numDims;
  }

  public int getNumChildren()
  {
    return numChildren;
  }

  public boolean isLeaf()
  {
    return isLeaf;
  }

  public float[] getMinCoordinates()
  {
    return getCoords(initialOffset + offsetFromInitial + HEADER_NUM_BYTES);
  }

  public float[] getMaxCoordinates()
  {
    return getCoords(initialOffset + offsetFromInitial + HEADER_NUM_BYTES + numDims * Floats.BYTES);
  }

  public ImmutableRoaringBitmap getImmutableRoaringBitmap()
  {
      final int sizePosition = initialOffset + offsetFromInitial + HEADER_NUM_BYTES + 2 * numDims * Floats.BYTES;
      int numBytes = data.getInt(sizePosition);
      data.position(sizePosition + Ints.BYTES);
      ByteBuffer tmpBuffer = data.slice();
      tmpBuffer.limit(numBytes);
      return new ImmutableRoaringBitmap(tmpBuffer.asReadOnlyBuffer());
  }

  public Iterable<ImmutableNode> getChildren()
  {
    return new Iterable<ImmutableNode>()
    {
      @Override
      public Iterator<ImmutableNode> iterator()
      {
        return new Iterator<ImmutableNode>()
        {
          private volatile int count = 0;

          @Override
          public boolean hasNext()
          {
            return (count < numChildren);
          }

          @Override
          public ImmutableNode next()
          {
            if (isLeaf) {
              return new ImmutablePoint(
                  numDims,
                  initialOffset,
                  data.getInt(childrenOffset + (count++) * Ints.BYTES),
                  data
              );
            }
            return new ImmutableNode(
                numDims,
                initialOffset,
                data.getInt(childrenOffset + (count++) * Ints.BYTES),
                data
            );
          }

          @Override
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }

  public ByteBuffer getData()
  {
    return data;
  }

  private float[] getCoords(int offset)
  {
    final float[] retVal = new float[numDims];

    final ByteBuffer readOnlyBuffer = data.asReadOnlyBuffer();
    readOnlyBuffer.position(offset);
    readOnlyBuffer.asFloatBuffer().get(retVal);

    return retVal;
  }
}
