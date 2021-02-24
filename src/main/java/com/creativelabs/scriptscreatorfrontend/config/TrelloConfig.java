package com.creativelabs.scriptscreatorfrontend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TrelloConfig {
    @Value("${trello.boardId}")
    private String boardId;
}
