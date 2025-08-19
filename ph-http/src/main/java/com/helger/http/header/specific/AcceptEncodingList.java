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
package com.helger.http.header.specific;

import java.util.Locale;
import java.util.function.Function;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.http.header.AbstractQValueList;
import com.helger.http.header.QValue;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contains a list of Accept-Encoding values as specified by the HTTP header
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class AcceptEncodingList extends AbstractQValueList <String>
{
  public AcceptEncodingList ()
  {}

  @Nonnull
  private static String _unify (@Nonnull final String sEncoding)
  {
    return sEncoding.toLowerCase (Locale.US);
  }

  @Nonnull
  public AcceptEncodingList addEncoding (@Nonnull final String sEncoding, @Nonnegative final double dQuality)
  {
    ValueEnforcer.notEmpty (sEncoding, "Encoding");
    qvalueMap ().put (_unify (sEncoding), new QValue (dQuality));
    return this;
  }

  /**
   * Return the associated quality of the given encoding.
   *
   * @param sEncoding
   *        The encoding name to query. May not be <code>null</code>.
   * @return The matching {@link QValue} and never <code>null</code>.
   */
  @Nonnull
  public QValue getQValueOfEncoding (@Nonnull final String sEncoding)
  {
    ValueEnforcer.notNull (sEncoding, "Encoding");

    // Direct search encoding
    QValue aQuality = qvalueMap ().get (_unify (sEncoding));
    if (aQuality == null)
    {
      // If not explicitly given, check for "*"
      aQuality = qvalueMap ().get (AcceptEncodingHandler.ANY_ENCODING);
      if (aQuality == null)
      {
        // Neither encoding nor "*" is present
        // -> assume minimum quality
        return QValue.MIN_QVALUE;
      }
    }
    return aQuality;
  }

  /**
   * Return the associated quality of the given encoding.
   *
   * @param sEncoding
   *        The encoding name to query. May not be <code>null</code>.
   * @return 0 means not accepted, 1 means fully accepted.
   */
  public double getQualityOfEncoding (@Nonnull final String sEncoding)
  {
    return getQValueOfEncoding (sEncoding).getQuality ();
  }

  /**
   * Check if the passed encoding is supported. Supported means the quality is &gt; 0.
   *
   * @param sEncoding
   *        The encoding to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the encoding is supported, <code>false</code> if not.
   */
  public boolean supportsEncoding (@Nonnull final String sEncoding)
  {
    return getQValueOfEncoding (sEncoding).isAboveMinimumQuality ();
  }

  public boolean explicitlySupportsEncoding (@Nonnull final String sEncoding)
  {
    ValueEnforcer.notNull (sEncoding, "Encoding");

    final QValue aQuality = qvalueMap ().get (_unify (sEncoding));
    return aQuality != null && aQuality.isAboveMinimumQuality ();
  }

  public boolean supportsGZIP ()
  {
    return supportsEncoding (AcceptEncodingHandler.GZIP_ENCODING) ||
           supportsEncoding (AcceptEncodingHandler.X_GZIP_ENCODING);
  }

  /**
   * @return the accepted GZip encoding. May either be {@link AcceptEncodingHandler#GZIP_ENCODING}
   *         or {@link AcceptEncodingHandler#X_GZIP_ENCODING} or <code>null</code>
   */
  @Nullable
  public String getUsedGZIPEncoding ()
  {
    if (supportsEncoding (AcceptEncodingHandler.GZIP_ENCODING))
      return AcceptEncodingHandler.GZIP_ENCODING;
    if (supportsEncoding (AcceptEncodingHandler.X_GZIP_ENCODING))
      return AcceptEncodingHandler.X_GZIP_ENCODING;
    return null;
  }

  public boolean supportsDeflate ()
  {
    return supportsEncoding (AcceptEncodingHandler.DEFLATE_ENCODING);
  }

  /**
   * @return the accepted GZip encoding. May either be
   *         {@link AcceptEncodingHandler#DEFLATE_ENCODING} or <code>null</code>
   */
  @Nullable
  public String getUsedDeflateEncoding ()
  {
    if (supportsEncoding (AcceptEncodingHandler.DEFLATE_ENCODING))
      return AcceptEncodingHandler.DEFLATE_ENCODING;
    return null;
  }

  public boolean supportsCompress ()
  {
    return supportsEncoding (AcceptEncodingHandler.COMPRESS_ENCODING) ||
           supportsEncoding (AcceptEncodingHandler.X_COMPRESS_ENCODING);
  }

  /**
   * @return the accepted GZip encoding. May either be
   *         {@link AcceptEncodingHandler#COMPRESS_ENCODING} or
   *         {@link AcceptEncodingHandler#X_COMPRESS_ENCODING} or <code>null</code>
   */
  @Nullable
  public String getUsedCompressEncoding ()
  {
    if (supportsEncoding (AcceptEncodingHandler.COMPRESS_ENCODING))
      return AcceptEncodingHandler.COMPRESS_ENCODING;
    if (supportsEncoding (AcceptEncodingHandler.X_COMPRESS_ENCODING))
      return AcceptEncodingHandler.X_COMPRESS_ENCODING;
    return null;
  }

  @Override
  @Nonnull
  public String getAsHttpHeaderValue ()
  {
    return getAsHttpHeaderValue (Function.identity ());
  }
}
