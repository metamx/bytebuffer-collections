package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

public class WrappedImmutableRoaringBitmap implements ImmutableGenericBitmap
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


	@Override
	public String toString() {
		return getClass().getSimpleName() + core.toString();
	}

	@Override
	public IntIterator iterator() {
		return core.getIntIterator();
	}

	@Override
	public int size() {
		return core.getCardinality();
	}

	@Override
	public boolean isEmpty() {
		return core.isEmpty();
	}
	
	@Override
	public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap) {
		WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
		ImmutableRoaringBitmap othercore = other.core;
		return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.or(core, othercore));
	}

	@Override
	public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap) {
		WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
		ImmutableRoaringBitmap othercore = other.core;
		return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.and(core, othercore));
	}

	@Override
	public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap) {
		WrappedImmutableRoaringBitmap other = (WrappedImmutableRoaringBitmap) bitmap;
		ImmutableRoaringBitmap othercore = other.core;
		return new WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap.andNot(core, othercore));
	}
}
