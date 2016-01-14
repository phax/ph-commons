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
package com.helger.lesscommons.jmx;

import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.JMException;
import javax.management.ObjectName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.exception.LoggedRuntimeException;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;

/**
 * Utility class to create JMX {@link ObjectName} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class ObjectNameHelper
{
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static String s_sDefaultJMXDomain = CJMX.PH_JMX_DOMAIN;

  @PresentForCodeCoverage
  private static final ObjectNameHelper s_aInstance = new ObjectNameHelper ();

  private ObjectNameHelper ()
  {}

  /**
   * Set the default JMX domain
   *
   * @param sDefaultJMXDomain
   *        The new JMX domain. May neither be <code>null</code> nor empty nor
   *        may it contains ":" or " "
   */
  public static void setDefaultJMXDomain (@Nonnull @Nonempty final String sDefaultJMXDomain)
  {
    ValueEnforcer.notEmpty (sDefaultJMXDomain, "DefaultJMXDomain");
    if (sDefaultJMXDomain.indexOf (':') >= 0 || sDefaultJMXDomain.indexOf (' ') >= 0)
      throw new IllegalArgumentException ("defaultJMXDomain contains invalid chars: " + sDefaultJMXDomain);

    s_aRWLock.writeLocked ( () -> s_sDefaultJMXDomain = sDefaultJMXDomain);
  }

  /**
   * @return The default JMX domain to be used for {@link ObjectName} instances.
   *         The default value is {@link CJMX#PH_JMX_DOMAIN}.
   */
  @Nonnull
  @Nonempty
  public static String getDefaultJMXDomain ()
  {
    return s_aRWLock.readLocked ( () -> s_sDefaultJMXDomain);
  }

  @Nonnull
  public static ObjectName create (@Nonnull @Nonempty final Hashtable <String, String> aParams)
  {
    ValueEnforcer.notEmpty (aParams, "Params");

    try
    {
      return new ObjectName (getDefaultJMXDomain (), aParams);
    }
    catch (final JMException ex)
    {
      throw LoggedRuntimeException.newException ("Failed to create ObjectName with parameter " + aParams, ex);
    }
  }

  @Nonnull
  public static ObjectName create (@Nonnull @Nonempty final Map <String, String> aParams)
  {
    ValueEnforcer.notEmpty (aParams, "Params");

    return create (new Hashtable <> (aParams));
  }

  /**
   * Create a clean property value applicable for an {@link ObjectName} property
   * value by replacing the special chars ":" and "," with "." and "//" with
   * "__". If the input value contains a blank, the quotes value is returned.
   *
   * @param sPropertyValue
   *        The original property value. May not be <code>null</code>.
   * @return The modified property value applicable for {@link ObjectName}.
   * @see ObjectName#quote(String)
   */
  @Nonnull
  public static String getCleanPropertyValue (@Nonnull final String sPropertyValue)
  {
    // If a blank is contained, simply quote it
    if (sPropertyValue.indexOf (' ') != -1)
      return ObjectName.quote (sPropertyValue);

    // ":" is prohibited
    // "," is the property separator
    // "//" is according to the specs reserved for future use
    String ret = sPropertyValue;
    ret = StringHelper.replaceAll (ret, ':', '.');
    ret = StringHelper.replaceAll (ret, ',', '.');
    ret = StringHelper.replaceAll (ret, "//", "__");
    return ret;
  }

  /**
   * Create a standard {@link ObjectName} using the default domain and only the
   * "type" property. The type property is the class local name of the specified
   * object.
   *
   * @param aObj
   *        The object from which the name is to be created.
   * @return The non-<code>null</code> {@link ObjectName}.
   */
  @Nonnull
  public static ObjectName createWithDefaultProperties (@Nonnull final Object aObj)
  {
    ValueEnforcer.notNull (aObj, "Object");

    final Hashtable <String, String> aParams = new Hashtable <> ();
    aParams.put (CJMX.PROPERTY_TYPE, ClassHelper.getClassLocalName (aObj));
    return create (aParams);
  }

  /**
   * Create a standard {@link ObjectName} using the default domain and the
   * "type" and "name" properties. The type property is the class local name of
   * the specified object.
   *
   * @param aObj
   *        The object from which the name is to be created.
   * @param sName
   *        The value of the "name" JMX property
   * @return The non-<code>null</code> {@link ObjectName}.
   */
  @Nonnull
  public static ObjectName createWithDefaultProperties (@Nonnull final Object aObj, @Nonnull final String sName)
  {
    ValueEnforcer.notNull (aObj, "Object");
    ValueEnforcer.notNull (sName, "Name");

    final Hashtable <String, String> aParams = new Hashtable <> ();
    aParams.put (CJMX.PROPERTY_TYPE, ClassHelper.getClassLocalName (aObj));
    aParams.put (CJMX.PROPERTY_NAME, getCleanPropertyValue (sName));
    return create (aParams);
  }
}
