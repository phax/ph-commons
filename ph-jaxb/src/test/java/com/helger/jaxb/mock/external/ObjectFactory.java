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
package com.helger.jaxb.mock.external;

import javax.annotation.Nonnull;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the generated package.
 * <p>
 * An ObjectFactory allows you to programmatically construct new instances of
 * the Java representation for XML content. The Java representation of XML
 * content can consist of schema derived interfaces and classes representing the
 * binding of schema type definitions, element declarations and model groups.
 * Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public final class ObjectFactory
{
  /**
   * Create a new ObjectFactory that can be used to create new instances of
   * schema derived classes for package: generated
   */
  public ObjectFactory ()
  {}

  /**
   * @return an instance of {@link MockJAXBIssue }
   */
  @Nonnull
  public MockJAXBIssue createIssue ()
  {
    return new MockJAXBIssue ();
  }

  /**
   * @return an instance of {@link MockJAXBCollection }
   */
  @Nonnull
  public MockJAXBCollection createCollection ()
  {
    return new MockJAXBCollection ();
  }

  /**
   * @return an instance of {@link MockJAXBIssue.FirstPage }
   */
  @Nonnull
  public MockJAXBIssue.FirstPage createIssueFirstPage ()
  {
    return new MockJAXBIssue.FirstPage ();
  }

  /**
   * @return an instance of {@link MockJAXBArchive }
   */
  @Nonnull
  public MockJAXBArchive createArchive ()
  {
    return new MockJAXBArchive ();
  }
}
