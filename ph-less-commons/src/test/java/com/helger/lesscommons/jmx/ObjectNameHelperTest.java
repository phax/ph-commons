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
package com.helger.lesscommons.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.lesscommons.jmx.CJMX;
import com.helger.lesscommons.jmx.ObjectNameHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link ObjectNameHelper}.
 *
 * @author Philip Helger
 */
public final class ObjectNameHelperTest
{
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  @Test
  public void testDefaultJMXDomain ()
  {
    assertEquals (CJMX.PH_JMX_DOMAIN, ObjectNameHelper.getDefaultJMXDomain ());
    ObjectNameHelper.setDefaultJMXDomain ("abc");
    assertEquals ("abc", ObjectNameHelper.getDefaultJMXDomain ());

    try
    {
      // Space not allowed
      ObjectNameHelper.setDefaultJMXDomain ("ab c");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // Colon not allowed
      ObjectNameHelper.setDefaultJMXDomain ("ab:c");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // null not allowed
      ObjectNameHelper.setDefaultJMXDomain (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // empty string not allowed
      ObjectNameHelper.setDefaultJMXDomain ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals ("abc", ObjectNameHelper.getDefaultJMXDomain ());
  }
}
