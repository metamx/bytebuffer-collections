package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.RoaringImmutableNode;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

/**
 */
public interface RoaringSearchStrategy
{
  public Iterable<ImmutableRoaringBitmap> search(RoaringImmutableNode node, RoaringBound bound);
}
