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
package com.helger.json.valueserializer;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.json.CJson;

/**
 * A {@link IJsonValueSerializer} implementation that uses a constant value.
 * Required for true, false and null.
 *
 * @author Philip Helger
 */
public final class JsonValueSerializerConstant implements IJsonValueSerializer
{
  /** Special value for "true" */
  public static final JsonValueSerializerConstant TRUE = new JsonValueSerializerConstant (CJson.KEYWORD_TRUE);
  /** Special value for "false" */
  public static final JsonValueSerializerConstant FALSE = new JsonValueSerializerConstant (CJson.KEYWORD_FALSE);
  /** Special value for "null" */
  public static final JsonValueSerializerConstant NULL = new JsonValueSerializerConstant (CJson.KEYWORD_NULL);

  private final String m_sValue;

  public JsonValueSerializerConstant (@Nonnull @Nonempty final String sValue)
  {
    ValueEnforcer.notEmpty (sValue, "Value");
    m_sValue = sValue;
  }

  @Nonnull
  @Nonempty
  public String getValue ()
  {
    return m_sValue;
  }

  public void appendAsJsonString (@Nullable final Object aValue,
                                  @Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    aWriter.write (m_sValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JsonValueSerializerConstant rhs = (JsonValueSerializerConstant) o;
    return m_sValue.equals (rhs.m_sValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_sValue).getToString ();
  }
}
