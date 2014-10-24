package com.metamx.collections.spatial;

import com.google.common.collect.Lists;
import com.metamx.collections.spatial.CompressedBitmaps.GenericBitmap;

import java.util.Arrays;
import java.util.List;

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
        this.bitmap = bitmap;
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
}
