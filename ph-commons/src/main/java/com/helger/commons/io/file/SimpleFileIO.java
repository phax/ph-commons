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
package com.helger.commons.io.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
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

  @Nullable
  public static byte [] getAllFileBytes (@Nullable final Path aPath)
  {
    try
    {
      return aPath == null ? null : Files.readAllBytes (aPath);
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  /**
   * Read all lines from a file. This method ensures that the file is closed
   * when all bytes have been read or an I/O error, or other runtime exception,
   * is thrown. Bytes from the file are decoded into characters using the
   * specified charset.
   * <p>
   * This method recognizes the following as line terminators:
   * <ul>
   * <li><code>&#92;u000D</code> followed by <code>&#92;u000A</code>, CARRIAGE
   * RETURN followed by LINE FEED</li>
   * <li><code>&#92;u000A</code>, LINE FEED</li>
   * <li><code>&#92;u000D</code>, CARRIAGE RETURN</li>
   * </ul>
   * <p>
   * Additional Unicode line terminators may be recognized in future releases.
   * <p>
   * Note that this method is intended for simple cases where it is convenient
   * to read all lines in a single operation. It is not intended for reading in
   * large files.
   *
   * @param aPath
   *        the path to the file
   * @param aCharset
   *        the charset to use for decoding
   * @return the lines from the file as a {@code List}; whether the {@code
   *          List} is modifiable or not is implementation dependent and
   *         therefore not specified
   * @throws IOException
   *         if an I/O error occurs reading from the file or a malformed or
   *         unmappable byte sequence is read
   * @throws SecurityException
   *         In the case of the default provider, and a security manager is
   *         installed, the {@link SecurityManager#checkRead(String) checkRead}
   *         method is invoked to check read access to the file.
   */
  public static List <String> readAllLines (@Nonnull final Path aPath,
                                            @Nonnull final Charset aCharset) throws IOException
  {
    try (NonBlockingBufferedReader reader = PathHelper.getBufferedReader (aPath, aCharset))
    {
      final List <String> result = new ArrayList <> ();
      for (;;)
      {
        final String line = reader.readLine ();
        if (line == null)
          break;
        result.add (line);
      }
      return result;
    }
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
  public static ICommonsList <String> getAllFileLines (@Nullable final File aFile, @Nonnull final Charset aCharset)
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

  /**
   * Get the content of the passed file as a list of lines, whereas each line
   * does not contain a separator.
   *
   * @param aFile
   *        The file to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for each line. May not be
   *        <code>null</code>.
   */
  public static void readFileLines (@Nullable final File aFile,
                                    @Nonnull final Charset aCharset,
                                    @Nonnull final Consumer <? super String> aConsumer)
  {
    if (aFile != null)
      StreamHelper.readStreamLines (FileHelper.getInputStream (aFile), aCharset, aConsumer);
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
