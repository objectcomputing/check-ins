import React, { useCallback, useContext, useState } from "react";
import PropTypes from "prop-types";
import {
  Card,
  CardHeader,
  CardContent,
  Divider,
  Typography,
  Avatar,
  Chip,
  Button,
  AvatarGroup,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
} from "@mui/material";
import { selectCsrfToken, selectProfile } from "../../context/selectors";
import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import DateFnsUtils from "@date-io/date-fns";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import TeamIcon from "@mui/icons-material/Groups";

import { approveKudos, deleteKudos } from "../../api/kudos";
import { UPDATE_TOAST } from "../../context/actions";

import "./PublicKudosCard.css";

const dateUtils = new DateFnsUtils();

const propTypes = {
  kudos: PropTypes.shape({
    id: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    senderId: PropTypes.string.isRequired,
    recipientTeam: PropTypes.object,
    dateCreated: PropTypes.array.isRequired,
    dateApproved: PropTypes.array,
    recipientMembers: PropTypes.array,
  }).isRequired,
};

const KudosCard = ({ kudos }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const sender = selectProfile(state, kudos.senderId);

  const getRecipientComponent = useCallback(() => {
    if (kudos.recipientTeam) {
      return (
        <Tooltip
          arrow
          key={kudos.recipientTeam.id}
          title={kudos.recipientTeam.name}
        >
          <Avatar>
            <TeamIcon />
          </Avatar>
        </Tooltip>
      );
    }

    const multiTooltip = (num, list) => {
      let tooltip = "";
      let prefix = "";
      for (let member of list.slice(-num)) {
        tooltip += prefix + `${member.firstName} ${member.lastName}`;
        prefix = ", ";
      }
      return <Tooltip arrow key="multi-member-tooltip" title={tooltip}>
               {`+${num}`}
             </Tooltip>;
    };

    return (
      <AvatarGroup max={4}
                   renderSurplus={(extra) => multiTooltip(
                                               extra, kudos.recipientMembers)}>
        {kudos.recipientMembers.map((member) => (
          <Tooltip
            arrow
            key={member.id}
            title={`${member.firstName} ${member.lastName}`}
          >
            <Avatar src={getAvatarURL(member.workEmail)} />
          </Tooltip>
        ))}
      </AvatarGroup>
    );
  }, [kudos]);

  let titleText = kudos?.recipientTeam?.name ? "Kudos, " + kudos?.recipientTeam?.name + "!" : "Kudos!";
  if(kudos?.recipientMembers?.length === 1 && kudos?.recipientMembers[0]?.firstName) titleText = "Kudos, " + kudos?.recipientMembers[0]?.firstName + "!";

  return (
      <Card className="kudos-card">
        <CardHeader
          avatar={getRecipientComponent()}
          title={titleText}
          titleTypographyProps={{variant:"h5"}}
          subheader={(<>from <Chip
            size="small"
            avatar={<Avatar src={getAvatarURL(sender?.workEmail)} />}
            label={sender?.name}
          /></>)}
          subheaderTypographyProps={{variant:"subtitle1"}}
        />
        <CardContent>
          <Typography variant="body1"><em>{kudos.message}</em></Typography>
          {kudos.recipientTeam && (
      <AvatarGroup max={12}>
        {kudos.recipientMembers.map((member) => (
          <Tooltip
            arrow
            key={member.id}
            title={`${member.firstName} ${member.lastName}`}
          >
            <Avatar src={getAvatarURL(member.workEmail)} />
          </Tooltip>
        ))}
      </AvatarGroup>
          )}
        </CardContent>
      </Card>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;
