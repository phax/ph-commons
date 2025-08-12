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
package com.helger.xml.microdom.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.IHasOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Simple class that reads a list from an XML input stream.<br>
 * The XML file needs to look as follows:
 *
 * <pre>
 * &lt;list&gt;
 *   &lt;item value="..."/&gt;
 *   &lt;item value="..."/&gt;
 *   ...
 * &lt;/list&gt;
 * </pre>
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLListHandler
{
  /** Name of the root element */
  public static final String ELEMENT_LIST = "list";
  /** Element name of a single item */
  public static final String ELEMENT_ITEM = "item";
  /** Attribute name for the item value */
  public static final String ATTR_VALUE = "value";

  private static final Logger LOGGER = LoggerFactory.getLogger (XMLListHandler.class);

  @PresentForCodeCoverage
  private static final XMLListHandler INSTANCE = new XMLListHandler ();

  private XMLListHandler ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readList (@Nonnull final IHasInputStream aISP)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");

    return readList (aISP.getInputStream ());
  }

  @Nonnull
  public static ESuccess readList (@Nonnull final IHasInputStream aISP, @Nonnull final Collection <String> aTargetList)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");

    return readList (aISP.getInputStream (), aTargetList);
  }

  /**
   * Read a predefined XML file that contains list items.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>.
   *        Automatically closed no matter whether reading succeeded or not.
   * @return <code>null</code> if reading fails - all list items otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readList (@Nonnull @WillClose final InputStream aIS)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    if (readList (aIS, ret).isFailure ())
      return null;
    return ret;
  }

  /**
   * Read a predefined XML file that contains list items.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>.
   *        Automatically closed no matter whether reading succeeded or not.
   * @param aTargetList
   *        The target collection to be filled. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if reading succeeded,
   *         {@link ESuccess#FAILURE} if the input stream is no valid XML or any
   *         other error occurred.
   */
  @Nonnull
  public static ESuccess readList (@Nonnull @WillClose final InputStream aIS,
                                   @Nonnull final Collection <String> aTargetList)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aTargetList, "TargetList");
    try
    {
      // open file
      final IMicroDocument aDoc = MicroReader.readMicroXML (aIS);
      if (aDoc != null)
      {
        readList (aDoc.getDocumentElement (), aTargetList);
        return ESuccess.SUCCESS;
      }
    }
    catch (final Exception ex)
    {
      LOGGER.warn ("Failed to read list resource '" + aIS + "'", ex);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public static ESuccess readList (@Nonnull final IMicroElement aParentElement,
                                   @Nonnull final Collection <String> aTargetList)
  {
    ValueEnforcer.notNull (aParentElement, "ParentElement");
    ValueEnforcer.notNull (aTargetList, "TargetList");
    try
    {
      // and insert all elements
      for (final IMicroElement eItem : aParentElement.getAllChildElements (ELEMENT_ITEM))
      {
        final String sValue = eItem.getAttributeValue (ATTR_VALUE);
        if (sValue == null)
          LOGGER.warn ("Ignoring list item because value is null");
        else
          if (!aTargetList.add (sValue))
            LOGGER.warn ("Ignoring list item '" + sValue + "' because value is already contained");
      }
      return ESuccess.SUCCESS;
    }
    catch (final Exception ex)
    {
      LOGGER.warn ("Failed to read list document", ex);
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public static IMicroDocument createListDocument (@Nonnull final Collection <String> aCollection)
  {
    ValueEnforcer.notNull (aCollection, "Collection");

    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement (ELEMENT_LIST);
    for (final String sItem : aCollection)
    {
      final IMicroElement eItem = eRoot.addElement (ELEMENT_ITEM);
      eItem.setAttribute (ATTR_VALUE, sItem);
    }
    return aDoc;
  }

  @Nonnull
  public static ESuccess writeList (@Nonnull final Collection <String> aCollection,
                                    @Nonnull final IHasOutputStream aOSP)
  {
    ValueEnforcer.notNull (aOSP, "OutputStreamProvider");

    return writeList (aCollection, aOSP.getOutputStream (EAppend.DEFAULT));
  }

  /**
   * Write the passed collection to the passed output stream using the
   * predefined XML layout.
   *
   * @param aCollection
   *        The map to be written. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write to. The stream is closed independent of
   *        success or failure. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} when everything went well,
   *         {@link ESuccess#FAILURE} otherwise.
   */
  @Nonnull
  public static ESuccess writeList (@Nonnull final Collection <String> aCollection,
                                    @Nonnull @WillClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aCollection, "Collection");
    ValueEnforcer.notNull (aOS, "OutputStream");
    try
    {
      final IMicroDocument aDoc = createListDocument (aCollection);
      return MicroWriter.writeToStream (aDoc, aOS, XMLWriterSettings.DEFAULT_XML_SETTINGS);
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }
}
