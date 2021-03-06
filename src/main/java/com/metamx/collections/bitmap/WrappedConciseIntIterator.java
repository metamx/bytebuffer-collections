/*
 * Copyright 2011 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
