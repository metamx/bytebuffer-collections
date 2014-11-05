package com.metamx.collections.bitmap;

import it.uniroma3.mat.extendedset.intset.IntSet;
import org.roaringbitmap.IntIterator;

/**
 */
public class WrappedConciseIntIterator implements IntIterator
{
  private final IntSet.IntIterator itr;

  public WrappedConciseIntIterator(IntSet.IntIterator itr)
  {
    this.itr = itr;
  }

  @Override
  public boolean hasNext()
  {
    return itr.hasNext();
  }

  @Override
  public int next()
  {
    return itr.next();
  }

  @Override
  public IntIterator clone()
  {
    return new WrappedConciseIntIterator(itr.clone());
  }
}
