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
package com.helger.dao.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.IDAO;

/**
 * Test class for class {@link DefaultDAOContainer} and {@link AbstractDAOContainer}.
 *
 * @author Philip Helger
 */
public final class DefaultDAOContainerTest
{
  /**
   * A DAO that only records the auto save handling.
   *
   * @author Philip Helger
   */
  private static final class MockDAO implements IDAO
  {
    private int m_nAutoSaveLevel = 0;
    private boolean m_bPendingChanges = false;

    public boolean isAutoSaveEnabled ()
    {
      return m_nAutoSaveLevel == 0;
    }

    public void beginWithoutAutoSave ()
    {
      m_nAutoSaveLevel++;
    }

    public void endWithoutAutoSave ()
    {
      m_nAutoSaveLevel--;
    }

    public boolean hasPendingChanges ()
    {
      return m_bPendingChanges;
    }

    public void writeToFileOnPendingChanges ()
    {
      m_bPendingChanges = false;
    }

    public int getInitCount ()
    {
      return 0;
    }

    public LocalDateTime getLastInitDateTime ()
    {
      return null;
    }

    public int getReadCount ()
    {
      return 0;
    }

    public LocalDateTime getLastReadDateTime ()
    {
      return null;
    }

    public int getWriteCount ()
    {
      return 0;
    }

    public LocalDateTime getLastWriteDateTime ()
    {
      return null;
    }

    void setPendingChanges (final boolean b)
    {
      m_bPendingChanges = b;
    }
  }

  @NonNull
  private static DefaultDAOContainer _create (@NonNull final IDAO... aDAOs)
  {
    return new DefaultDAOContainer (aDAOs);
  }

  @Test
  public void testArrayCtor ()
  {
    final MockDAO aDAO1 = new MockDAO ();
    final MockDAO aDAO2 = new MockDAO ();
    final DefaultDAOContainer aCont = _create (aDAO1, aDAO2);

    final ICommonsList <IDAO> aAll = aCont.getAllContainedDAOs ();
    assertEquals (2, aAll.size ());
    assertTrue (aAll.contains (aDAO1));
    // Must be a copy
    assertTrue (aCont.getAllContainedDAOs () != aAll);
    assertNotNull (aCont.toString ());
  }

  @Test
  public void testIterableCtor ()
  {
    final DefaultDAOContainer aCont = new DefaultDAOContainer (new CommonsArrayList <> (new MockDAO ()));
    assertEquals (1, aCont.getAllContainedDAOs ().size ());
  }

  @Test
  public void testContainsAny ()
  {
    final MockDAO aDAO1 = new MockDAO ();
    final MockDAO aDAO2 = new MockDAO ();
    aDAO2.setPendingChanges (true);
    final DefaultDAOContainer aCont = _create (aDAO1, aDAO2);

    assertTrue (aCont.containsAny (IDAO::hasPendingChanges));
    assertFalse (aCont.containsAny (x -> false));
    assertTrue (aCont.containsAny (null));
  }

  @Test
  public void testAutoSaveIsDelegated ()
  {
    final MockDAO aDAO1 = new MockDAO ();
    final MockDAO aDAO2 = new MockDAO ();
    final DefaultDAOContainer aCont = _create (aDAO1, aDAO2);

    assertTrue (aCont.isAutoSaveEnabled ());

    aCont.beginWithoutAutoSave ();
    assertFalse (aDAO1.isAutoSaveEnabled ());
    assertFalse (aDAO2.isAutoSaveEnabled ());
    assertFalse (aCont.isAutoSaveEnabled ());

    aCont.endWithoutAutoSave ();
    assertTrue (aDAO1.isAutoSaveEnabled ());
    assertTrue (aDAO2.isAutoSaveEnabled ());
    assertTrue (aCont.isAutoSaveEnabled ());
  }

  @Test
  public void testPerformWithoutAutoSave ()
  {
    final MockDAO aDAO = new MockDAO ();
    final DefaultDAOContainer aCont = _create (aDAO);

    aCont.performWithoutAutoSave (() -> assertFalse (aDAO.isAutoSaveEnabled ()));
    assertTrue (aDAO.isAutoSaveEnabled ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new DefaultDAOContainer ((IDAO []) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // Empty is not allowed
      new DefaultDAOContainer (new IDAO [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
