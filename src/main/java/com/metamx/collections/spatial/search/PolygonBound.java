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

package com.metamx.collections.spatial.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Floats;
import com.metamx.collections.spatial.ImmutableNode;
import com.metamx.collections.spatial.ImmutablePoint;

import java.nio.ByteBuffer;
import java.util.List;

/**
 */
public class PolygonBound extends RectangularBound
{
  private final float[] abscissa;
  private final float[] ordinate;

  @JsonProperty
  public float[] getOrdinate()
  {
    return ordinate;
  }

  @JsonProperty
  public float[] getAbscissa()
  {
    return abscissa;
  }

  private static float[] getMinCoords(float[] abscissa, float[] ordinate)
  {
    float[] retVal = new float[2];
    retVal[0] = abscissa[0];
    retVal[1] = ordinate[0];

    for (int i = 1; i < abscissa.length; i++) {
      if (abscissa[i] < retVal[0])
        retVal[0] = abscissa[i];
      if (ordinate[i] < retVal[1])
        retVal[1] = ordinate[i];
    }
    return retVal;
  }

  private static float[] getMaxCoords(float[] abscissa, float[] ordinate)
  {
    float[] retVal = new float[2];
    retVal[0] = abscissa[0];
    retVal[1] = ordinate[0];
    for (int i = 1; i < abscissa.length; i++)
    {
      if (abscissa[i] > retVal[0])
        retVal[0] = abscissa[i];
      if (ordinate[i] > retVal[1])
        retVal[1] = ordinate[i];
    }
    return retVal;
  }

  /**
   * abscissa and ordinate contain the coordinates of polygon.
   * abscissa[i] is the horizontal coordinate for the i'th corner of the polygon,
   * and ordinate[i] is the vertical coordinate for the i'th corner.
   * The polygon must have more than 2 corners, so the length of abscissa or ordinate must be equal or greater than 3.
   * @param abscissa
   * @param ordinate
   * @param limit
     * @return
     */
  @JsonCreator
  public static PolygonBound from(
      @JsonProperty("abscissa") float[] abscissa,
      @JsonProperty("ordinate") float[] ordinate,
      @JsonProperty("limit") int limit
  )
  {
    Preconditions.checkArgument(abscissa.length == ordinate.length);
    Preconditions.checkArgument(abscissa.length >= 3);
    return new PolygonBound(abscissa, ordinate, limit);
  }

  public static PolygonBound from(float[] abscissa, float[] ordinate)
  {
    return PolygonBound.from(abscissa, ordinate, 0);
  }

  private PolygonBound (float[] abscissa, float[] ordinate, int limit)
  {
    super(getMinCoords(abscissa, ordinate), getMaxCoords(abscissa, ordinate), limit);
    this.abscissa = abscissa;
    this.ordinate = ordinate;
  }

  @Override
  public boolean contains(float[] coords)
  {
    int polyCorners = abscissa.length;
    int j = polyCorners - 1;
    boolean oddNodes = false;
    for (int i = 0; i < polyCorners; i++)
    {
      if ((ordinate[i] < coords[1] && ordinate[j] >= coords[1]
          || ordinate[j] < coords[1] && ordinate[i] >= coords[1])
          && (abscissa[i] <= coords[0] || abscissa[j] <= coords[0]))
      {
        if (abscissa[i] + (coords[1] - ordinate[i]) / (ordinate[j] - ordinate[i]) * (abscissa[j] - abscissa[i]) < coords[0])
        {
          oddNodes = !oddNodes;
        }
      }
      j = i;
    }
    return oddNodes;
  }

  @Override
  public Iterable<ImmutablePoint> filter(Iterable<ImmutablePoint> points)
  {
    return Iterables.filter(
        points,
        new Predicate<ImmutablePoint>()
        {
          @Override
          public boolean apply(ImmutablePoint immutablePoint)
          {
            return contains(immutablePoint.getCoords());
          }
        }
    );
  }
}
