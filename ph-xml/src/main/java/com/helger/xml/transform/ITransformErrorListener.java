package com.helger.xml.transform;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Extended {@link ErrorListener} interface with chaining method.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
public interface ITransformErrorListener extends ErrorListener, Serializable
{
  @Nonnull
  default ITransformErrorListener andThen (@Nullable final ErrorListener aOther)
  {
    final ITransformErrorListener aThis = this;
    if (aOther == null)
      return aThis;

    return new ITransformErrorListener ()
    {
      public void warning (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.warning (aEx);
        aOther.warning (aEx);
      }

      public void error (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.error (aEx);
        aOther.error (aEx);
      }

      public void fatalError (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.fatalError (aEx);
        aOther.fatalError (aEx);
      }
    };
  }
}
