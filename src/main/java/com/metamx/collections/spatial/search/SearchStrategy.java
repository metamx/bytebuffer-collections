package com.metamx.collections.spatial.search;

import com.metamx.collections.spatial.ImmutableNode;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

/**
 */
public interface SearchStrategy
{
  public Iterable<ImmutableConciseSet> search(ImmutableNode node, Bound bound);
}
