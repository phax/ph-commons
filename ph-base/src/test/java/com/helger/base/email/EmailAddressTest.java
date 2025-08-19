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
package com.helger.base.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.BaseTestHelper;

/**
 * Test class for class {@link EmailAddress}.
 *
 * @author Philip Helger
 */
public final class EmailAddressTest
{
  @Test
  public void testBasic ()
  {
    try
    {
      new EmailAddress ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new EmailAddress ("haha-no-email-address");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    IEmailAddress aMA = new EmailAddress ("ph@example.org");
    assertEquals ("ph@example.org", aMA.getAddress ());
    assertNull (aMA.getPersonal ());

    BaseTestHelper.testDefaultImplementationWithEqualContentObject (aMA, new EmailAddress ("ph@example.org"));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (aMA, new EmailAddress ("ph@example2.org"));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (aMA,
                                                                        new EmailAddress ("ph@example.org", "My name"));

    aMA = new EmailAddress ("ph@example.org", "Philip");
    assertEquals ("ph@example.org", aMA.getAddress ());
    assertEquals ("Philip", aMA.getPersonal ());
  }
}
