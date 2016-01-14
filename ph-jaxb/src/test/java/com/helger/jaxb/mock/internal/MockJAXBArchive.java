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
package com.helger.jaxb.mock.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Collection" type="{}CA_GEN_Collection" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "", propOrder = { "m_aCollection" })
@XmlRootElement (name = "Root")
public final class MockJAXBArchive
{
  @XmlElement (name = "Collection", required = true)
  protected List <MockJAXBCollection> m_aCollection;
  @XmlAttribute (name = "Version", required = true)
  protected String m_sVersion;

  /**
   * Gets the value of the collection property.
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the collection property.
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getCollection ().add (newItem);
   * </pre>
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link MockJAXBCollection }
   *
   * @return Collection
   */
  public List <MockJAXBCollection> getCollection ()
  {
    if (m_aCollection == null)
      m_aCollection = new ArrayList <MockJAXBCollection> ();
    return m_aCollection;
  }

  @Nonnull
  public MockJAXBCollection getOnlyCollection ()
  {
    if (m_aCollection == null || m_aCollection.size () != 1)
      throw new IllegalStateException ("Not exactly one collection present!");
    return m_aCollection.get (0);
  }

  /**
   * Gets the value of the version property.
   *
   * @return possible object is {@link String }
   */
  public String getVersion ()
  {
    return m_sVersion;
  }

  /**
   * Sets the value of the version property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setVersion (final String value)
  {
    m_sVersion = value;
  }
}
