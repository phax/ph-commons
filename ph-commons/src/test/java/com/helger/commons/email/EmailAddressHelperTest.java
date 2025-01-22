package com.helger.commons.email;

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
  private static final String [] INVALID = { "ph@helger", "karin@gmx-net", "tim@test.com." };

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
