/*
 * Copyright 2011 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metamx.collections.bitmap;

import com.google.common.base.Throwables;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    try {
      final RoaringBitmap roaringBitmap = new RoaringBitmap();
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      roaringBitmap.serialize(new DataOutputStream(out));
      final byte[] bytes = out.toByteArray();

      ByteBuffer buf = ByteBuffer.wrap(bytes);
      return new WrappedImmutableRoaringBitmap(
          new ImmutableRoaringBitmap(buf)
      );
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public ImmutableBitmap makeImmutableBitmap(MutableBitmap mutableBitmap)
  {
    if (!(mutableBitmap instanceof WrappedRoaringBitmap)) {
      throw new IllegalStateException(String.format("Cannot convert [%s]", mutableBitmap.getClass()));
    }
    try {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      ((WrappedRoaringBitmap) mutableBitmap).getBitmap().serialize(new DataOutputStream(out));
      final byte[] bytes = out.toByteArray();


      ByteBuffer buf = ByteBuffer.wrap(bytes);
      return new WrappedImmutableRoaringBitmap(
          new ImmutableRoaringBitmap(buf)
      );
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
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
            b.size()
        )
    );
  }

  @Override
  public ImmutableBitmap complement(
      ImmutableBitmap b, int length
  )
  {
    return new WrappedImmutableRoaringBitmap(
        ImmutableRoaringBitmap.flip(
            ((WrappedImmutableRoaringBitmap) b).getBitmap(),
            0,
            length
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
