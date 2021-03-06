package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
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
        Image image = new Image("https://trello-attachments.s3.amazonaws.com/60186a77ae9c0772451ea88e/60186a9af0ca1303489ad2fe/d1821249016a928100ece048ec6c94f3/avatar-avallach3.jpg", "NpcImage");
        image.setMaxHeight("50");
        image.setMaxWidth("50");
        VerticalLayout content = new VerticalLayout(logo,labelAllNpcs, labelAmountOfLocations, labelLocations, image);
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
