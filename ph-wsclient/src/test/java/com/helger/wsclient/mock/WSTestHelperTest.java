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
package com.helger.wsclient.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link WSTestHelper}.
 *
 * @author Philip Helger
 */
public final class WSTestHelperTest
{
  private static final String BASE = "src/test/resources/wstest/";

  @Test
  public void testNoSuchFile ()
  {
    // A directory without a sun-jaxws.xml is silently ignored
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "does-not-exist", false));
  }

  @Test
  public void testValidFile ()
  {
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "valid", false));
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "valid", true));
  }

  @Test
  public void testInvalidXML ()
  {
    // Continue on error just logs
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "invalidxml", true));
    try
    {
      WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "invalidxml", false);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testUnknownImplementationClass ()
  {
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "badimpl", true));
    try
    {
      WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "badimpl", false);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testMissingWebServiceAnnotation ()
  {
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "noannotation", true));
    try
    {
      WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "noannotation", false);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testEndpointInterfaceIsNotAnInterface ()
  {
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "badinterface", true));
    try
    {
      WSTestHelper.testIfAllSunJaxwsFilesAreValid (BASE + "badinterface", false);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testDefaultDirectories ()
  {
    // This project has no WEB-INF directories at all
    assertEquals (0, WSTestHelper.testIfAllSunJaxwsFilesAreValid (true));
    WSTestHelper.testIfAllSunJaxwsFilesAreValid ();
  }
}
