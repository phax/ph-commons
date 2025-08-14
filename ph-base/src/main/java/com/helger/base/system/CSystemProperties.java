/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.system;

import com.helger.annotation.concurrent.Immutable;

/**
 * List of predefined system properties.
 *
 * @author Philip Helger
 */
@Immutable
public class CSystemProperties
{
  public static final String SYSTEM_PROPERTY_FILE_SEPARATOR = "file.separator";
  public static final String SYSTEM_PROPERTY_JAVA_CLASS_PATH = "java.class.path";
  public static final String SYSTEM_PROPERTY_JAVA_CLASS_VERSION = "java.class.version";
  public static final String SYSTEM_PROPERTY_JAVA_LIBRARY_PATH = "java.library.path";
  public static final String SYSTEM_PROPERTY_JAVA_HOME = "java.home";
  public static final String SYSTEM_PROPERTY_JAVA_IO_TMPDIR = "java.io.tmpdir";
  public static final String SYSTEM_PROPERTY_JAVA_RUNTIME_NAME = "java.runtime.name";
  public static final String SYSTEM_PROPERTY_JAVA_RUNTIME_VERSION = "java.runtime.version";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_URL = "java.specification.url";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_VERSION = "java.specification.version";
  public static final String SYSTEM_PROPERTY_JAVA_VENDOR = "java.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VENDOR_URL = "java.vendor.url";
  public static final String SYSTEM_PROPERTY_JAVA_VERSION = "java.version";
  public static final String SYSTEM_PROPERTY_JAVA_VM_NAME = "java.vm.name";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_URL = "java.vm.specification.url";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
  public static final String SYSTEM_PROPERTY_JAVA_VM_URL = "java.vm.url";
  public static final String SYSTEM_PROPERTY_JAVA_VM_VENDOR = "java.vm.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VM_VERSION = "java.vm.version";
  public static final String SYSTEM_PROPERTY_LINE_SEPARATOR = "line.separator";
  public static final String SYSTEM_PROPERTY_OS_ARCH = "os.arch";
  public static final String SYSTEM_PROPERTY_OS_NAME = "os.name";
  public static final String SYSTEM_PROPERTY_OS_VERSION = "os.version";
  public static final String SYSTEM_PROPERTY_PATH_SEPARATOR = "path.separator";
  public static final String SYSTEM_PROPERTY_USER_DIR = "user.dir";
  public static final String SYSTEM_PROPERTY_USER_HOME = "user.home";
  public static final String SYSTEM_PROPERTY_USER_NAME = "user.name";

  private CSystemProperties ()
  {}
}
