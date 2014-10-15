package com.metamx.collections.spatial.bitmap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.roaringbitmap.buffer.MutableRoaringBitmap;

public class WrappedRoaringBitmap extends GenericBitmap {
    
    MutableRoaringBitmap core;

    @Override
    public void clear() {
        core.clear();
    }

    @Override
    public void or(GenericBitmap bitmap) {
        WrappedRoaringBitmap other = (WrappedRoaringBitmap) bitmap;
        MutableRoaringBitmap othercore = other.core;
        core.or(othercore);    
    }

    @Override
    public int getSizeInBytes() {
        return core.serializedSizeInBytes();
    }


    @Override
    public void add(int entry) {
        core.add(entry);        
    }

    @Override
    public int size() {
        return core.getCardinality();
    }

    @Override
    public void serialize(ByteBuffer buffer) {
        try {
            core.serialize(new DataOutputStream(new OutputStream(){
                ByteBuffer mBB;
                OutputStream init(ByteBuffer mbb) {mBB=mbb; return this;}
                public void close() {}
                public void flush() {}
                public void write(int b) {
                    mBB.put((byte) b);}
                public void write(byte[] b) {}            
                public void write(byte[] b, int off, int l) {}
            }.init(buffer)));
        } catch (IOException e) {
            e.printStackTrace();// impossible in theory
        }        
    }

}
