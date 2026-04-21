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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link HostnameVerifierVerifyAll}.<br>
 * Security verification tests demonstrating that TLS validation bypass classes exist and
 * unconditionally accept all certificates/hostnames.
 *
 * @author Philip Helger
 */
public final class HostnameVerifierVerifyAllTest
{
  @Test
  public void testHostnameVerifierVerifyAllAcceptsAnyHostname ()
  {
    // Demonstrates that HostnameVerifierVerifyAll accepts ANY hostname
    final HostnameVerifierVerifyAll aVerifier = new HostnameVerifierVerifyAll (false);
    assertTrue ("Should accept arbitrary hostname", aVerifier.verify ("evil-attacker.com", null));
  }
}
