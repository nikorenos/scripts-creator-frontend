package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
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

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Route(value = "npcsList", layout = MainLayout.class)
@PageTitle("Npcs | Scripts Creator")
public class NpcView extends VerticalLayout {

    private final NpcForm form = new NpcForm();
    Grid<NpcDto> grid = new Grid<>(NpcDto.class);
    TextField filterText = new TextField();
    private final ScriptsCreatorClient creatorClient;


    public NpcView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form.addListener(NpcForm.SaveEvent.class, this::saveNpc);
        form.addListener(NpcForm.DeleteEvent.class, this::deleteContact);
        form.addListener(NpcForm.CloseEvent.class, e -> closeEditor());

        //Div content = new Div(grid, form);
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteContact(NpcForm.DeleteEvent evt) {
        creatorClient.deleteNpc(evt.getContact().getId());
        updateList();
        closeEditor();
    }

    private void saveNpc(NpcForm.SaveEvent evt) {
        creatorClient.createNpc(evt.getContact());
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
        grid.setColumns("id", "name", "description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

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

}
