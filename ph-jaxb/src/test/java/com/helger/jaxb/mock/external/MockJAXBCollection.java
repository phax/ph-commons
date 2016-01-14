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
package com.helger.jaxb.mock.external;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CA_GEN_Collection complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="CA_GEN_Collection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Issue" type="{}CA_GEN_Issue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Description" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "CA_GEN_Collection", propOrder = { "m_aIssue" })
public final class MockJAXBCollection
{
  @XmlElement (name = "Issue")
  private List <MockJAXBIssue> m_aIssue;
  @XmlAttribute (name = "ID", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nID;
  @XmlAttribute (name = "Name", required = true)
  private String m_sName;
  @XmlAttribute (name = "Description", required = true)
  private String m_sDescription;

  /**
   * Gets the value of the issue property.
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the issue property.
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getIssue ().add (newItem);
   * </pre>
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link MockJAXBIssue }
   *
   * @return issue list
   */
  public List <MockJAXBIssue> getIssue ()
  {
    if (m_aIssue == null)
      m_aIssue = new ArrayList <MockJAXBIssue> ();
    return m_aIssue;
  }

  @Nonnull
  public MockJAXBIssue getOnlyIssue ()
  {
    if (m_aIssue == null || m_aIssue.size () != 1)
      throw new IllegalStateException ("Not exactly one issue present!");
    return m_aIssue.get (0);
  }

  /**
   * Gets the value of the id property.
   *
   * @return id
   */
  public long getID ()
  {
    return m_nID;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value
   *        new ID
   */
  public void setID (final long value)
  {
    m_nID = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setName (final String value)
  {
    m_sName = value;
  }

  /**
   * Gets the value of the description property.
   *
   * @return possible object is {@link String }
   */
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDescription (final String value)
  {
    m_sDescription = value;
  }
}
