/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.permissionspolicy;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * An allow list to be used in a Permissions Policy directive ({@link PermissionsPolicyDirective}).
 * It's just a convenient way to build a Permissions Policy directive value.
 *
 * @author Philip Helger
 * @since 12.4.0
 */
@NotThreadSafe
public class PermissionsPolicyAllowList extends AbstractPermissionsPolicyAllowList <PermissionsPolicyAllowList>
{
  /**
   * Constructor creating an empty allow list, meaning that the respective feature is disabled in
   * all browsing contexts.
   */
  public PermissionsPolicyAllowList ()
  {}

  /**
   * @return A new allow list <code>()</code> that disables the feature in this document and in all
   *         nested browsing contexts.
   */
  @NonNull
  public static PermissionsPolicyAllowList createNone ()
  {
    return new PermissionsPolicyAllowList ();
  }

  /**
   * @return A new allow list <code>*</code> that allows the feature in this document and in all
   *         nested browsing contexts, independent of their origin.
   */
  @NonNull
  public static PermissionsPolicyAllowList createAll ()
  {
    return new PermissionsPolicyAllowList ().setAll ();
  }

  /**
   * @return A new allow list <code>(self)</code> that allows the feature in this document and in
   *         all same-origin nested browsing contexts.
   */
  @NonNull
  public static PermissionsPolicyAllowList createSelf ()
  {
    return new PermissionsPolicyAllowList ().addKeywordSelf ();
  }
}
