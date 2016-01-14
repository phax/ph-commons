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
package com.helger.commons.regex;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for {@link RegExPattern}.
 *
 * @author Philip Helger
 */
public final class RegExPatternTest extends AbstractCommonsTestCase
{
  /**
   * Test method checkPatternConsistency
   */
  @Test
  public void testCheckPatternConsistency ()
  {
    RegExPattern.checkPatternConsistency ("any\\$here");
    RegExPattern.checkPatternConsistency ("at the end$");
    RegExPattern.checkPatternConsistency ("group $1 of $0");
    RegExPattern.checkPatternConsistency ("$1 and $2 are OK");
    try
    {
      // RegEx contains unquoted $!
      RegExPattern.checkPatternConsistency ("any$here");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
