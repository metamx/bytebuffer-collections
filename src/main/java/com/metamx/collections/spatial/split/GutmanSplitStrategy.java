package com.metamx.collections.spatial.split;

import CompressedBitmaps.GenericBitmap;
import CompressedBitmaps.ImmutableGenericBitmap;

import com.google.common.collect.Lists;
import com.metamx.collections.spatial.Node;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.metamx.collections.spatial.RTreeUtils;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 */
public abstract class GutmanSplitStrategy implements SplitStrategy
{
    private final int minNumChildren;
    private final int maxNumChildren;
    private final GenericBitmap bitmap;

    protected GutmanSplitStrategy(int minNumChildren, int maxNumChildren, GenericBitmap bitmap)
    {
        this.minNumChildren = minNumChildren;
        this.maxNumChildren = maxNumChildren;
        this.bitmap = bitmap;
    }

    @Override
    public boolean needToSplit(Node node)
    {
        return (node.getChildren().size() > maxNumChildren);
    }

    /**
     * This algorithm is from the original paper.
     *
     * Algorithm Split. Divide a set of M+1 index entries into two groups.
     *
     * S1. [Pick first entry for each group]. Apply Algorithm {@link #pickSeeds(java.util.List)} to choose
     * two entries to be the first elements of the groups. Assign each to a group.
     *
     * S2. [Check if done]. If all entries have been assigned, stop. If one group has so few entries that all the rest
     * must be assigned to it in order for it to have the minimum number m, assign them and stop.
     *
     * S3. [Select entry to assign]. Invoke Algorithm {@link #(java.util.List, com.metamx.collections.spatial.Node[])}
     * to choose the next entry to assign. Add it to the group whose covering rectangle will have to be enlarged least to
     * accommodate it. Resolve ties by adding the entry to the group smaller area, then to the one with fewer entries, then
     * to either. Repeat from S2.
     */
    @Override
    public Node[] split(Node node)
    {
        List<Node> children = Lists.newArrayList(node.getChildren());
        Node[] seeds = pickSeeds(children);

        node.clear();
        node.addChild(seeds[0]);
        node.addToBitmap(seeds[0]);

        Node group1 = new Node(
                Arrays.copyOf(seeds[1].getMinCoordinates(), seeds[1].getMinCoordinates().length),
                Arrays.copyOf(seeds[1].getMaxCoordinates(), seeds[1].getMaxCoordinates().length),
                Lists.newArrayList(seeds[1]),
                node.isLeaf(),
                node.getParent(),
                this.bitmap.getEmptyWrappedBitmap()
        );
        group1.addToBitmap(seeds[1]);
        if (node.getParent() != null) {
            node.getParent().addChild(group1);
        }
        Node[] groups = new Node[]{
                node, group1
        };

        RTreeUtils.enclose(groups);

        while (!children.isEmpty()) {
            for (Node group : groups) {
                if (group.getChildren().size() + children.size() <= minNumChildren) {
                    for (Node child : group.getChildren()) {
                        group.addToBitmap(child);
                        group.addChild(child);
                    }
                    RTreeUtils.enclose(groups);
                    return groups;
                }
            }

            Node nextToAssign = pickNext(children, groups);
            double group0ExpandedArea = RTreeUtils.getEnclosingArea(groups[0], nextToAssign);
            double group1ExpandedArea = RTreeUtils.getEnclosingArea(groups[1], nextToAssign);

            Node optimal;
            if (group0ExpandedArea < group1ExpandedArea) {
                optimal = groups[0];
            } else if (group0ExpandedArea == group1ExpandedArea) {
                if (groups[0].getArea() < groups[1].getArea()) {
                    optimal = groups[0];
                } else {
                    optimal = groups[1];
                }
            } else {
                optimal = groups[1];
            }

            optimal.addToBitmap(nextToAssign);
            optimal.addChild(nextToAssign);
            optimal.enclose();
        }

        return groups;
    }

    public abstract Node[] pickSeeds(List<Node> children);

    public abstract Node pickNext(List<Node> children, Node[] groups);
}
