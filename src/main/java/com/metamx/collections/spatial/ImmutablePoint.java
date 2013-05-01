package com.metamx.collections.spatial;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.nio.ByteBuffer;

/**
 * Byte layout:
 * 0 to numDims * Floats.BYTES : the coordinates
 * numDims * Floats.BYTES to numDims * Floats.BYTES + 4 : integer entry
 * <p/>
 * The child offset is an offset from the initialOffset
 */
public class ImmutablePoint extends ImmutableNode
{
  public static int calcNumBytes(Point point)
  {
    return point.getNumDims() * Floats.BYTES + Ints.BYTES;
  }

  public static int fillBuffer(Point point, int offSet, ByteBuffer buffer)
  {
    buffer.position(offSet);
    for (float v : point.getCoords()) {
      buffer.putFloat(v);
    }
    buffer.putInt(point.getEntry());

    return buffer.position();
  }

  private final int initialOffset;
  private final int offsetFromInitial;
  private final int numDims;
  private final int entryOffset;
  private final ByteBuffer data;

  public ImmutablePoint(int numDims, int initialOffset, int offsetFromInitial, ByteBuffer data)
  {
    super(numDims, initialOffset, offsetFromInitial, (short) 0, true, data);

    this.initialOffset = initialOffset;
    this.offsetFromInitial = offsetFromInitial;
    this.numDims = numDims;
    this.entryOffset = initialOffset + offsetFromInitial + getCoordinateNumBytes();
    this.data = data;
  }

  public ImmutablePoint(ImmutableNode node)
  {
    super(node.getNumDims(), node.getInitialOffset(), node.getOffsetFromInitial(), (short) 0, true, node.getData());

    this.initialOffset = node.getInitialOffset();
    this.offsetFromInitial = node.getOffsetFromInitial();
    this.numDims = node.getNumDims();
    this.entryOffset = node.getInitialOffset()
                       + node.getOffsetFromInitial()
                       + getCoordinateNumBytes();
    this.data = node.getData();
  }

  @Override
  public ImmutableConciseSet getImmutableConciseSet()
  {
    return makeConciseSet();
  }

  public int getCoordinateNumBytes()
  {
    return numDims * Floats.BYTES;
  }

  @Override
  public int getNumBytes()
  {
    return numDims * Floats.BYTES + Ints.BYTES;
  }

  public int getEntry()
  {
    return data.getInt(entryOffset);
  }

  public float[] getCoords()
  {
    final float[] retVal = new float[numDims];

    final ByteBuffer readOnlyBuffer = data.asReadOnlyBuffer();
    readOnlyBuffer.position(initialOffset + offsetFromInitial);
    readOnlyBuffer.asFloatBuffer().get(retVal);

    return retVal;
  }

  @Override
  protected ImmutableConciseSet makeConciseSet()
  {
    ConciseSet theSet = new ConciseSet();
    theSet.add(getEntry());

    return ImmutableConciseSet.newImmutableFromMutable(theSet);
  }
}
