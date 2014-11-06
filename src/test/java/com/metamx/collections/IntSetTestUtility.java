package com.metamx.collections;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metamx.collections.bitmap.ImmutableBitmap;
import com.metamx.collections.bitmap.MutableBitmap;
import org.roaringbitmap.IntIterator;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by charlesallen on 11/5/14.
 */
public class IntSetTestUtility
{

  private static Set<Integer> setBits = Sets.newTreeSet(Lists.newArrayList(1, 2, 3, 5, 8, 13, 21));
  public static Set<Integer> getSetBits(){
    return Sets.newTreeSet(setBits);
  }
  public static final BitSet createSimpleBitSet(Set<Integer> setBits){
    BitSet retval = new BitSet();
    for(int i : setBits){
      retval.set(i);
    }
    return retval;
  }

  public static final void addAllToMutable(MutableBitmap mutableBitmap, Iterable<Integer> intSet){
    for(Integer integer : intSet){
      mutableBitmap.add(integer);
    }
  }

  private static class IntIt implements Iterable<Integer>
  {
    private final Iterator<Integer> intIter;
    public IntIt(IntIterator intIt){
      this.intIter = new IntIter(intIt);
    }

    @Override
    public Iterator<Integer> iterator()
    {
      return intIter;
    }

    private static class IntIter implements Iterator<Integer>
    {
      private final IntIterator intIt;

      public IntIter(IntIterator intIt)
      {
        this.intIt = intIt;
      }

      @Override
      public boolean hasNext()
      {
        return intIt.hasNext();
      }

      @Override
      public Integer next()
      {
        return intIt.next();
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("Cannot remove ints from int iterator");
      }
    }
  }

  public static Boolean equalSets(Set<Integer> s1, ImmutableBitmap s2){
    Set<Integer> s3 = new HashSet<>();
    for(Integer i : new IntIt(s2.iterator())){
      s3.add(i);
    }
    return Sets.difference(s1,s3).isEmpty();
  }
}
