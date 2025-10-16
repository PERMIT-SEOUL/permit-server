package com.permitseoul.permitserver.global.external.notion.client;

import com.permitseoul.permitserver.global.config.FeignConfig;
import com.permitseoul.permitserver.global.external.notion.dto.NotionCategoryDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionStageDatasourceResponse;
import com.permitseoul.permitserver.global.external.notion.dto.NotionTimetableDatasourceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "notionClient",
        url = "https://api.notion.com",
        configuration = FeignConfig.class
)
public interface NotionClient {

    //노션 타임테이블 데이터소스 조회
    @PostMapping(value = "/v1/data_sources/{dataSourceId}/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    NotionTimetableDatasourceResponse getNotionTimetableDatasource(
            @RequestHeader("Authorization") final String authorization,
            @RequestHeader("Notion-Version") final String notionVersion,
            @RequestHeader("Content-Type") final String contentType,
//            @RequestHeader("Accept") final String accept,
            @PathVariable("dataSourceId") final String dataSourceId
    );

    //노션 category 데이터소스 조회
    @PostMapping(value = "/v1/data_sources/{dataSourceId}/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    NotionCategoryDatasourceResponse getNotionCategoryDatasource(
            @RequestHeader("Authorization") final String authorization,
            @RequestHeader("Notion-Version") final String notionVersion,
            @RequestHeader("Content-Type") final String contentType,
//            @RequestHeader("Accept") final String accept,
            @PathVariable("dataSourceId") final String dataSourceId
    );

    //노션 stage 데이터소스 조회
    @PostMapping(value = "/v1/data_sources/{dataSourceId}/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    NotionStageDatasourceResponse getNotionStageDatasource(
            @RequestHeader("Authorization") final String authorization,
            @RequestHeader("Notion-Version") final String notionVersion,
            @RequestHeader("Content-Type") final String contentType,
//            @RequestHeader("Accept") final String accept,
            @PathVariable("dataSourceId") final String dataSourceId
    );


}
