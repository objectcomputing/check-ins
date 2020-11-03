import React, { useEffect, useContext } from 'react';
import PropTypes from 'prop-types';
import Container from '@material-ui/core/Container';
import TeamSummaryCard from './TeamSummaryCard';
import { AppContext, UPDATE_TEAMS } from '../../context/AppContext';
import { getAllTeams } from '../../api/team';
import './TeamResults.css'

const propTypes = {
    teamEntities: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        description: PropTypes.string
    }))
};

const displayName = "TeamResults";

const TeamResults = () => {
    const { state, dispatch } = useContext(AppContext);
    const { teamEntities } = state;

    useEffect(() => {
        async function getTeams() {
            let res = await getAllTeams();
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
        getTeams();
    }, [dispatch]);

    return (
        <Container maxWidth="md">
            {teamEntities.map((team) => (
                <TeamSummaryCard key={`team-summary-${team.id}`} team={team} />
            ))}
        </Container>
    )
};

TeamResults.propTypes = propTypes;
TeamResults.displayName = displayName;

export default TeamResults;
