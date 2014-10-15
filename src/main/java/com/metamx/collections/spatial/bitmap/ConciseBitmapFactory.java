package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

public class ConciseBitmapFactory extends BitmapFactory {

    @Override
    public GenericBitmap getEmptyBitmap() {
        return new WrappedConciseBitmap();
    }

    @Override
    public ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b) {
        return new WrappedImmutableConciseBitmap(b);
    }

}
