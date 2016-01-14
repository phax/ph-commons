/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.resourceprovider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link DefaultResourceProvider}.
 *
 * @author Philip Helger
 */
public final class DefaultResourceProviderTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testNoPrefix ()
  {
    final DefaultResourceProvider aDRP = new DefaultResourceProvider ();
    assertTrue (aDRP.supportsReading ("test1.txt"));
    assertTrue (aDRP.supportsReading ("http://www.helger.com"));
    assertTrue (aDRP.supportsReading (new File ("test1.txt").getAbsolutePath ()));

    assertNotNull (aDRP.getReadableResource ("test1.txt"));
    assertNotNull (aDRP.getReadableResource ("http://www.helger.com"));
    assertNotNull (aDRP.getReadableResource (new File ("test1.txt").getAbsolutePath ()));

    assertTrue (aDRP.supportsWriting ("test1.txt"));
    assertFalse (aDRP.supportsWriting ("http://www.helger.com"));
    assertTrue (aDRP.supportsWriting (new File ("test1.txt").getAbsolutePath ()));

    assertNotNull (aDRP.getWritableResource ("test1.txt"));
    try
    {
      aDRP.getWritableResource ("http://www.helger.com");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    assertNotNull (aDRP.getWritableResource (new File ("test1.txt").getAbsolutePath ()));

    assertFalse (aDRP.supportsReading (null));
    assertFalse (aDRP.supportsReading (""));
    assertFalse (aDRP.supportsWriting (null));
    assertFalse (aDRP.supportsWriting (""));

    try
    {
      aDRP.getReadableResource (null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testEqualsAndHashcode ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new DefaultResourceProvider (),
                                                                       new DefaultResourceProvider ());
  }
}
