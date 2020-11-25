import React, { useContext, useState } from "react";

import { AppContext, UPDATE_TEAMS, UPDATE_TOAST } from "../../context/AppContext";
import EditTeamModal from "./EditTeamModal";

import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import { Skeleton } from "@material-ui/lab";
import PropTypes from "prop-types";
import { deleteTeam } from "../../api/team.js";

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({ team, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { teams,  memberProfiles, userProfile, csrf } = state;
  const [open, setOpen] = useState(false);
  const isAdmin =
        userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  let leads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter((teamMember) => teamMember.lead);
  let nonLeads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter((teamMember) => !teamMember.lead);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  console.log("teams");
    teams.forEach((team => {
        console.log(team.teamMembers);
        // console.log(lead.id);
    }));

    console.log("leads");
    leads.forEach((lead => {
        console.log(lead.name);
        console.log(lead.id);
    }));

    console.log("memberProfiles");
    memberProfiles.forEach((mp => {
        console.log(mp);
        console.log(mp.name);
        console.log(mp.id);
        console.log("work email " + mp.workEmail);
    }));

    console.log("userProfiles");
    userProfiles.forEach((up => {
        console.log(up);
        console.log(up.name);
        console.log(up.id);
        console.log("work email " + up.workEmail);
    }));

    const deleteATeam = (id) => {
        if (id && csrf) {
            // deleteTeam(id);
            console.log("team leads " + leads);  //
            const result = deleteTeam(id, csrf);
            console.log(result);  //
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
    <Card className="summary-card">
      <CardHeader title={team.name} subheader={team.description} />
      <CardContent>
        {team.teamMembers == null ? (
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
        <Button onClick={handleOpen}>Edit Team</Button>
          {isAdmin || leads.workemail.includes(userProfile.workemail) (    //fix for team leads to delete
              <Button
                  onClick={(e) => {
                      console.log("delete clicked " + team.id);  //
                      deleteATeam(team.id, e)}} >Delete Team</Button>
          )}
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
