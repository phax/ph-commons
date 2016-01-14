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
package com.helger.commons.io.file;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;

/**
 * All kind of file handling stuff. For other operations, please see
 * {@link FileOperations} class or the instance based
 * {@link FileOperationManager} class.
 *
 * @author Philip Helger
 */
@Immutable
public final class SimpleFileIO
{
  @PresentForCodeCoverage
  private static final SimpleFileIO s_aInstance = new SimpleFileIO ();

  private SimpleFileIO ()
  {}

  /**
   * Get the content of the file as a byte array.
   *
   * @param aFile
   *        The file to read. May be <code>null</code>.
   * @return <code>null</code> if the passed file is <code>null</code> or if the
   *         passed file does not exist.
   */
  @Nullable
  public static byte [] getAllFileBytes (@Nullable final File aFile)
  {
    return aFile == null ? null : StreamHelper.getAllBytes (FileHelper.getInputStream (aFile));
  }

  /**
   * Get the content of the passed file as a string using the system line
   * separator. Note: the last line does not end with the passed line separator.
   *
   * @param aFile
   *        The file to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the file does not exist, the content
   *         otherwise.
   */
  @Nullable
  public static String getFileAsString (@Nullable final File aFile, @Nonnull final Charset aCharset)
  {
    return aFile == null ? null : StreamHelper.getAllBytesAsString (FileHelper.getInputStream (aFile), aCharset);
  }

  /**
   * Get the content of the passed file as a list of lines, whereas each line
   * does not contain a separator.
   *
   * @param aFile
   *        The file to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the file does not exist, the content
   *         otherwise.
   */
  @Nullable
  public static List <String> getAllFileLines (@Nullable final File aFile, @Nonnull final Charset aCharset)
  {
    return aFile == null ? null : StreamHelper.readStreamLines (FileHelper.getInputStream (aFile), aCharset);
  }

  /**
   * Get the content of the passed file as a list of lines, whereas each line
   * does not contain a separator.
   *
   * @param aFile
   *        The file to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param aTargetList
   *        The target list to be filled. May not be <code>null</code>.
   */
  public static void readFileLines (@Nullable final File aFile,
                                    @Nonnull final Charset aCharset,
                                    @Nonnull final List <String> aTargetList)
  {
    if (aFile != null)
      StreamHelper.readStreamLines (FileHelper.getInputStream (aFile), aCharset, aTargetList);
  }

  @Nonnull
  public static ESuccess writeFile (@Nonnull final File aFile, @Nonnull final byte [] aContent)
  {
    final OutputStream aFOS = FileHelper.getOutputStream (aFile);
    return aFOS == null ? ESuccess.FAILURE : StreamHelper.writeStream (aFOS, aContent);
  }

  @Nonnull
  public static ESuccess writeFile (@Nonnull final File aFile,
                                    @Nonnull final byte [] aContent,
                                    @Nonnegative final int nOffset,
                                    @Nonnegative final int nLength)
  {
    final OutputStream aFOS = FileHelper.getOutputStream (aFile);
    return aFOS == null ? ESuccess.FAILURE : StreamHelper.writeStream (aFOS, aContent, nOffset, nLength);
  }

  @Nonnull
  public static ESuccess writeFile (@Nonnull final File aFile,
                                    @Nonnull final String sContent,
                                    @Nonnull final Charset aCharset)
  {
    final OutputStream aFOS = FileHelper.getOutputStream (aFile);
    return aFOS == null ? ESuccess.FAILURE : StreamHelper.writeStream (aFOS, sContent, aCharset);
  }
}
