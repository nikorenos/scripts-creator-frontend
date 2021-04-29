package com.creativelabs.scriptscreatorfrontend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NpcDto {
    private Long id;
    private Integer scriptId;
    private String name;
    private String description;
    private String location;
    private Long campId;
    private String trelloCardId;
    private String trelloCardUrl;
    private String attachmentUrl;
}