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
package com.helger.json.visit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.JsonValue;

/**
 * Test class for class {@link JsonVisitor}.
 *
 * @author Philip Helger
 */
public final class JsonVisitorTest
{
  /**
   * Callback that records all visited nodes in visiting order.
   *
   * @author Philip Helger
   */
  private static final class MockVisitorCallback implements IJsonVisitorCallback
  {
    private final ICommonsList <String> m_aEvents = new CommonsArrayList <> ();

    public void onJsonValue (@NonNull final IJsonValue aValue)
    {
      m_aEvents.add ("value:" + String.valueOf (aValue.getValue ()));
    }

    public void onJsonArrayStart (@NonNull final IJsonArray aValue)
    {
      m_aEvents.add ("[");
    }

    public void onJsonArrayEnd (@NonNull final IJsonArray aValue)
    {
      m_aEvents.add ("]");
    }

    public void onJsonObjectStart (@NonNull final IJsonObject aValue)
    {
      m_aEvents.add ("{");
    }

    public void onJsonObjectElementName (@NonNull final String sName)
    {
      m_aEvents.add ("name:" + sName);
    }

    public void onJsonObjectEnd (@NonNull final IJsonObject aValue)
    {
      m_aEvents.add ("}");
    }

    @NonNull
    public String getEvents ()
    {
      return StringImplode.imploder ().separator (' ').source (m_aEvents).build ();
    }
  }

  @Test
  public void testVisitValue ()
  {
    final MockVisitorCallback aCB = new MockVisitorCallback ();
    JsonVisitor.visit (JsonValue.create ("abc"), aCB);
    assertEquals ("value:abc", aCB.getEvents ());
  }

  @Test
  public void testVisitArray ()
  {
    final MockVisitorCallback aCB = new MockVisitorCallback ();
    JsonVisitor.visit (new JsonArray ().add ("a").add ("b"), aCB);
    assertEquals ("[ value:a value:b ]", aCB.getEvents ());
  }

  @Test
  public void testVisitObject ()
  {
    final MockVisitorCallback aCB = new MockVisitorCallback ();
    JsonVisitor.visit (new JsonObject ().add ("k", "v"), aCB);
    assertEquals ("{ name:k value:v }", aCB.getEvents ());
  }

  @Test
  public void testVisitNestedRecursive ()
  {
    final MockVisitorCallback aCB = new MockVisitorCallback ();
    JsonVisitor.visit (new JsonObject ().add ("arr", new JsonArray ().add (1).add (new JsonObject ().add ("k", "v"))),
                       aCB);
    assertEquals ("{ name:arr [ value:1 { name:k value:v } ] }", aCB.getEvents ());
  }

  @Test
  public void testDefaultCallbackDoesNothing ()
  {
    // All methods of IJsonVisitorCallback have an empty default implementation
    final IJsonVisitorCallback aCB = new IJsonVisitorCallback ()
    {};
    JsonVisitor.visit (new JsonObject ().add ("arr", new JsonArray ().add (1).add ("x")), aCB);
    assertNotNull (aCB);
  }

  @Test
  public void testInvalidParams ()
  {
    final MockVisitorCallback aCB = new MockVisitorCallback ();
    try
    {
      JsonVisitor.visit (null, aCB);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      JsonVisitor.visit (JsonValue.create ("abc"), null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
