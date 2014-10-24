package com.metamx.collections.bitmap;

import com.google.common.primitives.Ints;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;
import org.roaringbitmap.IntIterator;

import java.nio.ByteBuffer;

public class WrappedConciseBitmap implements GenericBitmap
{
  /**
   * Underlying bitmap.
   */
  private final ConciseSet invertedIndex;

  /**
   * Create a new WrappedConciseBitmap wrapping an empty  ConciseSet
   */
  public WrappedConciseBitmap()
  {
    this.invertedIndex = new ConciseSet();
  }

  /**
   * Create a bitmap wrappign the given bitmap
   *
   * @param conciseSet bitmap to be wrapped
   */
  public WrappedConciseBitmap(ConciseSet conciseSet)
  {
    this.invertedIndex = conciseSet;
  }

  public ConciseSet getInvertedIndex()
  {
    return invertedIndex;
  }

  @Override
  public void clear()
  {
    invertedIndex.clear();
  }

  @Override
  public void or(GenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    invertedIndex.addAll(otherIndex);
  }

  @Override
  public void and(GenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    invertedIndex.intersection(otherIndex);
  }

  @Override
  public void xor(GenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    invertedIndex.symmetricDifference(otherIndex);
  }

  @Override
  public void andNot(GenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    invertedIndex.difference(otherIndex);
  }

  @Override
  public int getSizeInBytes()
  {
    return invertedIndex.getWords().length * Ints.BYTES + Ints.BYTES;
  }

  @Override
  public void add(int entry)
  {
    invertedIndex.add(entry);
  }

  @Override
  public int size()
  {
    return invertedIndex.size();
  }

  @Override
  public void serialize(ByteBuffer buffer)
  {
    byte[] bytes = ImmutableConciseSet.newImmutableFromMutable(invertedIndex).toBytes();
    buffer.putInt(bytes.length);
    buffer.put(bytes);
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
    final IntSet.IntIterator i = invertedIndex.iterator();
    return new IntIterator()
    {
      @Override
      public IntIterator clone()
      {
        throw new UnsupportedOperationException("clone is not supported on ConciseSet iterator");
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
  public boolean isEmpty()
  {
    return invertedIndex.size() == 0;
  }

  @Override
  public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    return new WrappedConciseBitmap(invertedIndex.clone().union(otherIndex));
  }

  @Override
  public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    return new WrappedConciseBitmap(invertedIndex.clone().intersection(otherIndex));
  }

  @Override
  public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
    ConciseSet otherIndex = other.invertedIndex;
    return new WrappedConciseBitmap(invertedIndex.clone().difference(otherIndex));
  }
}
