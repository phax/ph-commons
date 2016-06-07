package com.helger.commons.mock;

import java.io.File;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.iterate.FileSystemIterator;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;

public final class SPITestHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SPITestHelper.class);

  private SPITestHelper ()
  {}

  @Nonnegative
  public static int testIfAllSPIImplementationsAreValid (@Nonnull final String sBaseDir,
                                                         final boolean bContinueOnError) throws Exception
  {
    int nTotalImplementationCount = 0;
    final File aBaseDir = new File (sBaseDir);
    if (aBaseDir.exists () && aBaseDir.isDirectory ())
      for (final File aFile : new FileSystemIterator (sBaseDir))
        if (aFile.isFile ())
        {
          s_aLogger.info ("Checking SPI file " + aFile.getAbsolutePath ());

          // Check if interface exists
          try
          {
            Class.forName (aFile.getName ());
          }
          catch (final Throwable t)
          {
            final String sMsg = "No interface representing " +
                                aFile.getName () +
                                " exists: " +
                                ClassHelper.getClassLocalName (t) +
                                " - " +
                                t.getMessage ();
            s_aLogger.warn (sMsg);
            if (!bContinueOnError)
              throw new Exception (sMsg);
          }

          // Check content
          try (
              final NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (StreamHelper.createReader (FileHelper.getInputStream (aFile),
                                                                                                                  CCharset.CHARSET_SERVICE_LOADER_OBJ)))
          {
            int nCount = 0;
            String sLine;
            while ((sLine = aReader.readLine ()) != null)
            {
              final String sClassName = StringHelper.trim (sLine);
              if (StringHelper.hasText (sClassName))
              {
                // Resolve class
                Class.forName (sLine);
                ++nCount;
                ++nTotalImplementationCount;
              }
            }
            if (nCount == 0)
              s_aLogger.warn ("  Contains no single valid implementation!");
            else
              s_aLogger.info ("  All implementations (" + nCount + ") are valid!");
          }
          catch (final Throwable t)
          {
            // Ensure the path name of the currently checked file is contained
            // in the exception text!
            s_aLogger.warn ("  Error checking content: " + t.getMessage ());
            if (!bContinueOnError)
              throw new Exception ("Error checking SPI file " + aFile.getAbsolutePath (), t);
          }
        }
    return nTotalImplementationCount;
  }

  @Nonnegative
  public static int testIfAllSPIImplementationsAreValid (final boolean bContinueOnError) throws Exception
  {
    int ret = 0;
    ret += testIfAllSPIImplementationsAreValid ("src/main/resources/META-INF/services", bContinueOnError);
    ret += testIfAllSPIImplementationsAreValid ("src/test/resources/META-INF/services", bContinueOnError);
    return ret;
  }

  @Nonnegative
  public static int testIfAllSPIImplementationsAreValid () throws Exception
  {
    return testIfAllSPIImplementationsAreValid (false);
  }
}
