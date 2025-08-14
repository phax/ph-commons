/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.traits;

import jakarta.annotation.Nullable;

/**
 * A generic convert Object to anything with convenience API.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IGetterDirectExtTrait extends IGetterDirectTrait
{
  /**
   * @return <code>getConvertedValue (null, Blob.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob ()
  {
    return getConvertedValue (null, java.sql.Blob.class);
  }

  /**
   * @return <code>getConvertedValue (null, Clob.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob ()
  {
    return getConvertedValue (null, java.sql.Clob.class);
  }

  /**
   * @return <code>getConvertedValue (null, Date.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate ()
  {
    return getConvertedValue (null, java.sql.Date.class);
  }

  /**
   * @return <code>getConvertedValue (null, NClob.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob ()
  {
    return getConvertedValue (null, java.sql.NClob.class);
  }

  /**
   * @return <code>getConvertedValue (null, RowId.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId ()
  {
    return getConvertedValue (null, java.sql.RowId.class);
  }

  /**
   * @return <code>getConvertedValue (null, Time.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime ()
  {
    return getConvertedValue (null, java.sql.Time.class);
  }

  /**
   * @return <code>getConvertedValue (null, Timestamp.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp ()
  {
    return getConvertedValue (null, java.sql.Timestamp.class);
  }
}
