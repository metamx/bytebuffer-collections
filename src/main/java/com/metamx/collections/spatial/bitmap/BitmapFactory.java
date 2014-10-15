package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

public abstract class BitmapFactory {
    public abstract GenericBitmap getEmptyBitmap();

    public abstract ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b);
}
