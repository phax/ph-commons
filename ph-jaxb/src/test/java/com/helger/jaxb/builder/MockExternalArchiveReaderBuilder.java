package com.helger.jaxb.builder;

public final class MockExternalArchiveReaderBuilder extends
                                                    JAXBReaderBuilder <com.helger.jaxb.mock.external.MockJAXBArchive, MockExternalArchiveReaderBuilder>
{
  public MockExternalArchiveReaderBuilder ()
  {
    super (new ExternalArchiveDocumentType ());
  }
}
