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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Nonnull;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaURLFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaURLFuncTest.class);

  private void _log (@Nonnull final URL aURL) throws URISyntaxException
  {
    LOGGER.info ("Next URL");
    LOGGER.info ("  protocol = " + aURL.getProtocol ());
    LOGGER.info ("  authority = " + aURL.getAuthority ());
    LOGGER.info ("  host = " + aURL.getHost ());
    LOGGER.info ("  port = " + aURL.getPort ());
    LOGGER.info ("  defaultPort = " + aURL.getDefaultPort ());
    LOGGER.info ("  path = " + aURL.getPath ());
    LOGGER.info ("  query = " + aURL.getQuery ());
    LOGGER.info ("  file = " + aURL.getFile ());
    LOGGER.info ("  ref = " + aURL.getRef ());
    LOGGER.info ("  externalForm = " + aURL.toExternalForm ());
    LOGGER.info ("  URI          = " + aURL.toURI ().toString ());
  }

  @Test
  @Ignore ("Too verbose")
  public void testURLParts () throws URISyntaxException, MalformedURLException
  {
    _log (new URL ("http://example.com:80/docs/books/tutorial/index.html?name=networking&x=y#DOWNLOADING"));
    _log (new URL ("http://example.com:82/docs/books/tutorial/index.html?name=networking&x=y#DOWNLOADING"));
    // A Jar entry
    _log (new URL ("jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class"));
    // A Jar file
    _log (new URL ("jar:http://www.foo.com/bar/baz.jar!/"));
    // A Jar directory
    _log (new URL ("jar:http://www.foo.com/bar/baz.jar!/COM/foo/"));
  }
}
