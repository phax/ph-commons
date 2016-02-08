package com.helger.jaxb.builder;

public final class MockExternalArchiveWriterBuilder extends
                                                    AbstractJAXBWriterBuilder <com.helger.jaxb.mock.external.MockJAXBArchive, MockExternalArchiveWriterBuilder>
{
  public MockExternalArchiveWriterBuilder ()
  {
    super (new ExternalArchiveDocumentType ());
  }
}
