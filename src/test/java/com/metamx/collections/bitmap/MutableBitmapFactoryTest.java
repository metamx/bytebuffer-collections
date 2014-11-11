package com.metamx.collections.bitmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metamx.collections.IntSetTestUtility;
import com.metamx.collections.IntegerSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 *
 */
public class MutableBitmapFactoryTest
{
  private static Iterable<Class<? extends MutableBitmap>> clazzes = Lists.newArrayList(
      WrappedBitSetBitmap.class,
      WrappedConciseBitmap.class,
      WrappedRoaringBitmap.class
  );
  @Test
  public void testCreation()
  {
    for(Class<? extends MutableBitmap> clazz : clazzes) {
      MutableBitmap mutableBitmap = MutableBitmapFactory.newEmpty(clazz);
      Assert.assertEquals(0,mutableBitmap.size());
      mutableBitmap.add(1);
      Assert.assertEquals(1,mutableBitmap.size());
    }
  }
}
