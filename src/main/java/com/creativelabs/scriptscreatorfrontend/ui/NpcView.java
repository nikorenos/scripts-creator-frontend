package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.books.BookForm;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Route(value = "npcsList", layout = MainLayout.class)
@PageTitle("Contacts | Vaadin CRM")
public class NpcView extends VerticalLayout {

    private NpcForm form = new NpcForm();
    Grid<NpcDto> grid = new Grid<>(NpcDto.class);
    TextField filterText = new TextField();
    @Autowired
    private final ScriptsCreatorClient creatorClient;


    public NpcView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;
        addClassName("list-view");
        setSizeFull();
        configureGrid();


        /*form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());*/

        //Div content = new Div(grid, form);
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
        /*
        grid.setColumns("title", "author", "publicationYear", "type");
        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        mainContent.setSizeFull();
        grid.setSizeFull();

        add(filter, mainContent);
        setSizeFull();
        refresh();
        */
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> filterNpc());

        Button addContactButton = new Button("Add contact", click -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();

    }

    private void configureGrid() {
        grid.addClassName("npc-grid");
        grid.setSizeFull();
        grid.setColumns("name", "description");


        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }

    private void updateList() {
        grid.setItems(creatorClient.getNpcs());
    }
    private void filterNpc() {
        grid.setItems(findNpcByName(filterText.getValue()));
    }

    private void closeEditor() {

    }

    public Set findNpcByName(String name) {
        return creatorClient.getNpcs().stream().filter(npc -> npc.getName().contains(name)).collect(Collectors.toSet());
    }

}
