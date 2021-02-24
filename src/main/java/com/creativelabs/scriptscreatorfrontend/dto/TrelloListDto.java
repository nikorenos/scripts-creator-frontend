package com.creativelabs.scriptscreatorfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrelloListDto {
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
