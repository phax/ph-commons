/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.io.Serializable;
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
public class PDTZoneID implements Serializable
{
  private static ICommonsList <PDTZoneID> s_aZIS = new CommonsArrayList <> ();

  static
  {
    // Longest strings first!
    s_aZIS.add (PDTZoneID.of ("UTC"));
    s_aZIS.add (PDTZoneID.of ("GMT"));
    s_aZIS.add (PDTZoneID.ofHours ("EST", -5));
    s_aZIS.add (PDTZoneID.ofHours ("EDT", -4));
    s_aZIS.add (PDTZoneID.ofHours ("CST", -6));
    s_aZIS.add (PDTZoneID.ofHours ("CDT", -5));
    s_aZIS.add (PDTZoneID.ofHours ("MST", -7));
    s_aZIS.add (PDTZoneID.ofHours ("MDT", -6));
    s_aZIS.add (PDTZoneID.ofHours ("PST", -8));
    s_aZIS.add (PDTZoneID.ofHours ("PDT", -7));
    s_aZIS.add (PDTZoneID.of ("UT"));
    s_aZIS.add (PDTZoneID.ofHours ("A", -1));
    s_aZIS.add (PDTZoneID.ofHours ("B", -2));
    s_aZIS.add (PDTZoneID.ofHours ("C", -3));
    s_aZIS.add (PDTZoneID.ofHours ("D", -4));
    s_aZIS.add (PDTZoneID.ofHours ("E", -5));
    s_aZIS.add (PDTZoneID.ofHours ("F", -6));
    s_aZIS.add (PDTZoneID.ofHours ("G", -7));
    s_aZIS.add (PDTZoneID.ofHours ("H", -8));
    s_aZIS.add (PDTZoneID.ofHours ("I", -9));
    s_aZIS.add (PDTZoneID.ofHours ("K", -10));
    s_aZIS.add (PDTZoneID.ofHours ("L", -11));
    s_aZIS.add (PDTZoneID.ofHours ("M", -12));
    s_aZIS.add (PDTZoneID.ofHours ("N", +1));
    s_aZIS.add (PDTZoneID.ofHours ("O", +2));
    s_aZIS.add (PDTZoneID.ofHours ("P", +3));
    s_aZIS.add (PDTZoneID.ofHours ("Q", +4));
    s_aZIS.add (PDTZoneID.ofHours ("R", +5));
    s_aZIS.add (PDTZoneID.ofHours ("S", +6));
    s_aZIS.add (PDTZoneID.ofHours ("T", +7));
    s_aZIS.add (PDTZoneID.ofHours ("U", +8));
    s_aZIS.add (PDTZoneID.ofHours ("V", +9));
    s_aZIS.add (PDTZoneID.ofHours ("W", +10));
    s_aZIS.add (PDTZoneID.ofHours ("X", +11));
    s_aZIS.add (PDTZoneID.ofHours ("Y", +12));
    s_aZIS.add (PDTZoneID.of ("Z"));
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
    return s_aZIS;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <PDTZoneID> getAllDefaultZoneIDs ()
  {
    return s_aZIS.getClone ();
  }
}
