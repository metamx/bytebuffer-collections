package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 */
public class RoaringPoint extends RoaringNode
{
  private static MutableRoaringBitmap makeRoaringBitmap(int entry)
  {
    MutableRoaringBitmap retVal = new MutableRoaringBitmap();
    retVal.add(entry);
    return retVal;
  }

  private final float[] coords;
  private final MutableRoaringBitmap roaring;

  public RoaringPoint(float[] coords, int entry)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<RoaringNode>newArrayList(), true, null, makeRoaringBitmap(entry));

    this.coords = coords;
    this.roaring = new MutableRoaringBitmap();
    this.roaring.add(entry);
  }

  public RoaringPoint(float[] coords, MutableRoaringBitmap entry)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<RoaringNode>newArrayList(), true, null, entry);

    this.coords = coords;
    this.roaring = entry;
  }

  public float[] getCoords()
  {
    return coords;
  }

  @Override
  public MutableRoaringBitmap getRoaringBitmap()
  {
    return this.roaring;
  }

  @Override
  public void addChild(RoaringNode node)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RoaringNode> getChildren()
  {
    return Lists.newArrayList();
  }

  @Override
  public boolean isLeaf()
  {
    return true;
  }

  @Override
  public double getArea()
  {
    return 0;
  }

  @Override
  public boolean contains(RoaringNode other)
  {
    return false;
  }

  @Override
  public boolean enclose()
  {
    return false;
  }
  //
  //@Override
  //public int getSizeInBytes()
  //{
  //  return coords.length * Floats.BYTES
  //         + Ints.BYTES // size of conciseSet
  //         + conciseSet.getWords().length * Ints.BYTES;
  //}
  //
  //@Override
  //public int storeInByteBuffer(ByteBuffer buffer, int position)
  //{
  //  buffer.position(position);
  //  for (float v : getCoords()) {
  //    buffer.putFloat(v);
  //  }
  //  byte[] bytes = ImmutableConciseSet.newImmutableFromMutable(conciseSet).toBytes();
  //  buffer.putInt(bytes.length);
  //  buffer.put(bytes);
  //
  //  return buffer.position();
  //}
}
