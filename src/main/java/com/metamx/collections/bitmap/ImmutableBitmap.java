package com.metamx.collections.bitmap;

import org.roaringbitmap.IntIterator;

/**
 * This class is meant to represent a simple wrapper around an immutable bitmap
 * class.
 */
public interface ImmutableBitmap
{
  /**
   * @return an iterator over the set bits of this bitmap
   */
  public IntIterator iterator();

  /**
   * @return The number of bits set to true in this bitmap
   */
  public int size();

  public byte[] toBytes();

  public int compareTo(ImmutableBitmap other);

  /**
   * @return True if this bitmap is empty (contains no set bit)
   */
  public boolean isEmpty();

  /**
   * Returns true if the bit at position value is set
   * @param value the position to check
   * @return true if bit is set
   */
  public boolean get(int value);
  /**
   * Compute the bitwise-or of this bitmap with another bitmap. A new bitmap is generated.
   * <p/>
   * Note that the other bitmap should be of the same class instance.
   *
   * @param otherBitmap other bitmap
   */
  public ImmutableBitmap union(ImmutableBitmap otherBitmap);

  /**
   * Compute the bitwise-and of this bitmap with another bitmap. A new bitmap is generated.
   * <p/>
   * Note that the other bitmap should be of the same class instance.
   *
   * @param otherBitmap other bitmap
   */
  public ImmutableBitmap intersection(ImmutableBitmap otherBitmap);

  /**
   * Compute the bitwise-andNot of this bitmap with another bitmap. A new bitmap is generated.
   * <p/>
   * Note that the other bitmap should be of the same class instance.
   *
   * @param otherBitmap other bitmap
   */
  public ImmutableBitmap difference(ImmutableBitmap otherBitmap);
}
