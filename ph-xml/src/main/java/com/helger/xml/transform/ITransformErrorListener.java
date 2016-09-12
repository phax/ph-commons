package com.helger.xml.transform;

import javax.annotation.Nonnull;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.helger.commons.ValueEnforcer;

/**
 * Extended {@link ErrorListener} interface with chaining method.
 *
 * @author Philip Helger
 */
public interface ITransformErrorListener extends ErrorListener
{
  @Nonnull
  default ITransformErrorListener andThen (@Nonnull final ErrorListener aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    final ITransformErrorListener aThis = this;

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
