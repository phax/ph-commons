package com.helger.jaxb.builder;

public final class MockInternalArchiveReaderBuilder extends
                                                    AbstractJAXBReaderBuilder <com.helger.jaxb.mock.internal.MockJAXBArchive, MockInternalArchiveReaderBuilder>
{
  public MockInternalArchiveReaderBuilder ()
  {
    super (new InternalArchiveDocumentType ());
  }
}
