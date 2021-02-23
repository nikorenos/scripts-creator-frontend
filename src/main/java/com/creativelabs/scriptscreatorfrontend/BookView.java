package com.creativelabs.scriptscreatorfrontend;


import com.creativelabs.scriptscreatorfrontend.books.Book;
import com.creativelabs.scriptscreatorfrontend.books.BookForm;
import com.creativelabs.scriptscreatorfrontend.books.BookService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.Set;
import java.util.stream.Collectors;


@Route("Test")
public class BookView extends VerticalLayout {

    private BookService bookService = BookService.getInstance();
    private Grid grid = new Grid<>(Book.class);
    private TextField filter = new TextField();

    private BookForm form = new BookForm();

    public BookView() {
        filter.setPlaceholder("Filter by title...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> update());
        grid.setColumns("title", "author", "publicationYear", "type");
        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        mainContent.setSizeFull();
        grid.setSizeFull();

        add(filter, mainContent);
        setSizeFull();
        refresh();
    }
    public void refresh() {
        grid.setItems(bookService.getBooks());
    }

    private void update() {
        grid.setItems(bookService.findByTitle(filter.getValue()));
    }
}