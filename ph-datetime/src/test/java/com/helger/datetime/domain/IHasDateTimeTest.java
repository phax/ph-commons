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
package com.helger.datetime.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * Test class for the default methods of {@link IHasCreationDateTime},
 * {@link IHasLastModificationDateTime}, {@link IHasDeletionDateTime} and {@link IHasTrashDateTime}.
 *
 * @author Philip Helger
 */
public final class IHasDateTimeTest
{
  private static final LocalDateTime DT = LocalDateTime.of (2026, 9, 20, 12, 30, 15);

  @Test
  public void testCreationDateTime ()
  {
    final IHasCreationDateTime aPresent = () -> DT;
    assertTrue (aPresent.hasCreationDateTime ());
    assertEquals (DT.toLocalDate (), aPresent.getCreationDate ());
    assertEquals (DT.toLocalTime (), aPresent.getCreationTime ());
    // Created at the very same time and afterwards
    assertTrue (aPresent.isCreatedAt (DT));
    assertTrue (aPresent.isCreatedAt (DT.plusSeconds (1)));
    assertFalse (aPresent.isCreatedAt (DT.minusSeconds (1)));

    final IHasCreationDateTime aAbsent = () -> null;
    assertFalse (aAbsent.hasCreationDateTime ());
    assertNull (aAbsent.getCreationDate ());
    assertNull (aAbsent.getCreationTime ());
    assertFalse (aAbsent.isCreatedAt (DT));

    try
    {
      aPresent.isCreatedAt (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testLastModificationDateTime ()
  {
    final IHasLastModificationDateTime aPresent = () -> DT;
    assertTrue (aPresent.hasLastModificationDateTime ());
    assertEquals (DT.toLocalDate (), aPresent.getLastModificationDate ());
    assertEquals (DT.toLocalTime (), aPresent.getLastModificationTime ());
    assertTrue (aPresent.isLastModifiedAt (DT));
    assertTrue (aPresent.isLastModifiedAt (DT.plusSeconds (1)));
    assertFalse (aPresent.isLastModifiedAt (DT.minusSeconds (1)));

    final IHasLastModificationDateTime aAbsent = () -> null;
    assertFalse (aAbsent.hasLastModificationDateTime ());
    assertNull (aAbsent.getLastModificationDate ());
    assertNull (aAbsent.getLastModificationTime ());
    assertFalse (aAbsent.isLastModifiedAt (DT));

    try
    {
      aPresent.isLastModifiedAt (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testDeletionDateTime ()
  {
    final IHasDeletionDateTime aPresent = () -> DT;
    assertTrue (aPresent.hasDeletionDateTime ());
    assertEquals (DT.toLocalDate (), aPresent.getDeletionDate ());
    assertEquals (DT.toLocalTime (), aPresent.getDeletionTime ());
    assertTrue (aPresent.isDeletedAt (DT));
    assertTrue (aPresent.isDeletedAt (DT.plusSeconds (1)));
    assertFalse (aPresent.isDeletedAt (DT.minusSeconds (1)));

    final IHasDeletionDateTime aAbsent = () -> null;
    assertFalse (aAbsent.hasDeletionDateTime ());
    assertNull (aAbsent.getDeletionDate ());
    assertNull (aAbsent.getDeletionTime ());
    assertFalse (aAbsent.isDeletedAt (DT));

    try
    {
      aPresent.isDeletedAt (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testTrashDateTime ()
  {
    final IHasTrashDateTime aPresent = () -> DT;
    assertTrue (aPresent.hasTrashDateTime ());
    assertEquals (DT.toLocalDate (), aPresent.getTrashDate ());
    assertEquals (DT.toLocalTime (), aPresent.getTrashTime ());
    assertTrue (aPresent.isTrashedAt (DT));
    assertTrue (aPresent.isTrashedAt (DT.plusSeconds (1)));
    assertFalse (aPresent.isTrashedAt (DT.minusSeconds (1)));

    final IHasTrashDateTime aAbsent = () -> null;
    assertFalse (aAbsent.hasTrashDateTime ());
    assertNull (aAbsent.getTrashDate ());
    assertNull (aAbsent.getTrashTime ());
    assertFalse (aAbsent.isTrashedAt (DT));

    try
    {
      aPresent.isTrashedAt (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
