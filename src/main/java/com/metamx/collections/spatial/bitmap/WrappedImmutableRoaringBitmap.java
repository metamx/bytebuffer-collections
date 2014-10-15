package com.metamx.collections.spatial.bitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

public class WrappedImmutableRoaringBitmap extends ImmutableGenericBitmap {
 public ImmutableRoaringBitmap core;
    
    private WrappedImmutableRoaringBitmap() {
        
    }
    
    protected WrappedImmutableRoaringBitmap(ByteBuffer b) {
        core = new ImmutableRoaringBitmap(b.asReadOnlyBuffer());
    }
    
    public WrappedImmutableRoaringBitmap(ImmutableRoaringBitmap c) {
        core = c;
    }
    
    public static Iterable<ImmutableRoaringBitmap> unwrap(final Iterable<ImmutableGenericBitmap> b){
        return new Iterable<ImmutableRoaringBitmap>() {

            @Override
            public Iterator<ImmutableRoaringBitmap> iterator() {
                final Iterator<ImmutableGenericBitmap> i = b.iterator();
                return new Iterator<ImmutableRoaringBitmap>() {

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
