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
package com.helger.http.security;

import java.security.cert.X509Certificate;

import org.junit.Test;

/**
 * Test class for class {@link TrustManagerTrustAll}.<br>
 * Security verification tests demonstrating that TLS validation bypass classes exist and
 * unconditionally accept all certificates/hostnames.
 *
 * @author Philip Helger
 */
public final class TrustManagerTrustAllTest
{
  @Test
  public void testTrustManagerTrustAllAcceptsNullChain () throws Exception
  {
    // Demonstrates that TrustManagerTrustAll accepts ANY certificate chain,
    // including null
    final TrustManagerTrustAll aStrategy = new TrustManagerTrustAll ();
    aStrategy.checkClientTrusted (null, "RSA");
    aStrategy.checkServerTrusted (null, "RSA");
  }

  @Test
  public void testTrustManagerTrustAllAcceptsEmptyChain () throws Exception
  {
    final TrustManagerTrustAll aStrategy = new TrustManagerTrustAll ();
    aStrategy.checkClientTrusted (new X509Certificate [0], "RSA");
    aStrategy.checkServerTrusted (new X509Certificate [0], "RSA");
  }
}
