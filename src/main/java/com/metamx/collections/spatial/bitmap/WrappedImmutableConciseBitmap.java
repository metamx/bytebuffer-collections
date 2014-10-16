package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

public class WrappedImmutableConciseBitmap extends ImmutableGenericBitmap
{

	/**
	 * Underlying bitmap.
	 */
	public ImmutableConciseSet core;

	@SuppressWarnings("unused")
	private WrappedImmutableConciseBitmap() {

	}

	protected WrappedImmutableConciseBitmap(ByteBuffer b) {
		core = new ImmutableConciseSet(b.asReadOnlyBuffer());
	}

	/**
	 * Wrap an ImmutableConciseSet
	 * 
	 * @param c
	 *          bitmap to be wrapped
	 */
	public WrappedImmutableConciseBitmap(ImmutableConciseSet c) {
		core = c;
	}

	/**
	 * Compute the union (bitwise-OR) of a set of bitmaps. They are assumed to be
	 * instances of WrappedImmutableConciseBitmap otherwise a ClassCastException
	 * is thrown.
	 * 
	 * This is a convenience method.
	 * 
	 * @param b
	 *          input ImmutableGenericBitmap objects
	 * @throws ClassCastException
	 *           if one of the ImmutableGenericBitmap objects if not an instance
	 *           of WrappedImmutableConciseBitmap
	 * @return the union.
	 */
	public static ImmutableConciseSet union(Iterable<ImmutableGenericBitmap> b)
			throws ClassCastException {
		return ImmutableConciseSet.union(WrappedImmutableConciseBitmap.unwrap(b));
	}

	protected static Iterable<ImmutableConciseSet> unwrap(
			final Iterable<ImmutableGenericBitmap> b) {
		return new Iterable<ImmutableConciseSet>() {

			@Override
			public Iterator<ImmutableConciseSet> iterator() {
				final Iterator<ImmutableGenericBitmap> i = b.iterator();
				return new Iterator<ImmutableConciseSet>() {

					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public ImmutableConciseSet next() {
						return ((WrappedImmutableConciseBitmap) i.next()).core;
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
