/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.xpath;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Utility class to evaluate XPath expressions more easily
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class XPathExpressionHelper
{
  @PresentForCodeCoverage
  private static final XPathExpressionHelper INSTANCE = new XPathExpressionHelper ();

  private XPathExpressionHelper ()
  {}

  @Nullable
  public static Object evalXPath (@Nullable final XPathVariableResolver aVariableResolver,
                                  @Nullable final XPathFunctionResolver aFunctionResolver,
                                  @Nullable final NamespaceContext aNamespaceContext,
                                  @NonNull final String sXPath,
                                  @NonNull final Document aDoc,
                                  @NonNull final QName aReturnType)
  {
    return evalXPath (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc, aReturnType);
  }

  @Nullable
  public static Object evalXPath (@NonNull final XPath aXPath,
                                  @NonNull @Nonempty final String sXPath,
                                  @NonNull final Document aDoc,
                                  @NonNull final QName aReturnType)
  {
    ValueEnforcer.notNull (aXPath, "XPath");
    ValueEnforcer.notEmpty (sXPath, "XPathExpression");
    ValueEnforcer.notNull (aDoc, "Doc");
    ValueEnforcer.notNull (aReturnType, "ReturnType");

    try
    {
      final XPathExpression aXPathExpression = XPathHelper.createNewXPathExpression (aXPath, sXPath);
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
                                          @NonNull final String sXPath,
                                          @NonNull final Document aDoc)
  {
    return evalXPathToNumber (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc);
  }

  @Nullable
  public static Double evalXPathToNumber (@NonNull final String sXPath, @NonNull final Document aDoc)
  {
    return evalXPathToNumber (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Double evalXPathToNumber (@NonNull final XPath aXPath, @NonNull final String sXPath, @NonNull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NUMBER);
    return (Double) aResult;
  }

  @Nullable
  public static String evalXPathToString (@Nullable final XPathVariableResolver aVariableResolver,
                                          @Nullable final XPathFunctionResolver aFunctionResolver,
                                          @Nullable final NamespaceContext aNamespaceContext,
                                          @NonNull final String sXPath,
                                          @NonNull final Document aDoc)
  {
    return evalXPathToString (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc);
  }

  @Nullable
  public static String evalXPathToString (@NonNull final String sXPath, @NonNull final Document aDoc)
  {
    return evalXPathToString (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static String evalXPathToString (@NonNull final XPath aXPath, @NonNull final String sXPath, @NonNull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.STRING);
    return (String) aResult;
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@Nullable final XPathVariableResolver aVariableResolver,
                                            @Nullable final XPathFunctionResolver aFunctionResolver,
                                            @Nullable final NamespaceContext aNamespaceContext,
                                            @NonNull final String sXPath,
                                            @NonNull final Document aDoc)
  {
    return evalXPathToBoolean (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc);
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@NonNull final String sXPath, @NonNull final Document aDoc)
  {
    return evalXPathToBoolean (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Boolean evalXPathToBoolean (@NonNull final XPath aXPath, @NonNull final String sXPath, @NonNull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.BOOLEAN);
    return (Boolean) aResult;
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@Nullable final XPathVariableResolver aVariableResolver,
                                              @Nullable final XPathFunctionResolver aFunctionResolver,
                                              @Nullable final NamespaceContext aNamespaceContext,
                                              @NonNull final String sXPath,
                                              @NonNull final Document aDoc)
  {
    return evalXPathToNodeList (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc);
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@NonNull final String sXPath, @NonNull final Document aDoc)
  {
    return evalXPathToNodeList (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static NodeList evalXPathToNodeList (@NonNull final XPath aXPath, @NonNull final String sXPath, @NonNull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NODESET);
    return (NodeList) aResult;
  }

  @Nullable
  public static Node evalXPathToNode (@Nullable final XPathVariableResolver aVariableResolver,
                                      @Nullable final XPathFunctionResolver aFunctionResolver,
                                      @Nullable final NamespaceContext aNamespaceContext,
                                      @NonNull final String sXPath,
                                      @NonNull final Document aDoc)
  {
    return evalXPathToNode (XPathHelper.createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath, aDoc);
  }

  @Nullable
  public static Node evalXPathToNode (@NonNull final String sXPath, @NonNull final Document aDoc)
  {
    return evalXPathToNode (XPathHelper.createNewXPath (), sXPath, aDoc);
  }

  @Nullable
  public static Node evalXPathToNode (@NonNull final XPath aXPath, @NonNull final String sXPath, @NonNull final Document aDoc)
  {
    final Object aResult = evalXPath (aXPath, sXPath, aDoc, XPathConstants.NODE);
    return (Node) aResult;
  }
}
