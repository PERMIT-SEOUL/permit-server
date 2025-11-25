package com.permitseoul.permitserver.global.external.notion;

import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.external.notion.client.NotionClient;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.exception.NotFoundNotionResponseException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class NotionProvider {
    private final NotionClient notionClient;
    private final NotionProperties notionProperties;
    private final String notionDatasourceAuthorizationHeader;

    public NotionProvider(NotionClient notionClient, NotionProperties notionProperties) {
        this.notionClient = notionClient;
        this.notionProperties = notionProperties;
        this.notionDatasourceAuthorizationHeader = Constants.BEARER + notionProperties.privateApiToken();
    }

    public NotionTimetableDatasourceResponse getNotionTimetableDatasource(final String notionTimetableDatasourceId) {
        final NotionTimetableDatasourceResponse response = notionClient.getNotionTimetableDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionTimetableDatasourceId
        );
        if (response.results().isEmpty()) {
            throw new NotFoundNotionResponseException();
        }
        return response;
    }

    public NotionStageDatasourceResponse getNotionStageDatasource(final String notionStageDatasourceId) {
        final NotionStageDatasourceResponse response = notionClient.getNotionStageDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionStageDatasourceId
        );
        if (response.results().isEmpty()) {
            throw new NotFoundNotionResponseException();
        }
        return response;
    }

    public NotionCategoryDatasourceResponse getNotionCategoryDatasource(final String notionCategoryDatasourceId) {
        final NotionCategoryDatasourceResponse response = notionClient.getNotionCategoryDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionCategoryDatasourceId
        );
        if (response.results().isEmpty()) {
            throw new NotFoundNotionResponseException();
        }
        return response;
    }
}
