package com.metamx.collections.bitmap;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;
import org.roaringbitmap.IntIterator;

import java.nio.ByteBuffer;

public class WrappedImmutableConciseBitmap implements ImmutableBitmap
{
  /**
   * Underlying bitmap.
   */
  private final ImmutableConciseSet bitmap;

  public WrappedImmutableConciseBitmap(ByteBuffer byteBuffer)
  {
    this.bitmap = new ImmutableConciseSet(byteBuffer.asReadOnlyBuffer());
  }

  /**
   * Wrap an ImmutableConciseSet
   *
   * @param immutableConciseSet bitmap to be wrapped
   */
  public WrappedImmutableConciseBitmap(ImmutableConciseSet immutableConciseSet)
  {
    this.bitmap = immutableConciseSet;
  }

  public ImmutableConciseSet getBitmap()
  {
    return bitmap;
  }

  @Override
  public boolean get(int value)
  {
    return bitmap.get(value) > 0;
  }

  @Override
  public byte[] toBytes()
  {
    return bitmap.toBytes();
  }

  @Override
  public int compareTo(ImmutableBitmap other)
  {
    return bitmap.compareTo(((WrappedImmutableConciseBitmap) other).getBitmap());
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + bitmap.toString();
  }

  @Override
  public IntIterator iterator()
  {
    final IntSet.IntIterator i = bitmap.iterator();
    return new IntIterator()
    {
      @Override
      public IntIterator clone()
      {
        return new WrappedConciseIntIterator(i.clone());
      }

      @Override
      public boolean hasNext()
      {
        return i.hasNext();
      }

      @Override
      public int next()
      {
        return i.next();
      }
    };
  }

  @Override
  public int size()
  {
    return bitmap.size();
  }

  @Override
  public boolean isEmpty()
  {
    return bitmap.size() == 0;
  }

  @Override
  public ImmutableBitmap union(ImmutableBitmap otherBitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) otherBitmap;
    ImmutableConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.union(bitmap, unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap intersection(ImmutableBitmap otherBitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) otherBitmap;
    ImmutableConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.intersection(bitmap, unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap difference(ImmutableBitmap otherBitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) otherBitmap;
    ImmutableConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedImmutableConciseBitmap(
        ImmutableConciseSet.intersection(
            bitmap,
            ImmutableConciseSet.complement(unwrappedOtherBitmap)
        )
    );
  }
}
