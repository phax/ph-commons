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
package com.helger.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link MimeTypeParameter}
 *
 * @author Philip Helger
 */
public final class MimeTypeParameterTest
{
  @Test
  public void testBasic ()
  {
    MimeTypeParameter p = new MimeTypeParameter ("charset", "iso-8859-1");
    assertEquals ("charset", p.getAttribute ());
    assertEquals ("iso-8859-1", p.getValue ());
    assertFalse (p.isValueRequiringQuoting ());

    TestHelper.testDefaultImplementationWithEqualContentObject (p, new MimeTypeParameter ("charset", "iso-8859-1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (p, new MimeTypeParameter ("charsetname", "iso-8859-1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (p, new MimeTypeParameter ("charset", "utf-8"));

    p = new MimeTypeParameter ("charset", "foo bar");
    assertEquals ("charset", p.getAttribute ());
    assertEquals ("foo bar", p.getValue ());
    assertTrue (p.isValueRequiringQuoting ());
  }

  @Test
  public void testEmptyValue ()
  {
    final MimeTypeParameter p = new MimeTypeParameter ("charset", "");
    assertEquals ("charset", p.getAttribute ());
    assertEquals ("", p.getValue ());
    assertTrue (p.isValueRequiringQuoting ());

    TestHelper.testDefaultImplementationWithEqualContentObject (p, new MimeTypeParameter ("charset", ""));
    TestHelper.testDefaultImplementationWithDifferentContentObject (p, new MimeTypeParameter ("charsetname", "iso-8859-1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (p, new MimeTypeParameter ("charset", "utf-8"));
  }
}
