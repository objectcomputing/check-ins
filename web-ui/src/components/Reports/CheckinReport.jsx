import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getAvatarURL } from "../../api/api.js";
import { AppContext } from "../../context/AppContext";
import { selectCheckinsForTeamMemberAndPDL } from "../../context/selectors";

import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Avatar,
  Box,
  Card,
  CardHeader,
  CardContent,
  Container,
  TextField,
  Typography,
} from "@material-ui/core";

import "./CheckinReport.css";

const CheckinsReport = ({ closed, pdl, planned }) => {
  const { state } = useContext(AppContext);
  const { name, id, members, workEmail } = pdl;
  const [searchText, setSearchText] = useState("");
  const [filteredMembers, setFilteredMembers] = useState(members);

  const now = Date.now();

  useEffect(() => {
    if (!members) return;
    let newMembers = members.filter((member) =>
      member.name.includes(searchText)
    );
    setFilteredMembers(newMembers);
    // don't need to watch searchText
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [members]);

  const getCheckinDate = (checkin) => {
    if (!checkin || !checkin.checkInDate) return;
    const [year, month, day, hour, minute] = checkin.checkInDate;
    return new Date(year, month - 1, day, hour, minute, 0);
  };

  const LinkSection = ({ checkin, member }) => {
    return (
      <Link
        key={checkin.id}
        style={{ textDecoration: "none" }}
        to={`/checkins/${member.id}/${checkin.id}`}
      >
        <Typography>{new Date(getCheckinDate(checkin)).toString()}</Typography>
      </Link>
    );
  };
  const TeamMemberMap = () => {
    return (
      filteredMembers &&
      filteredMembers.map(
        (member) =>
          member.name.toLowerCase().includes(searchText.toLowerCase()) && (
            <Accordion id="member-sub-card" key={member.name}>
              <AccordionSummary
                aria-controls="panel1a-content"
                id="accordion-summary"
              >
                <Avatar
                  className={"large"}
                  src={getAvatarURL(member.workEmail)}
                />
                <Typography>{member.name}</Typography>
              </AccordionSummary>
              <AccordionDetails id="accordion-checkin-date">
                {selectCheckinsForTeamMemberAndPDL(state, member.id, id).map(
                  (checkin) => {
                    const pastCheckin =
                      now >= getCheckinDate(checkin).getTime();
                    const shouldRender =
                      (!closed &&
                        !planned &&
                        !checkin.completed &&
                        pastCheckin) || // open checkin
                      (closed && checkin.completed) || //closed checkin
                      (planned && !pastCheckin && !checkin.completed); //planned checkin
                    return shouldRender ? (
                      <LinkSection
                        checkin={checkin}
                        key={checkin.id}
                        member={member}
                      />
                    ) : null;
                  }
                )}
              </AccordionDetails>
            </Accordion>
          )
      )
    );
  };

  return (
    <div>
      <Box display="flex" flexWrap="wrap">
        <Card id="pdl-card">
          <CardHeader
            title={
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
            }
            disableTypography
            avatar={<Avatar id="pdl-large" src={getAvatarURL(workEmail)} />}
          />
          <CardContent>
            <Container fixed>
              <TextField
                label="Search Members"
                placeholder="Member Name"
                value={searchText}
                onChange={(e) => {
                  setSearchText(e.target.value);
                }}
              />
              <TeamMemberMap />
            </Container>
          </CardContent>
        </Card>
      </Box>
    </div>
  );
};
export default CheckinsReport;
