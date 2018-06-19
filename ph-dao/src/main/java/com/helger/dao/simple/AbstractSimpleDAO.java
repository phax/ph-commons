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
package com.helger.dao.simple;

import java.io.File;
import java.io.OutputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.PDTToString;
import com.helger.commons.functional.ISupplier;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileIOError;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.relative.IFileRelativeIO;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.timing.StopWatch;
import com.helger.dao.AbstractDAO;
import com.helger.dao.DAOException;
import com.helger.xml.microdom.IMicroComment;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroComment;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class for a simple DAO.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractSimpleDAO extends AbstractDAO
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSimpleDAO.class);

  private final IMutableStatisticsHandlerCounter m_aStatsCounterInitTotal = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                 "$init-total");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterInitSuccess = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                   "$init-success");
  private final IMutableStatisticsHandlerTimer m_aStatsCounterInitTimer = StatisticsManager.getTimerHandler (getClass ().getName () +
                                                                                                             "$init");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterReadTotal = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                 "$read-total");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterReadSuccess = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                   "$read-success");
  private final IMutableStatisticsHandlerTimer m_aStatsCounterReadTimer = StatisticsManager.getTimerHandler (getClass ().getName () +
                                                                                                             "$read");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterWriteTotal = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                  "$write-total");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterWriteSuccess = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                    "$write-success");
  private final IMutableStatisticsHandlerCounter m_aStatsCounterWriteExceptions = StatisticsManager.getCounterHandler (getClass ().getName () +
                                                                                                                       "$write-exceptions");
  private final IMutableStatisticsHandlerTimer m_aStatsCounterWriteTimer = StatisticsManager.getTimerHandler (getClass ().getName () +
                                                                                                              "$write");

  private final IFileRelativeIO m_aIO;
  private final ISupplier <String> m_aFilenameProvider;
  private String m_sPreviousFilename;
  private int m_nInitCount = 0;
  private LocalDateTime m_aLastInitDT;
  private int m_nReadCount = 0;
  private LocalDateTime m_aLastReadDT;
  private int m_nWriteCount = 0;
  private LocalDateTime m_aLastWriteDT;

  protected AbstractSimpleDAO (@Nonnull final IFileRelativeIO aIO, @Nonnull final ISupplier <String> aFilenameProvider)
  {
    m_aIO = ValueEnforcer.notNull (aIO, "IO");
    m_aFilenameProvider = ValueEnforcer.notNull (aFilenameProvider, "FilenameProvider");
  }

  /**
   * @return The file-relative IO as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  protected final IFileRelativeIO getIO ()
  {
    return m_aIO;
  }

  /**
   * @return The filename provider used internally to build filenames. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final ISupplier <String> getFilenameProvider ()
  {
    return m_aFilenameProvider;
  }

  /**
   * Custom initialization routine. Called only if the underlying file does not
   * exist yet. This method is only called within a write lock!
   *
   * @return {@link EChange#CHANGED} if something was modified inside this
   *         method
   */
  @Nonnull
  @OverrideOnDemand
  protected EChange onInit ()
  {
    return EChange.UNCHANGED;
  }

  /**
   * Fill the internal structures with from the passed XML document. This method
   * is only called within a write lock!
   *
   * @param aDoc
   *        The XML document to read from. Never <code>null</code>.
   * @return {@link EChange#CHANGED} if reading the data changed something in
   *         the internal structures that requires a writing.
   */
  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  protected abstract EChange onRead (@Nonnull IMicroDocument aDoc);

  @Nonnull
  protected final File getSafeFile (@Nonnull final String sFilename, @Nonnull final EMode eMode) throws DAOException
  {
    ValueEnforcer.notNull (sFilename, "Filename");
    ValueEnforcer.notNull (eMode, "Mode");

    final File aFile = m_aIO.getFile (sFilename);
    if (aFile.exists ())
    {
      // file exist -> must be a file!
      if (!aFile.isFile ())
        throw new DAOException ("The passed filename '" +
                                sFilename +
                                "' is not a file - maybe a directory? Path is '" +
                                aFile.getAbsolutePath () +
                                "'");

      switch (eMode)
      {
        case READ:
          // Check for read-rights
          if (!aFile.canRead ())
            throw new DAOException ("The DAO of class " +
                                    getClass ().getName () +
                                    " has no access rights to read from '" +
                                    aFile.getAbsolutePath () +
                                    "'");
          break;
        case WRITE:
          // Check for write-rights
          if (!aFile.canWrite ())
            throw new DAOException ("The DAO of class " +
                                    getClass ().getName () +
                                    " has no access rights to write to '" +
                                    aFile.getAbsolutePath () +
                                    "'");
          break;
      }
    }
    else
    {
      // Ensure the parent directory is present
      final File aParentDir = aFile.getParentFile ();
      if (aParentDir != null)
      {
        final FileIOError aError = FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aParentDir);
        if (aError.isFailure ())
          throw new DAOException ("The DAO of class " +
                                  getClass ().getName () +
                                  " failed to create parent directory '" +
                                  aParentDir +
                                  "': " +
                                  aError);
      }
    }

    return aFile;
  }

  /**
   * Trigger the registered custom exception handlers for read errors.
   *
   * @param t
   *        Thrown exception. Never <code>null</code>.
   * @param bIsInitialization
   *        <code>true</code> if this happened during initialization of a new
   *        file, <code>false</code> if it happened during regular reading.
   * @param aFile
   *        The file that was read. May be <code>null</code> for in-memory DAOs.
   */
  protected static void triggerExceptionHandlersRead (@Nonnull final Throwable t,
                                                      final boolean bIsInitialization,
                                                      @Nullable final File aFile)
  {
    // Custom exception handler for reading
    if (exceptionHandlersRead ().isNotEmpty ())
    {
      final IReadableResource aRes = aFile == null ? null : new FileSystemResource (aFile);
      exceptionHandlersRead ().forEach (aCB -> aCB.onDAOReadException (t, bIsInitialization, aRes));
    }
  }

  /**
   * Call this method inside the constructor to read the file contents directly.
   * This method is write locked!
   *
   * @throws DAOException
   *         in case initialization or reading failed!
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void initialRead () throws DAOException
  {
    File aFile = null;
    final String sFilename = m_aFilenameProvider.get ();
    if (sFilename == null)
    {
      // required for testing
      s_aLogger.warn ("This DAO of class " + getClass ().getName () + " will not be able to read from a file");

      // do not return - run initialization anyway
    }
    else
    {
      // Check consistency
      aFile = getSafeFile (sFilename, EMode.READ);
    }

    final File aFinalFile = aFile;
    m_aRWLock.writeLockedThrowing ( () -> {
      final boolean bIsInitialization = aFinalFile == null || !aFinalFile.exists ();
      try
      {
        ESuccess eWriteSuccess = ESuccess.SUCCESS;
        if (bIsInitialization)
        {
          // initial setup for non-existing file
          if (isDebugLogging ())
            s_aLogger.info ("Trying to initialize DAO XML file '" + aFinalFile + "'");

          beginWithoutAutoSave ();
          try
          {
            m_aStatsCounterInitTotal.increment ();
            final StopWatch aSW = StopWatch.createdStarted ();

            if (onInit ().isChanged ())
              if (aFinalFile != null)
                eWriteSuccess = _writeToFile ();

            m_aStatsCounterInitTimer.addTime (aSW.stopAndGetMillis ());
            m_aStatsCounterInitSuccess.increment ();
            m_nInitCount++;
            m_aLastInitDT = PDTFactory.getCurrentLocalDateTime ();
          }
          finally
          {
            endWithoutAutoSave ();
            // reset any pending changes, because the initialization should
            // be read-only. If the implementing class changed something,
            // the return value of onInit() is what counts
            internalSetPendingChanges (false);
          }
        }
        else
        {
          // Read existing file
          if (isDebugLogging ())
            s_aLogger.info ("Trying to read DAO XML file '" + aFinalFile + "'");

          m_aStatsCounterReadTotal.increment ();
          final IMicroDocument aDoc = MicroReader.readMicroXML (aFinalFile);
          if (aDoc == null)
            s_aLogger.error ("Failed to read XML document from file '" + aFinalFile + "'");
          else
          {
            // Valid XML - start interpreting
            beginWithoutAutoSave ();
            try
            {
              final StopWatch aSW = StopWatch.createdStarted ();

              if (onRead (aDoc).isChanged ())
                eWriteSuccess = _writeToFile ();

              m_aStatsCounterReadTimer.addTime (aSW.stopAndGetMillis ());
              m_aStatsCounterReadSuccess.increment ();
              m_nReadCount++;
              m_aLastReadDT = PDTFactory.getCurrentLocalDateTime ();
            }
            finally
            {
              endWithoutAutoSave ();
              // reset any pending changes, because the initialization should
              // be read-only. If the implementing class changed something,
              // the return value of onRead() is what counts
              internalSetPendingChanges (false);
            }
          }
        }

        // Check if writing was successful on any of the 2 branches
        if (eWriteSuccess.isSuccess ())
          internalSetPendingChanges (false);
        else
          s_aLogger.warn ("File '" + aFinalFile + "' has pending changes after initialRead!");
      }
      catch (final Exception ex)
      {
        triggerExceptionHandlersRead (ex, bIsInitialization, aFinalFile);
        throw new DAOException ("Error " +
                                (bIsInitialization ? "initializing" : "reading") +
                                " the file '" +
                                aFinalFile +
                                "'",
                                ex);
      }
    });
  }

  /**
   * Called after a successful write of the file, if the filename is different
   * from the previous filename. This can e.g. be used to clear old data.
   *
   * @param sPreviousFilename
   *        The previous filename. May be <code>null</code>.
   * @param sNewFilename
   *        The new filename. Never <code>null</code>.
   */
  @OverrideOnDemand
  @MustBeLocked (ELockType.WRITE)
  protected void onFilenameChange (@Nullable final String sPreviousFilename, @Nonnull final String sNewFilename)
  {}

  /**
   * Create the XML document that should be saved to the file. This method is
   * only called within a write lock!
   *
   * @return The non-<code>null</code> document to write to the file.
   */
  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  protected abstract IMicroDocument createWriteData ();

  /**
   * Modify the created document by e.g. adding some comment or digital
   * signature or whatsoever.
   *
   * @param aDoc
   *        The created non-<code>null</code> document.
   */
  @OverrideOnDemand
  @MustBeLocked (ELockType.WRITE)
  protected void modifyWriteData (@Nonnull final IMicroDocument aDoc)
  {
    final IMicroComment aComment = new MicroComment ("This file was generated automatically - do NOT modify!\n" +
                                                     "Written at " +
                                                     PDTToString.getAsString (ZonedDateTime.now (Clock.systemUTC ()),
                                                                              Locale.US));
    final IMicroElement eRoot = aDoc.getDocumentElement ();
    // Add a small comment
    if (eRoot != null)
      aDoc.insertBefore (aComment, eRoot);
    else
      aDoc.appendChild (aComment);
  }

  /**
   * Optional callback method that is invoked before the file handle gets
   * opened. This method can e.g. be used to create backups.
   *
   * @param sFilename
   *        The filename provided by the internal filename provider. Never
   *        <code>null</code>.
   * @param aFile
   *        The resolved file. It is already consistency checked. Never
   *        <code>null</code>.
   */
  @OverrideOnDemand
  @MustBeLocked (ELockType.WRITE)
  protected void beforeWriteToFile (@Nonnull final String sFilename, @Nonnull final File aFile)
  {}

  /**
   * @return The {@link IXMLWriterSettings} to be used to serialize the data.
   */
  @Nonnull
  @OverrideOnDemand
  protected IXMLWriterSettings getXMLWriterSettings ()
  {
    return XMLWriterSettings.DEFAULT_XML_SETTINGS;
  }

  /**
   * @return The filename to which was written last. May be <code>null</code> if
   *         no wrote action was performed yet.
   */
  @Nullable
  public final String getLastFilename ()
  {
    return m_aRWLock.readLocked ( () -> m_sPreviousFilename);
  }

  /**
   * Trigger the registered custom exception handlers for read errors.
   *
   * @param t
   *        Thrown exception. Never <code>null</code>.
   * @param sErrorFilename
   *        The filename tried to write to. Never <code>null</code>.
   * @param aDoc
   *        The XML content that should be written. May be <code>null</code> if
   *        the error occurred in XML creation.
   */
  protected static void triggerExceptionHandlersWrite (@Nonnull final Throwable t,
                                                       @Nonnull final String sErrorFilename,
                                                       @Nullable final IMicroDocument aDoc)
  {
    // Check if a custom exception handler is present
    if (exceptionHandlersWrite ().isNotEmpty ())
    {
      final IReadableResource aRes = new FileSystemResource (sErrorFilename);
      final String sXMLContent = aDoc == null ? "no XML document created" : MicroWriter.getNodeAsString (aDoc);
      exceptionHandlersWrite ().forEach (aCB -> aCB.onDAOWriteException (t, aRes, sXMLContent));
    }
  }

  /**
   * The main method for writing the new data to a file. This method may only be
   * called within a write lock!
   *
   * @return {@link ESuccess} and never <code>null</code>.
   */
  @Nonnull
  @SuppressFBWarnings ("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  @MustBeLocked (ELockType.WRITE)
  private ESuccess _writeToFile ()
  {
    // Build the filename to write to
    final String sFilename = m_aFilenameProvider.get ();
    if (sFilename == null)
    {
      // We're not operating on a file! Required for testing
      s_aLogger.warn ("The DAO of class " + getClass ().getName () + " cannot write to a file");
      return ESuccess.FAILURE;
    }

    // Check for a filename change before writing
    if (!sFilename.equals (m_sPreviousFilename))
    {
      onFilenameChange (m_sPreviousFilename, sFilename);
      m_sPreviousFilename = sFilename;
    }

    if (isDebugLogging ())
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Trying to write DAO file '" + sFilename + "'");

    File aFile = null;
    IMicroDocument aDoc = null;
    try
    {
      // Get the file handle
      aFile = getSafeFile (sFilename, EMode.WRITE);

      m_aStatsCounterWriteTotal.increment ();
      final StopWatch aSW = StopWatch.createdStarted ();

      // Create XML document to write
      aDoc = createWriteData ();
      if (aDoc == null)
        throw new DAOException ("Failed to create data to write to file");

      // Generic modification
      modifyWriteData (aDoc);

      // Perform optional stuff like backup etc. Must be done BEFORE the output
      // stream is opened!
      beforeWriteToFile (sFilename, aFile);

      // Get the output stream
      final OutputStream aOS = FileHelper.getOutputStream (aFile);
      if (aOS == null)
      {
        // Happens, when another application has the file open!
        // Logger warning already emitted
        throw new DAOException ("Failed to open output stream");
      }

      // Write to file (closes the OS)
      final IXMLWriterSettings aXWS = getXMLWriterSettings ();
      if (MicroWriter.writeToStream (aDoc, aOS, aXWS).isFailure ())
        throw new DAOException ("Failed to write DAO XML data to file");

      m_aStatsCounterWriteTimer.addTime (aSW.stopAndGetMillis ());
      m_aStatsCounterWriteSuccess.increment ();
      m_nWriteCount++;
      m_aLastWriteDT = PDTFactory.getCurrentLocalDateTime ();
      return ESuccess.SUCCESS;
    }
    catch (final Exception ex)
    {
      final String sErrorFilename = aFile != null ? aFile.getAbsolutePath () : sFilename;

      s_aLogger.error ("The DAO of class " +
                       getClass ().getName () +
                       " failed to write the DAO data to '" +
                       sErrorFilename +
                       "'",
                       ex);

      triggerExceptionHandlersWrite (ex, sErrorFilename, aDoc);
      m_aStatsCounterWriteExceptions.increment ();
      return ESuccess.FAILURE;
    }
  }

  /**
   * This method must be called every time something changed in the DAO. It
   * triggers the writing to a file if auto-save is active. This method must be
   * called within a write-lock as it is not locked!
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void markAsChanged ()
  {
    // Just remember that something changed
    internalSetPendingChanges (true);
    if (internalIsAutoSaveEnabled ())
    {
      // Auto save
      if (_writeToFile ().isSuccess ())
        internalSetPendingChanges (false);
      else
      {
        s_aLogger.error ("The DAO of class " +
                         getClass ().getName () +
                         " still has pending changes after markAsChanged!");
      }
    }
  }

  /**
   * In case there are pending changes write them to the file. This method is
   * write locked!
   */
  public final void writeToFileOnPendingChanges ()
  {
    if (hasPendingChanges ())
    {
      m_aRWLock.writeLocked ( () -> {
        // Write to file
        if (_writeToFile ().isSuccess ())
          internalSetPendingChanges (false);
        else
          s_aLogger.error ("The DAO of class " +
                           getClass ().getName () +
                           " still has pending changes after writeToFileOnPendingChanges!");
      });
    }
  }

  @Nonnegative
  public int getInitCount ()
  {
    return m_nInitCount;
  }

  @Nullable
  public final LocalDateTime getLastInitDateTime ()
  {
    return m_aLastInitDT;
  }

  @Nonnegative
  public int getReadCount ()
  {
    return m_nReadCount;
  }

  @Nullable
  public final LocalDateTime getLastReadDateTime ()
  {
    return m_aLastReadDT;
  }

  @Nonnegative
  public int getWriteCount ()
  {
    return m_nWriteCount;
  }

  @Nullable
  public final LocalDateTime getLastWriteDateTime ()
  {
    return m_aLastWriteDT;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("filenameProvider", m_aFilenameProvider)
                            .append ("previousFilename", m_sPreviousFilename)
                            .append ("initCount", m_nInitCount)
                            .appendIfNotNull ("lastInitDT", m_aLastInitDT)
                            .append ("readCount", m_nReadCount)
                            .appendIfNotNull ("lastReadDT", m_aLastReadDT)
                            .append ("writeCount", m_nWriteCount)
                            .appendIfNotNull ("lastWriteDT", m_aLastWriteDT)
                            .getToString ();
  }
}
