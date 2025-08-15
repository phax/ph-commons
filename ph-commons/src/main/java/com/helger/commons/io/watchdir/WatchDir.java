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
package com.helger.commons.io.watchdir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.system.EOperatingSystem;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.commons.callback.CallbackList;

import jakarta.annotation.Nonnull;

/**
 * Generic directory watching service using the default JDK {@link WatchService}
 * class.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class WatchDir implements AutoCloseable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WatchDir.class);
  private final WatchService m_aWatcher;
  private final Path m_aStartDir;
  private final boolean m_bRecursive;
  private final boolean m_bRegisterRecursiveManually;
  private final ICommonsMap <WatchKey, Path> m_aKeys = new CommonsHashMap <> ();
  private final AtomicBoolean m_aProcessing = new AtomicBoolean (false);
  private final CallbackList <IWatchDirCallback> m_aCallbacks = new CallbackList <> ();
  private WatchEvent.Modifier [] m_aModifiers = null;

  /**
   * Register the given directory with the WatchService
   *
   * @param aDir
   *        Directory to be watched. May not be <code>null</code>.
   */
  private void _registerDir (@Nonnull final Path aDir) throws IOException
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Register directory " +
                    aDir +
                    (m_bRecursive && !m_bRegisterRecursiveManually ? " (recursively)" : ""));

    final WatchEvent.Kind <?> [] aKinds = { StandardWatchEventKinds.ENTRY_CREATE,
                                            StandardWatchEventKinds.ENTRY_DELETE,
                                            StandardWatchEventKinds.ENTRY_MODIFY };

    // throws exception when using with modifiers even if null
    final WatchKey aKey = m_aModifiers != null ? aDir.register (m_aWatcher, aKinds, m_aModifiers) : aDir.register (
                                                                                                                   m_aWatcher,
                                                                                                                   aKinds);
    m_aKeys.put (aKey, aDir);
  }

  /**
   * Register the given directory, and all its sub-directories, with the
   * WatchService.
   *
   * @param aStartDir
   *        The start directory to be iterated. May not be <code>null</code>.
   */
  private void _registerDirRecursive (@Nonnull final Path aStartDir) throws IOException
  {
    // register directory and sub-directories
    Files.walkFileTree (aStartDir, new SimpleFileVisitor <Path> ()
    {
      @Override
      public FileVisitResult preVisitDirectory (final Path dir, final BasicFileAttributes attrs) throws IOException
      {
        _registerDir (dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  /**
   * Creates a WatchService and registers the given directory
   *
   * @param aDir
   *        The directory to be watched. May not be <code>null</code>.
   * @param bRecursive
   *        <code>true</code> to watch the directory recursive,
   *        <code>false</code> to watch just this directory.
   * @throws IOException
   *         In case something goes wrong.
   */
  public WatchDir (@Nonnull final Path aDir, final boolean bRecursive) throws IOException
  {
    ValueEnforcer.notNull (aDir, "Directory");
    ValueEnforcer.isTrue (aDir.toFile ().isDirectory (), () -> "Provided path is not a directory: " + aDir);

    m_aWatcher = FileSystems.getDefault ().newWatchService ();
    m_aStartDir = aDir.toRealPath ();
    m_bRecursive = bRecursive;

    boolean bRegisterRecursiveManually = bRecursive;
    // Windows only!
    if (bRecursive && EOperatingSystem.WINDOWS.isCurrentOS ())
    {
      // Reflection, as this is for Windows/Oracle JDK only!
      // Shortcut for com.sun.nio.file.ExtendedWatchEventModifier.FILE_TREE
      final Class <?> aClass = GenericReflection.getClassFromNameSafe ("com.sun.nio.file.ExtendedWatchEventModifier");
      if (aClass != null)
      {
        // Use the special "register recursive" on Windows (enum constant
        // "FILE_TREE")
        @SuppressWarnings ("unchecked")
        final Enum <?> [] aEnumConstants = ((Class <Enum <?>>) aClass).getEnumConstants ();
        final Enum <?> aFileTree = ArrayHelper.findFirst (aEnumConstants, x -> x.name ().equals ("FILE_TREE"));
        if (aFileTree != null)
        {
          m_aModifiers = new WatchEvent.Modifier [] { (WatchEvent.Modifier) aFileTree };
          bRegisterRecursiveManually = false;
        }
      }
    }
    m_bRegisterRecursiveManually = bRegisterRecursiveManually;
    if (m_bRegisterRecursiveManually)
      _registerDirRecursive (m_aStartDir);
    else
      _registerDir (m_aStartDir);
  }

  /**
   * @return The modifiable callback list. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  public CallbackList <IWatchDirCallback> callbacks ()
  {
    return m_aCallbacks;
  }

  /**
   * @return The start directory as specified in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public Path getStartDirectory ()
  {
    return m_aStartDir;
  }

  /**
   * @return <code>true</code> if this is a recursive listener,
   *         <code>false</code> if not.
   */
  public boolean isRecursive ()
  {
    return m_bRecursive;
  }

  /**
   * Close the watch directory service and stop processing.
   */
  public void close () throws IOException
  {
    try
    {
      // Mark the processing to end
      // This ends the processing started in #processEvents and will end any
      // eventually running thread
      stopProcessing ();
    }
    finally
    {
      m_aWatcher.close ();
    }
  }

  /**
   * Stop processing, if {@link #processEvents()} is active. This method is
   * automatically called in {@link #close()}.
   */
  public void stopProcessing ()
  {
    m_aProcessing.set (false);
  }

  /**
   * Check if processing is active.
   *
   * @return <code>true</code> if event processing is active, <code>false</code>
   *         if not.
   * @see #processEvents()
   * @see #stopProcessing()
   * @see #close()
   */
  public boolean isProcessing ()
  {
    return m_aProcessing.get ();
  }

  /**
   * Process all events for keys queued to the watcher. Call
   * {@link #stopProcessing()} or {@link #close()} to stop processing within a
   * reasonable time. This method should run in a separate thread, as it
   * contains an infinite loop! Usually you don't call this method manually.
   *
   * @see #runAsync()
   */
  public void processEvents ()
  {
    LOGGER.info ("Start processing directory change events in '" +
                 m_aStartDir +
                 "'" +
                 (m_bRecursive ? " (recursively)" : ""));

    if (m_aCallbacks.isEmpty ())
      throw new IllegalStateException ("No callback registered for watching directory changes in " + m_aStartDir);

    m_aProcessing.set (true);
    while (m_aProcessing.get ())
    {
      // wait max 1 sec for key to be signaled
      WatchKey aKey;
      try
      {
        aKey = m_aWatcher.poll (1, TimeUnit.SECONDS);
      }
      catch (final InterruptedException x)
      {
        // Watcher was interrupted - stop loop
        Thread.currentThread ().interrupt ();
        break;
      }
      catch (final ClosedWatchServiceException x)
      {
        // Watcher was interrupted - stop loop
        break;
      }
      if (aKey == null)
      {
        // Nothing here within time limit - try again
        continue;
      }
      final Path aSrcDir = m_aKeys.get (aKey);
      if (aSrcDir == null)
      {
        LOGGER.error ("WatchKey " + aKey + " not recognized!!");
        continue;
      }
      for (final WatchEvent <?> aEvent : aKey.pollEvents ())
      {
        final WatchEvent.Kind <?> aKind = aEvent.kind ();
        if (aKind == StandardWatchEventKinds.OVERFLOW)
        {
          LOGGER.warn ("Got an overflow event on directory " + aSrcDir);
          continue;
        }
        // Context for directory entry event is the file name of entry
        final Path aEventPath = (Path) aEvent.context ();
        final Path aFullEventPath = aSrcDir.resolve (aEventPath);

        // print out event
        EWatchDirAction eAction;
        if (aKind == StandardWatchEventKinds.ENTRY_CREATE)
          eAction = EWatchDirAction.CREATE;
        else
          if (aKind == StandardWatchEventKinds.ENTRY_DELETE)
            eAction = EWatchDirAction.DELETE;
          else
            if (aKind == StandardWatchEventKinds.ENTRY_MODIFY)
              eAction = EWatchDirAction.MODIFY;
            else
            {
              eAction = null;
              LOGGER.error ("Unsupported event kind: " + aKind + " on path: '" + aFullEventPath + "'");
            }
        if (eAction != null)
        {
          // Main callback invocation
          m_aCallbacks.forEach (x -> x.onAction (eAction, aFullEventPath));
        }
        // if directory is created, and watching recursively, then
        // register it and its sub-directories
        if (m_bRecursive && aKind == StandardWatchEventKinds.ENTRY_CREATE)
        {
          try
          {
            // Better performance
            if (aFullEventPath.toFile ().isDirectory ())
            {
              if (m_bRegisterRecursiveManually)
                _registerDirRecursive (aFullEventPath);
              else
                if (aFullEventPath.equals (aSrcDir))
                {
                  // The main directory was altered (e.g. renamed) so
                  // re-register
                  _registerDir (aFullEventPath);
                }
            }
          }
          catch (final IOException x)
          {
            throw new UncheckedIOException ("Error registering handler ony the fly for " + aFullEventPath, x);
          }
        }
      }
      // reset key and remove from set if directory no longer accessible
      final boolean bValid = aKey.reset ();
      if (!bValid)
      {
        LOGGER.info ("Unregister directory " + aSrcDir);
        m_aKeys.remove (aKey);

        // all directories are inaccessible
        // -> leave main loop
        if (m_aKeys.isEmpty ())
          break;
      }
    }
    LOGGER.info ("Finished processing directory change events in '" + m_aStartDir + "'");
  }

  /**
   * Call this method to process events. This method creates a background thread
   * than runs {@link #processEvents()} and performs the heavy lifting.
   */
  public void runAsync ()
  {
    runAsyncAndReturn ();
  }

  /**
   * Call this method to process events. This method creates a background thread
   * than runs {@link #processEvents()} and performs the heavy lifting.
   *
   * @return The created {@link Thread} that can also be stopped again if not
   *         needed anymore.
   * @since 10.1.5
   */
  @Nonnull
  public Thread runAsyncAndReturn ()
  {
    final Thread aThread = new Thread (this::processEvents,
                                       "WatchDir-" + m_aStartDir + "-" + ThreadLocalRandom.current ().nextInt ());
    aThread.setDaemon (true);
    aThread.start ();
    return aThread;
  }

  /**
   * Static factory method to create a simple {@link WatchDir} instance that
   * already spawned an Thread to listen. To close the thread call the
   * {@link WatchDir#close()} method.
   *
   * @param aDir
   *        The directory to be watched. May not be <code>null</code>.
   * @param bRecursive
   *        <code>true</code> to watch the directory recursive,
   *        <code>false</code> to watch just this directory.
   * @param aCallback
   *        The callback to be invoked if something changed. May not be
   *        <code>null</code>.
   * @return The newly created {@link WatchDir} instance and never
   *         <code>null</code>.
   * @throws IOException
   *         In case something goes wrong.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static WatchDir createAsyncRunningWatchDir (@Nonnull final Path aDir,
                                                     final boolean bRecursive,
                                                     @Nonnull final IWatchDirCallback aCallback) throws IOException
  {
    final WatchDir ret = new WatchDir (aDir, bRecursive);
    ret.callbacks ().add (aCallback);
    ret.runAsync ();
    return ret;
  }
}
