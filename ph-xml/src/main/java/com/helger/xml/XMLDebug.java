/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.xml;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsEnumMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.xml.dom.EXMLDOMFeature;
import com.helger.xml.dom.EXMLDOMFeatureVersion;
import com.helger.xml.dom.EXMLDOMNodeType;

/**
 * Misc. XML DOM helper method for checking the setup etc.
 *
 * @author Philip Helger
 */
public final class XMLDebug
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (XMLDebug.class);
  private static final ICommonsMap <EXMLDOMFeatureVersion, ICommonsList <String>> s_aSupportedFeatures = new CommonsEnumMap <> (EXMLDOMFeatureVersion.class);

  static
  {
    for (final EXMLDOMFeatureVersion eFeatureVersion : EXMLDOMFeatureVersion.values ())
      s_aSupportedFeatures.put (eFeatureVersion, new CommonsArrayList <> ());

    // Check features as specified by
    // http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/introduction.html#ID-Conformance
    _initFeature (EXMLDOMFeature.DOM_FEATURE_CORE);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_XML);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_UI_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_MOUSE_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_TEXT_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_KEYBOARD_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_MUTATION_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_MUTATION_NAME_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_HTML_EVENTS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_LS);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_LS_ASYNC);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_VALIDATION);
    _initFeature (EXMLDOMFeature.DOM_FEATURE_XPATH);
  }

  private static void _initFeature (@Nonnull final EXMLDOMFeature eFeature)
  {
    final DOMImplementation aDOMImplementation = XMLFactory.getDOMImplementation ();
    for (final EXMLDOMFeatureVersion eFeatureVersion : EXMLDOMFeatureVersion.values ())
    {
      if (aDOMImplementation.hasFeature (eFeature.getID (), eFeatureVersion.getID ()))
        s_aSupportedFeatures.get (eFeatureVersion).add (eFeature.getID ());
      else
        if (aDOMImplementation.hasFeature (eFeature.getPlusFeature (), eFeatureVersion.getID ()))
          s_aSupportedFeatures.get (eFeatureVersion).add (eFeature.getPlusFeature ());
    }
  }

  @PresentForCodeCoverage
  private static final XMLDebug s_aInstance = new XMLDebug ();

  private XMLDebug ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLDOMFeatureVersion, ICommonsList <String>> getAllSupportedFeatures ()
  {
    return s_aSupportedFeatures.getClone ();
  }

  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> getAllSupportedFeatures (@Nonnull final EXMLDOMFeatureVersion eFeatureVersion)
  {
    final ICommonsList <String> ret = s_aSupportedFeatures.get (eFeatureVersion);
    return ret == null ? null : ret.getClone ();
  }

  /**
   * Emit all supported features to the logger.
   */
  public static void debugLogDOMFeatures ()
  {
    for (final Map.Entry <EXMLDOMFeatureVersion, ICommonsList <String>> aEntry : s_aSupportedFeatures.entrySet ())
      for (final String sFeature : aEntry.getValue ())
        if (s_aLogger.isInfoEnabled ())
          s_aLogger.info ("DOM " + aEntry.getKey ().getID () + " feature '" + sFeature + "' is present");
  }

  @Nonnull
  public static String getNodeTypeAsString (final int nNodeType)
  {
    final EXMLDOMNodeType eNodeType = EXMLDOMNodeType.getFromIDOrNull (nNodeType);
    if (eNodeType != null)
      return eNodeType.name ();

    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Unknown Node type " + nNodeType);
    return Integer.toString (nNodeType);
  }
}
