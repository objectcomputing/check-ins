import React, { useContext } from 'react';

import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api';
import { selectProfile, selectSkill } from '../../context/selectors';

import {
  Avatar,
  Card,
  CardHeader,
  Chip,
  List,
  ListItem,
  Typography
} from '@mui/material';

const SearchResults = ({ searchResults }) => {
  const { state } = useContext(AppContext);

  const getMemberProfile = member => selectProfile(state, member.id);

  const chip = skill => {
    let level = skill.level;
    let skillLevel = level.charAt(0) + level.slice(1).toLowerCase();
    let mappedSkill = selectSkill(state, skill.id);
    let chipLabel = mappedSkill.name + ' - ' + skillLevel;
    return <Chip label={chipLabel}></Chip>;
  };

  return (
    <div className="results-section">
      <List>
        {searchResults.map((member, i) => {
          return (
            <div key={`search-result-${i}`}>
              <Card className={'member-skills-card'} key={`card-${member?.id}`}>
                <CardHeader
                  title={
                    <Typography variant="h5" component="h2">
                      {getMemberProfile(member)?.name || ''}
                    </Typography>
                  }
                  subheader={
                    <Typography color="textSecondary" component="h3">
                      {getMemberProfile(member)?.title || ''}
                    </Typography>
                  }
                  disableTypography
                  avatar={
                    <Avatar
                      className={'large'}
                      src={getAvatarURL(
                        getMemberProfile(member)?.workEmail || ''
                      )}
                    />
                  }
                />
                <ListItem key={`member-${member?.id}`}>
                  {member.skills.map((skill, i) => {
                    return <div key={`${member?.id}-${i}`}>{chip(skill)}</div>;
                  })}
                </ListItem>
              </Card>
            </div>
          );
        })}
      </List>
    </div>
  );
};

export default SearchResults;
