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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.BaseTestHelper;

/**
 * Test class for class {@link AbstractPersistingIntIDFactory},
 * {@link AbstractPersistingLongIDFactory} and the String based ID factories.
 *
 * @author Philip Helger
 */
public final class PersistingIDFactoryTest
{
  /**
   * An in-memory persisting int ID factory.
   *
   * @author Philip Helger
   */
  private static final class MockIntIDFactory extends AbstractPersistingIntIDFactory
  {
    private int m_nCounter = 0;
    private int m_nReadCalls = 0;

    MockIntIDFactory (final int nReserveCount)
    {
      super (nReserveCount);
    }

    @Override
    protected int readAndUpdateIDCounter (final int nReserveCount)
    {
      m_nReadCalls++;
      final int nRet = m_nCounter;
      m_nCounter += nReserveCount;
      return nRet;
    }

    int getReadCalls ()
    {
      return m_nReadCalls;
    }
  }

  /**
   * An in-memory persisting long ID factory.
   *
   * @author Philip Helger
   */
  private static final class MockLongIDFactory extends AbstractPersistingLongIDFactory
  {
    private long m_nCounter = 0;
    private int m_nReadCalls = 0;

    MockLongIDFactory (final int nReserveCount)
    {
      super (nReserveCount);
    }

    @Override
    protected long readAndUpdateIDCounter (final int nReserveCount)
    {
      m_nReadCalls++;
      final long nRet = m_nCounter;
      m_nCounter += nReserveCount;
      return nRet;
    }

    int getReadCalls ()
    {
      return m_nReadCalls;
    }
  }

  @Test
  public void testIntFactory ()
  {
    final MockIntIDFactory a = new MockIntIDFactory (5);
    assertEquals (5, a.getReserveCount ());
    assertNotNull (a.toString ());

    // The first call reserves a block
    for (int i = 0; i < 5; ++i)
      assertEquals (i, a.getNewID ());
    assertEquals (1, a.getReadCalls ());

    // The next call reserves the next block
    assertEquals (5, a.getNewID ());
    assertEquals (2, a.getReadCalls ());
  }

  @Test
  public void testIntFactorySetReserveCount ()
  {
    final MockIntIDFactory a = new MockIntIDFactory (5);
    a.setReserveCount (10);
    assertEquals (10, a.getReserveCount ());

    try
    {
      a.setReserveCount (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new MockIntIDFactory (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testIntFactoryEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new MockIntIDFactory (5),
                                                                    new MockIntIDFactory (5));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new MockIntIDFactory (5),
                                                                        new MockIntIDFactory (6));
  }

  @Test
  public void testLongFactory ()
  {
    final MockLongIDFactory a = new MockLongIDFactory (5);
    assertEquals (5, a.getReserveCount ());
    assertNotNull (a.toString ());

    for (int i = 0; i < 5; ++i)
      assertEquals (i, a.getNewID ());
    assertEquals (1, a.getReadCalls ());

    assertEquals (5L, a.getNewID ());
    assertEquals (2, a.getReadCalls ());
  }

  @Test
  public void testLongFactoryEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new MockLongIDFactory (5),
                                                                    new MockLongIDFactory (5));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new MockLongIDFactory (5),
                                                                        new MockLongIDFactory (6));
  }

  @Test
  public void testStringIDFromIntIDFactory ()
  {
    final StringIDFromIntIDFactory a = new StringIDFromIntIDFactory (new MemoryIntIDFactory ());
    assertNotNull (a.getNewID ());
    assertNotNull (a.toString ());

    final StringIDFromIntIDFactory aPrefixed = new StringIDFromIntIDFactory (new MemoryIntIDFactory (), "pre-");
    assertTrue (aPrefixed.getNewID ().startsWith ("pre-"));

    // The factory is only dereferenced when an ID is created
    try
    {
      new StringIDFromIntIDFactory (null).getNewID ();
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testStringIDFromLongIDFactory ()
  {
    final StringIDFromLongIDFactory a = new StringIDFromLongIDFactory (new MemoryLongIDFactory ());
    assertNotNull (a.getNewID ());
    assertNotNull (a.toString ());

    final StringIDFromLongIDFactory aPrefixed = new StringIDFromLongIDFactory (new MemoryLongIDFactory (), "pre-");
    assertTrue (aPrefixed.getNewID ().startsWith ("pre-"));

    // The factory is only dereferenced when an ID is created
    try
    {
      new StringIDFromLongIDFactory (null).getNewID ();
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testStringIDFromUUIDFactory ()
  {
    final StringIDFromUUIDFactory a = new StringIDFromUUIDFactory ();
    final String sID = a.getNewID ();
    assertNotNull (sID);
    assertFalse (sID.equals (a.getNewID ()));
    assertNotNull (a.toString ());
    assertNotNull (StringIDFromUUIDFactory.INSTANCE);

    BaseTestHelper.testDefaultImplementationWithEqualContentObject (a, new StringIDFromUUIDFactory ());
  }
}
