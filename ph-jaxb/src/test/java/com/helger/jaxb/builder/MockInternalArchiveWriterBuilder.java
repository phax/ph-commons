package com.helger.jaxb.builder;

public final class MockInternalArchiveWriterBuilder extends
                                                    AbstractJAXBWriterBuilder <com.helger.jaxb.mock.internal.MockJAXBArchive, MockInternalArchiveWriterBuilder>
{
  public MockInternalArchiveWriterBuilder ()
  {
    super (new InternalArchiveDocumentType ());
  }
}
