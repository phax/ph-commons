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
package com.helger.commons.supplementary.test.java;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.system.EOperatingSystem;

/**
 * Reads a TTF font file and provides access to kerning information. Thanks to
 * the Apache FOP project for their inspiring work!
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public final class FontKerningFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaPrinterTrayFinderFuncTest.class);

  @Immutable
  public static final class KerningKey
  {
    private final int m_nFirstGlyphCode;
    private final int m_nSecondGlyphCode;

    public KerningKey (final int nFirstGlyphCode, final int nSecondGlyphCode)
    {
      m_nFirstGlyphCode = nFirstGlyphCode;
      m_nSecondGlyphCode = nSecondGlyphCode;
    }

    public int getFirstGlyphCode ()
    {
      return m_nFirstGlyphCode;
    }

    public int getSecondGlyphCode ()
    {
      return m_nSecondGlyphCode;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (this == o)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final KerningKey rhs = (KerningKey) o;
      return m_nFirstGlyphCode == rhs.m_nFirstGlyphCode && m_nSecondGlyphCode == rhs.m_nSecondGlyphCode;
    }

    @Override
    public int hashCode ()
    {
      return new HashCodeGenerator (this).append (m_nFirstGlyphCode).append (m_nSecondGlyphCode).getHashCode ();
    }
  }

  private static final class Kerning
  {
    private final Map <KerningKey, Integer> m_aKerning;
    private int m_nUnitsPerEm;
    private long m_nBytePosition;
    private long m_nHeadOffset = -1;
    private long m_nKernOffset = -1;

    /**
     * @param aIS
     *        The data for the TTF font.
     * @throws IOException
     *         If the font could not be read.
     */
    public Kerning (final InputStream aIS) throws IOException
    {
      if (aIS == null)
        throw new IllegalArgumentException ("aIS cannot be null.");

      final InputStream aDIS = aIS;
      try
      {
        _readTableDirectory (aDIS);
        if (m_nHeadOffset == -1)
          throw new IOException ("HEAD table not found.");
        if (m_nKernOffset == -1)
        {
          s_aLogger.info ("No kerning information present!");
          m_aKerning = CollectionHelper.newUnmodifiableMap ();
          return;
        }
        m_aKerning = new HashMap <KerningKey, Integer> (2048);
        if (m_nHeadOffset < m_nKernOffset)
        {
          _readHEAD (aDIS);
          _readKERN (aDIS);
        }
        else
        {
          _readKERN (aDIS);
          _readHEAD (aDIS);
        }
      }
      finally
      {
        StreamHelper.close (aDIS);
      }
    }

    /**
     * Returns the kerning value for the specified glyphs. The glyph code for a
     * Unicode codepoint can be retrieved with
     * {@link GlyphVector#getGlyphCode(int)}.
     *
     * @param nFirstGlyphCode
     *        first glyph code
     * @param nSecondGlyphCode
     *        second glyph code
     * @param fFontSize
     *        font size
     * @return kerning value
     */
    public float getValue (final int nFirstGlyphCode, final int nSecondGlyphCode, final float fFontSize)
    {
      final Integer aValue = m_aKerning.get (new KerningKey (nFirstGlyphCode, nSecondGlyphCode));
      if (aValue == null)
        return 0f;
      return aValue.intValue () * fFontSize / m_nUnitsPerEm;
    }

    private void _readTableDirectory (final InputStream aIS) throws IOException
    {
      _skip (aIS, 4);
      final int nTableCount = _readUnsignedShort (aIS);
      _skip (aIS, 6);

      final byte [] tagBytes = new byte [4];
      for (int i = 0; i < nTableCount; i++)
      {
        m_nBytePosition += tagBytes.length;
        StreamHelper.readFully (aIS, tagBytes);
        final String tag = CharsetManager.getAsString (tagBytes, CCharset.CHARSET_ISO_8859_1_OBJ);

        _skip (aIS, 4);
        final long nOffset = _readUnsignedInt (aIS);
        _skip (aIS, 4);

        if (tag.equals ("head"))
        {
          m_nHeadOffset = nOffset;
          if (m_nKernOffset != -1)
            break;
        }
        else
          if (tag.equals ("kern"))
          {
            m_nKernOffset = nOffset;
            if (m_nHeadOffset != -1)
              break;
          }
      }
    }

    private void _readHEAD (final InputStream aIS) throws IOException
    {
      _seek (aIS, m_nHeadOffset + 2 * 4 + 2 * 4 + 2);
      m_nUnitsPerEm = _readUnsignedShort (aIS);
      if (false)
        s_aLogger.info ("Units per EM = " + m_nUnitsPerEm + " @ " + m_nBytePosition);
    }

    private void _readKERN (final InputStream aIS) throws IOException
    {
      if (false)
        s_aLogger.info ("Going to KERN");
      _seek (aIS, m_nKernOffset + 2);
      for (int n = _readUnsignedShort (aIS); n > 0; n--)
      {
        _skip (aIS, 2 * 2);
        int k = _readUnsignedShort (aIS);
        if (!((k & 1) != 0) || (k & 2) != 0 || (k & 4) != 0)
          return;
        if (k >> 8 != 0)
          continue;
        k = _readUnsignedShort (aIS);
        _skip (aIS, 3 * 2);
        if (false)
          s_aLogger.info ("Reading " + k + " kernings");
        while (k-- > 0)
        {
          final int nFirstGlyphCode = _readUnsignedShort (aIS);
          final int nSecondGlyphCode = _readUnsignedShort (aIS);
          final int nValue = _readShort (aIS);
          if (nValue != 0)
            m_aKerning.put (new KerningKey (nFirstGlyphCode, nSecondGlyphCode), Integer.valueOf (nValue));
        }
      }
    }

    private int _readUnsignedByte (final InputStream aIS) throws IOException
    {
      m_nBytePosition++;
      final int b = aIS.read ();
      if (b == -1)
        throw new EOFException ("Unexpected end of file.");
      return b;
    }

    private int _readUnsignedShort (final InputStream aIS) throws IOException
    {
      return (_readUnsignedByte (aIS) << 8) + _readUnsignedByte (aIS);
    }

    private short _readShort (final InputStream aIS) throws IOException
    {
      return (short) _readUnsignedShort (aIS);
    }

    private long _readUnsignedInt (final InputStream aIS) throws IOException
    {
      return (((long) _readUnsignedShort (aIS)) << 16) | _readUnsignedShort (aIS);
    }

    private void _skip (final InputStream aIS, final int bytes) throws IOException
    {
      StreamHelper.skipFully (aIS, bytes);
      m_nBytePosition += bytes;
    }

    private void _seek (final InputStream aIS, final long position) throws IOException
    {
      if (false)
        s_aLogger.info ("position=" + position + "; pos=" + m_nBytePosition);
      StreamHelper.skipFully (aIS, position - m_nBytePosition);
      m_nBytePosition = position;
    }
  }

  @Test
  public void testKerning () throws IOException
  {
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
    {
      // required to get graphics up and running...
      GraphicsEnvironment.getLocalGraphicsEnvironment ();
      final int nFontSize = 25;

      final Map <TextAttribute, Object> aTextAttributes = new HashMap <TextAttribute, Object> ();
      aTextAttributes.put (TextAttribute.FAMILY, "Arial");
      aTextAttributes.put (TextAttribute.SIZE, Float.valueOf (nFontSize));
      final Font aFont = Font.getFont (aTextAttributes);

      final char [] aChars = "T,".toCharArray ();
      final GlyphVector aGlyphVector = aFont.layoutGlyphVector (new FontRenderContext (new AffineTransform (),
                                                                                       false,
                                                                                       true),
                                                                aChars,
                                                                0,
                                                                aChars.length,
                                                                Font.LAYOUT_LEFT_TO_RIGHT);
      final int tCode = aGlyphVector.getGlyphCode (0);
      final int commaCode = aGlyphVector.getGlyphCode (1);
      final Kerning aKerning = new Kerning (new FileInputStream (System.getenv ("windir") + "/fonts/ARIAL.TTF"));
      s_aLogger.info (Float.toString (aKerning.getValue (tCode, commaCode, nFontSize)));
    }
    else
      s_aLogger.warn ("Works only on Windows!");
  }
}
