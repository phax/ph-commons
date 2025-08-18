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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EmailAddressHelper}
 *
 * @author Philip Helger
 */
public final class EmailAddressHelperTest
{
  // "aa@bb" or "aa.bb@cc" are valid according to the spec, but not correctly
  // handled by the RegEx
  private static final String [] VALID = { "ph@helger.com",
                                           "tim.tester@test.com",
                                           "tim-tester@test.com",
                                           "tim.tester@test.sub.com",
                                           "tim-tester@test.sub.com",
                                           "tim.tester@test.sub.sub.sub.sub.sub.sub.com",
                                           "tim-tester@test.sub.sub.sub.sub.sub.sub.com",
                                           "a.b@c.d",
                                           "abc@bcd.def" };
  private static final String [] VALID_SIMPLE = { "info@kfz-sachverständiger-müller.de" };
  private static final String [] INVALID = { "ph@helger",
                                             "karin@gmx-net",
                                             "tim@test.com.",
                                             "tim @example.com",
                                             "tim@example .com" };

  @Test
  public void testIsValid ()
  {
    for (final String sValid : VALID)
      assertTrue (sValid, EmailAddressHelper.isValid (sValid));
    for (final String sInvalid : INVALID)
      assertFalse (sInvalid, EmailAddressHelper.isValid (sInvalid));
  }

  @Test
  public void testIsValidSimple ()
  {
    for (final String sValid : VALID)
      assertTrue (sValid, EmailAddressHelper.isValidForSimplePattern (sValid));
    for (final String sValid : VALID_SIMPLE)
      assertTrue (sValid, EmailAddressHelper.isValidForSimplePattern (sValid));
    for (final String sInvalid : INVALID)
      assertFalse (sInvalid, EmailAddressHelper.isValidForSimplePattern (sInvalid));
  }
}
