package com.metamx.collections.spatial.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.ImmutablePoint;;

/**
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes(value={
        @JsonSubTypes.Type(name="rectangular", value=RectangularBound.class),
        @JsonSubTypes.Type(name="radius", value=RadiusBound.class)
})
public interface Bound
{
    public int getLimit();

    public int getNumDims();

    public boolean overlaps(ImmutableNode node);

    public boolean contains(float[] coords);

    public Iterable<ImmutablePoint> filter(Iterable<ImmutablePoint> points);

    public byte[] getCacheKey();
}
