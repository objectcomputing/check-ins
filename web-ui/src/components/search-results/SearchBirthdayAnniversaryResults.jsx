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
} from "@mui/material";

const SearchBirthdayAnniversaryResults = ({
  hasSearched,
  searchBirthdayResults,
  searchAnniversaryResults,
  anniversary,
  birthday,
}) => {
  searchAnniversaryResults.sort((a, b) => {
    return a.tenure - b.tenure;
  });

  searchBirthdayResults.sort((a, b) => {
    const adate = new Date(a.birthDay);
    const bdate = new Date(b.birthDay);
    return adate - bdate;
  });

  const { state } = useContext(AppContext);
  const getMemberProfile = (member) => selectProfile(state, member.userId);
  const BirthdayMap = () => {
    if (searchBirthdayResults.length > 0) {
      return searchBirthdayResults.map((member) => {
        return (
          <Card
            className={"member-birthday-anniversary-card"}
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
    if (searchAnniversaryResults.length > 0) {
      return (
        searchAnniversaryResults.length > 0 &&
        searchAnniversaryResults.map((member) => {
          return (
            <Card
              className={"member-birthday-anniversary-card"}
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
        {searchBirthdayResults.length === 0 && hasSearched && birthday && (
          <Card>
            <CardHeader title="No birthdays found for the selected month" />
          </Card>
        )}
        {searchBirthdayResults.length > 0 && (
          <Card>
            <CardHeader title="Birthdays" />
            <Container fixed>
              <BirthdayMap />
            </Container>
          </Card>
        )}
        {searchAnniversaryResults.length === 0 &&
          hasSearched &&
          anniversary && (
            <Card>
              <CardHeader title="No anniversaries found for the selected month" />
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
