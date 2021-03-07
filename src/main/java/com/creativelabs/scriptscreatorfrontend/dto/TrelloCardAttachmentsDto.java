package com.creativelabs.scriptscreatorfrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloCardAttachmentsDto {
    private String id;
    private String name;
    private String bytes;
    private String url;
    private String filename;
}