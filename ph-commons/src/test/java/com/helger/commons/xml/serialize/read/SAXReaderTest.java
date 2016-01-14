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
package com.helger.commons.xml.serialize.read;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.xml.sax.CachingSAXInputSource;

/**
 * Test class for {@link SAXReader}
 *
 * @author Philip Helger
 */
public final class SAXReaderTest
{
  @SuppressWarnings ("unused")
  private static final Logger s_aLogger = LoggerFactory.getLogger (SAXReaderTest.class);

  @BeforeClass
  public static void bc ()
  {
    SAXReaderDefaultSettings.setExceptionHandler ( (ex) -> {});
  }

  @AfterClass
  public static void ac ()
  {
    SAXReaderDefaultSettings.setExceptionHandler (new XMLLoggingExceptionCallback ());
  }

  @Test
  public void testMultithreadedSAX_CachingSAXInputSource ()
  {
    CommonsTestHelper.testInParallel (1000,
                                      (IThrowingRunnable <SAXException>) () -> assertTrue (SAXReader.readXMLSAX (new CachingSAXInputSource (new ClassPathResource ("xml/buildinfo.xml")),
                                                                                                                 new SAXReaderSettings ().setContentHandler (new DefaultHandler ()))
                                                                                                    .isSuccess ()));
  }

  @Test
  public void testMultithreadedSAX_ReadableResourceSAXInputSource ()
  {
    CommonsTestHelper.testInParallel (1000,
                                      (IThrowingRunnable <SAXException>) () -> assertTrue (SAXReader.readXMLSAX (new ClassPathResource ("xml/buildinfo.xml"),
                                                                                                                 new SAXReaderSettings ().setContentHandler (new DefaultHandler ()))
                                                                                                    .isSuccess ()));
  }
}
