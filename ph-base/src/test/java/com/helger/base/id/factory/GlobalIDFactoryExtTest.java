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
package com.helger.base.id.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.state.EChange;

/**
 * Additional test class for class {@link GlobalIDFactory}, covering the persistent factories and
 * the bulk ID creation.
 *
 * @author Philip Helger
 */
public final class GlobalIDFactoryExtTest
{
  private IIntIDFactory m_aOldInt;
  private ILongIDFactory m_aOldLong;
  private IStringIDFactory m_aOldString;

  @Before
  public void rememberFactories ()
  {
    // These are global static settings that other tests may have changed
    m_aOldInt = GlobalIDFactory.hasPersistentIntIDFactory () ? GlobalIDFactory.getPersistentIntIDFactory () : null;
    m_aOldLong = GlobalIDFactory.hasPersistentLongIDFactory () ? GlobalIDFactory.getPersistentLongIDFactory () : null;
    m_aOldString = GlobalIDFactory.hasPersistentStringIDFactory () ? GlobalIDFactory.getPersistentStringIDFactory ()
                                                                   : null;
    GlobalIDFactory.setPersistentIntIDFactory (null);
    GlobalIDFactory.setPersistentLongIDFactory (null);
    GlobalIDFactory.setPersistentStringIDFactory (null);
  }

  @After
  public void restoreFactories ()
  {
    GlobalIDFactory.setPersistentIntIDFactory (m_aOldInt);
    GlobalIDFactory.setPersistentLongIDFactory (m_aOldLong);
    GlobalIDFactory.setPersistentStringIDFactory (m_aOldString);
  }

  @Test
  public void testNonPersistentFactoriesArePresent ()
  {
    assertTrue (GlobalIDFactory.hasIntIDFactory ());
    assertNotNull (GlobalIDFactory.getIntIDFactory ());
    assertTrue (GlobalIDFactory.hasLongIDFactory ());
    assertNotNull (GlobalIDFactory.getLongIDFactory ());
    assertTrue (GlobalIDFactory.hasStringIDFactory ());
    assertNotNull (GlobalIDFactory.getStringIDFactory ());
  }

  @Test
  public void testSetAndUsePersistentIntFactory ()
  {
    assertFalse (GlobalIDFactory.hasPersistentIntIDFactory ());

    final IIntIDFactory aFactory = new MemoryIntIDFactory ();
    assertSame (EChange.CHANGED, GlobalIDFactory.setPersistentIntIDFactory (aFactory));
    assertSame (EChange.UNCHANGED, GlobalIDFactory.setPersistentIntIDFactory (aFactory));
    assertTrue (GlobalIDFactory.hasPersistentIntIDFactory ());
    assertSame (aFactory, GlobalIDFactory.getPersistentIntIDFactory ());

    GlobalIDFactory.getNewPersistentIntID ();
    assertEquals (3, GlobalIDFactory.getBulkNewPersistentIntIDs (3).length);
  }

  @Test
  public void testSetAndUsePersistentLongFactory ()
  {
    assertFalse (GlobalIDFactory.hasPersistentLongIDFactory ());

    final ILongIDFactory aFactory = new MemoryLongIDFactory ();
    assertSame (EChange.CHANGED, GlobalIDFactory.setPersistentLongIDFactory (aFactory));
    assertTrue (GlobalIDFactory.hasPersistentLongIDFactory ());
    assertSame (aFactory, GlobalIDFactory.getPersistentLongIDFactory ());

    GlobalIDFactory.getNewPersistentLongID ();
    assertEquals (3, GlobalIDFactory.getBulkNewPersistentLongIDs (3).length);
  }

  @Test
  public void testSetAndUsePersistentStringFactory ()
  {
    assertFalse (GlobalIDFactory.hasPersistentStringIDFactory ());

    final IStringIDFactory aFactory = new StringIDFromIntIDFactory (new MemoryIntIDFactory ());
    assertSame (EChange.CHANGED, GlobalIDFactory.setPersistentStringIDFactory (aFactory));
    assertTrue (GlobalIDFactory.hasPersistentStringIDFactory ());
    assertSame (aFactory, GlobalIDFactory.getPersistentStringIDFactory ());

    assertNotNull (GlobalIDFactory.getNewPersistentStringID ());
    assertEquals (3, GlobalIDFactory.getBulkNewPersistentStringIDs (3).length);
  }

  @Test
  public void testPersistentWithoutFactoryFails ()
  {
    assertFalse (GlobalIDFactory.hasPersistentIntIDFactory ());
    try
    {
      GlobalIDFactory.getNewPersistentIntID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    try
    {
      GlobalIDFactory.getNewPersistentLongID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    try
    {
      GlobalIDFactory.getNewPersistentStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBulkNonPersistent ()
  {
    assertEquals (3, GlobalIDFactory.getBulkNewIntIDs (3).length);
    assertEquals (3, GlobalIDFactory.getBulkNewLongIDs (3).length);
    assertEquals (3, GlobalIDFactory.getBulkNewStringIDs (3).length);

    assertNotNull (GlobalIDFactory.getNewStringID ());
    assertTrue (GlobalIDFactory.getNewIntID () >= 0);
    assertTrue (GlobalIDFactory.getNewLongID () >= 0);
  }

  @Test
  public void testBulkInvalidCount ()
  {
    try
    {
      GlobalIDFactory.getBulkNewIntIDs (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
