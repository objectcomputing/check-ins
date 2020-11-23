import React, { useContext } from "react";

import {AppContext, UPDATE_TOAST} from "../../context/AppContext";

import PropTypes from "prop-types";
import { Skeleton } from "@material-ui/lab";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import { deleteTeam } from "../../api/team.js";

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({ team }) => {
  const { state, dispatch } = useContext(AppContext);
  const teamMembers = AppContext.selectMemberProfilesByTeamId(state)(team.id);
  const { memberProfiles, userProfile, csrf } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  let leads =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => teamMember.lead);

    console.log("team lead " + leads);

    let nonLeads =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => !teamMember.lead);

    const deleteATeam = (id) => {
        if (id && csrf) {
            // deleteTeam(id);
            console.log("team lead " + leads);
            const result = deleteTeam(id, csrf);
            console.log(result);
            if (result !== null) {
                window.snackDispatch({
                    type: UPDATE_TOAST,
                    payload: {
                        severity: "success",
                        toast: "Team deleted",
                    },
                });
            }
            // see api.js 30-40

            // redisplay list with deleted item removed

            // let newItems = agendaItems.filter((agendaItem) => {
            //     return agendaItem.id !== id;
            // });
            // setAgendaItems(newItems);
        }
    };

  return (
    <Card>
      <CardHeader title={team.name} subheader={team.description} />
      <CardContent>
        {teamMembers == null ? (
          <React.Fragment>
            <Skeleton />
            <Skeleton />
          </React.Fragment>
        ) : (
          <React.Fragment>
            <strong>Team Leads: </strong>
            {leads.map((lead, index) => {
              return index !== leads.length - 1 ? `${lead.name}, ` : lead.name;
            })}
            <br />
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
        <Button>Edit Team</Button>
        {isAdmin && (    //fix for team leads to delete
          <Button
              onClick={(e) => {
                  console.log("delete clicked " + team.id);
                  deleteATeam(team.id, e)}} >Delete Team</Button>
        )}
      </CardActions>
    </Card>
  );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
