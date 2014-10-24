package com.metamx.collections.spatial;

import CompressedBitmaps.GenericBitmap;

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
public class Point extends Node
{
    private static GenericBitmap addToGenericBitmap(int entry, GenericBitmap bitmap)
    {   
        bitmap.add(entry);
        return bitmap;
    }

    private final float[] coords;
    private final GenericBitmap bitmap;

    public Point(float[] coords, int entry, GenericBitmap bitmap)
    {
        super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, addToGenericBitmap(entry, bitmap));

        this.coords = coords;
        this.bitmap = bitmap.getEmptyWrappedBitmap();
        this.bitmap.add(entry);
    }

    public Point(float[] coords, GenericBitmap bitmap)
    {
        super(coords, Arrays.copyOf(coords, coords.length), Lists.<Node>newArrayList(), true, null, bitmap);

        this.coords = coords;
        this.bitmap = bitmap;
    }

    public float[] getCoords()
    {
        return coords;
    }

    @Override
    public GenericBitmap getBitmap()
    {
        return this.bitmap;
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
