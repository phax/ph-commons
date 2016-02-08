package com.helger.jaxb.builder;

public final class MockExternalArchiveWriterBuilder extends
                                                    JAXBWriterBuilder <com.helger.jaxb.mock.external.MockJAXBArchive, MockExternalArchiveWriterBuilder>
{
  public MockExternalArchiveWriterBuilder ()
  {
    super (new ExternalArchiveDocumentType ());
  }
}
