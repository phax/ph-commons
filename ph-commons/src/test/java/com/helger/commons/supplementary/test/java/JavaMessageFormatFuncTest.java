/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.Locale;

import org.junit.Test;

public final class JavaMessageFormatFuncTest
{
  @Test
  public void testBasicFormat ()
  {
    assertEquals ("Hallo Welt!", new MessageFormat ("Hallo {0}!", Locale.US).format (new Object [] { "Welt" }));
    assertEquals ("Hallo Welt!", new MessageFormat ("{1} {0}!", Locale.US).format (new Object [] { "Welt", "Hallo" }));
    assertEquals ("Hallo 17!",
                  new MessageFormat ("Hallo {0}!", Locale.US).format (new Object [] { Integer.valueOf (17) }));
  }
}
