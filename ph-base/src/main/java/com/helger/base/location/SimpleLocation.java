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
package com.helger.base.location;

import javax.xml.stream.Location;
import javax.xml.transform.SourceLocator;

import org.jspecify.annotations.Nullable;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.functional.Predicates;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Default implementation of the {@link ILocation} interface. The implementation
 * is immutable.
 *
 * @author Philip Helger
 */
@Immutable
public class SimpleLocation implements ILocation
{
  /** A constant representing no location */
  public static final SimpleLocation NO_LOCATION = new SimpleLocation (null);

  private final String m_sResourceID;
  private final int m_nColumnNumber;
  private final int m_nLineNumber;

  /**
   * Constructor with resource ID only.
   *
   * @param sResourceID
   *        The resource ID. May be <code>null</code>.
   */
  public SimpleLocation (@Nullable final String sResourceID)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER);
  }

  /**
   * Constructor with resource ID, line number and column number.
   *
   * @param sResourceID
   *        The resource ID. May be <code>null</code>.
   * @param nLineNumber
   *        The line number. Use {@link ILocation#ILLEGAL_NUMBER} if not available.
   * @param nColumnNumber
   *        The column number. Use {@link ILocation#ILLEGAL_NUMBER} if not available.
   */
  public SimpleLocation (@Nullable final String sResourceID, final int nLineNumber, final int nColumnNumber)
  {
    m_sResourceID = sResourceID;
    m_nLineNumber = nLineNumber;
    m_nColumnNumber = nColumnNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getResourceID ()
  {
    return m_sResourceID;
  }

  /**
   * {@inheritDoc}
   */
  public int getLineNumber ()
  {
    return m_nLineNumber;
  }

  /**
   * {@inheritDoc}
   */
  public int getColumnNumber ()
  {
    return m_nColumnNumber;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SimpleLocation rhs = (SimpleLocation) o;
    return EqualsHelper.equals (m_sResourceID, rhs.m_sResourceID) &&
           m_nLineNumber == rhs.m_nLineNumber &&
           m_nColumnNumber == rhs.m_nColumnNumber;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sResourceID).append (m_nLineNumber).append (m_nColumnNumber).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).appendIfNotNull ("ResourceID", m_sResourceID)
                                       .appendIf ("LineNumber", m_nLineNumber, Predicates.intIsGE0 ())
                                       .appendIf ("ColumnNumber", m_nColumnNumber, Predicates.intIsGE0 ())
                                       .getToString ();
  }

  /**
   * Create a {@link SimpleLocation} from a SAX {@link Locator}.
   *
   * @param aLocator
   *        The SAX locator. May be <code>null</code>.
   * @return <code>null</code> if the passed locator is <code>null</code>.
   */
  @Nullable
  public static SimpleLocation create (@Nullable final Locator aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (), "/", aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  /**
   * Create a {@link SimpleLocation} from a JAXP {@link SourceLocator}.
   *
   * @param aLocator
   *        The source locator. May be <code>null</code>.
   * @return <code>null</code> if the passed locator is <code>null</code>.
   */
  @Nullable
  public static SimpleLocation create (@Nullable final SourceLocator aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (), "/", aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  /**
   * Create a {@link SimpleLocation} from a {@link SAXParseException}.
   *
   * @param aLocator
   *        The SAX parse exception. May be <code>null</code>.
   * @return <code>null</code> if the passed exception is <code>null</code>.
   */
  @Nullable
  public static SimpleLocation create (@Nullable final SAXParseException aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (), "/", aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  /**
   * Create a {@link SimpleLocation} from a StAX {@link Location}.
   *
   * @param aLocator
   *        The StAX location. May be <code>null</code>.
   * @return <code>null</code> if the passed location is <code>null</code>.
   */
  @Nullable
  public static SimpleLocation create (@Nullable final Location aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (), "/", aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }
}
