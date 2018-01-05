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
package com.helger.settings.exchange;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.state.ESuccess;
import com.helger.settings.ISettings;

/**
 * Interface for persisting settings via input- and output streams.
 *
 * @author philip
 */
public interface ISettingsPersistence
{
  /**
   * @return The charset for reading and writing. Never <code>null</code>.
   */
  @Nonnull
  Charset getCharset ();

  /**
   * Read settings from a String and convert it to an {@link ISettings} object.
   * Note: to read from a file you need to explicitly invoke the
   * {@link #readSettings(File)} method!
   *
   * @param sSettings
   *        The settings string. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, a non-<code>null</code>
   *         settings object otherwise.
   */
  @Nonnull
  default ISettings readSettings (@Nonnull final String sSettings)
  {
    ValueEnforcer.notNull (sSettings, "Settings");

    return readSettings (new StringInputStream (sSettings, getCharset ()));
  }

  /**
   * Read settings from a file and convert it to an {@link ISettings} object.
   *
   * @param aFile
   *        The settings file. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, a non-<code>null</code>
   *         settings object otherwise.
   */
  @Nonnull
  default ISettings readSettings (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    return readSettings (FileHelper.getInputStream (aFile));
  }

  /**
   * Read settings from an InputStream provider and convert it to an
   * {@link ISettings} object.
   *
   * @param aISP
   *        The InputStream provider to read from. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, a non-<code>null</code>
   *         settings object otherwise.
   */
  @Nonnull
  default ISettings readSettings (@Nonnull final IHasInputStream aISP)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");

    final InputStream aIS = aISP.getInputStream ();
    if (aIS == null)
      throw new IllegalArgumentException ("Failed to open the provided input stream for the settings: " + aISP);
    return readSettings (aIS);
  }

  /**
   * Read settings from an input stream and convert it to an {@link ISettings}
   * object.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>. Must be
   *        closed by the implementing method.
   * @return <code>null</code> if reading failed, a non-<code>null</code>
   *         settings object otherwise.
   */
  @Nullable
  ISettings readSettings (@Nonnull @WillClose InputStream aIS);

  /**
   * Write settings to a String.
   *
   * @param aSettings
   *        The settings to be written. May not be <code>null</code>.
   * @return The string representation of the settings. <code>null</code> when
   *         writing/conversion fails.
   */
  @Nullable
  default String writeSettings (@Nonnull final ISettings aSettings)
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    if (writeSettings (aSettings, aBAOS).isFailure ())
      return null;
    return aBAOS.getAsString (getCharset ());
  }

  /**
   * Write settings to a file.
   *
   * @param aSettings
   *        The settings to be written. May not be <code>null</code>.
   * @param aFile
   *        The file where the settings should be written to. May not be
   *        <code>null</code>.
   * @return Success and never <code>null</code>.
   */
  @Nonnull
  default ESuccess writeSettings (@Nonnull final ISettings aSettings, @Nonnull final File aFile)
  {
    final OutputStream aOS = FileHelper.getOutputStream (aFile);
    if (aOS == null)
      throw new IllegalArgumentException ("Failed to open file '" + aFile.getAbsolutePath () + "' for writing!");
    return writeSettings (aSettings, aOS);
  }

  /**
   * Write settings to a stream.
   *
   * @param aSettings
   *        The settings to be written. May not be <code>null</code>.
   * @param aOS
   *        The output stream where the settings should be written to. May not
   *        be <code>null</code>. After writing to the stream the output stream
   *        must be closed by the implementing method.
   * @return Success and never <code>null</code>.
   */
  @Nonnull
  ESuccess writeSettings (@Nonnull ISettings aSettings, @Nonnull @WillClose OutputStream aOS);
}
