package com.metamx.collections.bitmap;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;
import org.roaringbitmap.IntIterator;

import java.nio.ByteBuffer;

public class WrappedImmutableConciseBitmap implements ImmutableGenericBitmap
{
  /**
   * Underlying bitmap.
   */
  private final ImmutableConciseSet invertedIndex;

  public WrappedImmutableConciseBitmap(ByteBuffer byteBuffer)
  {
    this.invertedIndex = new ImmutableConciseSet(byteBuffer.asReadOnlyBuffer());
  }

  /**
   * Wrap an ImmutableConciseSet
   *
   * @param immutableConciseSet bitmap to be wrapped
   */
  public WrappedImmutableConciseBitmap(ImmutableConciseSet immutableConciseSet)
  {
    this.invertedIndex = immutableConciseSet;
  }

  public ImmutableConciseSet getInvertedIndex()
  {
    return invertedIndex;
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + invertedIndex.toString();
  }

  @Override
  public IntIterator iterator()
  {
    final IntSet.IntIterator i = invertedIndex.iterator();
    return new IntIterator()
    {
      @Override
      public IntIterator clone()
      {
        throw new RuntimeException("clone is not supported on ConciseSet iterator");
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
    return invertedIndex.size();
  }

  @Override
  public boolean isEmpty()
  {
    return invertedIndex.size() == 0;
  }

  @Override
  public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
    ImmutableConciseSet otherIndex = other.invertedIndex;
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.union(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
    ImmutableConciseSet otherIndex = other.invertedIndex;
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.intersection(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
    ImmutableConciseSet otherIndex = other.invertedIndex;
    return new WrappedImmutableConciseBitmap(
        ImmutableConciseSet.intersection(
            invertedIndex,
            ImmutableConciseSet.complement(otherIndex)
        )
    );
  }
}
