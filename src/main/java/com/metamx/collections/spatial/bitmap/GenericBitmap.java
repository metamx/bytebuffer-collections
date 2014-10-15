package com.metamx.collections.spatial.bitmap;

public abstract class GenericBitmap {

    public abstract void clear();

    public abstract void or(GenericBitmap bitmap);

    public abstract int getSizeInBytes();

    public abstract ImmutableGenericBitmap toImmutable();

    public abstract void add(int entry);
    
    public abstract int size();


}
