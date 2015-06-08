package com.helger.commons.aggregate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base implementation for {@link IAggregator}
 * 
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The input type.
 * @param <DSTTYPE>
 *        The output type.
 */
public abstract class AbstractAggregator <SRCTYPE, DSTTYPE> implements IAggregator <SRCTYPE, DSTTYPE>
{
  @Nullable
  public DSTTYPE aggregate (@Nonnull final SRCTYPE... aObjects)
  {
    return aggregate (CollectionHelper.newList (aObjects));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
