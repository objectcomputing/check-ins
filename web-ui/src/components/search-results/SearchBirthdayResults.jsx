import React, { useContext } from "react";

import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import { selectProfile, selectSkill } from "../../context/selectors";

import {
  Avatar,
  Card,
  CardHeader,
  Chip,
  List,
  ListItem,
  Typography,
} from "@material-ui/core";

const SearchBirthdayResults = ({ searchResults }) => {
  const { state } = useContext(AppContext);

  const getMemberProfile = (member) => selectProfile(state, member.userId);
  console.log(getMemberProfile);

  const chip = (skill) => {
    let level = skill.level;
    let skillLevel = level.charAt(0) + level.slice(1).toLowerCase();
    let mappedSkill = selectSkill(state, skill.id);
    let chipLabel = mappedSkill.name + " - " + skillLevel;
    return <Chip label={chipLabel}></Chip>;
  };

  return (
    <div className="results-section">
      <List>
        {searchResults === undefined ? (
          <div />
        ) : (
          searchResults.map((member, index) => {
            let profile=getMemberProfile(member);
            return (
              <Card className={"member-skills-card"} key={`card-${member.userId}`}>
                <CardHeader
                  title={
                    <Typography variant="h5" component="h2">
                      {getMemberProfile(member).name || ""}
                    </Typography>
                  }
                  subheader={
                    <Typography color="textSecondary" component="h3">
                      {getMemberProfile(member).title || ""}
                    </Typography>
                  }
//                   subheader={
//                     <Typography color="textSecondary" component="h4">
//                       {getMemberProfile(member).anniversary || ""}
//                     </Typography>
//                   }
//                   subheader={
//                     <Typography color="textSecondary" component="h4">
//                       {getMemberProfile(member).anniversary || ""}
//                     </Typography>
//                   }
                  disableTypography
                  avatar={
                    <Avatar
                      className={"large"}
                      src={getAvatarURL(
                        getMemberProfile(member).workEmail || ""
                      )}
                    />
                  }
                />
{/*                 <ListItem key={`member-${member.userId}`}> */}
{/*                   {member.skills.map((skill, index) => { */}
{/*                     return <div key={member.id}>{chip(skill)}</div>; */}
{/*                   })} */}
{/*                 </ListItem> */}
              </Card>
            );
          })
        )}
      </List>
    </div>
  );
};

export default SearchBirthdayResults;
