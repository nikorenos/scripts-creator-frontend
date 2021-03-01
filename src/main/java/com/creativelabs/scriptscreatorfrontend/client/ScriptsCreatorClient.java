package com.creativelabs.scriptscreatorfrontend.client;

import com.creativelabs.scriptscreatorfrontend.config.ClientConfig;
import com.creativelabs.scriptscreatorfrontend.config.TrelloConfig;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloCardDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class ScriptsCreatorClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptsCreatorClient.class);

    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;
    private final TrelloConfig trelloConfig;

    public List<NpcDto> getNpcs() {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs")
                .build().encode().toUri();
        try {
            NpcDto[] boardsResponse = restTemplate.getForObject(url, NpcDto[].class);
            return Arrays.asList(ofNullable(boardsResponse).orElse(new NpcDto[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    public NpcDto createNpc(NpcDto npcDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs")
                .build().encode().toUri();
        return restTemplate.postForObject(url, npcDto, NpcDto.class);
    }

    public void deleteNpc(Long id) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs/" + id)
                .build().encode().toUri();
        restTemplate.delete(url);
    }
    public List<TrelloListDto> getTrelloLists() {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "trello/boards/" +
                trelloConfig.getBoardId() + "/lists")
                .build().encode().toUri();
        try {
            TrelloListDto[] boardsResponse = restTemplate.getForObject(url, TrelloListDto[].class);
            return Arrays.asList(ofNullable(boardsResponse).orElse(new TrelloListDto[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    public TrelloCardDto createTrelloCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "trello")
                .build().encode().toUri();
        return restTemplate.postForObject(url, trelloCardDto, TrelloCardDto.class);
    }

    public void updateTrelloCard(String cardId, TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "trello" + "/cards/" + cardId)
                .build().encode().toUri();
        restTemplate.put(url, trelloCardDto);
    }
}
