package com.metamx.collections.bitmap;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * BitSetBitmapFactory implements BitmapFactory as a wrapper for java.util.BitSet
 * Created by charlesallen on 11/5/14.
 */
public class BitSetBitmapFactory implements BitmapFactory
{
  @Override
  public MutableBitmap makeEmptyMutableBitmap()
  {
    return new WrappedBitSetBitmap();
  }

  @Override
  public ImmutableBitmap makeEmptyImmutableBitmap()
  {
    return makeEmptyMutableBitmap();
  }

  @Override
  public ImmutableBitmap makeImmutableBitmap(MutableBitmap mutableBitmap)
  {
    return mutableBitmap;
  }

  @Override
  public ImmutableBitmap mapImmutableBitmap(ByteBuffer b)
  {
    return new WrappedBitSetBitmap(BitSet.valueOf(b.array()));
  }

  @Override
  public ImmutableBitmap union(Iterable<ImmutableBitmap> b)
  {
    WrappedBitSetBitmap newSet = null;
    for (ImmutableBitmap bm : b) {
      if (null == newSet) {
        newSet = new WrappedBitSetBitmap(((WrappedBitSetBitmap) bm).cloneBitSet());
      } else {
        newSet.union(bm);
      }
    }
    return newSet;
  }

  @Override
  public ImmutableBitmap intersection(Iterable<ImmutableBitmap> b)
  {

    WrappedBitSetBitmap newSet = null;
    for (ImmutableBitmap bm : b) {
      if (null == newSet) {
        newSet = new WrappedBitSetBitmap(((WrappedBitSetBitmap) bm).cloneBitSet());
      } else {
        newSet.intersection(bm);
      }
    }
    return newSet;
  }

  @Override
  public ImmutableBitmap complement(ImmutableBitmap b)
  {
    BitSet bitSet = ((WrappedBitSetBitmap) b).cloneBitSet();
    bitSet.flip(0, bitSet.size());
    return new WrappedBitSetBitmap(bitSet);
  }

  @Override
  public ImmutableBitmap complement(
      ImmutableBitmap b, int length
  )
  {
    return null;
  }
}
