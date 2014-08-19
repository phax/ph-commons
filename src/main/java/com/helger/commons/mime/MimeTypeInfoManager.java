package com.helger.commons.mime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.multimap.IMultiMapListBased;
import com.helger.commons.collections.multimap.MultiTreeMapArrayListBased;
import com.helger.commons.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.commons.mime.MimeTypeInfo.MimeTypeWithSource;
import com.helger.commons.string.StringHelper;
import com.phloc.commons.annotations.ReturnsMutableCopy;

/**
 * This is the central manager for all {@link MimeTypeInfo} objects.
 *
 * @author Philip Helger
 */
public class MimeTypeInfoManager
{
  private final List <MimeTypeInfo> m_aList = new ArrayList <MimeTypeInfo> ();
  private final IMultiMapListBased <MimeType, MimeTypeInfo> m_aMapMimeType = new MultiTreeMapArrayListBased <MimeType, MimeTypeInfo> ();
  private final IMultiMapListBased <String, MimeTypeInfo> m_aMapExt = new MultiTreeMapArrayListBased <String, MimeTypeInfo> ();

  public MimeTypeInfoManager ()
  {}

  public void registerMimeType (@Nonnull final MimeTypeInfo aInfo)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    final Set <MimeTypeWithSource> aMimeTypes = aInfo.getAllMimeTypes ();
    final Set <ExtensionWithSource> aExtensions = aInfo.getAllExtensions ();

    // Check
    for (final MimeTypeWithSource aMimeType : aMimeTypes)
    {
      final List <MimeTypeInfo> aExisting = m_aMapMimeType.get (aMimeType.getMimeType ());
      if (aExisting != null)
        throw new IllegalArgumentException ("Cannot register " +
                                            aInfo +
                                            ". A mapping for mime type '" +
                                            aMimeType +
                                            "' is already registered: " +
                                            aExisting);
    }

    // Perform
    m_aList.add (aInfo);
    for (final MimeTypeWithSource aMimeType : aMimeTypes)
      m_aMapMimeType.putSingle (aMimeType.getMimeType (), aInfo);
    for (final ExtensionWithSource aExt : aExtensions)
      m_aMapExt.putSingle (aExt.getExtension (), aInfo);
  }

  public void addExtension (@Nonnull final MimeTypeInfo aInfo, @Nonnull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    ValueEnforcer.notNull (aExt, "Ext");
    m_aMapExt.putSingle (aExt.getExtension (), aInfo);
    aInfo.addExtension (aExt);
  }

  @Nullable
  public List <MimeTypeInfo> getAllInfosOfExtension (@Nullable final String sExtension)
  {
    if (StringHelper.hasNoText (sExtension))
      return null;

    List <MimeTypeInfo> ret = m_aMapExt.get (sExtension);
    if (ret == null)
    {
      // Especially on Windows, sometimes file extensions like "JPG" can be
      // found. Therefore also test for the lowercase version of the extension.
      ret = m_aMapExt.get (sExtension.toLowerCase (Locale.US));
    }

    // Create a copy if present
    return ret == null ? null : ContainerHelper.newList (ret);
  }

  @Nullable
  public List <MimeTypeInfo> getAllInfosOfMimeType (@Nullable final MimeType aMimeType)
  {
    if (aMimeType == null)
      return null;

    final List <MimeTypeInfo> ret = m_aMapMimeType.get (aMimeType);

    // Create a copy if present
    return ret == null ? null : ContainerHelper.newList (ret);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllMimeTypeInfos ()
  {
    return ContainerHelper.newList (m_aList);
  }
}
