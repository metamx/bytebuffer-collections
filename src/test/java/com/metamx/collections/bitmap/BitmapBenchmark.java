package com.metamx.collections.bitmap;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.google.common.collect.Lists;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.Random;


public class BitmapBenchmark
{
  @Rule
  public TestRule benchmarkRun = new BenchmarkRule();

  public static final int SIZE = 500;
  final static ImmutableConciseSet concise[] = new ImmutableConciseSet[SIZE];
  final static ImmutableRoaringBitmap roaring[] = new ImmutableRoaringBitmap[SIZE];
  final static ImmutableGenericBitmap generic[] = new ImmutableGenericBitmap[SIZE];
  final static ImmutableGenericBitmap genericRoaring[] = new ImmutableGenericBitmap[SIZE];
  final static ConciseBitmapFactory conciseFactory = new ConciseBitmapFactory();
  final static RoaringBitmapFactory roaringFactory = new RoaringBitmapFactory();

  @BeforeClass
  public static void prepare() throws Exception
  {
    final Random rand = new Random();
    for(int i = 0; i < SIZE; ++i) {
      ConciseSet c = new ConciseSet();
      MutableRoaringBitmap r = new MutableRoaringBitmap();
      for(int k = 0; k < 500_000; ++k) {
        if(rand.nextDouble() < .5) {
          c.add(k);
          r.add(k);
        }
      }
      concise[i] = ImmutableConciseSet.newImmutableFromMutable(c);
      roaring[i] = r.toImmutableRoaringBitmap();
      Assert.assertTrue(roaring[i].getCardinality() > 0);
      generic[i] = new WrappedImmutableConciseBitmap(concise[i]);
      genericRoaring[i] = new WrappedImmutableRoaringBitmap(roaring[i]);
    }
  }

  @Test
  public void timeConciseUnion() throws Exception
  {
    ImmutableConciseSet union = ImmutableConciseSet.union(concise);
    Assert.assertTrue(union.size() > 0);
  }

  @Test
  public void timeGenericConciseUnion() throws Exception
  {
    ImmutableGenericBitmap union = conciseFactory.union(Lists.newArrayList(generic));
    Assert.assertTrue(union.size() > 0);
  }

  @Test
  public void timeRoaringUnion() throws Exception
  {
    ImmutableRoaringBitmap union = BufferFastAggregation.horizontal_or(Lists.newArrayList(roaring).iterator());
    Assert.assertTrue(union.getCardinality() > 0);
  }


  @Test
  public void timeGenericRoaringUnion() throws Exception
  {
    ImmutableGenericBitmap union = roaringFactory.union(Lists.newArrayList(genericRoaring));
    Assert.assertTrue(union.size() > 0);
  }
}
