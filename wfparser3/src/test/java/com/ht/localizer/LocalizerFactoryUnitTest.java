package com.ht.localizer;

import static org.junit.Assert.assertNotNull;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import com.ht.uid.UID;
import com.ht.wrap.ResourceBundleWrapperConfigurator;
import com.ht.wrap.StubWrapperFactory;

public class LocalizerFactoryUnitTest {
  private LocalizerSystemInternal localizerSystemInternal;
  private LocalizerFactoryInternal localizerFactoryInternal;
  private StubLocalizerFactory stubLocalizerFactory;
  private StubWrapperFactory stubWrapperFactory;
  private Assert localizerAssert;
  private Locale expectedUndefinedLocale;
  private ResourceBundleWrapperConfigurator resourceBundleWrapperForLocaleConfigurator;
  private ResourceBundleWrapperConfigurator resourceBundleWrapperForRootLocaleConfigurator;

  @Before
  public void setup() {
    localizerSystemInternal = LocalizerSystemInternal.getSystemInternal();
    localizerFactoryInternal = localizerSystemInternal.getLocalizerFactoryInternal();
    localizerFactoryInternal.resetAll();
    stubLocalizerFactory = StubLocalizerFactory.createStubLocalizerFactory(localizerSystemInternal);
    stubWrapperFactory = StubWrapperFactory.createStubWrapperFactory();
    resourceBundleWrapperForLocaleConfigurator =
        stubWrapperFactory.getResourceBundleWrapperForLocaleConfigurator();
    resourceBundleWrapperForRootLocaleConfigurator =
        stubWrapperFactory.getResourceBundleWrapperForRootLocaleConfigurator();
    localizerFactoryInternal.setWrapperFactory(stubWrapperFactory);
    localizerAssert = Assert.createAssert();

    final String expectedUndefinedLanguage = "xx";
    final String expectedUndefinedRegion = "ZZ";
    expectedUndefinedLocale = (new Locale.Builder()).setLanguage(expectedUndefinedLanguage)
        .setRegion(expectedUndefinedRegion).build();
  }

  @Test
  public void LocalizerFactory_setupTestingAssets_testingAssetsSetUp() {
    assertNotNull(localizerSystemInternal);
    assertNotNull(localizerFactoryInternal);
    assertNotNull(stubLocalizerFactory);
    assertNotNull(stubWrapperFactory);
    assertNotNull(resourceBundleWrapperForLocaleConfigurator);
    assertNotNull(resourceBundleWrapperForRootLocaleConfigurator);
    assertNotNull(localizerAssert);
    assertNotNull(expectedUndefinedLocale);
  }

  @Test
  public void LocalizerFactory_createUndefinedLocalizerBundle_undefinedLocalizerBundleCreated() {
    final String expectedResourceBundleName = "__UNDEFINED__";
    final Locale expectedTargetLocale = expectedUndefinedLocale;
    final Locale expectedResolvedLocale = expectedUndefinedLocale;
    final boolean expectedIsDefined = false;
    LocalizerBundle localizerBundle = localizerFactoryInternal.createUndefinedLocalizerBundle();

    localizerAssert.assertExpectedLocalizerBundle(expectedTargetLocale, expectedResolvedLocale,
        expectedResourceBundleName, expectedIsDefined, localizerBundle);
  }

  @Test
  public void LocalizerFactory_createUndefinedLocalizer_undefinedLocalizerCreated() {
    final String expectedName = "UNDEFINED";
    final UID<Localizer> expectedLocalizerUid = localizerFactoryInternal.getUidFactory()
        .createUid(expectedName, stubLocalizerFactory.createDefaultStubLocalizer());
    final boolean expectedIsDefined = false;

    Localizer localizer = localizerFactoryInternal.createUndefinedLocalizer();

    localizerAssert.assertExpectedLocalizer(expectedName, expectedLocalizerUid,
        expectedUndefinedLocale, expectedIsDefined, localizer);
  }

  @Test
  public void LocalizerFactory_createUndefinedLocalizerInstance_undefinedLocalizerInstanceCreated()
      throws Exception {
    final String expectedInstanceName = "undef.instance";
    final String expectedFullyQualifiedName = "undef.group.undef.type.undef.method.undef.instance";
    final LocalizerType expectedLocalizerType =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerType(null);
    final String expectedUnformattedString = "UNDEFINED";
    final String expectedFormattedString = "UNDEFINED";
    final boolean expectedIsDefined = false;

    LocalizerInstance localizerInstance =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerInstance(null);

    localizerAssert.assertExpectedLocalizerInstance(expectedInstanceName,
        expectedFullyQualifiedName, expectedLocalizerType, expectedUnformattedString,
        expectedFormattedString, expectedIsDefined, localizerInstance);
  }

  @Test
  public void LocalizerFactory_createUndefinedLocalizerType_undefinedLocalizerTypeCreated() {
    final String expectedGroupName = "undef.group";
    final String expectedTypeName = "undef.type";
    final String expectedMethodName = "undef.method";
    final boolean expectedIsDefined = false;

    LocalizerType localizerType =
        localizerFactoryInternal.createUndefinedLocalizer().getLocalizerType(null);

    localizerAssert.assertExpectedLocalizerType(expectedGroupName, expectedTypeName,
        expectedMethodName, expectedIsDefined, localizerType);
  }

  @Test(expected = NullPointerException.class)
  public void LocalizerFactory_createRootLocaleLocalizerBundleWithNullLocalizer_nullPointerExceptionIsThrown()
      throws Exception {
    localizerFactoryInternal.createRootLocaleLocalizerBundle(null,
        "com.resource.bundle.name.DoesNotMatter");
  }

  @Test(expected = NullPointerException.class)
  public void LocalizerFactory_createRootLocaleLocalizerBundleWithNullResourceBundleName_nullPointerExceptionIsThrown()
      throws Exception {
    localizerFactoryInternal.createRootLocaleLocalizerBundle(
        localizerFactoryInternal.createLocalizerInternal("localizer.name", Locale.CANADA_FRENCH),
        null);
  }

  @Test(expected = LocalizerException.class)
  public void LocalizerFactory_createRootLocaleLocalizerBundleWithNonExistentResourceBundle_localizerExceptionIsThrown()
      throws Exception {
    resourceBundleWrapperForRootLocaleConfigurator.resetAll().doesResourceBundleExist(false);
    LocalizerInternal localizerInternal =
        localizerFactoryInternal.createLocalizerInternal("localizer.name", Locale.CANADA_FRENCH);
    final String expectedResourceBundleName = "com.resource.bundle.name.DoesNotMatter";

    localizerFactoryInternal.createRootLocaleLocalizerBundle(localizerInternal,
        expectedResourceBundleName);
  }

  @Test
  public void LocalizerFactory_createRootLocaleLocalizerBundleWithExistingResourceBundle_rootLocaleLocalizerBundleIsCreated()
      throws Exception {
    resourceBundleWrapperForRootLocaleConfigurator.resetAll().doesResourceBundleExist(true);
    final Locale expectedTargetLocale = Locale.CANADA_FRENCH;
    final Locale expectedResolvedLocale = Locale.ROOT;
    final String expectedResourceBundleName = "com.resource.bundle.name.DoesNotMatter";
    final boolean expectedIsDefined = true;
    LocalizerInternal localizerIntrnal =
        localizerFactoryInternal.createLocalizerInternal("localizer.name", expectedTargetLocale);

    LocalizerBundle localizerBundle = localizerFactoryInternal
        .createRootLocaleLocalizerBundle(localizerIntrnal, expectedResourceBundleName);

    localizerAssert.assertExpectedLocalizerBundle(expectedTargetLocale, expectedResolvedLocale,
        expectedResourceBundleName, expectedIsDefined, localizerIntrnal, localizerBundle);
  }

  @Test(expected = NullPointerException.class)
  public void LocalizerFactory_createLocalizerBundleWithNullLocalizer_nullPointerExceptionIsThrown()
      throws Exception {
    localizerFactoryInternal.createTargetLocalizerBundle(null,
        "com.resource.bundle.name.DoesNotMatter");
  }

  @Test(expected = NullPointerException.class)
  public void LocalizerFactory_createLocalizerBundleWithNullResourceBundleName_nullPointerExceptionIsThrown()
      throws Exception {
    localizerFactoryInternal.createTargetLocalizerBundle(
        localizerFactoryInternal.createLocalizerInternal("localizer.name", Locale.CANADA_FRENCH),
        null);
  }

  @Test(expected = LocalizerException.class)
  public void LocalizerFactory_createLocalizerBundleWithNonExistentResourceBundle_localizerExceptionIsThrown()
      throws Exception {
    resourceBundleWrapperForLocaleConfigurator.resetAll().doesResourceBundleExist(false);
    LocalizerInternal localizerInternal =
        localizerFactoryInternal.createLocalizerInternal("localizer.name", Locale.CANADA_FRENCH);
    final String expectedResourceBundleName = "com.resource.bundle.name.DoesNotMatter";

    localizerFactoryInternal.createTargetLocalizerBundle(localizerInternal,
        expectedResourceBundleName);
  }

  @Test
  public void LocalizerFactory_createLocalizerBundleWithExistingResourceBundleForLocale_localizerBundleIsCreatedWithLocaleResourceBundle()
      throws Exception {
    resourceBundleWrapperForLocaleConfigurator.resetAll().doesResourceBundleExist(true);
    final Locale expectedTargetLocale = Locale.CANADA_FRENCH;
    final Locale expectedResolvedLocale = Locale.CANADA_FRENCH;
    final String expectedResourceBundleName = "com.resource.bundle.name.DoesNotMatter";
    final boolean expectedIsDefined = true;

    LocalizerInternal localizerInternal =
        localizerFactoryInternal.createLocalizerInternal("localizer.name", expectedTargetLocale);

    LocalizerBundle localizerBundle = localizerFactoryInternal
        .createTargetLocalizerBundle(localizerInternal, expectedResourceBundleName);

    localizerAssert.assertExpectedLocalizerBundle(expectedTargetLocale, expectedResolvedLocale,
        expectedResourceBundleName, expectedIsDefined, localizerInternal, localizerBundle);
  }
}
