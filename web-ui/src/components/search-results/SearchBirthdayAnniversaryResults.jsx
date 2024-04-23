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
  results,
  anniversary,
  birthday,
}) => {
  if (anniversary) {
    results.sort((a, b) => a.tenure - b.tenure);
  } else {
    results.sort((a, b) => {
      const adate = new Date(a.birthDay);
      const bdate = new Date(b.birthDay);
      return adate - bdate;
    });
  }

  const { state } = useContext(AppContext);
  const getMemberProfile = (member) => selectProfile(state, member.userId);
  const BirthdayMap = () => {
    if (birthday && results.length > 0) {
      return results.map((member) => {
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
    if (anniversary && results.length > 0) {
      return (
        results.length > 0 &&
        results.map((member) => {
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
        {birthday && hasSearched && results.length === 0 && (
          <Card>
            <CardHeader title="No birthdays found for the selected month" />
          </Card>
        )}
        {birthday && results.length > 0 && (
          <Card>
            <CardHeader title="Birthdays" />
            <Container fixed>
              <BirthdayMap />
            </Container>
          </Card>
        )}
        {anniversary && hasSearched && results.length === 0 && (
          <Card>
            <CardHeader title="No anniversaries found for the selected month" />
          </Card>
        )}
        {anniversary && results.length > 0 && (
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
