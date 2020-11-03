import React from 'react';
import TeamResults from '../components/team-results/TeamResults';
import TeamsActions from '../components/team-results/TeamsActions';

const displayName = "TeamsPage";

const TeamsPage = () => {
    return (
        <div className="teamEntities-page">
            <TeamsActions />
            <TeamResults />
        </div>
    );
};

TeamsPage.displayName = displayName;

export default TeamsPage;