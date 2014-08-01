package com.metamx.collections.spatial;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 */
public class RoaringNode
{
  private final float[] minCoordinates;
  private final float[] maxCoordinates;

  private final List<RoaringNode> children;
  private final boolean isLeaf;
   private final MutableRoaringBitmap roaring;
  private RoaringNode parent;

  public RoaringNode(float[] minCoordinates, float[] maxCoordinates, boolean isLeaf)
  {
    this(
        minCoordinates,
        maxCoordinates,
        Lists.<RoaringNode>newArrayList(),
        isLeaf,
        null,
        //new ConciseSet()
        new MutableRoaringBitmap()
    );
  }

  public RoaringNode(
      float[] minCoordinates,
      float[] maxCoordinates,
      List<RoaringNode> children,
      boolean isLeaf,
      RoaringNode parent,
      MutableRoaringBitmap roaring
  )
  {
    Preconditions.checkArgument(minCoordinates.length == maxCoordinates.length);

    this.minCoordinates = minCoordinates;
    this.maxCoordinates = maxCoordinates;
    this.children = children;
    for (RoaringNode child : children) {
      child.setParent(this);
    }
    this.isLeaf = isLeaf;
    this.roaring = roaring;
    this.parent = parent;
  }

  public int getNumDims()
  {
    return minCoordinates.length;
  }

  public float[] getMinCoordinates()
  {
    return minCoordinates;
  }

  public float[] getMaxCoordinates()
  {
    return maxCoordinates;
  }

  public RoaringNode getParent()
  {
    return parent;
  }

  public void addChild(RoaringNode node)
  {
    if (node == this) {
      System.out.println("WTF");
    }
    node.setParent(this);
    children.add(node);
  }

  public List<RoaringNode> getChildren()
  {
    return children;
  }

  public boolean isLeaf()
  {
    return isLeaf;
  }

  public double getArea()
  {
    return calculateArea();
  }

  public boolean contains(RoaringNode other)
  {
    Preconditions.checkArgument(getNumDims() == other.getNumDims());

    for (int i = 0; i < getNumDims(); i++) {
      if (other.getMinCoordinates()[i] < minCoordinates[i] || other.getMaxCoordinates()[i] > maxCoordinates[i]) {
        return false;
      }
    }
    return true;
  }

  public boolean contains(float[] coords)
  {
    Preconditions.checkArgument(getNumDims() == coords.length);

    for (int i = 0; i < getNumDims(); i++) {
      if (coords[i] < minCoordinates[i] || coords[i] > maxCoordinates[i]) {
        return false;
      }
    }
    return true;
  }

  public boolean enclose()
  {
    boolean retVal = false;
    float[] minCoords = new float[getNumDims()];
    Arrays.fill(minCoords, Float.MAX_VALUE);
    float[] maxCoords = new float[getNumDims()];
    Arrays.fill(maxCoords, -Float.MAX_VALUE);

    for (RoaringNode child : getChildren()) {
      for (int i = 0; i < getNumDims(); i++) {
        minCoords[i] = Math.min(child.getMinCoordinates()[i], minCoords[i]);
        maxCoords[i] = Math.max(child.getMaxCoordinates()[i], maxCoords[i]);
      }
    }

    if (!Arrays.equals(minCoords, minCoordinates)) {
      System.arraycopy(minCoords, 0, minCoordinates, 0, minCoordinates.length);
      retVal = true;
    }
    if (!Arrays.equals(maxCoords, maxCoordinates)) {
      System.arraycopy(maxCoords, 0, maxCoordinates, 0, maxCoordinates.length);
      retVal = true;
    }

    return retVal;
  }

  /*public ConciseSet getConciseSet()
  {
    return conciseSet;
  }*/
  public MutableRoaringBitmap getRoaringBitmap()
  {
    return this.roaring;
  }

  /*public void addToConciseSet(Node node)
  {
    conciseSet.addAll(node.getConciseSet());
  }*/
  public void addToRoaringBitmap(RoaringNode node)
  {
    this.roaring.or(node.getRoaringBitmap());
  }

  public void clear()
  {
    children.clear();
    roaring.clear();
  }

  public int getSizeInBytes()
  {
    return ImmutableNode.HEADER_NUM_BYTES
           + 2 * getNumDims() * Floats.BYTES
           + Ints.BYTES // size of set
           + roaring.serializedSizeInBytes()//conciseSet.getWords().length * Ints.BYTES
           + getChildren().size() * Ints.BYTES;
  }

  public int storeInByteBuffer(ByteBuffer buffer, int position)
  {
      buffer.position(position);
      buffer.putShort((short) (((isLeaf ? 0x1 : 0x0) << 15) | getChildren().size()));
      for (float v : getMinCoordinates()) {
        buffer.putFloat(v);
      }
      for (float v : getMaxCoordinates()) {
        buffer.putFloat(v);
      }
      buffer.putInt(roaring.serializedSizeInBytes());
      try {
          this.roaring.serialize(new DataOutputStream(new OutputStream(){
              ByteBuffer mBB;
              OutputStream init(ByteBuffer mbb) {mBB=mbb; return this;}
              public void close() {}
              public void flush() {}
              public void write(int b) {mBB.put((byte) b);}
              public void write(byte[] b) {}            
              public void write(byte[] b, int off, int l) {}
          }.init(buffer)));
      } catch (IOException e) {e.printStackTrace();}

      position = buffer.position();
      int childStartOffset = position + getChildren().size() * Ints.BYTES;
      for (RoaringNode child : getChildren()) {
        buffer.putInt(position, childStartOffset);
        childStartOffset = child.storeInByteBuffer(buffer, childStartOffset);
        position += Ints.BYTES;
      }

      return childStartOffset;
  }

  private double calculateArea()
  {
    double area = 1.0;
    for (int i = 0; i < minCoordinates.length; i++) {
      area *= (maxCoordinates[i] - minCoordinates[i]);
    }
    return area;
  }

  private void setParent(RoaringNode p)
  {
    parent = p;
  }
}
