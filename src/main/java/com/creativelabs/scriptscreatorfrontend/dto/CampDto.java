package com.creativelabs.scriptscreatorfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampDto {
    private Long id;
    private String name;
    private String description;
    private String attachmentUrl;
    private String trelloListId;
    private List<NpcDto> npcList;
}