package com.metamx.collections.bitmap;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;
import com.google.common.collect.Lists;
import com.metamx.test.annotation.Benchmark;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Random;


@Category({Benchmark.class})
@BenchmarkOptions(clock = Clock.NANO_TIME)
public class BitmapBenchmarkTest
{
  public static final int LENGTH = 500_000;
  public static final int SIZE = 5_000;

  @Rule
  public TestRule benchmarkRun = new BenchmarkRule();

  final static ImmutableConciseSet concise[] = new ImmutableConciseSet[SIZE];
  final static ImmutableRoaringBitmap roaring[] = new ImmutableRoaringBitmap[SIZE];
  final static ImmutableGenericBitmap genericConcise[] = new ImmutableGenericBitmap[SIZE];
  final static ImmutableGenericBitmap genericRoaring[] = new ImmutableGenericBitmap[SIZE];
  final static ConciseBitmapFactory conciseFactory = new ConciseBitmapFactory();
  final static RoaringBitmapFactory roaringFactory = new RoaringBitmapFactory();
  final static Random rand = new Random(0);

  static long unionCount = 0;
  static long minIntersection = 0;


  @BeforeClass
  public static void prepareMostlyUniform() throws Exception
  {
    final BitSet expectedUnion = new BitSet();
    final int[] knownTrue = new int[50];
    for (int i = 0; i < knownTrue.length; ++i) {
      knownTrue[i] = rand.nextInt(LENGTH);
    }
    for(int i = 0; i < SIZE; ++i) {
      ConciseSet c = new ConciseSet();
      MutableRoaringBitmap r = new MutableRoaringBitmap();
      for(int k = 0; k < LENGTH; ++k) {
        if(rand.nextDouble() < .1) {
          c.add(k);
          r.add(k);
          expectedUnion.set(k);
        }
      }
      for(int k = 0; k < knownTrue.length; ++k) {
        c.add(knownTrue[k]);
        r.add(knownTrue[k]);
        expectedUnion.set(knownTrue[k]);
      }
      concise[i] = ImmutableConciseSet.newImmutableFromMutable(c);
      int size = r.serializedSizeInBytes();
      ByteBuffer buf = ByteBuffer.allocateDirect(size);
      final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
      r.serialize(new DataOutputStream(out));
      buf.put(out.toByteArray());
      buf.rewind();
      roaring[i] = new ImmutableRoaringBitmap(buf.asReadOnlyBuffer());
      genericConcise[i] = new WrappedImmutableConciseBitmap(concise[i]);
      genericRoaring[i] = new WrappedImmutableRoaringBitmap(roaring[i]);
    }
    unionCount = expectedUnion.cardinality();
    minIntersection = knownTrue.length;
  }

//  @BeforeClass
  public static void prepareRandomRanges() throws Exception
  {
    final BitSet expectedUnion = new BitSet();
    for(int i = 0; i < SIZE; ++i) {
      ConciseSet c = new ConciseSet();
      MutableRoaringBitmap r = new MutableRoaringBitmap();
      {
        int k = 0;
        boolean fill = true;
        while (k < LENGTH) {
          int runLength =  LENGTH / 100 + rand.nextInt(LENGTH / 100);
          for (int j = k; fill && j < LENGTH && j < k + runLength; ++j) {
            c.add(j);
            r.add(j);
            expectedUnion.set(j);
          }
          k += runLength;
          fill = !fill;
        }
      }
      minIntersection = LENGTH / 10;
      for(int k = LENGTH / 2; k < LENGTH / 2 + minIntersection; ++k) {
        c.add(k);
        r.add(k);
        expectedUnion.set(k);
      }
      concise[i] = ImmutableConciseSet.newImmutableFromMutable(c);
      roaring[i] = r.toImmutableRoaringBitmap();
      genericConcise[i] = new WrappedImmutableConciseBitmap(concise[i]);
      genericRoaring[i] = new WrappedImmutableRoaringBitmap(roaring[i]);
    }
    unionCount = expectedUnion.cardinality();
  }

  @Test @Ignore
  public void timeConciseUnion() throws Exception
  {
    ImmutableConciseSet union = ImmutableConciseSet.union(concise);
    Assert.assertEquals(unionCount, union.size());
  }

  @Test @Ignore
  public void timeGenericConciseUnion() throws Exception
  {
    ImmutableGenericBitmap union = conciseFactory.union(Lists.newArrayList(genericConcise));
    Assert.assertEquals(unionCount, union.size());
  }

  @Test
  public void timeRoaringUnion() throws Exception
  {
    ImmutableRoaringBitmap union = BufferFastAggregation.horizontal_or(Lists.newArrayList(roaring).iterator());
    Assert.assertEquals(unionCount, union.getCardinality());
  }


  @Test
  public void timeGenericRoaringUnion() throws Exception
  {
    ImmutableGenericBitmap union = roaringFactory.union(Lists.newArrayList(genericRoaring));
    Assert.assertEquals(unionCount, union.size());
  }

  @Test @Ignore
  public void timeGenericConciseIntersection() throws Exception
  {
    ImmutableGenericBitmap intersection = conciseFactory.intersection(Lists.newArrayList(genericConcise));
    Assert.assertTrue(intersection.size() >= minIntersection);
  }

  @Test
  public void timeGenericRoaringIntersection() throws Exception
  {
    ImmutableGenericBitmap intersection = roaringFactory.intersection(Lists.newArrayList(genericRoaring));
    Assert.assertTrue(intersection.size() >= minIntersection);
  }
}
