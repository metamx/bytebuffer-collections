package com.metamx.collections.spatial;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.metamx.collections.spatial.search.*;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.nio.ByteBuffer;

/**
 * An immutable representation of an {@link com.metamx.collections.spatial.RTree} for spatial indexing.
 */
public class RoaringImmutableRTree
{
  private static byte VERSION = 0x0;

  public static RoaringImmutableRTree newImmutableFromMutable(RoaringRTree rTree)
  {
    if (rTree.getSize() == 0) {
      return new RoaringImmutableRTree();
    }
    int space = calcNumBytes(rTree);
    ByteBuffer buffer = ByteBuffer.wrap(new byte[space]);

    buffer.put(VERSION);
    buffer.putInt(rTree.getNumDims());
    int spaceUsed = rTree.getRoot().storeInByteBuffer(buffer, buffer.position());
    buffer.position(0);
    return new RoaringImmutableRTree(buffer.asReadOnlyBuffer());
  }

  private static int calcNumBytes(RoaringRTree tree)
  {
    int total = 1 + Ints.BYTES; // VERSION and numDims

    total += calcNodeBytes(tree.getRoot());

    return total;
  }

  private static int calcNodeBytes(RoaringNode node)
  {
    int total = 0;

    // find size of this node
    total += node.getSizeInBytes();

    // recursively find sizes of child nodes
    for (RoaringNode child : node.getChildren()) {
      if (node.isLeaf()) {
        total += child.getSizeInBytes();
      } else {
        total += calcNodeBytes(child);
      }
    }

    return total;
  }

  private final int numDims;
  private final RoaringImmutableNode root;
  private final ByteBuffer data;

  private final RoaringSearchStrategy defaultSearchStrategy = new RoaringGutmanSearchStrategy();

  public RoaringImmutableRTree()
  {
    this.numDims = 0;
    this.data = ByteBuffer.wrap(new byte[]{});
    this.root = null;
  }

  public RoaringImmutableRTree(ByteBuffer data)
  {
    final int initPosition = data.position();

    Preconditions.checkArgument(data.get(0) == VERSION, "Mismatching versions");

    this.numDims = data.getInt(1 + initPosition) & 0x7FFF;
    this.data = data;
    this.root = new RoaringImmutableNode(numDims, initPosition, 1 + Ints.BYTES, data);
  }

  public int size()
  {
    return data.capacity();
  }

  public RoaringImmutableNode getRoot()
  {
    return root;
  }

  public int getNumDims()
  {
    return numDims;
  }

  public Iterable<ImmutableRoaringBitmap> search(RoaringBound bound)
  {
    Preconditions.checkArgument(bound.getNumDims() == numDims);
    Iterable<ImmutableRoaringBitmap> res = defaultSearchStrategy.search(root, bound);
      return res;
   }

  public Iterable<ImmutableRoaringBitmap> search(RoaringSearchStrategy strategy, RoaringBound bound)
  {
    Preconditions.checkArgument(bound.getNumDims() == numDims);

    return strategy.search(root, bound);
  }

  public byte[] toBytes()
  {
    ByteBuffer buf = ByteBuffer.allocate(data.capacity());
    buf.put(data.asReadOnlyBuffer());
    return buf.array();
  }

  public int compareTo(RoaringImmutableRTree other)
  {
    return this.data.compareTo(other.data);
  }
}
