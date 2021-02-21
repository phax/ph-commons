/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.datetime;

import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Represents the combination of Zone ID string and ID object.
 *
 * @author Philip Helger
 * @since 8.6.5
 */
public class PDTZoneID
{
  private static final ICommonsList <PDTZoneID> ZONES = new CommonsArrayList <> ();

  static
  {
    // Longest strings first!
    ZONES.add (PDTZoneID.of ("UTC"));
    ZONES.add (PDTZoneID.of ("GMT"));
    ZONES.add (PDTZoneID.ofHours ("EST", -5));
    ZONES.add (PDTZoneID.ofHours ("EDT", -4));
    ZONES.add (PDTZoneID.ofHours ("CST", -6));
    ZONES.add (PDTZoneID.ofHours ("CDT", -5));
    ZONES.add (PDTZoneID.ofHours ("MST", -7));
    ZONES.add (PDTZoneID.ofHours ("MDT", -6));
    ZONES.add (PDTZoneID.ofHours ("PST", -8));
    ZONES.add (PDTZoneID.ofHours ("PDT", -7));
    ZONES.add (PDTZoneID.of ("UT"));
    ZONES.add (PDTZoneID.ofHours ("A", -1));
    ZONES.add (PDTZoneID.ofHours ("B", -2));
    ZONES.add (PDTZoneID.ofHours ("C", -3));
    ZONES.add (PDTZoneID.ofHours ("D", -4));
    ZONES.add (PDTZoneID.ofHours ("E", -5));
    ZONES.add (PDTZoneID.ofHours ("F", -6));
    ZONES.add (PDTZoneID.ofHours ("G", -7));
    ZONES.add (PDTZoneID.ofHours ("H", -8));
    ZONES.add (PDTZoneID.ofHours ("I", -9));
    ZONES.add (PDTZoneID.ofHours ("K", -10));
    ZONES.add (PDTZoneID.ofHours ("L", -11));
    ZONES.add (PDTZoneID.ofHours ("M", -12));
    ZONES.add (PDTZoneID.ofHours ("N", +1));
    ZONES.add (PDTZoneID.ofHours ("O", +2));
    ZONES.add (PDTZoneID.ofHours ("P", +3));
    ZONES.add (PDTZoneID.ofHours ("Q", +4));
    ZONES.add (PDTZoneID.ofHours ("R", +5));
    ZONES.add (PDTZoneID.ofHours ("S", +6));
    ZONES.add (PDTZoneID.ofHours ("T", +7));
    ZONES.add (PDTZoneID.ofHours ("U", +8));
    ZONES.add (PDTZoneID.ofHours ("V", +9));
    ZONES.add (PDTZoneID.ofHours ("W", +10));
    ZONES.add (PDTZoneID.ofHours ("X", +11));
    ZONES.add (PDTZoneID.ofHours ("Y", +12));
    ZONES.add (PDTZoneID.of ("Z"));
  }

  private final String m_sZoneID;
  private final ZoneId m_aZoneID;

  protected PDTZoneID (@Nonnull @Nonempty final String sZoneID, @Nonnull final ZoneId aZoneId)
  {
    m_sZoneID = ValueEnforcer.notEmpty (sZoneID, "ZoneIDString");
    m_aZoneID = ValueEnforcer.notNull (aZoneId, "ZoneID");
  }

  @Nonnull
  @Nonempty
  public String getZoneIDString ()
  {
    return m_sZoneID;
  }

  @Nonnull
  public ZoneId getZoneID ()
  {
    return m_aZoneID;
  }

  @Nonnull
  public static PDTZoneID of (@Nonnull final String sZoneID)
  {
    return new PDTZoneID (sZoneID, ZoneId.of (sZoneID));
  }

  @Nonnull
  public static PDTZoneID ofHours (@Nonnull final String sZoneID, final int nHours)
  {
    return new PDTZoneID (sZoneID, ZoneOffset.ofHours (nHours));
  }

  @Nonnull
  public static ICommonsIterable <PDTZoneID> getDefaultZoneIDs ()
  {
    return ZONES;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <PDTZoneID> getAllDefaultZoneIDs ()
  {
    return ZONES.getClone ();
  }
}
