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
public class RangeBitmapBenchmarkTest extends BitmapBenchmark
{

  public static final double DENSITY = 0.001;
  public static final int MIN_INTERSECT = 50;

  @BeforeClass
  public static void prepareRandomRanges() throws Exception
  {
    System.setProperty("jub.customkey", String.format("%06.5f", DENSITY));
    reset();

    final BitSet expectedUnion = new BitSet();
    for (int i = 0; i < SIZE; ++i) {
      ConciseSet c = new ConciseSet();
      MutableRoaringBitmap r = new MutableRoaringBitmap();
      {
        int k = 0;
        boolean fill = true;
        while (k < LENGTH) {
          int runLength = (int) (LENGTH * DENSITY) + rand.nextInt((int) (LENGTH * DENSITY));
          for (int j = k; fill && j < LENGTH && j < k + runLength; ++j) {
            c.add(j);
            r.add(j);
            expectedUnion.set(j);
          }
          k += runLength;
          fill = !fill;
        }
      }
      minIntersection = MIN_INTERSECT;
      for (int k = LENGTH / 2; k < LENGTH / 2 + minIntersection; ++k) {
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
    printSizeStats(DENSITY, "Random Alternating Bitmap");
  }
}
