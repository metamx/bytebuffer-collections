package com.metamx.collections.bitmap;

import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedRoaringBitmap and WrappedImmutableRoaringBitmap.
 */
public class RoaringBitmapFactory implements BitmapFactory
{
  @Override
  public MutableBitmap makeEmptyMutableBitmap()
  {
    return new WrappedRoaringBitmap();
  }

  @Override
  public ImmutableBitmap makeEmptyImmutableBitmap()
  {
    return new WrappedImmutableRoaringBitmap(new ImmutableRoaringBitmap(ByteBuffer.wrap(new byte[]{})));
  }

  @Override
  public ImmutableBitmap makeImmutableBitmap(MutableBitmap mutableBitmap)
  {
    if (mutableBitmap instanceof MutableRoaringBitmap) {
      throw new IllegalStateException(String.format("Cannot convert [%s]", mutableBitmap.getClass()));
    }
    ByteBuffer buf = ByteBuffer.allocate(mutableBitmap.getSizeInBytes());
    return new WrappedImmutableRoaringBitmap(
        new ImmutableRoaringBitmap(buf.asReadOnlyBuffer())
    );
  }

  @Override
  public ImmutableBitmap mapImmutableBitmap(ByteBuffer b)
  {
    return new WrappedImmutableRoaringBitmap(b);
  }

  @Override
  public ImmutableBitmap union(Iterable<ImmutableBitmap> b)
  {
    return new WrappedImmutableRoaringBitmap(BufferFastAggregation.horizontal_or(unwrap(b).iterator()));
  }

  @Override
  public ImmutableBitmap intersection(Iterable<ImmutableBitmap> b)
  {
    return new WrappedImmutableRoaringBitmap(BufferFastAggregation.and(unwrap(b).iterator()));
  }

  @Override
  public ImmutableBitmap complement(ImmutableBitmap b)
  {
    return new WrappedImmutableRoaringBitmap(
        ImmutableRoaringBitmap.flip(
            ((WrappedImmutableRoaringBitmap) b).getBitmap(),
            0,
            0xFFFF
        )
    );
  }

  private static Iterable<ImmutableRoaringBitmap> unwrap(
      final Iterable<ImmutableBitmap> b
  )
  {
    return new Iterable<ImmutableRoaringBitmap>()
    {
      @Override
      public Iterator<ImmutableRoaringBitmap> iterator()
      {
        final Iterator<ImmutableBitmap> i = b.iterator();
        return new Iterator<ImmutableRoaringBitmap>()
        {
          @Override
          public void remove()
          {
            i.remove();
          }

          @Override
          public boolean hasNext()
          {
            return i.hasNext();
          }

          @Override
          public ImmutableRoaringBitmap next()
          {
            return ((WrappedImmutableRoaringBitmap) i.next()).getBitmap();
          }
        };
      }
    };
  }
}
