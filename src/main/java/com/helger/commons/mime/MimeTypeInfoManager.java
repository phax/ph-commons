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
import com.helger.commons.mime.MimeTypeInfo.Extension;
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
  private final IMultiMapListBased <String, MimeTypeInfo> m_aMapMimeType = new MultiTreeMapArrayListBased <String, MimeTypeInfo> ();
  private final IMultiMapListBased <String, MimeTypeInfo> m_aMapExt = new MultiTreeMapArrayListBased <String, MimeTypeInfo> ();

  public MimeTypeInfoManager ()
  {}

  public void registerMimeType (@Nonnull final MimeTypeInfo aInfo)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    final Set <String> aMimeTypes = aInfo.getAllMimeTypes ();
    final Set <Extension> aExtensions = aInfo.getAllExtensions ();

    // Check
    for (final String sMimeType : aMimeTypes)
      if (m_aMapMimeType.containsKey (sMimeType))
        throw new IllegalArgumentException ("Cannot register " +
                                            aInfo +
                                            ". A mapping for mime type '" +
                                            sMimeType +
                                            "' is already registered: " +
                                            m_aMapMimeType.get (sMimeType));

    // Perform
    m_aList.add (aInfo);
    for (final String sMimeType : aMimeTypes)
      m_aMapMimeType.putSingle (sMimeType, aInfo);
    for (final Extension aExt : aExtensions)
      m_aMapExt.putSingle (aExt.getExtension (), aInfo);
  }

  public void addExtension (@Nonnull final MimeTypeInfo aInfo, @Nonnull final Extension aExt)
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
  public List <MimeTypeInfo> getAllInfosOfMimeType (@Nullable final String sMimeType)
  {
    if (StringHelper.hasNoText (sMimeType))
      return null;

    final List <MimeTypeInfo> ret = m_aMapMimeType.get (sMimeType);

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
