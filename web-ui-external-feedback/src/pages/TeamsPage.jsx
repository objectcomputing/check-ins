import React from 'react';
import TeamResults from '../components/team-results/TeamResults';

const displayName = 'TeamsPage';

const TeamsPage = () => {
  return (
    <div className="teams-page">
      <TeamResults />
    </div>
  );
};

TeamsPage.displayName = displayName;

export default TeamsPage;
