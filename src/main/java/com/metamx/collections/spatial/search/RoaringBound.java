package com.metamx.collections.spatial.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.metamx.collections.spatial.ImmutablePoint;
import com.metamx.collections.spatial.RoaringImmutableNode;
import com.metamx.collections.spatial.RoaringImmutablePoint;

/**
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes(value={
    @JsonSubTypes.Type(name="rectangular", value=RectangularBound.class),
    @JsonSubTypes.Type(name="radius", value=RadiusBound.class)
})
public interface RoaringBound
{
  public int getLimit();

  public int getNumDims();

  public boolean overlaps(RoaringImmutableNode node);

  public boolean contains(float[] coords);

  public Iterable<RoaringImmutablePoint> filter(Iterable<RoaringImmutablePoint> points);

  public byte[] getCacheKey();
}
