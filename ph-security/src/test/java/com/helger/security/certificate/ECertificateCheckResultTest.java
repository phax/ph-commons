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
package com.helger.security.certificate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link ECertificateCheckResult}.
 *
 * @author Philip Helger
 */
public final class ECertificateCheckResultTest
{
  @Test
  public void testBasic ()
  {
    for (final ECertificateCheckResult e : ECertificateCheckResult.values ())
    {
      assertTrue (StringHelper.isNotEmpty (e.getID ()));
      assertTrue (StringHelper.isNotEmpty (e.getReason ()));
      assertSame (e, ECertificateCheckResult.getFromIDOrNull (e.getID ()));
      assertSame (e, ECertificateCheckResult.getFromIDOrDefault (e.getID (), null));
    }
    assertNull (ECertificateCheckResult.getFromIDOrNull (null));
    assertNull (ECertificateCheckResult.getFromIDOrNull ("blafoo"));
    assertSame (ECertificateCheckResult.NOT_CHECKED,
                ECertificateCheckResult.getFromIDOrDefault ("blafoo", ECertificateCheckResult.NOT_CHECKED));
  }

  @Test
  public void testIsValid ()
  {
    // Only VALID counts as valid - everything else (including the new
    // REVOCATION_STATUS_UNKNOWN) does not.
    assertTrue (ECertificateCheckResult.VALID.isValid ());
    assertFalse (ECertificateCheckResult.NO_CERTIFICATE_PROVIDED.isValid ());
    assertFalse (ECertificateCheckResult.NOT_YET_VALID.isValid ());
    assertFalse (ECertificateCheckResult.EXPIRED.isValid ());
    assertFalse (ECertificateCheckResult.UNSUPPORTED_ISSUER.isValid ());
    assertFalse (ECertificateCheckResult.REVOKED.isValid ());
    assertFalse (ECertificateCheckResult.REVOCATION_STATUS_UNKNOWN.isValid ());
    assertFalse (ECertificateCheckResult.NOT_CHECKED.isValid ());
  }
}
