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
package com.helger.xml.util.mime;

import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.mime.MimeType;
import com.helger.mime.parse.MimeTypeParser;
import com.helger.mime.parse.MimeTypeParserException;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.util.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.xml.util.mime.MimeTypeInfo.MimeTypeWithSource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class MimeTypeInfoMicroTypeConverter implements IMicroTypeConverter <MimeTypeInfo>
{
  private static final String ELEMENT_MIMETYPE = "mime-type";
  private static final String ELEMENT_COMMENT = "comment";
  private static final String ELEMENT_PARENT_TYPE = "parent-type";
  private static final String ELEMENT_GLOB = "glob";
  private static final String ELEMENT_EXTENSION = "extension";
  private static final String ATTR_SOURCE = "source";

  @Nullable
  public IMicroElement convertToMicroElement (@Nonnull final MimeTypeInfo aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement eRet = new MicroElement (sNamespaceURI, sTagName);
    for (final MimeTypeWithSource aMimeType : aObject.getAllMimeTypesWithSource ())
    {
      eRet.addElement (sNamespaceURI, ELEMENT_MIMETYPE)
          .setAttribute (ATTR_SOURCE, aMimeType.getSource ())
          .addText (aMimeType.getMimeTypeAsString ());
    }
    if (aObject.hasComment ())
      eRet.addElement (sNamespaceURI, ELEMENT_COMMENT).addText (aObject.getComment ());
    for (final String sParentType : aObject.getAllParentTypes ())
      eRet.addElement (sNamespaceURI, ELEMENT_PARENT_TYPE).addText (sParentType);
    for (final String sGlob : aObject.getAllGlobs ())
      eRet.addElement (sNamespaceURI, ELEMENT_GLOB).addText (sGlob);
    for (final ExtensionWithSource aExtension : aObject.getAllExtensionsWithSource ())
    {
      eRet.addElement (sNamespaceURI, ELEMENT_EXTENSION)
          .setAttribute (ATTR_SOURCE, aExtension.getSource ())
          .addText (aExtension.getExtension ());
    }
    eRet.setAttribute (ATTR_SOURCE, aObject.getSource ());
    return eRet;
  }

  @Nullable
  public MimeTypeInfo convertToNative (@Nonnull final IMicroElement aElement)
  {
    final ICommonsOrderedSet <MimeTypeWithSource> aMimeTypes = new CommonsLinkedHashSet <> ();
    for (final IMicroElement eMimeType : aElement.getAllChildElements (ELEMENT_MIMETYPE))
    {
      try
      {
        final MimeType aMimeType = MimeTypeParser.parseMimeType (eMimeType.getTextContentTrimmed ());
        final String sSource = eMimeType.getAttributeValue (ATTR_SOURCE);
        aMimeTypes.add (new MimeTypeWithSource (aMimeType, sSource));
      }
      catch (final MimeTypeParserException ex)
      {
        throw new IllegalStateException ("Failed to parse MIME type", ex);
      }
    }

    final String sComment = MicroHelper.getChildTextContent (aElement, ELEMENT_COMMENT);

    final ICommonsOrderedSet <String> aParentTypes = new CommonsLinkedHashSet <> ();
    for (final IMicroElement eParentType : aElement.getAllChildElements (ELEMENT_PARENT_TYPE))
      aParentTypes.add (eParentType.getTextContentTrimmed ());

    final ICommonsOrderedSet <String> aGlobs = new CommonsLinkedHashSet <> ();
    for (final IMicroElement eGlob : aElement.getAllChildElements (ELEMENT_GLOB))
      aGlobs.add (eGlob.getTextContentTrimmed ());

    final ICommonsOrderedSet <ExtensionWithSource> aExtensions = new CommonsLinkedHashSet <> ();
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
}
