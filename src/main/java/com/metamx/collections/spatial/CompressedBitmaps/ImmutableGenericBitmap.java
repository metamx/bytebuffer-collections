package com.metamx.collections.spatial.CompressedBitmaps;

import java.nio.ByteBuffer;

import org.roaringbitmap.IntIterator;

/**
 * This class is meant to represent a simple wrapper around an immutable bitmap
 * class.
 * 
 */
public interface ImmutableGenericBitmap
{
	/**
	 * 
	 * @return an iterator over the set bits of this bitmap
	 */
   public  IntIterator iterator();
   
   /**
	 * 
	 * @return an immutableRoaringBitmap created from a byteBuffer
	 */
   public ImmutableGenericBitmap getImmutableBitmap(ByteBuffer buffer);
   
 	/**
 	 * 
 	 * @return The number of bits set to true in this bitmap
 	 */
 	public int size();
 	
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
	public ImmutableGenericBitmap union(Iterable<ImmutableGenericBitmap> b);
	
	
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

	/**
 	 * 
 	 * @return True if this bitmap is empty (contains no set bit)
 	 */
	public boolean isEmpty();
	
	
	/**
	 * Compute the bitwise-or of this bitmap with another bitmap. A new bitmap is generated.
	 * 
	 * Note that the other bitmap should be of the same class instance.
	 * 
	 * @param bitmap
	 *          other bitmap
	 */
	public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap);

	/**
	 * Compute the bitwise-and of this bitmap with another bitmap. A new bitmap is generated.
	 * 
	 * Note that the other bitmap should be of the same class instance.
	 * 
	 * @param bitmap
	 *          other bitmap
	 */
	public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap);
	


	/**
	 * Compute the bitwise-andNot of this bitmap with another bitmap. A new bitmap is generated.
	 * 
	 * Note that the other bitmap should be of the same class instance.
	 * 
	 * @param bitmap
	 *          other bitmap
	 */
	public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap);

}
