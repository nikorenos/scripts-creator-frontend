package com.creativelabs.scriptscreatorfrontend.books;

import com.creativelabs.scriptscreatorfrontend.BookType;
import com.creativelabs.scriptscreatorfrontend.BookView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class BookForm extends FormLayout {
    private TextField title = new TextField("Title");
    private TextField author = new TextField("Author");
    private TextField publicationYear = new TextField("Publication year");
    //private ComboBox type = new ComboBox<>("Book type");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    //Binder<Book> binder = new BeanValidationBinder<>(Book.class);
    Binder<Book> binder = new Binder<>(Book.class);
    BookService service;
    BookView bookView;
    Book book;

    public BookForm() {
        binder.bindInstanceFields(this);
        save.addClickListener(event -> save());
        delete.addClickListener(event -> delete());

        //type.setItems(BookType.values());
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(title, author, publicationYear, buttons);
    }

    private void save() {
        Book book = binder.getBean();
        service.save(book);
        bookView.refresh();
        setBook(null);
    }

    private void delete() {
        Book book = binder.getBean();
        service.delete(book);
        bookView.refresh();
        setBook(null);
    }

    public void setBook(Book book) {
        binder.setBean(book);

        if (book == null) {
            setVisible(false);
        } else {
            setVisible(true);
            title.focus();
        }
    }

}