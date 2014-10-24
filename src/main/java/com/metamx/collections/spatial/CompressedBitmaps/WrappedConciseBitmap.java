package com.metamx.collections.spatial.CompressedBitmaps;

import java.nio.ByteBuffer;

import org.roaringbitmap.IntIterator;

import com.google.common.primitives.Ints;

import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;

public class WrappedConciseBitmap implements GenericBitmap {

	/**
	 * Underlying bitmap.
	 */
    public ConciseSet core;
    

    /**
     * Create a new WrappedConciseBitmap wrapping an empty  ConciseSet
     */
    public WrappedConciseBitmap() {
        core = new ConciseSet();
    }
    
    /**
     * Create a bitmap wrappign the given bitmap
     * @param o bitmap to be wrapped
     */
    public WrappedConciseBitmap(ConciseSet o ) {
    	core = o;
    }

    @Override
    public void clear() {
        core.clear();
    }

    @Override
    public void or(GenericBitmap bitmap) {
        WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
        ConciseSet othercore = other.core;
        core.addAll(othercore);
    }
    

    @Override
    public void and(GenericBitmap bitmap) {
        WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
        ConciseSet othercore = other.core;
        core.intersection(othercore);
    }
    

    @Override
    public void xor(GenericBitmap bitmap) {
        WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
        ConciseSet othercore = other.core;
        core.symmetricDifference(othercore);
    }

    @Override
    public void andNot(GenericBitmap bitmap) {
        WrappedConciseBitmap other = (WrappedConciseBitmap) bitmap;
        ConciseSet othercore = other.core;
        core.difference(othercore);
    }

    
    @Override
    public int getSizeInBytes() {
        return core.getWords().length * Ints.BYTES + Ints.BYTES;
    }


    @Override
    public void add(int entry) {
        core.add(entry);
    }

    @Override
    public int size() {
        return core.size();
    }

    @Override
    public void serialize(ByteBuffer buffer) {
        byte[] bytes = ImmutableConciseSet.newImmutableFromMutable(core).toBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+core.toString();
    }

		@Override
		public void remove(int entry) {
			  core.remove(entry);
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
		public boolean isEmpty() {
			return core.size() == 0;
		}

		@Override
		public ImmutableGenericBitmap toImmutableGenericBitmap() {
			return new WrappedImmutableConciseBitmap(ImmutableConciseSet.newImmutableFromMutable(core));
		}

		@Override
		public GenericBitmap getEmptyWrappedBitmap() {
			return new WrappedConciseBitmap();
		}

		@Override
		public ImmutableGenericBitmap getEmptyImmutableGenericBitmap() {
			// TODO Auto-generated method stub
			return new WrappedImmutableConciseBitmap(new ImmutableConciseSet());
		}

}
