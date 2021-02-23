package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.dto.NpcDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route(value = "npcForm", layout = MainLayout.class)
public class NpcForm extends FormLayout {

    TextField name = new TextField("Name");
    TextField description = new TextField("Description");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");


    //Binder<NpcDto> binder = new BeanValidationBinder<>(NpcDto.class);
    Binder<NpcDto> binder = new Binder<>(NpcDto.class);
    private NpcDto npcDto;

    public NpcForm() {
        addClassName("npc-form");
        binder.bindInstanceFields(this);

        add(
                name,
                description,
                createButtonsLayout()
        );
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

    public void setNpcDto(NpcDto npcDto) {
        this.npcDto = npcDto;
        //binder.readBean(npcDto);
    }

    private void validateAndSave() {

        /*try {
            binder.writeBean(npcDto);
            fireEvent(new NpcFormEvent.SaveEvent(this, npcDto));
        } catch (ValidationException e) {
            e.printStackTrace();
        }*/
    }

    // Events
    public static abstract class NpcFormEvent extends ComponentEvent<NpcForm> {
        private NpcDto npcDto;

        protected NpcFormEvent(NpcForm source, NpcDto npcDto) {
            super(source, false);
            this.npcDto = npcDto;
        }

        public static class SaveEvent extends NpcFormEvent {
            SaveEvent(NpcForm source, NpcDto npc) {
                super(source, npc);
            }
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