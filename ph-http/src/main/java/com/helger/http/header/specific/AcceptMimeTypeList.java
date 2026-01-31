/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.http.header.AbstractQValueList;
import com.helger.http.header.QValue;
import com.helger.mime.IMimeType;
import com.helger.mime.parse.MimeTypeParser;
import com.helger.mime.parse.MimeTypeParserException;

/**
 * Represents a list of "Accept" HTTP header values, created e.g. by the
 * {@link AcceptMimeTypeHandler}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class AcceptMimeTypeList extends AbstractQValueList <IMimeType>
{
  public AcceptMimeTypeList ()
  {}

  @NonNull
  public AcceptMimeTypeList addMimeType (@NonNull @Nonempty final String sMimeType, @Nonnegative final double dQuality)
                                                                                                                        throws MimeTypeParserException
  {
    ValueEnforcer.notEmpty (sMimeType, "MimeType");
    return addMimeType (MimeTypeParser.parseMimeType (sMimeType), dQuality);
  }

  @NonNull
  public AcceptMimeTypeList addMimeType (@NonNull final IMimeType aMimeType, @Nonnegative final double dQuality)
  {
    ValueEnforcer.notNull (aMimeType, "MimeType");
    ValueEnforcer.isFalse (aMimeType.hasAnyParameters (), "MimeTypes used here may not contain any parameter!");

    qvalueMap ().put (aMimeType, new QValue (dQuality));
    return this;
  }

  /**
   * Return the associated quality of the given MIME type using the fallback mechanism.
   *
   * @param aMimeType
   *        The charset name to query. May not be <code>null</code>.
   * @return The {@link QValue} of the mime type
   */
  @NonNull
  public QValue getQValueOfMimeType (@NonNull final IMimeType aMimeType)
  {
    ValueEnforcer.notNull (aMimeType, "MimeType");

    // Extract only the real MIME type, without any parameters!
    final IMimeType aRealMimeType = aMimeType.getCopyWithoutParameters ();

    QValue aQuality = qvalueMap ().get (aRealMimeType);
    if (aQuality == null)
    {
      // Check for "contenttype/*"
      aQuality = qvalueMap ().get (aRealMimeType.getContentType ().buildMimeType ("*"));
      if (aQuality == null)
      {
        // If not explicitly given, check for "*"
        aQuality = qvalueMap ().get (AcceptMimeTypeHandler.ANY_MIMETYPE);
        if (aQuality == null)
        {
          // Neither charset nor "*" nor "*/*" is present
          return QValue.MIN_QVALUE;
        }
      }
    }
    return aQuality;
  }

  /**
   * Return the associated quality of the given MIME type using the fallback mechanism.
   *
   * @param sMimeType
   *        The MIME type name to query. May be <code>null</code>.
   * @return 0 means not accepted, 1 means fully accepted. If the passed MIME type is invalid, the
   *         "not accepted" quality is returned.
   */
  public double getQualityOfMimeType (@NonNull final String sMimeType)
  {
    return getQualityOfMimeType (MimeTypeParser.safeParseMimeType (sMimeType));
  }

  /**
   * Return the associated quality of the given MIME type using the fallback mechanism.
   *
   * @param aMimeType
   *        The MIME type to query. May be <code>null</code>.
   * @return 0 means not accepted, 1 means fully accepted. If the passed MIME type is
   *         <code>null</code>, the "not accepted" quality is returned.
   */
  public double getQualityOfMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType == null)
      return QValue.MIN_QUALITY;
    return getQValueOfMimeType (aMimeType).getQuality ();
  }

  /**
   * Check if the passed MIME type is supported, incl. fallback handling
   *
   * @param sMimeType
   *        The MIME type to check
   * @return <code>true</code> if it is supported, <code>false</code> if not
   */
  public boolean supportsMimeType (@NonNull final String sMimeType)
  {
    return supportsMimeType (MimeTypeParser.safeParseMimeType (sMimeType));
  }

  /**
   * Check if the passed MIME type is supported, incl. fallback handling
   *
   * @param aMimeType
   *        The MIME type to check. May be <code>null</code>.
   * @return <code>true</code> if it is supported, <code>false</code> if not
   */
  public boolean supportsMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType == null)
      return false;
    return getQValueOfMimeType (aMimeType).isAboveMinimumQuality ();
  }

  /**
   * Check if the passed MIME type is supported, without considering fallback MIME types (xxx/*)
   *
   * @param sMimeType
   *        The MIME type to check
   * @return <code>true</code> if it is supported, <code>false</code> if not
   */
  public boolean explicitlySupportsMimeType (@NonNull final String sMimeType)
  {
    return explicitlySupportsMimeType (MimeTypeParser.safeParseMimeType (sMimeType));
  }

  /**
   * Check if the passed MIME type is supported, without considering fallback MIME types (xxx/*)
   *
   * @param aMimeType
   *        The MIME type to check. May be <code>null</code>.
   * @return <code>true</code> if it is supported, <code>false</code> if not
   */
  public boolean explicitlySupportsMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType == null)
      return false;
    final QValue aQuality = qvalueMap ().get (aMimeType);
    return aQuality != null && aQuality.isAboveMinimumQuality ();
  }

  /**
   * Pick the preferred mime type of the provided list of mime types.
   *
   * @param aMimeTypes
   *        The MIME type array to be used. May be <code>null</code>.
   * @return <code>null</code> if no matching MIME type was found.
   * @since 9.6.4
   * @see #getQValueOfMimeType(IMimeType)
   */
  @Nullable
  public IMimeType getPreferredMimeType (@NonNull final IMimeType... aMimeTypes)
  {
    IMimeType ret = null;
    QValue qret = null;
    if (aMimeTypes != null)
      for (final IMimeType aMimeType : aMimeTypes)
      {
        final QValue q = getQValueOfMimeType (aMimeType);

        // Only consider values that have a value above 0
        if (q.isAboveMinimumQuality ())
          if (qret == null || q.compareTo (qret) > 0)
          {
            // We found a better one
            ret = aMimeType;
            qret = q;
          }
      }
    return ret;
  }

  @Override
  @NonNull
  public String getAsHttpHeaderValue ()
  {
    return getAsHttpHeaderValue (IMimeType::getAsString);
  }
}
