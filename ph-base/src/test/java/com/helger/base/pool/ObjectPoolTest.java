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
package com.helger.base.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for class {@link ObjectPool}.
 *
 * @author Philip Helger
 */
public final class ObjectPoolTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ObjectPoolTest.class);

  @Test
  public void testSimple () throws InterruptedException
  {
    final int ITEMS = 5;
    final Thread aThread = new Thread ()
    {
      @Override
      public void run ()
      {
        try
        {
          final ObjectPool <String> aOP = new ObjectPool <> (ITEMS, () -> "any");
          assertEquals (ITEMS, aOP.getPoolSize ());
          assertEquals (0, aOP.getBorrowedObjectCount ());

          for (int i = 0; i < ITEMS; ++i)
            if (!"any".equals (aOP.borrowObject ()))
              throw new IllegalStateException ("Failed to borrow object");

          assertEquals (ITEMS, aOP.getPoolSize ());
          assertEquals (ITEMS, aOP.getBorrowedObjectCount ());

          // hangs because pool has only 5 objects
          if (aOP.borrowObject () != null)
            throw new IllegalStateException ("Borrowed too much");

          // Start returning
          for (int i = 0; i < ITEMS; ++i)
            if (aOP.returnObject ("any").isFailure ())
              throw new IllegalStateException ("Failed to return object");

          assertEquals (ITEMS, aOP.getPoolSize ());
          assertEquals (0, aOP.getBorrowedObjectCount ());

          // Cannot return more than that
          if (aOP.returnObject ("any").isSuccess ())
            throw new IllegalStateException ("Returned too much");
        }
        catch (final Throwable t)
        {
          LOGGER.error ("Failure", t);
        }
        LOGGER.info ("Done");
      }
    };
    aThread.start ();
    Thread.sleep (100);
    aThread.interrupt ();
    aThread.join ();
  }

  @Test
  public void testBorrowAndReturn ()
  {
    final ObjectPool <StringBuilder> aPool = new ObjectPool <> (2, StringBuilder::new);
    assertEquals (2, aPool.getPoolSize ());
    assertEquals (0, aPool.getBorrowedObjectCount ());

    final StringBuilder aSB1 = aPool.borrowObject ();
    assertNotNull (aSB1);
    assertEquals (1, aPool.getBorrowedObjectCount ());

    final StringBuilder aSB2 = aPool.borrowObject ();
    assertNotNull (aSB2);
    assertEquals (2, aPool.getBorrowedObjectCount ());
    assertNotNull (aPool.toString ());

    assertTrue (aPool.returnObject (aSB1).isSuccess ());
    assertTrue (aPool.returnObject (aSB2).isSuccess ());
    assertEquals (0, aPool.getBorrowedObjectCount ());

    // Returning an object that was not borrowed fails
    assertTrue (aPool.returnObject (new StringBuilder ()).isFailure ());

    // All unused items are removed
    aPool.clearUnusedItems ();
    assertEquals (2, aPool.getPoolSize ());
    assertNotNull (aPool.borrowObject ());
  }
}
