package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
/**
 */
public interface SearchStrategy
{
    public Iterable<ImmutableRoaringBitmap> search(ImmutableNode node, Bound bound);
}
