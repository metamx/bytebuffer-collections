package com.metamx.collections.bitmap;

import com.google.common.base.Throwables;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;


/**
 *
 */
public class MutableBitmapFactory
{
  public static MutableBitmap newEmpty(Class<? extends MutableBitmap> otherClass, ByteBuffer buffer){
    try {
      return otherClass.getConstructor(ByteBuffer.class).newInstance(buffer);
    }
    catch (InstantiationException e) {
      throw Throwables.propagate(e);
    }
    catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
    catch (NoSuchMethodException e) {
      throw Throwables.propagate(e);
    }
    catch (InvocationTargetException e) {
      throw Throwables.propagate(e);
    }
  }
  public static MutableBitmap newEmpty(Class<? extends MutableBitmap> otherClass){
    try {
      return otherClass.newInstance();
    }
    catch (InstantiationException e) {
      throw Throwables.propagate(e);
    }
    catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }
}
