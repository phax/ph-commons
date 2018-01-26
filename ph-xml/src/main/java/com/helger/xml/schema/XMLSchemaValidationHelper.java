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
package com.helger.xml.schema;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.sax.WrappedCollectingSAXErrorHandler;
import com.helger.xml.transform.TransformSourceFactory;

/**
 * A helper class for simple XSD validation.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLSchemaValidationHelper
{
  @PresentForCodeCoverage
  private static final XMLSchemaValidationHelper s_aInstance = new XMLSchemaValidationHelper ();

  private XMLSchemaValidationHelper ()
  {}

  @Nonnull
  public static IErrorList validate (@Nonnull final IReadableResource aSchema, @Nonnull final IReadableResource aXML)
  {
    return validate (new IReadableResource [] { aSchema }, aXML);
  }

  @Nonnull
  public static IErrorList validate (@Nonnull @Nonempty final IReadableResource [] aSchemas,
                                     @Nonnull final IReadableResource aXML)
  {
    ValueEnforcer.notNull (aXML, "XML");

    return validate (aSchemas, TransformSourceFactory.create (aXML));
  }

  @Nonnull
  public static IErrorList validate (@Nonnull final Schema aSchema, @Nonnull final IReadableResource aXML)
  {
    ValueEnforcer.notNull (aXML, "XML");

    return validate (aSchema, TransformSourceFactory.create (aXML));
  }

  @Nonnull
  public static IErrorList validate (@Nonnull @Nonempty final IReadableResource aSchema, @Nonnull final Source aXML)
  {
    return validate (new IReadableResource [] { aSchema }, aXML);
  }

  @Nonnull
  public static IErrorList validate (@Nonnull @Nonempty final IReadableResource [] aSchemas, @Nonnull final Source aXML)
  {
    // Get Schema from XMLSchemaCache
    return validate (XMLSchemaCache.getInstance ().getSchema (aSchemas), aXML);
  }

  @Nonnull
  public static IErrorList validate (@Nonnull final Schema aSchema, @Nonnull final Source aXML)
  {
    final ErrorList aErrorList = new ErrorList ();
    validate (aSchema, aXML, aErrorList);
    return aErrorList;
  }

  /**
   * Validate the passed XML against the passed XSD and put all errors in the
   * passed error list.
   *
   * @param aSchema
   *        The source XSD. May not be <code>null</code>.
   * @param aXML
   *        The XML to be validated. May not be <code>null</code>.
   * @param aErrorList
   *        The error list to be filled. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If XSD validation failed with an exception
   * @since 8.5.3
   */
  public static void validate (@Nonnull final Schema aSchema,
                               @Nonnull final Source aXML,
                               @Nonnull final ErrorList aErrorList)
  {
    validate (aSchema, aXML, aErrorList, (Locale) null);
  }

  /**
   * Validate the passed XML against the passed XSD and put all errors in the
   * passed error list.
   *
   * @param aSchema
   *        The source XSD. May not be <code>null</code>.
   * @param aXML
   *        The XML to be validated. May not be <code>null</code>.
   * @param aErrorList
   *        The error list to be filled. May not be <code>null</code>.
   * @param aLocale
   *        The locale to use for error messages. May be <code>null</code> to
   *        use the system default locale.
   * @throws IllegalArgumentException
   *         If XSD validation failed with an exception
   * @since 9.0.1
   */
  public static void validate (@Nonnull final Schema aSchema,
                               @Nonnull final Source aXML,
                               @Nonnull final ErrorList aErrorList,
                               @Nullable final Locale aLocale)
  {
    ValueEnforcer.notNull (aSchema, "Schema");
    ValueEnforcer.notNull (aXML, "XML");
    ValueEnforcer.notNull (aErrorList, "ErrorList");

    // Build the validator
    final Validator aValidator = aSchema.newValidator ();
    if (aLocale != null)
      EXMLParserProperty.GENERAL_LOCALE.applyTo (aValidator, aLocale);
    aValidator.setErrorHandler (new WrappedCollectingSAXErrorHandler (aErrorList));
    try
    {
      aValidator.validate (aXML, null);
    }
    catch (final Exception ex)
    {
      // Most likely the input XML document is invalid
      throw new IllegalArgumentException ("Failed to validate the XML " + aXML + " against " + aSchema, ex);
    }
  }
}
