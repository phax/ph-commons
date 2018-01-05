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
package com.helger.commons.supplementary.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.stream.StreamHelper;

public final class MainValidFileNames
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainValidFileNames.class);

  private MainValidFileNames ()
  {}

  private static void _testName (final String sName)
  {
    final String fileName = sName + ".txt";
    final File f = new File (fileName);

    s_aLogger.info ("--- testing name: " + sName + " ---");
    s_aLogger.info (sName + ".exists()=" + f.exists ());
    s_aLogger.info (sName + ".getAbsolutePath()=" + f.getAbsolutePath ());
    s_aLogger.info (sName + ".canRead()=" + f.canRead ());
    s_aLogger.info (sName + ".canWrite()=" + f.canWrite ());

    s_aLogger.info ("Writing into " + fileName + " ...");

    try (final OutputStream out = FileHelper.getOutputStream (f))
    {
      out.write (42);
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("IOEx", ex);
    }

    s_aLogger.info (sName + ".exists()=" + f.exists ());
    s_aLogger.info (sName + ".getAbsolutePath()=" + f.getAbsolutePath ());
    s_aLogger.info (sName + ".canRead()=" + f.canRead ());
    s_aLogger.info (sName + ".canWrite()=" + f.canWrite ());
    s_aLogger.info ("Reading from " + fileName + " ...");
    FileInputStream in = null;
    try
    {
      in = new FileInputStream (f);
      s_aLogger.info (Character.toString ((char) in.read ()));
    }
    catch (final FileNotFoundException ex)
    {
      s_aLogger.error (fileName + " is not found");
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("IOEx", ex);
    }
    finally
    {
      StreamHelper.close (in);
    }
  }

  public static void main (final String [] args)
  {
    // no problem
    _testName ("PRN1");
    // printer
    _testName ("PRN");
    // null device
    _testName ("nul");
    // 1st parallel port
    _testName ("LPT1");
    // 2nd parallel port
    _testName ("LPT2");
    // console
    _testName ("CoN");
    // serial port
    _testName ("aUX");
  }
}
