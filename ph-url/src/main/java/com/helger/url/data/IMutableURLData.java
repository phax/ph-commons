package com.helger.url.data;

import java.nio.charset.Charset;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.param.URLParameter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Mutable version of the {@link IURLData} interface.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The real implementation type
 * @since 12.0.0 RC2
 */
public interface IMutableURLData <IMPLTYPE extends IMutableURLData <IMPLTYPE>> extends IURLData
{
  @Nonnull
  IMPLTYPE setPath (@Nonnull String sPath);

  @Nonnull
  @ReturnsMutableObject
  default ICommonsList <URLParameter> getAllParams ()
  {
    return params ().getClone ();
  }

  @Nonnull
  IMPLTYPE setParams (@Nullable ICommonsList <URLParameter> aParams);

  @Nonnull
  IMPLTYPE setAnchor (@Nullable String sAnchor);

  @Nonnull
  IMPLTYPE setCharset (@Nullable Charset aCharset);
}
