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
package com.helger.commons.errorlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.error.EErrorLevel;

/**
 * Test class for class {@link FormErrors}.
 *
 * @author Philip Helger
 */
public final class FormErrorsTest
{
  @Test
  public void testBasic ()
  {
    final FormErrors aFEL = new FormErrors ();
    assertTrue (aFEL.isEmpty ());
    assertFalse (aFEL.hasErrorsOrWarnings ());
    assertEquals (EErrorLevel.SUCCESS, aFEL.getMostSevereErrorLevel ());

    aFEL.addFieldInfo ("f1", "info");
    assertFalse (aFEL.isEmpty ());
    assertFalse (aFEL.hasErrorsOrWarnings ());
    assertEquals (EErrorLevel.INFO, aFEL.getMostSevereErrorLevel ());

    aFEL.addFieldError ("f2", "error");
    assertFalse (aFEL.isEmpty ());
    assertTrue (aFEL.hasErrorsOrWarnings ());
    assertEquals (EErrorLevel.ERROR, aFEL.getMostSevereErrorLevel ());

    assertNotNull (aFEL.getListOfField ("f1"));
    assertFalse (aFEL.getListOfField ("f1").isEmpty ());
    assertNotNull (aFEL.getListOfField ("f1-gibtsned"));
    assertTrue (aFEL.getListOfField ("f1-gibtsned").isEmpty ());

    assertTrue (aFEL.clear ().isChanged ());
    assertFalse (aFEL.clear ().isChanged ());

    assertTrue (aFEL.isEmpty ());
    assertFalse (aFEL.hasErrorsOrWarnings ());
    assertEquals (EErrorLevel.SUCCESS, aFEL.getMostSevereErrorLevel ());
  }
}
