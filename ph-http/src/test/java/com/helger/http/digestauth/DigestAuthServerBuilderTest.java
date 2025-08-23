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
package com.helger.http.digestauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.state.ETriState;
import com.helger.url.ReadOnlyURL;

/**
 * Test class for class {@link DigestAuthServerBuilder}.
 *
 * @author Philip Helger
 */
public final class DigestAuthServerBuilderTest
{
  @Test
  public void testBasic ()
  {
    final DigestAuthServerBuilder b = new DigestAuthServerBuilder ();
    try
    {
      // Mandatory realm not present
      b.build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    b.setRealm ("xyz");
    try
    {
      // Mandatory nonce not present
      b.build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    b.setNonce ("blanonce");
    assertEquals ("Digest realm=\"xyz\", nonce=\"blanonce\"", b.build ());
    b.addDomain (ReadOnlyURL.of ("/config"));
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\"", b.build ());
    b.setOpaque ("opaque");
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\", opaque=\"opaque\"", b.build ());
    b.setStale (ETriState.FALSE);
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\", opaque=\"opaque\", stale=false",
                  b.build ());
    b.setAlgorithm (HttpDigestAuth.ALGORITHM_MD5_SESS);
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\", opaque=\"opaque\", stale=false, algorithm=MD5-sess",
                  b.build ());
    b.addQOP (HttpDigestAuth.QOP_AUTH);
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\", opaque=\"opaque\", stale=false, algorithm=MD5-sess, qop=\"auth\"",
                  b.build ());
    b.addQOP (HttpDigestAuth.QOP_AUTH_INT);
    assertEquals ("Digest realm=\"xyz\", domain=\"/config\", nonce=\"blanonce\", opaque=\"opaque\", stale=false, algorithm=MD5-sess, qop=\"auth,auth-int\"",
                  b.build ());
  }
}
