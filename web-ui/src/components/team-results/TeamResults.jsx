import React, { useEffect, useContext, useState } from "react";

import TeamSummaryCard from "./TeamSummaryCard";
import { AppContext } from "../../context/AppContext";
import { UPDATE_TEAMS } from "../../context/actions";
import TeamsActions from "./TeamsActions";
import { getAllTeams } from "../../api/team";
import PropTypes from "prop-types";
import { TextField } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import "./TeamResults.css";

const useStyles = makeStyles((theme) => ({
  searchInput: {
    width: "20em",
  }
}));

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

  const classes = useStyles();

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
          className={classes.searchInput}
          label="Search Teams"
          placeholder="Team Name"
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
        />
        <TeamsActions />
      </div>
      <div className="teams">
        {teams.map((team, index) =>
          team.name
            .toLowerCase()
            .normalize('NFD').replace(/[\u0300-\u036f]/g, "")
            .includes(
                searchText.toLowerCase()
                .normalize('NFD').replace(/[\u0300-\u036f]/g, "")
            ) ?
          (
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
