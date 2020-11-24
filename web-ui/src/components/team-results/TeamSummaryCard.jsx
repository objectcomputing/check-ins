import React, { useContext } from "react";

import { AppContext, selectMemberProfilesByTeamId } from "../../context/AppContext";

import PropTypes from "prop-types";
import { Skeleton } from "@material-ui/lab";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({ team }) => {
  const { state } = useContext(AppContext);
  const teamMembers = selectMemberProfilesByTeamId(team.id)(state);

  let leads =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => teamMember.lead);
  let nonLeads =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => !teamMember.lead);

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
        <Button>Delete Team</Button>
      </CardActions>
    </Card>
  );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
