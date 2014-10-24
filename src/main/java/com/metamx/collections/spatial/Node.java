package com.metamx.collections.spatial;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.metamx.collections.spatial.CompressedBitmaps.GenericBitmap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 */
public class Node
{
    private final float[] minCoordinates;
    private final float[] maxCoordinates;

    private final List<Node> children;
    private final boolean isLeaf;
    private final GenericBitmap bitmap;
    private Node parent;

    public Node(float[] minCoordinates, float[] maxCoordinates, boolean isLeaf, GenericBitmap bitmap)
    {
        this(
                minCoordinates,
                maxCoordinates,
                Lists.<Node>newArrayList(),
                isLeaf,
                null,
                bitmap
        );
    }

    public Node(
            float[] minCoordinates,
            float[] maxCoordinates,
            List<Node> children,
            boolean isLeaf,
            Node parent,
            GenericBitmap bitmap
    )
    {
        Preconditions.checkArgument(minCoordinates.length == maxCoordinates.length);

        this.minCoordinates = minCoordinates;
        this.maxCoordinates = maxCoordinates;
        this.children = children;
        for (Node child : children) {
            child.setParent(this);
        }
        this.isLeaf = isLeaf;
        this.bitmap = bitmap;
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

    public Node getParent()
    {
        return parent;
    }

    public void addChild(Node node)
    {
        if (node == this) {
            System.out.println("WTF");
        }
        node.setParent(this);
        children.add(node);
    }

    public List<Node> getChildren()
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

    public boolean contains(Node other)
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

        for (Node child : getChildren()) {
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
    public GenericBitmap getBitmap()
    {
        return this.bitmap;
    }

    /*public void addToConciseSet(Node node)
    {
      conciseSet.addAll(node.getConciseSet());
    }*/
    public void addToBitmap(Node node)
    {
        this.bitmap.or(node.getBitmap());
    }

    public void clear()
    {
        children.clear();
        this.bitmap.clear();
    }

    public int getSizeInBytes()
    {
        return ImmutableNode.HEADER_NUM_BYTES
                + 2 * getNumDims() * Floats.BYTES
                + Ints.BYTES // size of set
                + this.bitmap.getSizeInBytes()
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
        
        this.bitmap.serialize(buffer);

        position = buffer.position();
        int childStartOffset = position + getChildren().size() * Ints.BYTES;
        for (Node child : getChildren()) {
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

    private void setParent(Node p)
    {
        parent = p;
    }
}
