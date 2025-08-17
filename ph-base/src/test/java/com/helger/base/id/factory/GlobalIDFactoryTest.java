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
package com.helger.base.id.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link GlobalIDFactory}.
 *
 * @author Philip Helger
 */
public final class GlobalIDFactoryTest
{
  private void _setDefault ()
  {
    GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory ());
    GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory ());
    GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory ());
    GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory ());
    GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ());
    GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ());

    assertTrue (GlobalIDFactory.hasIntIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentIntIDFactory ());
    assertTrue (GlobalIDFactory.hasLongIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentLongIDFactory ());
    assertTrue (GlobalIDFactory.hasStringIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentStringIDFactory ());
  }

  @Before
  public void before ()
  {
    _setDefault ();
  }

  @After
  public void after ()
  {
    _setDefault ();
  }

  @Test
  public void testSetFactories ()
  {
    // int factories
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryStaticIntIDFactory ()).isChanged ());
    assertFalse (GlobalIDFactory.setIntIDFactory (new MemoryStaticIntIDFactory ()).isChanged ());

    assertTrue (GlobalIDFactory.hasIntIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentIntIDFactory ());
    GlobalIDFactory.getNewIntID ();
    GlobalIDFactory.getNewPersistentIntID ();

    // long factories
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryStaticLongIDFactory ()).isChanged ());
    assertFalse (GlobalIDFactory.setLongIDFactory (new MemoryStaticLongIDFactory ()).isChanged ());

    assertTrue (GlobalIDFactory.hasLongIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentLongIDFactory ());
    GlobalIDFactory.getNewLongID ();
    GlobalIDFactory.getNewPersistentLongID ();

    // string factories
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix1")).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix2")).isChanged ());
    assertFalse (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix2")).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix1"))
                               .isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix2"))
                               .isChanged ());
    assertFalse (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix2"))
                                .isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix1")).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix2")).isChanged ());
    assertFalse (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix2")).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix1"))
                               .isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix2"))
                               .isChanged ());
    assertFalse (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix2"))
                                .isChanged ());
    assertTrue (GlobalIDFactory.hasStringIDFactory ());
    assertTrue (GlobalIDFactory.hasPersistentStringIDFactory ());
    GlobalIDFactory.getNewStringID ();
    GlobalIDFactory.getNewPersistentStringID ();
  }

  @Test
  public void testGetNewID ()
  {
    // value always > 0?
    assertTrue (GlobalIDFactory.getNewIntID () > 0);
    assertTrue (GlobalIDFactory.getNewLongID () > 0);

    // 2 invocation return different results?
    assertNotEquals (GlobalIDFactory.getNewIntID (), GlobalIDFactory.getNewIntID ());
    assertNotEquals (GlobalIDFactory.getNewPersistentIntID (), GlobalIDFactory.getNewPersistentIntID ());

    assertNotEquals (GlobalIDFactory.getNewLongID (), GlobalIDFactory.getNewLongID ());
    assertNotEquals (GlobalIDFactory.getNewPersistentLongID (), GlobalIDFactory.getNewPersistentLongID ());

    assertNotEquals (GlobalIDFactory.getNewStringID (), GlobalIDFactory.getNewStringID ());
    assertNotEquals (GlobalIDFactory.getNewPersistentStringID (), GlobalIDFactory.getNewPersistentStringID ());
  }

  @Test
  public void testNoFactories ()
  {
    assertTrue (GlobalIDFactory.setIntIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (null).isChanged ());
    assertFalse (GlobalIDFactory.hasIntIDFactory ());
    assertFalse (GlobalIDFactory.hasPersistentIntIDFactory ());

    try
    {
      GlobalIDFactory.getNewIntID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewPersistentIntID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    assertTrue (GlobalIDFactory.setLongIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (null).isChanged ());
    assertFalse (GlobalIDFactory.hasLongIDFactory ());
    assertFalse (GlobalIDFactory.hasPersistentLongIDFactory ());

    try
    {
      GlobalIDFactory.getNewLongID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewPersistentLongID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    assertTrue (GlobalIDFactory.setStringIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (null).isChanged ());
    assertFalse (GlobalIDFactory.hasStringIDFactory ());
    assertFalse (GlobalIDFactory.hasPersistentStringIDFactory ());

    try
    {
      GlobalIDFactory.getNewStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewPersistentStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testTooLongStringID ()
  {
    final String sTooLongPrefix = StringHelper.getRepeated ('a', GlobalIDFactory.STRING_ID_MAX_LENGTH);

    // It works in the beginning
    GlobalIDFactory.getNewStringID ();
    try
    {
      GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory (sTooLongPrefix));
      // This fails
      GlobalIDFactory.getNewStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }
    finally
    {
      // reset
      GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ());
    }
    // It works afterwards again
    GlobalIDFactory.getNewStringID ();

    // It works in the beginning
    GlobalIDFactory.getNewPersistentStringID ();
    try
    {
      GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory (sTooLongPrefix));
      // This fails
      GlobalIDFactory.getNewPersistentStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }
    finally
    {
      // reset
      GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ());
    }
    // It works afterwards again
    GlobalIDFactory.getNewPersistentStringID ();
  }
}
