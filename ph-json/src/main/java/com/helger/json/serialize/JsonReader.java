/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.NonClosingReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.commons.state.EValidity;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;
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

  private static final Logger LOGGER = LoggerFactory.getLogger (JsonReader.class);
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
  public static boolean isValidJson (@Nonnull final IHasInputStream aISP, @Nonnull final Charset aFallbackCharset)
  {
    ValueEnforcer.notNull (aISP, "InputStreamProvider");
    ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

    final InputStream aIS = aISP.getInputStream ();
    if (aIS == null)
    {
      if (LOGGER.isWarnEnabled ())
        LOGGER.warn ("Failed to open Json InputStream from " + aISP);
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Deprecated
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
   * @param aCustomizeCallback
   *        An optional {@link JsonParser} customization callback. May be
   *        <code>null</code>.
   * @param aCustomExceptionCallback
   *        A custom handler for unrecoverable errors. May be <code>null</code>.
   * @return <code>null</code> if parsing failed with an unrecoverable error
   *         (and no throwing exception handler is used), or <code>null</code>
   *         if a recoverable error occurred or non-<code>null</code> if parsing
   *         succeeded.
   * @since 9.1.8
   */
  @Nullable
  public static IJson readJson (@Nonnull @WillClose final Reader aReader,
                                @Nullable final IJsonParserCustomizeCallback aCustomizeCallback,
                                @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
    if (parseJson (aReader, aHandler, aCustomizeCallback, aCustomExceptionCallback).isFailure ())
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
    return builder ().setSource (sJson).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromString (@Nonnull final String sJson,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return builder ().setSource (sJson).setCustomExceptionCallback (aCustomExceptionHandler).read ();
  }

  /**
   * Read the Json from the passed File using the default charset.
   *
   * @param aFile
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromFile (@Nonnull final File aFile)
  {
    return builder ().setSource (aFile).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromFile (@Nonnull final File aFile, @Nonnull final Charset aFallbackCharset)
  {
    return builder ().setSource (aFile, aFallbackCharset).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromFile (@Nonnull final File aFile,
                                    @Nonnull final Charset aFallbackCharset,
                                    @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return builder ().setSource (aFile, aFallbackCharset).setCustomExceptionCallback (aCustomExceptionHandler).read ();
  }

  /**
   * Read the Json from the passed Path using the default charset.
   *
   * @param aPath
   *        The file containing the Json to be parsed. May not be
   *        <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromPath (@Nonnull final Path aPath)
  {
    return builder ().setSource (aPath).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromPath (@Nonnull final Path aPath, @Nonnull final Charset aFallbackCharset)
  {
    return builder ().setSource (aPath, aFallbackCharset).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromPath (@Nonnull final Path aPath,
                                    @Nonnull final Charset aFallbackCharset,
                                    @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return builder ().setSource (aPath, aFallbackCharset).setCustomExceptionCallback (aCustomExceptionHandler).read ();
  }

  /**
   * Read the Json from the passed {@link IHasInputStream} using the default
   * charset.
   *
   * @param aISP
   *        The input stream provider to use. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP)
  {
    return builder ().setSource (aISP).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP, @Nonnull final Charset aFallbackCharset)
  {
    return builder ().setSource (aISP, aFallbackCharset).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final IHasInputStream aISP,
                                      @Nonnull final Charset aFallbackCharset,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionHandler)
  {
    return builder ().setSource (aISP, aFallbackCharset).setCustomExceptionCallback (aCustomExceptionHandler).read ();
  }

  /**
   * Read the Json from the passed {@link InputStream} using the default
   * charset.
   *
   * @param aIS
   *        The input stream to use. May not be <code>null</code>.
   * @return <code>null</code> if reading failed, the Json declarations
   *         otherwise.
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final InputStream aIS)
  {
    return builder ().setSource (aIS).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final InputStream aIS, @Nonnull final Charset aFallbackCharset)
  {
    return builder ().setSource (aIS, aFallbackCharset).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromStream (@Nonnull final InputStream aIS,
                                      @Nonnull final Charset aFallbackCharset,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    try
    {
      return builder ().setSource (aIS, aFallbackCharset).setCustomExceptionCallback (aCustomExceptionCallback).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromReader (@Nonnull final Reader aReader)
  {
    return builder ().setSource (aReader).read ();
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
   * @deprecated Since v9.3.3 - use {@link #builder()} instead.
   */
  @Nullable
  @Deprecated
  public static IJson readFromReader (@Nonnull final Reader aReader,
                                      @Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
  {
    return builder ().setSource (aReader).setCustomExceptionCallback (aCustomExceptionCallback).read ();
  }

  /**
   * @return Create a new {@link Builder} instance.
   * @since 9.3.3
   */
  @Nonnull
  public static Builder builder ()
  {
    return new Builder ();
  }

  /**
   * @return Create a new {@link Builder} instance that is configured to read
   *         multiple instances. Never <code>null</code>. Use
   *         {@link Builder#setSource(Reader)} to ensure this works. When using
   *         {@link InputStream} or the like, there is too much pre read.
   * @since 9.3.8
   */
  @Nonnull
  public static Builder builderMultiObject ()
  {
    return builder ().setDontCloseSource (true)
                     .setUseBufferedReader (false)
                     .setCustomizeCallback (p -> p.setCheckForEOI (false));
  }

  /**
   * Factory for JSon reader for different sources. Use {@link #isValidJson()}
   * to check if the JSON is syntactically correct and {@link #read()} to
   * convert it to a parsed object.
   *
   * @author Philip Helger
   * @since 9.3.3
   */
  public static class Builder implements AutoCloseable
  {
    private boolean m_bDontCloseSource = false;
    private boolean m_bUseBufferedReader = true;
    private Reader m_aReader;
    private IJsonParserCustomizeCallback m_aCustomizeCallback;
    private IJsonParseExceptionCallback m_aCustomeExceptionCallback;

    public Builder ()
    {}

    public void close ()
    {
      StreamHelper.close (m_aReader);
    }

    /**
     * Set avoid closing the source stream. This may be helpful when reading
     * multiple objects. Default is <code>false</code>.
     *
     * @param bDontCloseSource
     *        <code>true</code> to not close the source
     * @return this for chaining
     * @since 9.3.8
     */
    @Nonnull
    public Builder setDontCloseSource (final boolean bDontCloseSource)
    {
      m_bDontCloseSource = bDontCloseSource;
      return this;
    }

    /**
     * Use a buffered reader or not. If you want to read multiple instances from
     * a simple {@link InputStream} this should be set to <code>false</code>.
     * Default is <code>true</code>.
     *
     * @param bUseBufferedReader
     *        <code>true</code> to use it, <code>false</code> to use a
     *        non-buffered reader.
     * @return this for chaining
     * @since 9.3.8
     */
    @Nonnull
    public Builder setUseBufferedReader (final boolean bUseBufferedReader)
    {
      m_bUseBufferedReader = bUseBufferedReader;
      return this;
    }

    /**
     * Use a constant JSON string as source
     *
     * @param sJson
     *        The JSON String to be parser. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final String sJson)
    {
      ValueEnforcer.notNull (sJson, "Json");

      return setSource (new NonBlockingStringReader (sJson));
    }

    /**
     * Use a {@link File} as JSON source. Assumes UTF-8 as fallback charset.
     *
     * @param aFile
     *        The File containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final File aFile)
    {
      return setSource (aFile, JsonReader.DEFAULT_CHARSET);
    }

    /**
     * Use a {@link File} as JSON source with a custom fallback charset.
     *
     * @param aFile
     *        The File containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @param aFallbackCharset
     *        The fallback charset to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final File aFile, @Nonnull final Charset aFallbackCharset)
    {
      ValueEnforcer.notNull (aFile, "File");
      ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

      return setSource (new FileSystemResource (aFile), aFallbackCharset);
    }

    /**
     * Use a {@link Path} as JSON source. Assumes UTF-8 as fallback charset.
     *
     * @param aPath
     *        The File containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final Path aPath)
    {
      return setSource (aPath, JsonReader.DEFAULT_CHARSET);
    }

    /**
     * Use a {@link Path} as JSON source with a custom fallback charset.
     *
     * @param aPath
     *        The File containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @param aFallbackCharset
     *        The fallback charset to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final Path aPath, @Nonnull final Charset aFallbackCharset)
    {
      ValueEnforcer.notNull (aPath, "Path");
      ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

      return setSource (new FileSystemResource (aPath), aFallbackCharset);
    }

    /**
     * Use a byte array as JSON source. Assumes UTF-8 as fallback charset.
     *
     * @param aBytes
     *        The byte array containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @return this for chaining
     * @since 9.3.4
     */
    @Nonnull
    public Builder setSource (@Nonnull final byte [] aBytes)
    {
      return setSource (aBytes, JsonReader.DEFAULT_CHARSET);
    }

    /**
     * Use a byte array as JSON source with a custom fallback charset.
     *
     * @param aBytes
     *        The byte array containing the JSON to be parsed. May not be
     *        <code>null</code>.
     * @param aFallbackCharset
     *        The fallback charset to be used. May not be <code>null</code>.
     * @return this for chaining
     * @since 9.3.4
     */
    @Nonnull
    public Builder setSource (@Nonnull final byte [] aBytes, @Nonnull final Charset aFallbackCharset)
    {
      ValueEnforcer.notNull (aBytes, "Bytes");
      ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

      return setSource (new NonBlockingByteArrayInputStream (aBytes), aFallbackCharset);
    }

    /**
     * Use an InputStream provider as JSON source. Assumes UTF-8 as fallback
     * charset.
     *
     * @param aISP
     *        The InputStream provider to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final IHasInputStream aISP)
    {
      return setSource (aISP, JsonReader.DEFAULT_CHARSET);
    }

    /**
     * Use an InputStream provider as JSON source with a custom fallback
     * charset.
     *
     * @param aISP
     *        The InputStream provider to be used. May not be <code>null</code>.
     * @param aFallbackCharset
     *        The fallback charset to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull final IHasInputStream aISP, @Nonnull final Charset aFallbackCharset)
    {
      ValueEnforcer.notNull (aISP, "InputStreamProvider");
      ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

      final InputStream aIS = aISP.getInputStream ();
      if (aIS != null)
        setSource (aIS, aFallbackCharset);
      return this;
    }

    /**
     * Use an {@link InputStream} as JSON source. Assumes UTF-8 as fallback
     * charset.
     *
     * @param aIS
     *        The InputStream to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull @WillClose final InputStream aIS)
    {
      return setSource (aIS, JsonReader.DEFAULT_CHARSET);
    }

    /**
     * Use an {@link InputStream} as JSON source with a custom fallback charset.
     *
     * @param aIS
     *        The InputStream to be used. May not be <code>null</code>.
     * @param aFallbackCharset
     *        The fallback charset to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull @WillClose final InputStream aIS, @Nonnull final Charset aFallbackCharset)
    {
      ValueEnforcer.notNull (aIS, "InputStream");
      ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

      final Reader aReader = CharsetHelper.getReaderByBOM (aIS, aFallbackCharset);
      if (aReader != null)
        return setSource (aReader);

      return this;
    }

    /**
     * Set a {@link Reader} as JSON source. Internally it is ensured, that it is
     * buffered.
     *
     * @param aReader
     *        The Reader to be used. May not be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setSource (@Nonnull @WillClose final Reader aReader)
    {
      ValueEnforcer.notNull (aReader, "Reader");
      if (m_aReader != null)
        LOGGER.warn ("Another source is already present - this may cause a resource leak, because the old source is not closed automatically");

      m_aReader = aReader;

      // Use buffered?
      if (m_bUseBufferedReader)
        m_aReader = StreamHelper.getBuffered (m_aReader);

      // Don't close?
      if (m_bDontCloseSource)
        m_aReader = new NonClosingReader (m_aReader);
      return this;
    }

    /**
     * Set the optional customizing callback.
     *
     * @param aCustomizeCallback
     *        The customizing callback to be used. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setCustomizeCallback (@Nullable final IJsonParserCustomizeCallback aCustomizeCallback)
    {
      m_aCustomizeCallback = aCustomizeCallback;
      return this;
    }

    /**
     * Set the optional exception callback.
     *
     * @param aCustomExceptionCallback
     *        The exception callback to be used. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public Builder setCustomExceptionCallback (@Nullable final IJsonParseExceptionCallback aCustomExceptionCallback)
    {
      m_aCustomeExceptionCallback = aCustomExceptionCallback;
      return this;
    }

    /**
     * @return <code>true</code> if a source is present, <code>false</code> if
     *         not.
     * @since 9.3.10
     */
    public boolean hasSource ()
    {
      return m_aReader != null;
    }

    /**
     * Check if the provided source is syntactically correct JSON or not,
     * without building an object structure.
     *
     * @return <code>true</code> if it is valid JSON, <code>false</code> if not
     */
    public boolean isValidJson ()
    {
      if (m_aReader == null)
        throw new IllegalStateException ("No source is set.");
      return JsonReader.parseJson (m_aReader,
                                   new DoNothingJsonParserHandler (),
                                   m_aCustomizeCallback,
                                   m_aCustomeExceptionCallback)
                       .isSuccess ();
    }

    /**
     * @return The parsed JSON from the specified source. May be
     *         <code>null</code> if parsing fails. This method should be invoked
     *         only once per instance, because the underlying reader is not
     *         guaranteed to be re-openable.
     */
    @Nullable
    public IJson read ()
    {
      if (m_aReader == null)
        throw new IllegalStateException ("No source is set.");
      return JsonReader.readJson (m_aReader, m_aCustomizeCallback, m_aCustomeExceptionCallback);
    }

    /**
     * @return The parsed JSON array from the specified source. May be
     *         <code>null</code> if parsing fails or if the parsed JSON is not
     *         an array. This method should be invoked only once per instance,
     *         because the underlying reader is not guaranteed to be
     *         re-openable.
     * @since 9.3.5
     */
    @Nullable
    public IJsonArray readAsArray ()
    {
      final IJson aJson = read ();
      return aJson == null ? null : aJson.getAsArray ();
    }

    /**
     * @return The parsed JSON array from the specified source. May be
     *         <code>null</code> if parsing fails or if the parsed JSON is not
     *         an object. This method should be invoked only once per instance,
     *         because the underlying reader is not guaranteed to be
     *         re-openable.
     * @since 9.3.5
     */
    @Nullable
    public IJsonObject readAsObject ()
    {
      final IJson aJson = read ();
      return aJson == null ? null : aJson.getAsObject ();
    }

    /**
     * @return The parsed JSON array from the specified source. May be
     *         <code>null</code> if parsing fails or if the parsed JSON is not a
     *         value. This method should be invoked only once per instance,
     *         because the underlying reader is not guaranteed to be
     *         re-openable.
     * @since 9.3.5
     */
    @Nullable
    public IJsonValue readAsValue ()
    {
      final IJson aJson = read ();
      return aJson == null ? null : aJson.getAsValue ();
    }
  }
}
