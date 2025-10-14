package com.permitseoul.permitserver.global.external.notion;

import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.external.notion.client.NotionClient;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
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
        return notionClient.getNotionTimetableDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionTimetableDatasourceId
        );
    }

    public NotionStageDatasourceResponse getNotionStageDatasource(final String notionStageDatasourceId) {
        return notionClient.getNotionStageDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionStageDatasourceId
        );
    }

    public NotionCategoryDatasourceResponse getNotionCategoryDatasource(final String notionCategoryDatasourceId) {
        return notionClient.getNotionCategoryDatasource(
                notionDatasourceAuthorizationHeader,
                notionProperties.notionVersion(),
                MediaType.APPLICATION_JSON_VALUE,
                notionCategoryDatasourceId
        );
    }
}
