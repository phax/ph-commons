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
package com.helger.json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.json.serialize.JsonReader;
import com.helger.json.valueserializer.IJsonValueSerializer;
import com.helger.json.valueserializer.JsonValueSerializerConstant;
import com.helger.json.valueserializer.JsonValueSerializerEscaped;
import com.helger.json.valueserializer.JsonValueSerializerRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Default implementation of {@link IJsonValue}.
 *
 * @author Philip Helger
 */
@Immutable
public class JsonValue implements IJsonValue
{
  /** Special value for "null" */
  public static final JsonValue NULL = new JsonValue (null);
  /** Special value for "true" */
  public static final JsonValue TRUE = new JsonValue (Boolean.TRUE);
  /** Special value for "false" */
  public static final JsonValue FALSE = new JsonValue (Boolean.FALSE);

  /** Cache for regular used numeric JSON values */
  private static final int INT_CACHE_MIN = -128;
  private static final int INT_CACHE_MAX = 127;
  private static final JsonValue [] NUMERIC = new JsonValue [INT_CACHE_MAX - INT_CACHE_MIN + 1];

  static
  {
    for (int i = INT_CACHE_MIN; i <= INT_CACHE_MAX; ++i)
      NUMERIC[i - INT_CACHE_MIN] = new JsonValue (Integer.valueOf (i));
  }

  private Object m_aValue;

  protected JsonValue (@Nullable final Object aValue)
  {
    m_aValue = aValue;
  }

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ();
    appendAsJsonString (aWriter);
    StreamHelper.writeSafeUTF (aOOS, aWriter.getAsString ());
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException
  {
    final String sJson = StreamHelper.readSafeUTF (aOIS);
    final JsonValue aJson = (JsonValue) JsonReader.readFromString (sJson);
    m_aValue = aJson.m_aValue;
  }

  public final boolean isArray ()
  {
    return false;
  }

  public final boolean isObject ()
  {
    return false;
  }

  public final boolean isValue ()
  {
    return true;
  }

  @Nullable
  public Object getValue ()
  {
    return m_aValue;
  }

  public boolean isBooleanValue ()
  {
    return this == TRUE || this == FALSE;
  }

  public boolean isIntValue ()
  {
    return m_aValue instanceof BigInteger || m_aValue instanceof Integer || m_aValue instanceof Long;
  }

  public boolean isDecimalValue ()
  {
    return m_aValue instanceof BigDecimal || m_aValue instanceof Double;
  }

  public boolean isStringValue ()
  {
    return m_aValue instanceof String;
  }

  /**
   * @return The default {@link IJsonValueSerializer} to be used if none is
   *         registered. This is the "as string" serializer.
   */
  @Nonnull
  public static IJsonValueSerializer getDefaultJsonValueSerializer ()
  {
    return JsonValueSerializerEscaped.getInstance ();
  }

  @Nonnull
  public IJsonValueSerializer getValueSerializer ()
  {
    if (this == NULL)
      return JsonValueSerializerConstant.NULL;
    if (this == TRUE)
      return JsonValueSerializerConstant.TRUE;
    if (this == FALSE)
      return JsonValueSerializerConstant.FALSE;
    IJsonValueSerializer ret = JsonValueSerializerRegistry.getInstance ().getJsonValueSerializer (getValueClass ());
    if (ret == null)
    {
      // Default: escaped
      ret = getDefaultJsonValueSerializer ();
    }
    return ret;
  }

  public void appendAsJsonString (@Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    getValueSerializer ().appendAsJsonString (m_aValue, aWriter);
  }

  @Nonnull
  public JsonValue getClone ()
  {
    // No need to clone, as this object is immutable!
    return this;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JsonValue rhs = (JsonValue) o;
    return EqualsHelper.equals (m_aValue, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Value", m_aValue)
                                       .append ("ValueClass", ClassHelper.getClassLocalName (m_aValue))
                                       .getToString ();
  }

  @Nonnull
  public static JsonValue create (final boolean bValue)
  {
    return bValue ? TRUE : FALSE;
  }

  @Nonnull
  public static JsonValue create (final byte nValue)
  {
    return create ((int) nValue);
  }

  @Nonnull
  public static JsonValue create (final char cValue)
  {
    return create (Character.toString (cValue));
  }

  @Nonnull
  public static JsonValue create (final double dValue)
  {
    return create (Double.valueOf (dValue));
  }

  @Nonnull
  public static JsonValue create (final float fValue)
  {
    return create (Double.valueOf (fValue));
  }

  @Nonnull
  public static JsonValue create (final int nValue)
  {
    // Use cached value
    if (nValue >= INT_CACHE_MIN && nValue < INT_CACHE_MAX)
      return NUMERIC[nValue - INT_CACHE_MIN];

    return create (Integer.valueOf (nValue));
  }

  @Nonnull
  public static JsonValue create (final long nValue)
  {
    // Use cached value
    if (nValue >= INT_CACHE_MIN && nValue < INT_CACHE_MAX)
      return NUMERIC[(int) nValue - INT_CACHE_MIN];

    return create (Long.valueOf (nValue));
  }

  @Nonnull
  public static JsonValue create (final short nValue)
  {
    return create ((int) nValue);
  }

  @Nonnull
  @SuppressFBWarnings ("RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN")
  public static JsonValue create (@Nullable final Object aValue)
  {
    // Special null constant
    if (aValue == null)
      return NULL;
    // Special true/false
    if (aValue == Boolean.TRUE)
      return TRUE;
    if (aValue == Boolean.FALSE)
      return FALSE;

    // Change to Integer/Double
    if (aValue instanceof Byte)
      return create (((Byte) aValue).byteValue ());
    if (aValue instanceof Short)
      return create (((Short) aValue).shortValue ());
    if (aValue instanceof Float)
      return create (new BigDecimal (aValue.toString ()).doubleValue ());

    // New object
    return new JsonValue (aValue);
  }
}
