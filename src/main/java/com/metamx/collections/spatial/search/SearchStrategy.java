package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;

/**
 */
public interface SearchStrategy
{
  public Iterable<Integer> search(ImmutableNode node, Bound bound);
}
