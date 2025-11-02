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
package com.helger.tree.xml;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;

/**
 * A special implementation of {@link IConverterTreeXML} that uses the
 * conversion rules stored in the
 * {@link com.helger.xml.microdom.convert.MicroTypeConverterRegistry}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be converted
 */
@NotThreadSafe
public final class MicroTypeConverterTreeXML <DATATYPE> implements IConverterTreeXML <DATATYPE>
{
  private String m_sNamespaceURI;
  private final String m_sElementName;
  private final Class <? extends DATATYPE> m_aNativeClass;

  /**
   * Constructor
   *
   * @param sElementName
   *        The element name to use. May neither be <code>null</code> nor empty
   * @param aNativeClass
   *        The data type class - required for reading. May be in an interface
   *        as well.
   */
  public MicroTypeConverterTreeXML (@NonNull @Nonempty final String sElementName, @NonNull final Class <? extends DATATYPE> aNativeClass)
  {
    this (null, sElementName, aNativeClass);
  }

  /**
   * Constructor
   *
   * @param sNamespaceURI
   *        The namespace URI for the created element. May be <code>null</code>.
   * @param sElementName
   *        The element name to use. May neither be <code>null</code> nor empty
   * @param aNativeClass
   *        The data type class - required for reading. May be in an interface
   *        as well.
   */
  public MicroTypeConverterTreeXML (@Nullable final String sNamespaceURI,
                                    @NonNull @Nonempty final String sElementName,
                                    @NonNull final Class <? extends DATATYPE> aNativeClass)
  {
    m_sNamespaceURI = sNamespaceURI;
    m_sElementName = ValueEnforcer.notEmpty (sElementName, "ElementName");
    m_aNativeClass = ValueEnforcer.notNull (aNativeClass, "NativeClass");
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @NonNull
  @Nonempty
  public String getElementName ()
  {
    return m_sElementName;
  }

  @NonNull
  public Class <? extends DATATYPE> getNativeClass ()
  {
    return m_aNativeClass;
  }

  public void appendDataValue (@NonNull final IMicroElement eDataElement, @Nullable final DATATYPE aObject)
  {
    // Append created element - or null if the passed object is null
    final IMicroElement eElement = MicroTypeConverter.convertToMicroElement (aObject, m_sNamespaceURI, m_sElementName);
    eDataElement.addChild (eElement);
  }

  @Nullable
  public DATATYPE getAsDataValue (@NonNull final IMicroElement eDataElement)
  {
    final IMicroElement eChildElement = eDataElement.getFirstChildElement ();
    if (eChildElement != null)
    {
      final Object aObj1 = m_sNamespaceURI;
      if (!EqualsHelper.equals (aObj1, eChildElement.getNamespaceURI ()))
        throw new IllegalStateException ("Namespace mismatch! Expected: " + m_sNamespaceURI);
      if (!m_sElementName.equals (eChildElement.getTagName ()))
        throw new IllegalStateException ("Tag name mismatch! Expected: " + m_sElementName);
    }
    return MicroTypeConverter.convertToNative (eChildElement, m_aNativeClass);
  }

  /**
   * Factory method.
   *
   * @param <DATATYPE>
   *        The data type to be converted
   * @param sElementName
   *        The element name to use. May neither be <code>null</code> nor empty
   * @param aNativeClass
   *        The data type class - required for reading. May be in an interface
   *        as well.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> MicroTypeConverterTreeXML <DATATYPE> create (@NonNull @Nonempty final String sElementName,
                                                                        @NonNull final Class <? extends DATATYPE> aNativeClass)
  {
    return new MicroTypeConverterTreeXML <> (sElementName, aNativeClass);
  }

  /**
   * Factory method
   *
   * @param <DATATYPE>
   *        The data type to be converted
   * @param sNamespaceURI
   *        The namespace URI for the created element. May be <code>null</code>.
   * @param sElementName
   *        The element name to use. May neither be <code>null</code> nor empty
   * @param aNativeClass
   *        The data type class - required for reading. May be in an interface
   *        as well.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> MicroTypeConverterTreeXML <DATATYPE> create (@Nullable final String sNamespaceURI,
                                                                        @NonNull @Nonempty final String sElementName,
                                                                        @NonNull final Class <? extends DATATYPE> aNativeClass)
  {
    return new MicroTypeConverterTreeXML <> (sNamespaceURI, sElementName, aNativeClass);
  }
}
