/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.util.mime;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.hashcode.IHashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.mime.IMimeType;
import com.helger.mime.parse.MimeTypeParser;
import com.helger.mime.parse.MimeTypeParserException;

/**
 * Represents a single MIME type with information relevant for us.
 *
 * @author Philip Helger
 */
@Immutable
public final class MimeTypeInfo
{
  /**
   * A MIME type together with its optional source information.
   *
   * @author Philip Helger
   */
  @Immutable
  public static final class MimeTypeWithSource
  {
    private final IMimeType m_aMimeType;
    private final String m_sSource;
    // status vars
    private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

    /**
     * Constructor.
     *
     * @param sMimeType
     *        The MIME type string to parse. May not be <code>null</code>.
     * @throws MimeTypeParserException
     *         If the MIME type string cannot be parsed.
     */
    public MimeTypeWithSource (@NonNull final String sMimeType) throws MimeTypeParserException
    {
      this (MimeTypeParser.parseMimeType (sMimeType), (String) null);
    }

    /**
     * Constructor.
     *
     * @param aMimeType
     *        The MIME type. May not be <code>null</code>.
     */
    public MimeTypeWithSource (@NonNull final IMimeType aMimeType)
    {
      this (aMimeType, (String) null);
    }

    /**
     * Constructor.
     *
     * @param aMimeType
     *        The MIME type. May not be <code>null</code>.
     * @param sSource
     *        The optional source information. May be <code>null</code>.
     */
    public MimeTypeWithSource (@NonNull final IMimeType aMimeType, @Nullable final String sSource)
    {
      m_aMimeType = ValueEnforcer.notNull (aMimeType, "MimeType");
      m_sSource = sSource;
    }

    /**
     * @return The contained MIME type. Never <code>null</code>.
     */
    @NonNull
    public IMimeType getMimeType ()
    {
      return m_aMimeType;
    }

    /**
     * @return The MIME type as a string. Never <code>null</code> nor empty.
     */
    @NonNull
    @Nonempty
    public String getMimeTypeAsString ()
    {
      return m_aMimeType.getAsString ();
    }

    /**
     * @return The optional source information. May be <code>null</code>.
     */
    @Nullable
    public String getSource ()
    {
      return m_sSource;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final MimeTypeWithSource rhs = (MimeTypeWithSource) o;
      return m_aMimeType.equals (rhs.m_aMimeType) && EqualsHelper.equals (m_sSource, rhs.m_sSource);
    }

    @Override
    public int hashCode ()
    {
      int ret = m_nHashCode;
      if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
        ret = m_nHashCode = new HashCodeGenerator (this).append (m_aMimeType).append (m_sSource).getHashCode ();
      return ret;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("mimeType", m_aMimeType)
                                         .appendIfNotNull ("source", m_sSource)
                                         .getToString ();
    }
  }

  /**
   * A file extension together with its optional source information.
   *
   * @author Philip Helger
   */
  @Immutable
  public static final class ExtensionWithSource
  {
    private final String m_sExt;
    private final String m_sSource;
    // status vars
    private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

    /**
     * Constructor.
     *
     * @param sExt
     *        The file extension. May not be <code>null</code>.
     */
    public ExtensionWithSource (@NonNull final String sExt)
    {
      this (sExt, (String) null);
    }

    /**
     * Constructor.
     *
     * @param sExt
     *        The file extension. May not be <code>null</code>.
     * @param sSource
     *        The optional source information. May be <code>null</code>.
     */
    public ExtensionWithSource (@NonNull final String sExt, @Nullable final String sSource)
    {
      m_sExt = ValueEnforcer.notNull (sExt, "Extension");
      m_sSource = sSource;
    }

    /**
     * @return The file extension. Never <code>null</code>.
     */
    @NonNull
    public String getExtension ()
    {
      return m_sExt;
    }

    /**
     * @return The optional source information. May be <code>null</code>.
     */
    @Nullable
    public String getSource ()
    {
      return m_sSource;
    }

    /**
     * Check if this extension matches the passed extension string.
     *
     * @param sExtension
     *        The extension to check. May neither be <code>null</code> nor empty.
     * @return <code>true</code> if the extension matches, <code>false</code> otherwise.
     */
    public boolean matches (@NonNull @Nonempty final String sExtension)
    {
      if (m_sExt.contains (sExtension))
        return true;

      // Especially on Windows, sometimes file extensions like "JPG" can be
      // found. Therefore also test for the lowercase version of the extension.
      if (m_sExt.contains (sExtension.toLowerCase (Locale.US)))
        return true;

      return false;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final ExtensionWithSource rhs = (ExtensionWithSource) o;
      return m_sExt.equals (rhs.m_sExt) && EqualsHelper.equals (m_sSource, rhs.m_sSource);
    }

    @Override
    public int hashCode ()
    {
      int ret = m_nHashCode;
      if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
        ret = m_nHashCode = new HashCodeGenerator (this).append (m_sExt).append (m_sSource).getHashCode ();
      return ret;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("extension", m_sExt)
                                         .appendIfNotNull ("source", m_sSource)
                                         .getToString ();
    }
  }

  private final ICommonsOrderedSet <MimeTypeWithSource> m_aMimeTypes;
  private final String m_sComment;
  private final ICommonsOrderedSet <String> m_aParentTypes;
  private final ICommonsOrderedSet <String> m_aGlobs;
  private final ICommonsOrderedSet <ExtensionWithSource> m_aExtensions;
  private final String m_sSource;

  /**
   * Constructor.
   *
   * @param aMimeTypes
   *        The set of MIME types. May neither be <code>null</code> nor empty.
   * @param sComment
   *        An optional comment. May be <code>null</code>.
   * @param aParentTypes
   *        The set of parent types. May not be <code>null</code>.
   * @param aGlobs
   *        The set of globs. May not be <code>null</code>.
   * @param aExtensions
   *        The set of file extensions. May not be <code>null</code>.
   * @param sSource
   *        An optional source. May be <code>null</code>.
   */
  public MimeTypeInfo (@NonNull @Nonempty final ICommonsOrderedSet <MimeTypeWithSource> aMimeTypes,
                       @Nullable final String sComment,
                       @NonNull final ICommonsOrderedSet <String> aParentTypes,
                       @NonNull final ICommonsOrderedSet <String> aGlobs,
                       @NonNull final ICommonsOrderedSet <ExtensionWithSource> aExtensions,
                       @Nullable final String sSource)
  {
    ValueEnforcer.notEmptyNoNullValue (aMimeTypes, "MimeTypes");
    ValueEnforcer.notNull (aParentTypes, "ParentTypes");
    ValueEnforcer.notNull (aGlobs, "Globs");
    ValueEnforcer.notNull (aExtensions, "Extensions");
    m_aMimeTypes = aMimeTypes.getClone ();
    m_sComment = sComment;
    m_aParentTypes = aParentTypes.getClone ();
    m_aGlobs = aGlobs.getClone ();
    m_aExtensions = aExtensions.getClone ();
    m_sSource = sSource;
  }

  /**
   * @return A mutable copy of all MIME types with source information. Never <code>null</code> nor
   *         empty.
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <MimeTypeWithSource> getAllMimeTypesWithSource ()
  {
    return m_aMimeTypes.getClone ();
  }

  /**
   * @return A mutable copy of all MIME types. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <IMimeType> getAllMimeTypes ()
  {
    return m_aMimeTypes.getAllMapped (MimeTypeWithSource::getMimeType);
  }

  /**
   * @return A mutable copy of all MIME type strings. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllMimeTypeStrings ()
  {
    return m_aMimeTypes.getAllMapped (MimeTypeWithSource::getMimeTypeAsString);
  }

  /**
   * Check if this info contains the specified MIME type.
   *
   * @param aMimeType
   *        The MIME type to check. May be <code>null</code>.
   * @return <code>true</code> if the MIME type is contained.
   */
  public boolean containsMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType != null)
      for (final MimeTypeWithSource aItem : m_aMimeTypes)
        if (aItem.getMimeType ().equals (aMimeType))
          return true;
    return false;
  }

  /**
   * Check if this info contains the specified MIME type string.
   *
   * @param sMimeType
   *        The MIME type string to check. May be <code>null</code>.
   * @return <code>true</code> if the MIME type string is contained.
   */
  public boolean containsMimeType (@Nullable final String sMimeType)
  {
    if (StringHelper.isNotEmpty (sMimeType))
      for (final MimeTypeWithSource aItem : m_aMimeTypes)
        if (aItem.getMimeTypeAsString ().equals (sMimeType))
          return true;
    return false;
  }

  /**
   * @return The primary (first) MIME type with source information. Never <code>null</code>.
   */
  @NonNull
  public MimeTypeWithSource getPrimaryMimeTypeWithSource ()
  {
    return m_aMimeTypes.getFirst ();
  }

  /**
   * @return The primary (first) MIME type. Never <code>null</code>.
   */
  @NonNull
  public IMimeType getPrimaryMimeType ()
  {
    return getPrimaryMimeTypeWithSource ().getMimeType ();
  }

  /**
   * @return The primary (first) MIME type as a string. Never <code>null</code>.
   */
  @NonNull
  public String getPrimaryMimeTypeString ()
  {
    return getPrimaryMimeTypeWithSource ().getMimeTypeAsString ();
  }

  /**
   * @return The optional comment. May be <code>null</code>.
   */
  @Nullable
  public String getComment ()
  {
    return m_sComment;
  }

  /**
   * @return <code>true</code> if a comment is present.
   */
  public boolean hasComment ()
  {
    return StringHelper.isNotEmpty (m_sComment);
  }

  /**
   * @return A mutable copy of all parent types. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllParentTypes ()
  {
    return m_aParentTypes.getClone ();
  }

  /**
   * @return <code>true</code> if at least one parent type is present.
   */
  public boolean hasAnyParentType ()
  {
    return !m_aParentTypes.isEmpty ();
  }

  /**
   * @return A mutable copy of all glob patterns. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllGlobs ()
  {
    return m_aGlobs.getClone ();
  }

  /**
   * @return The primary (first) glob pattern. May be <code>null</code> if no globs are present.
   */
  @Nullable
  public String getPrimaryGlob ()
  {
    return m_aGlobs.getFirst ();
  }

  /**
   * @return <code>true</code> if at least one glob pattern is present.
   */
  public boolean hasAnyGlob ()
  {
    return m_aGlobs.isNotEmpty ();
  }

  /**
   * @return A mutable copy of all extensions with source information. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <ExtensionWithSource> getAllExtensionsWithSource ()
  {
    return m_aExtensions.getClone ();
  }

  /**
   * @return A mutable copy of all extension strings. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllExtensions ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    ret.addAllMapped (m_aExtensions, ExtensionWithSource::getExtension);
    return ret;
  }

  /**
   * @return The primary (first) extension with source information. May be <code>null</code> if no
   *         extensions are present.
   */
  @Nullable
  public ExtensionWithSource getPrimaryExtensionWithSource ()
  {
    return m_aExtensions.getFirst ();
  }

  /**
   * @return The primary (first) extension string. May be <code>null</code> if no extensions are
   *         present.
   */
  @Nullable
  public String getPrimaryExtension ()
  {
    final ExtensionWithSource aExtension = getPrimaryExtensionWithSource ();
    return aExtension == null ? null : aExtension.getExtension ();
  }

  /**
   * @return <code>true</code> if at least one extension is present.
   */
  public boolean hasAnyExtension ()
  {
    return m_aExtensions.isNotEmpty ();
  }

  /**
   * Check if the specified extension is contained.
   *
   * @param sExtension
   *        The extension to check. May be <code>null</code>.
   * @return <code>true</code> if the extension is contained.
   */
  public boolean containsExtension (@Nullable final String sExtension)
  {
    if (StringHelper.isNotEmpty (sExtension))
      for (final ExtensionWithSource aExtension : m_aExtensions)
        if (aExtension.matches (sExtension))
          return true;
    return false;
  }

  void addExtension (@NonNull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aExt, "Ext");
    // Don't add to glob - can easily be constructed from all extensions
    m_aExtensions.add (aExt);
  }

  void addMimeType (@NonNull final MimeTypeWithSource aMimeType)
  {
    ValueEnforcer.notNull (aMimeType, "MimeType");
    // Don't add to glob - can easily be constructed from all extensions
    m_aMimeTypes.add (aMimeType);
  }

  /**
   * @return The optional source information. May be <code>null</code>.
   */
  @Nullable
  public String getSource ()
  {
    return m_sSource;
  }

  /**
   * @return <code>true</code> if source information is present.
   */
  public boolean hasSource ()
  {
    return StringHelper.isNotEmpty (m_sSource);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MimeTypeInfo rhs = (MimeTypeInfo) o;
    return m_aMimeTypes.equals (rhs.m_aMimeTypes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMimeTypes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("mimeTypes", m_aMimeTypes)
                                       .appendIfNotNull ("comment", m_sComment)
                                       .appendIf ("parentTypes", m_aParentTypes, CollectionHelper::isNotEmpty)
                                       .appendIf ("globs", m_aGlobs, CollectionHelper::isNotEmpty)
                                       .appendIf ("extensions", m_aExtensions, CollectionHelper::isNotEmpty)
                                       .appendIfNotNull ("source", m_sSource)
                                       .getToString ();
  }
}
