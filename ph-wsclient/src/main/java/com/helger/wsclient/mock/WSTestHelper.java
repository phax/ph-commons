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
package com.helger.wsclient.mock;

import java.io.File;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

import jakarta.jws.WebService;

/**
 * Test class that checks if the file <code>sun-jaxws.xml</code> is correct.
 *
 * @author Philip Helger
 */
public final class WSTestHelper
{
  public static final String SUN_JAXWS_XML = "sun-jaxws.xml";

  private static final Logger LOGGER = LoggerFactory.getLogger (WSTestHelper.class);

  private WSTestHelper ()
  {}

  @Nonnegative
  public static int testIfAllSunJaxwsFilesAreValid (@NonNull final String sBaseDir, final boolean bContinueOnError)
  {
    final int nTotalImplementationCount = 0;
    final File aFile = new File (sBaseDir, SUN_JAXWS_XML);
    if (aFile.isFile ())
    {
      LOGGER.info ("Checking file " + aFile.getAbsolutePath ());

      final IMicroDocument aDoc = MicroReader.readMicroXML (aFile);
      if (aDoc == null)
      {
        final String sMsg = "The file is invalid XML!";
        LOGGER.warn (sMsg);
        if (!bContinueOnError)
          throw new IllegalStateException (sMsg);
      }
      else
      {
        for (final IMicroElement eEndpoint : aDoc.getDocumentElement ().getAllChildElements ("endpoint"))
        {
          final String sName = eEndpoint.getAttributeValue ("name");
          final String sImplementation = eEndpoint.getAttributeValue ("implementation");

          // Check if implementation class exists
          Class <?> aImplClass = null;
          try
          {
            aImplClass = Class.forName (sImplementation);
          }
          catch (final Exception ex)
          {
            final String sMsg = "The implementation class '" +
                                sImplementation +
                                "' of endpoint '" +
                                sName +
                                "' is invalid - " +
                                ex.getMessage ();
            LOGGER.warn (sMsg);
            if (!bContinueOnError)
              throw new IllegalStateException (sMsg);
          }

          if (aImplClass != null)
          {
            if (LOGGER.isDebugEnabled ())
              LOGGER.debug ("Implementation class '" + sImplementation + "' found");

            final WebService aWebService = aImplClass.getAnnotation (WebService.class);
            if (aWebService == null)
            {
              final String sMsg = "The implementation class '" + sImplementation + "' is missing the @WebService annotation";
              LOGGER.warn (sMsg);
              if (!bContinueOnError)
                throw new IllegalStateException (sMsg);
            }
            else
            {
              final String sEndpointInterface = aWebService.endpointInterface ();

              // Check if interface exists
              Class <?> aInterfaceClass = null;
              try
              {
                aInterfaceClass = Class.forName (sEndpointInterface);
              }
              catch (final Exception ex)
              {
                final String sMsg = "The endpoint interface class '" +
                                    sEndpointInterface +
                                    "' of implementation class '" +
                                    sImplementation +
                                    "' is invalid - " +
                                    ex.getMessage ();
                LOGGER.warn (sMsg);
                if (!bContinueOnError)
                  throw new IllegalStateException (sMsg);
              }

              if (aInterfaceClass != null)
              {
                if (!aInterfaceClass.isInterface ())
                {
                  final String sMsg = "The endpoint interface class '" +
                                      sEndpointInterface +
                                      "' of endpoint '" +
                                      sName +
                                      "' is not an interface!";
                  LOGGER.warn (sMsg);
                  if (!bContinueOnError)
                    throw new IllegalStateException (sMsg);
                }
              }
            }
          }
        }
      }
    }
    return nTotalImplementationCount;
  }

  @Nonnegative
  public static int testIfAllSunJaxwsFilesAreValid (final boolean bContinueOnError)
  {
    int ret = 0;
    ret += testIfAllSunJaxwsFilesAreValid ("src/main/resources/WEB-INF", bContinueOnError);
    ret += testIfAllSunJaxwsFilesAreValid ("src/main/webapp/WEB-INF", bContinueOnError);
    return ret;
  }

  public static void testIfAllSunJaxwsFilesAreValid ()
  {
    testIfAllSunJaxwsFilesAreValid (false);
  }
}
