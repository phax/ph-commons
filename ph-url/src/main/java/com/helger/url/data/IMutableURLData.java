package com.helger.url.data;

import java.nio.charset.Charset;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.url.param.IURLParameterList;
import com.helger.url.param.URLParameterList;

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

  /**
   * @return A list of all query string parameters. May not be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  URLParameterList params ();

  @Nonnull
  @ReturnsMutableObject
  default IURLParameterList getAllParams ()
  {
    return params ().getClone ();
  }

  @Nonnull
  IMPLTYPE setParams (@Nullable IURLParameterList aParams);

  @Nonnull
  IMPLTYPE setAnchor (@Nullable String sAnchor);

  @Nonnull
  IMPLTYPE setCharset (@Nullable Charset aCharset);
}
