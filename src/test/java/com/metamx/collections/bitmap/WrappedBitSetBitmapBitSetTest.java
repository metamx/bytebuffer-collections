package com.metamx.collections.bitmap;

import com.google.common.collect.Sets;
import com.metamx.collections.IntSetTestUtility;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.IntIterator;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Set;

/**
 *
 */
public class WrappedBitSetBitmapBitSetTest
{

  private static final WrappedBitSetBitmap defaultBitSet(){
    return new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(IntSetTestUtility.getSetBits()));
  }
  @Test
  public void testIterator(){
    WrappedBitSetBitmap bitSet = new WrappedBitSetBitmap();
    for(int i : IntSetTestUtility.getSetBits()){
      bitSet.add(i);
    }
    IntIterator intIt = bitSet.iterator();
    for(int i : IntSetTestUtility.getSetBits()){
      Assert.assertTrue(intIt.hasNext());
      Assert.assertEquals(i,intIt.next());
    }
  }

  @Test
  public void testSize(){
    BitSet bitSet = IntSetTestUtility.createSimpleBitSet(IntSetTestUtility.getSetBits());
    WrappedBitSetBitmap wrappedBitSetBitmapBitSet = new WrappedBitSetBitmap(bitSet);
    Assert.assertEquals(bitSet.cardinality(), wrappedBitSetBitmapBitSet.size());
  }

  @Test
  public void testSimpleBitSet(){
    WrappedBitSetBitmap bitSet = new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(IntSetTestUtility.getSetBits()));
    Assert.assertTrue(IntSetTestUtility.equalSets(IntSetTestUtility.getSetBits(), bitSet));
  }

  @Test
  public void testUnion(){
    WrappedBitSetBitmap bitSet = new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(IntSetTestUtility.getSetBits()));

    Set<Integer> extraBits = Sets.newHashSet(6,9);
    WrappedBitSetBitmap bitExtraSet = new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(extraBits));

    Set<Integer> union = Sets.union(extraBits, IntSetTestUtility.getSetBits());

    Assert.assertTrue(IntSetTestUtility.equalSets(union, (WrappedBitSetBitmap) bitSet.union(bitExtraSet)));
  }
  @Test
  public void testIntersection(){
    WrappedBitSetBitmap bitSet = new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(IntSetTestUtility.getSetBits()));

    Set<Integer> extraBits = Sets.newHashSet(1,2,3,4,5,6,7,8);
    WrappedBitSetBitmap bitExtraSet = new WrappedBitSetBitmap(IntSetTestUtility.createSimpleBitSet(extraBits));

    Set<Integer> intersection = Sets.intersection(extraBits, IntSetTestUtility.getSetBits());

    Assert.assertTrue(IntSetTestUtility.equalSets(intersection, (WrappedBitSetBitmap) bitSet.intersection(bitExtraSet)));
  }

  @Test
  public void testAnd(){
    WrappedBitSetBitmap bitSet = defaultBitSet();
    WrappedBitSetBitmap bitSet2 = defaultBitSet();
    Set<Integer> defaultBitSet = IntSetTestUtility.getSetBits();
    bitSet.remove(1);
    bitSet2.remove(2);

    bitSet.and(bitSet2);

    defaultBitSet.remove(1);
    defaultBitSet.remove(2);

    Assert.assertTrue(IntSetTestUtility.equalSets(defaultBitSet,bitSet));
  }


  @Test
  public void testOr(){
    WrappedBitSetBitmap bitSet = defaultBitSet();
    WrappedBitSetBitmap bitSet2 = defaultBitSet();
    Set<Integer> defaultBitSet = IntSetTestUtility.getSetBits();
    bitSet.remove(1);
    bitSet2.remove(2);

    bitSet.or(bitSet2);

    Assert.assertTrue(IntSetTestUtility.equalSets(defaultBitSet,bitSet));
  }

  @Test
  public void testAndNot(){
    WrappedBitSetBitmap bitSet = defaultBitSet();
    WrappedBitSetBitmap bitSet2 = defaultBitSet();
    Set<Integer> defaultBitSet = Sets.newHashSet();
    bitSet.remove(1);
    bitSet2.remove(2);

    bitSet.andNot(bitSet2);

    defaultBitSet.add(2);

    Assert.assertTrue(IntSetTestUtility.equalSets(defaultBitSet,bitSet));
  }


  @Test
  public void testSerialize(){
    WrappedBitSetBitmap bitSet = defaultBitSet();
    Set<Integer> defaultBitSet = IntSetTestUtility.getSetBits();
    byte[] buffer = new byte[bitSet.getSizeInBytes()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    bitSet.serialize(byteBuffer);
  }
}
