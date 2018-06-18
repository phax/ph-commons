/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.json.serialize;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.commons.state.EValidity;
import com.helger.json.IJson;
import com.helger.json.parser.IJsonParserCustomizeCallback;
import com.helger.json.parser.JsonParseException;
import com.helger.json.parser.JsonParser;
import com.helger.json.parser.errorhandler.IJsonParseExceptionCallback;
import com.helger.json.parser.errorhandler.LoggingJsonParseExceptionCallback;
import com.helger.json.parser.handler.CollectingJsonParserHandler;
import com.helger.json.parser.handler.DoNothingJsonParserHandler;
import com.helger.json.parser.handler.IJsonParserHandler;

/**
 * This is the central user class for reading and parsing Json from different
 * sources. This class reads full Json declarations only.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class JsonReader
{
  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private static final Logger s_aLogger = LoggerFactory.getLogger (JsonReader.class);
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();

  // Use the LoggingJsonParseExceptionHandler for maximum backward compatibility
  @GuardedBy ("s_aRWLock")
  private static IJsonParseExceptionCallback s_aDefaultParseExceptionCallback = new LoggingJsonParseExceptionCallback ();

  @PresentForCodeCoverage
  private static final JsonReader s_aInstance = new JsonReader ();

  private JsonReader ()
  {}

  /**
   * @return The default Json parse exception handler. May not be
   *         <code>null</code>. For backwards compatibility reasons this is be
   *         default an instance of {@link LoggingJsonParseExceptionCallback}.
   */
  @Nonnull
  public static IJsonParseExceptionCallback getDefaultParseExceptionCallback ()
  {
    return s_aRWLock.readLocked ( () -> s_aDefaultParseExceptionCallback);
  }

  /**
   * Set the default Json parse exception handler (for unrecoverable errors).
   *
   * @param aDefaultParseExceptionCallback
   *        The new default exception handler to be used. May not be
   *        <code>null</code>.
   */
  public static void setDefaultParseExceptionCallback (@Nonnull final IJsonParseExceptionCallback aDefaultParseExceptionCallback)
  {
    ValueEnforcer.notNull (aDefaultParseExceptionCallback, "DefaultParseExceptionCallback");

    s_aRWLock.writeLocked ( () -> s_aDefaultParseExceptionCallback = aDefaultParseExceptionCallback);
  }

  /**
   * Simple JSON parse method taking only the most basic parameters.
   *
   * @param aReader
   *        The reader to read from. Should be buffered. May not be
   *        <code>null</code>.
   * @param aParserHandler
   *        The parser handler. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess parseJson (@Nonnull @WillClose final Reader aReader,
                                    @Nonnull final IJsonParserHandler aParserHandler)
  {
    return parseJson (aReader, aParserHandler, (IJsonParserCustomizeCallback) null, (IJsonParseExceptionCallback) null);
  }

  /**
   * Generic JSON parse method. Usually this is not to be called manually - call
   * this only when you know what you are doing :)
   *
   * @param aReader
   *        The reader to read from. Should be buffered. May not be
   *        <code>null</code>.
   * @param aParserHandler
   *        The parser handler. May not be <code>null</code>.
   * @param aCustomizeCallback
   *        An optional {@link JsonParser} customization callback. May be
   *        <code>null</code>.
   * @param aCustomExceptionCallback
   *        A custom handler for unrecoverable errors. May be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess parseJson (@Nonnull @WillClose final Reader aReader,
                                    @Nonnull final IJsonParserHandler aParserHandler,
                                    @Nullable final IJsonParserCustomizeCallback aCustomizeCallback,
                                    @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    ValueEnforcer.notNull (aParserHandler, "ParserHandler");

    try
    {
      final JsonParser aParser = new JsonParser (aReader, aParserHandler);
      if (aCustomizeCallback != null)
        aCustomizeCallback.customizeJsonParser (aParser);
      aParser.parse ();
      return ESuccess.SUCCESS;
    }
    catch (final JsonParseException ex)
    {
      // Unrecoverable error
      if (aCustomExceptionCallback != null)
        aCustomExceptionCallback.onException (ex);
      else
        getDefaultParseExceptionCallback ().onException (ex);
      return ESuccess.FAILURE;
    }
    finally
    {
      StreamHelper.close (aReader);
    }
  }

  /**
   * Validate a JSON without building the tree in memory.
   *
   * @param aReader
   *        The reader to read from. Should be buffered. May not be
   *        <code>null</code>.
   * @return {@link EValidity#VALID} if the JSON is valid,
   *         {@link EValidity#INVALID} otherwise.
   */
  @Nonnull
  private static EValidity _validateJson (@Nonnull @WillClose final Reader aReader)
  {
    // Force silent parsing :)
    final ESuccess eSuccess = parseJson (aReader,
                                         new DoNothingJsonParserHandler (),
                                         (IJsonParserCustomizeCallback) null,
                                         ex -> {});
    return EValidity.valueOf (eSuccess.isSuccess ());
  }

  /**
   * Check if the passed File can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json file each time the method is called.
   * This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aFile
   *        The file to be parsed. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final File aFile)
  {
    return isValidJson (aFile, DEFAULT_CHARSET);
  }

  /**
   * Check if the passed File can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json file each time the method is called.
   * This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aFile
   *        The file to be parsed. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used for reading the Json file in case no BOM is
   *        present. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final File aFile, @Nonnull final Charset aFallbackCharset)
  {
    return isValidJson (new FileSystemResource (aFile), aFallbackCharset);
  }

  /**
   * Check if the passed Path can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json file each time the method is called.
   * This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aPath
   *        The file to be parsed. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final Path aPath)
  {
    return isValidJson (aPath, DEFAULT_CHARSET);
  }

  /**
   * Check if the passed Path can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json file each time the method is called.
   * This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aPath
   *        The file to be parsed. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used for reading the Json file in case no BOM is
   *        present. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final Path aPath, @Nonnull final Charset aFallbackCharset)
  {
    return isValidJson (new FileSystemResource (aPath), aFallbackCharset);
  }

  /**
   * Check if the passed input stream can be resembled to valid Json content.
   * This is accomplished by fully parsing the Json file each time the method is
   * called. This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aISP
   *        The resource to be parsed. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final IHasInputStream aISP)
  {
    return isValidJson (aISP, DEFAULT_CHARSET);
  }

  /**
   * Check if the passed input stream can be resembled to valid Json content.
   * This is accomplished by fully parsing the Json file each time the method is
   * called. This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aISP
   *        The resource to be parsed. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used for reading the Json file in case no BOM is
   *        present. May not be <code>null</code>.
   * @return <code>true</code> if the file can be parsed without error,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final IHasInputStream aISP, @Nonnull final Charset aFallbackCharset)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");
    ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

    final InputStream aIS = aISP.getInputStream ();
    if (aIS == null)
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to open Json InputStream from " + aISP);
      return false;
    }
    return isValidJson (aIS, aFallbackCharset);
  }

  /**
   * Check if the passed input stream can be resembled to valid Json content.
   * This is accomplished by fully parsing the Json file each time the method is
   * called using the default charset. This consumes <b>less memory</b> than
   * calling any of the <code>read...</code> methods and checking for a non-
   * <code>null</code> result.
   *
   * @param aIS
   *        The input stream to use. Is automatically closed. May not be
   *        <code>null</code>.
   * @return <code>true</code> if the Json is valid according to the version,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull @WillClose final InputStream aIS)
  {
    return isValidJson (aIS, DEFAULT_CHARSET);
  }

  /**
   * Check if the passed input stream can be resembled to valid Json content.
   * This is accomplished by fully parsing the Json file each time the method is
   * called. This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param aIS
   *        The input stream to use. Is automatically closed. May not be
   *        <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no BOM is present. May not be
   *        <code>null</code>.
   * @return <code>true</code> if the Json is valid according to the version,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull @WillClose final InputStream aIS, @Nonnull final Charset aFallbackCharset)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

    try
    {
      final Reader aReader = CharsetHelper.getReaderByBOM (aIS, aFallbackCharset);
      return isValidJson (aReader);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  /**
   * Check if the passed String can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json file each time the method is called.
   * This consumes <b>less memory</b> than calling any of the
   * <code>read...</code> methods and checking for a non-<code>null</code>
   * result.
   *
   * @param sJson
   *        The Json string to scan. May not be <code>null</code>.
   * @return <code>true</code> if the Json is valid according to the version,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull final String sJson)
  {
    ValueEnforcer.notNull (sJson, "Json");

    return isValidJson (new NonBlockingStringReader (sJson));
  }

  /**
   * Check if the passed reader can be resembled to valid Json content. This is
   * accomplished by fully parsing the Json each time the method is called. This
   * consumes <b>less memory</b> than calling any of the <code>read...</code>
   * methods and checking for a non-<code>null</code> result.
   *
   * @param aReader
   *        The reader to use. May not be <code>null</code>.
   * @return <code>true</code> if the Json is valid according to the version,
   *         <code>false</code> if not
   */
  public static boolean isValidJson (@Nonnull @WillClose final Reader aReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");

    return _validateJson (StreamHelper.getBuffered (aReader)).isValid ();
  }

  /**
   * Main reading of the JSON
   *
   * @param aReader
   *        The reader to read from. Should be buffered. May not be
   *        <code>null</code>.
   * @param aCustomExceptionCallback
   *        A custom handler for unrecoverable errors. May be <code>null</code>.
   * @return <code>null</code> if parsing failed with an unrecoverable error
   *         (and no throwing exception handler is used), or <code>null</code>
   *         if a recoverable error occurred or non-<code>null</code> if parsing
   *         succeeded.
   */
  @Nullable
  private static IJson _readJson (@Nonnull @WillClose final Reader aReader,
                                  @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
    if (parseJson (aReader, aHandler, (IJsonParserCustomizeCallback) null, aCustomExceptionCallback).isFailure ())
      return null;
    return aHandler.getJson ();
  }

  /**
   * Read the Json from the passed String using a character stream. An
   * eventually contained <code>@charset</code> rule is ignored.
   *
   * @param sJson
   *        The source string containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromString (@Nonnull final String sJson)
  {
    return readFromReader (new NonBlockingStringReader (sJson), (IJsonParseExceptionCallback) null);
  }

  /**
   * Read the Json from the passed String using a character stream.
   *
   * @param sJson
   *        The source string containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @param aCustomExceptionHandler
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromString (@Nonnull final String sJson,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return readFromReader (new NonBlockingStringReader (sJson), aCustomExceptionHandler);
  }

  /**
   * Read the Json from the passed File using the default charset.
   *
   * @param aFile
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromFile (@Nonnull final File aFile)
  {
    return readFromFile (aFile, DEFAULT_CHARSET);
  }

  /**
   * Read the Json from the passed File.
   *
   * @param aFile
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no is BOM is present. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromFile (@Nonnull final File aFile, @Nonnull final Charset aFallbackCharset)
  {
    return readFromFile (aFile, aFallbackCharset, null);
  }

  /**
   * Read the Json from the passed File.
   *
   * @param aFile
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no BOM is present. May not be
   *        <code>null</code>.
   * @param aCustomExceptionHandler
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromFile (@Nonnull final File aFile,
                                    @Nonnull final Charset aFallbackCharset,
                                    @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return readFromStream (new FileSystemResource (aFile), aFallbackCharset, aCustomExceptionHandler);
  }

  /**
   * Read the Json from the passed Path using the default charset.
   *
   * @param aPath
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromPath (@Nonnull final Path aPath)
  {
    return readFromPath (aPath, DEFAULT_CHARSET);
  }

  /**
   * Read the Json from the passed Path.
   *
   * @param aPath
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no is BOM is present. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromPath (@Nonnull final Path aPath, @Nonnull final Charset aFallbackCharset)
  {
    return readFromPath (aPath, aFallbackCharset, null);
  }

  /**
   * Read the Json from the passed Path.
   *
   * @param aPath
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no BOM is present. May not be
   *        <code>null</code>.
   * @param aCustomExceptionHandler
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromPath (@Nonnull final Path aPath,
                                    @Nonnull final Charset aFallbackCharset,
                                    @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return readFromStream (new FileSystemResource (aPath), aFallbackCharset, aCustomExceptionHandler);
  }

  /**
   * Read the Json from the passed {@link IHasInputStream} using the default
   * charset.
   *
   * @param aISP
   *        The input stream provider to use. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP)
  {
    return readFromStream (aISP, DEFAULT_CHARSET);
  }

  /**
   * Read the Json from the passed {@link IHasInputStream}.
   *
   * @param aISP
   *        The input stream provider to use. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used if no BOM is present. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP, @Nonnull final Charset aFallbackCharset)
  {
    return readFromStream (aISP, aFallbackCharset, null);
  }

  /**
   * Read the Json from the passed {@link IHasInputStream}.
   *
   * @param aISP
   *        The input stream to use. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no BOM is present. May not be
   *        <code>null</code>.
   * @param aCustomExceptionHandler
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP,
                                      @Nonnull final Charset aFallbackCharset,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");
    final InputStream aIS = aISP.getInputStream ();
    if (aIS == null)
      return null;
    return readFromStream (aIS, aFallbackCharset, aCustomExceptionHandler);
  }

  /**
   * Read the Json from the passed {@link InputStream} using the default
   * charset.
   *
   * @param aIS
   *        The input stream to use. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final InputStream aIS)
  {
    return readFromStream (aIS, DEFAULT_CHARSET);
  }

  /**
   * Read the Json from the passed {@link InputStream}.
   *
   * @param aIS
   *        The input stream to use. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used if no BOM is present. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final InputStream aIS, @Nonnull final Charset aFallbackCharset)
  {
    return readFromStream (aIS, aFallbackCharset, null);
  }

  /**
   * Read the Json from the passed {@link InputStream}.
   *
   * @param aIS
   *        The input stream to use. May not be <code>null</code>.
   * @param aFallbackCharset
   *        The charset to be used in case no BOM is present. May not be
   *        <code>null</code>.
   * @param aCustomExceptionCallback
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromStream (@Nonnull final InputStream aIS,
                                      @Nonnull final Charset aFallbackCharset,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

    try
    {
      final Reader aReader = CharsetHelper.getReaderByBOM (aIS, aFallbackCharset);
      return _readJson (aReader, aCustomExceptionCallback);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  /**
   * Read the Json from the passed {@link Reader}.
   *
   * @param aReader
   *        The reader to use. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromReader (@Nonnull final Reader aReader)
  {
    return readFromReader (aReader, null);
  }

  /**
   * Read the Json from the passed {@link Reader}.
   *
   * @param aReader
   *        The reader to use. May not be <code>null</code>.
   * @param aCustomExceptionCallback
   *        An optional custom exception handler that can be used to collect the
   *        unrecoverable parsing errors. May be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   */
  @Nullable
  public static IJson readFromReader (@Nonnull final Reader aReader,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    ValueEnforcer.notNull (aReader, "Reader");

    // No charset determination, as the Reader already has an implicit Charset

    return _readJson (StreamHelper.getBuffered (aReader), aCustomExceptionCallback);
  }
}
