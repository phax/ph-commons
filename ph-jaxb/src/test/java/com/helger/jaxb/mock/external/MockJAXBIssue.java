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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for CA_GEN_Issue complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="CA_GEN_Issue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="SubTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DateDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstPage">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Width" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *                 &lt;attribute name="Height" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="CollectionID" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="ContentLanguage" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MenuLanguage" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MenuLayout" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PageCount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="ArticleCount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="Directory" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DateSort" use="required" type="{}CA_DATETIME" />
 *       &lt;attribute name="DateFrom" use="required" type="{}CA_DATETIME" />
 *       &lt;attribute name="DateTo" use="required" type="{}CA_DATETIME" />
 *       &lt;attribute name="DirAbsolute" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "CA_GEN_Issue", propOrder = { "m_aTitle", "m_sSubTitle", "m_sDateDescription", "m_aFirstPage" })
public final class MockJAXBIssue
{
  @XmlElement (name = "Title", required = true)
  private BigDecimal m_aTitle;
  @XmlElement (name = "SubTitle", required = true)
  private String m_sSubTitle;
  @XmlElement (name = "DateDescription", required = true)
  private String m_sDateDescription;
  @XmlElement (name = "FirstPage", required = true)
  private MockJAXBIssue.FirstPage m_aFirstPage;
  @XmlAttribute (name = "ID", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nID;
  @XmlAttribute (name = "CollectionID", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nCollectionID;
  @XmlAttribute (name = "ContentLanguage", required = true)
  private String m_sContentLanguage;
  @XmlAttribute (name = "MenuLanguage", required = true)
  private String m_sMenuLanguage;
  @XmlAttribute (name = "MenuLayout", required = true)
  private String m_sMenuLayout;
  @XmlAttribute (name = "PageCount", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nPageCount;
  @XmlAttribute (name = "ArticleCount", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nArticleCount;
  @XmlAttribute (name = "Directory", required = true)
  private String m_sDirectory;
  @XmlAttribute (name = "DateSort", required = true)
  private String m_sDateSort;
  @XmlAttribute (name = "DateFrom", required = true)
  private String m_sDateFrom;
  @XmlAttribute (name = "DateTo", required = true)
  private String m_sDateTo;
  @XmlAttribute (name = "DirAbsolute", required = true)
  @XmlSchemaType (name = "unsignedInt")
  private long m_nDirAbsolute;

  /**
   * Gets the value of the title property.
   *
   * @return possible object is {@link String }
   */
  public BigDecimal getTitle ()
  {
    return m_aTitle;
  }

  /**
   * Sets the value of the title property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setTitle (final BigDecimal value)
  {
    this.m_aTitle = value;
  }

  /**
   * Gets the value of the subTitle property.
   *
   * @return possible object is {@link String }
   */
  public String getSubTitle ()
  {
    return m_sSubTitle;
  }

  /**
   * Sets the value of the subTitle property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setSubTitle (final String value)
  {
    this.m_sSubTitle = value;
  }

  /**
   * Gets the value of the dateDescription property.
   *
   * @return possible object is {@link String }
   */
  public String getDateDescription ()
  {
    return m_sDateDescription;
  }

  /**
   * Sets the value of the dateDescription property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDateDescription (final String value)
  {
    this.m_sDateDescription = value;
  }

  /**
   * Gets the value of the firstPage property.
   *
   * @return possible object is {@link MockJAXBIssue.FirstPage }
   */
  public MockJAXBIssue.FirstPage getFirstPage ()
  {
    return m_aFirstPage;
  }

  /**
   * Sets the value of the firstPage property.
   *
   * @param value
   *        allowed object is {@link MockJAXBIssue.FirstPage }
   */
  public void setFirstPage (final MockJAXBIssue.FirstPage value)
  {
    this.m_aFirstPage = value;
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
   *        new id
   */
  public void setID (final long value)
  {
    this.m_nID = value;
  }

  /**
   * Gets the value of the collectionID property.
   *
   * @return collection ID
   */
  public long getCollectionID ()
  {
    return m_nCollectionID;
  }

  /**
   * Sets the value of the collectionID property.
   *
   * @param value
   *        new value
   */
  public void setCollectionID (final long value)
  {
    this.m_nCollectionID = value;
  }

  /**
   * Gets the value of the contentLanguage property.
   *
   * @return possible object is {@link String }
   */
  public String getContentLanguage ()
  {
    return m_sContentLanguage;
  }

  /**
   * Sets the value of the contentLanguage property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setContentLanguage (final String value)
  {
    this.m_sContentLanguage = value;
  }

  /**
   * Gets the value of the menuLanguage property.
   *
   * @return possible object is {@link String }
   */
  public String getMenuLanguage ()
  {
    return m_sMenuLanguage;
  }

  /**
   * Sets the value of the menuLanguage property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setMenuLanguage (final String value)
  {
    this.m_sMenuLanguage = value;
  }

  /**
   * Gets the value of the menuLayout property.
   *
   * @return possible object is {@link String }
   */
  public String getMenuLayout ()
  {
    return m_sMenuLayout;
  }

  /**
   * Sets the value of the menuLayout property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setMenuLayout (final String value)
  {
    this.m_sMenuLayout = value;
  }

  /**
   * Gets the value of the pageCount property.
   *
   * @return page count
   */
  public long getPageCount ()
  {
    return m_nPageCount;
  }

  /**
   * Sets the value of the pageCount property.
   *
   * @param value
   *        new value
   */
  public void setPageCount (final long value)
  {
    this.m_nPageCount = value;
  }

  /**
   * Gets the value of the articleCount property.
   *
   * @return article count
   */
  public long getArticleCount ()
  {
    return m_nArticleCount;
  }

  /**
   * Sets the value of the articleCount property.
   *
   * @param value
   *        new value
   */
  public void setArticleCount (final long value)
  {
    this.m_nArticleCount = value;
  }

  /**
   * Gets the value of the directory property.
   *
   * @return possible object is {@link String }
   */
  public String getDirectory ()
  {
    return m_sDirectory;
  }

  /**
   * Sets the value of the directory property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDirectory (final String value)
  {
    this.m_sDirectory = value;
  }

  /**
   * Gets the value of the dateSort property.
   *
   * @return possible object is {@link String }
   */
  public String getDateSort ()
  {
    return m_sDateSort;
  }

  /**
   * Sets the value of the dateSort property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDateSort (final String value)
  {
    this.m_sDateSort = value;
  }

  /**
   * Gets the value of the dateFrom property.
   *
   * @return possible object is {@link String }
   */
  public String getDateFrom ()
  {
    return m_sDateFrom;
  }

  /**
   * Sets the value of the dateFrom property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDateFrom (final String value)
  {
    this.m_sDateFrom = value;
  }

  /**
   * Gets the value of the dateTo property.
   *
   * @return possible object is {@link String }
   */
  public String getDateTo ()
  {
    return m_sDateTo;
  }

  /**
   * Sets the value of the dateTo property.
   *
   * @param value
   *        allowed object is {@link String }
   */
  public void setDateTo (final String value)
  {
    this.m_sDateTo = value;
  }

  /**
   * Gets the value of the dirAbsolute property.
   *
   * @return absolute dir?
   */
  public long getDirAbsolute ()
  {
    return m_nDirAbsolute;
  }

  /**
   * Sets the value of the dirAbsolute property.
   *
   * @param value
   *        new value
   */
  public void setDirAbsolute (final long value)
  {
    this.m_nDirAbsolute = value;
  }

  /**
   * <p>
   * Java class for anonymous complex type.
   * <p>
   * The following schema fragment specifies the expected content contained
   * within this class.
   *
   * <pre>
   * &lt;complexType>
   *   &lt;simpleContent>
   *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
   *       &lt;attribute name="Width" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
   *       &lt;attribute name="Height" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
   *     &lt;/extension>
   *   &lt;/simpleContent>
   * &lt;/complexType>
   * </pre>
   */
  @XmlAccessorType (XmlAccessType.FIELD)
  @XmlType (name = "", propOrder = { "m_sValue" })
  public static class FirstPage
  {
    @XmlValue
    private String m_sValue;
    @XmlAttribute (name = "Width", required = true)
    @XmlSchemaType (name = "unsignedInt")
    private long m_nWidth;
    @XmlAttribute (name = "Height", required = true)
    @XmlSchemaType (name = "unsignedInt")
    private long m_nHeight;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is {@link String }
     */
    public String getValue ()
    {
      return m_sValue;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *        allowed object is {@link String }
     */
    public void setValue (final String value)
    {
      this.m_sValue = value;
    }

    /**
     * Gets the value of the width property.
     *
     * @return width
     */
    public long getWidth ()
    {
      return m_nWidth;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *        new width
     */
    public void setWidth (final long value)
    {
      this.m_nWidth = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return height
     */
    public long getHeight ()
    {
      return m_nHeight;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *        new height
     */
    public void setHeight (final long value)
    {
      this.m_nHeight = value;
    }
  }
}
