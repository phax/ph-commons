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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Parser for the small DER subset used by the X.509 "CRL Distribution Points" extension. Only the
 * URIs of the <code>fullName</code> variant are extracted - that is the only variant used in
 * practice and it was the only one supported by the previous BouncyCastle based implementation as
 * well.<br>
 * The relevant grammar from RFC 5280, section 4.2.1.13 is:
 *
 * <pre>
 * CRLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
 * DistributionPoint ::= SEQUENCE {
 *   distributionPoint  [0] DistributionPointName OPTIONAL,
 *   reasons            [1] ReasonFlags OPTIONAL,
 *   cRLIssuer          [2] GeneralNames OPTIONAL }
 * DistributionPointName ::= CHOICE {
 *   fullName                 [0] GeneralNames,
 *   nameRelativeToCRLIssuer  [1] RelativeDistinguishedName }
 * GeneralName ::= CHOICE {
 *   ...
 *   uniformResourceIdentifier [6] IA5String,
 *   ... }
 * </pre>
 *
 * This parser is deliberately strict - RFC 5280 requires certificate extensions to be DER encoded,
 * so BER specialties like the indefinite length form are rejected instead of guessed.
 *
 * @author Philip Helger
 * @since 12.3.6
 */
@Immutable
final class CRLDistributionPointParser
{
  /**
   * A minimalistic reader for a sequence of DER tag-length-value triplets.
   *
   * @author Philip Helger
   */
  private static final class DERReader
  {
    private final byte [] m_aData;
    private int m_nPosition;

    DERReader (final byte @NonNull [] aData)
    {
      m_aData = aData;
      m_nPosition = 0;
    }

    private int _readUnsignedByte () throws IOException
    {
      if (!hasRemaining ())
        throw new IOException ("Unexpected end of DER data");
      final int ret = m_aData[m_nPosition] & 0xff;
      m_nPosition++;
      return ret;
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

    @NonNull
    public DERValue readExpected (final int nExpectedTag) throws IOException
    {
      final DERValue ret = read ();
      if (ret.getTag () != nExpectedTag)
        throw new IOException ("Expected DER tag " + nExpectedTag + " but found " + ret.getTag ());
      return ret;
    }
  }

  /**
   * A single DER tag-length-value triplet.
   *
   * @author Philip Helger
   */
  @Immutable
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

  private static final int TAG_OCTET_STRING = 0x04;
  private static final int TAG_SEQUENCE = 0x30;
  // Both "DistributionPoint.distributionPoint" and "DistributionPointName.fullName" are the
  // constructed context specific tag [0]
  private static final int TAG_CONTEXT_0 = 0xa0;
  // "GeneralName.uniformResourceIdentifier" is the primitive context specific tag [6]
  private static final int TAG_URI = 0x86;

  @PresentForCodeCoverage
  private static final CRLDistributionPointParser INSTANCE = new CRLDistributionPointParser ();

  private CRLDistributionPointParser ()
  {}

  @NonNull
  private static String _readIA5String (final byte @NonNull [] aValue) throws IOException
  {
    for (final byte b : aValue)
      if ((b & 0x80) != 0)
        throw new IOException ("IA5String contains a non-ASCII byte");
    return new String (aValue, StandardCharsets.US_ASCII);
  }

  private static void _readDistributionPointName (@NonNull final DERValue aDistributionPointName,
                                                  @NonNull final ICommonsList <String> aTarget) throws IOException
  {
    final DERReader aNameReader = aDistributionPointName.createReader ();
    final DERValue aName = aNameReader.read ();
    aNameReader.requireEnd ();

    // Silently ignore "nameRelativeToCRLIssuer"
    if (aName.getTag () == TAG_CONTEXT_0)
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

  private static void _readDistributionPoint (@NonNull final DERValue aDistributionPoint,
                                              @NonNull final ICommonsList <String> aTarget) throws IOException
  {
    final DERReader aFieldsReader = aDistributionPoint.createReader ();
    while (aFieldsReader.hasRemaining ())
    {
      final DERValue aField = aFieldsReader.read ();
      // Silently ignore "reasons" and "cRLIssuer"
      if (aField.getTag () == TAG_CONTEXT_0)
        _readDistributionPointName (aField, aTarget);
    }
  }

  /**
   * Extract all CRL distribution point URIs from the provided DER encoded extension value.
   *
   * @param aExtensionValue
   *        The raw extension value as returned by
   *        {@link java.security.cert.X509Certificate#getExtensionValue(String)}. That is the DER
   *        encoded OCTET STRING wrapping the actual extension content. May not be
   *        <code>null</code>.
   * @return Never <code>null</code> but maybe empty list of distribution point URIs.
   * @throws UncheckedIOException
   *         If the provided data is not a valid DER encoded "CRL Distribution Points" extension.
   */
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
}
