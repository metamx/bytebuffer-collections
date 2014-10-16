package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedConciseBitmap and WrappedImmutableConciseBitmap.
 */
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
