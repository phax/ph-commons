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
package com.helger.jaxb.builder;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.schema.IHasSchema;
import com.helger.xml.schema.XMLSchemaValidationHelper;

/**
 * Base interface describing a single JAXB based document type, independent of
 * the version and implementation.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IJAXBDocumentType extends IHasSchema, Serializable
{
  /**
   * @return The class implementing this document type. Never <code>null</code>.
   */
  @Nonnull
  Class <?> getImplementationClass ();

  /**
   * @return The list of all paths within the classpath where the main XSD file
   *         resides. Never <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <String> getAllXSDPaths ();

  /**
   * @return The non-<code>null</code> XML namespace of this JAXB document type.
   *         If the element has no namespace this method must also return the
   *         empty string.
   */
  @Nonnull
  String getNamespaceURI ();

  /**
   * @return The local name of the root element of an XML document of this type.
   *         Corresponds to the name of the implementation class (without a
   *         package).
   */
  @Nonnull
  @Nonempty
  String getLocalName ();

  /**
   * @return The resources from which the XSD can be read using the current
   *         class loader. Never <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <? extends IReadableResource> getAllXSDResources ()
  {
    return new CommonsArrayList <> (getAllXSDPaths (), ClassPathResource::new);
  }

  /**
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @return The resources from which the XSD can be read. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <? extends IReadableResource> getAllXSDResources (@Nullable final ClassLoader aClassLoader)
  {
    return new CommonsArrayList <> (getAllXSDPaths (), x -> new ClassPathResource (x, aClassLoader));
  }

  /**
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @return The compiled {@link Schema} object retrieved by the
   *         {@link com.helger.xml.schema.XMLSchemaCache}. May be
   *         <code>null</code> if no XSD was provided.
   */
  @Nullable
  Schema getSchema (@Nullable final ClassLoader aClassLoader);

  /**
   * @return The compiled {@link Validator} object retrieved from the schema to
   *         be obtained from {@link #getSchema()}.If this document type has no
   *         XML Schema that no {@link Validator} can be created and the return
   *         value is <code>null</code>.
   */
  @Nullable
  default Validator getValidator ()
  {
    return getValidator ((ClassLoader) null, (Locale) null);
  }

  /**
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @return The compiled {@link Validator} object retrieved from the schema to
   *         be obtained from {@link #getSchema(ClassLoader)}. If this document
   *         type has no XML Schema that no {@link Validator} can be created and
   *         the return value is <code>null</code>.
   */
  @Nullable
  default Validator getValidator (@Nullable final ClassLoader aClassLoader)
  {
    return getValidator (aClassLoader, (Locale) null);
  }

  /**
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @param aLocale
   *        The locale to use for error message creation. May be
   *        <code>null</code> to use the system locale.
   * @return The compiled {@link Validator} object retrieved from the schema to
   *         be obtained from {@link #getSchema(ClassLoader)}. If this document
   *         type has no XML Schema that no {@link Validator} can be created and
   *         the return value is <code>null</code>.
   * @since 9.0.1
   */
  @Nullable
  default Validator getValidator (@Nullable final ClassLoader aClassLoader, @Nullable final Locale aLocale)
  {
    final Schema aSchema = getSchema (aClassLoader);
    if (aSchema != null)
    {
      final Validator aValidator = aSchema.newValidator ();
      if (aValidator != null)
      {
        if (aLocale != null)
          EXMLParserProperty.GENERAL_LOCALE.applyTo (aValidator, aLocale);
        return aValidator;
      }
    }
    return null;
  }

  /**
   * Validate the passed XML instance against the XML Schema of this document
   * type using the default class loader.
   *
   * @param aXML
   *        The XML resource to be validated. May not be <code>null</code>.
   * @return A group of validation errors. Is empty if no error occurred.
   *         <code>null</code> is returned if this document type has no XSDs
   *         assigned and therefore not validation can take place.
   */
  @Nullable
  default IErrorList validateXML (@Nonnull final IReadableResource aXML)
  {
    return validateXML (aXML, (ClassLoader) null);
  }

  /**
   * Validate the passed XML instance against the XML Schema of this document
   * type.
   *
   * @param aXML
   *        The XML resource to be validated. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @return A group of validation errors. Is empty if no error occurred.
   *         <code>null</code> is returned if this document type has no XSDs
   *         assigned and therefore not validation can take place.
   */
  @Nullable
  default IErrorList validateXML (@Nonnull final IReadableResource aXML, @Nullable final ClassLoader aClassLoader)
  {
    final Schema aSchema = getSchema (aClassLoader);
    return aSchema == null ? null : XMLSchemaValidationHelper.validate (aSchema, aXML);
  }
}
