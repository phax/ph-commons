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
package com.helger.commons.xml.ls;

import java.io.InputStream;
import java.io.Reader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.ls.LSInput;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;

/**
 * Simple {@link LSInput} implementation.
 *
 * @author Philip Helger
 */
public class ResourceLSInput implements LSInput
{
  private final IHasInputStream m_aISP;
  private String m_sEncoding;
  private String m_sPublicId;
  private String m_sSystemId;
  private boolean m_bCertifiedText;
  private String m_sBaseURI;
  private String m_sStringData;

  public ResourceLSInput (@Nonnull final IReadableResource aResource)
  {
    this (aResource, aResource.getResourceID ());
  }

  public ResourceLSInput (@Nonnull final IHasInputStream aISP, @Nullable final String sSystemID)
  {
    m_aISP = ValueEnforcer.notNull (aISP, "InputStreamProvider");
    m_sSystemId = sSystemID;
  }

  @Nullable
  public String getBaseURI ()
  {
    return m_sBaseURI;
  }

  public void setBaseURI (@Nullable final String sBaseURI)
  {
    m_sBaseURI = sBaseURI;
  }

  @Nonnull
  public IHasInputStream getInputStreamProvider ()
  {
    return m_aISP;
  }

  @Nonnull
  public InputStream getByteStream ()
  {
    return m_aISP.getInputStream ();
  }

  @UnsupportedOperation
  public void setByteStream (final InputStream aByteStream)
  {
    throw new UnsupportedOperationException ();
  }

  public boolean getCertifiedText ()
  {
    return m_bCertifiedText;
  }

  public void setCertifiedText (final boolean bCertifiedText)
  {
    m_bCertifiedText = bCertifiedText;
  }

  @Nullable
  public Reader getCharacterStream ()
  {
    return null;
  }

  @UnsupportedOperation
  public void setCharacterStream (final Reader aCharacterStream)
  {
    throw new UnsupportedOperationException ();
  }

  @Nullable
  public String getEncoding ()
  {
    return m_sEncoding;
  }

  public void setEncoding (@Nullable final String sEncoding)
  {
    m_sEncoding = sEncoding;
  }

  @Nullable
  public String getPublicId ()
  {
    return m_sPublicId;
  }

  public void setPublicId (@Nullable final String sPublicId)
  {
    m_sPublicId = sPublicId;
  }

  @Nullable
  public String getStringData ()
  {
    return m_sStringData;
  }

  public void setStringData (@Nullable final String sStringData)
  {
    m_sStringData = sStringData;
  }

  @Nullable
  public String getSystemId ()
  {
    return m_sSystemId;
  }

  public void setSystemId (@Nullable final String sSystemId)
  {
    m_sSystemId = sSystemId;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ISP", m_aISP)
                                       .appendIfNotNull ("encoding", m_sEncoding)
                                       .appendIfNotNull ("publicId", m_sPublicId)
                                       .appendIfNotNull ("systemId", m_sSystemId)
                                       .append ("certifiedText", m_bCertifiedText)
                                       .appendIfNotNull ("baseURI", m_sBaseURI)
                                       .appendIfNotNull ("stringData", m_sStringData)
                                       .toString ();
  }
}
