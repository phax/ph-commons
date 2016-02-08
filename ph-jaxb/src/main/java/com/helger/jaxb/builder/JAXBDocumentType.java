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
package com.helger.jaxb.builder;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.validation.Schema;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.xml.schema.XMLSchemaCache;

/**
 * Stand alone implementation of {@link IJAXBDocumentType}
 *
 * @author Philip Helger
 */
public class JAXBDocumentType implements IJAXBDocumentType
{
  private final Class <?> m_aClass;
  private final String m_sLocalName;
  private final String m_sNamespaceURI;
  private final List <String> m_aXSDPaths;
  private Schema m_aSchema;

  /**
   * Constructor
   *
   * @param aClass
   *        The JAXB generated class of the root element. May not be
   *        <code>null</code>. This class must have the <code>@XmlType</code>
   *        annotation and the package the class resides in must have the
   *        <code>@XmlSchema</code> annotation with a non-null
   *        <code>namespace</code> property!
   * @param aXSDPaths
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
                           @Nullable final List <String> aXSDPaths,
                           @Nullable final Function <String, String> aTypeToElementNameMapper)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.noNullValue (aXSDPaths, "XSDPaths");

    // Check whether it is an @XmlType class
    final XmlType aXmlType = aClass.getAnnotation (XmlType.class);
    if (aXmlType == null)
      throw new IllegalArgumentException ("The passed class does not have an @XmlType annotation!");

    // Get the package of the passed Class
    final Package aPackage = aClass.getPackage ();

    // The package must have the annotation "XmlSchema" with the corresponding
    // namespace it supports (maybe empty but not null)
    final XmlSchema aXmlSchema = aPackage.getAnnotation (XmlSchema.class);
    if (aXmlSchema == null)
      throw new IllegalArgumentException ("The package '" + aPackage.getName () + "' has no @XmlSchema annotation!");
    if (aXmlSchema.namespace () == null)
      throw new IllegalArgumentException ("The package '" +
                                          aPackage.getName () +
                                          "' has no namespace URI in the @XmlSchema annotation!");

    // Depending on the generation mode, the class may have the @XmlRootElement
    // annotation or not. If it is present, use the name from it, else try to
    // deduce the name from the type.
    String sLocalName;
    final XmlRootElement aRootElement = aClass.getAnnotation (XmlRootElement.class);
    if (aRootElement != null)
    {
      sLocalName = aRootElement.name ();
    }
    else
    {
      // Hack: build the element name from the type name
      sLocalName = aXmlType.name ();
      if (StringHelper.hasNoText (sLocalName))
        throw new IllegalArgumentException ("Failed to determine the local name of the element to be created!");
    }

    if (aTypeToElementNameMapper != null)
      sLocalName = aTypeToElementNameMapper.apply (sLocalName);

    m_aClass = aClass;
    m_sLocalName = sLocalName;
    m_sNamespaceURI = StringHelper.getNotNull (aXmlSchema.namespace ());
    m_aXSDPaths = CollectionHelper.newList (aXSDPaths);
  }

  @Nonnull
  public Class <?> getImplementationClass ()
  {
    return m_aClass;
  }

  @Nonnull
  public String getLocalName ()
  {
    return m_sLocalName;
  }

  @Nonnull
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAllXSDPaths ()
  {
    return CollectionHelper.newList (m_aXSDPaths);
  }

  @Nonnull
  private Schema _createSchema (@Nullable final ClassLoader aClassLoader)
  {
    final List <? extends IReadableResource> aXSDRes = getAllXSDResources (aClassLoader);
    final Schema ret = XMLSchemaCache.getInstanceOfClassLoader (aClassLoader).getSchema (aXSDRes);
    if (ret == null)
      throw new IllegalStateException ("Failed to create Schema from " +
                                       aXSDRes +
                                       " using class loader " +
                                       aClassLoader);
    return ret;
  }

  @Nullable
  public Schema getSchema (@Nullable final ClassLoader aClassLoader)
  {
    if (m_aXSDPaths.isEmpty ())
    {
      // No XSD -> no Schema
      return null;
    }

    if (aClassLoader != null)
    {
      // Don't cache if a class loader is provided
      return _createSchema (aClassLoader);
    }

    if (m_aSchema == null)
    {
      // Lazy initialization if no class loader is present
      m_aSchema = _createSchema (aClassLoader);
    }
    return m_aSchema;
  }
}
