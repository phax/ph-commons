package com.helger.commons.mime;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
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
    private final MimeType m_aMimeType;
    private final String m_sSource;

    public MimeTypeWithSource (@Nonnull final String sMimeType)
    {
      this (MimeTypeParser.parseMimeType (sMimeType), (String) null);
    }

    public MimeTypeWithSource (@Nonnull final MimeType aMimeType)
    {
      this (aMimeType, (String) null);
    }

    public MimeTypeWithSource (@Nonnull final MimeType aMimeType, @Nullable final String sSource)
    {
      m_aMimeType = ValueEnforcer.notNull (aMimeType, "MimeType");
      m_sSource = sSource;
    }

    @Nonnull
    public MimeType getMimeType ()
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
      if (!(o instanceof MimeTypeWithSource))
        return false;
      final MimeTypeWithSource rhs = (MimeTypeWithSource) o;
      return m_aMimeType.equals (rhs.m_aMimeType) && EqualsUtils.equals (m_sSource, rhs.m_sSource);
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
      if (!(o instanceof ExtensionWithSource))
        return false;
      final ExtensionWithSource rhs = (ExtensionWithSource) o;
      return m_sExt.equals (rhs.m_sExt) && EqualsUtils.equals (m_sSource, rhs.m_sSource);
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
    m_aMimeTypes = ContainerHelper.newOrderedSet (aMimeTypes);
    m_sComment = sComment;
    m_aParentTypes = ContainerHelper.newOrderedSet (aParentTypes);
    m_aGlobs = ContainerHelper.newOrderedSet (aGlobs);
    m_aExtensions = ContainerHelper.newOrderedSet (aExtensions);
    m_sSource = sSource;
  }

  @Nonnull
  @Nonempty
  public Set <MimeTypeWithSource> getAllMimeTypes ()
  {
    return ContainerHelper.newOrderedSet (m_aMimeTypes);
  }

  public boolean containsMimeType (@Nullable final String sMimeType)
  {
    if (StringHelper.hasNoText (sMimeType))
      return false;
    for (final MimeTypeWithSource aMimeType : m_aMimeTypes)
      if (aMimeType.getMimeTypeAsString ().equals (sMimeType))
        return true;
    return false;
  }

  @Nonnull
  public MimeTypeWithSource getPrimaryMimeType ()
  {
    return ContainerHelper.getFirstElement (m_aMimeTypes);
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
  public Set <String> getAllParentTypes ()
  {
    return ContainerHelper.newOrderedSet (m_aParentTypes);
  }

  @Nonnull
  public Set <String> getAllGlobs ()
  {
    return ContainerHelper.newOrderedSet (m_aGlobs);
  }

  public boolean hasAnyGlob ()
  {
    return !m_aGlobs.isEmpty ();
  }

  @Nonnull
  public Set <ExtensionWithSource> getAllExtensions ()
  {
    return ContainerHelper.newOrderedSet (m_aExtensions);
  }

  public boolean hasAnyExtension ()
  {
    return !m_aExtensions.isEmpty ();
  }

  public boolean containsExtension (@Nullable final String sExtension)
  {
    if (StringHelper.hasText (sExtension))
    {
      for (final ExtensionWithSource aExtension : m_aExtensions)
        if (aExtension.matches (sExtension))
          return true;
    }
    return false;
  }

  void addExtension (@Nonnull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aExt, "Ext");
    // Don't add to glob - can easily be constructed from all extensions
    m_aExtensions.add (aExt);
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
    if (!(o instanceof MimeTypeInfo))
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
