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
package com.helger.url;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.data.IURLData;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;

/**
 * Builder class for {@link ISimpleURL} objects
 *
 * @author Philip Helger
 * @since 12.0.0
 */
public class URLBuilder implements IBuilder <ISimpleURL>
{
  private final URLData m_aData;

  public URLBuilder ()
  {
    m_aData = URLData.createEmpty ();
  }

  protected URLBuilder (@NonNull final URLData aData)
  {
    m_aData = aData;
  }

  @NonNull
  public IURLData urlData ()
  {
    return m_aData;
  }

  @NonNull
  public URLBuilder path (@NonNull final String s)
  {
    m_aData.setPath (s);
    return this;
  }

  @NonNull
  public URLBuilder params (@Nullable final Map <String, String> a)
  {
    m_aData.params ().removeAll ();
    if (a != null)
      for (final var e : a.entrySet ())
        addParam (e.getKey (), e.getValue ());
    return this;
  }

  @NonNull
  public URLBuilder params (@Nullable final ICommonsList <URLParameter> a)
  {
    m_aData.setParams (a);
    return this;
  }

  @NonNull
  public URLBuilder addParam (@NonNull @Nonempty final String sName, final boolean b)
  {
    return addParam (sName, Boolean.toString (b));
  }

  @NonNull
  public URLBuilder addParam (@NonNull @Nonempty final String sName, final int n)
  {
    return addParam (sName, Integer.toString (n));
  }

  @NonNull
  public URLBuilder addParam (@NonNull @Nonempty final String sName, final long n)
  {
    return addParam (sName, Long.toString (n));
  }

  @NonNull
  public URLBuilder addParam (@NonNull @Nonempty final String sName, @Nullable final String sValue)
  {
    return addParam (new URLParameter (sName, sValue));
  }

  @NonNull
  public URLBuilder addParam (@NonNull final URLParameter aParam)
  {
    ValueEnforcer.notNull (aParam, "Param");
    m_aData.params ().add (aParam);
    return this;
  }

  @NonNull
  public URLBuilder removeParam (@NonNull @Nonempty final String sName)
  {
    m_aData.params ().removeIf (x -> x.hasName (sName));
    return this;
  }

  @NonNull
  public URLBuilder param (@NonNull @Nonempty final String sName, @Nullable final String sValue)
  {
    removeParam (sName);
    return addParam (sName, sValue);
  }

  @NonNull
  public URLBuilder param (@NonNull final URLParameter aParam)
  {
    ValueEnforcer.notNull (aParam, "Param");
    removeParam (aParam.getName ());
    return addParam (aParam);
  }

  @NonNull
  public URLBuilder anchor (@Nullable final String s)
  {
    m_aData.setAnchor (s);
    return this;
  }

  @NonNull
  public URLBuilder charset (@Nullable final Charset a)
  {
    m_aData.setCharset (a);
    return this;
  }

  @NonNull
  public ISimpleURL build ()
  {
    return new ReadOnlyURL (m_aData);
  }

  @NonNull
  public static URLBuilder of (@Nullable final IURLData aURLData)
  {
    // Copy the URL data if present
    return aURLData == null ? new URLBuilder () : new URLBuilder (new URLData (aURLData));
  }

  @NonNull
  public static URLBuilder of (@Nullable final String sURL)
  {
    if (StringHelper.isEmpty (sURL))
      return new URLBuilder ();
    return new URLBuilder (SimpleURLHelper.getAsURLData (sURL, URLData.DEFAULT_CHARSET));
  }

  @NonNull
  public static URLBuilder of (@Nullable final URL aURL)
  {
    return of (aURL == null ? null : aURL.toExternalForm ());
  }

  @NonNull
  public static URLBuilder of (@Nullable final URI aURI)
  {
    return of (aURI == null ? null : aURI.toString ());
  }
}
