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
package com.helger.security.crl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jspecify.annotations.NonNull;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Parser for the small DER subset used by the X.509 CRL Distribution Points extension.
 *
 * @author GT
 */
final class CRLDistributionPointParser
{
  private static final int TAG_OCTET_STRING = 0x04;
  private static final int TAG_SEQUENCE = 0x30;
  private static final int TAG_DISTRIBUTION_POINT_NAME = 0xa0;
  private static final int TAG_FULL_NAME = 0xa0;
  private static final int TAG_URI = 0x86;

  private CRLDistributionPointParser ()
  {}

  @NonNull
  public static ICommonsList <String> parse (final byte @NonNull [] aExtensionValue)
  {
    try
    {
      final DERReader aOuterReader = new DERReader (aExtensionValue);
      final DERValue aExtensionOctets = aOuterReader.readExpected (TAG_OCTET_STRING);
      aOuterReader.requireEnd ();

      final DERReader aExtensionReader = aExtensionOctets.createReader ();
      final DERValue aDistributionPointsSequence = aExtensionReader.readExpected (TAG_SEQUENCE);
      aExtensionReader.requireEnd ();

      final ICommonsList <String> ret = new CommonsArrayList <> ();
      final DERReader aDistributionPointsReader = aDistributionPointsSequence.createReader ();
      while (aDistributionPointsReader.hasRemaining ())
      {
        final DERValue aDistributionPoint = aDistributionPointsReader.readExpected (TAG_SEQUENCE);
        _readDistributionPoint (aDistributionPoint, ret);
      }
      return ret;
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException ("Failed to decode the X.509 CRL Distribution Points extension", ex);
    }
  }

  private static void _readDistributionPoint (@NonNull final DERValue aDistributionPoint,
                                               @NonNull final ICommonsList <String> aTarget) throws IOException
  {
    final DERReader aFieldsReader = aDistributionPoint.createReader ();
    while (aFieldsReader.hasRemaining ())
    {
      final DERValue aField = aFieldsReader.read ();
      if (aField.getTag () == TAG_DISTRIBUTION_POINT_NAME)
        _readDistributionPointName (aField, aTarget);
    }
  }

  private static void _readDistributionPointName (@NonNull final DERValue aDistributionPointName,
                                                   @NonNull final ICommonsList <String> aTarget) throws IOException
  {
    final DERReader aNameReader = aDistributionPointName.createReader ();
    final DERValue aName = aNameReader.read ();
    aNameReader.requireEnd ();

    if (aName.getTag () == TAG_FULL_NAME)
    {
      final DERReader aGeneralNamesReader = aName.createReader ();
      while (aGeneralNamesReader.hasRemaining ())
      {
        final DERValue aGeneralName = aGeneralNamesReader.read ();
        if (aGeneralName.getTag () == TAG_URI)
          aTarget.add (_readIA5String (aGeneralName.getValue ()).trim ());
      }
    }
  }

  @NonNull
  private static String _readIA5String (final byte @NonNull [] aValue) throws IOException
  {
    for (final byte b : aValue)
      if ((b & 0x80) != 0)
        throw new IOException ("IA5String contains a non-ASCII byte");
    return new String (aValue, StandardCharsets.US_ASCII);
  }

  private static final class DERReader
  {
    private final byte [] m_aData;
    private int m_nPosition;

    DERReader (final byte @NonNull [] aData)
    {
      m_aData = aData;
    }

    public boolean hasRemaining ()
    {
      return m_nPosition < m_aData.length;
    }

    public void requireEnd () throws IOException
    {
      if (hasRemaining ())
        throw new IOException ("Unexpected trailing DER data");
    }

    @NonNull
    public DERValue readExpected (final int nExpectedTag) throws IOException
    {
      final DERValue ret = read ();
      if (ret.getTag () != nExpectedTag)
        throw new IOException ("Expected DER tag " + nExpectedTag + " but found " + ret.getTag ());
      return ret;
    }

    @NonNull
    public DERValue read () throws IOException
    {
      final int nTag = _readUnsignedByte ();
      if ((nTag & 0x1f) == 0x1f)
        throw new IOException ("High-tag-number DER values are not supported");

      final int nLength = _readLength ();
      if (nLength > m_aData.length - m_nPosition)
        throw new IOException ("DER value length exceeds the available data");

      final byte [] aValue = Arrays.copyOfRange (m_aData, m_nPosition, m_nPosition + nLength);
      m_nPosition += nLength;
      return new DERValue (nTag, aValue);
    }

    private int _readLength () throws IOException
    {
      final int nFirst = _readUnsignedByte ();
      if ((nFirst & 0x80) == 0)
        return nFirst;

      final int nLengthBytes = nFirst & 0x7f;
      if (nLengthBytes == 0)
        throw new IOException ("Indefinite-length encoding is not valid DER");
      if (nLengthBytes > 4 || nLengthBytes > m_aData.length - m_nPosition)
        throw new IOException ("Invalid DER length field");
      if ((m_aData[m_nPosition] & 0xff) == 0)
        throw new IOException ("DER length has a redundant leading zero");

      long nLength = 0;
      for (int i = 0; i < nLengthBytes; ++i)
        nLength = (nLength << 8) | _readUnsignedByte ();
      if (nLength < 128 || nLength > Integer.MAX_VALUE)
        throw new IOException ("Invalid DER length value");
      return (int) nLength;
    }

    private int _readUnsignedByte () throws IOException
    {
      if (!hasRemaining ())
        throw new IOException ("Unexpected end of DER data");
      return m_aData[m_nPosition++] & 0xff;
    }
  }

  private static final class DERValue
  {
    private final int m_nTag;
    private final byte [] m_aValue;

    DERValue (final int nTag, final byte @NonNull [] aValue)
    {
      m_nTag = nTag;
      m_aValue = aValue;
    }

    public int getTag ()
    {
      return m_nTag;
    }

    public byte @NonNull [] getValue ()
    {
      return m_aValue;
    }

    @NonNull
    public DERReader createReader ()
    {
      return new DERReader (m_aValue);
    }
  }
}
