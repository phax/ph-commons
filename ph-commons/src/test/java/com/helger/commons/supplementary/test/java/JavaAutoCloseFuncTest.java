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
package com.helger.commons.supplementary.test.java;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AutoClose implements AutoCloseable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AutoClose.class);

  public AutoClose ()
  {
    LOGGER.info ("AutoClose constructor");
  }

  public void close ()
  {
    // Close is called before the Exception handler and before the finally
    LOGGER.info ("AutoClose.close()");
  }

  public void throwNow ()
  {
    throw new IllegalStateException ("oops");
  }
}

public class JavaAutoCloseFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaAutoCloseFuncTest.class);

  @Test
  @Ignore
  public void testGoodCase ()
  {
    try (final AutoClose aAC = new AutoClose ())
    {
      LOGGER.info ("in try");
    }
    finally
    {
      LOGGER.info ("finally");
    }
  }

  @Test
  @Ignore
  public void testException ()
  {
    try (final AutoClose aAC = new AutoClose ())
    {
      LOGGER.info ("in try");
      aAC.throwNow ();
    }
    catch (final Exception ex)
    {
      LOGGER.info ("Caught exception: " + ex.getMessage ());
    }
    finally
    {
      LOGGER.info ("finally");
    }
  }
}
