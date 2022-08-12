package com.objectcomputing.checkins.http;

import io.micronaut.http.MediaType;

public class MediaTypeExtension {

    public static final String APPLICATION_GEO_JSON = "application/geo+json";

    public static final MediaType APPLICATION_GEO_JSON_TYPE = new MediaType(APPLICATION_GEO_JSON);

    public static final String TEXT_TSV = "text/tab-separated-values";

    public static final MediaType TEXT_TSV_TYPE = new MediaType(TEXT_TSV);
}
