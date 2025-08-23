package com.helger.url;

import java.nio.charset.Charset;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.url.param.IURLParameterList;
import com.helger.url.param.URLParameterList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
