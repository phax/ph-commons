/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.collection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

/**
 * A version of a stack that does not use {@link java.util.Vector} but an
 * {@link ArrayList} as the underlying data structure as opposed to
 * {@link java.util.Stack}. This spares us from unnecessary synchronization.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of the elements contained in the stack
 */
@NotThreadSafe
public class NonBlockingStack <ELEMENTTYPE> extends ArrayList <ELEMENTTYPE>
                              implements ICloneable <NonBlockingStack <ELEMENTTYPE>>
{
  public NonBlockingStack ()
  {}

  public NonBlockingStack (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  @SafeVarargs
  public NonBlockingStack (@Nullable final ELEMENTTYPE... aElements)
  {
    CollectionHelper.getConcatenatedInline (this, aElements);
  }

  public NonBlockingStack (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    if (aCollection != null)
      addAll (aCollection);
  }

  public NonBlockingStack (@Nullable final NonBlockingStack <? extends ELEMENTTYPE> aStack)
  {
    if (aStack != null)
      addAll (aStack);
  }

  /**
   * Pushes an item onto the top of this stack.
   *
   * @param aItem
   *        the item to be pushed onto this stack.
   * @return the <code>aItem</code> argument.
   */
  @Nullable
  public ELEMENTTYPE push (@Nullable final ELEMENTTYPE aItem)
  {
    add (aItem);
    return aItem;
  }

  /**
   * Removes the object at the top of this stack and returns that object as the
   * value of this function.
   *
   * @return The object at the top of this stack (the last item of the list).
   * @exception EmptyStackException
   *            if this stack is empty.
   */
  @Nullable
  public ELEMENTTYPE pop ()
  {
    if (isEmpty ())
      throw new EmptyStackException ();
    return remove (size () - 1);
  }

  /**
   * Looks at the object at the top of this stack without removing it from the
   * stack.
   *
   * @return the object at the top of this stack (the last item of the list).
   * @exception EmptyStackException
   *            if this stack is empty.
   */
  @Nullable
  public ELEMENTTYPE peek ()
  {
    if (isEmpty ())
      throw new EmptyStackException ();
    return get (size () - 1);
  }

  /**
   * Looks at the object at the top of this stack without removing it from the
   * stack. Synonym for {@link #peek()}
   *
   * @return the object at the top of this stack (the last item of the list).
   * @exception EmptyStackException
   *            if this stack is empty.
   * @see #peek()
   */
  @Nullable
  public ELEMENTTYPE top ()
  {
    return peek ();
  }

  /**
   * @return The first element in the stack (the oldest element) in comparison
   *         to {@link #peek()} delivering the last element.
   * @throws EmptyStackException
   *         if the stack is empty
   */
  @Nullable
  public ELEMENTTYPE firstElement ()
  {
    if (isEmpty ())
      throw new EmptyStackException ();
    return get (0);
  }

  /**
   * Replaces the top element in the stack. This is a shortcut for
   * <code>pop (); push (aItem);</code>
   *
   * @param aItem
   *        the item to be pushed onto this stack.
   * @return the <code>aItem</code> argument.
   * @throws EmptyStackException
   *         if the stack is empty
   */
  @Nullable
  public ELEMENTTYPE replaceTopElement (@Nullable final ELEMENTTYPE aItem)
  {
    if (isEmpty ())
      throw new EmptyStackException ();
    set (size () - 1, aItem);
    return aItem;
  }

  @Nonnull
  public NonBlockingStack <ELEMENTTYPE> getClone ()
  {
    return new NonBlockingStack <ELEMENTTYPE> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (super.hashCode ()).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("list", super.toString ()).toString ();
  }
}
