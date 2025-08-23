package com.helger.url;

import java.nio.charset.Charset;
import java.util.Map;

import com.helger.annotation.Nonempty;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.data.IURLData;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Builder class for {@link ISimpleURL} objects
 *
 * @author Philip Helger
 */
public class URLBuilder implements IBuilder <ISimpleURL>
{
  private final URLData m_aData;

  public URLBuilder ()
  {
    m_aData = URLData.createEmpty ();
  }

  protected URLBuilder (@Nonnull final URLData aData)
  {
    m_aData = aData;
  }

  @Nonnull
  public URLBuilder path (@Nonnull final String s)
  {
    m_aData.setPath (s);
    return this;
  }

  @Nonnull
  public URLBuilder params (@Nullable final Map <String, String> a)
  {
    m_aData.params ().removeAll ();
    if (a != null)
      for (final var e : a.entrySet ())
        addParam (e.getKey (), e.getValue ());
    return this;
  }

  @Nonnull
  public URLBuilder params (@Nullable final ICommonsList <URLParameter> a)
  {
    m_aData.setParams (a);
    return this;
  }

  @Nonnull
  public URLBuilder addParam (@Nonnull @Nonempty final String sName, final boolean b)
  {
    return addParam (sName, Boolean.toString (b));
  }

  @Nonnull
  public URLBuilder addParam (@Nonnull @Nonempty final String sName, final int n)
  {
    return addParam (sName, Integer.toString (n));
  }

  @Nonnull
  public URLBuilder addParam (@Nonnull @Nonempty final String sName, final long n)
  {
    return addParam (sName, Long.toString (n));
  }

  @Nonnull
  public URLBuilder addParam (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    return addParam (new URLParameter (sName, sValue));
  }

  @Nonnull
  public URLBuilder addParam (@Nonnull final URLParameter aParam)
  {
    ValueEnforcer.notNull (aParam, "Param");
    m_aData.params ().add (aParam);
    return this;
  }

  @Nonnull
  public URLBuilder anchor (@Nullable final String s)
  {
    m_aData.setAnchor (s);
    return this;
  }

  @Nonnull
  public URLBuilder charset (@Nullable final Charset a)
  {
    m_aData.setCharset (a);
    return this;
  }

  @Nonnull
  public ISimpleURL build ()
  {
    return new ReadOnlyURL (m_aData);
  }

  @Nonnull
  public static URLBuilder of (@Nullable final IURLData aURLData)
  {
    // Copy the URL data if present
    return aURLData == null ? new URLBuilder () : new URLBuilder (new URLData (aURLData));
  }

  @Nonnull
  public static URLBuilder of (@Nullable final String sURL)
  {
    return new URLBuilder (SimpleURLHelper.getAsURLData (sURL, URLData.DEFAULT_CHARSET));
  }
}
