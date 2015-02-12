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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ConciseBitmapFactoryTest
{
  @Test
  public void testUnwrapWithNull() throws Exception
  {
    ConciseBitmapFactory factory = new ConciseBitmapFactory();

    ImmutableBitmap bitmap = factory.union(
        Iterables.transform(
            Lists.newArrayList(new WrappedConciseBitmap()),
            new Function<WrappedConciseBitmap, ImmutableBitmap>()
            {
              @Override
              public ImmutableBitmap apply(WrappedConciseBitmap input)
              {
                return null;
              }
            }
        )
    );

    Assert.assertEquals(0, bitmap.size());
  }

  @Test
  public void testUnwrapMerge() throws Exception
  {
    ConciseBitmapFactory factory = new ConciseBitmapFactory();

    WrappedConciseBitmap set = new WrappedConciseBitmap();
    set.add(1);
    set.add(3);
    set.add(5);

    ImmutableBitmap bitmap = factory.union(
        Arrays.asList(
            factory.makeImmutableBitmap(set),
            null
        )
    );

    Assert.assertEquals(3, bitmap.size());
  }
}
