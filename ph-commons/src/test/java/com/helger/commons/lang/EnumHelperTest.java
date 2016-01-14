/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.compare.ESortOrder;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.id.IHasIntID;

/**
 * Test class for class {@link EnumHelper}.
 *
 * @author Philip Helger
 */
public final class EnumHelperTest
{
  public static enum EHasSimpleID implements IHasIntID
  {
   A,
   B;

    public int getID ()
    {
      return ordinal ();
    }
  }

  @Test
  public void testGetEnumID ()
  {
    assertEquals ("com.helger.commons.compare.ESortOrder.ASCENDING", EnumHelper.getEnumID (ESortOrder.ASCENDING));
    assertEquals ("com.helger.commons.lang.EnumHelperTest$EHasSimpleID.A", EnumHelper.getEnumID (EHasSimpleID.A));

    try
    {
      EnumHelper.getEnumID (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetFromIDString ()
  {
    for (final EErrorLevel e : EErrorLevel.values ())
    {
      assertSame (e, EnumHelper.getFromIDOrNull (EErrorLevel.class, e.getID ()));
      assertNull (EnumHelper.getFromIDOrNull (EErrorLevel.class, e.getID () + 'X'));
      assertNull (EnumHelper.getFromIDOrNull (EErrorLevel.class, 'X' + e.getID ()));
      assertSame (e, EnumHelper.getFromIDOrDefault (EErrorLevel.class, e.getID (), EErrorLevel.INFO));
      assertSame (EErrorLevel.INFO,
                  EnumHelper.getFromIDOrDefault (EErrorLevel.class, e.getID () + 'X', EErrorLevel.INFO));
      assertSame (EErrorLevel.INFO,
                  EnumHelper.getFromIDOrDefault (EErrorLevel.class, 'X' + e.getID (), EErrorLevel.INFO));
      assertSame (e, EnumHelper.getFromIDOrThrow (EErrorLevel.class, e.getID ()));
    }

    try
    {
      EnumHelper.getFromIDOrNull ((Class <EErrorLevel>) null, "X");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      EnumHelper.getFromIDOrThrow (EErrorLevel.class, "blafoo");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetFromIDCaseInsensitiveString ()
  {
    for (final EErrorLevel e : EErrorLevel.values ())
    {
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrNull (EErrorLevel.class, e.getID ()));
      assertNull (EnumHelper.getFromIDCaseInsensitiveOrNull (EErrorLevel.class, e.getID () + 'X'));
      assertNull (EnumHelper.getFromIDCaseInsensitiveOrNull (EErrorLevel.class, 'X' + e.getID ()));
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class, e.getID (), EErrorLevel.INFO));
      assertSame (e,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class,
                                                                e.getID ().toLowerCase (Locale.US),
                                                                EErrorLevel.INFO));
      assertSame (e,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class,
                                                                e.getID ().toUpperCase (Locale.US),
                                                                EErrorLevel.INFO));
      assertSame (EErrorLevel.INFO,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class, e.getID () + 'X', EErrorLevel.INFO));
      assertSame (EErrorLevel.INFO,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class, 'X' + e.getID (), EErrorLevel.INFO));
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrThrow (EErrorLevel.class, e.getID ()));
    }

    try
    {
      EnumHelper.getFromIDCaseInsensitiveOrNull ((Class <EErrorLevel>) null, "X");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      EnumHelper.getFromIDCaseInsensitiveOrThrow (EErrorLevel.class, "blafoo");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetFromIDInt ()
  {
    for (final EHasSimpleID e : EHasSimpleID.values ())
    {
      assertSame (e, EnumHelper.getFromIDOrNull (EHasSimpleID.class, e.getID ()));
      assertNull (EnumHelper.getFromIDOrNull (EHasSimpleID.class, e.getID () + 999));
      assertSame (e, EnumHelper.getFromIDOrDefault (EHasSimpleID.class, e.getID (), EHasSimpleID.A));
      assertSame (EHasSimpleID.A, EnumHelper.getFromIDOrDefault (EHasSimpleID.class, e.getID () + 999, EHasSimpleID.A));
      assertSame (e, EnumHelper.getFromIDOrThrow (EHasSimpleID.class, e.getID ()));
    }

    try
    {
      EnumHelper.getFromIDOrNull ((Class <EHasSimpleID>) null, 14);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      EnumHelper.getFromIDOrThrow (EHasSimpleID.class, 4711);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
