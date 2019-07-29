package com.ht.l10n;

import static org.junit.Assert.assertNotNull;

import com.ht.wrap.ResourceBundleWrapperConfigurator;
import com.ht.wrap.StubWrapperFactory;

import org.junit.Before;
import org.junit.Test;

public class LocalizerUnitTest {
  private LocalizerFactoryInternal localizerFactoryInternal;
  private StubWrapperFactory stubWrapperFactory;
  private Assert localizerAssert;
  private ResourceBundleWrapperConfigurator resourceBundleWrapperForLocaleConfigurator;
  private ResourceBundleWrapperConfigurator resourceBundleWrapperForRootLocaleConfigurator;


  @Before
  public void setUp() throws Exception {
    localizerFactoryInternal = SystemInternal.getSystemInternal().getFactoryInternal();
    localizerFactoryInternal.resetAll();
    stubWrapperFactory = StubWrapperFactory.createStubWrapperFactory();
    resourceBundleWrapperForLocaleConfigurator =
        stubWrapperFactory.getResourceBundleWrapperForLocaleConfigurator();
    resourceBundleWrapperForRootLocaleConfigurator =
        stubWrapperFactory.getResourceBundleWrapperForRootLocaleConfigurator();
    localizerFactoryInternal.setWrapperFactory(stubWrapperFactory);
    localizerAssert = Assert.createAssert();
  }

  @Test
  public void Localizer_createTestingAssets_testingAssetsAreCreated() {
    assertNotNull(localizerFactoryInternal);
    assertNotNull(stubWrapperFactory);
    assertNotNull(resourceBundleWrapperForLocaleConfigurator);
    assertNotNull(resourceBundleWrapperForRootLocaleConfigurator);
    assertNotNull(localizerAssert);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void Localizer_addLocalizerFieldToUndefinedLocalizerType_unsupportedOperationExceptionIsThrown()
      throws Exception {
    LocalizerType localizerType =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerTypeInternal(null);

    localizerFactoryInternal.createLocalizerField(localizerType, "does.not.matter");
  }

  @Test
  public void Localizer_getLocalizerTypeFromUndefinedLocalizerUsingAnyLocalizerTypeUid_undefinedLocalizerTypeReturned() {
    final String expectedGroupName = "undef.group";
    final String expectedTypeName = "undef.type";
    final String expectedInstanceName = "undef.instance";
    final boolean expectedIsDefined = false;
    LocalizerType localizerType =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerType(null);

    localizerAssert.assertExpectedLocalizerType(expectedGroupName, expectedTypeName,
        expectedInstanceName, expectedIsDefined, localizerType);
  }

  @Test
  public void Localizer_getLocalizerFieldFromUndefindLocalizerTypeUsingAnyLocalizerFieldUid_undefinedLocalizerFieldReturned() {
    final String expectedFieldName = "undef.field";
    final String expectedFullyQualifiedName = "undef.group.undef.type.undef.instance.undef.field";
    final String expectedUidKey = expectedFullyQualifiedName;
    final boolean expectedIsDefined = false;
    final LocalizerType undefinedLocalizerType =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerType(null);

    LocalizerField localizerField = undefinedLocalizerType.getLocalizerField(null);

    localizerAssert.assertExpectedLocalizerField(expectedFieldName, expectedFullyQualifiedName,
        expectedUidKey, expectedIsDefined, localizerField);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void Localizer_addLocalizerTypeToUndefinedLocalizer_unsupportedOperationExceptionIsThrown()
      throws Exception {
    Localizer localizer = localizerFactoryInternal.createUndefinedLocalizer();

    localizerFactoryInternal.createLocalizerType(localizer, "does.not.matter", "does.not.matter",
        "does.not.matter");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void Localizer_addLocalizerBundleToUndefinedLocalizer_unsupportedOperationExceptionIsThrown()
      throws Exception {
    resourceBundleWrapperForLocaleConfigurator.resetAll().doesResourceBundleExist(true);
    resourceBundleWrapperForRootLocaleConfigurator.resetAll().doesResourceBundleExist(true);
    Localizer localizer = localizerFactoryInternal.createUndefinedLocalizer();

    localizerFactoryInternal.createLocalizerBundle(localizer,
        "com.resource.bundle.name.DoesNotMatter");
  }
}
