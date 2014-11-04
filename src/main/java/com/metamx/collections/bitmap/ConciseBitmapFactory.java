package com.metamx.collections.bitmap;

import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * As the name suggests, this class instantiates bitmaps of the types
 * WrappedConciseBitmap and WrappedImmutableConciseBitmap.
 */
public class ConciseBitmapFactory implements BitmapFactory
{
  @Override
  public MutableBitmap makeEmptyMutableBitmap()
  {
    return new WrappedConciseBitmap();
  }

  @Override
  public ImmutableBitmap makeEmptyImmutableBitmap()
  {
    return new WrappedImmutableConciseBitmap(new ImmutableConciseSet());
  }

  @Override
  public ImmutableBitmap makeImmutableBitmap(MutableBitmap mutableBitmap)
  {
    return new WrappedImmutableConciseBitmap(
        ImmutableConciseSet.newImmutableFromMutable(
            ((WrappedConciseBitmap) mutableBitmap).getBitmap()
        )
    );
  }

  @Override
  public ImmutableBitmap mapImmutableBitmap(ByteBuffer b)
  {
    return new WrappedImmutableConciseBitmap(b);
  }

  @Override
  public ImmutableBitmap union(Iterable<ImmutableBitmap> b)
      throws ClassCastException
  {
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.union(unwrap(b)));
  }

  @Override
  public ImmutableBitmap intersection(Iterable<ImmutableBitmap> b)
      throws ClassCastException
  {
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.intersection(unwrap(b)));
  }

  @Override
  public ImmutableBitmap complement(ImmutableBitmap b)
  {
    return new WrappedImmutableConciseBitmap(ImmutableConciseSet.complement(((WrappedImmutableConciseBitmap) b).getBitmap()));
  }

  private static Iterable<ImmutableConciseSet> unwrap(
      final Iterable<ImmutableBitmap> b
  )
  {
    return new Iterable<ImmutableConciseSet>()
    {
      @Override
      public Iterator<ImmutableConciseSet> iterator()
      {
        final Iterator<ImmutableBitmap> i = b.iterator();
        return new Iterator<ImmutableConciseSet>()
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
          public ImmutableConciseSet next()
          {
            return ((WrappedImmutableConciseBitmap) i.next()).getBitmap();
          }
        };
      }
    };
  }
}
