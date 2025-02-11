package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.slack.kudos.AutomatedKudos;
import com.objectcomputing.checkins.services.slack.kudos.AutomatedKudosRepository;

import java.util.ArrayList;
import java.util.List;

public interface AutomatedKudosFixture extends RepositoryFixture {
    default List<AutomatedKudos> getAutomatedKudos() {
        List<AutomatedKudos> list = new ArrayList<>();
        getAutomatedKudosRepository().findAll().forEach(list::add);
        return list;
    }
}
