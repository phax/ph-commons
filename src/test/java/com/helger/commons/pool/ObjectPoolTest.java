/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.concurrent.ThreadUtils;
import com.helger.commons.factory.FactoryConstantValue;

/**
 * Test class for class {@link ObjectPool}.
 * 
 * @author Philip Helger
 */
public final class ObjectPoolTest
{
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
          final ObjectPool <String> aOP = new ObjectPool <String> (ITEMS, new FactoryConstantValue <String> ("any"));
          for (int i = 0; i < ITEMS; ++i)
            assertEquals ("any", aOP.borrowObject ());

          // hangs because pool has only 5 objects
          assertNull (aOP.borrowObject ());

          // Start returning
          for (int i = 0; i < ITEMS; ++i)
            assertTrue (aOP.returnObject ("any").isSuccess ());

          // Cannot return more than that
          assertFalse (aOP.returnObject ("any").isSuccess ());
        }
        catch (final Throwable t)
        {
          t.printStackTrace ();
        }
        System.out.println ("Done");
      }
    };
    aThread.start ();
    ThreadUtils.sleep (100);
    aThread.interrupt ();
    aThread.join ();
  }
}
