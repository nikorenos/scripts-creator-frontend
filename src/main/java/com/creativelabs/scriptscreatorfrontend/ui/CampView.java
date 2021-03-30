package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.CampDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Route(value = "campList", layout = MainLayout.class)
@PageTitle("Camps | Scripts Creator")
public class CampView extends VerticalLayout {

    CampForm form;
    Grid<CampDto> grid = new Grid<>(CampDto.class);
    TextField filterText = new TextField();
    Image image = new Image("", "Camp");
    private final ScriptsCreatorClient creatorClient;


    public CampView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new CampForm(creatorClient);
        form.addListener(CampForm.SaveEvent.class, this::saveCamp);
        form.addListener(CampForm.DeleteEvent.class, this::deleteCamp);
        form.addListener(CampForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteCamp(CampForm.DeleteEvent evt) {
        if (evt.getCamp().getTrelloListId() != null) {
            creatorClient.deleteTrelloCard(evt.getCamp().getTrelloListId());
        }
        creatorClient.deleteCamp(evt.getCamp().getId());
        updateList();
        closeEditor();
    }

    private void saveCamp(CampForm.SaveEvent evt) {
        TrelloListDto list = manageTrelloList(evt);
        evt.getCamp().setTrelloListId(list.getId());
        creatorClient.createCamp(evt.getCamp());
        updateList();
        closeEditor();
    }

    private TrelloListDto manageTrelloList(CampForm.SaveEvent evt) {
        TrelloListDto list = new TrelloListDto(evt.getCamp().getName());
        return creatorClient.createTrelloList(list);
    }


    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> filterCamp());

        Button addCampButton = new Button("Add camp", click -> addCamp());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addCampButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addCamp() {
        grid.asSingleSelect().clear();
        editCamp(new CampDto());
    }

    private void configureGrid() {
        grid.addClassName("camp-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name", "description", "trelloListId");
        grid.getColumns().forEach(col -> col.setWidth("10px"));

        grid.asSingleSelect().addValueChangeListener(evt -> editCamp(evt.getValue()));
    }

    private void editCamp(CampDto campDto) {
        if (campDto == null) {
            closeEditor();
        } else {
            if (campDto.getAttachmentUrl() != null) {
                image.setSrc(campDto.getAttachmentUrl());
                image.setMaxWidth("300px");
                image.setMaxHeight("500px");
                form.add(image);
                if (campDto.getAttachmentUrl().equals("")) {
                    form.remove(image);
                }
            }

            form.setCamp(campDto);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void updateList() {
        grid.setItems(creatorClient.getCamps());
    }
    private void filterCamp() {
        grid.setItems(findCampByName(filterText.getValue()));
    }

    private void closeEditor() {
        form.remove(image);
        form.setCamp(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public Set findCampByName(String name) {
        return creatorClient.getCamps().stream().filter(camp -> camp.getName().contains(name)).collect(Collectors.toSet());
    }
}
