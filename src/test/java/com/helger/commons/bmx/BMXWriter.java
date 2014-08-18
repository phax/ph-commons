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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import com.helger.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.microdom.EMicroNodeType;
import com.helger.commons.microdom.IMicroCDATA;
import com.helger.commons.microdom.IMicroComment;
import com.helger.commons.microdom.IMicroDocumentType;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroEntityReference;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.IMicroProcessingInstruction;
import com.helger.commons.microdom.IMicroText;
import com.helger.commons.microdom.MicroException;
import com.helger.commons.microdom.utils.MicroWalker;
import com.helger.commons.state.ESuccess;

/**
 * Binary Micro XML (BMX) Writer
 *
 * @author Philip Helger
 */
public class BMXWriter
{
  private final BMXSettings m_aSettings;

  public BMXWriter ()
  {
    this (BMXSettings.createDefault ());
  }

  public BMXWriter (@Nonnull final BMXSettings aSettings)
  {
    if (aSettings == null)
      throw new NullPointerException ("settings");
    m_aSettings = aSettings.getClone ();
  }

  private static void _writeNodeBeforeChildren (@Nonnull final BMXWriterStringTable aST,
                                                @Nonnull final IMicroNode aChildNode,
                                                @Nonnull final DataOutput aDOS)
  {
    try
    {
      final EMicroNodeType eNodeType = aChildNode.getType ();
      switch (eNodeType)
      {
        case CDATA:
        {
          final IMicroCDATA aCDATA = (IMicroCDATA) aChildNode;
          final int nStringIdx = aST.addString (aCDATA.getData ());
          aDOS.writeByte (CBMXIO.NODETYPE_CDATA);
          aDOS.writeInt (nStringIdx);
          break;
        }
        case COMMENT:
        {
          final IMicroComment aComment = (IMicroComment) aChildNode;
          final int nStringIdx = aST.addString (aComment.getData ());
          aDOS.writeByte (CBMXIO.NODETYPE_COMMENT);
          aDOS.writeInt (nStringIdx);
          break;
        }
        case CONTAINER:
          aDOS.writeByte (CBMXIO.NODETYPE_CONTAINER);
          break;
        case DOCUMENT:
          aDOS.writeByte (CBMXIO.NODETYPE_DOCUMENT);
          break;
        case DOCUMENT_TYPE:
        {
          final IMicroDocumentType aDocType = (IMicroDocumentType) aChildNode;
          final int nStringIdx1 = aST.addString (aDocType.getQualifiedName ());
          final int nStringIdx2 = aST.addString (aDocType.getPublicID ());
          final int nStringIdx3 = aST.addString (aDocType.getSystemID ());
          aDOS.writeByte (CBMXIO.NODETYPE_DOCUMENT_TYPE);
          aDOS.writeInt (nStringIdx1);
          aDOS.writeInt (nStringIdx2);
          aDOS.writeInt (nStringIdx3);
          break;
        }
        case ELEMENT:
          final IMicroElement aElement = (IMicroElement) aChildNode;
          final List <Integer> aIntList = new ArrayList <Integer> ();
          aIntList.add (Integer.valueOf (aST.addString (aElement.getNamespaceURI ())));
          aIntList.add (Integer.valueOf (aST.addString (aElement.getTagName ())));
          if (aElement.hasAttributes ())
          {
            final Map <String, String> aAttrs = aElement.getAllAttributes ();
            aIntList.add (Integer.valueOf (aAttrs.size ()));
            for (final Map.Entry <String, String> aEntry : aAttrs.entrySet ())
            {
              aIntList.add (Integer.valueOf (aST.addString (aEntry.getKey ())));
              aIntList.add (Integer.valueOf (aST.addString (aEntry.getValue ())));
            }
          }
          else
          {
            aIntList.add (Integer.valueOf (0));
          }
          aDOS.writeByte (CBMXIO.NODETYPE_ELEMENT);
          for (final Integer aInt : aIntList)
            aDOS.writeInt (aInt.intValue ());
          break;
        case ENTITY_REFERENCE:
        {
          final IMicroEntityReference aER = (IMicroEntityReference) aChildNode;
          final int nStringIdx = aST.addString (aER.getName ());
          aDOS.writeByte (CBMXIO.NODETYPE_ENTITY_REFERENCE);
          aDOS.writeInt (nStringIdx);
          break;
        }
        case PROCESSING_INSTRUCTION:
        {
          final IMicroProcessingInstruction aPI = (IMicroProcessingInstruction) aChildNode;
          final int nStringIdx1 = aST.addString (aPI.getTarget ());
          final int nStringIdx2 = aST.addString (aPI.getData ());
          aDOS.writeByte (CBMXIO.NODETYPE_PROCESSING_INSTRUCTION);
          aDOS.writeInt (nStringIdx1);
          aDOS.writeInt (nStringIdx2);
          break;
        }
        case TEXT:
        {
          final IMicroText aText = (IMicroText) aChildNode;
          final int nStringIdx = aST.addString (aText.getData ());
          aDOS.writeByte (CBMXIO.NODETYPE_TEXT);
          aDOS.writeInt (nStringIdx);
          aDOS.writeBoolean (aText.isElementContentWhitespace ());
          break;
        }
        default:
          throw new IllegalStateException ("Illegal node type:" + aChildNode);
      }

      if (aChildNode.hasChildren ())
        aDOS.writeByte (CBMXIO.SPECIAL_CHILDREN_START);
    }
    catch (final IOException ex)
    {
      throw new MicroException ("Failed to write BMX content to output stream", ex);
    }
  }

  private static void _writeNodeAfterChildren (@Nonnull final IMicroNode aChildNode, @Nonnull final DataOutput aDOS)
  {
    try
    {
      if (aChildNode.hasChildren ())
        aDOS.writeByte (CBMXIO.SPECIAL_CHILDREN_END);
    }
    catch (final IOException ex)
    {
      throw new MicroException ("Failed to write BMX content to output stream", ex);
    }
  }

  private static void _writeContent (@Nonnull final BMXWriterStringTable aST,
                                     @Nonnull final IMicroNode aNode,
                                     @Nonnull final DataOutput aDOS)
  {
    // Write main content
    _writeNodeBeforeChildren (aST, aNode, aDOS);
    MicroWalker.walkNode (aNode, new DefaultHierarchyWalkerCallback <IMicroNode> ()
                          {
      @Override
      public void onItemBeforeChildren (final IMicroNode aChildNode)
      {
        _writeNodeBeforeChildren (aST, aChildNode, aDOS);
      }

      @Override
      public void onItemAfterChildren (@Nonnull final IMicroNode aChildNode)
      {
        _writeNodeAfterChildren (aChildNode, aDOS);
      }
                          });
    _writeNodeAfterChildren (aNode, aDOS);
  }

  @Nonnull
  public ESuccess writeToDataOutput (@Nonnull final IMicroNode aNode, @Nonnull final DataOutput aDOS)
  {
    if (aNode == null)
      throw new NullPointerException ("node");
    if (aDOS == null)
      throw new NullPointerException ("dataOutput");

    try
    {
      // Main format version
      aDOS.write (CBMXIO.VERSION1);

      // Write settings
      aDOS.writeInt (m_aSettings.getStorageValue ());

      // The string table to be filled
      final BMXWriterStringTable aST = new BMXWriterStringTable (aDOS, !m_aSettings.isSet (EBMXSetting.NO_STRINGTABLE));

      // Write the main content and filling the string table
      _writeContent (aST, aNode, aDOS);

      // Write EOF marker
      aDOS.writeByte (CBMXIO.NODETYPE_EOF);
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      throw new MicroException ("Failed to write BMX content to output stream", ex);
    }
  }

  @Nonnull
  public ESuccess writeToStream (@Nonnull final IMicroNode aNode, @Nonnull @WillClose final OutputStream aOS)
  {
    if (aOS == null)
      throw new NullPointerException ("OS");

    // Wrap the passed output stream in a buffered output stream
    final DataOutputStream aDOS = new DataOutputStream (StreamUtils.getBuffered (aOS));
    try
    {
      return writeToDataOutput (aNode, aDOS);
    }
    finally
    {
      StreamUtils.close (aDOS);
    }
  }

  @Nonnull
  public ESuccess writeToFile (@Nonnull final IMicroNode aNode, @Nonnull final File aFile)
  {
    if (aFile == null)
      throw new NullPointerException ("file");

    // This is much quicker than using RandomAccessFile
    final OutputStream aFOS = FileUtils.getOutputStream (aFile);
    if (aFOS == null)
      return ESuccess.FAILURE;

    return writeToStream (aNode, aFOS);
  }

  @Nullable
  public byte [] getAsBytes (@Nonnull final IMicroNode aNode)
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    if (writeToStream (aNode, aBAOS).isFailure ())
      return null;
    return aBAOS.toByteArray ();
  }
}
