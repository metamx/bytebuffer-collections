package com.metamx.collections.spatial.bitmap;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedConciseBitmap and WrappedImmutableConciseBitmap.
 */
public class ConciseBitmapFactory extends BitmapFactory {

    @Override
    public GenericBitmap getEmptyBitmap() {
        return new WrappedConciseBitmap();
    }

    @Override
    public ImmutableGenericBitmap mapImmutableBitmap(ByteBuffer b) {
        return new WrappedImmutableConciseBitmap(b);
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
}
