package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.metamx.collections.bitmap.BitmapFactory;
import com.metamx.collections.bitmap.GenericBitmap;

import java.util.Arrays;
import java.util.List;

/**
 */
public class Point extends Node
{
  private static GenericBitmap makeBitmap(int entry, BitmapFactory bitmapFactory)
  {
    GenericBitmap retVal = bitmapFactory.getEmptyBitmap();
    retVal.add(entry);
    return retVal;
  }

  private final float[] coords;
  private final GenericBitmap invertedIndex;

  public Point(float[] coords, int entry, BitmapFactory bitmapFactory)
  {
    super(
        coords,
        Arrays.copyOf(coords, coords.length),
        Lists.<Node>newArrayList(),
        true,
        null,
        makeBitmap(entry, bitmapFactory)
    );

    this.coords = coords;
    this.invertedIndex = bitmapFactory.getEmptyBitmap();
    this.invertedIndex.add(entry);
  }

  public Point(float[] coords, GenericBitmap entry)
  {
    super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, entry);

    this.coords = coords;
    this.invertedIndex = entry;
  }

  public float[] getCoords()
  {
    return coords;
  }

  @Override
  public GenericBitmap getBitmap()
  {
    return invertedIndex;
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
}
