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



const SearchBirthdayAnniversaryResults = ({searchBirthdayResults, searchAnniversaryResults}) => {

  searchAnniversaryResults = searchAnniversaryResults.sort((a, b) => {
  return a.anniversary.localeCompare(b.anniversary);
  })
  const { state } = useContext(AppContext);
  const getMemberProfile = (member) => selectProfile(state, member.userId);
  return (
    <div className="results-section">
      <List>
      <h2>Birthdays</h2>
      { searchBirthdayResults.length > 0 &&(
        searchBirthdayResults.map((member, index) => {
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
                    <br/>
                    Birthday: {member.birthDay || ""}
                  </Typography>
                }
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
            </Card>
          );
        })
       )}
       <h2>Anniversaries</h2>
        { searchAnniversaryResults.length > 0 && (
          searchAnniversaryResults.map((member, index) => {
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
                      <br/>
                      Anniversary: {member.anniversary || ""}
                      <br/>
                      Tenure: {member.yearsOfService || ""}
                    </Typography>
                  }
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
              </Card>
            );
          })
        )}
      </List>
    </div>
  );
};

export default SearchBirthdayAnniversaryResults;
