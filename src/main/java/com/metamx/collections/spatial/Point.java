package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.metamx.collections.spatial.bitmap.BitmapFactory;
import com.metamx.collections.spatial.bitmap.GenericBitmap;
import java.util.Arrays;
import java.util.List;

/**
 */
public class Point extends Node
{
  private static GenericBitmap makeBitmap(int entry, BitmapFactory bf)
  {
    GenericBitmap retVal = bf.getEmptyBitmap();
    retVal.add(entry);
    return retVal;
  }

  private final float[] coords;
  private final GenericBitmap conciseSet;

  public Point(float[] coords, int entry, BitmapFactory bf)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, makeBitmap(entry,bf));

    this.coords = coords;
    this.conciseSet = bf.getEmptyBitmap();
    this.conciseSet.add(entry);
  }

  public Point(float[] coords, GenericBitmap entry)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, entry);

    this.coords = coords;
    this.conciseSet = entry;
  }

  public float[] getCoords()
  {
    return coords;
  }

  @Override
  public GenericBitmap getBitmap()
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
