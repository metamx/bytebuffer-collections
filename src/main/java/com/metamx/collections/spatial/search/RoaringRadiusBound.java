package com.metamx.collections.spatial.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.metamx.collections.spatial.ImmutablePoint;
import com.metamx.collections.spatial.RoaringImmutablePoint;

/**
 */
public class RoaringRadiusBound extends RoaringRectangularBound
{
  private static float[] getMinCoords(float[] coords, float radius)
  {
    float[] retVal = new float[coords.length];
    for (int i = 0; i < coords.length; i++) {
      retVal[i] = coords[i] - radius;
    }
    return retVal;
  }

  private static float[] getMaxCoords(float[] coords, float radius)
  {
    float[] retVal = new float[coords.length];
    for (int i = 0; i < coords.length; i++) {
      retVal[i] = coords[i] + radius;
    }
    return retVal;
  }

  private final float[] coords;
  private final float radius;

  @JsonCreator
  public RoaringRadiusBound(
          @JsonProperty("coords") float[] coords,
          @JsonProperty("radius") float radius,
          @JsonProperty("limit") int limit
  )
  {
    super(getMinCoords(coords, radius), getMaxCoords(coords, radius), limit);

    this.coords = coords;
    this.radius = radius;
  }

  public RoaringRadiusBound(
          float[] coords,
          float radius
  )
  {
    this(coords, radius, 0);
  }

  @JsonProperty
  public float[] getCoords()
  {
    return coords;
  }

  @JsonProperty
  public float getRadius()
  {
    return radius;
  }

  @Override
  public boolean contains(float[] otherCoords)
  {
    double total = 0.0;
    for (int i = 0; i < coords.length; i++) {
      total += Math.pow(otherCoords[i] - coords[i], 2);
    }

    return (total <= Math.pow(radius, 2));
  }

  @Override
  public Iterable<RoaringImmutablePoint> filter(Iterable<RoaringImmutablePoint> points)
  {
    return Iterables.filter(
        points,
        new Predicate<RoaringImmutablePoint>()
        {
          @Override
          public boolean apply(RoaringImmutablePoint point)
          {
            return contains(point.getCoords());
          }
        }
    );
  }
}
