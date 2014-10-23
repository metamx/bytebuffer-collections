package com.metamx.collections.bitmap;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WrappedRoaringBitmap implements GenericBitmap
{
  /**
   * Underlying bitmap.
   */
  private MutableRoaringBitmap invertedIndex;

  /**
   * Create a new WrappedRoaringBitmap wrapping an empty MutableRoaringBitmap
   */
  public WrappedRoaringBitmap()
  {
    this.invertedIndex = new MutableRoaringBitmap();
  }

  public MutableRoaringBitmap getInvertedIndex()
  {
    return invertedIndex;
  }

  @Override
  public void clear()
  {
    this.invertedIndex.clear();
  }

  @Override
  public void or(GenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    invertedIndex.or(otherIndex);
  }

  @Override
  public void and(GenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    invertedIndex.and(otherIndex);
  }


  @Override
  public void andNot(GenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    invertedIndex.andNot(otherIndex);
  }


  @Override
  public void xor(GenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    invertedIndex.xor(otherIndex);
  }

  @Override
  public int getSizeInBytes()
  {
    return invertedIndex.serializedSizeInBytes();
  }

  @Override
  public void add(int entry)
  {
    invertedIndex.add(entry);
  }

  @Override
  public int size()
  {
    return invertedIndex.getCardinality();
  }

  @Override
  public void serialize(ByteBuffer buffer)
  {
    buffer.putInt(getSizeInBytes());
    try {
      invertedIndex.serialize(
          new DataOutputStream(
              new OutputStream()
              {
                ByteBuffer mBB;

                OutputStream init(ByteBuffer mbb)
                {
                  mBB = mbb;
                  return this;
                }

                @Override
                public void close()
                {
                  // unnecessary
                }

                @Override
                public void flush()
                {
                  // unnecessary
                }

                @Override
                public void write(int b)
                {
                  mBB.put((byte) b);
                }

                @Override
                public void write(byte[] b)
                {
                  throw new RuntimeException("Should never be called");
                }

                @Override
                public void write(byte[] b, int off, int l)
                {
                  throw new RuntimeException("Should never be called");
                }
              }.init(buffer)
          )
      );
    }
    catch (IOException e) {
      e.printStackTrace(); // impossible in theory
    }
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + invertedIndex.toString();
  }

  @Override
  public void remove(int entry)
  {
    invertedIndex.remove(entry);
  }

  @Override
  public IntIterator iterator()
  {
    return invertedIndex.getIntIterator();
  }

  @Override
  public boolean isEmpty()
  {
    return invertedIndex.isEmpty();
  }

  @Override
  public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.or(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.and(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
    MutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.andNot(invertedIndex, otherIndex));
  }
}
