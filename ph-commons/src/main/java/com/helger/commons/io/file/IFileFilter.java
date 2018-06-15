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
import java.io.FileFilter;
import java.io.FilenameFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.functional.IPredicate;
import com.helger.commons.regex.RegExHelper;

/**
 * Abstract interface that collects {@link FileFilter}, {@link FilenameFilter}
 * and {@link IPredicate}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IFileFilter extends FileFilter, FilenameFilter, IPredicate <File>
{
  default boolean accept (@Nullable final File aFile)
  {
    return test (aFile);
  }

  default boolean accept (@Nullable final File aDir, @Nullable final String sName)
  {
    if (sName == null)
      return false;

    final File aFileToCheck = aDir != null ? new File (aDir, sName) : new File (sName);
    return test (aFileToCheck);
  }

  /**
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter directoryOnly ()
  {
    return FileHelper::existsDir;
  }

  /**
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter directoryPublic ()
  {
    return aFile -> FileHelper.existsDir (aFile) && !FilenameHelper.isHiddenFilename (aFile);
  }

  /**
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter parentDirectoryPublic ()
  {
    return aFile -> {
      final File aParentFile = aFile != null ? aFile.getAbsoluteFile ().getParentFile () : null;
      return aParentFile != null && !FilenameHelper.isHiddenFilename (aParentFile);
    };
  }

  /**
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter fileOnly ()
  {
    return FileHelper::existsFile;
  }

  /**
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameHidden ()
  {
    return FilenameHelper::isHiddenFilename;
  }

  /**
   * @param sPrefix
   *        The extension to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameStartsWith (@Nonnull @Nonempty final String sPrefix)
  {
    ValueEnforcer.notEmpty (sPrefix, "Prefix");
    return aFile -> {
      if (aFile != null)
      {
        final String sSecureFilename = FilenameHelper.getSecureFilename (aFile.getName ());
        if (sSecureFilename != null)
          return sSecureFilename.startsWith (sPrefix);
      }
      return false;
    };
  }

  /**
   * @param sSuffix
   *        The suffix to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameEndsWith (@Nonnull @Nonempty final String sSuffix)
  {
    ValueEnforcer.notEmpty (sSuffix, "Suffix");
    return aFile -> {
      if (aFile != null)
      {
        final String sSecureFilename = FilenameHelper.getSecureFilename (aFile.getName ());
        if (sSecureFilename != null)
          return sSecureFilename.endsWith (sSuffix);
      }
      return false;
    };
  }

  /**
   * @param sFilename
   *        The filename to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameEquals (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");
    return aFile -> aFile != null && sFilename.equals (FilenameHelper.getSecureFilename (aFile.getName ()));
  }

  /**
   * @param sFilename
   *        The filename to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameEqualsIgnoreCase (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");
    return aFile -> aFile != null && sFilename.equalsIgnoreCase (FilenameHelper.getSecureFilename (aFile.getName ()));
  }

  /**
   * @param sFilename
   *        The filename to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameNotEquals (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");
    return aFile -> aFile != null && !sFilename.equals (FilenameHelper.getSecureFilename (aFile.getName ()));
  }

  /**
   * @param sFilename
   *        The filename to use. May neither be <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   */
  @Nonnull
  static IFileFilter filenameNotEqualsIgnoreCase (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");
    return aFile -> aFile != null && !sFilename.equalsIgnoreCase (FilenameHelper.getSecureFilename (aFile.getName ()));
  }

  /**
   * Create a file filter that matches, if it matches one of the provided
   * regular expressions
   *
   * @param aRegExs
   *        The regular expressions to match against. May neither be
   *        <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   * @see #filenameMatchNoRegEx(String...)
   * @see #filenameMatchAny(String...)
   * @see #filenameMatchNone(String...)
   */
  @Nonnull
  static IFileFilter filenameMatchAnyRegEx (@Nonnull @Nonempty final String... aRegExs)
  {
    ValueEnforcer.notEmpty (aRegExs, "RegularExpressions");
    return aFile -> {
      if (aFile != null)
      {
        final String sRealName = FilenameHelper.getSecureFilename (aFile.getName ());
        if (sRealName != null)
          for (final String sRegEx : aRegExs)
            if (RegExHelper.stringMatchesPattern (sRegEx, sRealName))
              return true;
      }
      return false;
    };
  }

  /**
   * Create a file filter that matches, if it matches none of the provided
   * regular expressions
   *
   * @param aRegExs
   *        The regular expressions to match against. May neither be
   *        <code>null</code> nor empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   * @see #filenameMatchAnyRegEx(String...)
   * @see #filenameMatchAny(String...)
   * @see #filenameMatchNone(String...)
   */
  @Nonnull
  static IFileFilter filenameMatchNoRegEx (@Nonnull @Nonempty final String... aRegExs)
  {
    ValueEnforcer.notEmpty (aRegExs, "RegularExpressions");
    return aFile -> {
      if (aFile == null)
        return false;
      final String sRealName = FilenameHelper.getSecureFilename (aFile.getName ());
      if (sRealName == null)
        return false;
      for (final String sRegEx : aRegExs)
        if (RegExHelper.stringMatchesPattern (sRegEx, sRealName))
          return false;
      return true;
    };
  }

  /**
   * Create a file filter that matches, if it matches one of the provided
   * filenames.
   *
   * @param aFilenames
   *        The names to match against. May neither be <code>null</code> nor
   *        empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   * @see #filenameMatchAnyRegEx(String...)
   * @see #filenameMatchNoRegEx(String...)
   * @see #filenameMatchNone(String...)
   */
  @Nonnull
  static IFileFilter filenameMatchAny (@Nonnull @Nonempty final String... aFilenames)
  {
    ValueEnforcer.notEmpty (aFilenames, "Filenames");
    return aFile -> {
      if (aFile != null)
      {
        final String sRealName = FilenameHelper.getSecureFilename (aFile.getName ());
        if (sRealName != null)
          for (final String sFilename : aFilenames)
            if (sFilename.equals (sRealName))
              return true;
      }
      return false;
    };
  }

  /**
   * Create a file filter that matches, if it matches none of the provided
   * filenames.
   *
   * @param aFilenames
   *        The filenames to match against. May neither be <code>null</code> nor
   *        empty.
   * @return The created {@link IFileFilter}. Never <code>null</code>.
   * @see #filenameMatchAnyRegEx(String...)
   * @see #filenameMatchNoRegEx(String...)
   * @see #filenameMatchAny(String...)
   */
  @Nonnull
  static IFileFilter filenameMatchNone (@Nonnull @Nonempty final String... aFilenames)
  {
    ValueEnforcer.notEmpty (aFilenames, "Filenames");
    return aFile -> {
      if (aFile == null)
        return false;
      final String sRealName = FilenameHelper.getSecureFilename (aFile.getName ());
      if (sRealName == null)
        return false;
      for (final String sFilename : aFilenames)
        if (sFilename.equals (sRealName))
          return false;
      return true;
    };
  }
}
