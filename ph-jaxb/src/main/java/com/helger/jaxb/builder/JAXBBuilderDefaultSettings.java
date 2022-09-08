/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.jaxb.builder;

import java.nio.charset.Charset;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.NamespaceContext;

import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.jaxb.validation.LoggingValidationEventHandler;
import com.helger.xml.serialize.write.XMLWriterSettings;

import jakarta.xml.bind.ValidationEventHandler;

/**
 * A class containing some default settings for the various JAXB builders.<br>
 * Changes made in this class only effects instances that are created
 * afterwards. Existing instances are never changed.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class JAXBBuilderDefaultSettings
{
  public static final boolean DEFAULT_USE_CONTEXT_CACHE = true;
  public static final ValidationEventHandler DEFAULT_VALIDATION_EVENT_HANDLER = LoggingValidationEventHandler.DEFAULT_INSTANCE;
  public static final boolean DEFAULT_FORMATTED_OUTPUT = false;
  public static final Charset DEFAULT_CHARSET = XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ;

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static boolean s_bUseContextCache = DEFAULT_USE_CONTEXT_CACHE;
  @GuardedBy ("RW_LOCK")
  private static ValidationEventHandler s_aEventHandler = DEFAULT_VALIDATION_EVENT_HANDLER;
  @GuardedBy ("RW_LOCK")
  private static NamespaceContext s_aNamespaceContext;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bFormattedOutput = DEFAULT_FORMATTED_OUTPUT;
  @GuardedBy ("RW_LOCK")
  private static Charset s_aCharset = DEFAULT_CHARSET;
  @GuardedBy ("RW_LOCK")
  private static String s_sIndentString;
  @GuardedBy ("RW_LOCK")
  private static String s_sSchemaLocation;
  @GuardedBy ("RW_LOCK")
  private static String s_sNoNamespaceSchemaLocation;

  private JAXBBuilderDefaultSettings ()
  {}

  /**
   * Enable or disable the usage of the JAXBContext cache. For performance
   * reasons it is recommended to enable it. By default it is enabled.
   *
   * @param bUseContextCache
   *        <code>true</code> to enable it, <code>false</code> to disable it.
   */
  public static void setDefaultUseContextCache (final boolean bUseContextCache)
  {
    RW_LOCK.writeLocked ( () -> s_bUseContextCache = bUseContextCache);
  }

  /**
   * @return <code>true</code> if the JAXBContext cache should be used. Default
   *         is <code>true</code>.
   */
  public static boolean isDefaultUseContextCache ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bUseContextCache);
  }

  /**
   * Set a global event handler that should be passed to all read/write actions.
   * If no global validation handler is defined, a default logging event handler
   * is used.
   *
   * @param aEventHandler
   *        The new default event handler. May be <code>null</code> to indicate,
   *        that the default handler should be used.
   */
  public static void setDefaultValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aEventHandler = aEventHandler);
  }

  /**
   * @return The current default validation event handler. May be
   *         <code>null</code> to indicate that no global validation event
   *         handler is defined, and the default validation handler is used.
   */
  @Nullable
  public static ValidationEventHandler getDefaultValidationEventHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aEventHandler);
  }

  /**
   * Set the default namespace context (prefix to namespace URL mapping) to be
   * used.
   *
   * @param aNamespaceContext
   *        The namespace context to be used by default. May be
   *        <code>null</code>.
   */
  public static void setDefaultNamespaceContext (@Nullable final NamespaceContext aNamespaceContext)
  {
    RW_LOCK.writeLocked ( () -> s_aNamespaceContext = aNamespaceContext);
  }

  /**
   * @return The special JAXB namespace context to be used. <code>null</code> by
   *         default.
   */
  @Nullable
  public static NamespaceContext getDefaultNamespaceContext ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aNamespaceContext);
  }

  /**
   * Enable or disable the formatting of the output.
   *
   * @param bFormattedOutput
   *        <code>true</code> to enable it, <code>false</code> to disable it.
   */
  public static void setDefaultFormattedOutput (final boolean bFormattedOutput)
  {
    RW_LOCK.writeLocked ( () -> s_bFormattedOutput = bFormattedOutput);
  }

  /**
   * @return <code>true</code> if the JAXB output should be formatted. Only for
   *         writers. Default is <code>false</code>. The JDK implementation does
   *         not format by default.
   */
  public static boolean isDefaultFormattedOutput ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bFormattedOutput);
  }

  /**
   * Set the default charset to be used for writing JAXB objects.
   *
   * @param aCharset
   *        The charset to be used by default. May be <code>null</code>.
   */
  public static void setDefaultCharset (@Nullable final Charset aCharset)
  {
    RW_LOCK.writeLocked ( () -> s_aCharset = aCharset);
  }

  /**
   * @return The special JAXB Charset to be used for writing. <code>null</code>
   *         by default. The JDK implementation uses UTF-8 by default.
   */
  @Nullable
  public static Charset getDefaultCharset ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aCharset);
  }

  /**
   * Set the default indent string to be used for writing JAXB objects.
   *
   * @param sIndentString
   *        The indent string to be used by default. May be <code>null</code>.
   */
  public static void setDefaultIndentString (@Nullable final String sIndentString)
  {
    RW_LOCK.writeLocked ( () -> s_sIndentString = sIndentString);
  }

  /**
   * @return The JAXB indentation string to be used for writing.
   *         <code>null</code> by default. Only used when formatted output is
   *         used. The JDK implementation uses 4 spaces by default.
   */
  @Nullable
  public static String getDefaultIndentString ()
  {
    return RW_LOCK.readLockedGet ( () -> s_sIndentString);
  }

  /**
   * Set the schema location to be used for writing JAXB objects.
   *
   * @param sSchemaLocation
   *        The schema location to be used by default. May be <code>null</code>.
   * @since 8.6.0
   */
  public static void setDefaultSchemaLocation (@Nullable final String sSchemaLocation)
  {
    RW_LOCK.writeLocked ( () -> s_sSchemaLocation = sSchemaLocation);
  }

  /**
   * @return The JAXB schema location to be used for writing. <code>null</code>
   *         by default.
   * @since 8.6.0
   */
  @Nullable
  public static String getDefaultSchemaLocation ()
  {
    return RW_LOCK.readLockedGet ( () -> s_sSchemaLocation);
  }

  /**
   * Set the no namespace schema location to be used for writing JAXB objects.
   *
   * @param sNoNamespaceSchemaLocation
   *        The no namespace schema location to be used by default. May be
   *        <code>null</code>.
   * @since 9.0.0
   */
  public static void setDefaultNoNamespaceSchemaLocation (@Nullable final String sNoNamespaceSchemaLocation)
  {
    RW_LOCK.writeLocked ( () -> s_sNoNamespaceSchemaLocation = sNoNamespaceSchemaLocation);
  }

  /**
   * @return The JAXB no namespace schema location to be used for writing.
   *         <code>null</code> by default.
   * @since 9.0.0
   */
  @Nullable
  public static String getDefaultNoNamespaceSchemaLocation ()
  {
    return RW_LOCK.readLockedGet ( () -> s_sNoNamespaceSchemaLocation);
  }
}
