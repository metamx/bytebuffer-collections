package CompressedBitmaps;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.roaringbitmap.IntIterator;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;

public class WrappedImmutableConciseBitmap implements ImmutableGenericBitmap
{

	/**
	 * Underlying bitmap.
	 */
	public ImmutableConciseSet core=null;

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



	@Override
	public String toString() {
		return getClass().getSimpleName() + core.toString();
	}

	@Override
	public IntIterator iterator() {
		final IntSet.IntIterator i = core.iterator();
		return new IntIterator() {
			
			@Override
			public IntIterator clone() {
				throw new RuntimeException("clone is not supported on ConciseSet iterator");
			}

			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public int next() {
				return i.next();
			}
			
		};
	}

	@Override
	public int size() {
		return core.size();
	}

	@Override
	public boolean isEmpty() {
		return core.size() == 0;
	}

	@Override
	public ImmutableGenericBitmap union(ImmutableGenericBitmap bitmap) {
		WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
		ImmutableConciseSet othercore = other.core;
		return new WrappedImmutableConciseBitmap( ImmutableConciseSet.union(core,othercore));
	}

	@Override
	public ImmutableGenericBitmap intersection(ImmutableGenericBitmap bitmap) {
		WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
		ImmutableConciseSet othercore = other.core;
		return new WrappedImmutableConciseBitmap( ImmutableConciseSet.intersection(core,othercore));
	}

	@Override
	public ImmutableGenericBitmap difference(ImmutableGenericBitmap bitmap) {
		WrappedImmutableConciseBitmap other = (WrappedImmutableConciseBitmap) bitmap;
		ImmutableConciseSet othercore = other.core;
		return new WrappedImmutableConciseBitmap( ImmutableConciseSet.intersection(core,ImmutableConciseSet.complement(othercore)));
	}

	@Override
	public ImmutableGenericBitmap getImmutableBitmap(ByteBuffer buffer) {
		return new WrappedImmutableConciseBitmap(buffer);
	}
	
	@Override
  	public ImmutableGenericBitmap union(Iterable<ImmutableGenericBitmap> b)
  			throws ClassCastException {
  		return new WrappedImmutableConciseBitmap(ImmutableConciseSet.union(unwrap(b)));
  	}
    

    @Override
  	public ImmutableGenericBitmap intersection(Iterable<ImmutableGenericBitmap> b)
  			throws ClassCastException {
    	return new WrappedImmutableConciseBitmap(ImmutableConciseSet.intersection(unwrap(b)));
  	}

  	private static Iterable<ImmutableConciseSet> unwrap(
  			final Iterable<ImmutableGenericBitmap> b) {
  		return new Iterable<ImmutableConciseSet>() {

  			@Override
  			public Iterator<ImmutableConciseSet> iterator() {
  				final Iterator<ImmutableGenericBitmap> i = b.iterator();
  				return new Iterator<ImmutableConciseSet>() {
                    @Override
                    public void remove() { 
                        i.remove();
                    }

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
  	
  	public ImmutableConciseSet getCore(){
  		return this.core;
  	}

}
