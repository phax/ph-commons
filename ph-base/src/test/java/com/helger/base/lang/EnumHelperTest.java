/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.base.compare.ESortOrder;
import com.helger.base.id.IHasID;
import com.helger.base.id.IHasIntID;

import jakarta.annotation.Nonnull;

/**
 * Test class for class {@link EnumHelper}.
 *
 * @author Philip Helger
 */
public final class EnumHelperTest
{
  private enum EHasSimpleID implements IHasIntID
  {
    A,
    B;

    public int getID ()
    {
      return ordinal ();
    }
  }

  private enum EMockEL implements IHasID <String>
  {
    INFO,
    WARN,
    ERROR;

    @Nonnull
    public String getID ()
    {
      return name ().toLowerCase (Locale.ROOT);
    }
  }

  @Test
  public void testGetEnumID ()
  {
    assertEquals ("com.helger.base.compare.ESortOrder.ASCENDING", EnumHelper.getEnumID (ESortOrder.ASCENDING));
    assertEquals ("com.helger.base.lang.EnumHelperTest$EHasSimpleID.A", EnumHelper.getEnumID (EHasSimpleID.A));

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
    for (final EMockEL e : EMockEL.values ())
    {
      assertSame (e, EnumHelper.getFromIDOrNull (EMockEL.class, e.getID ()));
      assertNull (EnumHelper.getFromIDOrNull (EMockEL.class, e.getID () + 'X'));
      assertNull (EnumHelper.getFromIDOrNull (EMockEL.class, 'X' + e.getID ()));
      assertSame (e, EnumHelper.getFromIDOrDefault (EMockEL.class, e.getID (), EMockEL.INFO));
      assertSame (EMockEL.INFO, EnumHelper.getFromIDOrDefault (EMockEL.class, e.getID () + 'X', EMockEL.INFO));
      assertSame (EMockEL.INFO, EnumHelper.getFromIDOrDefault (EMockEL.class, 'X' + e.getID (), EMockEL.INFO));
      assertSame (e, EnumHelper.getFromIDOrThrow (EMockEL.class, e.getID ()));
    }

    try
    {
      EnumHelper.getFromIDOrNull ((Class <EMockEL>) null, "X");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      EnumHelper.getFromIDOrThrow (EMockEL.class, "blafoo");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetFromIDCaseInsensitiveString ()
  {
    for (final EMockEL e : EMockEL.values ())
    {
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrNull (EMockEL.class, e.getID ()));
      assertNull (EnumHelper.getFromIDCaseInsensitiveOrNull (EMockEL.class, e.getID () + 'X'));
      assertNull (EnumHelper.getFromIDCaseInsensitiveOrNull (EMockEL.class, 'X' + e.getID ()));
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrDefault (EMockEL.class, e.getID (), EMockEL.INFO));
      assertSame (e,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EMockEL.class,
                                                                e.getID ().toLowerCase (Locale.US),
                                                                EMockEL.INFO));
      assertSame (e,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EMockEL.class,
                                                                e.getID ().toUpperCase (Locale.US),
                                                                EMockEL.INFO));
      assertSame (EMockEL.INFO,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EMockEL.class, e.getID () + 'X', EMockEL.INFO));
      assertSame (EMockEL.INFO,
                  EnumHelper.getFromIDCaseInsensitiveOrDefault (EMockEL.class, 'X' + e.getID (), EMockEL.INFO));
      assertSame (e, EnumHelper.getFromIDCaseInsensitiveOrThrow (EMockEL.class, e.getID ()));
    }

    try
    {
      EnumHelper.getFromIDCaseInsensitiveOrNull ((Class <EMockEL>) null, "X");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      EnumHelper.getFromIDCaseInsensitiveOrThrow (EMockEL.class, "blafoo");
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
