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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistry;
import com.helger.commons.microdom.util.MicroHelper;
import com.helger.commons.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.commons.mime.MimeTypeInfo.MimeTypeWithSource;
import com.helger.commons.string.StringHelper;

/**
 * {@link IMicroTypeConverterRegistrarSPI} implementation for
 * {@link MimeTypeInfo}.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class MimeTypeInfoMicroTypeConverterRegistrar implements IMicroTypeConverterRegistrarSPI
{
  static final class MimeTypeInfoMicroTypeConverter implements IMicroTypeConverter
  {
    private static final String ELEMENT_MIMETYPE = "mime-type";
    private static final String ELEMENT_COMMENT = "comment";
    private static final String ELEMENT_PARENT_TYPE = "parent-type";
    private static final String ELEMENT_GLOB = "glob";
    private static final String ELEMENT_EXTENSION = "extension";
    private static final String ATTR_SOURCE = "source";

    @Nullable
    public MimeTypeInfo convertToNative (@Nonnull final IMicroElement aElement)
    {
      final Set <MimeTypeWithSource> aMimeTypes = new LinkedHashSet <MimeTypeWithSource> ();
      for (final IMicroElement eMimeType : aElement.getAllChildElements (ELEMENT_MIMETYPE))
      {
        final MimeType aMimeType = MimeTypeParser.parseMimeType (eMimeType.getTextContentTrimmed ());
        final String sSource = eMimeType.getAttributeValue (ATTR_SOURCE);
        aMimeTypes.add (new MimeTypeWithSource (aMimeType, sSource));
      }

      final String sComment = MicroHelper.getChildTextContent (aElement, ELEMENT_COMMENT);

      final Set <String> aParentTypes = new LinkedHashSet <String> ();
      for (final IMicroElement eParentType : aElement.getAllChildElements (ELEMENT_PARENT_TYPE))
        aParentTypes.add (eParentType.getTextContentTrimmed ());

      final Set <String> aGlobs = new LinkedHashSet <String> ();
      for (final IMicroElement eGlob : aElement.getAllChildElements (ELEMENT_GLOB))
        aGlobs.add (eGlob.getTextContentTrimmed ());

      final Set <ExtensionWithSource> aExtensions = new LinkedHashSet <ExtensionWithSource> ();
      for (final IMicroElement eExtension : aElement.getAllChildElements (ELEMENT_EXTENSION))
      {
        // May be null if the empty extension ("") is used
        final String sExtension = StringHelper.getNotNull (eExtension.getTextContentTrimmed ());
        final String sSource = eExtension.getAttributeValue (ATTR_SOURCE);
        aExtensions.add (new ExtensionWithSource (sExtension, sSource));
      }

      final String sSource = aElement.getAttributeValue (ATTR_SOURCE);

      return new MimeTypeInfo (aMimeTypes, sComment, aParentTypes, aGlobs, aExtensions, sSource);
    }

    @Nullable
    public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                                @Nullable final String sNamespaceURI,
                                                @Nonnull final String sTagName)
    {
      final MimeTypeInfo aValue = (MimeTypeInfo) aObject;
      final IMicroElement eRet = new MicroElement (sNamespaceURI, sTagName);
      for (final MimeTypeWithSource aMimeType : aValue.getAllMimeTypesWithSource ())
      {
        eRet.appendElement (sNamespaceURI, ELEMENT_MIMETYPE)
            .setAttribute (ATTR_SOURCE, aMimeType.getSource ())
            .appendText (aMimeType.getMimeTypeAsString ());
      }
      if (aValue.hasComment ())
        eRet.appendElement (sNamespaceURI, ELEMENT_COMMENT).appendText (aValue.getComment ());
      for (final String sParentType : aValue.getAllParentTypes ())
        eRet.appendElement (sNamespaceURI, ELEMENT_PARENT_TYPE).appendText (sParentType);
      for (final String sGlob : aValue.getAllGlobs ())
        eRet.appendElement (sNamespaceURI, ELEMENT_GLOB).appendText (sGlob);
      for (final ExtensionWithSource aExtension : aValue.getAllExtensionsWithSource ())
      {
        eRet.appendElement (sNamespaceURI, ELEMENT_EXTENSION)
            .setAttribute (ATTR_SOURCE, aExtension.getSource ())
            .appendText (aExtension.getExtension ());
      }
      eRet.setAttribute (ATTR_SOURCE, aValue.getSource ());
      return eRet;
    }
  }

  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry)
  {
    aRegistry.registerMicroElementTypeConverter (MimeTypeInfo.class, new MimeTypeInfoMicroTypeConverter ());
  }
}
