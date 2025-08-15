package com.helger.commons.url;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link URNHelper}.
 *
 * @author Philip Helger
 */
public final class URNHelperTest
{
  @Test
  public void testIsValidURN ()
  {
    assertTrue (URNHelper.isValidURN ("urn:de4a:dba:canonical-evidence:v0.5"));
    assertTrue (URNHelper.isValidURN ("urn:de4a::dba:canonical-evidence:v0.5"));
    assertTrue (URNHelper.isValidURN ("urn:de4a:dba:canonical-evidence::v0.5"));
    assertTrue (URNHelper.isValidURN ("urn:de4a:::::::::::::::::::::::::::"));

    assertFalse (URNHelper.isValidURN ("ur:de4a:dba:canonical-evidence:v0.5"));
    assertFalse (URNHelper.isValidURN ("urn:de4a"));
    assertFalse (URNHelper.isValidURN ("urn::de4a:dba:canonical-evidence:v0.5"));
    assertFalse (URNHelper.isValidURN (null));
    assertFalse (URNHelper.isValidURN (""));
    assertFalse (URNHelper.isValidURN (" urn:de4a:dba:canonical-evidence:v0.5"));
    assertFalse (URNHelper.isValidURN ("urn:de4a:dba:canonical-evidence:v0.5 "));
    assertFalse (URNHelper.isValidURN ("  urn:de4a:dba:canonical-evidence:v0.5  "));
  }
}
