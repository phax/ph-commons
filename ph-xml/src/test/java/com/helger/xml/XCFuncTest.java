package com.helger.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.charset.CharsetHelper.InputStreamAndCharset;
import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;

public class XCFuncTest
{
  /**
   * Main match method
   *
   * @param aSrcBytes
   *        The bytes to compare to this type.
   * @param nSrcOffset
   *        The offset within aBytes to start searching
   * @param aCmpBytes
   *        The compare bytes of this object.
   * @return <code>true</code> if the bytes match, <code>false</code> otherwise.
   */
  private static boolean _match (@Nonnull final byte [] aSrcBytes,
                                 @Nonnegative final int nSrcOffset,
                                 @Nonnull final byte [] aCmpBytes)
  {
    final int nEnd = aCmpBytes.length;
    for (int i = 0; i < nEnd; ++i)
      if (aSrcBytes[nSrcOffset + i] != aCmpBytes[i])
        return false;
    return true;
  }

  private static final ICommonsSet <Charset> XML_CHARSETS = new CommonsHashSet <> ();
  static
  {
    for (final Charset c : CharsetHelper.getAllCharsets ().values ())
    {
      // Charset must be able to encode!
      // The special names failed on Windows 10, JDK 1.8.0_131
      if (c.canEncode () &&
          !c.name ().equals ("JIS_X0212-1990") &&
          !c.name ().equals ("x-IBM300") &&
          !c.name ().equals ("x-IBM834") &&
          !c.name ().equals ("x-JIS0208") &&
          !c.name ().equals ("x-MacDingbat") &&
          !c.name ().equals ("x-MacSymbol"))
        XML_CHARSETS.add (c);
    }
  }
  private static final Charset CHARSET_UTF_32BE = Charset.forName ("UTF-32BE");
  private static final Charset CHARSET_UTF_32LE = Charset.forName ("UTF-32LE");
  private static final Charset CHARSET_EBCDIC = Charset.forName ("Cp1047");
  private static final Charset CHARSET_IBM290 = Charset.forName ("IBM290");

  private static final byte [] CS_UTF32_BE = new byte [] { 0, 0, 0, 0x3c };
  private static final byte [] CS_UTF32_LE = new byte [] { 0x3c, 0, 0, 0 };
  private static final byte [] CS_UTF16_BE = new byte [] { 0, 0x3c, 0, 0x3f };
  private static final byte [] CS_UTF16_LE = new byte [] { 0x3c, 0, 0x3f, 0 };
  private static final byte [] CS_UTF8 = new byte [] { 0x3c, 0x3f, 0x78, 0x6d };
  private static final byte [] CS_EBCDIC = new byte [] { 0x4c, 0x6f, (byte) 0xa7, (byte) 0x94 };
  private static final byte [] CS_IBM290 = new byte [] { 0x4c, 0x6f, (byte) 0xb7, (byte) 0x75 };

  @Nullable
  private static Charset _parseXMLEncoding (@Nonnull final byte [] aBytes,
                                            @Nonnegative final int nOfs,
                                            @Nonnull final Charset aDeterminedBasic)
  {
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aBytes,
                                                                                          nOfs,
                                                                                          aBytes.length - nOfs);
         final Reader aReader = new InputStreamReader (aIS, aDeterminedBasic))
    {
      final StringBuilder aSB = new StringBuilder ();
      int c;
      while ((c = aReader.read ()) != -1)
      {
        aSB.append ((char) c);
        if (c == '>' && aIS.getPosition () >= (nOfs + 4096))
        {
          // Stop at first '>' as this will end the <?xml..?> stuff or after
          // 4096 bytes
          break;
        }
      }
      final int nMaxChars = aSB.length ();
      final int nIndex = aSB.indexOf ("encoding");
      if (nIndex > 0)
      {
        int nStartIndex = nIndex + "encoding".length ();
        while (nStartIndex < nMaxChars && Character.isWhitespace (aSB.charAt (nStartIndex)))
          nStartIndex++;
        if (nStartIndex < nMaxChars && aSB.charAt (nStartIndex) == '=')
        {
          nStartIndex++;
          while (nStartIndex < nMaxChars && Character.isWhitespace (aSB.charAt (nStartIndex)))
            nStartIndex++;
          if (nStartIndex < nMaxChars)
          {
            final char cQuote = aSB.charAt (nStartIndex);
            // Ü is IBM1026 hack
            if (cQuote == '"' || cQuote == '\'' || cQuote == 'Ü')
            {
              nStartIndex++;
              // read until next quote
              final int nEndIndex = aSB.indexOf (Character.toString (cQuote), nStartIndex);
              if (nEndIndex > 0)
              {
                final String sEncoding = aSB.substring (nStartIndex, nEndIndex).trim ();
                return Charset.forName (sEncoding);
              }
            }
          }
        }
      }
      return null;
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nullable
  private static Charset determineXMLCharset (@Nonnull final byte [] aBytes)
  {
    Charset aCharset = null;
    int nSearchOfs = 0;

    if (aBytes.length > 0)
    {
      // Read at maximum 4 bytes (max BOM bytes)
      try (NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aBytes,
                                                                                      0,
                                                                                      Math.min (EUnicodeBOM.getMaximumByteCount (),
                                                                                                aBytes.length)))
      {
        // Check for BOM first
        final InputStreamAndCharset aISC = CharsetHelper.getInputStreamAndCharsetFromBOM (aIS);
        if (aISC.hasBOM ())
        {
          // A BOM was found, but not necessarily a charset could uniquely be
          // identified - skip the
          // BOM bytes and continue determination from there
          nSearchOfs = aISC.getBOM ().getByteCount ();
        }

        if (aISC.hasCharset ())
        {
          // A BOM was found, and that BOM also has a unique charset assigned
          aCharset = aISC.getCharset ();
        }
      }
    }

    // No charset found and enough bytes left?
    if (aCharset == null && aBytes.length - nSearchOfs >= 4)
      if (_match (aBytes, nSearchOfs, CS_UTF32_BE))
        aCharset = CHARSET_UTF_32BE;
      else
        if (_match (aBytes, nSearchOfs, CS_UTF32_LE))
          aCharset = CHARSET_UTF_32LE;
        else
          if (_match (aBytes, nSearchOfs, CS_UTF16_BE))
            aCharset = StandardCharsets.UTF_16BE;
          else
            if (_match (aBytes, nSearchOfs, CS_UTF16_LE))
              aCharset = StandardCharsets.UTF_16LE;
            else
              if (_match (aBytes, nSearchOfs, CS_UTF8))
                aCharset = StandardCharsets.UTF_8;
              else
                if (_match (aBytes, nSearchOfs, CS_EBCDIC))
                  aCharset = CHARSET_EBCDIC;
                else
                  if (_match (aBytes, nSearchOfs, CS_IBM290))
                    aCharset = CHARSET_IBM290;

    if (aCharset == null)
    {
      // Fallback charset is always UTF-8
      aCharset = StandardCharsets.UTF_8;
    }

    return _parseXMLEncoding (aBytes, nSearchOfs, aCharset);
  }

  @Test
  public void testAllCharsetsDoubleQuotes ()
  {
    for (final Charset c : XML_CHARSETS)
    {
      final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
      aNames.addAll (c.aliases ());
      for (final String sAlias : aNames)
      {
        final String sXML = "<?xml version=\"1.0\" encoding=\"" + sAlias + "\"?><!-- " + c.name () + " --><root />";
        if (false)
          System.out.println (sXML);
        final byte [] aBytes = sXML.getBytes (c);
        final Charset aDetermined = determineXMLCharset (aBytes);
        assertEquals (c, aDetermined);
      }
    }
  }

  @Test
  public void testAllCharsetsSingleQuotes ()
  {
    for (final Charset c : XML_CHARSETS)
    {
      final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
      aNames.addAll (c.aliases ());
      for (final String sAlias : aNames)
      {
        final String sXML = "<?xml version=\"1.0\" encoding='" + sAlias + "'?><!-- " + c.name () + " --><root />";
        if (false)
          System.out.println (sXML);
        final byte [] aBytes = sXML.getBytes (c);
        final Charset aDetermined = determineXMLCharset (aBytes);
        assertEquals (c, aDetermined);
      }
    }
  }

  @Test
  public void testAllBOMCharsets ()
  {
    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
      if (eBOM.hasCharset ())
      {
        final Charset c = eBOM.getCharset ();
        final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
        aNames.addAll (c.aliases ());
        for (final String sAlias : aNames)
        {
          final String sXML = "<?xml version=\"1.0\" encoding=\"" + sAlias + "\"?><!-- " + c.name () + " --><root />";
          if (false)
            System.out.println (sXML);
          final byte [] aBytes = sXML.getBytes (c);
          assertFalse ("Charset " + sAlias + " already contains BOM " + eBOM, eBOM.isPresent (aBytes));

          // Prefix with BOM
          final Charset aDetermined = determineXMLCharset (ArrayHelper.getConcatenated (eBOM.getAllBytes (), aBytes));
          assertEquals (c, aDetermined);
        }
      }
  }

  private static void _testUTF8Good (final String sXML)
  {
    final byte [] aBytes = sXML.getBytes (StandardCharsets.UTF_8);
    final Charset aDetermined = determineXMLCharset (aBytes);
    assertEquals (StandardCharsets.UTF_8, aDetermined);
  }

  private static void _testUTF8Bad (final String sXML)
  {
    final byte [] aBytes = sXML.getBytes (StandardCharsets.UTF_8);
    final Charset aDetermined = determineXMLCharset (aBytes);
    assertNull (aDetermined);
  }

  @Test
  public void testParser ()
  {
    // Double quotes
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"utf-8\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml version=\"1.0\" encoding='utf-8'?><root />");
    // Blanks around
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   \"utf-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   'utf-8'?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"   utf-8  \"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='   utf-8  '?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   \"  utf-8 \"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   '   utf-8   '?><root />");
    // Upper case
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='UTF-8'?><root />");
    // Mixed case
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"Utf-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='Utf-8'?><root />");

    // -- no version

    // Double quotes
    _testUTF8Good ("<?xml encoding=\"utf-8\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml encoding='utf-8'?><root />");
    // Blanks around
    _testUTF8Good ("<?xml encoding  =   \"utf-8\"?><root />");
    _testUTF8Good ("<?xml encoding  =   'utf-8'?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml encoding=\"   utf-8  \"?><root />");
    _testUTF8Good ("<?xml encoding='   utf-8  '?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml encoding  =   \"  utf-8 \"?><root />");
    _testUTF8Good ("<?xml encoding  =   '   utf-8   '?><root />");
    // Upper case
    _testUTF8Good ("<?xml encoding=\"UTF-8\"?><root />");
    _testUTF8Good ("<?xml encoding='UTF-8'?><root />");
    // Mixed case
    _testUTF8Good ("<?xml encoding=\"Utf-8\"?><root />");
    _testUTF8Good ("<?xml encoding='Utf-8'?><root />");

    // -- first encoding than version

    // Double quotes
    _testUTF8Good ("<?xml encoding=\"utf-8\" version=\"1.0\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml encoding='utf-8' version=\"1.0\"?><root />");
    // Blanks around
    _testUTF8Good ("<?xml encoding  =   \"utf-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding  =   'utf-8' version=\"1.0\"?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml encoding=\"   utf-8  \" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='   utf-8  ' version=\"1.0\"?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml encoding  =   \"  utf-8 \" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding  =   '   utf-8   ' version=\"1.0\"?><root />");
    // Upper case
    _testUTF8Good ("<?xml encoding=\"UTF-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='UTF-8' version=\"1.0\"?><root />");
    // Mixed case
    _testUTF8Good ("<?xml encoding=\"Utf-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='Utf-8' version=\"1.0\"?><root />");

    // Bad cases
    _testUTF8Bad ("");
    _testUTF8Bad ("abc");
    _testUTF8Bad ("<");
    _testUTF8Bad ("<?");
    _testUTF8Bad ("<?x");
    _testUTF8Bad ("<?xm");
    _testUTF8Bad ("<?xml");
    _testUTF8Bad ("<?xml version=\"1.0\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8'");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8?>");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8?>");
  }
}
