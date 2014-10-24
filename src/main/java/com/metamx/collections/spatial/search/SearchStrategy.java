package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.CompressedBitmaps.ImmutableGenericBitmap;

import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
/**
 */
public interface SearchStrategy
{
    public Iterable<ImmutableGenericBitmap> search(ImmutableNode node, Bound bound);
}
