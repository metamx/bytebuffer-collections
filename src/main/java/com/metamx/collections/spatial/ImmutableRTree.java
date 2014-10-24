package com.metamx.collections.spatial;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.metamx.collections.spatial.CompressedBitmaps.ImmutableGenericBitmap;
import com.metamx.collections.spatial.search.Bound;
import com.metamx.collections.spatial.search.GutmanSearchStrategy;
import com.metamx.collections.spatial.search.SearchStrategy;

import java.nio.ByteBuffer;

/**
 * An immutable representation of an {@link com.metamx.collections.spatial.RTree} for spatial indexing.
 */
public class ImmutableRTree
{
    private static byte VERSION = 0x0;

    public static ImmutableRTree newImmutableFromMutable(RTree rTree)
    {
        if (rTree.getSize() == 0) {
            return new ImmutableRTree();
        }
        ByteBuffer buffer = ByteBuffer.wrap(new byte[calcNumBytes(rTree)]);

        buffer.put(VERSION);
        buffer.putInt(rTree.getNumDims());
        int spaceUsed = rTree.getRoot().storeInByteBuffer(buffer, buffer.position());
        buffer.position(0);
        return new ImmutableRTree(buffer.asReadOnlyBuffer(), rTree.getRoot().getBitmap().getEmptyWrappedBitmap().toImmutableGenericBitmap());
    }

    private static int calcNumBytes(RTree tree)
    {
        int total = 1 + Ints.BYTES; // VERSION and numDims

        total += calcNodeBytes(tree.getRoot());

        return total;
    }

    private static int calcNodeBytes(Node node)
    {
        int total = 0;

        // find size of this node
        total += node.getSizeInBytes();

        // recursively find sizes of child nodes
        for (Node child : node.getChildren()) {
            if (node.isLeaf()) {
                total += child.getSizeInBytes();
            } else {
                total += calcNodeBytes(child);
            }
        }

        return total;
    }

    private final int numDims;
    private final ImmutableNode root;
    private final ByteBuffer data;

    private final SearchStrategy defaultSearchStrategy = new GutmanSearchStrategy();

    public ImmutableRTree()
    {
        this.numDims = 0;
        this.data = ByteBuffer.wrap(new byte[]{});
        this.root = null;
    }

    public ImmutableRTree(ByteBuffer data, ImmutableGenericBitmap bitmap)
    {
        final int initPosition = data.position();

        Preconditions.checkArgument(data.get(0) == VERSION, "Mismatching versions");

        this.numDims = data.getInt(1 + initPosition) & 0x7FFF;
        this.data = data;
        this.root = new ImmutableNode(numDims, initPosition, 1 + Ints.BYTES, data, bitmap);
    }

    public int size()
    {
        return data.capacity();
    }

    public ImmutableNode getRoot()
    {
        return root;
    }

    public int getNumDims()
    {
        return numDims;
    }

    public Iterable<ImmutableGenericBitmap> search(Bound bound)
    {
        Preconditions.checkArgument(bound.getNumDims() == numDims);

        return defaultSearchStrategy.search(root, bound);
    }

    public Iterable<ImmutableGenericBitmap> search(SearchStrategy strategy, Bound bound)
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

    public int compareTo(ImmutableRTree other)
    {
        return this.data.compareTo(other.data);
    }
}
