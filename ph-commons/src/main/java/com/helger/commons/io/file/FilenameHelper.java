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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemHelper;

/**
 * All kind of file name handling stuff. This class gives you platform
 * independent file name handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class FilenameHelper
{
  /** The file extension separation character. */
  public static final char EXTENSION_SEPARATOR = '.';

  /** The replacement character used for illegal file name characters. */
  public static final char ILLEGAL_FILENAME_CHAR_REPLACEMENT = '_';

  /** Special name of the current path */
  public static final String PATH_CURRENT = ".";

  /** Special name of the parent path */
  public static final String PATH_PARENT = "..";

  /** The Unix path separator character. */
  public static final char UNIX_SEPARATOR = '/';

  /** The Unix path separator string. */
  public static final String UNIX_SEPARATOR_STR = Character.toString (UNIX_SEPARATOR);

  /** The Windows separator character. */
  public static final char WINDOWS_SEPARATOR = '\\';

  /** The Windows separator string. */
  public static final String WINDOWS_SEPARATOR_STR = Character.toString (WINDOWS_SEPARATOR);

  /** The prefix to identify UNC paths on Unix based systems */
  public static final String UNIX_UNC_PREFIX = "//";

  /** The prefix to identify UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX = "\\\\";

  /** The prefix to identify local UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX_LOCAL1 = "\\\\.\\";
  /** The prefix to identify local UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX_LOCAL2 = "\\\\?\\";

  /** The prefix used for Unix hidden files */
  public static final char HIDDEN_FILE_PREFIX = '.';

  private static final Logger s_aLogger = LoggerFactory.getLogger (FilenameHelper.class);

  /**
   * Illegal characters in Windows file names.<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final char [] ILLEGAL_CHARACTERS = { 0, '<', '>', '?', '*', ':', '|', '"' };

  /**
   * see http://www.w3.org/TR/widgets/#zip-relative <br>
   * see http://forum.java.sun.com/thread.jspa?threadID=544334&tstart=165<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final String [] ILLEGAL_PREFIXES = { "CLOCK$",
                                                      "CON",
                                                      "PRN",
                                                      "AUX",
                                                      "NUL",
                                                      "COM2",
                                                      "COM3",
                                                      "COM4",
                                                      "COM5",
                                                      "COM6",
                                                      "COM7",
                                                      "COM8",
                                                      "COM9",
                                                      "LPT1",
                                                      "LPT2",
                                                      "LPT3",
                                                      "LPT4",
                                                      "LPT5",
                                                      "LPT6",
                                                      "LPT7",
                                                      "LPT8",
                                                      "LPT9" };

  private static final char [] ILLEGAL_SUFFIXES = new char [] { '.', ' ', '\t' };

  static
  {
    if (!isSecureFilenameCharacter (ILLEGAL_FILENAME_CHAR_REPLACEMENT))
      throw new InitializationException ("The illegal filename replacement character must be a valid ASCII character!");
  }

  @PresentForCodeCoverage
  private static final FilenameHelper s_aInstance = new FilenameHelper ();

  private FilenameHelper ()
  {}

  /**
   * Returns the index of the last extension separator character, which is a
   * dot.
   * <p>
   * This method also checks that there is no directory separator after the last
   * dot. To do this it uses {@link #getIndexOfLastSeparator(String)} which will
   * handle a file in either Unix or Windows format.
   * <p>
   * The output will be the same irrespective of the machine that the code is
   * running on.
   *
   * @param sFilename
   *        The filename to find the last path separator in. May be
   *        <code>null</code>.
   * @return the index of the last separator character, or
   *         {@link CGlobal#ILLEGAL_UINT} if there is no such character or the
   *         input parameter is <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  public static int getIndexOfExtension (@Nullable final String sFilename)
  {
    if (sFilename == null)
      return CGlobal.ILLEGAL_UINT;

    final int nExtensionIndex = sFilename.lastIndexOf (EXTENSION_SEPARATOR);
    final int nLastSepIndex = getIndexOfLastSeparator (sFilename);
    return nLastSepIndex > nExtensionIndex ? CGlobal.ILLEGAL_UINT : nExtensionIndex;
  }

  /**
   * Get the name of the passed file without the extension. If the file name
   * contains a leading absolute path, the path is returned as well.
   *
   * @param aFile
   *        The file to extract the extension from. May be <code>null</code>.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. If the input file is <code>null</code>
   *         the return value is <code>null</code>.
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getWithoutExtension (@Nullable final File aFile)
  {
    return aFile == null ? null : getWithoutExtension (aFile.getPath ());
  }

  /**
   * Get the passed filename without the extension. If the file name contains a
   * leading absolute path, the path is returned as well.
   *
   * @param sFilename
   *        The filename to extract the extension from. May be <code>null</code>
   *        or empty.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. If the input string is <code>null</code>
   *         the return value is <code>null</code>.
   * @see #getIndexOfExtension(String)
   */
  @Nullable
  public static String getWithoutExtension (@Nullable final String sFilename)
  {
    final int nIndex = getIndexOfExtension (sFilename);
    return nIndex == CGlobal.ILLEGAL_UINT ? sFilename : sFilename.substring (0, nIndex);
  }

  /**
   * Get the extension of the passed file.
   *
   * @param aFile
   *        The file to extract the extension from. May be <code>null</code>.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. Never <code>null</code>.
   * @see #getExtension(String)
   */
  @Nonnull
  public static String getExtension (@Nullable final File aFile)
  {
    return aFile == null ? "" : getExtension (aFile.getName ());
  }

  /**
   * Get the extension of the passed filename.
   *
   * @param sFilename
   *        The filename to extract the extension from. May be <code>null</code>
   *        or empty.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. Never <code>null</code>.
   * @see #getIndexOfExtension(String)
   */
  @Nonnull
  public static String getExtension (@Nullable final String sFilename)
  {
    final int nIndex = getIndexOfExtension (sFilename);
    if (nIndex == CGlobal.ILLEGAL_UINT)
      return "";
    return sFilename.substring (nIndex + 1);
  }

  /**
   * Check if the passed file has one of the passed extensions. The comparison
   * is done case insensitive even on Unix machines.
   *
   * @param aFile
   *        The file to check the extension from. May be <code>null</code> or
   *        empty.
   * @param aExtensions
   *        An array of extensions (without the leading dot) which are matched
   *        case insensitive. May not be <code>null</code>.
   * @return <code>true</code> if the file has one of the passed extensions,
   *         else <code>false</code>.
   * @see #getExtension(File)
   */
  public static boolean hasExtension (@Nullable final File aFile, @Nonnull final String... aExtensions)
  {
    ValueEnforcer.notNull (aExtensions, "Extensions");

    // determine current extension.
    final String sExt = getExtension (aFile);
    for (final String sExtension : aExtensions)
      if (sExt.equalsIgnoreCase (sExtension))
        return true;
    return false;
  }

  /**
   * Check if the passed filename has one of the passed extensions. The
   * comparison is done case insensitive even on Unix machines.
   *
   * @param sFilename
   *        The filename to check the extension from. May be <code>null</code>
   *        or empty.
   * @param aExtensions
   *        An array of extensions (without the leading dot) which are matched
   *        case insensitive. May not be <code>null</code>.
   * @return <code>true</code> if the filename has one of the passed extensions,
   *         else <code>false</code>.
   * @see #getExtension(String)
   */
  public static boolean hasExtension (@Nullable final String sFilename, @Nonnull final String... aExtensions)
  {
    ValueEnforcer.notNull (aExtensions, "Extensions");

    // determine current extension.
    final String sExt = getExtension (sFilename);
    for (final String sExtension : aExtensions)
      if (sExt.equalsIgnoreCase (sExtension))
        return true;
    return false;
  }

  /**
   * Returns the index of the last directory separator character. This method
   * will handle a file in either Unix or Windows format. The position of the
   * last forward or backslash is returned. The output will be the same
   * irrespective of the machine that the code is running on.
   *
   * @param sFilename
   *        The filename to find the last path separator in, <code>null</code>
   *        returns {@link CGlobal#ILLEGAL_UINT}.
   * @return The index of the last separator character, or
   *         {@link CGlobal#ILLEGAL_UINT} if there is no such character
   */
  public static int getIndexOfLastSeparator (@Nullable final String sFilename)
  {
    return sFilename == null ? CGlobal.ILLEGAL_UINT : Math.max (sFilename.lastIndexOf (UNIX_SEPARATOR),
                                                                sFilename.lastIndexOf (WINDOWS_SEPARATOR));
  }

  /**
   * Get the name of the passed file without any eventually leading path. Note:
   * if the passed file is a directory, the name of the directory is returned.
   *
   * @param aFile
   *        The file. May be <code>null</code>.
   * @return The name only or <code>null</code> if the passed parameter is
   *         <code>null</code>.
   */
  @Nullable
  public static String getWithoutPath (@Nullable final File aFile)
  {
    return aFile == null ? null : aFile.getName ();
  }

  /**
   * Get the name of the passed file without any eventually leading path.
   *
   * @param sAbsoluteFilename
   *        The fully qualified file name. May be <code>null</code>.
   * @return The name only or <code>null</code> if the passed parameter is
   *         <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  @Nullable
  public static String getWithoutPath (@Nullable final String sAbsoluteFilename)
  {
    /**
     * Note: do not use <code>new File (sFilename).getName ()</code> since this
     * only invokes the underlying FileSystem implementation which handles path
     * handling only correctly on the native platform. Problem arose when
     * running application on a Linux server and making a file upload from a
     * Windows machine.
     */
    if (sAbsoluteFilename == null)
      return null;
    final int nLastSepIndex = getIndexOfLastSeparator (sAbsoluteFilename);
    return nLastSepIndex == CGlobal.ILLEGAL_UINT ? sAbsoluteFilename : sAbsoluteFilename.substring (nLastSepIndex + 1);
  }

  /**
   * Get the path of the passed file name without any eventually contained
   * filename.
   *
   * @param sAbsoluteFilename
   *        The fully qualified file name. May be <code>null</code>.
   * @return The path only including the last trailing path separator character.
   *         Returns <code>null</code> if the passed parameter is
   *         <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  @Nullable
  public static String getPath (@Nullable final String sAbsoluteFilename)
  {
    /**
     * Note: do not use <code>new File (sFilename).getPath ()</code> since this
     * only invokes the underlying FileSystem implementation which handles path
     * handling only correctly on the native platform. Problem arose when
     * running application on a Linux server and making a file upload from a
     * Windows machine.
     */
    if (sAbsoluteFilename == null)
      return null;
    final int nLastSepIndex = getIndexOfLastSeparator (sAbsoluteFilename);
    return nLastSepIndex == CGlobal.ILLEGAL_UINT ? "" : sAbsoluteFilename.substring (0, nLastSepIndex + 1);
  }

  /**
   * Get the passed filename without path and without extension.<br>
   * Example: <code>/dir1/dir2/file.txt</code> becomes <code>file</code>
   *
   * @param aFile
   *        The file to get the base name from. May be <code>null</code>.
   * @return The base name of the passed parameter. May be <code>null</code> if
   *         the parameter was <code>null</code>.
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getBaseName (@Nullable final File aFile)
  {
    return aFile == null ? null : getWithoutExtension (aFile.getName ());
  }

  /**
   * Get the passed filename without path and without extension.<br>
   * Example: <code>/dir1/dir2/file.txt</code> becomes <code>file</code>
   *
   * @param sAbsoluteFilename
   *        The filename to get the base name from. May be <code>null</code>.
   * @return The base name of the passed parameter. May be <code>null</code> if
   *         the parameter was <code>null</code>.
   * @see #getWithoutPath(String)
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getBaseName (@Nullable final String sAbsoluteFilename)
  {
    return getWithoutExtension (getWithoutPath (sAbsoluteFilename));
  }

  /**
   * Ensure that the path (not the absolute path!) of the passed file is using
   * the Unix style separator "/" instead of the Operating System dependent one.
   *
   * @param aFile
   *        The file to use. May be <code>null</code>
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @see #getPathUsingUnixSeparator(String)
   */
  @Nullable
  public static String getPathUsingUnixSeparator (@Nullable final File aFile)
  {
    return aFile == null ? null : getPathUsingUnixSeparator (aFile.getPath ());
  }

  /**
   * Ensure that the passed path is using the Unix style separator "/" instead
   * of the Operating System dependent one.
   *
   * @param sAbsoluteFilename
   *        The file name to use. May be <code>null</code>
   * @return <code>null</code> if the passed path is <code>null</code>.
   * @see #getPathUsingUnixSeparator(File)
   */
  @Nullable
  public static String getPathUsingUnixSeparator (@Nullable final String sAbsoluteFilename)
  {
    return sAbsoluteFilename == null ? null : sAbsoluteFilename.replace (WINDOWS_SEPARATOR, UNIX_SEPARATOR);
  }

  /**
   * Ensure that the path (not the absolute path!) of the passed file is using
   * the Windows style separator "\" instead of the Operating System dependent
   * one.
   *
   * @param aFile
   *        The file to use. May be <code>null</code>
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @see #getPathUsingWindowsSeparator(String)
   */
  @Nullable
  public static String getPathUsingWindowsSeparator (@Nullable final File aFile)
  {
    return aFile == null ? null : getPathUsingWindowsSeparator (aFile.getPath ());
  }

  /**
   * Ensure that the passed path is using the Windows style separator "\"
   * instead of the Operating System dependent one.
   *
   * @param sAbsoluteFilename
   *        The file name to use. May be <code>null</code>
   * @return <code>null</code> if the passed path is <code>null</code>.
   * @see #getPathUsingWindowsSeparator(File)
   */
  @Nullable
  public static String getPathUsingWindowsSeparator (@Nullable final String sAbsoluteFilename)
  {
    return sAbsoluteFilename == null ? null : sAbsoluteFilename.replace (UNIX_SEPARATOR, WINDOWS_SEPARATOR);
  }

  /**
   * Check whether the two passed file names are equal, independent of the used
   * separators (/ or \).
   *
   * @param sAbsoluteFilename1
   *        First file name. May be <code>null</code>.
   * @param sAbsoluteFilename2
   *        Second file name. May be <code>null</code>.
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   * @see #getPathUsingUnixSeparator(String)
   */
  public static boolean isEqualIgnoreFileSeparator (@Nullable final String sAbsoluteFilename1,
                                                    @Nullable final String sAbsoluteFilename2)
  {
    return EqualsHelper.equals (getPathUsingUnixSeparator (sAbsoluteFilename1),
                                getPathUsingUnixSeparator (sAbsoluteFilename2));
  }

  /**
   * Avoid 0 byte attack. E.g. file name "test.java\u0000.txt" is internally
   * represented as "test.java" but ends with ".txt".<br>
   * Note: the passed file name is <b>NOT</b> decoded (e.g. %20 stays %20 and
   * will not be converted to a space).
   *
   * @param sFilename
   *        The file name to check. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code> or
   *         everything up to the 0-byte.
   */
  @Nullable
  public static String getSecureFilename (@Nullable final String sFilename)
  {
    if (sFilename == null)
      return null;
    final int nIdx0 = sFilename.indexOf ('\0');
    return nIdx0 == -1 ? sFilename : sFilename.substring (0, nIdx0);
  }

  /**
   * Check if the passed file name is valid. It checks for illegal prefixes that
   * affects compatibility to Windows, illegal characters within a filename and
   * forbidden suffixes. This method fits only for filenames on one level. If
   * you want to check a full path, use
   * {@link #isValidFilenameWithPaths(String)}.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or
   *         empty or invalid. <code>true</code> if the filename is not empty
   *         and valid.
   * @see #containsPathSeparatorChar(String)
   */
  public static boolean isValidFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (StringHelper.hasNoText (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (containsPathSeparatorChar (sFilename))
      return false;

    // check for illegal last characters
    if (StringHelper.endsWithAny (sFilename, ILLEGAL_SUFFIXES))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : ILLEGAL_CHARACTERS)
      if (sFilename.indexOf (cIllegal) != -1)
        return false;

    // check prefixes directly
    for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
      if (sFilename.equalsIgnoreCase (sIllegalPrefix))
        return false;

    // check if filename is prefixed with it
    // Note: we can use the default locale, since all fixed names are pure ANSI
    // names
    final String sUCFilename = sFilename.toUpperCase (SystemHelper.getSystemLocale ());
    for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
      if (sUCFilename.startsWith (sIllegalPrefix + "."))
        return false;

    return true;
  }

  /**
   * Check if the passed filename path is valid. In contrast to
   * {@link #isValidFilename(String)} this method can also handle filenames
   * including paths.
   *
   * @param sFilename
   *        The filename to be checked for validity.
   * @return <code>true</code> if all path elements of the filename are valid,
   *         <code>false</code> if at least one element is invalid
   * @see #isValidFilename(String)
   */
  public static boolean isValidFilenameWithPaths (@Nullable final String sFilename)
  {
    if (StringHelper.hasNoText (sFilename))
      return false;

    // Iterate filename path by path
    File aFile = new File (sFilename);
    while (aFile != null)
    {
      final String sCurFilename = aFile.getName ();
      final File aParentFile = aFile.getParentFile ();
      if (sCurFilename.length () == 0 && aParentFile == null)
      {
        // The last part of an absolute path can be skipped!
        break;
      }
      if (!isValidFilename (sCurFilename))
        return false;
      aFile = aParentFile;
    }
    return true;
  }

  /**
   * Convert the passed filename into a valid filename by performing the
   * following actions:
   * <ol>
   * <li>Remove everything after a potential \0 character</li>
   * <li>Remove all characters that are invalid at the end of a file name</li>
   * <li>Replace all characters that are invalid inside a filename with a
   * underscore</li>
   * <li>If the filename is invalid on Windows platforms it is prefixed with an
   * underscore.</li>
   * </ol>
   *
   * @param sFilename
   *        The filename to be made value. May be <code>null</code>.
   * @return <code>null</code> if the input filename was <code>null</code> or if
   *         it consisted only of characters invalid for a filename; the
   *         potentially modified filename otherwise but <b>never</b> an empy
   *         string.
   * @see #getSecureFilename(String)
   */
  @Nullable
  @Nonempty
  public static String getAsSecureValidFilename (@Nullable final String sFilename)
  {
    // First secure it, by cutting everything behind the '\0'
    String ret = getSecureFilename (sFilename);

    // empty not allowed
    if (StringHelper.hasText (ret))
    {
      // Remove all trailing invalid suffixes
      while (ret.length () > 0 && StringHelper.endsWithAny (ret, ILLEGAL_SUFFIXES))
        ret = ret.substring (0, ret.length () - 1);

      // Replace all characters that are illegal inside a file name
      for (final char cIllegal : ILLEGAL_CHARACTERS)
        ret = ret.replace (cIllegal, ILLEGAL_FILENAME_CHAR_REPLACEMENT);

      // Check if a file matches an illegal prefix
      for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
        if (ret.equalsIgnoreCase (sIllegalPrefix))
        {
          ret = ILLEGAL_FILENAME_CHAR_REPLACEMENT + ret;
          break;
        }

      // check if filename is prefixed with an illegal prefix
      // Note: we can use the default locale, since all fixed names are pure
      // ANSI names
      final String sUCFilename = ret.toUpperCase (SystemHelper.getSystemLocale ());
      for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
        if (sUCFilename.startsWith (sIllegalPrefix + "."))
        {
          ret = ILLEGAL_FILENAME_CHAR_REPLACEMENT + ret;
          break;
        }
    }

    // Avoid returning an empty string as valid file name
    return StringHelper.hasNoText (ret) ? null : ret;
  }

  /**
   * Check if the passed character is secure to be used in filenames. Therefore
   * it must be &ge; 0x20 and &lt; 0x80.
   *
   * @param c
   *        The character to check
   * @return <code>true</code> if it is valid, <code>false</code> if not
   */
  public static boolean isSecureFilenameCharacter (final char c)
  {
    return c >= 0x20 && c < 0x80;
  }

  /**
   * Replace all non-ASCII characters from the filename (e.g. German Umlauts)
   * with underscores. Before replacing non-ASCII characters the filename is
   * made valid using {@link #getAsSecureValidFilename(String)}.
   *
   * @param sFilename
   *        Input file name. May not be <code>null</code>.
   * @return <code>null</code> if the input filename was <code>null</code>. The
   *         file name containing only ASCII characters. The returned value is
   *         never an empty String.
   * @see #getAsSecureValidASCIIFilename(String, char)
   */
  @Nullable
  @Nonempty
  public static String getAsSecureValidASCIIFilename (@Nullable final String sFilename)
  {
    return getAsSecureValidASCIIFilename (sFilename, ILLEGAL_FILENAME_CHAR_REPLACEMENT);
  }

  /**
   * Replace all non-ASCII characters from the filename (e.g. German Umlauts)
   * with a replacement char. Before replacing non-ASCII characters the filename
   * is made valid using {@link #getAsSecureValidFilename(String)}.
   *
   * @param sFilename
   *        Input file name. May not be <code>null</code>.
   * @param cReplacementChar
   *        The replacement character to be used for insecure filenames.
   * @return <code>null</code> if the input filename was <code>null</code>. The
   *         file name containing only ASCII characters. The returned value is
   *         never an empty String.
   * @see #getAsSecureValidASCIIFilename(String)
   * @see #getAsSecureValidFilename(String)
   * @see #isSecureFilenameCharacter(char)
   */
  @Nullable
  @Nonempty
  public static String getAsSecureValidASCIIFilename (@Nullable final String sFilename, final char cReplacementChar)
  {
    // Make it valid according to the general rules
    final String sValid = getAsSecureValidFilename (sFilename);
    if (sValid == null)
      return null;

    // Start replacing all non-ASCII characters with '_'
    final StringBuilder ret = new StringBuilder (sValid.length ());
    for (final char c : sValid.toCharArray ())
      if (isSecureFilenameCharacter (c))
        ret.append (c);
      else
        ret.append (cReplacementChar);
    return ret.toString ();
  }

  /**
   * Check if the passed character is a path separation character. This method
   * handles both Windows- and Unix-style path separation characters.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if the character is a path separation character,
   *         <code>false</code> otherwise.
   */
  public static boolean isPathSeparatorChar (final char c)
  {
    return c == UNIX_SEPARATOR || c == WINDOWS_SEPARATOR;
  }

  /**
   * Check if the passed character sequence starts with a path separation
   * character.
   *
   * @param s
   *        The character sequence to check. May be <code>null</code> or empty.
   * @return <code>true</code> if the character sequences starts with a Windows-
   *         or Unix-style path character.
   * @see #isPathSeparatorChar(char)
   */
  public static boolean startsWithPathSeparatorChar (@Nullable final CharSequence s)
  {
    return isPathSeparatorChar (StringHelper.getFirstChar (s));
  }

  /**
   * Check if the passed character sequence ends with a path separation
   * character.
   *
   * @param s
   *        The character sequence to check. May be <code>null</code> or empty.
   * @return <code>true</code> if the character sequences ends with a Windows-
   *         or Unix-style path character.
   * @see #isPathSeparatorChar(char)
   */
  public static boolean endsWithPathSeparatorChar (@Nullable final CharSequence s)
  {
    return isPathSeparatorChar (StringHelper.getLastChar (s));
  }

  /**
   * Check if the passed String contains at least one path separator char
   * (either Windows or Unix style).
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the passed string is not <code>null</code> and
   *         contains at least one separator.
   */
  public static boolean containsPathSeparatorChar (@Nullable final String s)
  {
    // This is a tick faster than iterating the s.toCharArray() chars
    return s != null && (s.indexOf (UNIX_SEPARATOR) >= 0 || s.indexOf (WINDOWS_SEPARATOR) >= 0);
  }

  /**
   * Check if the passed file is a system directory. A system directory is
   * either {@value #PATH_CURRENT} or {@value #PATH_PARENT}.
   *
   * @param aFile
   *        The file to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed file name (not the path) matches
   *         any of the special directory names, <code>false</code> of the
   *         passed file is <code>null</code> or does not denote a special
   *         directory.
   * @see #isSystemInternalDirectory(CharSequence)
   */
  public static boolean isSystemInternalDirectory (@Nullable final File aFile)
  {
    return aFile != null && isSystemInternalDirectory (aFile.getName ());
  }

  /**
   * Check if the passed string is a system directory. A system directory is
   * either {@value #PATH_CURRENT} or {@value #PATH_PARENT}.
   *
   * @param s
   *        The value to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed string matches any of the special
   *         directory names, <code>false</code> of the passed string is
   *         <code>null</code> or does not denote a special directory.
   * @see #isSystemInternalDirectory(File)
   */
  public static boolean isSystemInternalDirectory (@Nullable final CharSequence s)
  {
    return s != null && (s.equals (PATH_CURRENT) || s.equals (PATH_PARENT));
  }

  /**
   * Check if the passed file is an UNC path. UNC paths are identified by
   * starting with "//" or "\\".
   *
   * @param aFile
   *        The file to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isUNCPath(String)
   */
  public static boolean isUNCPath (@Nonnull final File aFile)
  {
    final String sPath = aFile.getAbsolutePath ();
    return isUNCPath (sPath);
  }

  /**
   * Check if the passed file is an UNC path. UNC paths are identified by
   * starting with "//" or "\\".
   *
   * @param sFilename
   *        The absolute filename to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isUNCPath(File)
   */
  public static boolean isUNCPath (@Nonnull final String sFilename)
  {
    return sFilename.startsWith (WINDOWS_UNC_PREFIX) || sFilename.startsWith (UNIX_UNC_PREFIX);
  }

  /**
   * Check if the passed file is a Windows local UNC path. This type is
   * identified by starting with "\\?\" or "\\.\".
   *
   * @param aFile
   *        The file to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isWindowsLocalUNCPath(String)
   */
  public static boolean isWindowsLocalUNCPath (@Nonnull final File aFile)
  {
    final String sPath = aFile.getAbsolutePath ();
    return isWindowsLocalUNCPath (sPath);
  }

  /**
   * Check if the passed file is a Windows local UNC path. This type is
   * identified by starting with "\\?\" or "\\.\".
   *
   * @param sFilename
   *        The absolute filename to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to a Windows local UNC path,
   *         <code>false</code> if not.
   * @see #isWindowsLocalUNCPath(File)
   */
  public static boolean isWindowsLocalUNCPath (@Nonnull final String sFilename)
  {
    return sFilename.startsWith (WINDOWS_UNC_PREFIX_LOCAL1) || sFilename.startsWith (WINDOWS_UNC_PREFIX_LOCAL2);
  }

  /**
   * Get a clean path of the passed file resolving all "." and ".." paths.<br>
   * Note: in case {@link FileHelper#getCanonicalPath(File)} fails,
   * {@link #getCleanPath(String)} is used as a fallback.<br>
   * Note 2: no cleansing operations beside "." and ".." are returned. You need
   * to ensure yourself, that the returned file name is valid!
   *
   * @param aFile
   *        The file to be cleaned. May not be <code>null</code>.
   * @return The cleaned path and never <code>null</code>.
   * @see #getPathUsingUnixSeparator(String)
   * @see #getCleanPath(String)
   */
  @Nonnull
  public static String getCleanPath (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    // getCanoncialPath fails for Windows local UNC paths
    if (!isUNCPath (aFile))
      try
      {
        // This works only if the file exists
        // Note: getCanonicalPath may be a bottleneck on Unix based file
        // systems!
        return getPathUsingUnixSeparator (FileHelper.getCanonicalPath (aFile));
      }
      catch (final IOException ex)
      {
        // Use our method
        s_aLogger.warn ("Using getCleanPath on an invalid file '" + aFile + "'", ex);
      }

    // Fallback: do it manually
    return getCleanPath (aFile.getAbsolutePath ());
  }

  /**
   * Clean the path by removing all ".." and "." path elements.
   *
   * @param sPath
   *        The path to be cleaned.
   * @return The cleaned path or <code>null</code> if the input parameter was
   *         <code>null</code>.
   * @see #getSecureFilename(String)
   * @see #getPathUsingUnixSeparator(String)
   */
  @Nullable
  public static String getCleanPath (@Nullable final String sPath)
  {
    if (sPath == null)
      return null;

    // Remove all relative paths and insecure characters
    String sPrefix = "";
    String sPathToUse = getSecureFilename (sPath);

    boolean bForceWindowsSeparator = false;
    if (isUNCPath (sPathToUse))
    {
      // It's an UNC path
      sPrefix += sPathToUse.substring (0, 2);
      sPathToUse = sPathToUse.substring (2);
      bForceWindowsSeparator = sPrefix.startsWith (WINDOWS_SEPARATOR_STR);
    }

    // Strip prefix from path to analyze, to not treat it as part of the
    // first path element. This is necessary to correctly parse paths like
    // "file://core/../core/io/Resource.class", where the ".." should just
    // strip the first "core" directory while keeping the "file:" prefix.
    // The same applies to http:// addresses where the domain should be kept!
    final int nProtoIdx = sPathToUse.indexOf ("://");
    boolean bPrefixIsAbsolute = false;
    if (nProtoIdx > -1)
    {
      // Keep protocol and server name

      // Start searching for the first slash after "://" (length=3)
      final int nPrefixIndex = sPathToUse.indexOf ('/', nProtoIdx + 3);
      if (nPrefixIndex >= 0)
      {
        sPrefix += sPathToUse.substring (0, nPrefixIndex + 1);
        sPathToUse = sPathToUse.substring (nPrefixIndex + 1);
        bPrefixIsAbsolute = true;
      }
      else
      {
        // We have e.g. a URL like "http://www.helger.com"
        // -> Nothing to
        return sPathToUse;
      }
    }
    else
    {
      // Keep volume or protocol prefix
      final int nPrefixIndex = sPathToUse.indexOf (':');
      if (nPrefixIndex >= 0)
      {
        sPrefix += sPathToUse.substring (0, nPrefixIndex + 1);
        sPathToUse = sPathToUse.substring (nPrefixIndex + 1);
        bPrefixIsAbsolute = true;
      }
    }

    // Unify all remaining path separators to be "/"
    // Unify after the prefixes where removed
    sPathToUse = getPathUsingUnixSeparator (sPathToUse);

    // Is it an absolute Path?
    if (StringHelper.startsWith (sPathToUse, UNIX_SEPARATOR))
    {
      // If no other prefix is present yet, this seems to be an absolute path!
      if (sPrefix.length () == 0)
        bPrefixIsAbsolute = true;

      sPrefix += bForceWindowsSeparator ? WINDOWS_SEPARATOR : UNIX_SEPARATOR;
      sPathToUse = sPathToUse.substring (1);
    }

    // Start splitting into paths
    final List <String> aElements = new ArrayList <String> ();
    int nParentFolders = 0;
    final String [] aPathArray = StringHelper.getExplodedArray (UNIX_SEPARATOR, sPathToUse);

    // from right to left
    for (int i = aPathArray.length - 1; i >= 0; i--)
    {
      final String sElement = aPathArray[i];

      // Empty paths and paths reflecting the current directory (".") are
      // ignored
      if (sElement.length () == 0 || PATH_CURRENT.equals (sElement))
        continue;

      if (PATH_PARENT.equals (sElement))
      {
        // Remember that we have a parent path to skip
        nParentFolders++;
      }
      else
        if (nParentFolders > 0)
        {
          // Merging path element with element corresponding to top path.
          nParentFolders--;
        }
        else
        {
          // Normal path element found.
          aElements.add (0, sElement);
        }
    }

    if (!bPrefixIsAbsolute)
    {
      // Remaining top paths need to be retained.
      for (int i = 0; i < nParentFolders; i++)
        aElements.add (0, PATH_PARENT);
    }

    return sPrefix + StringHelper.getImploded (bForceWindowsSeparator ? WINDOWS_SEPARATOR : UNIX_SEPARATOR, aElements);
  }

  /**
   * Concatenate a base URL and a sub path incl. the path cleansing. More or
   * less the same as calling <code>getCleanPath (sURL + "/" + sPath)</code>
   *
   * @param sURL
   *        The base URL. May not be <code>null</code>.
   * @param sPath
   *        The path to append. May not be <code>null</code>.
   * @return The combined, cleaned path.
   * @see #getCleanPath(String)
   */
  @Nonnull
  public static String getCleanConcatenatedUrlPath (@Nonnull final String sURL, @Nonnull final String sPath)
  {
    ValueEnforcer.notNull (sURL, "URL");
    ValueEnforcer.notNull (sPath, "Path");

    // If nothing is to be appended, just clean the base URL
    if (StringHelper.hasNoText (sPath))
      return getCleanPath (sURL);

    final String sRealURL = StringHelper.endsWith (sURL, UNIX_SEPARATOR) ? sURL : sURL + UNIX_SEPARATOR;
    final String sRealPath = StringHelper.startsWith (sPath, UNIX_SEPARATOR) ? sPath.substring (1) : sPath;
    return getCleanPath (sRealURL + sRealPath);
  }

  /**
   * Ensure that the passed path starts with a directory separator character. If
   * the passed path starts with either {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} no changes are performed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to start with the directory separator of
   *         the current operating system.
   * @see #startsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathStartingWithSeparator (@Nullable final String sPath)
  {
    return sPath == null ? null : startsWithPathSeparatorChar (sPath) ? sPath : File.separator + sPath;
  }

  /**
   * Ensure that the passed path does NOT end with a directory separator
   * character. Any number of trailing {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} are removed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to NOT end with the directory separator.
   * @see #endsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathEndingWithoutSeparator (@Nullable final String sPath)
  {
    if (sPath == null)
      return null;

    String sRet = sPath;
    while (endsWithPathSeparatorChar (sRet))
      sRet = sRet.substring (0, sRet.length () - 1);
    return sRet;
  }

  /**
   * Ensure that the passed path ends with a directory separator character. If
   * the passed path ends with either {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} no changes are performed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to end with the directory separator of the
   *         current operating system.
   * @see #endsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathEndingWithSeparator (@Nullable final String sPath)
  {
    return sPath == null ? null : endsWithPathSeparatorChar (sPath) ? sPath : sPath + File.separator;
  }

  /**
   * Tries to express the passed file path relative to the passed parent
   * directory. If the parent directory is null or not actually a parent of the
   * passed file, the passed file name will be returned unchanged.
   *
   * @param aFile
   *        The file which is to be described relatively. May not be
   *        <code>null</code>.
   * @param aParentDirectory
   *        The parent directory of the file to which the relative path
   *        expression will relate to. May be <code>null</code>.
   * @return The relative path or the unchanged absolute file path using Unix
   *         path separators instead of Operating System dependent separator. Or
   *         <code>null</code> if the passed file contains a path traversal at
   *         the beginning
   * @see #getCleanPath(File)
   * @see #startsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  public static String getRelativeToParentDirectory (@Nonnull final File aFile, @Nullable final File aParentDirectory)
  {
    ValueEnforcer.notNull (aFile, "File");

    final String sCleanedFile = getCleanPath (aFile);
    if (aParentDirectory == null)
      return sCleanedFile;

    String sRelative = StringHelper.trimStart (sCleanedFile, getCleanPath (aParentDirectory));
    if (sRelative.equals (sCleanedFile))
    {
      // The passed file contains a path traversal!
      return null;
    }

    if (startsWithPathSeparatorChar (sRelative))
    {
      // Ignore any leading path separator char
      sRelative = sRelative.substring (1);
    }
    return sRelative;
  }

  /**
   * Get a concatenated absolute path consisting of the parent directory and the
   * file path. It is ensured that the resulting (cleaned) filename is still the
   * same or a child of the passed parent directory. If the file path contains
   * some directory traversal elements (e.g. starting with "..")
   * <code>null</code> is returned.
   *
   * @param aParentDirectory
   *        The parent directory to be ensured. May not be <code>null</code>.
   * @param sFilePath
   *        The file path to be appended to the passed parent directory. May not
   *        be <code>null</code>.
   * @return <code>null</code> if the parent directory would be changed with the
   *         passed file path - the concatenated cleaned path otherwise (using
   *         Unix separators).
   * @see #getRelativeToParentDirectory(File, File)
   * @see #getCleanPath(File)
   */
  @Nullable
  public static String getAbsoluteWithEnsuredParentDirectory (@Nonnull final File aParentDirectory,
                                                              @Nonnull final String sFilePath)
  {
    ValueEnforcer.notNull (aParentDirectory, "ParentDirectory");
    ValueEnforcer.notNull (sFilePath, "FilePath");

    final File aSubFile = new File (sFilePath);
    String sRelativeSubPath = sFilePath;
    // if sub path is absolute, must contain parent path!
    if (aSubFile.isAbsolute ())
    {
      if (!aParentDirectory.isAbsolute ())
      {
        s_aLogger.error ("Cannot express absolute child file ('" +
                         aSubFile +
                         "') relative to a relative parent file ('" +
                         aParentDirectory +
                         "')!");
        return null;
      }
      sRelativeSubPath = getRelativeToParentDirectory (aSubFile, aParentDirectory);
    }
    if (sRelativeSubPath == null)
      return null;

    // Merge the files together as usual, and clean all "." and ".." up
    final String sCleanParentPath = getCleanPath (aParentDirectory);
    final String sEstimatedPath = getCleanPath (new File (sCleanParentPath, sRelativeSubPath));
    if (!sEstimatedPath.startsWith (sCleanParentPath))
    {
      // Seems like there is a path traversal at the beginning of the passed
      // file path
      return null;
    }
    return sEstimatedPath;
  }

  /**
   * Check if the passed filename is a Unix hidden filename.
   *
   * @param aFile
   *        The file to check. May be <code>null</code>.
   * @return <code>true</code> if the file is not <code>null</code> and the name
   *         starts with a dot.
   * @see #isHiddenFilename(String)
   */
  public static boolean isHiddenFilename (@Nullable final File aFile)
  {
    return aFile != null && isHiddenFilename (aFile.getName ());
  }

  /**
   * Check if the passed filename is a Unix hidden filename.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>true</code> if the filename is neither <code>null</code> nor
   *         empty and starts with a dot.
   * @see #isHiddenFilename(File)
   */
  public static boolean isHiddenFilename (@Nullable final String sFilename)
  {
    return StringHelper.hasText (sFilename) && sFilename.charAt (0) == HIDDEN_FILE_PREFIX;
  }
}
