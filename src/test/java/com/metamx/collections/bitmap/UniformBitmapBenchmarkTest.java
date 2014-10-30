package com.metamx.collections.bitmap;

import com.metamx.test.annotation.Benchmark;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.BitSet;

@Category({Benchmark.class})
public class UniformBitmapBenchmarkTest extends BitmapBenchmark
{
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
      for (int k : knownTrue) {
        c.add(k);
        r.add(k);
        expectedUnion.set(k);
      }
      concise[i] = ImmutableConciseSet.newImmutableFromMutable(c);
      offheapConcise[i] = makeOffheapConcise(concise[i]);;
      roaring[i] = r;
      immutableRoaring[i] = makeImmutable(r);
      offheapRoaring[i] = makeOffheap(r);
      genericConcise[i] = new WrappedImmutableConciseBitmap(offheapConcise[i]);
      genericRoaring[i] = new WrappedImmutableRoaringBitmap(offheapRoaring[i]);
    }
    unionCount = expectedUnion.cardinality();
    minIntersection = knownTrue.length;
    printSizeStats();
  }
}
