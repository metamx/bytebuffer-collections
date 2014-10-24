package CompressedBitmaps;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.BufferFastAggregation;
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

	@Override
	public ImmutableGenericBitmap getImmutableBitmap(ByteBuffer buffer) {
		return new WrappedImmutableRoaringBitmap(buffer);
	}

	@Override
	public ImmutableGenericBitmap union(Iterable<ImmutableGenericBitmap> b) {
		 return	new WrappedImmutableRoaringBitmap(BufferFastAggregation.horizontal_or(unwrap(b).iterator()));
	}

	@Override
	public  ImmutableGenericBitmap intersection(Iterable<ImmutableGenericBitmap> b) {
		return	new WrappedImmutableRoaringBitmap(BufferFastAggregation.and(unwrap(b).iterator()));
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

	public ImmutableRoaringBitmap getCore() {
		return this.core;
	}
}
