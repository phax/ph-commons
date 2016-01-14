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
package com.helger.commons.base64;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;

/**
 * <p>
 * Encodes and decodes to and from Base64 notation.
 * </p>
 * <p>
 * Homepage: <a href="http://iharder.net/base64">http://iharder.net/base64</a>.
 * </p>
 * <p>
 * Example:
 * </p>
 * <code>String encoded = Base64.encode( myByteArray );</code> <br>
 * <code>byte[] myByteArray = Base64.decode( encoded );</code>
 * <p>
 * The <tt>options</tt> parameter, which appears in a few places, is used to
 * pass several pieces of information to the encoder. In the "higher level"
 * methods such as encodeBytes( bytes, options ) the options parameter can be
 * used to indicate such things as first gzipping the bytes before encoding
 * them, not inserting linefeeds, and encoding using the URL-safe and Ordered
 * dialects.
 * </p>
 * <p>
 * Note, according to
 * <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>, Section 2.1,
 * implementations should not add line feeds unless explicitly told to do so.
 * I've got Base64 set to this behavior now, although earlier versions broke
 * lines by default.
 * </p>
 * <p>
 * The constants defined in Base64 can be OR-ed together to combine options, so
 * you might make a call like this:
 * </p>
 * <code>String encoded = Base64.encodeBytes( mybytes, Base64.GZIP | Base64.DO_BREAK_LINES );</code>
 * <p>
 * to compress the data before encoding it and then making the output have
 * newline characters.
 * </p>
 * <p>
 * Also...
 * </p>
 * <code>String encoded = Base64.encodeBytes( crazyString.getBytes() );</code>
 * <p>
 * Change Log:
 * </p>
 * <ul>
 * <li>v2.3.7 - Fixed subtle bug when base 64 input stream contained the value
 * 01111111, which is an invalid base 64 character but should not throw an
 * ArrayIndexOutOfBoundsException either. Led to discovery of mishandling (or
 * potential for better handling) of other bad input characters. You should now
 * get an IOException if you try decoding something that has bad characters in
 * it.</li>
 * <li>v2.3.6 - Fixed bug when breaking lines and the final byte of the encoded
 * string ended in the last column; the buffer was not properly shrunk and
 * contained an extra (null) byte that made it into the string.</li>
 * <li>v2.3.5 - Fixed bug in {@link #encodeFromFile} where estimated buffer size
 * was wrong for files of size 31, 34, and 37 bytes.</li>
 * <li>v2.3.4 - Fixed bug when working with gzipped streams whereby flushing the
 * Base64.OutputStream closed the Base64 encoding (by padding with equals signs)
 * too soon. Also added an option to suppress the automatic decoding of gzipped
 * streams. Also added experimental support for specifying a class loader when
 * using the
 * {@link #decodeToObject(java.lang.String, int, java.lang.ClassLoader)} method.
 * </li>
 * <li>v2.3.3 - Changed default char encoding to US-ASCII which reduces the
 * internal Java footprint with its CharEncoders and so forth. Fixed some
 * javadocs that were inconsistent. Removed imports and specified things like
 * IOException explicitly inline.</li>
 * <li>v2.3.2 - Reduced memory footprint! Finally refined the "guessing" of how
 * big the final encoded data will be so that the code doesn't have to create
 * two output arrays: an oversized initial one and then a final, exact-sized
 * one. Big win when using the {@link #encodeBytesToBytes(byte[])} family of
 * methods (and not using the gzip options which uses a different mechanism with
 * streams and stuff).</li>
 * <li>v2.3.1 - Added {@link #encodeBytesToBytes(byte[], int, int, int)} and
 * some similar helper methods to be more efficient with memory by not returning
 * a String but just a byte array.</li>
 * <li>v2.3 - <strong>This is not a drop-in replacement!</strong> This is two
 * years of comments and bug fixes queued up and finally executed. Thanks to
 * everyone who sent me stuff, and I'm sorry I wasn't able to distribute your
 * fixes to everyone else. Much bad coding was cleaned up including throwing
 * exceptions where necessary instead of returning null values or something
 * similar. Here are some changes that may affect you:
 * <ul>
 * <li><em>Does not break lines, by default.</em> This is to keep in compliance
 * with <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>.</li>
 * <li><em>Throws exceptions instead of returning null values.</em> Because some
 * operations (especially those that may permit the GZIP option) use IO streams,
 * there is a possiblity of an IOException being thrown. After some discussion
 * and thought, I've changed the behavior of the methods to throw IOExceptions
 * rather than return null if ever there's an error. I think this is more
 * appropriate, though it will require some changes to your code. Sorry, it
 * should have been done this way to begin with.</li>
 * <li><em>Removed all references to System.out, System.err, and the like.</em>
 * Shame on me. All I can say is sorry they were ever there.</li>
 * <li><em>Throws NullPointerExceptions and IllegalArgumentExceptions</em> as
 * needed such as when passed arrays are null or offsets are invalid.</li>
 * <li>Cleaned up as much javadoc as I could to avoid any javadoc warnings. This
 * was especially annoying before for people who were thorough in their own
 * projects and then had gobs of javadoc warnings on this file.</li>
 * </ul>
 * <li>v2.2.1 - Fixed bug using URL_SAFE and ORDERED encodings. Fixed bug when
 * using very small files (~&lt; 40 bytes).</li>
 * <li>v2.2 - Added some helper methods for encoding/decoding directly from one
 * file to the next. Also added a main() method to support command line
 * encoding/decoding from one file to the next. Also added these Base64
 * dialects:
 * <ol>
 * <li>The default is RFC3548 format.</li>
 * <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.URLSAFE_FORMAT) generates
 * URL and file name friendly format as described in Section 4 of RFC3548.
 * http://www.faqs.org/rfcs/rfc3548.html</li>
 * <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.ORDERED_FORMAT) generates
 * URL and file name friendly format that preserves lexical ordering as
 * described in http://www.faqs.org/qa/rfcc-1940.html</li>
 * </ol>
 * Special thanks to Jim Kellerman at
 * <a href="http://www.powerset.com/">http://www.powerset.com/</a> for
 * contributing the new Base64 dialects.</li>
 * <li>v2.1 - Cleaned up javadoc comments and unused variables and methods.
 * Added some convenience methods for reading and writing to and from files.
 * </li>
 * <li>v2.0.2 - Now specifies UTF-8 encoding in places where the code fails on
 * systems with other encodings (like EBCDIC).</li>
 * <li>v2.0.1 - Fixed an error when decoding a single byte, that is, when the
 * encoded data was a single byte.</li>
 * <li>v2.0 - I got rid of methods that used booleans to set options. Now
 * everything is more consolidated and cleaner. The code now detects when data
 * that's being decoded is gzip-compressed and will decompress it automatically.
 * Generally things are cleaner. You'll probably have to change some method
 * calls that you were making to support the new options format (<tt>int</tt>s
 * that you "OR" together).</li>
 * <li>v1.5.1 - Fixed bug when decompressing and decoding to a byte[] using
 * <tt>decode( String s, boolean gzipCompressed )</tt>. Added the ability to
 * "suspend" encoding in the Output Stream so you can turn on and off the
 * encoding if you need to embed base64 data in an otherwise "normal" stream
 * (like an XML file).</li>
 * <li>v1.5 - Output stream pases on flush() command but doesn't do anything
 * itself. This helps when using GZIP streams. Added the ability to
 * GZip-compress objects before encoding them.</li>
 * <li>v1.4 - Added helper methods to read/write files.</li>
 * <li>v1.3.6 - Fixed OutputStream.flush() so that 'position' is reset.</li>
 * <li>v1.3.5 - Added flag to turn on and off line breaks. Fixed bug in input
 * stream where last buffer being read, if not completely full, was not
 * returned.</li>
 * <li>v1.3.4 - Fixed when "improperly padded stream" error was thrown at the
 * wrong time.</li>
 * <li>v1.3.3 - Fixed I/O streams which were totally messed up.</li>
 * </ul>
 * <p>
 * I am placing this code in the Public Domain. Do with it as you will. This
 * software comes with no guarantees or warranties but with plenty of
 * well-wishing instead! Please visit
 * <a href="http://iharder.net/base64">http://iharder.net/base64</a>
 * periodically to check for updates or to contribute improvements.
 * </p>
 *
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.3.7
 */
@Immutable
public final class Base64
{

  /* ******** P U B L I C F I E L D S ******** */

  /** No options specified. Value is zero. */
  public static final int NO_OPTIONS = 0;

  /** Specify encoding in first bit. Value is one. */
  public static final int ENCODE = 1;

  /** Specify decoding in first bit. Value is zero. */
  public static final int DECODE = 0;

  /**
   * Specify that data should be gzip-compressed in second bit. Value is two.
   */
  public static final int GZIP = 2;

  /**
   * Specify that gzipped data should <em>not</em> be automatically gunzipped.
   */
  public static final int DONT_GUNZIP = 4;

  /** Do break lines when encoding. Value is 8. */
  public static final int DO_BREAK_LINES = 8;

  /**
   * Encode using Base64-like encoding that is URL- and Filename-safe as
   * described in Section 4 of RFC3548:
   * <a href="http://www.faqs.org/rfcs/rfc3548.html" >http://www.faqs.org/rfcs/
   * rfc3548.html</a>. It is important to note that data encoded this way is
   * <em>not</em> officially valid Base64, or at the very least should not be
   * called Base64 without also specifying that is was encoded using the URL-
   * and Filename-safe dialect.
   */
  public static final int URL_SAFE = 16;

  /**
   * Encode using the special "ordered" dialect of Base64 described here:
   * <a href ="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/
   * rfcc- 1940. html</a>.
   */
  public static final int ORDERED = 32;

  /* ******** P R I V A T E F I E L D S ******** */

  /** Maximum line length (76) of Base64 output. */
  static final int MAX_LINE_LENGTH = 76;

  /** The equals sign (=) as a byte. */
  private static final byte EQUALS_SIGN = (byte) '=';

  /** The new line character (\n) as a byte. */
  static final byte NEW_LINE = (byte) '\n';

  /** Preferred encoding. */
  public static final Charset PREFERRED_ENCODING = CCharset.CHARSET_US_ASCII_OBJ;

  // Indicates white space in encoding
  static final byte WHITE_SPACE_ENC = -5;
  // Indicates equals sign in encoding
  private static final byte EQUALS_SIGN_ENC = -1;

  /* ******** S T A N D A R D B A S E 6 4 A L P H A B E T ******** */

  /** The 64 valid Base64 values. */
  /*
   * Host platform me be something funny like EBCDIC, so we hardcode these
   * values.
   */
  private static final byte [] _STANDARD_ALPHABET = { (byte) 'A',
                                                      (byte) 'B',
                                                      (byte) 'C',
                                                      (byte) 'D',
                                                      (byte) 'E',
                                                      (byte) 'F',
                                                      (byte) 'G',
                                                      (byte) 'H',
                                                      (byte) 'I',
                                                      (byte) 'J',
                                                      (byte) 'K',
                                                      (byte) 'L',
                                                      (byte) 'M',
                                                      (byte) 'N',
                                                      (byte) 'O',
                                                      (byte) 'P',
                                                      (byte) 'Q',
                                                      (byte) 'R',
                                                      (byte) 'S',
                                                      (byte) 'T',
                                                      (byte) 'U',
                                                      (byte) 'V',
                                                      (byte) 'W',
                                                      (byte) 'X',
                                                      (byte) 'Y',
                                                      (byte) 'Z',
                                                      (byte) 'a',
                                                      (byte) 'b',
                                                      (byte) 'c',
                                                      (byte) 'd',
                                                      (byte) 'e',
                                                      (byte) 'f',
                                                      (byte) 'g',
                                                      (byte) 'h',
                                                      (byte) 'i',
                                                      (byte) 'j',
                                                      (byte) 'k',
                                                      (byte) 'l',
                                                      (byte) 'm',
                                                      (byte) 'n',
                                                      (byte) 'o',
                                                      (byte) 'p',
                                                      (byte) 'q',
                                                      (byte) 'r',
                                                      (byte) 's',
                                                      (byte) 't',
                                                      (byte) 'u',
                                                      (byte) 'v',
                                                      (byte) 'w',
                                                      (byte) 'x',
                                                      (byte) 'y',
                                                      (byte) 'z',
                                                      (byte) '0',
                                                      (byte) '1',
                                                      (byte) '2',
                                                      (byte) '3',
                                                      (byte) '4',
                                                      (byte) '5',
                                                      (byte) '6',
                                                      (byte) '7',
                                                      (byte) '8',
                                                      (byte) '9',
                                                      (byte) '+',
                                                      (byte) '/' };

  /**
   * Translates a Base64 value to either its 6-bit reconstruction value or a
   * negative number indicating some other meaning.
   **/
  private static final byte [] _STANDARD_DECODABET = { -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal
                                                           // 0
                                                           // -
                                                           // 8
                                                       -5,
                                                       -5, // Whitespace: Tab
                                                           // and
                                                           // Linefeed
                                                       -9,
                                                       -9, // Decimal 11 - 12
                                                       -5, // Whitespace:
                                                           // Carriage Return
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 14 - 26
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 27 - 31
                                                       -5, // Whitespace: Space
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 33 - 42
                                                       62, // Plus sign at
                                                           // decimal 43
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 44 - 46
                                                       63, // Slash at decimal
                                                           // 47
                                                       52,
                                                       53,
                                                       54,
                                                       55,
                                                       56,
                                                       57,
                                                       58,
                                                       59,
                                                       60,
                                                       61, // Numbers zero
                                                           // through nine
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 58 - 60
                                                       -1, // Equals sign at
                                                           // decimal 61
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 62 - 64
                                                       0,
                                                       1,
                                                       2,
                                                       3,
                                                       4,
                                                       5,
                                                       6,
                                                       7,
                                                       8,
                                                       9,
                                                       10,
                                                       11,
                                                       12,
                                                       13, // Letters 'A'
                                                           // through
                                                           // 'N'
                                                       14,
                                                       15,
                                                       16,
                                                       17,
                                                       18,
                                                       19,
                                                       20,
                                                       21,
                                                       22,
                                                       23,
                                                       24,
                                                       25, // Letters 'O'
                                                           // through
                                                           // 'Z'
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 91 - 96
                                                       26,
                                                       27,
                                                       28,
                                                       29,
                                                       30,
                                                       31,
                                                       32,
                                                       33,
                                                       34,
                                                       35,
                                                       36,
                                                       37,
                                                       38, // Letters 'a'
                                                           // through
                                                           // 'm'
                                                       39,
                                                       40,
                                                       41,
                                                       42,
                                                       43,
                                                       44,
                                                       45,
                                                       46,
                                                       47,
                                                       48,
                                                       49,
                                                       50,
                                                       51, // Letters 'n'
                                                           // through
                                                           // 'z'
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9 // Decimal 123 - 127
                                                       ,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 128 - 139
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 140 - 152
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 153 - 165
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 166 - 178
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 179 - 191
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 192 - 204
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 205 - 217
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 218 - 230
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 231 - 243
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9 // Decimal 244 - 255
  };

  /* ******** U R L S A F E B A S E 6 4 A L P H A B E T ******** */

  /**
   * Used in the URL- and Filename-safe dialect described in Section 4 of
   * RFC3548:
   * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org
   * /rfcs/rfc3548.html</a>. Notice that the last two bytes become "hyphen" and
   * "underscore" instead of "plus" and "slash."
   */
  private static final byte [] _URL_SAFE_ALPHABET = { (byte) 'A',
                                                      (byte) 'B',
                                                      (byte) 'C',
                                                      (byte) 'D',
                                                      (byte) 'E',
                                                      (byte) 'F',
                                                      (byte) 'G',
                                                      (byte) 'H',
                                                      (byte) 'I',
                                                      (byte) 'J',
                                                      (byte) 'K',
                                                      (byte) 'L',
                                                      (byte) 'M',
                                                      (byte) 'N',
                                                      (byte) 'O',
                                                      (byte) 'P',
                                                      (byte) 'Q',
                                                      (byte) 'R',
                                                      (byte) 'S',
                                                      (byte) 'T',
                                                      (byte) 'U',
                                                      (byte) 'V',
                                                      (byte) 'W',
                                                      (byte) 'X',
                                                      (byte) 'Y',
                                                      (byte) 'Z',
                                                      (byte) 'a',
                                                      (byte) 'b',
                                                      (byte) 'c',
                                                      (byte) 'd',
                                                      (byte) 'e',
                                                      (byte) 'f',
                                                      (byte) 'g',
                                                      (byte) 'h',
                                                      (byte) 'i',
                                                      (byte) 'j',
                                                      (byte) 'k',
                                                      (byte) 'l',
                                                      (byte) 'm',
                                                      (byte) 'n',
                                                      (byte) 'o',
                                                      (byte) 'p',
                                                      (byte) 'q',
                                                      (byte) 'r',
                                                      (byte) 's',
                                                      (byte) 't',
                                                      (byte) 'u',
                                                      (byte) 'v',
                                                      (byte) 'w',
                                                      (byte) 'x',
                                                      (byte) 'y',
                                                      (byte) 'z',
                                                      (byte) '0',
                                                      (byte) '1',
                                                      (byte) '2',
                                                      (byte) '3',
                                                      (byte) '4',
                                                      (byte) '5',
                                                      (byte) '6',
                                                      (byte) '7',
                                                      (byte) '8',
                                                      (byte) '9',
                                                      (byte) '-',
                                                      (byte) '_' };

  /**
   * Used in decoding URL- and Filename-safe dialects of Base64.
   */
  private static final byte [] _URL_SAFE_DECODABET = { -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal
                                                           // 0
                                                           // -
                                                           // 8
                                                       -5,
                                                       -5, // Whitespace: Tab
                                                           // and
                                                           // Linefeed
                                                       -9,
                                                       -9, // Decimal 11 - 12
                                                       -5, // Whitespace:
                                                           // Carriage Return
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 14 - 26
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 27 - 31
                                                       -5, // Whitespace: Space
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 33 - 42
                                                       -9, // Plus sign at
                                                           // decimal 43
                                                       -9, // Decimal 44
                                                       62, // Minus sign at
                                                           // decimal 45
                                                       -9, // Decimal 46
                                                       -9, // Slash at decimal
                                                           // 47
                                                       52,
                                                       53,
                                                       54,
                                                       55,
                                                       56,
                                                       57,
                                                       58,
                                                       59,
                                                       60,
                                                       61, // Numbers zero
                                                           // through nine
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 58 - 60
                                                       -1, // Equals sign at
                                                           // decimal 61
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 62 - 64
                                                       0,
                                                       1,
                                                       2,
                                                       3,
                                                       4,
                                                       5,
                                                       6,
                                                       7,
                                                       8,
                                                       9,
                                                       10,
                                                       11,
                                                       12,
                                                       13, // Letters 'A'
                                                           // through
                                                           // 'N'
                                                       14,
                                                       15,
                                                       16,
                                                       17,
                                                       18,
                                                       19,
                                                       20,
                                                       21,
                                                       22,
                                                       23,
                                                       24,
                                                       25, // Letters 'O'
                                                           // through
                                                           // 'Z'
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 91 - 94
                                                       63, // Underscore at
                                                           // decimal 95
                                                       -9, // Decimal 96
                                                       26,
                                                       27,
                                                       28,
                                                       29,
                                                       30,
                                                       31,
                                                       32,
                                                       33,
                                                       34,
                                                       35,
                                                       36,
                                                       37,
                                                       38, // Letters 'a'
                                                           // through
                                                           // 'm'
                                                       39,
                                                       40,
                                                       41,
                                                       42,
                                                       43,
                                                       44,
                                                       45,
                                                       46,
                                                       47,
                                                       48,
                                                       49,
                                                       50,
                                                       51, // Letters 'n'
                                                           // through
                                                           // 'z'
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9 // Decimal 123 - 127
                                                       ,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 128 - 139
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 140 - 152
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 153 - 165
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 166 - 178
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 179 - 191
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 192 - 204
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 205 - 217
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 218 - 230
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9, // Decimal 231 - 243
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9,
                                                       -9 // Decimal 244 - 255
  };

  /* ******** O R D E R E D B A S E 6 4 A L P H A B E T ******** */

  /**
   * I don't get the point of this technique, but someone requested it, and it
   * is described here:
   * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www
   * .faqs.org/qa/rfcc-1940.html</a>.
   */
  private static final byte [] _ORDERED_ALPHABET = { (byte) '-',
                                                     (byte) '0',
                                                     (byte) '1',
                                                     (byte) '2',
                                                     (byte) '3',
                                                     (byte) '4',
                                                     (byte) '5',
                                                     (byte) '6',
                                                     (byte) '7',
                                                     (byte) '8',
                                                     (byte) '9',
                                                     (byte) 'A',
                                                     (byte) 'B',
                                                     (byte) 'C',
                                                     (byte) 'D',
                                                     (byte) 'E',
                                                     (byte) 'F',
                                                     (byte) 'G',
                                                     (byte) 'H',
                                                     (byte) 'I',
                                                     (byte) 'J',
                                                     (byte) 'K',
                                                     (byte) 'L',
                                                     (byte) 'M',
                                                     (byte) 'N',
                                                     (byte) 'O',
                                                     (byte) 'P',
                                                     (byte) 'Q',
                                                     (byte) 'R',
                                                     (byte) 'S',
                                                     (byte) 'T',
                                                     (byte) 'U',
                                                     (byte) 'V',
                                                     (byte) 'W',
                                                     (byte) 'X',
                                                     (byte) 'Y',
                                                     (byte) 'Z',
                                                     (byte) '_',
                                                     (byte) 'a',
                                                     (byte) 'b',
                                                     (byte) 'c',
                                                     (byte) 'd',
                                                     (byte) 'e',
                                                     (byte) 'f',
                                                     (byte) 'g',
                                                     (byte) 'h',
                                                     (byte) 'i',
                                                     (byte) 'j',
                                                     (byte) 'k',
                                                     (byte) 'l',
                                                     (byte) 'm',
                                                     (byte) 'n',
                                                     (byte) 'o',
                                                     (byte) 'p',
                                                     (byte) 'q',
                                                     (byte) 'r',
                                                     (byte) 's',
                                                     (byte) 't',
                                                     (byte) 'u',
                                                     (byte) 'v',
                                                     (byte) 'w',
                                                     (byte) 'x',
                                                     (byte) 'y',
                                                     (byte) 'z' };

  /**
   * Used in decoding the "ordered" dialect of Base64.
   */
  private static final byte [] _ORDERED_DECODABET = { -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal
                                                          // 0
                                                          // -
                                                          // 8
                                                      -5,
                                                      -5, // Whitespace: Tab and
                                                          // Linefeed
                                                      -9,
                                                      -9, // Decimal 11 - 12
                                                      -5, // Whitespace:
                                                          // Carriage
                                                          // Return
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 14 - 26
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 27 - 31
                                                      -5, // Whitespace: Space
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 33 - 42
                                                      -9, // Plus sign at
                                                          // decimal
                                                          // 43
                                                      -9, // Decimal 44
                                                      0, // Minus sign at
                                                         // decimal
                                                         // 45
                                                      -9, // Decimal 46
                                                      -9, // Slash at decimal 47
                                                      1,
                                                      2,
                                                      3,
                                                      4,
                                                      5,
                                                      6,
                                                      7,
                                                      8,
                                                      9,
                                                      10, // Numbers zero
                                                          // through
                                                          // nine
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 58 - 60
                                                      -1, // Equals sign at
                                                          // decimal 61
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 62 - 64
                                                      11,
                                                      12,
                                                      13,
                                                      14,
                                                      15,
                                                      16,
                                                      17,
                                                      18,
                                                      19,
                                                      20,
                                                      21,
                                                      22,
                                                      23, // Letters 'A' through
                                                          // 'M'
                                                      24,
                                                      25,
                                                      26,
                                                      27,
                                                      28,
                                                      29,
                                                      30,
                                                      31,
                                                      32,
                                                      33,
                                                      34,
                                                      35,
                                                      36, // Letters 'N' through
                                                          // 'Z'
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 91 - 94
                                                      37, // Underscore at
                                                          // decimal 95
                                                      -9, // Decimal 96
                                                      38,
                                                      39,
                                                      40,
                                                      41,
                                                      42,
                                                      43,
                                                      44,
                                                      45,
                                                      46,
                                                      47,
                                                      48,
                                                      49,
                                                      50, // Letters 'a' through
                                                          // 'm'
                                                      51,
                                                      52,
                                                      53,
                                                      54,
                                                      55,
                                                      56,
                                                      57,
                                                      58,
                                                      59,
                                                      60,
                                                      61,
                                                      62,
                                                      63, // Letters 'n' through
                                                          // 'z'
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9 // Decimal 123 - 127
                                                      ,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 128 - 139
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 140 - 152
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 153 - 165
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 166 - 178
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 179 - 191
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 192 - 204
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 205 - 217
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 218 - 230
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9, // Decimal 231 - 243
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9,
                                                      -9 // Decimal 244 - 255
  };

  /* ******** D E T E R M I N E W H I C H A L H A B E T ******** */

  /**
   * Returns one of the _SOMETHING_ALPHABET byte arrays depending on the options
   * specified. It's possible, though silly, to specify ORDERED <b>and</b>
   * URLSAFE in which case one of them will be picked, though there is no
   * guarantee as to which one will be picked.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  private static byte [] _getAlphabet (final int options)
  {
    if ((options & URL_SAFE) == URL_SAFE)
      return _URL_SAFE_ALPHABET;
    if ((options & ORDERED) == ORDERED)
      return _ORDERED_ALPHABET;
    return _STANDARD_ALPHABET;
  }

  /**
   * Returns one of the _SOMETHING_DECODABET byte arrays depending on the
   * options specified. It's possible, though silly, to specify ORDERED and
   * URL_SAFE in which case one of them will be picked, though there is no
   * guarantee as to which one will be picked.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  static byte [] _getDecodabet (final int options)
  {
    if ((options & URL_SAFE) == URL_SAFE)
      return _URL_SAFE_DECODABET;
    if ((options & ORDERED) == ORDERED)
      return _ORDERED_DECODABET;
    return _STANDARD_DECODABET;
  }

  @PresentForCodeCoverage
  private static final Base64 s_aInstance = new Base64 ();

  /** Defeats instantiation. */
  private Base64 ()
  {}

  /* ******** E N C O D I N G M E T H O D S ******** */

  /**
   * Encodes up to the first three bytes of array <var>threeBytes</var> and
   * returns a four-byte array in Base64 notation. The actual number of
   * significant bytes in your array is given by <var>numSigBytes</var>. The
   * array <var>threeBytes</var> needs only be as big as <var>numSigBytes</var>.
   * Code can reuse a byte array by passing a four-byte array as <var>b4</var>.
   *
   * @param b4
   *        A reusable byte array to reduce array instantiation
   * @param threeBytes
   *        the array to convert
   * @param numSigBytes
   *        the number of significant bytes in your array
   * @return four byte array in Base64 notation.
   * @since 1.5.1
   */
  @Nonnull
  @ReturnsMutableObject ("passed parameter")
  static byte [] _encode3to4 (@Nonnull final byte [] b4,
                              @Nonnull final byte [] threeBytes,
                              @Nonnegative final int numSigBytes,
                              final int options)
  {
    _encode3to4 (threeBytes, 0, numSigBytes, b4, 0, options);
    return b4;
  }

  /**
   * <p>
   * Encodes up to three bytes of the array <var>source</var> and writes the
   * resulting four Base64 bytes to <var>destination</var>. The source and
   * destination arrays can be manipulated anywhere along their length by
   * specifying <var>srcOffset</var> and <var>destOffset</var>. This method does
   * not check to make sure your arrays are large enough to accomodate
   * <var>srcOffset</var> + 3 for the <var>source</var> array or
   * <var>destOffset</var> + 4 for the <var>destination</var> array. The actual
   * number of significant bytes in your array is given by
   * <var>numSigBytes</var>.
   * </p>
   * <p>
   * This is the lowest level of the encoding methods with all possible
   * parameters.
   * </p>
   *
   * @param source
   *        the array to convert
   * @param srcOffset
   *        the index where conversion begins
   * @param numSigBytes
   *        the number of significant bytes in your array
   * @param destination
   *        the array to hold the conversion
   * @param destOffset
   *        the index where output will be put
   * @return the <var>destination</var> array
   * @since 1.3
   */
  @Nonnull
  @ReturnsMutableObject ("passed parameter")
  static byte [] _encode3to4 (@Nonnull final byte [] source,
                              @Nonnegative final int srcOffset,
                              @Nonnegative final int numSigBytes,
                              @Nonnull final byte [] destination,
                              @Nonnegative final int destOffset,
                              final int options)
  {
    final byte [] aAlphabet = _getAlphabet (options);

    // 1 2 3
    // 01234567890123456789012345678901 Bit position
    // --------000000001111111122222222 Array position from threeBytes
    // --------| || || || | Six bit groups to index ALPHABET
    // >>18 >>12 >> 6 >> 0 Right shift necessary
    // 0x3f 0x3f 0x3f Additional AND

    // Create buffer with zero-padding if there are only one or two
    // significant bytes passed in the array.
    // We have to shift left 24 in order to flush out the 1's that appear
    // when Java treats a value as negative that is cast from a byte to an int.
    final int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) |
                       (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) |
                       (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

    switch (numSigBytes)
    {
      case 3:
        destination[destOffset] = aAlphabet[(inBuff >>> 18)];
        destination[destOffset + 1] = aAlphabet[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = aAlphabet[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = aAlphabet[(inBuff) & 0x3f];
        return destination;

      case 2:
        destination[destOffset] = aAlphabet[(inBuff >>> 18)];
        destination[destOffset + 1] = aAlphabet[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = aAlphabet[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = EQUALS_SIGN;
        return destination;

      case 1:
        destination[destOffset] = aAlphabet[(inBuff >>> 18)];
        destination[destOffset + 1] = aAlphabet[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = EQUALS_SIGN;
        destination[destOffset + 3] = EQUALS_SIGN;
        return destination;

      default:
        return destination;
    }
  }

  /**
   * Performs Base64 encoding on the <code>raw</code> ByteBuffer, writing it to
   * the <code>encoded</code> ByteBuffer. This is an experimental feature.
   * Currently it does not pass along any options (such as
   * {@link #DO_BREAK_LINES} or {@link #GZIP}.
   *
   * @param raw
   *        input buffer
   * @param encoded
   *        output buffer
   * @since 2.3
   */
  public static void encode (@Nonnull final ByteBuffer raw, @Nonnull final ByteBuffer encoded)
  {
    final byte [] raw3 = new byte [3];
    final byte [] enc4 = new byte [4];

    while (raw.hasRemaining ())
    {
      final int rem = Math.min (3, raw.remaining ());
      raw.get (raw3, 0, rem);
      _encode3to4 (enc4, raw3, rem, NO_OPTIONS);
      encoded.put (enc4);
    }
  }

  /**
   * Performs Base64 encoding on the <code>raw</code> ByteBuffer, writing it to
   * the <code>encoded</code> CharBuffer. This is an experimental feature.
   * Currently it does not pass along any options (such as
   * {@link #DO_BREAK_LINES} or {@link #GZIP}.
   *
   * @param raw
   *        input buffer
   * @param encoded
   *        output buffer
   * @since 2.3
   */
  public static void encode (@Nonnull final ByteBuffer raw, @Nonnull final CharBuffer encoded)
  {
    final byte [] raw3 = new byte [3];
    final byte [] enc4 = new byte [4];

    while (raw.hasRemaining ())
    {
      final int rem = Math.min (3, raw.remaining ());
      raw.get (raw3, 0, rem);
      _encode3to4 (enc4, raw3, rem, NO_OPTIONS);
      for (int i = 0; i < 4; i++)
      {
        encoded.put ((char) (enc4[i] & 0xFF));
      }
    }
  }

  /**
   * Serializes an object and returns the Base64-encoded version of that
   * serialized object.
   * <p>
   * As of v 2.3, if the object cannot be serialized or there is another error,
   * the method will throw an IOException. <b>This is new to v2.3!</b> In
   * earlier versions, it just returned a null value, but in retrospect that's a
   * pretty poor way to handle it.
   * </p>
   * The object is not GZip-compressed before being encoded.
   *
   * @param serializableObject
   *        The object to encode
   * @return The Base64-encoded object
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if serializedObject is null
   * @since 1.4
   */
  public static String encodeObject (@Nonnull final Serializable serializableObject) throws IOException
  {
    return encodeObject (serializableObject, NO_OPTIONS);
  }

  /**
   * Serializes an object and returns the Base64-encoded version of that
   * serialized object.
   * <p>
   * As of v 2.3, if the object cannot be serialized or there is another error,
   * the method will throw an IOException. <b>This is new to v2.3!</b> In
   * earlier versions, it just returned a null value, but in retrospect that's a
   * pretty poor way to handle it.
   * </p>
   * The object is not GZip-compressed before being encoded.
   * <p>
   * Example options:
   *
   * <pre>
   *   GZIP: gzip-compresses object before encoding it.
   *   DO_BREAK_LINES: break lines at 76 characters
   * </pre>
   * <p>
   * Example: <code>encodeObject( myObj, Base64.GZIP )</code> or
   * <p>
   * Example:
   * <code>encodeObject( myObj, Base64.GZIP | Base64.DO_BREAK_LINES )</code>
   *
   * @param serializableObject
   *        The object to encode
   * @param options
   *        Specified options
   * @return The Base64-encoded object
   * @see Base64#GZIP
   * @see Base64#DO_BREAK_LINES
   * @throws IOException
   *         if there is an error
   * @since 2.0
   */
  @Nonnull
  public static String encodeObject (@Nonnull final Serializable serializableObject,
                                     final int options) throws IOException
  {
    ValueEnforcer.notNull (serializableObject, "Object");

    // ObjectOutputStream -> (GZIP) -> Base64 -> ByteArrayOutputStream
    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    try (final Base64OutputStream b64os = new Base64OutputStream (baos, ENCODE | options))
    {
      if ((options & GZIP) != 0)
      {
        // Gzip
        try (GZIPOutputStream gzos = new GZIPOutputStream (b64os);
            ObjectOutputStream oos = new ObjectOutputStream (gzos))
        {
          oos.writeObject (serializableObject);
        }
      }
      else
      {
        // Not gzipped
        try (ObjectOutputStream oos = new ObjectOutputStream (b64os))
        {
          oos.writeObject (serializableObject);
        }
      }
    }

    // Return value according to relevant encoding.
    return CharsetManager.getAsString (baos.toByteArray (), PREFERRED_ENCODING);
  }

  /**
   * Encodes a byte array into Base64 notation. Does not GZip-compress data.
   *
   * @param source
   *        The data to convert
   * @return The data in Base64-encoded form
   * @throws NullPointerException
   *         if source array is null
   * @since 1.4
   */
  @Nonnull
  public static String encodeBytes (@Nonnull final byte [] source)
  {
    // Since we're not going to have the GZIP encoding turned on,
    // we're not going to have an IOException thrown, so
    // we should not force the user to have to catch it.
    String encoded = null;
    try
    {
      encoded = encodeBytes (source, 0, source.length, NO_OPTIONS);
    }
    catch (final IOException ex)
    {
      assert false : ex.getMessage ();
    }
    assert encoded != null;
    return encoded;
  }

  /**
   * Encodes a byte array into Base64 notation.
   * <p>
   * Example options:
   *
   * <pre>
   *   GZIP: gzip-compresses object before encoding it.
   *   DO_BREAK_LINES: break lines at 76 characters
   *     Note: Technically, this makes your encoding non-compliant.
   * </pre>
   * <p>
   * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
   * <p>
   * Example:
   * <code>encodeBytes( myData, Base64.GZIP | Base64.DO_BREAK_LINES )</code>
   * <p>
   * As of v 2.3, if there is an error with the GZIP stream, the method will
   * throw an IOException. <b>This is new to v2.3!</b> In earlier versions, it
   * just returned a null value, but in retrospect that's a pretty poor way to
   * handle it.
   * </p>
   *
   * @param source
   *        The data to convert
   * @param options
   *        Specified options
   * @return The Base64-encoded data as a String
   * @see Base64#GZIP
   * @see Base64#DO_BREAK_LINES
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if source array is null
   * @since 2.0
   */
  @Nonnull
  public static String encodeBytes (@Nonnull final byte [] source, final int options) throws IOException
  {
    return encodeBytes (source, 0, source.length, options);
  }

  /**
   * Encodes a byte array into Base64 notation. Does not GZip-compress data.
   * <p>
   * As of v 2.3, if there is an error, the method will throw an IOException.
   * <b>This is new to v2.3!</b> In earlier versions, it just returned a null
   * value, but in retrospect that's a pretty poor way to handle it.
   * </p>
   *
   * @param source
   *        The data to convert
   * @param off
   *        Offset in array where conversion should begin
   * @param len
   *        Length of data to convert
   * @return The Base64-encoded data as a String
   * @throws NullPointerException
   *         if source array is null
   * @throws IllegalArgumentException
   *         if source array, offset, or length are invalid
   * @since 1.4
   */
  @Nonnull
  public static String encodeBytes (@Nonnull final byte [] source, final int off, final int len)
  {
    // Since we're not going to have the GZIP encoding turned on,
    // we're not going to have an IOException thrown, so
    // we should not force the user to have to catch it.
    String encoded = null;
    try
    {
      encoded = encodeBytes (source, off, len, NO_OPTIONS);
    }
    catch (final IOException ex)
    {
      assert false : ex.getMessage ();
    }
    assert encoded != null;
    return encoded;
  }

  /**
   * Encodes a byte array into Base64 notation.
   * <p>
   * Example options:
   *
   * <pre>
   *   GZIP: gzip-compresses object before encoding it.
   *   DO_BREAK_LINES: break lines at 76 characters
   *     Note: Technically, this makes your encoding non-compliant.
   * </pre>
   * <p>
   * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
   * <p>
   * Example:
   * <code>encodeBytes( myData, Base64.GZIP | Base64.DO_BREAK_LINES )</code>
   * <p>
   * As of v 2.3, if there is an error with the GZIP stream, the method will
   * throw an IOException. <b>This is new to v2.3!</b> In earlier versions, it
   * just returned a null value, but in retrospect that's a pretty poor way to
   * handle it.
   * </p>
   *
   * @param source
   *        The data to convert
   * @param off
   *        Offset in array where conversion should begin
   * @param len
   *        Length of data to convert
   * @param options
   *        Specified options
   * @return The Base64-encoded data as a String
   * @see Base64#GZIP
   * @see Base64#DO_BREAK_LINES
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if source array is null
   * @throws IllegalArgumentException
   *         if source array, offset, or length are invalid
   * @since 2.0
   */
  @Nonnull
  public static String encodeBytes (@Nonnull final byte [] source,
                                    final int off,
                                    final int len,
                                    final int options) throws IOException
  {
    final byte [] encoded = encodeBytesToBytes (source, off, len, options);

    // Return value according to relevant encoding.
    return CharsetManager.getAsString (encoded, PREFERRED_ENCODING);
  }

  /**
   * Similar to {@link #encodeBytes(byte[])} but returns a byte array instead of
   * instantiating a String. This is more efficient if you're working with I/O
   * streams and have large data sets to encode.
   *
   * @param source
   *        The data to convert
   * @return The Base64-encoded data as a byte[] (of ASCII characters)
   * @throws NullPointerException
   *         if source array is null
   * @since 2.3.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeBytesToBytes (@Nonnull final byte [] source)
  {
    byte [] encoded;
    try
    {
      encoded = encodeBytesToBytes (source, 0, source.length, NO_OPTIONS);
    }
    catch (final IOException ex)
    {
      assert false : "IOExceptions only come from GZipping, which is turned off: " + ex.getMessage ();
      encoded = null;
    }
    return encoded;
  }

  /**
   * Similar to {@link #encodeBytes(byte[], int, int, int)} but returns a byte
   * array instead of instantiating a String. This is more efficient if you're
   * working with I/O streams and have large data sets to encode.
   *
   * @param source
   *        The data to convert
   * @param off
   *        Offset in array where conversion should begin
   * @param len
   *        Length of data to convert
   * @param options
   *        Specified options
   * @return The Base64-encoded data as a String
   * @see Base64#GZIP
   * @see Base64#DO_BREAK_LINES
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if source array is null
   * @throws IllegalArgumentException
   *         if source array, offset, or length are invalid
   * @since 2.3.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeBytesToBytes (@Nonnull final byte [] source,
                                            @Nonnegative final int off,
                                            @Nonnegative final int len,
                                            final int options) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (source, off, len);

    // Compress?
    if ((options & GZIP) != 0)
    {
      // GZip -> Base64 -> ByteArray
      final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
      try (final Base64OutputStream b64os = new Base64OutputStream (baos, ENCODE | options);
          final GZIPOutputStream gzos = new GZIPOutputStream (b64os))
      {
        gzos.write (source, off, len);
      }
      return baos.toByteArray ();
    }

    // Else, don't compress. Better not to use streams at all then.
    {
      final boolean breakLines = (options & DO_BREAK_LINES) != 0;

      // int len43 = len * 4 / 3;
      // byte[] outBuff = new byte[ ( len43 ) // Main 4:3
      // + ( (len % 3) > 0 ? 4 : 0 ) // Account for padding
      // + (breakLines ? ( len43 / MAX_LINE_LENGTH ) : 0) ]; // New lines
      // Try to determine more precisely how big the array needs to be.
      // If we get it right, we don't have to do an array copy, and
      // we save a bunch of memory.
      int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0); // Bytes needed for
                                                          // actual encoding
      if (breakLines)
      {
        encLen += encLen / MAX_LINE_LENGTH; // Plus extra newline characters
      }
      final byte [] outBuff = new byte [encLen];

      int d = 0;
      int e = 0;
      final int len2 = len - 2;
      int lineLength = 0;
      for (; d < len2; d += 3, e += 4)
      {
        _encode3to4 (source, d + off, 3, outBuff, e, options);

        lineLength += 4;
        if (breakLines && lineLength >= MAX_LINE_LENGTH)
        {
          outBuff[e + 4] = NEW_LINE;
          e++;
          lineLength = 0;
        }
      } // en dfor: each piece of array

      if (d < len)
      {
        _encode3to4 (source, d + off, len - d, outBuff, e, options);
        e += 4;
      }

      // Only resize array if we didn't guess it right.
      if (e <= outBuff.length - 1)
      {
        // If breaking lines and the last byte falls right at
        // the line length (76 bytes per line), there will be
        // one extra byte, and the array will need to be resized.
        // Not too bad of an estimate on array size, I'd say.
        final byte [] finalOut = new byte [e];
        System.arraycopy (outBuff, 0, finalOut, 0, e);
        // System.err.println("Having to resize array from " + outBuff.length +
        // " to " + e );
        return finalOut;
      }

      // System.err.println("No need to resize array.");
      return outBuff;
    }
  }

  /* ******** D E C O D I N G M E T H O D S ******** */

  /**
   * Decodes four bytes from array <var>source</var> and writes the resulting
   * bytes (up to three of them) to <var>destination</var>. The source and
   * destination arrays can be manipulated anywhere along their length by
   * specifying <var>srcOffset</var> and <var>destOffset</var>. This method does
   * not check to make sure your arrays are large enough to accomodate
   * <var>srcOffset</var> + 4 for the <var>source</var> array or
   * <var>destOffset</var> + 3 for the <var>destination</var> array. This method
   * returns the actual number of bytes that were converted from the Base64
   * encoding.
   * <p>
   * This is the lowest level of the decoding methods with all possible
   * parameters.
   * </p>
   *
   * @param source
   *        the array to convert
   * @param srcOffset
   *        the index where conversion begins
   * @param destination
   *        the array to hold the conversion
   * @param destOffset
   *        the index where output will be put
   * @param options
   *        alphabet type is pulled from this (standard, url-safe, ordered)
   * @return the number of decoded bytes converted
   * @throws NullPointerException
   *         if source or destination arrays are null
   * @throws IllegalArgumentException
   *         if srcOffset or destOffset are invalid or there is not enough room
   *         in the array.
   * @since 1.3
   */
  @Nonnegative
  static int _decode4to3 (@Nonnull final byte [] source,
                          final int srcOffset,
                          @Nonnull final byte [] destination,
                          final int destOffset,
                          final int options)
  {
    // Lots of error checking and exception throwing
    ValueEnforcer.notNull (source, "Source");
    ValueEnforcer.notNull (destination, "Destination");
    if (srcOffset < 0 || srcOffset + 3 >= source.length)
    {
      throw new IllegalArgumentException ("Source array with length " +
                                          source.length +
                                          " cannot have offset of " +
                                          srcOffset +
                                          " and still process four bytes.");
    }
    if (destOffset < 0 || destOffset + 2 >= destination.length)
    {
      throw new IllegalArgumentException ("Destination array with length " +
                                          destination.length +
                                          " cannot have offset of " +
                                          destOffset +
                                          " and still store three bytes.");
    }

    final byte [] aDecodabet = _getDecodabet (options);

    // Example: Dk==
    if (source[srcOffset + 2] == EQUALS_SIGN)
    {
      // Two ways to do the same thing. Don't know which way I like best.
      // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6 )
      // | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
      final int outBuff = ((aDecodabet[source[srcOffset]] & 0xFF) << 18) |
                          ((aDecodabet[source[srcOffset + 1]] & 0xFF) << 12);

      destination[destOffset] = (byte) (outBuff >>> 16);
      return 1;
    }

    // Example: DkL=
    if (source[srcOffset + 3] == EQUALS_SIGN)
    {
      // Two ways to do the same thing. Don't know which way I like best.
      // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6 )
      // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
      // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
      final int outBuff = ((aDecodabet[source[srcOffset]] & 0xFF) << 18) |
                          ((aDecodabet[source[srcOffset + 1]] & 0xFF) << 12) |
                          ((aDecodabet[source[srcOffset + 2]] & 0xFF) << 6);

      destination[destOffset] = (byte) (outBuff >>> 16);
      destination[destOffset + 1] = (byte) (outBuff >>> 8);
      return 2;
    }

    // Example: DkLE
    {
      // Two ways to do the same thing. Don't know which way I like best.
      // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6 )
      // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
      // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
      // | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
      final int outBuff = ((aDecodabet[source[srcOffset]] & 0xFF) << 18) |
                          ((aDecodabet[source[srcOffset + 1]] & 0xFF) << 12) |
                          ((aDecodabet[source[srcOffset + 2]] & 0xFF) << 6) |
                          ((aDecodabet[source[srcOffset + 3]] & 0xFF));

      destination[destOffset] = (byte) (outBuff >> 16);
      destination[destOffset + 1] = (byte) (outBuff >> 8);
      destination[destOffset + 2] = (byte) (outBuff);

      return 3;
    }
  }

  /**
   * Low-level access to decoding ASCII characters in the form of a byte array.
   * <strong>Ignores GUNZIP option, if it's set.</strong> This is not generally
   * a recommended method, although it is used internally as part of the
   * decoding process. Special case: if len = 0, an empty array is returned.
   * Still, if you need more speed and reduced memory footprint (and aren't
   * gzipping), consider this method.
   *
   * @param source
   *        The Base64 encoded data
   * @return decoded data
   * @throws IOException
   *         In case of error
   * @since 2.3.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decode (@Nonnull final byte [] source) throws IOException
  {
    return decode (source, NO_OPTIONS);
  }

  /**
   * Low-level access to decoding ASCII characters in the form of a byte array.
   * <strong>Ignores GUNZIP option, if it's set.</strong> This is not generally
   * a recommended method, although it is used internally as part of the
   * decoding process. Special case: if len = 0, an empty array is returned.
   * Still, if you need more speed and reduced memory footprint (and aren't
   * gzipping), consider this method.
   *
   * @param source
   *        The Base64 encoded data
   * @param options
   *        Can specify options such as alphabet type to use
   * @return decoded data
   * @throws IOException
   *         In case of error
   * @since 2.3.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decode (@Nonnull final byte [] source, final int options) throws IOException
  {
    final byte [] decoded = decode (source, 0, source.length, options);
    return decoded;
  }

  /**
   * Low-level access to decoding ASCII characters in the form of a byte array.
   * <strong>Ignores GUNZIP option, if it's set.</strong> This is not generally
   * a recommended method, although it is used internally as part of the
   * decoding process. Special case: if len = 0, an empty array is returned.
   * Still, if you need more speed and reduced memory footprint (and aren't
   * gzipping), consider this method.
   *
   * @param source
   *        The Base64 encoded data
   * @param off
   *        The offset of where to begin decoding
   * @param len
   *        The length of characters to decode
   * @param options
   *        Can specify options such as alphabet type to use
   * @return decoded data
   * @throws IOException
   *         If bogus characters exist in source data
   * @since 1.3
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decode (@Nonnull final byte [] source,
                                final int off,
                                final int len,
                                final int options) throws IOException
  {
    // Lots of error checking and exception throwing
    ValueEnforcer.isArrayOfsLen (source, off, len);

    if (len == 0)
      return ArrayHelper.EMPTY_BYTE_ARRAY;

    ValueEnforcer.isTrue (len >= 4,
                          "Base64-encoded string must have at least four characters, but length specified was " + len);

    final byte [] aDecodabet = _getDecodabet (options);

    // Estimate on array size
    final int len34 = len * 3 / 4;
    // Upper limit on size of output
    final byte [] outBuff = new byte [len34];
    // Keep track of where we're writing
    int outBuffPosn = 0;

    // Four byte buffer from source, eliminating white space
    final byte [] b4 = new byte [4];
    // Keep track of four byte input buffer
    int b4Posn = 0;
    // Source array counter
    int i;
    // Special value from DECODABET
    byte sbiDecode;

    for (i = off; i < off + len; i++)
    {
      // Loop through source
      sbiDecode = aDecodabet[source[i] & 0xFF];

      // White space, Equals sign, or legit Base64 character
      // Note the values such as -5 and -9 in the
      // DECODABETs at the top of the file.
      if (sbiDecode >= WHITE_SPACE_ENC)
      {
        if (sbiDecode >= EQUALS_SIGN_ENC)
        {
          b4[b4Posn++] = source[i]; // Save non-whitespace
          if (b4Posn > 3)
          { // Time to decode?
            outBuffPosn += _decode4to3 (b4, 0, outBuff, outBuffPosn, options);
            b4Posn = 0;

            // If that was the equals sign, break out of 'for' loop
            if (source[i] == EQUALS_SIGN)
              break;
          }
        }
      }
      else
      {
        // There's a bad input character in the Base64 stream.
        throw new IOException ("Bad Base64 input character decimal " + (source[i] & 0xFF) + " in array position " + i);
      }
    }

    final byte [] out = new byte [outBuffPosn];
    System.arraycopy (outBuff, 0, out, 0, outBuffPosn);
    return out;
  }

  /**
   * Decodes data from Base64 notation, automatically detecting gzip-compressed
   * data and decompressing it.
   *
   * @param s
   *        the string to decode
   * @return the decoded data
   * @throws IOException
   *         If there is a problem
   * @since 1.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decode (@Nonnull final String s) throws IOException
  {
    return decode (s, NO_OPTIONS);
  }

  /**
   * Decodes data from Base64 notation, automatically detecting gzip-compressed
   * data and decompressing it.
   *
   * @param s
   *        the string to decode
   * @param options
   *        encode options such as URL_SAFE
   * @return the decoded data
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if <tt>s</tt> is null
   * @since 1.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decode (@Nonnull final String s, final int options) throws IOException
  {
    ValueEnforcer.notNull (s, "InputString");

    byte [] bytes = CharsetManager.getAsBytes (s, PREFERRED_ENCODING);

    // Decode
    bytes = decode (bytes, 0, bytes.length, options);

    // Check to see if it's gzip-compressed
    // GZIP Magic Two-Byte Number: 0x8b1f (35615)
    final boolean dontGunzip = (options & DONT_GUNZIP) != 0;
    if ((bytes != null) && (bytes.length >= 4) && (!dontGunzip))
    {
      final int head = (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
      if (GZIPInputStream.GZIP_MAGIC == head)
      {
        try (final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
            final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (bytes);
            final GZIPInputStream gzis = new GZIPInputStream (bais))
        {
          final byte [] buffer = new byte [2048];
          int length;
          while ((length = gzis.read (buffer)) >= 0)
          {
            baos.write (buffer, 0, length);
          }

          // No error? Get new bytes.
          bytes = baos.toByteArray ();
        }
        catch (final IOException e)
        {
          // Just return originally-decoded bytes
        }
      }
    }

    return bytes;
  }

  /**
   * Attempts to decode Base64 data and deserialize a Java Object within.
   * Returns <tt>null</tt> if there was an error.
   *
   * @param encodedObject
   *        The Base64 data to decode
   * @return The decoded and deserialized object
   * @throws NullPointerException
   *         if encodedObject is null
   * @throws IOException
   *         if there is a general error
   * @throws ClassNotFoundException
   *         if the decoded object is of a class that cannot be found by the JVM
   * @since 1.5
   */
  public static Object decodeToObject (@Nonnull final String encodedObject) throws IOException, ClassNotFoundException
  {
    return decodeToObject (encodedObject, NO_OPTIONS, null);
  }

  /**
   * Attempts to decode Base64 data and deserialize a Java Object within.
   * Returns <tt>null</tt> if there was an error. If <tt>loader</tt> is not
   * null, it will be the class loader used when deserializing.
   *
   * @param encodedObject
   *        The Base64 data to decode
   * @param options
   *        Various parameters related to decoding
   * @param loader
   *        Optional class loader to use in deserializing classes.
   * @return The decoded and deserialized object
   * @throws NullPointerException
   *         if encodedObject is null
   * @throws IOException
   *         if there is a general error
   * @throws ClassNotFoundException
   *         if the decoded object is of a class that cannot be found by the JVM
   * @since 2.3.4
   */
  public static Object decodeToObject (@Nonnull final String encodedObject,
                                       final int options,
                                       @Nullable final ClassLoader loader) throws IOException, ClassNotFoundException
  {
    // Decode and gunzip if necessary
    final byte [] objBytes = decode (encodedObject, options);

    ObjectInputStream ois = null;
    Object obj = null;

    try (final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (objBytes))
    {
      // If no custom class loader is provided, use Java's builtin OIS.
      if (loader == null)
      {
        ois = new ObjectInputStream (bais);
      }
      else
      {
        // Else make a customized object input stream that uses
        // the provided class loader.
        ois = new ObjectInputStream (bais)
        {
          @Override
          public Class <?> resolveClass (final ObjectStreamClass streamClass) throws IOException, ClassNotFoundException
          {
            final Class <?> c = Class.forName (streamClass.getName (), false, loader);
            if (c != null)
              return c;
            return super.resolveClass (streamClass);
          }
        };
      }

      obj = ois.readObject ();
    }
    finally
    {
      StreamHelper.close (ois);
    }

    return obj;
  }

  /**
   * Convenience method for encoding data to a file.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException.
   * <b>This is new to v2.3!</b> In earlier versions, it just returned false,
   * but in retrospect that's a pretty poor way to handle it.
   * </p>
   *
   * @param aDataToEncode
   *        byte array of data to encode in base64 form
   * @param sFilename
   *        Filename for saving encoded data
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if dataToEncode is null
   * @since 2.1
   */
  public static void encodeToFile (@Nonnull final byte [] aDataToEncode, final String sFilename) throws IOException
  {
    ValueEnforcer.notNull (aDataToEncode, "DataToEncode");

    try (final Base64OutputStream bos = new Base64OutputStream (FileHelper.getOutputStream (sFilename), ENCODE))
    {
      bos.write (aDataToEncode);
    }
  }

  /**
   * Convenience method for decoding data to a file.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException.
   * <b>This is new to v2.3!</b> In earlier versions, it just returned false,
   * but in retrospect that's a pretty poor way to handle it.
   * </p>
   *
   * @param dataToDecode
   *        Base64-encoded data as a string
   * @param filename
   *        Filename for saving decoded data
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  public static void decodeToFile (@Nonnull final String dataToDecode,
                                   @Nonnull final String filename) throws IOException
  {
    try (final Base64OutputStream bos = new Base64OutputStream (FileHelper.getOutputStream (filename), DECODE))
    {
      bos.write (CharsetManager.getAsBytes (dataToDecode, PREFERRED_ENCODING));
    }
  }

  /**
   * Convenience method for reading a base64-encoded file and decoding it.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException.
   * <b>This is new to v2.3!</b> In earlier versions, it just returned false,
   * but in retrospect that's a pretty poor way to handle it.
   * </p>
   *
   * @param filename
   *        Filename for reading encoded data
   * @return decoded byte array
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decodeFromFile (@Nonnull final String filename) throws IOException
  {
    // Set up some useful variables
    final File file = new File (filename);

    // Check for size of file
    if (file.length () > Integer.MAX_VALUE)
      throw new IOException ("File is too big for this convenience method (" + file.length () + " bytes).");

    final byte [] buffer = new byte [(int) file.length ()];

    // Open a stream
    try (
        final Base64InputStream bis = new Base64InputStream (StreamHelper.getBuffered (FileHelper.getInputStream (file)),
                                                             DECODE))
    {
      int nOfs = 0;
      int numBytes;

      // Read until done
      while ((numBytes = bis.read (buffer, nOfs, 4096)) >= 0)
      {
        nOfs += numBytes;
      }

      // Save in a variable to return
      final byte [] decodedData = new byte [nOfs];
      System.arraycopy (buffer, 0, decodedData, 0, nOfs);
      return decodedData;
    }
  }

  /**
   * Convenience method for reading a binary file and base64-encoding it.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException.
   * <b>This is new to v2.3!</b> In earlier versions, it just returned false,
   * but in retrospect that's a pretty poor way to handle it.
   * </p>
   *
   * @param filename
   *        Filename for reading binary data
   * @return base64-encoded string
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  @Nonnull
  public static String encodeFromFile (@Nonnull final String filename) throws IOException
  {
    // Set up some useful variables
    final File file = new File (filename);

    // Open a stream
    try (
        final Base64InputStream bis = new Base64InputStream (StreamHelper.getBuffered (FileHelper.getInputStream (file)),
                                                             ENCODE))
    {
      // Need max() for math on small files (v2.2.1);
      // Need +1 for a few corner cases (v2.3.5)
      final byte [] buffer = new byte [Math.max ((int) (file.length () * 1.4 + 1), 40)];

      int length = 0;
      int numBytes;

      // Read until done
      while ((numBytes = bis.read (buffer, length, 4096)) >= 0)
      {
        length += numBytes;
      }

      // Save in a variable to return
      final String encodedData = CharsetManager.getAsString (buffer, 0, length, PREFERRED_ENCODING);
      return encodedData;
    }
  }

  /**
   * Reads <tt>infile</tt> and encodes it to <tt>outfile</tt>.
   *
   * @param infile
   *        Input file
   * @param outfile
   *        Output file
   * @throws IOException
   *         if there is an error
   * @since 2.2
   */
  public static void encodeFileToFile (@Nonnull final String infile, @Nonnull final String outfile) throws IOException
  {
    final String encoded = encodeFromFile (infile);
    try (final OutputStream out = StreamHelper.getBuffered (FileHelper.getOutputStream (outfile)))
    {
      // Strict, 7-bit output.
      out.write (CharsetManager.getAsBytes (encoded, PREFERRED_ENCODING));
    }
  }

  /**
   * Reads <tt>infile</tt> and decodes it to <tt>outfile</tt>.
   *
   * @param infile
   *        Input file
   * @param outfile
   *        Output file
   * @throws IOException
   *         if there is an error
   * @since 2.2
   */
  public static void decodeFileToFile (@Nonnull final String infile, @Nonnull final String outfile) throws IOException
  {
    final byte [] decoded = decodeFromFile (infile);
    try (final OutputStream out = StreamHelper.getBuffered (FileHelper.getOutputStream (outfile)))
    {
      out.write (decoded);
    }
  }

  // ph methods

  /**
   * Decode the string with the default encoding (US-ASCII is the preferred
   * one).
   *
   * @param sEncoded
   *        The encoded string.
   * @return <code>null</code> if decoding failed.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] safeDecode (@Nullable final String sEncoded)
  {
    if (sEncoded != null)
      try
      {
        return decode (sEncoded);
      }
      catch (final Exception ex)
      {
        // fall through
      }
    return null;
  }

  /**
   * Decode the byte array.
   *
   * @param aEncodedBytes
   *        The encoded byte array.
   * @return <code>null</code> if decoding failed.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] safeDecode (@Nullable final byte [] aEncodedBytes)
  {
    if (aEncodedBytes != null)
      try
      {
        return decode (aEncodedBytes, DONT_GUNZIP);
      }
      catch (final Exception ex)
      {
        // fall through
      }
    return null;
  }

  /**
   * Decode the string and convert it back to a string.
   *
   * @param sEncoded
   *        The encoded string.
   * @param aCharset
   *        The character set to be used.
   * @return <code>null</code> if decoding failed.
   */
  @Nullable
  public static String safeDecodeAsString (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");
    if (sEncoded != null)
      try
      {
        final byte [] aDecoded = decode (sEncoded, DONT_GUNZIP);
        return CharsetManager.getAsString (aDecoded, aCharset);
      }
      catch (final Exception ex)
      {
        // Fall throuh
      }
    return null;
  }

  /**
   * Decode the byte array and convert it to a string.
   *
   * @param aEncodedBytes
   *        The encoded byte array.
   * @param aCharset
   *        The character set to be used.
   * @return <code>null</code> if decoding failed.
   */
  @Nullable
  public static String safeDecodeAsString (@Nullable final byte [] aEncodedBytes, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");
    if (aEncodedBytes != null)
      try
      {
        final byte [] aDecoded = decode (aEncodedBytes, DONT_GUNZIP);
        return CharsetManager.getAsString (aDecoded, aCharset);
      }
      catch (final Exception ex)
      {
        // Fall through
      }
    return null;
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] safeEncodeBytesToBytes (@Nullable final byte [] aDecoded)
  {
    if (aDecoded != null)
      return encodeBytesToBytes (aDecoded);
    return null;
  }

  /**
   * @param s
   *        The string to be encoded
   * @param aCharset
   *        The charset to be used
   * @return The encoded byte array.
   */
  @Nullable
  public static String safeEncode (@Nonnull final String s, @Nonnull final Charset aCharset)
  {
    final byte [] aDecoded = CharsetManager.getAsBytes (s, aCharset);
    return encodeBytes (aDecoded);
  }
}
