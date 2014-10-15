package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

public class RoaringBitmapFactory extends BitmapFactory {
    @Override
    public GenericBitmap getEmptyBitmap() {
        return new WrappedRoaringBitmap();
    }

    @Override
    public ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b) {
        return new WrappedImmutableRoaringBitmap(b);
    }

}
