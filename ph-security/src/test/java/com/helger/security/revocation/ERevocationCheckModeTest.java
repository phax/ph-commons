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
package com.helger.security.revocation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.security.cert.PKIXRevocationChecker;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link ERevocationCheckMode}.
 *
 * @author Philip Helger
 */
public final class ERevocationCheckModeTest
{
  @Test
  public void testBasic ()
  {
    for (final ERevocationCheckMode e : ERevocationCheckMode.values ())
    {
      assertTrue (StringHelper.isNotEmpty (e.getID ()));
      assertSame (e, ERevocationCheckMode.getFromIDOrNull (e.getID ()));
      assertSame (e, ERevocationCheckMode.getFromIDOrDefault (e.getID (), null));
      assertSame (e, ERevocationCheckMode.getFromIDOrDefault (e.getID (), ERevocationCheckMode.NONE));
    }
    assertNull (ERevocationCheckMode.getFromIDOrNull (null));
    assertNull (ERevocationCheckMode.getFromIDOrNull ("blafoo"));
    assertNull (ERevocationCheckMode.getFromIDOrDefault (null, null));
    assertSame (ERevocationCheckMode.NONE,
                ERevocationCheckMode.getFromIDOrDefault ("blafoo", ERevocationCheckMode.NONE));
  }

  @Test
  public void testFlags ()
  {
    assertTrue (ERevocationCheckMode.OCSP_BEFORE_CRL.isOCSP ());
    assertTrue (ERevocationCheckMode.OCSP_BEFORE_CRL.isCRL ());

    assertTrue (ERevocationCheckMode.OCSP.isOCSP ());
    assertTrue (ERevocationCheckMode.OCSP.isOnlyOne ());

    assertTrue (ERevocationCheckMode.CRL_BEFORE_OCSP.isCRL ());
    assertTrue (ERevocationCheckMode.CRL_BEFORE_OCSP.isOCSP ());

    assertTrue (ERevocationCheckMode.CRL.isCRL ());
    assertTrue (ERevocationCheckMode.CRL.isOnlyOne ());

    assertTrue (ERevocationCheckMode.NONE.isNone ());
  }

  @Test
  public void testAddAllOptionsTo ()
  {
    for (final ERevocationCheckMode e : ERevocationCheckMode.values ())
    {
      final Set <PKIXRevocationChecker.Option> aOptions = new HashSet <> ();
      e.addAllOptionsTo (aOptions);
      assertNotNull (aOptions);
    }
  }
}
