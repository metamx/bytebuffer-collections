package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

public abstract class BitmapFactory
{
	/**
	 * Create a new empty bitmap
	 * 
	 * @return the new bitmap
	 */
	public abstract GenericBitmap getEmptyBitmap();

	/**
	 * Given a ByteBuffer pointing at a serialized version of a bitmap,
	 * instantiate an immutable mapped bitmap.
	 * 
	 * When using RoaringBitmap (with the RoaringBitmapFactory class), it is not
	 * necessary for b.limit() to indicate the end of the serialized content
	 * whereas it is critical to set b.limit() appropriately with ConciseSet (with
	 * the ConciseBitmapFactory).
	 * 
	 * @param b
	 *          the input byte buffer
	 * @return the new bitmap
	 */
	public abstract ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b);
}
