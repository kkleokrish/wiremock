package com.github.tomakehurst.wiremock;

import com.github.tomakehurst.wiremock.admin.model.SingleStubMappingResult;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.io.Resources;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.testsupport.TestHttpHeader.withHeader;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RemoteMappingsLoaderAcceptanceTest extends AcceptanceTestBase {

    static WireMock wmClient;
    static File rootDir;

    @BeforeClass
    public static void initWithTempDir() throws Exception {
        setupServerWithTempFileRoot();
        wmClient = new WireMock(wireMockServer.port());
        rootDir = new File(Resources.getResource("remoteloader").toURI());
    }

    @Test
    public void loadsTheMappingsFromTheDirectorySpecifiedIntoTheRemoteWireMockServer() {
        wmClient.loadMappingsFrom(rootDir);

        assertThat(
            testClient.get("/remote-load/1").content(), is("Remote load 1")
        );
        assertThat(
            testClient.get(
                "/remote-load/2", withHeader("Accept", "text/plain")
            ).content(), is("Remote load 2")
        );
    }

    @Test
    public void convertsBodyFileToStringBodyWhenAKnownTextTypeFromFileExtension() {
        wmClient.loadMappingsFrom(rootDir);

        SingleStubMappingResult stubMapping =
            wmClient.getStubMapping(UUID.fromString("e7af68ed-ed7c-4f9f-9d34-344c88cca8b7"));

        assertThat(stubMapping.getItem().getResponse().specifiesBinaryBodyContent(), is(false));
        assertThat(testClient.get("/text-file").content(), is("Some text"));
    }

    @Test
    public void convertsBodyFileToStringBodyWhenAKnownImageTypeFromFileExtension() {
        wmClient.loadMappingsFrom(rootDir);
        SingleStubMappingResult stubMapping =
            wmClient.getStubMapping(UUID.fromString("f7550b27-b544-4967-b7e8-f777eca68235"));

        assertThat(stubMapping.getItem().getResponse().specifiesBinaryBodyContent(), is(true));
    }

    @Test
    public void convertsBodyFileToStringBodyWhenAKnownTextTypeFromContentTypeHeader() {
        wmClient.loadMappingsFrom(rootDir);

        SingleStubMappingResult stubMapping =
            wmClient.getStubMapping(UUID.fromString("08851f9e-8b9a-4e32-a4f3-7befd9c72d4d"));

        assertThat(stubMapping.getItem().getResponse().specifiesBinaryBodyContent(), is(false));
    }

    @Test
    public void convertsBodyFileToStringBodyWhenAKnownImageTypeFromContentTypeHeader() {
        wmClient.loadMappingsFrom(rootDir);
        SingleStubMappingResult stubMapping =
            wmClient.getStubMapping(UUID.fromString("59179b2b-ce01-49cf-8381-280dcd559484"));

        assertThat(stubMapping.getItem().getResponse().specifiesBinaryBodyContent(), is(true));
    }



}
