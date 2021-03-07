package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloCardDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Route(value = "npcsList", layout = MainLayout.class)
@PageTitle("Npcs | Scripts Creator")
public class NpcView extends VerticalLayout {

    NpcForm form;
    Grid<NpcDto> grid = new Grid<>(NpcDto.class);
    TextField filterText = new TextField();
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
        TrelloCardDto card;
        if (evt.getNpc().getTrelloCardId() != null) {
            card = prepareTrelloCard(evt.getNpc());
            String cardId = evt.getNpc().getTrelloCardId();
            creatorClient.updateTrelloCard(cardId, card);
            if (evt.getNpc().getAttachmentUrl() != "") {
                creatorClient.createTrelloCardAttachment(cardId, evt.getNpc().getAttachmentUrl());
            }
        } else {
            card = creatorClient.createTrelloCard(prepareTrelloCard(evt.getNpc()));
            evt.getNpc().setTrelloCardId(card.getId());
            evt.getNpc().setTrelloCardUrl(card.getShortUrl());
            if (evt.getNpc().getAttachmentUrl() != "") {
                creatorClient.createTrelloCardAttachment(card.getId(), evt.getNpc().getAttachmentUrl());
            }
        }

        creatorClient.createNpc(evt.getNpc());
        updateList();
        closeEditor();
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
        grid.setColumns("id", "name", "description", "location", "trelloCardUrl");
        grid.getColumns().forEach(col -> col.setWidth("10px"));

        grid.asSingleSelect().addValueChangeListener(evt -> editNpc(evt.getValue()));
    }

    private void editNpc(NpcDto npcDto) {
        if (npcDto == null) {
            closeEditor();
        } else {
            form.setNpc(npcDto);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void updateList() {
        grid.setItems(creatorClient.getNpcs());
    }
    private void filterNpc() {
        grid.setItems(findNpcByName(filterText.getValue()));
    }

    private void closeEditor() {
        form.setNpc(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public Set findNpcByName(String name) {
        return creatorClient.getNpcs().stream().filter(npc -> npc.getName().contains(name)).collect(Collectors.toSet());
    }

    public TrelloCardDto prepareTrelloCard(NpcDto npcDto) {
        HashMap<String, String> trelloListsMap = new HashMap<>();
        List<TrelloListDto> trelloLists = creatorClient.getTrelloLists();
        for (TrelloListDto list : trelloLists) {
            trelloListsMap.put(list.getName(), list.getId());

        }
        List<String> list = trelloListsMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(npcDto.getLocation()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        String idList = list.get(0);
        return new TrelloCardDto(npcDto.getName(), npcDto.getDescription(), "bottom", idList);
    }

}
