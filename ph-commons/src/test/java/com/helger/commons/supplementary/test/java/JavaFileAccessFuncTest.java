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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.charset.CharsetHelper;
import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.system.EOperatingSystem;
import com.helger.base.system.SystemProperties;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileOperationManager;
import com.helger.io.file.FileOperations;
import com.helger.io.file.LoggingFileOperationCallback;
import com.helger.io.file.SimpleFileIO;

public final class JavaFileAccessFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaFileAccessFuncTest.class);

  private static synchronized void _println (final String s)
  {
    LOGGER.info (s);
  }

  private static final class ProcessOutputListener extends Thread
  {
    private final String m_sCmd;
    private final InputStream m_aIS;
    private final Charset m_aCharset;

    private ProcessOutputListener (@NonNull final String sWhat,
                                   @NonNull final String sCmd,
                                   @NonNull final InputStream aIS,
                                   @NonNull final Charset aCharset)
    {
      super ("POL " + sWhat + " " + sCmd);
      m_sCmd = sCmd;
      m_aIS = aIS;
      m_aCharset = aCharset;
    }

    @SuppressWarnings ("resource")
    @Override
    public void run ()
    {
      // Don't close them - will lead to truncated output!
      final InputStreamReader aISR = new InputStreamReader (m_aIS, m_aCharset);
      final NonBlockingBufferedReader aBR = new NonBlockingBufferedReader (aISR);
      try
      {
        String sLine;
        while ((sLine = aBR.readLine ()) != null && !Thread.currentThread ().isInterrupted ())
          _println (m_sCmd + "> " + sLine);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Internal error", ex);
      }
    }
  }

  private static void _exec (@NonNull @Nonempty final String... aCmdArray) throws IOException, InterruptedException
  {
    final Charset c = CharsetHelper.getCharsetFromName ("cp1252");

    final String sCmd = aCmdArray[0];
    _println (Arrays.toString (aCmdArray));
    final ProcessBuilder aPB = new ProcessBuilder (aCmdArray);
    final Process p = aPB.start ();
    try (final InputStream aOutIS = p.getInputStream (); final InputStream aErrIS = p.getErrorStream ())
    {
      final Thread tIn = new ProcessOutputListener ("[stdout]", sCmd, aOutIS, c);
      final Thread tErr = new ProcessOutputListener ("[stderr]", sCmd, aErrIS, c);
      tIn.start ();
      tErr.start ();
      final int nResult = p.waitFor ();
      tIn.interrupt ();
      tErr.interrupt ();
      if (nResult != 0)
        _println ("  Error code = " + nResult);
      // Wait until done
      tIn.join (1000);
      tErr.join (1000);
    }
  }

  @Test
  public void testAccessRights () throws IOException, InterruptedException
  {
    if (EOperatingSystem.getCurrentOS ().isUnixBased ())
    {
      final FileOperationManager aFOM = new FileOperationManager ();
      aFOM.callbacks ().add (new LoggingFileOperationCallback ());
      final File fTempDir = new File (SystemProperties.getTmpDir (), "helger");
      FileOperations.createDirIfNotExisting (fTempDir);
      assertTrue (fTempDir.getAbsolutePath (), fTempDir.exists ());

      // Create the files for testing
      {
        final boolean [] aTrueFalse = new boolean [] { true, false };
        for (final boolean bReadOwner : aTrueFalse)
          for (final boolean bWriteOwner : aTrueFalse)
            for (final boolean bReadGroup : aTrueFalse)
              for (final boolean bWriteGroup : aTrueFalse)
                for (final boolean bReadOther : aTrueFalse)
                  for (final boolean bWriteOther : aTrueFalse)
                    for (final boolean bExec : aTrueFalse)
                    {
                      final int nModeOwn = (bReadOwner ? 4 : 0) | (bWriteOwner ? 2 : 0) | (bExec ? 1 : 0);
                      final int nModeGroup = (bReadGroup ? 4 : 0) | (bWriteGroup ? 2 : 0) | (bExec ? 1 : 0);
                      final int nModeOther = (bReadOther ? 4 : 0) | (bWriteOther ? 2 : 0) | (bExec ? 1 : 0);
                      final String sMod = Integer.toString (nModeOwn) + Integer.toString (nModeGroup) + Integer.toString (nModeOther);
                      final String sPrefix = "ph-commons-";

                      final File fFile = new File (fTempDir, sPrefix + sMod + ".dat");
                      if (SimpleFileIO.writeFile (fFile, "content".getBytes (StandardCharsets.ISO_8859_1)).isSuccess ())
                        _exec ("chmod", sMod, fFile.getAbsolutePath ());

                      final File fDir = new File (fTempDir, sPrefix + sMod + ".dir");
                      if (aFOM.createDir (fDir).isSuccess ())
                        _exec ("chmod", sMod, fDir.getAbsolutePath ());
                    }
      }

      _exec ("chmod", "-v", "711", fTempDir.getAbsolutePath ());

      // Test there readability
      final ICommonsList <File> aFiles = FileHelper.getDirectoryContent (fTempDir);
      for (final File f : aFiles.getSorted (Comparator.naturalOrder ()))
      {
        final boolean bCanRead = f.canRead ();
        final boolean bCanWrite = f.canWrite ();
        final boolean bCanExec = f.canRead ();
        final String sRights = bCanRead + "/" + bCanWrite + "/" + bCanExec;
        final File f2 = new File (f.getParentFile (), f.getName () + "2");

        if (f.isFile ())
        {
          _println ("file:" + f.getName () + "    " + sRights);
          if (aFOM.renameFile (f, f2).isSuccess ())
            aFOM.deleteFile (f2);
          else
            aFOM.deleteFile (f);
        }
        else
          if (f.isDirectory ())
          {
            _println ("dir: " + f.getName () + "    " + sRights);
            if (aFOM.renameDir (f, f2).isSuccess ())
              aFOM.deleteDir (f2);
            else
              aFOM.deleteDir (f);
          }
          else
          {
            _println ("Neither file not directory: " + f.getName () + "    " + sRights);
          }
      }

      if (false)
        _exec ("chmod", "--preserve-root", "-v", "-R", "777", fTempDir.getAbsolutePath ());

      aFOM.deleteDirRecursive (fTempDir);
      if (fTempDir.exists ())
        _exec ("rm", "-rf", fTempDir.getAbsolutePath ());

      _println ("done");
    }
    else
      if (EOperatingSystem.getCurrentOS ().isWindowsBased ())
      {
        _exec ("cmd", "/c", "dir");
      }
  }
}
