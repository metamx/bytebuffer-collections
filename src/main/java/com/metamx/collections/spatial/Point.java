package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 */
public class Point extends Node
{
  private static ConciseSet makeConciseSet(int entry)
  {
    ConciseSet retVal = new ConciseSet();
    retVal.add(entry);
    return retVal;
  }

  private final float[] coords;
  private final ConciseSet conciseSet;

  public Point(float[] coords, int entry)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, makeConciseSet(entry));

    this.coords = coords;
    this.conciseSet = new ConciseSet();
    this.conciseSet.add(entry);
  }

  public float[] getCoords()
  {
    return coords;
  }

  @Override
  public ConciseSet getConciseSet()
  {
    return conciseSet;
  }

  @Override
  public void addChild(Node node)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Node> getChildren()
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
  public boolean contains(Node other)
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
