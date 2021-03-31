package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.CampDto;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloCardDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Route(value = "npcsList", layout = MainLayout.class)
@PageTitle("Npcs | Scripts Creator")
public class NpcView extends VerticalLayout {

    NpcForm form;
    Grid<NpcDto> grid = new Grid<>(NpcDto.class);
    TextField filterText = new TextField();
    Image image = new Image("", "NPC");
    private final ScriptsCreatorClient creatorClient;


    public NpcView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new NpcForm(creatorClient);
        form.addListener(NpcForm.SaveEvent.class, this::saveNpc);
        form.addListener(NpcForm.DeleteEvent.class, this::deleteNpc);
        form.addListener(NpcForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteNpc(NpcForm.DeleteEvent evt) {
        if (evt.getNpc().getTrelloCardId() != null) {
            creatorClient.deleteTrelloCard(evt.getNpc().getTrelloCardId());
        }
        creatorClient.deleteNpc(evt.getNpc().getId());
        updateList();
        closeEditor();
    }

    private void saveNpc(NpcForm.SaveEvent evt) {
        //manageTrelloCard(evt);
        creatorClient.createNpc(setNpcCampId(evt));
        updateList();
        closeEditor();
    }

    private NpcDto setNpcCampId(NpcForm.SaveEvent evt) {
        List<CampDto> filteredCamp;
        NpcDto npc = evt.getNpc();
        String location = npc.getLocation();

        filteredCamp = creatorClient.getCamps().stream()
                .filter(n -> n.getName().equals(location))
                .collect(Collectors.toList());
        npc.setCampId(filteredCamp.get(0).getId());

        return npc;
    }

    private void manageTrelloCard(NpcForm.SaveEvent evt) {
        TrelloCardDto card;
        if (evt.getNpc().getTrelloCardId() == null) {
            card = creatorClient.createTrelloCard(prepareTrelloCard(evt.getNpc()));
            evt.getNpc().setTrelloCardId(card.getId());
            evt.getNpc().setTrelloCardUrl(card.getShortUrl());
            if (!evt.getNpc().getAttachmentUrl().equals("")) {
                manageTrelloCardAttachment(card.getId(), evt.getNpc().getAttachmentUrl());
            }
        } else {
            card = prepareTrelloCard(evt.getNpc());
            String cardId = evt.getNpc().getTrelloCardId();
            creatorClient.updateTrelloCard(cardId, card);
            if (!evt.getNpc().getAttachmentUrl().equals("")) {
                manageTrelloCardAttachment(cardId, evt.getNpc().getAttachmentUrl());
            }
        }
    }

    private void manageTrelloCardAttachment(String cardId, String attachmentUrl) {
        creatorClient.createTrelloCardAttachment(cardId, attachmentUrl);
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> filterNpc());

        Button addNpcButton = new Button("Add npc", click -> addNpc());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addNpcButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addNpc() {
        grid.asSingleSelect().clear();
        editNpc(new NpcDto());
    }

    private void configureGrid() {
        grid.addClassName("npc-grid");
        grid.setSizeFull();
        grid.setColumns("id", "scriptId", "name", "description", "campId", "location", "trelloCardUrl");
        grid.getColumns().forEach(col -> col.setWidth("10px"));

        grid.asSingleSelect().addValueChangeListener(evt -> editNpc(evt.getValue()));
    }

    private void editNpc(NpcDto npcDto) {
        if (npcDto == null) {
            closeEditor();
        } else {
            if (npcDto.getAttachmentUrl() != null) {
                image.setSrc(npcDto.getAttachmentUrl());
                image.setMaxWidth("300px");
                image.setMaxHeight("500px");
                form.add(image);
                if (npcDto.getAttachmentUrl().equals("")) {
                    form.remove(image);
                }
            }

            form.setNpc(npcDto);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void updateList() {
        grid.setItems(creatorClient.getNpcs());
    }

    public void setNpcLocation() {
        List<NpcDto> npcList = creatorClient.getNpcs();
        for (NpcDto npc: npcList) {
            Long campId = npc.getCampId();
            List<CampDto> filteredCamp = creatorClient.getCamps().stream()
                    .filter(n -> n.getId() == campId)
                    .collect(Collectors.toList());
            npc.setLocation(filteredCamp.get(0).getName());
        }
    }
    private void filterNpc() {
        grid.setItems(findNpcByName(filterText.getValue()));
    }

    private void closeEditor() {
        form.remove(image);
        form.setNpc(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public Set findNpcByName(String name) {
        return creatorClient.getNpcs().stream().filter(npc -> npc.getName().contains(name)).collect(Collectors.toSet());
    }

    public TrelloCardDto prepareTrelloCard(NpcDto npcDto) {
        List<CampDto> filteredCamp = new ArrayList<>();
        String location = npcDto.getLocation();
        Long campId = npcDto.getCampId();
        filteredCamp = creatorClient.getCamps().stream()
                    .filter(n -> n.getName().equals(location))
                    .collect(Collectors.toList());

        System.out.println("filteredCamp : " + filteredCamp);
        System.out.println("filteredCamp : " + filteredCamp.get(0).getTrelloListId());

        return new TrelloCardDto(npcDto.getName(), npcDto.getDescription(), "bottom", filteredCamp.get(0).getTrelloListId());

        /*
        HashMap<String, String> trelloListsMap = new HashMap<>();
        List<TrelloListDto> trelloLists = creatorClient.getTrelloLists();
        for (TrelloListDto list : trelloLists) {
            trelloListsMap.put(list.getName(), list.getId());

        }
        List<String> list = trelloListsMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals("npcDto.getLocation()"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        String idList = list.get(0);
        return new TrelloCardDto(npcDto.getName(), npcDto.getDescription(), "bottom", idList);*/
    }

}
