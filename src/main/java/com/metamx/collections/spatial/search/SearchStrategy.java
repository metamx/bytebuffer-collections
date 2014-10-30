package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;
//import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import com.metamx.collections.bitmap.ImmutableGenericBitmap;

/**
 */
public interface SearchStrategy
{
  public Iterable<ImmutableGenericBitmap> search(ImmutableNode node, Bound bound);
}
