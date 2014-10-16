package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;

import com.google.common.primitives.Ints;

import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

public class WrappedConciseBitmap extends GenericBitmap {

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
}
