package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedRoaringBitmap and WrappedImmutableRoaringBitmap.
 */
public class RoaringBitmapFactory implements BitmapFactory
{
	@Override
	public GenericBitmap getEmptyBitmap() {
		return new WrappedRoaringBitmap();
	}

	@Override
	public ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b) {
		return new WrappedImmutableRoaringBitmap(b);
	}
	
	@Override
	public  ImmutableGenericBitmap union(Iterable<ImmutableGenericBitmap> b) {
		return new WrappedImmutableRoaringBitmap(BufferFastAggregation.horizontal_or(unwrap(b).iterator()));
	}

	@Override
	public  ImmutableGenericBitmap intersection(Iterable<ImmutableGenericBitmap> b) {
		return new WrappedImmutableRoaringBitmap(BufferFastAggregation.and(unwrap(b).iterator()));
	}

	
	private static Iterable<ImmutableRoaringBitmap> unwrap(
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

}
