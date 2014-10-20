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

	/**
	 * Compute the union (bitwise-OR) of a set of bitmaps. They are assumed to be
	 * instances of of the proper WrappedConciseBitmap otherwise a ClassCastException
	 * is thrown.
	 * 
	 * 
	 * @param b
	 *          input ImmutableGenericBitmap objects
	 * @throws ClassCastException
	 *           if one of the ImmutableGenericBitmap objects if not an instance
	 *           of WrappedImmutableConciseBitmap
	 * @return the union.
	 */
	public abstract ImmutableGenericBitmap union(Iterable<ImmutableGenericBitmap> b);

	/**
	 * Compute the intersection (bitwise-AND) of a set of bitmaps. They are assumed to be
	 * instances of of the proper WrappedConciseBitmap otherwise a ClassCastException
	 * is thrown.
	 * 
	 * 
	 * @param b
	 *          input ImmutableGenericBitmap objects
	 * @throws ClassCastException
	 *           if one of the ImmutableGenericBitmap objects if not an instance
	 *           of WrappedImmutableConciseBitmap
	 * @return the union.
	 */
	public abstract ImmutableGenericBitmap intersection(Iterable<ImmutableGenericBitmap> b) ;

}
