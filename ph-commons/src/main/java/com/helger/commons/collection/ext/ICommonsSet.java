package com.helger.commons.collection.ext;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public interface ICommonsSet <ELEMENTTYPE> extends Set <ELEMENTTYPE>, ICommonsCollection <ELEMENTTYPE>
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSet <ELEMENTTYPE> getCopy ()
  {
    return new CommonsSet <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSet <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCopy ();

    final ICommonsSet <ELEMENTTYPE> ret = new CommonsSet <> (size ());
    findAll (aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSet <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSet <DSTTYPE> ret = new CommonsSet <> (size ());
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSet <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSet <DSTTYPE> ret = new CommonsSet <> ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsSet <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsSet <DSTTYPE> ret = new CommonsSet <> ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }
}
