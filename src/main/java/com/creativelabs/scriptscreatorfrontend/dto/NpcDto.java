package com.creativelabs.scriptscreatorfrontend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NpcDto {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String trelloCardId;
    private String trelloCardUrl;
    private String attachmentUrl;


}