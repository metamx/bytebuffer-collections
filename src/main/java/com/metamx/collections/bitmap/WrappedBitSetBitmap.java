package com.metamx.collections.bitmap;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * WrappedBitSetBitmap implements MutableBitmap for java.util.BitSet
 */
public class WrappedBitSetBitmap extends WrappedImmutableBitSetBitmap implements MutableBitmap
{

  public WrappedBitSetBitmap()
  {
    super();
  }

  public WrappedBitSetBitmap(BitSet bitSet)
  {
    super(bitSet);
  }

  public WrappedBitSetBitmap(ByteBuffer byteBuffer){
    super(byteBuffer);
  }
  protected BitSet cloneBitSet()
  {
    return (BitSet) bitmap.clone();
  }

  @Override
  public void clear()
  {
    bitmap.clear();
  }

  @Override
  public void or(MutableBitmap mutableBitmap)
  {
    if (mutableBitmap instanceof WrappedBitSetBitmap) {
      WrappedBitSetBitmap bitSet = (WrappedBitSetBitmap) mutableBitmap;
      this.bitmap.or(bitSet.bitmap);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unknown class type: %s  expected %s",
              mutableBitmap.getClass().getCanonicalName(),
              WrappedBitSetBitmap.class.getCanonicalName()
          )
      );
    }
  }

  @Override
  public void and(MutableBitmap mutableBitmap)
  {
    if (mutableBitmap instanceof WrappedBitSetBitmap) {
      WrappedBitSetBitmap bitSet = (WrappedBitSetBitmap) mutableBitmap;
      this.bitmap.and(bitSet.bitmap);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unknown class type: %s  expected %s",
              mutableBitmap.getClass().getCanonicalName(),
              WrappedBitSetBitmap.class.getCanonicalName()
          )
      );
    }
  }

  @Override
  public void xor(MutableBitmap mutableBitmap)
  {
    if (mutableBitmap instanceof WrappedBitSetBitmap) {
      WrappedBitSetBitmap bitSet = (WrappedBitSetBitmap) mutableBitmap;
      this.bitmap.xor(bitSet.bitmap);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unknown class type: %s  expected %s",
              mutableBitmap.getClass().getCanonicalName(),
              WrappedBitSetBitmap.class.getCanonicalName()
          )
      );
    }
  }

  @Override
  public void andNot(MutableBitmap mutableBitmap)
  {
    if (mutableBitmap instanceof WrappedBitSetBitmap) {
      WrappedBitSetBitmap bitSet = (WrappedBitSetBitmap) mutableBitmap;
      this.bitmap.andNot(bitSet.bitmap);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unknown class type: %s  expected %s",
              mutableBitmap.getClass().getCanonicalName(),
              WrappedBitSetBitmap.class.getCanonicalName()
          )
      );
    }
  }

  @Override
  public int getSizeInBytes()
  {
    // BitSet.size() returns the size in *bits*
    return this.bitmap.size() / Byte.SIZE;
  }

  @Override
  public void add(int entry)
  {
    this.bitmap.set(entry);
  }

  @Override
  public void remove(int entry)
  {
    this.bitmap.clear(entry);
  }

  @Override
  public void serialize(ByteBuffer buffer)
  {
    buffer.put(this.bitmap.toByteArray());
  }
}
