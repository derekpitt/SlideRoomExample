package com.slideroom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

public class SlideRoomClientV2
{
    public final ApplicationResource Application;
    public final ExportResource Export;

    private static final String DEFAULT_API_URL = "https://api.slideroom.com";
    private String _oauthToken;
    private String _apiUrl;

    private HttpTransport _httpTransport = new NetHttpTransport();
    private JsonFactory _jsonFactory = new JacksonFactory();

    public SlideRoomClientV2(String oauthToken)
    {
        this(oauthToken, DEFAULT_API_URL);
    }

    public SlideRoomClientV2(String oauthToken, String apiUrl)
    {
        this._oauthToken = oauthToken;
        this._apiUrl = apiUrl;

        this.Application = new ApplicationResource(this);
        this.Export = new ExportResource(this);
    }

    private Boolean isValueType(Field f)
    {
        Class<?> t = f.getType();
        return t.isPrimitive() || t == String.class || t == Integer.class || t == Boolean.class || t.isEnum();
    }

    private URI setQueryParameters(URI u, Object queryParameters, String prefix) throws IllegalArgumentException, IllegalAccessException, URISyntaxException
    {
        GenericUrl gu = new GenericUrl(u);
        for (Field f : queryParameters.getClass().getFields())
        {
            Object ff = f.get(queryParameters);
            if (ff == null)
            {
                continue;
            }

            if (this.isValueType(f))
            {
                gu.set(prefix + f.getName(), f.get(queryParameters).toString())
                        .toURI();
            }
            else
            {
                gu = new GenericUrl(this.setQueryParameters(gu.toURI(), f.get(queryParameters), f.getName() + "."));
            }
        }

        return gu.toURI();
    }

    private <T> T executeRequest(String method, String url, Object queryParameters,Object bodyParams, Class<T> type)
            throws IllegalAccessException, IOException, IllegalArgumentException, URISyntaxException, Models.ApiErrorException
    {
        HttpRequestFactory requestFactory = _httpTransport
                .createRequestFactory(new HttpRequestInitializer()
                {
                    @Override
                    public void initialize(HttpRequest request)
                    {
                        request.setParser(new JsonObjectParser(_jsonFactory));
                    }
                });

        URI u = this.setQueryParameters(new URI(_apiUrl + url), queryParameters, "");

        HttpRequest request = requestFactory.buildRequest(method, new GenericUrl(u), null);

        if (method != "GET")
        {
            request.setContent(new JsonHttpContent(_jsonFactory, bodyParams == null ? "" : bodyParams));
        }

        request.getHeaders().setAuthorization("Bearer " + _oauthToken);

        try
        {
            HttpResponse res = request.execute();
            return type == null ? null : res.parseAs(type);
        }
        catch (HttpResponseException e)
        {
            Reader r = new StringReader(e.getContent());

            JsonObjectParser p = _jsonFactory.createJsonObjectParser();
            Models.ApiErrorException ex = p.parseAndClose(r, Models.ApiErrorException.class);

            ex.StatusCode = e.getStatusCode();

            throw ex;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public class ApplicationResource
    {
        private SlideRoomClientV2 _client;

        private ApplicationResource(SlideRoomClientV2 client)
        {
            this._client = client;
        }

        public Models.RequestApplicationExportResult RequestExportByApplicationIdV2(String applicationId, Models.RequestExportByApplicationIdV2Parameters parms)
                throws SignatureException, IllegalArgumentException, IllegalAccessException, IOException, URISyntaxException, Models.ApiErrorException
        {
            String __url = "/api/v2/application/{applicationId}/request-export".replace("{applicationId}", applicationId.toString());
            return this._client.executeRequest("POST", __url, parms, null, Models.RequestApplicationExportResult.class);
        }

        public Models.RequestApplicationExportResult RequestExportV2(Models.RequestExportV2Parameters parms)
                throws SignatureException, IllegalArgumentException, IllegalAccessException, IOException, URISyntaxException, Models.ApiErrorException
        {
            String __url = "/api/v2/application/request-export";
            return this._client.executeRequest("POST", __url, parms, null, Models.RequestApplicationExportResult.class);
        }
    }

    public class ExportResource
    {
        private SlideRoomClientV2 _client;

        private ExportResource(SlideRoomClientV2 client)
        {
            this._client = client;
        }

        public Models.ExportResultV2 GetV2(Integer token)
                throws SignatureException, IllegalArgumentException, IllegalAccessException, IOException, URISyntaxException, Models.ApiErrorException
        {
            String __url = "/api/v2/export/{token}".replace("{token}", token.toString());
            return this._client.executeRequest("GET", __url, null, null, Models.ExportResultV2.class);
        }
    }



    static public class Models
    {
        public static class ApiErrorException extends Exception
        {
            private static final long serialVersionUID = 1L;
            @Key
            public Integer StatusCode;
            @Key
            public String Message;
        }

        public static class RequestExportByApplicationIdV2Parameters
        {
            public FormatEnum format;
            public RoundtypeEnum roundtype;
            public String roundname;
            public TabParameters tab;
            public PdfParameters pdf;
            public ZipParameters zip;
            public DeliveryParameters delivery;

            public enum FormatEnum { csv, txt, tab, xlsx, pdf, zip }
            public enum RoundtypeEnum { Assigned, Current, Named, All }

            public static class TabParameters
            {
                public String export;
            }

            public static class PdfParameters
            {
                public Boolean includeforms;
                public Boolean includereferences;
                public Boolean includemedia;
                public Boolean includeapplicantattachments;
                public Boolean includeorganizationattachments;
                public Boolean includeratings;
                public Boolean includefullpagemedia;
                public Boolean includehighlights;
                public Boolean includecomments;
                public Boolean includecommonapp;
            }

            public static class ZipParameters
            {
                public Boolean originalmedia;
                public Boolean includeforms;
                public Boolean includereferences;
                public Boolean includemedia;
                public Boolean includeapplicantattachments;
                public Boolean includeorganizationattachments;
                public Boolean includeratings;
                public Boolean includecomments;
                public Boolean includecommonapp;
            }

            public static class DeliveryParameters
            {
                public String account;
                public String folder;
            }
        }

        public static class RequestExportV2Parameters
        {
            public FormatEnum format;
            public RoundtypeEnum roundtype;
            public String roundname;
            public Integer since;
            public PoolEnum pool;
            public String searchname;
            public String email;
            public TabParameters tab;
            public PdfParameters pdf;
            public ZipParameters zip;
            public DeliveryParameters delivery;

            public enum FormatEnum { csv, txt, tab, xlsx, pdf, zip }
            public enum RoundtypeEnum { Assigned, Current, Named, All }
            public enum PoolEnum { All, Current, Archived, CommonAppSDS }

            public static class TabParameters
            {
                public String export;
            }

            public static class PdfParameters
            {
                public Boolean includeforms;
                public Boolean includereferences;
                public Boolean includemedia;
                public Boolean includeapplicantattachments;
                public Boolean includeorganizationattachments;
                public Boolean includeratings;
                public Boolean includefullpagemedia;
                public Boolean includehighlights;
                public Boolean includecomments;
                public Boolean includecommonapp;
            }

            public static class ZipParameters
            {
                public Boolean originalmedia;
                public Boolean includeforms;
                public Boolean includereferences;
                public Boolean includemedia;
                public Boolean includeapplicantattachments;
                public Boolean includeorganizationattachments;
                public Boolean includeratings;
                public Boolean includecomments;
                public Boolean includecommonapp;
            }

            public static class DeliveryParameters
            {
                public String account;
                public String folder;
            }
        }


        public static class RequestApplicationExportResult
        {
            @Key
            public String message;
            @Key
            public Integer submissions;
            @Key
            public Integer token;
        }

        public static class ExportResultV2
        {
            @Key
            public String status;
            @Key
            public Integer total_files;
            @Key
            public Integer completed_files;
            @Key
            public String[] file_urls;
        }
    }
}




