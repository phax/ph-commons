/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link EDAOActionType} and {@link DAOException}.
 *
 * @author Philip Helger
 */
public final class EDAOActionTypeTest
{
  @Test
  public void testGetID ()
  {
    assertEquals ("create", EDAOActionType.CREATE.getID ());
    assertEquals ("update", EDAOActionType.UPDATE.getID ());
    assertEquals ("delete", EDAOActionType.DELETE.getID ());
  }

  @Test
  public void testGetFromIDOrNull ()
  {
    for (final EDAOActionType e : EDAOActionType.values ())
      assertSame (e, EDAOActionType.getFromIDOrNull (e.getID ()));

    assertNull (EDAOActionType.getFromIDOrNull (null));
    assertNull (EDAOActionType.getFromIDOrNull (""));
    assertNull (EDAOActionType.getFromIDOrNull ("does-not-exist"));
  }

  @Test
  public void testGetFromIDOrThrow ()
  {
    for (final EDAOActionType e : EDAOActionType.values ())
      assertSame (e, EDAOActionType.getFromIDOrThrow (e.getID ()));

    try
    {
      EDAOActionType.getFromIDOrThrow ("does-not-exist");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testDAOException ()
  {
    final DAOException aEx = new DAOException ("any message");
    assertEquals ("any message", aEx.getMessage ());
    assertNull (aEx.getCause ());

    final Throwable aCause = new IllegalStateException ("cause");
    final DAOException aEx2 = new DAOException ("any message", aCause);
    assertEquals ("any message", aEx2.getMessage ());
    assertSame (aCause, aEx2.getCause ());
    assertNotNull (aEx2.toString ());
  }
}
