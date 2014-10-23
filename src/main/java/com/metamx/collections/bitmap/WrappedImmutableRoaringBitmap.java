package com.metamx.collections.bitmap;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.nio.ByteBuffer;

public class WrappedImmutableRoaringBitmap implements ImmutableGenericBitmap
{
  /**
   * Underlying bitmap.
   */
  private final ImmutableRoaringBitmap invertedIndex;

  protected WrappedImmutableRoaringBitmap(ByteBuffer byteBuffer)
  {
    this.invertedIndex = new ImmutableRoaringBitmap(byteBuffer.asReadOnlyBuffer());
  }

  /**
   * Wrap an ImmutableRoaringBitmap
   *
   * @param immutableRoaringBitmap bitmap to be wrapped
   */
  public WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap immutableRoaringBitmap)
  {
    this.invertedIndex = immutableRoaringBitmap;
  }

  public ImmutableRoaringBitmap getInvertedIndex()
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
    return invertedIndex.getIntIterator();
  }

  @Override
  public int size()
  {
    return invertedIndex.getCardinality();
  }

  @Override
  public boolean isEmpty()
  {
    return invertedIndex.isEmpty();
  }

  @Override
  public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
    ImmutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.or(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
    ImmutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.and(invertedIndex, otherIndex));
  }

  @Override
  public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap)
  {
    WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
    ImmutableRoaringBitmap otherIndex = other.invertedIndex;
    return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.andNot(invertedIndex, otherIndex));
  }
}
