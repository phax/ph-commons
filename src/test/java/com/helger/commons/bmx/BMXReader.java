/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.bmx;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.collections.NonBlockingStack;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.impl.MicroCDATA;
import com.helger.commons.microdom.impl.MicroComment;
import com.helger.commons.microdom.impl.MicroContainer;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.impl.MicroDocumentType;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.microdom.impl.MicroEntityReference;
import com.helger.commons.microdom.impl.MicroProcessingInstruction;
import com.helger.commons.microdom.impl.MicroText;

public final class BMXReader
{
  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final BMXReader s_aInstance = new BMXReader ();

  private BMXReader ()
  {}

  @Nullable
  public static IMicroNode readFromFile (@Nonnull final File aFile)
  {
    if (aFile == null)
      throw new NullPointerException ("file");

    final InputStream aFIS = FileUtils.getInputStream (aFile);
    if (aFIS == null)
      return null;

    return readFromStream (aFIS);
  }

  @Nullable
  public static IMicroNode readFromStream (@Nonnull @WillClose final InputStream aIS)
  {
    if (aIS == null)
      throw new NullPointerException ("inputStream");

    // Ensure stream is buffered!
    final InputStream aISToUse = StreamUtils.getBuffered (aIS);
    try
    {
      final DataInput aDIS = new DataInputStream (aISToUse);
      return readFromDataInput (aDIS);
    }
    finally
    {
      StreamUtils.close (aISToUse);
    }
  }

  @Nullable
  public static IMicroNode readFromDataInput (@Nonnull @WillClose final DataInput aDI)
  {
    if (aDI == null)
      throw new NullPointerException ("dataInput");

    try
    {
      // Read version
      final byte [] aVersion = new byte [4];
      aDI.readFully (aVersion);
      if (!Arrays.equals (CBMXIO.VERSION1, aVersion))
        throw new BMXReadException ("This is not a BMX file!");

      // Read settings
      final int nSettings = aDI.readInt ();
      final BMXSettings aSettings = BMXSettings.createFromStorageValue (nSettings);

      // Start iterating the main content
      IMicroNode aResultNode = null;
      final NonBlockingStack <IMicroNode> aNodeStack = new NonBlockingStack <IMicroNode> ();
      IMicroNode aLastNode = null;
      final BMXReaderStringTable aST = new BMXReaderStringTable (!aSettings.isSet (EBMXSetting.NO_STRINGTABLE));

      int nNodeType;
      while ((nNodeType = aDI.readByte () & 0xff) != CBMXIO.NODETYPE_EOF)
      {
        IMicroNode aCreatedNode = null;
        switch (nNodeType)
        {
          case CBMXIO.NODETYPE_CDATA:
            aCreatedNode = new MicroCDATA (aST.getString (aDI.readInt ()));
            break;
          case CBMXIO.NODETYPE_COMMENT:
            aCreatedNode = new MicroComment (aST.getString (aDI.readInt ()));
            break;
          case CBMXIO.NODETYPE_CONTAINER:
            aCreatedNode = new MicroContainer ();
            break;
          case CBMXIO.NODETYPE_DOCUMENT:
            aCreatedNode = new MicroDocument ();
            break;
          case CBMXIO.NODETYPE_DOCUMENT_TYPE:
          {
            final String sQualifiedName = aST.getString (aDI.readInt ());
            final String sPublicID = aST.getString (aDI.readInt ());
            final String sSystemID = aST.getString (aDI.readInt ());
            aCreatedNode = new MicroDocumentType (sQualifiedName, sPublicID, sSystemID);
            break;
          }
          case CBMXIO.NODETYPE_ELEMENT:
          {
            final String sNamespaceURI = aST.getString (aDI.readInt ());
            final String sTagName = aST.getString (aDI.readInt ());
            final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
            final int nAttrCount = aDI.readInt ();
            for (int i = 0; i < nAttrCount; ++i)
            {
              final String sAttrName = aST.getString (aDI.readInt ());
              final String sAttrValue = aST.getString (aDI.readInt ());
              aElement.setAttribute (sAttrName, sAttrValue);
            }
            aCreatedNode = aElement;
            break;
          }
          case CBMXIO.NODETYPE_ENTITY_REFERENCE:
            aCreatedNode = new MicroEntityReference (aST.getString (aDI.readInt ()));
            break;
          case CBMXIO.NODETYPE_PROCESSING_INSTRUCTION:
          {
            final String sTarget = aST.getString (aDI.readInt ());
            final String sData = aST.getString (aDI.readInt ());
            aCreatedNode = new MicroProcessingInstruction (sTarget, sData);
            break;
          }
          case CBMXIO.NODETYPE_TEXT:
          {
            final String sText = aST.getString (aDI.readInt ());
            final boolean bIgnorableWhitespace = aDI.readBoolean ();
            aCreatedNode = new MicroText (sText, bIgnorableWhitespace);
            break;
          }
          case CBMXIO.NODETYPE_STRING:
          {
            final int nLength = aDI.readInt ();
            final char [] aChars = new char [nLength];
            for (int i = 0; i < nLength; ++i)
              aChars[i] = aDI.readChar ();
            aST.add (aChars);
            break;
          }
          case CBMXIO.SPECIAL_CHILDREN_START:
            aNodeStack.push (aLastNode);
            break;
          case CBMXIO.SPECIAL_CHILDREN_END:
            aLastNode = aNodeStack.pop ();
            break;
          default:
            throw new BMXReadException ("Unsupported node type " + nNodeType);
        }

        if (aCreatedNode != null)
        {
          if (aResultNode == null)
            aResultNode = aCreatedNode;
          else
            aNodeStack.peek ().appendChild (aCreatedNode);
          aLastNode = aCreatedNode;
        }
      }

      return aResultNode;
    }
    catch (final IOException ex)
    {
      throw new BMXReadException ("Failed to read from InputStream", ex);
    }
  }

  // @Nullable
  // public static IMicroNode readFromChannel (@Nonnull @WillClose final
  // ReadableByteChannel aChannel)
  // {
  // if (aChannel == null)
  // throw new NullPointerException ("channel");
  //
  // try
  // {
  // // Read version
  // final byte [] aVersion = new byte [4];
  // aChannel.readFully (aVersion);
  // final String sVersion = new String (aVersion,
  // CCharset.CHARSET_ISO_8859_1_OBJ);
  // if (!sVersion.equals (CBMXIO.VERSION1))
  // throw new BMXReadException ("This is not a BMX file!");
  //
  // // Read settings
  // final int nSettings = aDIS.readInt ();
  // final BMXSettings aSettings = BMXSettings.createFromStorageValue
  // (nSettings);
  //
  // DataInputStream aContentDIS = aDIS;
  // Inflater aInflater = null;
  // InflaterInputStream aInflaterIS = null;
  // if (false)
  // {
  // aInflater = new Inflater ();
  // aInflaterIS = new InflaterInputStream (aDIS, aInflater);
  // aContentDIS = new DataInputStream (aInflaterIS);
  // }
  //
  // // Start iterating the main content
  // IMicroNode aResultNode = null;
  // final NonBlockingStack <IMicroNode> aNodeStack = new NonBlockingStack
  // <IMicroNode> ();
  // IMicroNode aLastNode = null;
  // final BMXReaderStringTable aST = new BMXReaderStringTable (!aSettings.isSet
  // (EBMXSetting.NO_STRINGTABLE));
  //
  // int nNodeType;
  // while ((nNodeType = aContentDIS.readByte () & 0xff) != CBMXIO.NODETYPE_EOF)
  // {
  // IMicroNode aCreatedNode = null;
  // switch (nNodeType)
  // {
  // case CBMXIO.NODETYPE_CDATA:
  // aCreatedNode = new MicroCDATA (aST.get (aContentDIS.readInt ()));
  // break;
  // case CBMXIO.NODETYPE_COMMENT:
  // aCreatedNode = new MicroComment (aST.get (aContentDIS.readInt ()));
  // break;
  // case CBMXIO.NODETYPE_CONTAINER:
  // aCreatedNode = new MicroContainer ();
  // break;
  // case CBMXIO.NODETYPE_DOCUMENT:
  // aCreatedNode = new MicroDocument ();
  // break;
  // case CBMXIO.NODETYPE_DOCUMENT_TYPE:
  // {
  // final String sQualifiedName = aST.get (aContentDIS.readInt ());
  // final String sPublicID = aST.get (aContentDIS.readInt ());
  // final String sSystemID = aST.get (aContentDIS.readInt ());
  // aCreatedNode = new MicroDocumentType (sQualifiedName, sPublicID,
  // sSystemID);
  // break;
  // }
  // case CBMXIO.NODETYPE_ELEMENT:
  // {
  // final String sNamespaceURI = aST.get (aContentDIS.readInt ());
  // final String sTagName = aST.get (aContentDIS.readInt ());
  // final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
  // final int nAttrCount = aContentDIS.readInt ();
  // for (int i = 0; i < nAttrCount; ++i)
  // {
  // final String sAttrName = aST.get (aContentDIS.readInt ());
  // final String sAttrValue = aST.get (aContentDIS.readInt ());
  // aElement.setAttribute (sAttrName, sAttrValue);
  // }
  // aCreatedNode = aElement;
  // break;
  // }
  // case CBMXIO.NODETYPE_ENTITY_REFERENCE:
  // aCreatedNode = new MicroEntityReference (aST.get (aContentDIS.readInt ()));
  // break;
  // case CBMXIO.NODETYPE_PROCESSING_INSTRUCTION:
  // {
  // final String sTarget = aST.get (aContentDIS.readInt ());
  // final String sData = aST.get (aContentDIS.readInt ());
  // aCreatedNode = new MicroProcessingInstruction (sTarget, sData);
  // break;
  // }
  // case CBMXIO.NODETYPE_TEXT:
  // {
  // final String sText = aST.get (aContentDIS.readInt ());
  // final boolean bIgnorableWhitespace = aContentDIS.readBoolean ();
  // aCreatedNode = new MicroText (sText, bIgnorableWhitespace);
  // break;
  // }
  // case CBMXIO.NODETYPE_STRING:
  // {
  // final int nLength = aContentDIS.readInt ();
  // final byte [] aString = new byte [nLength];
  // aContentDIS.readFully (aString);
  // aST.add (new String (aString, CBMXIO.ENCODING));
  // break;
  // }
  // case CBMXIO.SPECIAL_CHILDREN_START:
  // aNodeStack.push (aLastNode);
  // break;
  // case CBMXIO.SPECIAL_CHILDREN_END:
  // aLastNode = aNodeStack.pop ();
  // break;
  // default:
  // throw new BMXReadException ("Unsupported node type " + nNodeType);
  // }
  //
  // if (aCreatedNode != null)
  // {
  // if (aResultNode == null)
  // aResultNode = aCreatedNode;
  // else
  // aNodeStack.peek ().appendChild (aCreatedNode);
  // aLastNode = aCreatedNode;
  // }
  // }
  //
  // if (aInflater != null)
  // aInflater.end ();
  //
  // return aResultNode;
  // }
  // catch (final IOException ex)
  // {
  // throw new BMXReadException ("Failed to read from InputStream", ex);
  // }
  // finally
  // {
  // StreamUtils.close (aISToUse);
  // }
  // }
}
