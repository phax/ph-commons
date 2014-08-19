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
package com.helger.commons.regex;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.PatternSyntaxException;

import org.junit.Test;

import com.helger.commons.mock.AbstractPHTestCase;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for {@link RegExPool}.
 * 
 * @author Philip Helger
 */
public final class RegExPoolTest extends AbstractPHTestCase
{
  /**
   * Test method getPattern
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetPattern ()
  {
    assertNotNull (RegExPool.getPattern ("xy"));
    assertSame (RegExPool.getPattern ("xy"), RegExPool.getPattern ("xy"));
    assertNotSame (RegExPool.getPattern ("xy"), RegExPool.getPattern ("yy"));

    try
    {
      // empty string not allowed
      RegExPool.getPattern ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null not allowed
      RegExPool.getPattern (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Illegal regular expression
      RegExPool.getPattern ("[a-z+");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      assertNotNull (ex.getCause ());
      assertTrue (ex.getCause () instanceof PatternSyntaxException);
    }
  }
}
