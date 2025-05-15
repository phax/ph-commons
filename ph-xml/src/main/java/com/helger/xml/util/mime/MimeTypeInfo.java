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
package com.helger.xml.util.mime;

import java.util.Locale;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.hashcode.IHashCodeGenerator;
import com.helger.commons.mime.IMimeType;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.mime.MimeTypeParserException;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single MIME type with information relevant for us.
 *
 * @author Philip Helger
 */
@Immutable
public final class MimeTypeInfo
{
  @Immutable
  public static final class MimeTypeWithSource
  {
    private final IMimeType m_aMimeType;
    private final String m_sSource;
    // status vars
    private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

    public MimeTypeWithSource (@Nonnull final String sMimeType) throws MimeTypeParserException
    {
      this (MimeTypeParser.parseMimeType (sMimeType), (String) null);
    }

    public MimeTypeWithSource (@Nonnull final IMimeType aMimeType)
    {
      this (aMimeType, (String) null);
    }

    public MimeTypeWithSource (@Nonnull final IMimeType aMimeType, @Nullable final String sSource)
    {
      m_aMimeType = ValueEnforcer.notNull (aMimeType, "MimeType");
      m_sSource = sSource;
    }

    @Nonnull
    public IMimeType getMimeType ()
    {
      return m_aMimeType;
    }

    @Nonnull
    @Nonempty
    public String getMimeTypeAsString ()
    {
      return m_aMimeType.getAsString ();
    }

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
      return new ToStringGenerator (this).append ("mimeType", m_aMimeType).appendIfNotNull ("source", m_sSource).getToString ();
    }
  }

  @Immutable
  public static final class ExtensionWithSource
  {
    private final String m_sExt;
    private final String m_sSource;
    // status vars
    private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

    public ExtensionWithSource (@Nonnull final String sExt)
    {
      this (sExt, (String) null);
    }

    public ExtensionWithSource (@Nonnull final String sExt, @Nullable final String sSource)
    {
      m_sExt = ValueEnforcer.notNull (sExt, "Extension");
      m_sSource = sSource;
    }

    @Nonnull
    public String getExtension ()
    {
      return m_sExt;
    }

    @Nullable
    public String getSource ()
    {
      return m_sSource;
    }

    public boolean matches (@Nonnull @Nonempty final String sExtension)
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
      return new ToStringGenerator (this).append ("extension", m_sExt).appendIfNotNull ("source", m_sSource).getToString ();
    }
  }

  private final ICommonsOrderedSet <MimeTypeWithSource> m_aMimeTypes;
  private final String m_sComment;
  private final ICommonsOrderedSet <String> m_aParentTypes;
  private final ICommonsOrderedSet <String> m_aGlobs;
  private final ICommonsOrderedSet <ExtensionWithSource> m_aExtensions;
  private final String m_sSource;

  public MimeTypeInfo (@Nonnull @Nonempty final ICommonsOrderedSet <MimeTypeWithSource> aMimeTypes,
                       @Nullable final String sComment,
                       @Nonnull final ICommonsOrderedSet <String> aParentTypes,
                       @Nonnull final ICommonsOrderedSet <String> aGlobs,
                       @Nonnull final ICommonsOrderedSet <ExtensionWithSource> aExtensions,
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

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <MimeTypeWithSource> getAllMimeTypesWithSource ()
  {
    return m_aMimeTypes.getClone ();
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <IMimeType> getAllMimeTypes ()
  {
    return m_aMimeTypes.getAllMapped (MimeTypeWithSource::getMimeType);
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllMimeTypeStrings ()
  {
    return m_aMimeTypes.getAllMapped (MimeTypeWithSource::getMimeTypeAsString);
  }

  public boolean containsMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType != null)
      for (final MimeTypeWithSource aItem : m_aMimeTypes)
        if (aItem.getMimeType ().equals (aMimeType))
          return true;
    return false;
  }

  public boolean containsMimeType (@Nullable final String sMimeType)
  {
    if (StringHelper.hasText (sMimeType))
      for (final MimeTypeWithSource aItem : m_aMimeTypes)
        if (aItem.getMimeTypeAsString ().equals (sMimeType))
          return true;
    return false;
  }

  @Nonnull
  public MimeTypeWithSource getPrimaryMimeTypeWithSource ()
  {
    return m_aMimeTypes.getFirst ();
  }

  @Nonnull
  public IMimeType getPrimaryMimeType ()
  {
    return getPrimaryMimeTypeWithSource ().getMimeType ();
  }

  @Nonnull
  public String getPrimaryMimeTypeString ()
  {
    return getPrimaryMimeTypeWithSource ().getMimeTypeAsString ();
  }

  @Nullable
  public String getComment ()
  {
    return m_sComment;
  }

  public boolean hasComment ()
  {
    return StringHelper.hasText (m_sComment);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllParentTypes ()
  {
    return m_aParentTypes.getClone ();
  }

  public boolean hasAnyParentType ()
  {
    return !m_aParentTypes.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllGlobs ()
  {
    return m_aGlobs.getClone ();
  }

  @Nullable
  public String getPrimaryGlob ()
  {
    return m_aGlobs.getFirst ();
  }

  public boolean hasAnyGlob ()
  {
    return m_aGlobs.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <ExtensionWithSource> getAllExtensionsWithSource ()
  {
    return m_aExtensions.getClone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllExtensions ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    ret.addAllMapped (m_aExtensions, ExtensionWithSource::getExtension);
    return ret;
  }

  @Nullable
  public ExtensionWithSource getPrimaryExtensionWithSource ()
  {
    return m_aExtensions.getFirst ();
  }

  @Nullable
  public String getPrimaryExtension ()
  {
    final ExtensionWithSource aExtension = getPrimaryExtensionWithSource ();
    return aExtension == null ? null : aExtension.getExtension ();
  }

  public boolean hasAnyExtension ()
  {
    return m_aExtensions.isNotEmpty ();
  }

  public boolean containsExtension (@Nullable final String sExtension)
  {
    if (StringHelper.hasText (sExtension))
      for (final ExtensionWithSource aExtension : m_aExtensions)
        if (aExtension.matches (sExtension))
          return true;
    return false;
  }

  void addExtension (@Nonnull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aExt, "Ext");
    // Don't add to glob - can easily be constructed from all extensions
    m_aExtensions.add (aExt);
  }

  void addMimeType (@Nonnull final MimeTypeWithSource aMimeType)
  {
    ValueEnforcer.notNull (aMimeType, "MimeType");
    // Don't add to glob - can easily be constructed from all extensions
    m_aMimeTypes.add (aMimeType);
  }

  @Nullable
  public String getSource ()
  {
    return m_sSource;
  }

  public boolean hasSource ()
  {
    return StringHelper.hasText (m_sSource);
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
