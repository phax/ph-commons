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
package com.helger.xml.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.io.stream.StreamHelper;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link StringSAXInputSource}.
 *
 * @author Philip Helger
 */
public final class StringSAXInputSourceTest
{
  @Test
  public void testBasic ()
  {
    final StringSAXInputSource sis = new StringSAXInputSource ("bla foo 90");
    assertEquals ("bla foo 90", StreamHelper.getAllCharactersAsString (sis.getCharacterStream ()));
    TestHelper.testToStringImplementation (sis);
  }

  @Test
  public void testCreationError ()
  {
    try
    {
      new StringSAXInputSource ((char []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
