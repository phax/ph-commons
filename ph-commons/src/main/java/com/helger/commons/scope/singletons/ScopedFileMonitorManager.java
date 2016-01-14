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
package com.helger.commons.scope.singletons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Singleton;
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.io.monitor.FileMonitor;
import com.helger.commons.io.monitor.FileMonitorManager;
import com.helger.commons.io.monitor.IFileMonitorCallback;
import com.helger.commons.scope.IScope;
import com.helger.commons.scope.singleton.AbstractGlobalSingleton;
import com.helger.commons.state.EChange;

/**
 * A global scoped singleton encapsulating a {@link FileMonitorManager}
 *
 * @author Philip Helger
 */
@Singleton
public class ScopedFileMonitorManager extends AbstractGlobalSingleton
{
  private final FileMonitorManager m_aFMM = new FileMonitorManager ();

  @Deprecated
  @UsedViaReflection
  public ScopedFileMonitorManager ()
  {
    // Start monitoring files - spawn thread
    m_aFMM.start ();
  }

  @Nonnull
  public static ScopedFileMonitorManager getInstance ()
  {
    return getGlobalSingleton (ScopedFileMonitorManager.class);
  }

  @Override
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction)
  {
    // Stop monitor thread
    m_aFMM.stop ();
  }

  /**
   * Starts monitoring the files
   *
   * @return {@link EChange}
   * @throws IllegalStateException
   *         if the monitoring is already running
   * @see #isRunning()
   * @see #stop()
   */
  @Nonnull
  public EChange start ()
  {
    if (isRunning ())
      return EChange.UNCHANGED;
    m_aFMM.start ();
    return EChange.CHANGED;
  }

  /**
   * Stops monitoring the files.
   *
   * @return {@link EChange}
   */
  @Nonnull
  public EChange stop ()
  {
    if (!isRunning ())
      return EChange.UNCHANGED;
    m_aFMM.stop ();
    return EChange.CHANGED;
  }

  /**
   * @return <code>true</code> if the monitoring thread is running,
   *         <code>false</code> if not.
   */
  public boolean isRunning ()
  {
    return m_aFMM.isRunning ();
  }

  /**
   * Get the delay between runs.
   *
   * @return The delay period in milliseconds.
   */
  public long getDelay ()
  {
    return m_aFMM.getDelay ();
  }

  /**
   * Set the delay between runs.
   *
   * @param nDelay
   *        The delay period in milliseconds.
   */
  public void setDelay (final long nDelay)
  {
    m_aFMM.setDelay (nDelay);
  }

  /**
   * get the number of files to check per run.
   *
   * @return The number of files to check per iteration.
   */
  public int getChecksPerRun ()
  {
    return m_aFMM.getChecksPerRun ();
  }

  /**
   * set the number of files to check per run. a additional delay will be added
   * if there are more files to check
   *
   * @param nChecksPerRun
   *        a value less than 1 will disable this feature
   */
  public void setChecksPerRun (final int nChecksPerRun)
  {
    m_aFMM.setChecksPerRun (nChecksPerRun);
  }

  /**
   * Create a new {@link FileMonitor} based on the passed file listener.
   *
   * @param aListener
   *        The listener to be used. May not be <code>null</code>.
   * @return The created {@link FileMonitor} that was already added.
   * @see #addFileMonitor(FileMonitor)
   */
  @Nonnull
  public FileMonitor createFileMonitor (@Nonnull final IFileMonitorCallback aListener)
  {
    return m_aFMM.createFileMonitor (aListener);
  }

  /**
   * Add a new {@link FileMonitor}.
   *
   * @param aMonitor
   *        The monitor to be added. May not be <code>null</code>.
   */
  public void addFileMonitor (@Nonnull final FileMonitor aMonitor)
  {
    m_aFMM.addFileMonitor (aMonitor);
  }

  /**
   * Remove a {@link FileMonitor}.
   *
   * @param aMonitor
   *        The monitor to be remove. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeFileMonitor (@Nullable final FileMonitor aMonitor)
  {
    return m_aFMM.removeFileMonitor (aMonitor);
  }
}
