/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.attr;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Abstract base class for all kind of string-object mapping container. This
 * implementation provides a default implementation for all things that can be
 * independently implemented from the underlying data structure.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public abstract class AbstractReadOnlyAttributeContainer <KEYTYPE, VALUETYPE> implements IAttributeContainer <KEYTYPE, VALUETYPE>
{
  @Nullable
  public final <DATATYPE> DATATYPE getCastedAttribute (@Nullable final KEYTYPE aName)
  {
    return GenericReflection.<Object, DATATYPE> uncheckedCast (getAttributeObject (aName));
  }

  @Nullable
  public final <DATATYPE> DATATYPE getCastedAttribute (@Nullable final KEYTYPE aName, @Nullable final DATATYPE aDefault)
  {
    final DATATYPE aValue = this.<DATATYPE> getCastedAttribute (aName);
    return aValue == null ? aDefault : aValue;
  }

  @Nullable
  public final <DATATYPE> DATATYPE getTypedAttribute (@Nullable final KEYTYPE aName, @Nonnull final Class <DATATYPE> aDstClass)
  {
    return TypeConverter.convertIfNecessary (getAttributeObject (aName), aDstClass);
  }

  @Nullable
  public final <DATATYPE> DATATYPE getTypedAttribute (@Nullable final KEYTYPE aName,
                                                      @Nonnull final Class <DATATYPE> aDstClass,
                                                      @Nullable final DATATYPE aDefault)
  {
    final DATATYPE aValue = this.<DATATYPE> getTypedAttribute (aName, aDstClass);
    return aValue == null ? aDefault : aValue;
  }

  @Nullable
  public final String getAttributeAsString (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsString (aName, null);
  }

  public final String getAttributeAsString (@Nullable final KEYTYPE aName, @Nullable final String sDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsString (aName, aValue, sDefault);
  }

  public final int getAttributeAsInt (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsInt (aName, CGlobal.ILLEGAL_UINT);
  }

  public final int getAttributeAsInt (@Nullable final KEYTYPE aName, final int nDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsInt (aName, aValue, nDefault);
  }

  public final long getAttributeAsLong (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsLong (aName, CGlobal.ILLEGAL_ULONG);
  }

  public final long getAttributeAsLong (@Nullable final KEYTYPE aName, final long nDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsLong (aName, aValue, nDefault);
  }


  public final double getAttributeAsDouble (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsDouble (aName, CGlobal.ILLEGAL_UINT);
  }

  public final double getAttributeAsDouble (@Nullable final KEYTYPE aName, final double dDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsDouble (aName, aValue, dDefault);
  }

  public final boolean getAttributeAsBoolean (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBoolean (aName, false);
  }

  public final boolean getAttributeAsBoolean (@Nullable final KEYTYPE aName, final boolean bDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsBoolean (aName, aValue, bDefault);
  }

  public final BigInteger getAttributeAsBigInteger (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBigInteger (aName, null);
  }

  @Nullable
  public final BigInteger getAttributeAsBigInteger (@Nullable final KEYTYPE aName, @Nullable final BigInteger aDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsBigInteger (aName, aValue, aDefault);
  }

  @Nullable
  public final BigDecimal getAttributeAsBigDecimal (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBigDecimal (aName, null);
  }

  @Nullable
  public final BigDecimal getAttributeAsBigDecimal (@Nullable final KEYTYPE aName, @Nullable final BigDecimal aDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return AttributeValueConverter.getAsBigDecimal (aName, aValue, aDefault);
  }
}
