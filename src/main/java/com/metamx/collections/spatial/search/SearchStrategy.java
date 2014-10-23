package com.metamx.collections.spatial.search;

import CompressedBitmaps.ImmutableGenericBitmap;

import com.metamx.collections.spatial.ImmutableNode;

import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
/**
 */
public interface SearchStrategy
{
    public Iterable<ImmutableGenericBitmap> search(ImmutableNode node, Bound bound);
}
