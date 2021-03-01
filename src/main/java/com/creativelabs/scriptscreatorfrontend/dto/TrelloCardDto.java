package com.creativelabs.scriptscreatorfrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloCardDto {
    private String id;
    private String name;
    private String desc;
    private String pos;
    private String shortUrl;
    private String idList;

    public TrelloCardDto(String name, String desc, String pos, String idList) {
        this.name = name;
        this.desc = desc;
        this.pos = pos;
        this.idList = idList;
    }
}