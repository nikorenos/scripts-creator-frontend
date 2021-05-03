package com.creativelabs.scriptscreatorfrontend.ui;

import com.creativelabs.scriptscreatorfrontend.MainLayout;
import com.creativelabs.scriptscreatorfrontend.client.ScriptsCreatorClient;
import com.creativelabs.scriptscreatorfrontend.dto.CampDto;
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
    List<CampDto> locations = new ArrayList<>();

    public StatsView(ScriptsCreatorClient creatorClient) {
        this.creatorClient = creatorClient;

        addClassName("stats-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(getContent());
    }

    private VerticalLayout getContent() {
        H1 logo = new H1("Stats:");

        allNpcs = creatorClient.getNpcs().size();
        amountOfLocations = creatorClient.getCamps().size();
        locations = creatorClient.getCamps();

        Label labelAllNpcs = new Label("Amount of NPCs: " + allNpcs);
        Label labelAmountOfLocations = new Label("Amount of camps: " + amountOfLocations);
        Label labelLocations = new Label("Created camps with NPCs: " + displayLocations(locations));
        VerticalLayout content = new VerticalLayout(logo,labelAllNpcs, labelAmountOfLocations, labelLocations);
        content.addClassName("content");
        content.setSizeFull();
        return  content;
    }

    public String displayLocations(List<CampDto> lists) {
        String campsList = "";
        for (CampDto list : lists) {
            campsList = campsList + list.getName() + "(" + list.getNpcList().size() + ")" + ", ";
        }
        return campsList.substring(0, campsList.length()-2) + ".";
    }
}
