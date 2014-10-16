package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedRoaringBitmap and WrappedImmutableRoaringBitmap.
 */
public class RoaringBitmapFactory extends BitmapFactory
{
	@Override
	public GenericBitmap getEmptyBitmap() {
		return new WrappedRoaringBitmap();
	}

	@Override
	public ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b) {
		return new WrappedImmutableRoaringBitmap(b);
	}

}
