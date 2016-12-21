package com.microsoft.aad.adal;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DRSMetadataRequestorTests extends AndroidTestHelper {

    private static final String RESPONSE = "{\n"
            +
            "  \"DeviceRegistrationService\": {\n"
            +
            "    \"RegistrationEndpoint\": \"https://fs.lindft6.com/EnrollmentServer/DeviceEnrollmentWebService.svc\",\n"
            +
            "    \"RegistrationResourceId\": \"urn:ms-drs:UUID\",\n"
            +
            "    \"ServiceVersion\": \"1.0\"\n"
            +
            "  },\n"
            +
            "  \"AuthenticationService\": {\n"
            +
            "    \"OAuth2\": {\n"
            +
            "      \"AuthCodeEndpoint\": \"https://fs.lindft6.com/adfs/oauth2/authorize\",\n"
            +
            "      \"TokenEndpoint\": \"https://fs.lindft6.com/adfs/oauth2/token\"\n"
            +
            "    }\n"
            +
            "  },\n"
            +
            "  \"IdentityProviderService\": {\n"
            +
            "    \"PassiveAuthEndpoint\": \"https://fs.lindft6.com/adfs/ls\"\n"
            +
            "  }\n"
            +
            "}";

    private static final String TEST_ADFS = "https://fs.lindft6.com/adfs/ls";
    private static final String DOMAIN = "lindft6.com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        HttpUrlConnectionFactory.setMockedHttpUrlConnection(null);
        super.tearDown();
    }

    @SmallTest
    public void testRequestMetadata() throws IOException, AuthenticationException {
        final HttpURLConnection mockedConnection = Mockito.mock(HttpURLConnection.class);
        Util.prepareMockedUrlConnection(mockedConnection);

        Mockito.when(mockedConnection.getInputStream()).thenReturn(Util.createInputStream(RESPONSE));
        Mockito.when(mockedConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        DRSMetadataRequestor requestor = new DRSMetadataRequestor();

        DRSMetadata metadata = requestor.requestMetadata(DOMAIN);

        Assert.assertEquals(
                TEST_ADFS,
                metadata.getIdentityProviderService().getPassiveAuthEndpoint()
        );
    }

    @SmallTest
    public void testRequestMetadataThrows() throws IOException, AuthenticationException {
        final HttpURLConnection mockedConnection = Mockito.mock(HttpURLConnection.class);
        Util.prepareMockedUrlConnection(mockedConnection);

        Mockito.when(mockedConnection.getInputStream()).thenReturn(Util.createInputStream(RESPONSE));
        Mockito.when(mockedConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        DRSMetadataRequestor requestor = new DRSMetadataRequestor();

        try {
            DRSMetadata metadata = requestor.requestMetadata(DOMAIN);
            fail();
        } catch (AuthenticationException e) {
            // should throw
            return;
        }
    }

    @SmallTest
    public void testParseMetadata() throws AuthenticationException {
        HttpWebResponse mockWebResponse = Mockito.mock(HttpWebResponse.class);
        Mockito.when(mockWebResponse.getBody()).thenReturn(RESPONSE);

        DRSMetadata metadata = new DRSMetadataRequestor().parseMetadata(mockWebResponse);

        Assert.assertEquals(
                TEST_ADFS,
                metadata.getIdentityProviderService().getPassiveAuthEndpoint()
        );
    }

    @SmallTest
    public void testBuildRequestUrlByTypeOnPrem() {
        final String expected = "https://enterpriseregistration.lindft6.com/enrollmentserver/contract?api-version=1.0";
        DRSMetadataRequestor requestor = new DRSMetadataRequestor();
        Assert.assertEquals(
                expected,
                requestor.buildRequestUrlByType(
                        DRSMetadataRequestor.Type.ON_PREM,
                        DOMAIN
                )
        );
    }

    @SmallTest
    public void testBuildRequestUrlByTypeCloud() {
        final String expected = "https://enterpriseregistration.windows.net/lindft6.com/enrollmentserver/contract?api-version=1.0";
        DRSMetadataRequestor requestor = new DRSMetadataRequestor();
        Assert.assertEquals(
                expected,
                requestor.buildRequestUrlByType(
                        DRSMetadataRequestor.Type.CLOUD,
                        DOMAIN
                )
        );
    }
}
