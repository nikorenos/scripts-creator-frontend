package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.CampDto;
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

@Route(value = "campForm", layout = MainLayout.class)
public class CampForm extends FormLayout {

    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    TextField attachmentUrl = new TextField("Image Url");
    List<String> locations = new ArrayList<>();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<CampDto> binder = new Binder<>(CampDto.class);
    private CampDto campDto;

    public CampForm(ScriptsCreatorClient creatorClient) {
        addClassName("camp-form");
        binder.bindInstanceFields(this);
        List<TrelloListDto> locationList = creatorClient.getTrelloLists();
        for (TrelloListDto list : locationList) {
            locations.add(list.getName());
        }

        add(
                name,
                description,
                attachmentUrl,
                createButtonsLayout()
        );
    }

    public void setCamp(CampDto campDto) {
        this.campDto = campDto;
        binder.readBean(campDto);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, campDto)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(campDto);
            fireEvent(new SaveEvent(this, campDto));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class CampFormEvent extends ComponentEvent<CampForm> {
        private final CampDto campDto;

        protected CampFormEvent(CampForm source, CampDto campDto) {
            super(source, false);
            this.campDto = campDto;
        }

        public CampDto getCamp() {
            return campDto;
        }
    }

    public static class SaveEvent extends CampFormEvent {
        SaveEvent(CampForm source, CampDto camp) {
            super(source, camp);
        }
    }


    public static class DeleteEvent extends CampFormEvent {
        DeleteEvent(CampForm source, CampDto campDto) {
            super(source, campDto);
        }

    }

    public static class CloseEvent extends CampFormEvent {
        CloseEvent(CampForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}