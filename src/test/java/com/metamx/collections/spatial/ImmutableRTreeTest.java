package com.metamx.collections.spatial;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metamx.collections.spatial.bitmap.BitmapFactory;
import com.metamx.collections.spatial.bitmap.ConciseBitmapFactory;
import com.metamx.collections.spatial.bitmap.ImmutableGenericBitmap;
import com.metamx.collections.spatial.bitmap.RoaringBitmapFactory;
import com.metamx.collections.spatial.bitmap.WrappedImmutableConciseBitmap;
import com.metamx.collections.spatial.bitmap.WrappedImmutableRoaringBitmap;
import com.metamx.collections.spatial.search.RadiusBound;
import com.metamx.collections.spatial.search.RectangularBound;
import com.metamx.collections.spatial.split.LinearGutmanSplitStrategy;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;
import junit.framework.Assert;

import org.junit.Test;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Set;

/**
 */
public class ImmutableRTreeTest
{
  @Test
  public void testToAndFromByteBuffer()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);

    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 1}, 2);
    tree.insert(new float[]{2, 2}, 3);
    tree.insert(new float[]{3, 3}, 4);
    tree.insert(new float[]{4, 4}, 5);

    ImmutableRTree firstTree = ImmutableRTree.newImmutableFromMutable(tree);
    ByteBuffer buffer = ByteBuffer.wrap(firstTree.toBytes());
    ImmutableRTree secondTree = new ImmutableRTree(buffer, bf);
    Iterable<ImmutableGenericBitmap> points = secondTree.search(new RadiusBound(new float[]{0, 0}, 10));
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 5);
    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }

  @Test
  public void testToAndFromByteBufferRoaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);

    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 1}, 2);
    tree.insert(new float[]{2, 2}, 3);
    tree.insert(new float[]{3, 3}, 4);
    tree.insert(new float[]{4, 4}, 5);

    ImmutableRTree firstTree = ImmutableRTree.newImmutableFromMutable(tree);
    ByteBuffer buffer = ByteBuffer.wrap(firstTree.toBytes());
    ImmutableRTree secondTree = new ImmutableRTree(buffer, bf);
    Iterable<ImmutableGenericBitmap> points = secondTree.search(new RadiusBound(new float[]{0, 0}, 10));   
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }
  
  @Test
  public void testSearchNoSplit()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{10, 10}, 10);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{27, 34}, 20);
    tree.insert(new float[]{106, 19}, 30);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{4, 72}, 40);
    tree.insert(new float[]{-4, -3}, 5);
    tree.insert(new float[]{119, -78}, 50);

    Assert.assertEquals(tree.getRoot().getChildren().size(), 10);

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5));
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }

  @Test
  public void testSearchNoSplitRoaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{10, 10}, 10);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{27, 34}, 20);
    tree.insert(new float[]{106, 19}, 30);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{4, 72}, 40);
    tree.insert(new float[]{-4, -3}, 5);
    tree.insert(new float[]{119, -78}, 50);

    Assert.assertEquals(tree.getRoot().getChildren().size(), 10);

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5));
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }
  
  @Test
  public void testSearchWithSplit()
  {
    BitmapFactory bf = new ConciseBitmapFactory(); 
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{-4, -3}, 5);

    Random rand = new Random();
    for (int i = 0; i < 95; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5));
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }

  @Test
  public void testSearchWithSplitRoaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory(); 
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{-4, -3}, 5);

    Random rand = new Random();
    for (int i = 0; i < 95; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5));
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }
  

  @Test
  public void testSearchWithSplit2()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, 0);
    tree.insert(new float[]{1.0f, 3.0f}, 1);
    tree.insert(new float[]{4.0f, 2.0f}, 2);
    tree.insert(new float[]{7.0f, 3.0f}, 3);
    tree.insert(new float[]{8.0f, 6.0f}, 4);

    Random rand = new Random();
    for (int i = 5; i < 5000; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RectangularBound(
            new float[]{0, 0},
            new float[]{9, 9}
        )
    );
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 5);

    Set<Integer> expected = Sets.newHashSet(0, 1, 2, 3, 4);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }

  @Test
  public void testSearchWithSplit2Roaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, 0);
    tree.insert(new float[]{1.0f, 3.0f}, 1);
    tree.insert(new float[]{4.0f, 2.0f}, 2);
    tree.insert(new float[]{7.0f, 3.0f}, 3);
    tree.insert(new float[]{8.0f, 6.0f}, 4);

    Random rand = new Random();
    for (int i = 5; i < 5000; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RectangularBound(
            new float[]{0, 0},
            new float[]{9, 9}
        )
    );
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 5);

    Set<Integer> expected = Sets.newHashSet(0, 1, 2, 3, 4);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }
  
  @Test
  public void testSearchWithSplit3()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, 0);
    tree.insert(new float[]{1.0f, 3.0f}, 1);
    tree.insert(new float[]{4.0f, 2.0f}, 2);
    tree.insert(new float[]{7.0f, 3.0f}, 3);
    tree.insert(new float[]{8.0f, 6.0f}, 4);

    Random rand = new Random();
    for (int i = 5; i < 5000; i++) {
      tree.insert(
          new float[]{(float) (rand.nextFloat() * 10 + 10.0), (float) (rand.nextFloat() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RadiusBound(new float[]{0.0f, 0.0f}, 5)
    );
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 3);

    Set<Integer> expected = Sets.newHashSet(0, 1, 2);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }

  @Test
  public void testSearchWithSplit3Roaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, 0);
    tree.insert(new float[]{1.0f, 3.0f}, 1);
    tree.insert(new float[]{4.0f, 2.0f}, 2);
    tree.insert(new float[]{7.0f, 3.0f}, 3);
    tree.insert(new float[]{8.0f, 6.0f}, 4);

    Random rand = new Random();
    for (int i = 5; i < 5000; i++) {
      tree.insert(
          new float[]{(float) (rand.nextFloat() * 10 + 10.0), (float) (rand.nextFloat() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RadiusBound(new float[]{0.0f, 0.0f}, 5)
    );
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 3);

    Set<Integer> expected = Sets.newHashSet(0, 1, 2);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }  

  @Test
  public void testEmptyConciseSet()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, bf.getEmptyBitmap());

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RadiusBound(new float[]{0.0f, 0.0f}, 5)
    );
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertEquals(finalSet.size(), 0);
  }

  @Test
  public void testEmptyRoaringBitmap()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0.0f, 0.0f}, bf.getEmptyBitmap());

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(
        new RadiusBound(new float[]{0.0f, 0.0f}, 5)
    );
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertEquals(finalSet.getCardinality(), 0);
    Assert.assertTrue(finalSet.isEmpty());
  }

  @Test
  public void testSearchWithSplitLimitedBound()
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{-4, -3}, 5);

    Random rand = new Random();
    for (int i = 0; i < 4995; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5, 2));
    ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);
    Assert.assertTrue(finalSet.size() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntSet.IntIterator iter = finalSet.iterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }


  @Test
  public void testSearchWithSplitLimitedBoundRoaring()
  {
    BitmapFactory bf = new RoaringBitmapFactory();
    RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);
    tree.insert(new float[]{0, 0}, 1);
    tree.insert(new float[]{1, 3}, 2);
    tree.insert(new float[]{4, 2}, 3);
    tree.insert(new float[]{5, 0}, 4);
    tree.insert(new float[]{-4, -3}, 5);

    Random rand = new Random();
    for (int i = 0; i < 4995; i++) {
      tree.insert(
          new float[]{(float) (rand.nextDouble() * 10 + 10.0), (float) (rand.nextDouble() * 10 + 10.0)},
          i
      );
    }

    ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
    Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{0, 0}, 5, 2));
    ImmutableRoaringBitmap finalSet = WrappedImmutableRoaringBitmap.union(points);
    Assert.assertTrue(finalSet.getCardinality() >= 5);

    Set<Integer> expected = Sets.newHashSet(1, 2, 3, 4, 5);
    IntIterator iter = finalSet.getIntIterator();
    while (iter.hasNext()) {
      Assert.assertTrue(expected.contains(iter.next()));
    }
  }
  //@Test
  public void showBenchmarks()
  {
    final int start = 1;
    final int factor = 10;
    final int end = 10000000;
    final int radius = 10;

    for (int numPoints = start; numPoints <= end; numPoints *= factor) {
      try {
        BitmapFactory bf = new ConciseBitmapFactory();
        RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);

        Stopwatch stopwatch = new Stopwatch().start();
        Random rand = new Random();
        for (int i = 0; i < numPoints; i++) {
          tree.insert(new float[]{(float) (rand.nextDouble() * 100), (float) (rand.nextDouble() * 100)}, i);
        }
        long stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: insert = %,d ms%n", numPoints, stop);

        stopwatch.reset().start();
        ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
        stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: size = %,d bytes%n", numPoints, searchTree.toBytes().length);
        System.out.printf("[%,d]: buildImmutable = %,d ms%n", numPoints, stop);

        stopwatch.reset().start();

        Iterable<ImmutableGenericBitmap> points = searchTree.search(new RadiusBound(new float[]{50, 50}, radius));

        Iterables.size(points);
        stop = stopwatch.elapsedMillis();

        System.out.printf("[%,d]: search = %,dms%n", numPoints, stop);

        stopwatch.reset().start();

        ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);

        stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: union of %,d points in %,d ms%n", numPoints, finalSet.size(), stop);
      }
      catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }
  }

  //@Test
  public void showBenchmarksBoundWithLimits()
  {
    //final int start = 1;
    final int start = 10000000;
    final int factor = 10;
    final int end = 10000000;
    //final int end = 10;

    for (int numPoints = start; numPoints <= end; numPoints *= factor) {
      try {
        BitmapFactory bf = new ConciseBitmapFactory();
        RTree tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf);

        Stopwatch stopwatch = new Stopwatch().start();
        Random rand = new Random();
        for (int i = 0; i < numPoints; i++) {
          tree.insert(new float[]{(float) (rand.nextDouble() * 100), (float) (rand.nextDouble() * 100)}, i);
        }
        long stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: insert = %,d ms%n", numPoints, stop);

        stopwatch.reset().start();
        ImmutableRTree searchTree = ImmutableRTree.newImmutableFromMutable(tree);
        stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: size = %,d bytes%n", numPoints, searchTree.toBytes().length);
        System.out.printf("[%,d]: buildImmutable = %,d ms%n", numPoints, stop);

        stopwatch.reset().start();

        Iterable<ImmutableGenericBitmap> points = searchTree.search(
            new RectangularBound(
                new float[]{40, 40},
                new float[]{60, 60},
                100
            )
        );

        Iterables.size(points);
        stop = stopwatch.elapsedMillis();

        System.out.printf("[%,d]: search = %,dms%n", numPoints, stop);

        stopwatch.reset().start();

        ImmutableConciseSet finalSet = WrappedImmutableConciseBitmap.union(points);

        stop = stopwatch.elapsedMillis();
        System.out.printf("[%,d]: union of %,d points in %,d ms%n", numPoints, finalSet.size(), stop);
      }
      catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }
  }
}
