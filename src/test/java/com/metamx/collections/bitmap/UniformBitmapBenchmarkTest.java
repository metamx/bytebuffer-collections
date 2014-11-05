package com.metamx.collections.bitmap;

import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import com.metamx.test.annotation.Benchmark;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.BitSet;

@Category({Benchmark.class})
@BenchmarkHistoryChart(labelWith = LabelType.CUSTOM_KEY, maxRuns = 20)
public class UniformBitmapBenchmarkTest extends BitmapBenchmark
{

  public static final double DENSITY = 0.01;
  public static final int MIN_INTERSECT = 50;

  @BeforeClass
  public static void prepareMostlyUniform() throws Exception
  {
    System.setProperty("jub.customkey", String.format("%05.4f", DENSITY));
    reset();

    final BitSet expectedUnion = new BitSet();
    final int[] knownTrue = new int[MIN_INTERSECT];
    for (int i = 0; i < knownTrue.length; ++i) {
      knownTrue[i] = rand.nextInt(LENGTH);
    }
    for (int i = 0; i < SIZE; ++i) {
      ConciseSet c = new ConciseSet();
      MutableRoaringBitmap r = new MutableRoaringBitmap();
      for (int k = 0; k < LENGTH; ++k) {
        if (rand.nextDouble() < DENSITY) {
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
      offheapConcise[i] = makeOffheapConcise(concise[i]);
      roaring[i] = r;
      immutableRoaring[i] = makeImmutableRoaring(r);
      offheapRoaring[i] = makeOffheapRoaring(r);
      genericConcise[i] = new WrappedImmutableConciseBitmap(offheapConcise[i]);
      genericRoaring[i] = new WrappedImmutableRoaringBitmap(offheapRoaring[i]);
    }
    unionCount = expectedUnion.cardinality();
    minIntersection = knownTrue.length;
    printSizeStats(DENSITY, "Uniform Bitmap");
  }
}
