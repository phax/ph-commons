package com.helger.commons.collection;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.NonBlockingStack;

@Immutable
public final class StackHelper
{
  private StackHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nonnegative final int nInitialCapacity)
  {
    return new NonBlockingStack <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack ()
  {
    return new NonBlockingStack <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> NonBlockingStack <DSTTYPE> newStackMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                              @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newStack (0);
    final NonBlockingStack <DSTTYPE> ret = newStack (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.push (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Create a new stack with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValue
   *        The value to push. Maybe <code>null</code>.
   * @return A non-<code>null</code> stack.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE aValue)
  {
    final NonBlockingStack <ELEMENTTYPE> ret = newStack ();
    ret.push (aValue);
    return ret;
  }

  /**
   * Create a new stack from the given array.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will
   *        be the top element on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE... aValues)
  {
    return new NonBlockingStack <> (aValues);
  }

  /**
   * Create a new stack from the given collection.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will
   *        be the top element on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final Collection <? extends ELEMENTTYPE> aValues)
  {
    return new NonBlockingStack <> (aValues);
  }
}
