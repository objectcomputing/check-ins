import React from 'react';
import GuildResults from '../components/guild-results/GuildResults';

const displayName = 'GuildsPage';

const GuildsPage = () => {
  return (
    <div className="guilds-page">
      <GuildResults />
    </div>
  );
};

GuildsPage.displayName = displayName;

export default GuildsPage;
