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
package com.helger.commons.location;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.stream.Location;
import javax.xml.transform.SourceLocator;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

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

  public SimpleLocation (@Nullable final String sResourceID)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER);
  }

  public SimpleLocation (@Nullable final String sResourceID, final int nLineNumber, final int nColumnNumber)
  {
    m_sResourceID = sResourceID;
    m_nLineNumber = nLineNumber;
    m_nColumnNumber = nColumnNumber;
  }

  @Nullable
  public String getResourceID ()
  {
    return m_sResourceID;
  }

  public int getLineNumber ()
  {
    return m_nLineNumber;
  }

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
    return new HashCodeGenerator (this).append (m_sResourceID)
                                       .append (m_nLineNumber)
                                       .append (m_nColumnNumber)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).appendIfNotNull ("ResourceID", m_sResourceID)
                                       .appendIf ("LineNumber", m_nLineNumber, (final int x) -> x > ILLEGAL_NUMBER)
                                       .appendIf ("ColumnNumber", m_nColumnNumber, (final int x) -> x > ILLEGAL_NUMBER)
                                       .getToString ();
  }

  @Nullable
  public static SimpleLocation create (@Nullable final Locator aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (),
                                                                     "/",
                                                                     aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  @Nullable
  public static SimpleLocation create (@Nullable final SourceLocator aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (),
                                                                     "/",
                                                                     aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  @Nullable
  public static SimpleLocation create (@Nullable final SAXParseException aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (),
                                                                     "/",
                                                                     aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }

  @Nullable
  public static SimpleLocation create (@Nullable final Location aLocator)
  {
    if (aLocator == null)
      return null;

    return new SimpleLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (),
                                                                     "/",
                                                                     aLocator.getSystemId ()),
                               aLocator.getLineNumber (),
                               aLocator.getColumnNumber ());
  }
}
