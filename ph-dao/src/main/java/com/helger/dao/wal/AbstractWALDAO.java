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
package com.helger.dao.wal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.ELockType;
import com.helger.annotation.concurrent.IsLocked;
import com.helger.annotation.concurrent.MustBeLocked;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.PDTToString;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.EFileIOErrorCode;
import com.helger.commons.io.file.EFileIOOperation;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileIOError;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.relative.IFileRelativeIO;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.timing.StopWatch;
import com.helger.dao.AbstractDAO;
import com.helger.dao.DAOException;
import com.helger.dao.EDAOActionType;
import com.helger.xml.microdom.IMicroComment;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroComment;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Base class for a simple DAO using write ahead logging (WAL).
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be serialized
 */
@ThreadSafe
public abstract class AbstractWALDAO <DATATYPE> extends AbstractDAO
{
  public static final Duration DEFAULT_WAITING_TIME = Duration.ofSeconds (10);
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractWALDAO.class);

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
  // Performance and small version
  public static final IXMLWriterSettings WRITE_XWS = new XMLWriterSettings ().setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG);
  public static final IXMLWriterSettings WAL_XWS = new XMLWriterSettings ().setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG)
                                                                           .setIndent (EXMLSerializeIndent.NONE);

  private final Class <DATATYPE> m_aDataTypeClass;
  private final IFileRelativeIO m_aIO;
  private final Supplier <String> m_aFilenameProvider;
  private String m_sPreviousFilename;
  private int m_nInitCount = 0;
  private LocalDateTime m_aLastInitDT;
  private int m_nReadCount = 0;
  private LocalDateTime m_aLastReadDT;
  private int m_nWriteCount = 0;
  private LocalDateTime m_aLastWriteDT;
  private boolean m_bCanWriteWAL = true;
  private Duration m_aWaitingTime = DEFAULT_WAITING_TIME;

  // Status vars
  private final WALListener m_aWALListener;

  @Nonnull
  private static String _getFilenameNew (@Nonnull final String sFilename)
  {
    return sFilename + FILENAME_EXTENSION_NEW;
  }

  @Nonnull
  private static String _getFilenamePrev (@Nonnull final String sFilename)
  {
    return sFilename + FILENAME_EXTENSION_PREV;
  }

  protected AbstractWALDAO (@Nonnull final Class <DATATYPE> aDataTypeClass,
                            @Nonnull final IFileRelativeIO aIO,
                            @Nonnull final Supplier <String> aFilenameProvider)
  {
    m_aDataTypeClass = ValueEnforcer.notNull (aDataTypeClass, "DataTypeClass");
    m_aIO = ValueEnforcer.notNull (aIO, "DAOIO");
    m_aFilenameProvider = ValueEnforcer.notNull (aFilenameProvider, "FilenameProvider");

    // Remember instance in case it is trigger upon shutdown
    m_aWALListener = WALListener.getInstance ();

    // Consistency checks
    final String sFilename = m_aFilenameProvider.get ();
    if (sFilename != null)
    {
      try
      {
        // A ".new" file already exists - previous changes could not be
        // performed.
        final File aFileNew = getSafeFile (_getFilenameNew (sFilename), EMode.WRITE);
        if (aFileNew.exists ())
        {
          final String sMsg = "The temporary WAL file '" +
                              aFileNew.getAbsolutePath () +
                              "' already exists! If the '" +
                              FILENAME_EXTENSION_NEW +
                              "' file is complete, rename it to match '" +
                              sFilename +
                              "'. Please resolve this conflict manually.";
          LOGGER.error (sMsg);
          throw new IllegalStateException (sMsg);
        }
      }
      catch (final DAOException ex)
      {
        // Ignore
      }

      try
      {
        // A ".prev" file still exist, meaning the deletion failed. Most likely
        // this happens together with the ".new" error above
        final File aFilePrev = getSafeFile (_getFilenamePrev (sFilename), EMode.WRITE);
        if (aFilePrev.exists ())
        {
          final String sMsg = "The temporary WAL file '" +
                              aFilePrev.getAbsolutePath () +
                              "' already exists! If the target filename '" +
                              sFilename +
                              "' exists and is complete, you may consider deleting this '" +
                              FILENAME_EXTENSION_PREV +
                              "' file. Please resolve this conflict manually.";
          LOGGER.error (sMsg);
          throw new IllegalStateException (sMsg);
        }
      }
      catch (final DAOException ex)
      {
        // Ignore
      }
    }
  }

  final void internalWriteLocked (@Nonnull final Runnable aRunnable)
  {
    m_aRWLock.writeLocked (aRunnable);
  }

  /**
   * @return The file-relative IO as passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  protected final IFileRelativeIO getIO ()
  {
    return m_aIO;
  }

  /**
   * @return The filename provider used internally to build filenames. Never <code>null</code>.
   */
  @Nonnull
  public final Supplier <String> getFilenameProvider ()
  {
    return m_aFilenameProvider;
  }

  /**
   * Custom initialization routine. Called only if the underlying file does not exist yet. This
   * method is only called within a write lock!
   *
   * @return {@link EChange#CHANGED} if something was modified inside this method
   */
  @Nonnull
  @OverrideOnDemand
  protected EChange onInit ()
  {
    return EChange.UNCHANGED;
  }

  /**
   * Fill the internal structures with from the passed XML document. This method is only called
   * within a write lock!
   *
   * @param aDoc
   *        The XML document to read from. Never <code>null</code>.
   * @return {@link EChange#CHANGED} if reading the data changed something in the internal
   *         structures that requires a writing.
   */
  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  protected abstract EChange onRead (@Nonnull IMicroDocument aDoc);

  /**
   * Get a {@link File} object for the specified filename that can perform the respective operation
   * indicated by the "mode" parameter.
   *
   * @param sFilename
   *        Filename to use. May not be <code>null</code>.
   * @param eMode
   *        The access mode to be used. May not be <code>null</code>.
   * @return The created and checked {@link File} if everything is fine, otherwise an exception is
   *         thrown.
   * @throws DAOException
   *         If the requested access mode cannot be provided.
   */
  @Nonnull
  protected final File getSafeFile (@Nonnull final String sFilename, @Nonnull final EMode eMode) throws DAOException
  {
    ValueEnforcer.notNull (sFilename, "Filename");
    ValueEnforcer.notNull (eMode, "Mode");

    final File aFile = m_aIO.getFile (sFilename);
    checkFileAccess (aFile, eMode);
    return aFile;
  }

  /**
   * Trigger the registered custom exception handlers for read errors.
   *
   * @param t
   *        Thrown exception. Never <code>null</code>.
   * @param bIsInitialization
   *        <code>true</code> if this happened during initialization of a new file,
   *        <code>false</code> if it happened during regular reading.
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
   * @return The implementation class as specified in the constructor. Never <code>null</code>.
   */
  @Nonnull
  protected final Class <DATATYPE> getDataTypeClass ()
  {
    return m_aDataTypeClass;
  }

  /**
   * This method is used upon recovery to convert a stored object to its native representation. If
   * you overwrite this method, you should consider overriding
   * {@link #convertNativeToWALString(Object)} as well.
   *
   * @param sElement
   *        The string representation to be converted. Never <code>null</code>.
   * @return The native representation of the object. If the return value is <code>null</code>, the
   *         recovery will fail with an exception!
   */
  @Nullable
  @OverrideOnDemand
  @IsLocked (ELockType.WRITE)
  protected DATATYPE convertWALStringToNative (@Nonnull final String sElement)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (sElement);
    if (aDoc == null || aDoc.getDocumentElement () == null)
      return null;
    return MicroTypeConverter.convertToNative (aDoc.getDocumentElement (), m_aDataTypeClass);
  }

  /**
   * Called between initial read and WAL handling.
   *
   * @param aDoc
   *        The read document. Never <code>null</code>
   */
  @OverrideOnDemand
  @IsLocked (ELockType.WRITE)
  protected void onBetweenReadAndWAL (@Nonnull final IMicroDocument aDoc)
  {}

  /**
   * This method is called, when the conversion from the read XML string to the native type failed.
   * By default an error message is logged and processing continues.
   *
   * @param eActionType
   *        The action type to recover. May not be <code>null</code>
   * @param i
   *        The index of the element to recover. Always &ge; 0.
   * @param sElement
   *        The string read from the WAL file that could not be recovered.
   * @since 9.1.6
   */
  @OverrideOnDemand
  protected void onRecoveryErrorConvertToNative (@Nonnull final EDAOActionType eActionType,
                                                 @Nonnegative final int i,
                                                 @Nonnull final String sElement)
  {
    LOGGER.error ("Action [" +
                  eActionType +
                  "][" +
                  i +
                  "]: failed to convert the following element to native:\n" +
                  sElement);
  }

  /**
   * Called when a recovery is needed to create a new item.
   *
   * @param aElement
   *        The element to be created. Never <code>null</code>.
   */
  @IsLocked (ELockType.WRITE)
  protected abstract void onRecoveryCreate (@Nonnull DATATYPE aElement);

  /**
   * Called when a recovery is needed to update an existing item.
   *
   * @param aElement
   *        The element to be updated. Never <code>null</code>.
   */
  @IsLocked (ELockType.WRITE)
  protected abstract void onRecoveryUpdate (@Nonnull DATATYPE aElement);

  /**
   * Called when a recovery is needed to delete an existing item.
   *
   * @param aElement
   *        The element to be deleted. Never <code>null</code>.
   */
  @IsLocked (ELockType.WRITE)
  protected abstract void onRecoveryDelete (@Nonnull DATATYPE aElement);

  /**
   * Call this method inside the constructor to read the file contents directly. This method is
   * write locking internally. This method performs WAL file reading upon startup both after init as
   * well as after read!
   *
   * @throws DAOException
   *         in case initialization or reading failed!
   */
  protected final void initialRead () throws DAOException
  {
    final File aFile;
    final String sFilename = m_aFilenameProvider.get ();
    if (sFilename == null)
    {
      // required for testing
      CONDLOG.info ( () -> "This DAO of class " + getClass ().getName () + " will not be able to read from a file");

      // do not return - run initialization anyway
      aFile = null;
    }
    else
    {
      // Check consistency
      aFile = getSafeFile (sFilename, EMode.READ);

      CONDLOG.info ( () -> "This DAO of class " + getClass ().getName () + " is initially read from file ");
    }
    final boolean bIsInitialization = aFile == null || !aFile.exists ();

    m_aRWLock.writeLock ().lock ();
    try
    {
      m_bCanWriteWAL = false;

      IMicroDocument aDoc = null;
      try
      {
        ESuccess eWriteSuccess = ESuccess.SUCCESS;
        if (bIsInitialization)
        {
          // initial setup for non-existing file
          CONDLOG.info ( () -> "Trying to initialize WAL DAO" +
                               (aFile == null ? "" : " XML file '" + aFile.getAbsolutePath () + "'"));

          beginWithoutAutoSave ();
          try
          {
            m_aStatsCounterInitTotal.increment ();
            final StopWatch aSW = StopWatch.createdStarted ();

            if (onInit ().isChanged ())
              if (aFile != null)
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
          // Read existing file (aFile must be set)
          CONDLOG.info ( () -> "Trying to read WAL DAO XML file '" + aFile.getAbsolutePath () + "'");

          m_aStatsCounterReadTotal.increment ();
          aDoc = MicroReader.readMicroXML (aFile);
          if (aDoc == null)
          {
            LOGGER.error ("Failed to read DAO XML document from file '" + aFile.getAbsolutePath () + "'");
          }
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
        {
          // Reset any pending changes, since the changes were already saved
          internalSetPendingChanges (false);
        }
        else
        {
          // There is something wrong
          LOGGER.error ("File '" + aFile.getAbsolutePath () + "' has pending changes after initialRead!");
        }
      }
      catch (final Exception ex)
      {
        triggerExceptionHandlersRead (ex, bIsInitialization, aFile);
        throw new DAOException ("Error " +
                                (bIsInitialization ? "initializing" : "reading") +
                                (aFile == null ? "in-memory" : " the file '" + aFile.getAbsolutePath () + "'"),
                                ex);
      }
      // Trigger after read before WAL
      if (aDoc != null)
        onBetweenReadAndWAL (aDoc);

      // Check if there is any WAL file to recover
      final String sWALFilename = _getWALFilename ();
      final File aWALFile = sWALFilename == null ? null : m_aIO.getFile (sWALFilename);
      if (aWALFile != null && aWALFile.exists ())
      {
        CONDLOG.info ( () -> "Trying to recover from WAL file " + aWALFile.getAbsolutePath ());
        boolean bPerformedAtLeastOnRecovery = false;
        boolean bRecoveryContainedErrors = false;

        // Avoid writing the recovery actions to the WAL file again :)
        try (final DataInputStream aOIS = new DataInputStream (FileHelper.getInputStream (aWALFile)))
        {
          while (true)
          {
            // Read action type
            final String sActionTypeID;
            try
            {
              sActionTypeID = StreamHelper.readSafeUTF (aOIS);
            }
            catch (final EOFException ex)
            {
              // End of file
              break;
            }
            final EDAOActionType eActionType = EDAOActionType.getFromIDOrThrow (sActionTypeID);

            // Read number of elements
            final int nElements = aOIS.readInt ();
            CONDLOG.info ( () -> "Trying to recover " + nElements + " " + eActionType + " actions from WAL file");

            // Read all elements
            for (int i = 0; i < nElements; ++i)
            {
              final String sElement = StreamHelper.readSafeUTF (aOIS);
              final DATATYPE aElement = convertWALStringToNative (sElement);
              if (aElement == null)
              {
                // Cannot recover, because conversion fails
                bRecoveryContainedErrors = true;
                onRecoveryErrorConvertToNative (eActionType, i, sElement);
                continue;
              }
              final int nIdx = i;
              CONDLOG.info ( () -> "Trying to recover object [" + nIdx + "] with " + sElement.length () + " chars");

              switch (eActionType)
              {
                case CREATE:
                  try
                  {
                    onRecoveryCreate (aElement);
                    bPerformedAtLeastOnRecovery = true;
                    CONDLOG.info ( () -> "[WAL] wal-recovery create " + aElement);
                  }
                  catch (final RuntimeException ex)
                  {
                    LOGGER.error ("[WAL] wal-recovery create " +
                                  aElement +
                                  " - " +
                                  ex.getClass ().getName () +
                                  ": " +
                                  ex.getMessage ());
                    throw ex;
                  }
                  break;
                case UPDATE:
                  try
                  {
                    onRecoveryUpdate (aElement);
                    bPerformedAtLeastOnRecovery = true;
                    CONDLOG.info ( () -> "[WAL] wal-recovery update " + aElement);
                    break;
                  }
                  catch (final RuntimeException ex)
                  {
                    LOGGER.error ("[WAL] wal-recovery update " +
                                  aElement +
                                  " - " +
                                  ex.getClass ().getName () +
                                  ": " +
                                  ex.getMessage ());
                    throw ex;
                  }
                case DELETE:
                  try
                  {
                    onRecoveryDelete (aElement);
                    bPerformedAtLeastOnRecovery = true;
                    CONDLOG.info ( () -> "[WAL] wal-recovery delete " + aElement);
                    break;
                  }
                  catch (final RuntimeException ex)
                  {
                    LOGGER.error ("[WAL] wal-recovery delete " +
                                  aElement +
                                  " - " +
                                  ex.getClass ().getName () +
                                  ": " +
                                  ex.getMessage ());
                    throw ex;
                  }
                default:
                {
                  final String sMsg = "Unsupported action type provided: " + eActionType;
                  LOGGER.error (sMsg);
                  throw new IllegalStateException (sMsg);
                }
              }
            }
          }
          CONDLOG.info ( () -> "Successfully finished recovery from WAL file " + aWALFile.getAbsolutePath ());
        }
        catch (final IOException | RuntimeException ex)
        {
          LOGGER.error ("Failed to recover from WAL file '" +
                        aWALFile.getAbsolutePath () +
                        "'. Technical details: " +
                        ex.getClass ().getName () +
                        ": " +
                        ex.getMessage ());
          triggerExceptionHandlersRead (ex, false, aWALFile);
          throw new DAOException ("Error the WAL file '" + aWALFile.getAbsolutePath () + "'", ex);
        }
        // Finished recovery successfully
        // Perform the remaining actions AFTER the WAL input stream was
        // closed!
        if (bPerformedAtLeastOnRecovery)
        {
          // Write the file without using WAL
          _writeToFileAndResetPendingChanges ("onRecovery");
        }
        // Finally maintain or delete the WAL file, as the recovery has
        // finished
        if (bRecoveryContainedErrors)
          _maintainWALFileAfterProcessing (sWALFilename);
        else
          _deleteWALFileAfterProcessing (sWALFilename);
      }
    }
    finally
    {
      // Now a WAL file can be written again
      m_bCanWriteWAL = true;
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Called after a successful write of the file, if the filename is different from the previous
   * filename. This can e.g. be used to clear old data.
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
   * Create the XML document that should be saved to the file. This method is only called within a
   * write lock!
   *
   * @return The non-<code>null</code> document to write to the file.
   */
  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  protected abstract IMicroDocument createWriteData ();

  /**
   * Modify the created document by e.g. adding some comment or digital signature or whatsoever.
   *
   * @param aDoc
   *        The created non-<code>null</code> document.
   */
  @OverrideOnDemand
  @MustBeLocked (ELockType.WRITE)
  protected void modifyWriteData (@Nonnull final IMicroDocument aDoc)
  {
    CONDLOG.info ( () -> "Inserting automatic 'do NOT modify' header to XML");

    final IMicroComment aComment = new MicroComment ("This file was generated automatically - do NOT modify!\n" +
                                                     "Written at " +
                                                     PDTToString.getAsString (ZonedDateTime.now (Clock.systemUTC ()),
                                                                              Locale.US));
    final IMicroElement eRoot = aDoc.getDocumentElement ();
    // Add a small comment
    if (eRoot != null)
      aDoc.insertBefore (aComment, eRoot);
    else
      aDoc.addChild (aComment);
  }

  /**
   * @return The {@link IXMLWriterSettings} to be used to serialize the data.
   */
  @Nonnull
  @OverrideOnDemand
  protected IXMLWriterSettings getXMLWriterSettings ()
  {
    return WRITE_XWS;
  }

  /**
   * @return The filename to which was written last. May be <code>null</code> if no wrote action was
   *         performed yet.
   */
  @Nullable
  public final String getLastFilename ()
  {
    return m_aRWLock.readLockedGet ( () -> m_sPreviousFilename);
  }

  /**
   * Trigger the registered custom exception handlers for read errors.
   *
   * @param t
   *        Thrown exception. Never <code>null</code>.
   * @param sErrorFilename
   *        The filename tried to write to. Never <code>null</code>.
   * @param aDoc
   *        The XML content that should be written. May be <code>null</code> if the error occurred
   *        in XML creation.
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
   * The main method for writing the new data to a file. This method may only be called within a
   * write lock!
   *
   * @return {@link ESuccess} and never <code>null</code>.
   */
  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  private ESuccess _writeToFile ()
  {
    // Build the filename to write to
    final String sFilename = m_aFilenameProvider.get ();
    if (sFilename == null)
    {
      // We're not operating on a file! Required for testing
      CONDLOG.info ( () -> "The DAO of class " + getClass ().getName () + " cannot write to a file");
      return ESuccess.FAILURE;
    }

    // Check for a filename change before writing
    if (!sFilename.equals (m_sPreviousFilename))
    {
      onFilenameChange (m_sPreviousFilename, sFilename);
      m_sPreviousFilename = sFilename;
    }
    CONDLOG.info ( () -> "Trying to write WAL DAO file '" + sFilename + "'");

    File aFileNew = null;
    IMicroDocument aDoc = null;
    final String sFilenameNew = _getFilenameNew (sFilename);
    final String sFilenamePrev = _getFilenamePrev (sFilename);
    try
    {
      // Get the file handle
      aFileNew = getSafeFile (sFilenameNew, EMode.WRITE);

      m_aStatsCounterWriteTotal.increment ();
      final StopWatch aSW = StopWatch.createdStarted ();

      CONDLOG.info ( () -> "Creating XML file to write");

      // Create XML document to write
      aDoc = createWriteData ();
      if (aDoc == null)
        throw new DAOException ("Failed to create data to write to file");

      // Generic modification
      modifyWriteData (aDoc);

      CONDLOG.info ( () -> "Opening output stream of '" + sFilenameNew + "'");

      // Get the output stream
      final OutputStream aOS = FileHelper.getOutputStream (aFileNew);
      if (aOS == null)
      {
        // Happens, when another application has the file open!
        // Logger warning already emitted
        throw new DAOException ("Failed to open output stream for '" + aFileNew.getAbsolutePath () + "'");
      }
      // Write to file (closes the OS)
      final IXMLWriterSettings aXWS = getXMLWriterSettings ();

      CONDLOG.info ( () -> "Now serializing XML to stream with XWS " + aXWS);

      if (MicroWriter.writeToStream (aDoc, aOS, aXWS).isFailure ())
        throw new DAOException ("Failed to write DAO XML data to file");

      CONDLOG.info ( () -> "Finished serializing XML to stream");

      // Rename existing file to old
      FileIOError aIOError;
      boolean bRenamedToPrev = false;
      if (m_aIO.existsFile (sFilename))
      {
        aIOError = m_aIO.renameFile (sFilename, sFilenamePrev);
        bRenamedToPrev = true;
      }
      else
      {
        aIOError = new FileIOError (EFileIOOperation.RENAME_FILE, EFileIOErrorCode.NO_ERROR);
      }

      if (aIOError.isSuccess ())
      {
        // Rename new file to final
        aIOError = m_aIO.renameFile (sFilenameNew, sFilename);
        if (aIOError.isSuccess ())
        {
          // Finally delete old file
          aIOError = m_aIO.deleteFileIfExisting (sFilenamePrev);
        }
        else
        {
          // 2nd rename failed
          // -> Revert original rename to stay as consistent as possible
          if (bRenamedToPrev)
            m_aIO.renameFile (sFilenamePrev, sFilename);
        }
      }
      if (aIOError.isFailure ())
      {
        final String sMsg = "Error on rename(existing-old)/rename(new-existing)/delete(old): " + aIOError;
        LOGGER.error (sMsg);
        throw new IllegalStateException (sMsg);
      }

      // Update stats etc.
      m_aStatsCounterWriteTimer.addTime (aSW.stopAndGetMillis ());
      m_aStatsCounterWriteSuccess.increment ();
      m_nWriteCount++;
      m_aLastWriteDT = PDTFactory.getCurrentLocalDateTime ();
      return ESuccess.SUCCESS;
    }
    catch (final DAOException | RuntimeException ex)
    {
      final String sErrorFilename = aFileNew != null ? aFileNew.getAbsolutePath () : sFilename;

      LOGGER.error ("The DAO of class " +
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

  @MustBeLocked (ELockType.WRITE)
  final void _writeToFileAndResetPendingChanges (@Nonnull final String sCallingMethodName)
  {
    if (_writeToFile ().isSuccess ())
      internalSetPendingChanges (false);
    else
    {
      LOGGER.error ("The DAO of class " +
                    getClass ().getName () +
                    " still has pending changes after " +
                    sCallingMethodName +
                    "!");
    }
  }

  /**
   * @return The name of the WAL file of this DAO or <code>null</code> if this DAO does not support
   *         WAL files.
   */
  @Nullable
  private String _getWALFilename ()
  {
    final String sWALFilename = m_aFilenameProvider.get ();
    if (sWALFilename == null)
      return null;
    return sWALFilename + ".wal";
  }

  /**
   * This method is called if recovery from the WAL file (partially) failed an analysis might be
   * needed.
   */
  final void _maintainWALFileAfterProcessing (@Nonnull @Nonempty final String sWALFilename)
  {
    ValueEnforcer.notEmpty (sWALFilename, "WALFilename");
    final File aWALFile = m_aIO.getFile (sWALFilename);
    final File aNewFile = new File (aWALFile.getParentFile (),
                                    aWALFile.getName () + "." + PDTFactory.getCurrentMillis () + ".bup");

    if (FileOperationManager.INSTANCE.renameFile (aWALFile, aNewFile).isFailure ())
    {
      LOGGER.error ("Failed to rename WAL file '" +
                    aWALFile.getAbsolutePath () +
                    "' to '" +
                    aNewFile.getAbsolutePath () +
                    "'");
    }
    else
    {
      CONDLOG.info ( () -> "Maintained WAL file '" +
                           aWALFile.getAbsolutePath () +
                           "' as '" +
                           aNewFile.getAbsolutePath () +
                           "' for debugging purposes");
    }
  }

  /**
   * This method may only be triggered with valid WAL filenames, as the passed file is deleted!
   */
  final void _deleteWALFileAfterProcessing (@Nonnull @Nonempty final String sWALFilename)
  {
    ValueEnforcer.notEmpty (sWALFilename, "WALFilename");
    final File aWALFile = m_aIO.getFile (sWALFilename);
    if (FileOperationManager.INSTANCE.deleteFile (aWALFile).isFailure ())
    {
      LOGGER.error ("Failed to delete WAL file '" + aWALFile.getAbsolutePath () + "'");
    }
    else
    {
      CONDLOG.info ( () -> "Deleted successfully imported WAL file '" + aWALFile.getAbsolutePath () + "'");
    }
  }

  /**
   * @return The {@link IXMLWriterSettings} to be used to serialize the data.
   */
  @Nonnull
  @OverrideOnDemand
  protected IXMLWriterSettings getWALXMLWriterSettings ()
  {
    return WAL_XWS;
  }

  @Nonnull
  @OverrideOnDemand
  protected String convertNativeToWALString (@Nonnull final DATATYPE aModifiedElement)
  {
    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aModifiedElement, "item");
    if (aElement == null)
    {
      final String sMsg = "Failed to convert " +
                          aModifiedElement +
                          " of class " +
                          aModifiedElement.getClass ().getName () +
                          " to XML!";
      LOGGER.error (sMsg);
      throw new IllegalStateException (sMsg);
    }
    return MicroWriter.getNodeAsString (aElement, getWALXMLWriterSettings ());
  }

  @Nonnull
  @MustBeLocked (ELockType.WRITE)
  private ESuccess _writeWALFile (@Nonnull @Nonempty final List <DATATYPE> aModifiedElements,
                                  @Nonnull final EDAOActionType eActionType,
                                  @Nonnull @Nonempty final String sWALFilename)
  {
    final FileSystemResource aWALRes = m_aIO.getResource (sWALFilename);
    CONDLOG.info ( () -> "Writing WAL file " + aWALRes);

    try (final DataOutputStream aDOS = new DataOutputStream (aWALRes.getOutputStream (EAppend.APPEND)))
    {
      // Write action type ID
      StreamHelper.writeSafeUTF (aDOS, eActionType.getID ());

      // Write number of elements
      aDOS.writeInt (aModifiedElements.size ());

      // Write all data elements as XML Strings :)
      for (final DATATYPE aModifiedElement : aModifiedElements)
      {
        final String sElement = convertNativeToWALString (aModifiedElement);
        StreamHelper.writeSafeUTF (aDOS, sElement);
      }

      CONDLOG.info ( () -> "Finished writing WAL file " + aWALRes);
      return ESuccess.SUCCESS;
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error writing WAL file " + aWALRes, ex);
      triggerExceptionHandlersWrite (ex, sWALFilename, (IMicroDocument) null);
    }
    return ESuccess.FAILURE;
  }

  /**
   * @return The waiting time used before the file is effectively written. Never <code>null</code>.
   *         Default value is 10 seconds.
   */
  @Nonnull
  public Duration getWaitingTime ()
  {
    return m_aWaitingTime;
  }

  /**
   * Set the waiting time used before the file is effectively written. Default value is 10 seconds.
   * Setting the value to a duration of 0 means that the write ahead is effectively disabled.
   *
   * @param aWaitingTime
   *        The waiting time to be used. May not be <code>null</code>.
   */
  protected void setWaitingTime (@Nonnull final Duration aWaitingTime)
  {
    ValueEnforcer.notNull (aWaitingTime, "WaitingTime");
    m_aWaitingTime = aWaitingTime;
    CONDLOG.info ( () -> "Set WAL DAO waiting time to " + aWaitingTime);
  }

  /**
   * This method must be called every time something changed in the DAO. It triggers the writing to
   * a file if auto-save is active. This method must be called within a write-lock as it is not
   * locked!
   *
   * @param aModifiedElement
   *        The modified data element. May not be <code>null</code>.
   * @param eActionType
   *        The action that was performed. May not be <code>null</code>.
   */
  @MustBeLocked (ELockType.WRITE)
  @OverridingMethodsMustInvokeSuper
  protected void markAsChanged (@Nonnull final DATATYPE aModifiedElement, @Nonnull final EDAOActionType eActionType)
  {
    ValueEnforcer.notNull (aModifiedElement, "ModifiedElement");
    ValueEnforcer.notNull (eActionType, "ActionType");

    // Convert single item to list
    markAsChanged (new CommonsArrayList <> (aModifiedElement), eActionType);
  }

  @MustBeLocked (ELockType.WRITE)
  protected final void markAsChanged (@Nonnull final List <DATATYPE> aModifiedElements,
                                      @Nonnull final EDAOActionType eActionType)
  {
    ValueEnforcer.notNull (aModifiedElements, "ModifiedElements");
    ValueEnforcer.notNull (eActionType, "ActionType");

    CONDLOG.info ( () -> "Now processing WAL DAO action " +
                         eActionType +
                         " for " +
                         aModifiedElements.size () +
                         " elements");

    // Just remember that something changed
    internalSetPendingChanges (true);
    if (internalIsAutoSaveEnabled ())
    {
      // Auto save

      // Write a WAL file
      final String sWALFilename = _getWALFilename ();
      // Note: writing a WAL is forbidden when a WAL file is recovered upon
      // startup!
      // Note: writing a WAL makes no sense, if the waiting time is zero
      if (m_bCanWriteWAL &&
          m_aWaitingTime.compareTo (Duration.ZERO) > 0 &&
          sWALFilename != null &&
          _writeWALFile (aModifiedElements, eActionType, sWALFilename).isSuccess ())
      {
        // Remember change for later writing
        // Note: pass the WAL filename in case the filename changes over time!
        m_aWALListener.registerForLaterWriting (this, sWALFilename, m_aWaitingTime);
      }
      else
      {
        // write directly
        _writeToFileAndResetPendingChanges ("markAsChanged(" + eActionType.getID () + ")");
      }
    }
  }

  /**
   * In case there are pending changes write them to the file. This method is write locked!
   */
  public final void writeToFileOnPendingChanges ()
  {
    if (hasPendingChanges ())
    {
      // Write to file
      m_aRWLock.writeLocked ( () -> _writeToFileAndResetPendingChanges ("writeToFileOnPendingChanges"));
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
                            .append ("DataTypeClass", m_aDataTypeClass)
                            .append ("IO", m_aIO)
                            .append ("FilenameProvider", m_aFilenameProvider)
                            .append ("PreviousFilename", m_sPreviousFilename)
                            .append ("InitCount", m_nInitCount)
                            .appendIfNotNull ("LastInitDT", m_aLastInitDT)
                            .append ("ReadCount", m_nReadCount)
                            .appendIfNotNull ("LastReadDT", m_aLastReadDT)
                            .append ("WriteCount", m_nWriteCount)
                            .appendIfNotNull ("LastWriteDT", m_aLastWriteDT)
                            .append ("CanWriteWAL", m_bCanWriteWAL)
                            .append ("WaitingTime", m_aWaitingTime)
                            .getToString ();
  }
}
