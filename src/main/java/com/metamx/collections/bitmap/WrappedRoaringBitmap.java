package com.metamx.collections.bitmap;

import com.google.common.base.Throwables;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WrappedRoaringBitmap implements MutableBitmap
{
  /**
   * Underlying bitmap.
   */
  private MutableRoaringBitmap bitmap;

  /**
   * Create a new WrappedRoaringBitmap wrapping an empty MutableRoaringBitmap
   */
  public WrappedRoaringBitmap()
  {
    this.bitmap = new MutableRoaringBitmap();
  }

  public MutableRoaringBitmap getBitmap()
  {
    return bitmap;
  }

  @Override
  public byte[] toBytes()
  {
    try {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      bitmap.serialize(new DataOutputStream(out));
      return out.toByteArray();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public int compareTo(ImmutableBitmap other)
  {
    return 0;
  }

  @Override
  public void clear()
  {
    this.bitmap.clear();
  }

  @Override
  public void or(MutableBitmap mutableBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) mutableBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    bitmap.or(unwrappedOtherBitmap);
  }

  @Override
  public void and(MutableBitmap mutableBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) mutableBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    bitmap.and(unwrappedOtherBitmap);
  }


  @Override
  public void andNot(MutableBitmap mutableBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) mutableBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    bitmap.andNot(unwrappedOtherBitmap);
  }


  @Override
  public void xor(MutableBitmap mutableBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) mutableBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    bitmap.xor(unwrappedOtherBitmap);
  }

  @Override
  public int getSizeInBytes()
  {
    return bitmap.serializedSizeInBytes();
  }

  @Override
  public void add(int entry)
  {
    bitmap.add(entry);
  }

  @Override
  public int size()
  {
    return bitmap.getCardinality();
  }

  @Override
  public void serialize(ByteBuffer buffer)
  {
    buffer.putInt(getSizeInBytes());
    try {
      bitmap.serialize(
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
                  mBB.put(b);
                }

                @Override
                public void write(byte[] b, int off, int l)
                {
                  mBB.put(b, off, l);
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
    return getClass().getSimpleName() + bitmap.toString();
  }

  @Override
  public void remove(int entry)
  {
    bitmap.remove(entry);
  }

  @Override
  public IntIterator iterator()
  {
    return bitmap.getIntIterator();
  }

  @Override
  public boolean isEmpty()
  {
    return bitmap.isEmpty();
  }

  @Override
  public ImmutableBitmap union(ImmutableBitmap otherBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) otherBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.or(bitmap, unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap intersection(ImmutableBitmap otherBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) otherBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.and(bitmap, unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap difference(ImmutableBitmap otherBitmap)
  {
    WrappedRoaringBitmap other = (WrappedRoaringBitmap) otherBitmap;
    MutableRoaringBitmap unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableRoaringBitmap(MutableRoaringBitmap.andNot(bitmap, unwrappedOtherBitmap));
  }

  @Override
  public boolean get(int value)
  {
    return bitmap.contains(value);
  }
}
