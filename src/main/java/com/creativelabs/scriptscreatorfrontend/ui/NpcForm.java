package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.CampDto;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.*;

@Route(value = "npcForm", layout = MainLayout.class)
public class NpcForm extends FormLayout {

    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    ComboBox<String> location = new ComboBox<>("Location");
    TextField attachmentUrl = new TextField("Image Url");
    List<String> locations = new ArrayList<>();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<NpcDto> binder = new Binder<>(NpcDto.class);
    private NpcDto npcDto;

    public NpcForm(ScriptsCreatorClient creatorClient) {
        addClassName("npc-form");
        binder.bindInstanceFields(this);
        List<CampDto> campsList = creatorClient.getCamps();
        for (CampDto camp : campsList) {
            locations.add(camp.getName());
        }

        location.setItems(locations);
        location.setItemLabelGenerator(String::toString);

        add(
                name,
                description,
                location,
                attachmentUrl,
                createButtonsLayout()
        );
    }

    public void setNpc(NpcDto npcDto) {
        this.npcDto = npcDto;
        binder.readBean(npcDto);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, npcDto)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(npcDto);
            fireEvent(new SaveEvent(this, npcDto));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class NpcFormEvent extends ComponentEvent<NpcForm> {
        private final NpcDto npcDto;

        protected NpcFormEvent(NpcForm source, NpcDto npcDto) {
            super(source, false);
            this.npcDto = npcDto;
        }

        public NpcDto getNpc() {
            return npcDto;
        }
    }

    public static class SaveEvent extends NpcFormEvent {
            SaveEvent(NpcForm source, NpcDto npc) {
                super(source, npc);
            }
    }


    public static class DeleteEvent extends NpcFormEvent {
        DeleteEvent(NpcForm source, NpcDto npcDto) {
            super(source, npcDto);
        }

    }

    public static class CloseEvent extends NpcFormEvent {
        CloseEvent(NpcForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}