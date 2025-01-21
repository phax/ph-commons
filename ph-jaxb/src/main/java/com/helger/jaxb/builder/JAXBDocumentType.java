/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.validation.Schema;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.schema.XMLSchemaCache;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Stand alone implementation of {@link IJAXBDocumentType}. It is not
 * thread-safe because of the lazily loaded Schema.
 *
 * @author Philip Helger
 */
@NotThreadSafe
@Deprecated (forRemoval = true, since = "11.0.3")
public class JAXBDocumentType implements IJAXBDocumentType
{
  public static final String JAXB_DEFAULT = "##default";

  private final Class <?> m_aClass;
  private final ICommonsList <ClassPathResource> m_aXSDs = new CommonsArrayList <> ();
  private Supplier <XMLSchemaCache> m_aXMLSchemaCacheProvider = XMLSchemaCache::getInstance;

  // Status vars
  private final String m_sNamespaceURI;
  private final String m_sLocalName;
  private Schema m_aCachedSchema;

  /**
   * Constructor
   *
   * @param aClass
   *        The JAXB generated class of the root element. May not be
   *        <code>null</code>. This class must have the <code>@XmlType</code>
   *        annotation and the package the class resides in must have the
   *        <code>@XmlSchema</code> annotation with a non-null
   *        <code>namespace</code> property!
   * @param aXSDs
   *        The classpath relative paths to the XML Schema. May not be
   *        <code>null</code> but maybe empty. If the main XSD imports another
   *        XSD, the imported XSD must come first in the list. So the XSDs
   *        without any dependencies must come first!
   * @param aTypeToElementNameMapper
   *        An optional function to determine element name from type name. E.g.
   *        in UBL the type has an additional "Type" at the end that may not
   *        occur here. SBDH in contrary does not have such a suffix. May be
   *        <code>null</code> indicating that no name mapping is necessary.
   */
  public JAXBDocumentType (@Nonnull final Class <?> aClass,
                           @Nullable final List <? extends ClassPathResource> aXSDs,
                           @Nullable final Function <? super String, ? extends String> aTypeToElementNameMapper)
  {
    ValueEnforcer.notNull (aClass, "Class");
    if (aXSDs != null)
    {
      ValueEnforcer.notEmptyNoNullValue (aXSDs, "XSDs");
      for (final ClassPathResource aRes : aXSDs)
        ValueEnforcer.isTrue (aRes.hasClassLoader (),
                              () -> "ClassPathResource " + aRes + " MUST define its ClassLoader!");
    }

    // Check whether it is an @XmlType class
    final XmlType aXmlType = aClass.getAnnotation (XmlType.class);
    if (aXmlType == null)
      throw new IllegalArgumentException ("The passed class '" +
                                          aClass.getName () +
                                          "' does not have an @XmlType annotation!");

    // Get the package of the passed Class
    final Package aPackage = aClass.getPackage ();

    // The package must have the annotation "XmlSchema" with the corresponding
    // namespace it supports (maybe empty but not null). If the base XSD does
    // not contain any namespace URI, the XMLSchema annotation might be missing!
    final XmlSchema aXmlSchema = aPackage.getAnnotation (XmlSchema.class);
    if (aXmlSchema != null && aXmlSchema.namespace () == null)
      throw new IllegalArgumentException ("The package '" +
                                          aPackage.getName () +
                                          "' has no namespace URI in the @XmlSchema annotation!");

    // Depending on the generation mode, the class may have the @XmlRootElement
    // annotation or not. If it is present, use the namespace URI and the local
    // name from it, else try to deduce the name from the type.
    String sNamespaceURI;
    String sLocalName;
    final XmlRootElement aRootElement = aClass.getAnnotation (XmlRootElement.class);
    if (aRootElement != null)
    {
      // Annotation is present
      sNamespaceURI = aRootElement.namespace ();
      if (JAXB_DEFAULT.equals (sNamespaceURI) && aXmlSchema != null)
        sNamespaceURI = aXmlSchema.namespace ();

      sLocalName = aRootElement.name ();
      if (JAXB_DEFAULT.equals (sLocalName))
        sLocalName = aXmlType.name ();
    }
    else
    {
      // Hack: build the element name from the type name
      if (aXmlSchema != null)
        sNamespaceURI = aXmlSchema.namespace ();
      else
        sNamespaceURI = null;
      sLocalName = aXmlType.name ();
    }
    // Call customizer (if provided)
    if (aTypeToElementNameMapper != null)
      sLocalName = aTypeToElementNameMapper.apply (sLocalName);
    if (StringHelper.hasNoText (sLocalName))
      throw new IllegalArgumentException ("Failed to determine the local name of the element to be created!");

    m_aClass = aClass;
    if (aXSDs != null)
      m_aXSDs.addAll (aXSDs);
    m_sNamespaceURI = StringHelper.getNotNull (sNamespaceURI);
    m_sLocalName = sLocalName;
  }

  /**
   * Simple constructor when you know what you are doing.
   *
   * @param aClass
   *        The JAXB generated class of the root element. May not be
   *        <code>null</code>. This class must have the <code>@XmlType</code>
   *        annotation and the package the class resides in must have the
   *        <code>@XmlSchema</code> annotation with a non-null
   *        <code>namespace</code> property!
   * @param aXSDs
   *        The classpath relative paths to the XML Schema. May not be
   *        <code>null</code> but maybe empty. If the main XSD imports another
   *        XSD, the imported XSD must come first in the list. So the XSDs
   *        without any dependencies must come first!
   * @param sNamespaceURI
   *        The namespace URI to use. May be <code>null</code>.
   * @param sLocalName
   *        The locale name of the element. May neither be <code>null</code> nor
   *        empty.
   * @since 9.4.0
   */
  public JAXBDocumentType (@Nonnull final Class <?> aClass,
                           @Nullable final List <? extends ClassPathResource> aXSDs,
                           @Nullable final String sNamespaceURI,
                           @Nonnull @Nonempty final String sLocalName)
  {
    ValueEnforcer.notNull (aClass, "Class");
    if (aXSDs != null)
    {
      ValueEnforcer.notEmptyNoNullValue (aXSDs, "XSDs");
      for (final ClassPathResource aRes : aXSDs)
        ValueEnforcer.isTrue (aRes.hasClassLoader (),
                              () -> "ClassPathResource " + aRes + " MUST define its ClassLoader!");
    }
    ValueEnforcer.notEmpty (sLocalName, "sLocalName");

    // Check whether it is an @XmlType class
    final XmlType aXmlType = aClass.getAnnotation (XmlType.class);
    if (aXmlType == null)
      throw new IllegalArgumentException ("The passed class '" +
                                          aClass.getName () +
                                          "' does not have an @XmlType annotation!");

    // Get the package of the passed Class
    final Package aPackage = aClass.getPackage ();

    // The package must have the annotation "XmlSchema" with the corresponding
    // namespace it supports (maybe empty but not null). If the base XSD does
    // not contain any namespace URI, the XMLSchema annotation might be missing!
    final XmlSchema aXmlSchema = aPackage.getAnnotation (XmlSchema.class);
    if (aXmlSchema != null && aXmlSchema.namespace () == null)
      throw new IllegalArgumentException ("The package '" +
                                          aPackage.getName () +
                                          "' has no namespace URI in the @XmlSchema annotation!");

    m_aClass = aClass;
    if (aXSDs != null)
      m_aXSDs.addAll (aXSDs);
    m_sNamespaceURI = sNamespaceURI;
    m_sLocalName = sLocalName;
  }

  @Nonnull
  public final Class <?> getImplementationClass ()
  {
    return m_aClass;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsList <ClassPathResource> getAllXSDResources ()
  {
    return m_aXSDs.getClone ();
  }

  @Nonnull
  public final Supplier <XMLSchemaCache> getXMLSchemaCacheProvider ()
  {
    return m_aXMLSchemaCacheProvider;
  }

  @Nonnull
  public final JAXBDocumentType setXMLSchemaCacheProvider (@Nonnull final Supplier <XMLSchemaCache> aXMLSchemaCacheProvider)
  {
    ValueEnforcer.notNull (aXMLSchemaCacheProvider, "SchemaCacheProvider");
    m_aXMLSchemaCacheProvider = aXMLSchemaCacheProvider;
    return this;
  }

  @Nonnull
  public final String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Nonnull
  @Nonempty
  public final String getLocalName ()
  {
    return m_sLocalName;
  }

  @Nullable
  public Schema getSchema ()
  {
    if (m_aXSDs.isEmpty ())
    {
      // No XSD -> no Schema
      return null;
    }

    if (m_aCachedSchema == null)
    {
      final XMLSchemaCache aSchemaCache = m_aXMLSchemaCacheProvider.get ();
      if (aSchemaCache == null)
        throw new IllegalStateException ("Failed to get an instance of XMLSchemaCache from " +
                                         m_aXMLSchemaCacheProvider);
      m_aCachedSchema = aSchemaCache.getSchema (m_aXSDs);
    }
    return m_aCachedSchema;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JAXBDocumentType rhs = (JAXBDocumentType) o;
    return m_aClass.equals (rhs.m_aClass) && m_aXSDs.equals (rhs.m_aXSDs);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aClass).append (m_aXSDs).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Class", m_aClass)
                                       .append ("XSDPaths", m_aXSDs)
                                       .append ("XMLSchemaCacheProvider", m_aXMLSchemaCacheProvider)
                                       .append ("NamespaceURI", m_sNamespaceURI)
                                       .append ("LocalName", m_sLocalName)
                                       .getToString ();
  }
}
