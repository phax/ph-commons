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
package com.helger.commons.mime;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
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

    public MimeTypeWithSource (@Nonnull final String sMimeType)
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
      return new HashCodeGenerator (this).append (m_aMimeType).append (m_sSource).getHashCode ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("mimeType", m_aMimeType)
                                         .appendIfNotNull ("source", m_sSource)
                                         .toString ();
    }
  }

  @Immutable
  public static final class ExtensionWithSource
  {
    private final String m_sExt;
    private final String m_sSource;

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
      return new HashCodeGenerator (this).append (m_sExt).append (m_sSource).getHashCode ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("extension", m_sExt)
                                         .appendIfNotNull ("source", m_sSource)
                                         .toString ();
    }
  }

  private final Set <MimeTypeWithSource> m_aMimeTypes;
  private final String m_sComment;
  private final Set <String> m_aParentTypes;
  private final Set <String> m_aGlobs;
  private final Set <ExtensionWithSource> m_aExtensions;
  private final String m_sSource;

  public MimeTypeInfo (@Nonnull @Nonempty final Set <MimeTypeWithSource> aMimeTypes,
                       @Nullable final String sComment,
                       @Nonnull final Set <String> aParentTypes,
                       @Nonnull final Set <String> aGlobs,
                       @Nonnull final Set <ExtensionWithSource> aExtensions,
                       @Nullable final String sSource)
  {
    ValueEnforcer.notEmptyNoNullValue (aMimeTypes, "MimeTypes");
    ValueEnforcer.notNull (aParentTypes, "ParentTypes");
    ValueEnforcer.notNull (aGlobs, "Globs");
    ValueEnforcer.notNull (aExtensions, "Extensions");
    m_aMimeTypes = CollectionHelper.newOrderedSet (aMimeTypes);
    m_sComment = sComment;
    m_aParentTypes = CollectionHelper.newOrderedSet (aParentTypes);
    m_aGlobs = CollectionHelper.newOrderedSet (aGlobs);
    m_aExtensions = CollectionHelper.newOrderedSet (aExtensions);
    m_sSource = sSource;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public Set <MimeTypeWithSource> getAllMimeTypesWithSource ()
  {
    return CollectionHelper.newOrderedSet (m_aMimeTypes);
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public Set <IMimeType> getAllMimeTypes ()
  {
    final Set <IMimeType> ret = new LinkedHashSet <IMimeType> ();
    for (final MimeTypeWithSource aItem : m_aMimeTypes)
      ret.add (aItem.getMimeType ());
    return ret;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public Set <String> getAllMimeTypeStrings ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    for (final MimeTypeWithSource aItem : m_aMimeTypes)
      ret.add (aItem.getMimeTypeAsString ());
    return ret;
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
    return CollectionHelper.getFirstElement (m_aMimeTypes);
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
  public Set <String> getAllParentTypes ()
  {
    return CollectionHelper.newOrderedSet (m_aParentTypes);
  }

  public boolean hasAnyParentType ()
  {
    return !m_aParentTypes.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllGlobs ()
  {
    return CollectionHelper.newOrderedSet (m_aGlobs);
  }

  @Nullable
  public String getPrimaryGlob ()
  {
    return CollectionHelper.getFirstElement (m_aGlobs);
  }

  public boolean hasAnyGlob ()
  {
    return !m_aGlobs.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <ExtensionWithSource> getAllExtensionsWithSource ()
  {
    return CollectionHelper.newOrderedSet (m_aExtensions);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllExtensions ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    for (final ExtensionWithSource aItem : m_aExtensions)
      ret.add (aItem.getExtension ());
    return ret;
  }

  @Nullable
  public ExtensionWithSource getPrimaryExtensionWithSource ()
  {
    return CollectionHelper.getFirstElement (m_aExtensions);
  }

  @Nullable
  public String getPrimaryExtension ()
  {
    final ExtensionWithSource aExtension = getPrimaryExtensionWithSource ();
    return aExtension == null ? null : aExtension.getExtension ();
  }

  public boolean hasAnyExtension ()
  {
    return !m_aExtensions.isEmpty ();
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
                                       .appendIfNotEmpty ("parentTypes", m_aParentTypes)
                                       .appendIfNotEmpty ("globs", m_aGlobs)
                                       .appendIfNotEmpty ("extensions", m_aExtensions)
                                       .appendIfNotNull ("source", m_sSource)
                                       .toString ();
  }
}
