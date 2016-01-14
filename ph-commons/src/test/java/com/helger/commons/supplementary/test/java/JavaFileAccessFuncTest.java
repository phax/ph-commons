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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.io.file.LoggingFileOperationCallback;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.system.EOperatingSystem;
import com.helger.commons.system.SystemProperties;

public final class JavaFileAccessFuncTest
{
  private static final class ProcListener extends Thread
  {
    private final String m_sCmd;
    private final InputStream m_aIS;

    private ProcListener (final String sCmd, final InputStream aIS)
    {
      super ("ProcessListener " + sCmd);
      m_sCmd = sCmd;
      m_aIS = aIS;
    }

    @Override
    public void run ()
    {
      try
      {
        final InputStreamReader aISR = new InputStreamReader (m_aIS);
        final BufferedReader aBR = new BufferedReader (aISR);
        String line;
        while ((line = aBR.readLine ()) != null)
          _println (m_sCmd + "> " + line);
      }
      catch (final Exception ex)
      {
        ex.printStackTrace ();
      }
    }
  }

  @SuppressWarnings ("unused")
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaFileAccessFuncTest.class);

  private static synchronized void _println (final String s)
  {
    System.out.println (s);
  }

  private static void _exec (@Nonnull final String... aCmdArray) throws IOException, InterruptedException
  {
    final String sCmd = aCmdArray[0];
    _println (Arrays.toString (aCmdArray));
    final Process p = Runtime.getRuntime ().exec (aCmdArray);
    final Thread tIn = new ProcListener ("out " + sCmd, p.getInputStream ());
    final Thread tErr = new ProcListener ("err " + sCmd, p.getErrorStream ());
    tIn.start ();
    tErr.start ();
    final int nResult = p.waitFor ();
    tIn.interrupt ();
    tErr.interrupt ();
    if (nResult != 0)
      _println ("  Error code = " + nResult);
  }

  @Test
  public void testAccessRights () throws IOException, InterruptedException
  {
    if (EOperatingSystem.getCurrentOS ().isUnixBased ())
    {
      final FileOperationManager aFOM = new FileOperationManager (new LoggingFileOperationCallback ());
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
                      final String sMod = Integer.toString (nModeOwn) +
                                          Integer.toString (nModeGroup) +
                                          Integer.toString (nModeOther);
                      final String sPrefix = "ph-commons-";

                      final File fFile = new File (fTempDir, sPrefix + sMod + ".dat");
                      if (SimpleFileIO.writeFile (fFile,
                                                  CharsetManager.getAsBytes ("content",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ))
                                      .isSuccess ())
                        _exec ("chmod", sMod, fFile.getAbsolutePath ());

                      final File fDir = new File (fTempDir, sPrefix + sMod + ".dir");
                      if (aFOM.createDir (fDir).isSuccess ())
                        _exec ("chmod", sMod, fDir.getAbsolutePath ());
                    }
      }

      _exec ("chmod", "-v", "711", fTempDir.getAbsolutePath ());

      // Test there readability
      final List <File> aFiles = FileHelper.getDirectoryContent (fTempDir);
      for (final File f : CollectionHelper.getSorted (aFiles))
      {
        final boolean bCanRead = FileHelper.canRead (f);
        final boolean bCanWrite = FileHelper.canWrite (f);
        final boolean bCanExec = FileHelper.canRead (f);
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
  }
}
