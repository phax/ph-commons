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

import java.lang.management.ManagementFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.management.JMException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;

/**
 * Some generic JMX utility classes
 *
 * @author Philip Helger
 */
@Immutable
public final class JMXHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JMXHelper.class);

  @SuppressWarnings ("unused")
  private static final JMXHelper s_aInstance = new JMXHelper ();

  private JMXHelper ()
  {}

  @Nonnull
  public static ESuccess exposeMBean (@Nonnull final Object aObject, @Nonnull final ObjectName aObjectName)
  {
    ValueEnforcer.notNull (aObject, "Object");
    ValueEnforcer.notNull (aObjectName, "ObjectName");

    try
    {
      ManagementFactory.getPlatformMBeanServer ().registerMBean (aObject, aObjectName);
      return ESuccess.SUCCESS;
    }
    catch (final JMException ex)
    {
      s_aLogger.error ("Error registering MBean with name " + aObjectName, ex);
      return ESuccess.FAILURE;
    }
  }

  @Nonnull
  public static ESuccess exposeMBeanWithAutoName (@Nonnull final Object aObj)
  {
    return exposeMBean (aObj, ObjectNameHelper.createWithDefaultProperties (aObj));
  }

  @Nonnull
  public static ESuccess exposeMBeanWithAutoName (@Nonnull final Object aObj, @Nonnull final String sName)
  {
    return exposeMBean (aObj, ObjectNameHelper.createWithDefaultProperties (aObj, sName));
  }
}
