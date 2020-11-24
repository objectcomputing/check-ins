import React, { useEffect, useContext, useState } from "react";

import TeamSummaryCard from "./TeamSummaryCard";
import { AppContext, UPDATE_TEAMS } from "../../context/AppContext";
import TeamsActions from "./TeamsActions";
import { getAllTeams } from "../../api/team";

import PropTypes from "prop-types";
import { TextField } from "@material-ui/core";

import "./TeamResults.css";

const propTypes = {
  teams: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
    })
  ),
};

const displayName = "TeamResults";

const TeamResults = () => {
    const { state, dispatch } = useContext(AppContext);
  const { csrf, teams } = state;
  const [searchText, setSearchText] = useState("");

  useEffect(() => {
    async function getTeams() {
      let res = await getAllTeams(csrf);
      let data =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_TEAMS, payload: data });
      }
    }
    if (csrf) {
      getTeams();
    }
  }, [csrf, dispatch]);

  return (
    <div>
      <div className="team-search">
        <TextField
          className="fullWidth"
          label="Search Teams"
          placeholder="Team Name"
          style={{ marginBottom: "1rem" }}
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
        />
        <TeamsActions />
      </div>
      <div className="teams">
        {teams.map((team, index) =>
          team.name.toLowerCase().includes(searchText.toLowerCase()) ? (
            <TeamSummaryCard
              key={`team-summary-${team.id}`}
              index={index}
              team={team}
            />
          ) : null
        )}
      </div>
    </div>
  );
};

TeamResults.propTypes = propTypes;
TeamResults.displayName = displayName;

export default TeamResults;
