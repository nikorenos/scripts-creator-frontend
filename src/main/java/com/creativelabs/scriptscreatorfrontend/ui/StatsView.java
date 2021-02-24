package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.TrelloListDto;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PageTitle("Stats | Scripts Creator")
@Route(value = "stats", layout = MainLayout.class)
public class StatsView extends VerticalLayout {

    @Autowired
    private final ScriptsCreatorClient creatorClient;


    public StatsView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;

        addClassName("stats-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        H1 logo = new H1("Stats:");
        int allNpcs = creatorClient.getNpcs().size();
        int amountOfLocations = creatorClient.getTrelloLists().size();
        List<TrelloListDto> locations = creatorClient.getTrelloLists();


        Label label1 = new Label("All NPCs in database: " + allNpcs);
        Label label2 = new Label("All locations in Trello: " + amountOfLocations);
        Label label3 = new Label("Available locations in Trello: " + displayLocationFromTrello(locations));

        VerticalLayout content = new VerticalLayout(logo,label1, label2, label3);
        content.addClassName("content");
        content.setSizeFull();

        add(content);

    }

    public String displayLocationFromTrello(List<TrelloListDto> lists) {
        String names = "";
        for (TrelloListDto list : lists) {
            names = names + list.getName() + ", ";
        }
        return names;
    }
}
