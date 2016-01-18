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
package com.helger.commons.changelog;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.datetime.DateTimeFormatterCache;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.text.MultilingualText;
import com.helger.commons.version.Version;
import com.helger.commons.xml.CXML;

/**
 * This class handles the reading and writing of changelog objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class ChangeLogSerializer
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ChangeLogSerializer.class);
  // "u" == year, "y" == year of era
  private static final DateTimeFormatter DF = DateTimeFormatterCache.getDateTimeFormatterStrict ("uuuu-MM-dd");
  private static final String ELEMENT_CHANGELOG = "changelog";
  private static final String ATTR_VERSION = "version";
  private static final String ATTR_COMPONENT = "component";
  private static final String ELEMENT_ENTRY = "entry";
  private static final String ATTR_DATE = "date";
  private static final String ATTR_ACTION = "action";
  private static final String ATTR_CATEGORY = "category";
  private static final String ATTR_INCOMPATIBLE = "incompatible";
  private static final String ELEMENT_CHANGE = "change";
  private static final String ELEMENT_ISSUE = "issue";
  private static final String ELEMENT_RELEASE = "release";

  private static final IChangeLogSerializerCallback s_aDefaultCallback = new LoggingChangeLogSerializerCallback ();

  @PresentForCodeCoverage
  private static final ChangeLogSerializer s_aInstance = new ChangeLogSerializer ();

  private ChangeLogSerializer ()
  {}

  /**
   * Read the change log resource specified by the input stream provider using
   * the default logging callback.
   *
   * @param aISP
   *        The ISP to read from. Maybe <code>null</code> resulting in a
   *        <code>null</code> return.
   * @return <code>null</code> if the passed ISP was <code>null</code>.
   */
  @Nullable
  public static ChangeLog readChangeLog (@Nullable final IHasInputStream aISP)
  {
    return readChangeLog (aISP, s_aDefaultCallback);
  }

  /**
   * Read the change log resource specified by the input stream provider.
   *
   * @param aISP
   *        The ISP to read from. Maybe <code>null</code> resulting in a
   *        <code>null</code> return.
   * @param aErrorCallback
   *        The callback that handles potential errors.
   * @return <code>null</code> if the passed ISP was <code>null</code>.
   */
  @Nullable
  public static ChangeLog readChangeLog (@Nullable final IHasInputStream aISP,
                                         @Nonnull final IChangeLogSerializerCallback aErrorCallback)
  {
    ValueEnforcer.notNull (aErrorCallback, "ErrorCallback");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aISP);
    if (aDoc == null)
      return null;

    final IMicroElement eRoot = aDoc.getDocumentElement ();
    if (eRoot == null)
      return null;

    final ChangeLog ret = new ChangeLog (eRoot.getAttributeValue (ATTR_VERSION),
                                         eRoot.getAttributeValue (ATTR_COMPONENT));

    // Add all entries
    for (final IMicroElement eElement : eRoot.getAllChildElements ())
    {
      if (!CChangeLog.CHANGELOG_NAMESPACE_10.equals (eElement.getNamespaceURI ()))
      {
        aErrorCallback.accept ("Element '" +
                               eElement.getTagName () +
                               "' has the wrong namespace URI '" +
                               eElement.getNamespaceURI () +
                               "'");
        continue;
      }

      final String sTagName = eElement.getTagName ();
      if (ELEMENT_ENTRY.equals (sTagName))
      {
        final String sDate = eElement.getAttributeValue (ATTR_DATE);
        final String sAction = eElement.getAttributeValue (ATTR_ACTION);
        final String sCategory = eElement.getAttributeValue (ATTR_CATEGORY);
        final String sIncompatible = eElement.getAttributeValue (ATTR_INCOMPATIBLE);

        LocalDate aLocalDate;
        try
        {
          aLocalDate = DF.parse (sDate, LocalDate::from);
        }
        catch (final DateTimeParseException ex)
        {
          aErrorCallback.accept ("Failed to parse entry date '" + sDate + "'");
          continue;
        }
        final EChangeLogAction eAction = EChangeLogAction.getFromIDOrNull (sAction);
        if (eAction == null)
        {
          aErrorCallback.accept ("Failed to parse change log action '" + sAction + "'");
          continue;
        }
        final EChangeLogCategory eCategory = EChangeLogCategory.getFromIDOrNull (sCategory);
        if (eCategory == null)
        {
          aErrorCallback.accept ("Failed to parse change log category '" + sCategory + "'");
          continue;
        }
        final boolean bIsIncompatible = StringHelper.hasText (sIncompatible) && StringParser.parseBool (sIncompatible);

        final ChangeLogEntry aEntry = new ChangeLogEntry (ret, aLocalDate, eAction, eCategory, bIsIncompatible);
        ret.addEntry (aEntry);

        final IMicroElement eChange = eElement.getFirstChildElement (CChangeLog.CHANGELOG_NAMESPACE_10, ELEMENT_CHANGE);
        if (eChange == null)
        {
          aErrorCallback.accept ("No change element present!");
          continue;
        }
        final MultilingualText aMLT = MicroTypeConverter.convertToNative (eChange, MultilingualText.class);
        if (aMLT == null)
        {
          aErrorCallback.accept ("Failed to read multi lingual text in change element!");
          continue;
        }
        aEntry.setText (aMLT);
        for (final IMicroElement eIssue : eElement.getAllChildElements (CChangeLog.CHANGELOG_NAMESPACE_10,
                                                                        ELEMENT_ISSUE))
          aEntry.addIssue (eIssue.getTextContent ());
      }
      else
        if (ELEMENT_RELEASE.equals (sTagName))
        {
          final String sDate = eElement.getAttributeValue (ATTR_DATE);
          final String sVersion = eElement.getAttributeValue (ATTR_VERSION);
          LocalDate aLocalDate;
          try
          {
            aLocalDate = DF.parse (sDate, LocalDate::from);
          }
          catch (final DateTimeParseException ex)
          {
            s_aLogger.warn ("Failed to parse release date '" + sDate + "'");
            continue;
          }
          ret.addRelease (new ChangeLogRelease (aLocalDate, new Version (sVersion, false)));
        }
        else
          aErrorCallback.accept ("Changelog contains unsupported element '" + sTagName + "!");
    }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <URI, ChangeLog> readAllChangeLogs ()
  {
    return readAllChangeLogs (s_aDefaultCallback);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <URI, ChangeLog> readAllChangeLogs (@Nullable final ClassLoader aClassLoader)
  {
    return readAllChangeLogs (s_aDefaultCallback, aClassLoader);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <URI, ChangeLog> readAllChangeLogs (@Nonnull final IHasClassLoader aClassLoaderProvider)
  {
    return readAllChangeLogs (s_aDefaultCallback, aClassLoaderProvider.getClassLoader ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <URI, ChangeLog> readAllChangeLogs (@Nonnull final IChangeLogSerializerCallback aErrorCallback)
  {
    return readAllChangeLogs (aErrorCallback, (ClassLoader) null);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <URI, ChangeLog> readAllChangeLogs (@Nonnull final IChangeLogSerializerCallback aErrorCallback,
                                                        @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aErrorCallback, "ErrorCallback");

    try
    {
      final ClassLoader aRealClassLoader = aClassLoader != null ? aClassLoader
                                                                : ClassLoaderHelper.getDefaultClassLoader ();

      final Map <URI, ChangeLog> ret = new HashMap <URI, ChangeLog> ();
      // Find all change log XML files in the classpath
      for (final URL aURL : CollectionHelper.newList (ClassLoaderHelper.getResources (aRealClassLoader,
                                                                                      CChangeLog.CHANGELOG_XML_FILENAME)))
      {
        final URLResource aRes = new URLResource (aURL);
        final ChangeLog aChangeLog = readChangeLog (aRes, aErrorCallback);
        if (aChangeLog != null)
          ret.put (aRes.getAsURI (), aChangeLog);
        else
          s_aLogger.warn ("Failed to read changelog from URL " + aURL.toExternalForm ());
      }
      return ret;
    }
    catch (final IOException ex)
    {
      // Can be thrown by getResources
      throw new IllegalStateException ("Failed to resolved changelogs", ex);
    }
  }

  @Nonnull
  public static IMicroDocument writeChangeLog (@Nonnull final ChangeLog aChangeLog)
  {
    ValueEnforcer.notNull (aChangeLog, "ChangeLog");

    final IMicroDocument ret = new MicroDocument ();
    final IMicroElement eRoot = ret.appendElement (CChangeLog.CHANGELOG_NAMESPACE_10, ELEMENT_CHANGELOG);
    eRoot.setAttribute (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, CXML.XML_NS_PREFIX_XSI, CXML.XML_NS_XSI);
    eRoot.setAttribute (CXML.XML_NS_XSI, "schemaLocation", CChangeLog.CHANGELOG_SCHEMALOCATION_10);
    eRoot.setAttribute (ATTR_VERSION, aChangeLog.getOriginalVersion ());
    if (StringHelper.hasText (aChangeLog.getComponent ()))
      eRoot.setAttribute (ATTR_COMPONENT, aChangeLog.getComponent ());

    for (final AbstractChangeLogEntry aBaseEntry : aChangeLog.getAllBaseEntries ())
    {
      if (aBaseEntry instanceof ChangeLogEntry)
      {
        final ChangeLogEntry aEntry = (ChangeLogEntry) aBaseEntry;
        final IMicroElement eEntry = eRoot.appendElement (CChangeLog.CHANGELOG_NAMESPACE_10, ELEMENT_ENTRY);
        eEntry.setAttribute (ATTR_DATE, DF.format (aEntry.getDate ()));
        eEntry.setAttribute (ATTR_ACTION, aEntry.getAction ().getID ());
        eEntry.setAttribute (ATTR_CATEGORY, aEntry.getCategory ().getID ());
        if (aEntry.isIncompatible ())
          eEntry.setAttribute (ATTR_INCOMPATIBLE, Boolean.TRUE.toString ());
        eEntry.appendChild (MicroTypeConverter.convertToMicroElement (aEntry.getAllTexts (),
                                                                      CChangeLog.CHANGELOG_NAMESPACE_10,
                                                                      ELEMENT_CHANGE));
        for (final String sIssue : aEntry.getAllIssues ())
          eEntry.appendElement (CChangeLog.CHANGELOG_NAMESPACE_10, ELEMENT_ISSUE).appendText (sIssue);
      }
      else
      {
        // Must be a release
        final ChangeLogRelease aRelease = (ChangeLogRelease) aBaseEntry;
        final IMicroElement eRelease = eRoot.appendElement (CChangeLog.CHANGELOG_NAMESPACE_10, ELEMENT_RELEASE);
        eRelease.setAttribute (ATTR_DATE, DF.format (aRelease.getDate ()));
        eRelease.setAttribute (ATTR_VERSION, aRelease.getVersion ().getAsString ());
      }
    }

    return ret;
  }
}
