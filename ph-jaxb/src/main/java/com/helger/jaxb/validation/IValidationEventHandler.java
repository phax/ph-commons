package com.helger.jaxb.validation;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.ValidationEventHandler;

/**
 * An extended version of {@link ValidationEventHandler} with chaining
 * possibilities.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
public interface IValidationEventHandler extends ValidationEventHandler, Serializable
{
  @Nonnull
  default IValidationEventHandler andThen (@Nullable final ValidationEventHandler aOther)
  {
    final IValidationEventHandler aThis = this;
    if (aOther == null)
      return aThis;

    return x -> {
      if (!aThis.handleEvent (x))
      {
        // We should not continue
        return false;
      }
      return aOther.handleEvent (x);
    };
  }
}
