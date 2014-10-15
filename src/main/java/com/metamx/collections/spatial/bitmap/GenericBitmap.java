package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

public abstract class GenericBitmap {

    public abstract void clear();

    public abstract void or(GenericBitmap bitmap);

    public abstract int getSizeInBytes();

    public abstract void add(int entry);
    
    public abstract int size();

    public abstract void serialize(ByteBuffer buffer);


}
