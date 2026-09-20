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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.functional.IThrowingSupplier;
import com.helger.base.iface.IThrowingRunnable;
import com.helger.base.state.EChange;

/**
 * Test class for the default methods of {@link IAutoSaveAware}.
 *
 * @author Philip Helger
 */
public final class IAutoSaveAwareTest
{
  /**
   * Counts the begin/end calls of the auto save handling.
   *
   * @author Philip Helger
   */
  private static final class MockAutoSaveAware implements IAutoSaveAware
  {
    private int m_nBegin = 0;
    private int m_nEnd = 0;

    public boolean isAutoSaveEnabled ()
    {
      return m_nBegin == m_nEnd;
    }

    public void beginWithoutAutoSave ()
    {
      m_nBegin++;
    }

    public void endWithoutAutoSave ()
    {
      m_nEnd++;
    }
  }

  @Test
  public void testPerformWithoutAutoSaveRunnable ()
  {
    final MockAutoSaveAware aObj = new MockAutoSaveAware ();
    assertTrue (aObj.isAutoSaveEnabled ());

    final boolean [] aInvoked = new boolean [1];
    aObj.performWithoutAutoSave (() -> {
      aInvoked[0] = true;
      assertFalse (aObj.isAutoSaveEnabled ());
    });
    assertTrue (aInvoked[0]);
    assertTrue (aObj.isAutoSaveEnabled ());
  }

  @Test
  public void testPerformWithoutAutoSaveSupplier ()
  {
    final MockAutoSaveAware aObj = new MockAutoSaveAware ();
    assertEquals (EChange.CHANGED, aObj.performWithoutAutoSave (() -> EChange.CHANGED));
    assertTrue (aObj.isAutoSaveEnabled ());
  }

  @Test
  public void testPerformWithoutAutoSaveThrowing () throws DAOException
  {
    final MockAutoSaveAware aObj = new MockAutoSaveAware ();

    // Without exception
    aObj.performWithoutAutoSaveThrowing ((IThrowingRunnable <DAOException>) () -> { /* empty */ });
    assertTrue (aObj.isAutoSaveEnabled ());

    // With exception - the end must still be invoked
    try
    {
      aObj.performWithoutAutoSaveThrowing ((IThrowingRunnable <DAOException>) () -> {
        throw new DAOException ("mock");
      });
      fail ();
    }
    catch (final DAOException ex)
    {
      // expected
    }
    assertTrue (aObj.isAutoSaveEnabled ());

    // The same for the supplier based overload
    assertEquals ("value",
                  aObj.performWithoutAutoSaveThrowing ((IThrowingSupplier <String, DAOException>) () -> "value"));
    assertTrue (aObj.isAutoSaveEnabled ());
  }

  @Test
  public void testInvalidParams ()
  {
    final MockAutoSaveAware aObj = new MockAutoSaveAware ();
    try
    {
      aObj.performWithoutAutoSave ((Runnable) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aObj.performWithoutAutoSave ((java.util.function.Supplier <String>) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aObj.performWithoutAutoSaveThrowing ((IThrowingRunnable <DAOException>) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException | DAOException ex)
    {
      // expected
    }
  }
}
