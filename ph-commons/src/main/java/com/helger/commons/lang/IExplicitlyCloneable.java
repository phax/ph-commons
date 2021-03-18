/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.lang;

import javax.annotation.Nonnull;

/**
 * A marker interface for objects using {@link Cloneable} but in a more explicit
 * way. Note: cannot use a type argument for the result of "clone" because
 * otherwise this interface cannot be used on multiple levels of the inheritance
 * tree. So implementing classes should just change the return type accordingly.
 *
 * @author Philip Helger
 * @since 9.1.8
 */
public interface IExplicitlyCloneable extends Cloneable
{
  /**
   * Creates and returns a copy of this object. The precise meaning of "copy"
   * may depend on the class of the object. The general intent is that, for any
   * object {@code x}, the expression: <blockquote>
   *
   * <pre>
   * x.clone () != x
   * </pre>
   *
   * </blockquote> will be true, and that the expression: <blockquote>
   *
   * <pre>
   * x.clone ().getClass () == x.getClass ()
   * </pre>
   *
   * </blockquote> will be {@code true}, but these are not absolute
   * requirements. While it is typically the case that: <blockquote>
   *
   * <pre>
   * x.clone ().equals (x)
   * </pre>
   *
   * </blockquote> will be {@code true}, this is not an absolute requirement.
   * <p>
   * By convention, the returned object should be obtained by calling
   * {@code super.clone}. If a class and all of its superclasses (except
   * {@code Object}) obey this convention, it will be the case that
   * {@code x.clone().getClass() == x.getClass()}.
   * <p>
   * By convention, the object returned by this method should be independent of
   * this object (which is being cloned). To achieve this independence, it may
   * be necessary to modify one or more fields of the object returned by
   * {@code super.clone} before returning it. Typically, this means copying any
   * mutable objects that comprise the internal "deep structure" of the object
   * being cloned and replacing the references to these objects with references
   * to the copies. If a class contains only primitive fields or references to
   * immutable objects, then it is usually the case that no fields in the object
   * returned by {@code super.clone} need to be modified.
   * <p>
   * The method {@code clone} for class {@code Object} performs a specific
   * cloning operation. First, if the class of this object does not implement
   * the interface {@code Cloneable}, then a {@code CloneNotSupportedException}
   * is thrown. Note that all arrays are considered to implement the interface
   * {@code Cloneable} and that the return type of the {@code clone} method of
   * an array type {@code T[]} is {@code T[]} where T is any reference or
   * primitive type. Otherwise, this method creates a new instance of the class
   * of this object and initializes all its fields with exactly the contents of
   * the corresponding fields of this object, as if by assignment; the contents
   * of the fields are not themselves cloned. Thus, this method performs a
   * "shallow copy" of this object, not a "deep copy" operation.
   * <p>
   * The class {@code Object} does not itself implement the interface
   * {@code Cloneable}, so calling the {@code clone} method on an object whose
   * class is {@code Object} will result in throwing an exception at run time.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException
   *         if the object's class does not support the {@code Cloneable}
   *         interface. Subclasses that override the {@code clone} method can
   *         also throw this exception to indicate that an instance cannot be
   *         cloned.
   * @see java.lang.Cloneable
   */
  @Nonnull
  Object clone () throws CloneNotSupportedException;
}
