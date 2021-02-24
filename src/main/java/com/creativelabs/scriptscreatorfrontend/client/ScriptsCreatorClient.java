package com.creativelabs.scriptscreatorfrontend.client;

import com.creativelabs.scriptscreatorfrontend.config.ClientConfig;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class ScriptsCreatorClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptsCreatorClient.class);

    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;

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

    public NpcDto getNpc(Long id) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs/" + id)
                .build().encode().toUri();

        try {
            NpcDto npcResponse = restTemplate.getForObject(url, NpcDto.class);
            return (ofNullable(npcResponse).orElse(new NpcDto()));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(),e);
            return new NpcDto();
        }
    }

    public NpcDto createNpc(NpcDto npcDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs")
                .build().encode().toUri();
        return restTemplate.postForObject(url, npcDto, NpcDto.class);
    }

    public void updateNpc(NpcDto npcDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs")
                .build().encode().toUri();
        restTemplate.put(url, npcDto);
    }

    public void deleteNpc(Long id) {
        URI url = UriComponentsBuilder.fromHttpUrl(clientConfig.getBackApiAddress() + "npcs/" + id)
                .build().encode().toUri();
        restTemplate.delete(url);
    }
}
