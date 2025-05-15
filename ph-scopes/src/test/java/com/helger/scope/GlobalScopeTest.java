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
package com.helger.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.helger.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.mutable.MutableBoolean;

/**
 * Test class for class {@link GlobalScope}.
 *
 * @author Philip Helger
 */
public final class GlobalScopeTest
{
  @Test
  public void testAll ()
  {
    final GlobalScope aGS = new GlobalScope ("test");
    assertEquals ("test", aGS.getID ());
    assertEquals (0, aGS.attrs ().size ());
    assertTrue (aGS.attrs ().putIn ("key1", "whatsoever").isChanged ());
    assertEquals (1, aGS.attrs ().size ());
    final MutableBoolean aPreDestroyedCalled = new MutableBoolean (false);
    final MutableBoolean aDestroyedCalled = new MutableBoolean (false);
    assertTrue (aGS.attrs ().putIn ("key2", new IScopeDestructionAware ()
    {
      public void onBeforeScopeDestruction (@Nonnull final IScope aScopeToBeDestroyed) throws Exception
      {
        assertFalse (aPreDestroyedCalled.booleanValue ());
        assertFalse (aDestroyedCalled.booleanValue ());
        // Mark as destroyed
        aPreDestroyedCalled.set (true);
      }

      public void onScopeDestruction (@Nonnull final IScope aScopeInDestruction) throws Exception
      {
        assertTrue (aPreDestroyedCalled.booleanValue ());
        assertFalse (aDestroyedCalled.booleanValue ());
        // Mark as destroyed
        aDestroyedCalled.set (true);
      }
    }).isChanged ());
    assertEquals (2, aGS.attrs ().size ());
    // Check null value - exception
    try
    {
      aGS.attrs ().putIn ("key3", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // ConcurrentHashMap does not allow null values
    }
    assertEquals (2, aGS.attrs ().size ());
    assertTrue (aGS.isValid ());
    assertFalse (aGS.isInPreDestruction ());
    assertFalse (aGS.isInDestruction ());
    assertFalse (aGS.isDestroyed ());

    // Did the scope destruction aware class trigger?
    assertFalse (aPreDestroyedCalled.booleanValue ());
    assertFalse (aDestroyedCalled.booleanValue ());

    // destroy scope
    aGS.destroyScope ();

    assertFalse (aGS.isValid ());
    assertFalse (aGS.isInPreDestruction ());
    assertFalse (aGS.isInDestruction ());
    assertTrue (aGS.isDestroyed ());

    // Did the scope destruction aware class trigger?
    assertTrue (aPreDestroyedCalled.booleanValue ());
    assertTrue (aDestroyedCalled.booleanValue ());
  }
}
