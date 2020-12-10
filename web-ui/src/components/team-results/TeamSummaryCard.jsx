import React, {useContext, useState} from "react";

import {AppContext, UPDATE_TEAMS, UPDATE_TOAST} from "../../context/AppContext";
import EditTeamModal from "./EditTeamModal";

import {Card, CardActions, CardContent, CardHeader} from "@material-ui/core";
import {Skeleton} from "@material-ui/lab";
import PropTypes from "prop-types";
import {deleteTeam} from "../../api/team.js";
import SplitButton from "../split-button/SplitButton";

const propTypes = {
    team: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        description: PropTypes.string,
    }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({team, index}) => {
    const {state, dispatch} = useContext(AppContext);
    const {teams, userProfile, csrf} = state;
    const [open, setOpen] = useState(false);
    const isAdmin =
        userProfile && userProfile.role && userProfile.role.includes("ADMIN");
    console.log(JSON.stringify(state));
    let leads =
        team.teamMembers == null
            ? null
            : team.teamMembers.filter((teamMember) => teamMember.lead);
    let nonLeads =
        team.teamMembers == null
            ? null
            : team.teamMembers.filter((teamMember) => !teamMember.lead);

    const isTeamLead =
        leads === null
        ? false
        : leads.some((lead) => lead.memberid === userProfile.memberProfile.id);

    const handleOpen = () => setOpen(true);

    const handleClose = () => setOpen(false);

    const deleteATeam = (id) => {
        if (id && csrf) {

            const result = deleteTeam(id, csrf);
            if (result !== null) {
                window.snackDispatch({
                    type: UPDATE_TOAST,
                    payload: {
                        severity: "success",
                        toast: "Team deleted",
                    },
                });
            }

            let newTeams = teams.filter((team) => {
                return team.id !== id;
            });
            dispatch({
                type: UPDATE_TEAMS,
                payload: newTeams,
            });

        }
    };

    const options=(isAdmin || isTeamLead) ? ['Edit Team', 'Delete Team'] : ['Edit Team'];

    const handleAction = (e,index)=>index === 0 ? handleOpen() : deleteATeam(team.id);

    return (
        <Card className="summary-card">
            <CardHeader title={team.name} subheader={team.description}/>
            <CardContent>
                {team.teamMembers == null ? (
                    <React.Fragment>
                        <Skeleton/>
                        <Skeleton/>
                    </React.Fragment>
                ) : (
                    <React.Fragment>
                        <strong>Team Leads: </strong>
                        {leads.map((lead, index) => {
                            return index !== leads.length - 1 ? `${lead.name}, ` : lead.name;
                        })}
                        <br/>
                        <strong>Team Members: </strong>
                        {nonLeads.map((member, index) => {
                            return index !== nonLeads.length - 1
                                ? `${member.name}, `
                                : member.name;
                        })}
                    </React.Fragment>
                )}
            </CardContent>
            <CardActions>
                <SplitButton options = {options} onClick={handleAction} />
            </CardActions>
            <EditTeamModal
                team={team}
                open={open}
                onClose={handleClose}
                onSave={(team) => {
                    const copy = [...teams];
                    copy[index] = team;
                    dispatch({
                        type: UPDATE_TEAMS,
                        payload: copy,
                    });
                    handleClose();
                }}
            />
        </Card>
    );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
