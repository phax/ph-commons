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
package com.helger.commons.xml.xpath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Utility class to evaluate XPath expressions more easily
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class XPathExpressionHelper
{
  @PresentForCodeCoverage
  private static final XPathExpressionHelper s_aInstance = new XPathExpressionHelper ();

  private XPathExpressionHelper ()
  {}

  @Nullable
  public static Object evalXPath (@Nullable final XPathVariableResolver aVariableResolver,
                                  @Nullable final XPathFunctionResolver aFunctionResolver,
                                  @Nullable final NamespaceContext aNamespaceContext,
                                  @Nonnull final String sXPath,
                                  @Nonnull final Document aDoc,
                                  @Nonnull final QName aReturnType)
  {
    return evalXPath (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                      sXPath,
                      aDoc,
                      aReturnType);
  }

  @Nullable
  public static Object evalXPath (@Nonnull final XPath aXPath,
                                  @Nonnull @Nonempty final String sXPath,
                                  @Nonnull final Document aDoc,
                                  @Nonnull final QName aReturnType)
  {
    ValueEnforcer.notNull (aXPath, "XPath");
    ValueEnforcer.notEmpty (sXPath, "XPathExpression");
    ValueEnforcer.notNull (aDoc, "Doc");
    ValueEnforcer.notNull (aReturnType, "ReturnType");

    try
    {
      final XPathExpression aXPathExpression = XPathHelper.createNewXPathExpresion (aXPath, sXPath);
      return aXPathExpression.evaluate (aDoc, aReturnType);
    }
    catch (final XPathExpressionException ex)
    {
      throw new IllegalArgumentException ("Failed to evaluate XPath expression '" +
                                          sXPath +
                                          "' with return type " +
                                          aReturnType.toString (),
                                          ex);
    }
  }

  @Nullable
  public static Double evalXPathToNumber (@Nullable final XPathVariableResolver aVariableResolver,
                                          @Nullable final XPathFunctionResolver aFunctionResolver,
                                          @Nullable final NamespaceContext aNamespaceContext,
                                          @Nonnull final String sXPath,
                                          @Nonnull final Document aDoc)
  {
    return evalXPathToNumber (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                              sXPath,
                              aDoc);
  }

  @Nullable
  public static Double evalXPathToNumber (@Nonnull final String sXPath, @Nonnull final Document aDoc)
  {
    return evalXPathToNumber (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Double evalXPathToNumber (@Nonnull final XPath aXPath,
                                          @Nonnull final String sXPath,
                                          @Nonnull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NUMBER);
    return (Double) aResult;
  }

  @Nullable
  public static String evalXPathToString (@Nullable final XPathVariableResolver aVariableResolver,
                                          @Nullable final XPathFunctionResolver aFunctionResolver,
                                          @Nullable final NamespaceContext aNamespaceContext,
                                          @Nonnull final String sXPath,
                                          @Nonnull final Document aDoc)
  {
    return evalXPathToString (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                              sXPath,
                              aDoc);
  }

  @Nullable
  public static String evalXPathToString (@Nonnull final String sXPath, @Nonnull final Document aDoc)
  {
    return evalXPathToString (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static String evalXPathToString (@Nonnull final XPath aXPath,
                                          @Nonnull final String sXPath,
                                          @Nonnull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.STRING);
    return (String) aResult;
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@Nullable final XPathVariableResolver aVariableResolver,
                                            @Nullable final XPathFunctionResolver aFunctionResolver,
                                            @Nullable final NamespaceContext aNamespaceContext,
                                            @Nonnull final String sXPath,
                                            @Nonnull final Document aDoc)
  {
    return evalXPathToBoolean (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                               sXPath,
                               aDoc);
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@Nonnull final String sXPath, @Nonnull final Document aDoc)
  {
    return evalXPathToBoolean (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@Nonnull final XPath aXPath,
                                            @Nonnull final String sXPath,
                                            @Nonnull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.BOOLEAN);
    return (Boolean) aResult;
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@Nullable final XPathVariableResolver aVariableResolver,
                                              @Nullable final XPathFunctionResolver aFunctionResolver,
                                              @Nullable final NamespaceContext aNamespaceContext,
                                              @Nonnull final String sXPath,
                                              @Nonnull final Document aDoc)
  {
    return evalXPathToNodeList (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                                sXPath,
                                aDoc);
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@Nonnull final String sXPath, @Nonnull final Document aDoc)
  {
    return evalXPathToNodeList (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@Nonnull final XPath aXPath,
                                              @Nonnull final String sXPath,
                                              @Nonnull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NODESET);
    return (NodeList) aResult;
  }

  @Nullable
  public static Node evalXPathToNode (@Nullable final XPathVariableResolver aVariableResolver,
                                      @Nullable final XPathFunctionResolver aFunctionResolver,
                                      @Nullable final NamespaceContext aNamespaceContext,
                                      @Nonnull final String sXPath,
                                      @Nonnull final Document aDoc)
  {
    return evalXPathToNode (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext),
                            sXPath,
                            aDoc);
  }

  @Nullable
  public static Node evalXPathToNode (@Nonnull final String sXPath, @Nonnull final Document aDoc)
  {
    return evalXPathToNode (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Node evalXPathToNode (@Nonnull final XPath aXPath,
                                      @Nonnull final String sXPath,
                                      @Nonnull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NODE);
    return (Node) aResult;
  }
}
