import React, { useEffect, useContext } from "react";

import TeamSummaryCard from "./TeamSummaryCard";
import { AppContext, UPDATE_TEAMS } from "../../context/AppContext";
import { getAllTeams } from "../../api/team";

import PropTypes from "prop-types";
import Container from "@material-ui/core/Container";

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
        };
        if (csrf) {
          getTeams();
        }
    }, [csrf, dispatch]);

    return (
        <Container maxWidth="md">
            {teams.map((team, index) => (
                <TeamSummaryCard key={`team-summary-${team.id}`} team={team} index={index}/>
            ))}
        </Container>
    )
};

TeamResults.propTypes = propTypes;
TeamResults.displayName = displayName;

export default TeamResults;
