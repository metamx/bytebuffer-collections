package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

public class WrappedImmutableRoaringBitmap extends ImmutableGenericBitmap
{

	/**
	 * Underlying bitmap.
	 */
	public ImmutableRoaringBitmap core;

	@SuppressWarnings("unused")
	private WrappedImmutableRoaringBitmap() {
	}

	protected WrappedImmutableRoaringBitmap(ByteBuffer b) {
		core = new ImmutableRoaringBitmap(b.asReadOnlyBuffer());
	}

	/**
	 * Wrap an ImmutableRoaringBitmap
	 * 
	 * @param c
	 *          bitmap to be wrapped
	 */
	public WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap c) {
		core = c;
	}

	/**
	 * Compute the union (bitwise-OR) of a set of bitmaps. They are assumed to be
	 * instances of WrappedImmutableRoaringBitmap otherwise a ClassCastException
	 * is thrown.
	 * 
	 * This is a convenience method.
	 * 
	 * @param b
	 *          input ImmutableGenericBitmap objects
	 * @throws ClassCastException
	 *           if one of the ImmutableGenericBitmap objects if not an instance
	 *           of WrappedImmutableRoaringBitmap
	 * @return the union.
	 */
	public static MutableRoaringBitmap union(Iterable<ImmutableGenericBitmap> b) {
		return BufferFastAggregation.horizontal_or(WrappedImmutableRoaringBitmap
				.unwrap(b).iterator());
	}

	protected static Iterable<ImmutableRoaringBitmap> unwrap(
			final Iterable<ImmutableGenericBitmap> b) {
		return new Iterable<ImmutableRoaringBitmap>() {

			@Override
			public Iterator<ImmutableRoaringBitmap> iterator() {
				final Iterator<ImmutableGenericBitmap> i = b.iterator();
				return new Iterator<ImmutableRoaringBitmap>() {

                    @Override
                    public void remove() {
                       i.remove();
                    }

					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public ImmutableRoaringBitmap next() {
						return ((WrappedImmutableRoaringBitmap) i.next()).core;
					}

				};
			}

		};

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + core.toString();
	}
}
