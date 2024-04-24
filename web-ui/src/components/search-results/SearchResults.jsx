import React, { useContext, useEffect } from "react";

import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import { selectProfile, selectSkill } from "../../context/selectors";
import { isArrayPresent } from '../../helpers/checks';

import {
  Avatar,
  Card,
  CardHeader,
  Chip,
  List,
  ListItem,
  Typography,
} from "@mui/material";

const SearchResults = ({ searchResults }) => {
  const { state } = useContext(AppContext);

  function sortMembersBySkill() {
    // testing results temporarily - this will change
    let newSearchResults = [
      {
        "id": "72655c4f-1fb8-4514-b31e-7f7e19fa9bd7",
        "name": "CigBoss2",
        "skills": [
          {
            "id": "6b56f0aa-09aa-4b09-bb81-03481af7e49f",
            "level": "INTERESTED"
          },
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "EXPERT"
          }
        ]
      },
      {
        "id": "72655c4f-1fb8-4514-b31e-7f7e19fa9bd7",
        "name": "BigBoss",
        "skills": [
          {
            "id": "6b56f0aa-09aa-4b09-bb81-03481af7e49f",
            "level": "ADVANCED"
          },
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "EXPERT"
          }
        ]
      },
      {
        "id": "72655c4f-1fb8-4514-b31e-7f7e19fa9bd7",
        "name": "BigBoss2",
        "skills": [
          {
            "id": "6b56f0aa-09aa-4b09-bb81-03481af7e49f",
            "level": "INTERESTED"
          },
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "EXPERT"
          }
        ]
      },
      {
        "id": "6207b3fd-042d-49aa-9e28-dcc04f537c2d",
        "name": "Michael Kimberlin",
        "skills": [
          {
            "id": "756cdfff-3e56-4475-85c5-840fb3c8f7d1",
            "level": "INTERMEDIATE"
          },
          {
            "id": "905b7c02-c8d1-4f46-8805-c65affcd9b72",
            "level": "NOVICE"
          },
          {
            "id": "6bee81b4-2962-46ec-838d-929180387a7a",
            "level": "INTERMEDIATE"
          },
          {
            "id": "23edd7d6-2347-4d0c-a520-5f4c363c59a2",
            "level": "ADVANCED"
          }
        ]
      },
      {
        "id": "6207b3fd-042d-49aa-9e28-dcc04f537c2d",
        "name": "Michael Kimberlin",
        "skills": [
          {
            "id": "756cdfff-3e56-4475-85c5-840fb3c8f7d1",
            "level": "INTERMEDIATE"
          },
          {
            "id": "905b7c02-c8d1-4f46-8805-c65affcd9b72",
            "level": "INTERMEDIATE"
          },
          {
            "id": "6bee81b4-2962-46ec-838d-929180387a7a",
            "level": "INTERMEDIATE"
          },
          {
            "id": "23edd7d6-2347-4d0c-a520-5f4c363c59a2",
            "level": "INTERMEDIATE"
          }
        ]
      },
      {
        "id": "105f2968-a182-45a3-892c-eeff76383fe0",
        "name": "Bevolver Ocelot",
        "skills": [
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "INTERMEDIATE"
          }
        ]
      },
      {
        "id": "105f2968-a182-45a3-892c-eeff76383fe0",
        "name": "Revolver Ocelot",
        "skills": [
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "INTERMEDIATE"
          }
        ]
      },
      {
        "id": "105f2968-a182-45a3-892c-eeff76383fe0",
        "name": "Revolver Ocelot",
        "skills": [
          {
            "id": "f057af45-e627-499c-8a71-1e6b4ab2fcd2",
            "level": "ADVANCED"
          }
        ]
      }
    ];

    // TODO: Add a type of mapped sorting that cycles through these options
    const skillLevelsOrder = ["EXPERT", "ADVANCED", "INTERMEDIATE", "NOVICE", "INTERESTED"];

    if (isArrayPresent(searchResults)) {
      // Sort the array by the number of skills
      const sortedArray = newSearchResults.sort((a, b) => {
        // // If number of skills is not the same, sort by skill level
        if (a.skills.length !== b.skills.length) {
          for (let i = 0; i < a.skills.length; i++) {
            return b.skills.length - a.skills.length;
          }
        } else {
          // If skill numbers are the same but skill levels are not, sort by skill level
          const levelIndexA = skillLevelsOrder.indexOf(a.skills[0].level);
          const levelIndexB = skillLevelsOrder.indexOf(b.skills[0].level);

          if ( a.skills.length === b.skills.length && levelIndexA !== levelIndexB) {
            return levelIndexA - levelIndexB;
          } else {
            // If skills and levels are the same, sort alphabetically by name
            return a.name.localeCompare(b.name);
          }
        }
      });

      console.log(sortedArray);
    }
  };

  useEffect(() => {
    sortMembersBySkill();
  }, [searchResults])

  const getMemberProfile = (member) => selectProfile(state, member.id);

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
        { !searchResults ? (
          <div />
        ) : (
          searchResults.map((member, index) => {
            return (
              <Card className={"member-skills-card"} key={`card-${member?.id}`}>
                <CardHeader
                  title={
                    <Typography variant="h5" component="h2">
                      {getMemberProfile(member)?.name || ""}
                    </Typography>
                  }
                  subheader={
                    <Typography color="textSecondary" component="h3">
                      {getMemberProfile(member)?.title || ""}
                    </Typography>
                  }
                  disableTypography
                  avatar={
                    <Avatar
                      className={"large"}
                      src={getAvatarURL(
                        getMemberProfile(member)?.workEmail || ""
                      )}
                    />
                  }
                />
                <ListItem key={`member-${member?.id}`}>
                  {member.skills.map((skill, index) => {
                    return <div key={member?.id}>{chip(skill)}</div>;
                  })}
                </ListItem>
              </Card>
            );
          })
        )}
      </List>
    </div>
  );
};

export default SearchResults;