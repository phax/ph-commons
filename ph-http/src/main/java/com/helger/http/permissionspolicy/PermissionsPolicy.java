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

import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * Permissions Policy. It's a list of {@link PermissionsPolicyDirective}. The result of
 * {@link #getAsString()} is meant to be used as the value of the
 * {@link com.helger.http.CHttpHeader#PERMISSIONS_POLICY} HTTP header.<br>
 * See https://w3c.github.io/webappsec-permissions-policy/ and
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Permissions_Policy
 *
 * @author Philip Helger
 * @since 12.4.0
 */
@NotThreadSafe
public class PermissionsPolicy extends AbstractPermissionsPolicy <PermissionsPolicyDirective>
{
  /**
   * Constructor creating an empty Permissions Policy.
   */
  public PermissionsPolicy ()
  {}
}
