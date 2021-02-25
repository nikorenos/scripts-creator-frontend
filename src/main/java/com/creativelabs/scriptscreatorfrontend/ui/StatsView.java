package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "stats", layout = MainLayout.class)
@PageTitle("Stats | Scripts Creator")
public class StatsView extends VerticalLayout {

    private final ScriptsCreatorClient creatorClient;
    int allNpcs = 0;
    int amountOfLocations = 0;
    List<TrelloListDto> locations = new ArrayList<>();

    public StatsView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;

        addClassName("stats-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(getContent());
    }

    private VerticalLayout getContent() {
        H1 logo = new H1("Stats:");

        allNpcs = creatorClient.getNpcs().size();
        amountOfLocations = creatorClient.getTrelloLists().size();
        locations = creatorClient.getTrelloLists();

        Label labelAllNpcs = new Label("All NPCs in database: " + allNpcs);
        Label labelAmountOfLocations = new Label("All locations in Trello: " + amountOfLocations);
        Label labelLocations = new Label("Available locations in Trello: " + displayLocationFromTrello(locations));
        VerticalLayout content = new VerticalLayout(logo,labelAllNpcs, labelAmountOfLocations, labelLocations);
        content.addClassName("content");
        content.setSizeFull();
        return  content;
    }

    public String displayLocationFromTrello(List<TrelloListDto> lists) {
        StringBuilder names = new StringBuilder();
        for (TrelloListDto list : lists) {
            names.append(list.getName()).append(", ");
        }
        return names.toString();
    }
}
