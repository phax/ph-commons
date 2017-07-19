/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.http;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsIterable;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.commons.collection.ext.ICommonsOrderedSet;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.net.PDTWebDateHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstracts HTTP header interface for external usage.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class HTTPHeaderMap implements
                           IHasSize,
                           ICommonsIterable <Map.Entry <String, ICommonsList <String>>>,
                           ICloneable <HTTPHeaderMap>
{
  private final ICommonsOrderedMap <String, ICommonsList <String>> m_aHeaders = new CommonsLinkedHashMap <> ();

  /**
   * Default constructor.
   */
  public HTTPHeaderMap ()
  {}

  /**
   * Copy constructor.
   *
   * @param aOther
   *        Map to copy from. May not be <code>null</code>.
   * @since 6.0.5
   */
  public HTTPHeaderMap (@Nonnull final HTTPHeaderMap aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_aHeaders.putAll (aOther.m_aHeaders);
  }

  /**
   * Remove all contained headers.
   *
   * @return {@link EChange}.
   * @since 6.0.5
   */
  @Nonnull
  public EChange clear ()
  {
    if (m_aHeaders.isEmpty ())
      return EChange.UNCHANGED;
    m_aHeaders.clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  private ICommonsList <String> _getOrCreateHeaderList (@Nonnull @Nonempty final String sName)
  {
    return m_aHeaders.computeIfAbsent (sName, x -> new CommonsArrayList <> (2));
  }

  private void _setHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (sValue, "Value");

    _getOrCreateHeaderList (sName).set (sValue);
  }

  private void _addHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (sValue, "Value");

    _getOrCreateHeaderList (sName).add (sValue);
  }

  /**
   * Set the passed header as is.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        The value to be set. May not be <code>null</code>.
   */
  public void setHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    _setHeader (sName, sValue);
  }

  /**
   * Add the passed header as is.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        The value to be set. May not be <code>null</code>.
   */
  public void addHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    _addHeader (sName, sValue);
  }

  @Nonnull
  public static String getDateTimeAsString (@Nonnull final ZonedDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "DateTime");

    // This method internally converts the date to UTC
    return PDTWebDateHelper.getAsStringRFC822 (aDT);
  }

  @Nonnull
  public static String getDateTimeAsString (@Nonnull final LocalDateTime aLDT)
  {
    ValueEnforcer.notNull (aLDT, "DateTime");

    // This method internally converts the date to UTC
    return PDTWebDateHelper.getAsStringRFC822 (aLDT);
  }

  /**
   * Set the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nMillis
   *        The milliseconds to set as a date.
   */
  public void setDateHeader (@Nonnull @Nonempty final String sName, final long nMillis)
  {
    setDateHeader (sName, PDTFactory.createZonedDateTime (nMillis));
  }

  /**
   * Set the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aLD
   *        The LocalDate to set as a date. The time is set to start of day. May
   *        not be <code>null</code>.
   * @since 6.0.5
   */
  public void setDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final LocalDate aLD)
  {
    setDateHeader (sName, PDTFactory.createZonedDateTime (aLD));
  }

  /**
   * Set the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aLDT
   *        The LocalDateTime to set as a date. May not be <code>null</code>.
   * @since 6.0.5
   */
  public void setDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final LocalDateTime aLDT)
  {
    setDateHeader (sName, PDTFactory.createZonedDateTime (aLDT));
  }

  /**
   * Set the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aDT
   *        The DateTime to set as a date. May not be <code>null</code>.
   */
  public void setDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final ZonedDateTime aDT)
  {
    _setHeader (sName, getDateTimeAsString (aDT));
  }

  /**
   * Add the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nMillis
   *        The milliseconds to set as a date.
   */
  public void addDateHeader (@Nonnull @Nonempty final String sName, final long nMillis)
  {
    addDateHeader (sName, PDTFactory.createZonedDateTime (nMillis));
  }

  /**
   * Add the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aLD
   *        The LocalDate to set as a date. The time is set to start of day. May
   *        not be <code>null</code>.
   * @since 6.0.5
   */
  public void addDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final LocalDate aLD)
  {
    addDateHeader (sName, PDTFactory.createZonedDateTime (aLD));
  }

  /**
   * Add the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aLDT
   *        The LocalDateTime to set as a date. May not be <code>null</code>.
   * @since 6.0.5
   */
  public void addDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final LocalDateTime aLDT)
  {
    addDateHeader (sName, PDTFactory.createZonedDateTime (aLDT));
  }

  /**
   * Add the passed header as a date header.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aDT
   *        The DateTime to set as a date. May not be <code>null</code>.
   */
  public void addDateHeader (@Nonnull @Nonempty final String sName, @Nonnull final ZonedDateTime aDT)
  {
    _addHeader (sName, getDateTimeAsString (aDT));
  }

  /**
   * Set the passed header as a number.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nValue
   *        The value to be set. May not be <code>null</code>.
   */
  public void setIntHeader (@Nonnull @Nonempty final String sName, final int nValue)
  {
    _setHeader (sName, Integer.toString (nValue));
  }

  /**
   * Add the passed header as a number.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nValue
   *        The value to be set. May not be <code>null</code>.
   */
  public void addIntHeader (@Nonnull @Nonempty final String sName, final int nValue)
  {
    _addHeader (sName, Integer.toString (nValue));
  }

  /**
   * Set the passed header as a number.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nValue
   *        The value to be set. May not be <code>null</code>.
   * @since 6.0.5
   */
  public void setLongHeader (@Nonnull @Nonempty final String sName, final long nValue)
  {
    _setHeader (sName, Long.toString (nValue));
  }

  /**
   * Add the passed header as a number.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param nValue
   *        The value to be set. May not be <code>null</code>.
   * @since 6.0.5
   */
  public void addLongHeader (@Nonnull @Nonempty final String sName, final long nValue)
  {
    _addHeader (sName, Long.toString (nValue));
  }

  /**
   * Add all headers from the passed map.
   *
   * @param aOther
   *        The header map to add. May not be <code>null</code>.
   * @since 6.0.5
   */
  public void addAllHeaders (@Nonnull final HTTPHeaderMap aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");

    aOther.forEachSingleHeader ( (k, v) -> _addHeader (k, v));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, ICommonsList <String>> getAllHeaders ()
  {
    return m_aHeaders.getClone ();
  }

  /**
   * @return A copy of all contained header names. Never <code>null</code>.
   * @since 8.7.0
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllHeaderNames ()
  {
    return m_aHeaders.copyOfKeySet ();
  }

  /**
   * Get all header values doing a case sensitive match
   *
   * @param sName
   *        The name to be searched.
   * @return The list with all matching values. Never <code>null</code> but
   *         maybe empty.
   * @since 6.0.5
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllHeaderValues (@Nullable final String sName)
  {
    return new CommonsArrayList <> (m_aHeaders.get (sName));
  }

  /**
   * Get all header values matching the provided header name filter.
   *
   * @param aNameFilter
   *        The name filter to be applied. May not be <code>null</code>.
   * @return The list with all matching values of the first header matching the
   *         passed filter. Never <code>null</code> but maybe empty.
   * @since 8.5.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllHeaderValuesByName (@Nonnull final Predicate <String> aNameFilter)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    for (final Map.Entry <String, ICommonsList <String>> aEntry : m_aHeaders.entrySet ())
      if (aNameFilter.test (aEntry.getKey ()))
      {
        ret.addAll (aEntry.getValue ());
        break;
      }
    return ret;
  }

  /**
   * Get the first header value of the first header matching the provided header
   * name filter.
   *
   * @param aNameFilter
   *        The name filter to be applied. May not be <code>null</code>.
   * @return <code>null</code> if no such header exists, or if the has no value.
   * @since 8.5.1
   */
  @Nullable
  public String getFirstHeaderValueByName (@Nonnull final Predicate <String> aNameFilter)
  {
    for (final Map.Entry <String, ICommonsList <String>> aEntry : m_aHeaders.entrySet ())
      if (aNameFilter.test (aEntry.getKey ()))
        return aEntry.getValue ().getFirst ();
    return null;
  }

  /**
   * Get all header values doing a case insensitive match
   *
   * @param sName
   *        The name to be searched.
   * @return The list with all matching values. Never <code>null</code> but
   *         maybe empty.
   * @since 6.0.5
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllHeaderValuesCaseInsensitive (@Nullable final String sName)
  {
    if (StringHelper.hasNoText (sName))
      return new CommonsArrayList <> ();
    return getAllHeaderValuesByName (x -> sName.equalsIgnoreCase (x));
  }

  /**
   * Get the first header value doing a case insensitive match
   *
   * @param sName
   *        The name to be searched.
   * @return The first matching value or <code>null</code>.
   * @since 8.5.1
   */
  @Nullable
  public String getFirstHeaderValueCaseInsensitive (@Nullable final String sName)
  {
    if (StringHelper.hasNoText (sName))
      return null;
    return getFirstHeaderValueByName (x -> sName.equalsIgnoreCase (x));
  }

  public boolean containsHeaders (@Nullable final String sName)
  {
    return m_aHeaders.containsKey (sName);
  }

  public boolean containsHeadersCaseInsensitive (@Nullable final String sName)
  {
    return StringHelper.hasText (sName) &&
           CollectionHelper.containsAny (m_aHeaders.keySet (), x -> sName.equalsIgnoreCase (x));
  }

  /**
   * Remove all header values with the provided name
   * 
   * @param sName
   *        The name to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeHeaders (@Nullable final String sName)
  {
    return EChange.valueOf (sName != null && m_aHeaders.remove (sName) != null);
  }

  @Nonnull
  public EChange removeHeader (@Nullable final String sName, @Nullable final String sValue)
  {
    final ICommonsList <String> aValues = m_aHeaders.get (sName);
    final boolean bRemoved = aValues != null && aValues.remove (sValue);
    if (bRemoved && aValues.isEmpty ())
    {
      // If the last value was removed, remove the whole header
      m_aHeaders.remove (sName);
    }

    return EChange.valueOf (bRemoved);
  }

  @Nonnull
  public Iterator <Map.Entry <String, ICommonsList <String>>> iterator ()
  {
    return m_aHeaders.entrySet ().iterator ();
  }

  public boolean isEmpty ()
  {
    return m_aHeaders.isEmpty ();
  }

  @Nonnegative
  public int getSize ()
  {
    return m_aHeaders.size ();
  }

  /**
   * Invoke the provided consumer for every name/value pair.
   *
   * @param aConsumer
   *        Consumer to be invoked. May not be <code>null</code>.
   * @since 8.7.3
   */
  public void forEachSingleHeader (@Nonnull final BiConsumer <String, String> aConsumer)
  {
    for (final Map.Entry <String, ICommonsList <String>> aEntry : m_aHeaders.entrySet ())
    {
      final String sKey = aEntry.getKey ();
      for (final String sValue : aEntry.getValue ())
        aConsumer.accept (sKey, sValue);
    }
  }

  /**
   * @since 6.0.5
   */
  @Nonnull
  @ReturnsMutableCopy
  public HTTPHeaderMap getClone ()
  {
    return new HTTPHeaderMap (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final HTTPHeaderMap rhs = (HTTPHeaderMap) o;
    return m_aHeaders.equals (rhs.m_aHeaders);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aHeaders).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Headers", m_aHeaders).getToString ();
  }
}
