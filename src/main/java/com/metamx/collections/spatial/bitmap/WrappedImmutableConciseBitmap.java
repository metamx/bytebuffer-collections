package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet.IntIterator;

public class WrappedImmutableConciseBitmap extends ImmutableGenericBitmap {
    public ImmutableConciseSet core;
    
    public WrappedImmutableConciseBitmap(ByteBuffer b) {
        core = new ImmutableConciseSet(b.asReadOnlyBuffer());
    }
    
    public WrappedImmutableConciseBitmap(ImmutableConciseSet c) {
        core = c;
    }

    @Override
    public byte[] toBytes() {
        return core.toBytes();
    }


    @Override
    public int size() {
        return core.size();
    }
    
    public static Iterable<ImmutableConciseSet> unwrap(final Iterable<ImmutableGenericBitmap> b){
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

}
