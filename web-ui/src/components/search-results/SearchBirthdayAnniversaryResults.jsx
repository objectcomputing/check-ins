import React, { useContext } from "react";

import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import { selectProfile } from "../../context/selectors";

import {
  Avatar,
  Card,
  CardHeader,
  Container,
  List,
  Typography,
} from "@material-ui/core";

const SearchBirthdayAnniversaryResults = ({
  searchBirthdayResults,
  searchAnniversaryResults,
}) => {
  searchAnniversaryResults = searchAnniversaryResults.sort((a, b) => {
    return a.anniversary.localeCompare(b.anniversary);
  });
  const { state } = useContext(AppContext);
  const getMemberProfile = (member) => selectProfile(state, member.userId);

  const BirthdayMap = () => {
    if (searchBirthdayResults.length > 0) {
      return searchBirthdayResults.map((member, index) => {
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
                  <br />
                  Birthday: {member.birthDay || ""}
                </Typography>
              }
              disableTypography
              avatar={
                <Avatar
                  className={"large"}
                  src={getAvatarURL(getMemberProfile(member).workEmail || "")}
                />
              }
            />
          </Card>
        );
      });
    } else return null;
  };

  const AnniversaryMap = () => {
    if (searchBirthdayResults.length > 0) {
      return (
        searchAnniversaryResults.length > 0 &&
        searchAnniversaryResults.map((member, index) => {
          return (
            <Card
              className={"member-skills-card"}
              key={`card-${member.userId}`}
            >
              <CardHeader
                title={
                  <Typography variant="h5" component="h2">
                    {getMemberProfile(member).name || ""}
                  </Typography>
                }
                subheader={
                  <Typography color="textSecondary" component="h3">
                    {getMemberProfile(member).title || ""}
                    <br />
                    Anniversary: {member.anniversary || ""}
                    <br />
                    Tenure: {member.yearsOfService || ""}
                  </Typography>
                }
                disableTypography
                avatar={
                  <Avatar
                    className={"large"}
                    src={getAvatarURL(getMemberProfile(member).workEmail || "")}
                  />
                }
              />
            </Card>
          );
        })
      );
    } else return null;
  };

  return (
    <div className="results-section">
      <List>
        {searchBirthdayResults.length > 0 && (
          <Card>
            <CardHeader title="Birthdays" />
            <Container fixed>
              <BirthdayMap />
            </Container>
          </Card>
        )}
        {searchAnniversaryResults.length > 0 && (
          <Card>
            <CardHeader title="Anniversaries" />
            <Container fixed>
              <AnniversaryMap />
            </Container>
          </Card>
        )}
      </List>
    </div>
  );
};

export default SearchBirthdayAnniversaryResults;
