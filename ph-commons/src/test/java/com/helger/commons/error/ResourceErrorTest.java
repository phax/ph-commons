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
package com.helger.commons.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.exception.mock.MockException;
import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link ResourceError}.
 *
 * @author Philip Helger
 */
public final class ResourceErrorTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCtor ()
  {
    final IResourceLocation loc = new ResourceLocation ("res", null);
    try
    {
      // location may not be null
      new ResourceError (null, EErrorLevel.ERROR, "bla");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // error level may not be null
      new ResourceError (loc, (EErrorLevel) null, (String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // error text may not be null
      new ResourceError (loc, EErrorLevel.SUCCESS, (String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testBasic ()
  {
    final IResourceLocation loc = new ResourceLocation ("res", "field");
    final IResourceLocation loc2 = new ResourceLocation ("res", "field2");
    ResourceError re = new ResourceError (loc, EErrorLevel.ERROR, "mock error");
    assertEquals (loc, re.getLocation ());
    assertEquals (EErrorLevel.ERROR, re.getErrorLevel ());
    assertEquals ("mock error", re.getDisplayText (CGlobal.LOCALE_ALL));
    assertNull (re.getLinkedException ());
    assertNotNull (re.getAsString (CGlobal.LOCALE_ALL));
    assertFalse (re.isSuccess ());
    assertTrue (re.isFailure ());
    assertTrue (re.isError ());
    assertFalse (re.isNoError ());

    re = new ResourceError (loc, EErrorLevel.WARN, "mock error", new MockException ());
    assertEquals (loc, re.getLocation ());
    assertEquals (EErrorLevel.WARN, re.getErrorLevel ());
    assertEquals ("mock error", re.getDisplayText (CGlobal.LOCALE_ALL));
    assertTrue (re.getLinkedException () instanceof MockException);
    assertNotNull (re.getAsString (CGlobal.LOCALE_ALL));
    assertFalse (re.isSuccess ());
    assertTrue (re.isFailure ());
    assertFalse (re.isError ());
    assertTrue (re.isNoError ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ResourceError (loc,
                                                                                          EErrorLevel.ERROR,
                                                                                          "mock error"),
                                                                       new ResourceError (loc,
                                                                                          EErrorLevel.ERROR,
                                                                                          "mock error"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceError (loc,
                                                                                              EErrorLevel.ERROR,
                                                                                              "mock error"),
                                                                           new ResourceError (loc2,
                                                                                              EErrorLevel.ERROR,
                                                                                              "mock error"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceError (loc,
                                                                                              EErrorLevel.ERROR,
                                                                                              "mock error"),
                                                                           new ResourceError (loc,
                                                                                              EErrorLevel.FATAL_ERROR,
                                                                                              "mock error"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceError (loc,
                                                                                              EErrorLevel.ERROR,
                                                                                              "mock error"),
                                                                           new ResourceError (loc,
                                                                                              EErrorLevel.ERROR,
                                                                                              "mock error2"));
  }
}
