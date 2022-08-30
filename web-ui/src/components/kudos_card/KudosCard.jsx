import React, {useContext, useState} from "react";
import PropTypes from "prop-types";
import {Paper, Collapse, Divider, Typography, Avatar, Chip, Button} from "@mui/material";
import {selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {getAvatarURL} from "../../api/api";
import DateFnsUtils from "@date-io/date-fns";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";

import "./KudosCard.css";

const dateUtils = new DateFnsUtils();

const propTypes = {
  kudos: PropTypes.shape({
    senderId: PropTypes.string.isRequired,
    recipientId: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    dateApproved: PropTypes.array,
    dateCreated: PropTypes.array.isRequired
  }).isRequired,
  type: PropTypes.oneOf(["RECEIVED", "SENT", "MANAGE"]).isRequired
};

const KudosCard = ({ kudos, type }) => {
  const { state } = useContext(AppContext);

  const [expanded, setExpanded] = useState(true);

  const sender = selectProfile(state, kudos.senderId);
  const recipient = selectProfile(state, kudos.recipientId);

  const dateApproved = kudos.dateApproved ? new Date(kudos.dateApproved.join("/")) : null;
  const dateCreated = new Date(kudos.dateCreated.join("/"));

  return (
    <Paper className="kudos-card">
      <div className="kudos-card-header" onClick={() => setExpanded(!expanded)}>
        <div className="members-container">
          {type === "RECEIVED" && <>
            <Avatar style={{ marginRight: "0.5em" }} src={getAvatarURL(sender?.workEmail)} />
            <Typography variant="h5">{sender?.name}</Typography>
          </>}
          {type === "SENT" && <>
            <Avatar style={{ marginRight: "0.5em" }} src={getAvatarURL(recipient?.workEmail)} />
            <Typography variant="h5">{recipient?.name}</Typography>
          </>}
          {type === "MANAGE" && <>
            <Chip
              avatar={<Avatar src={getAvatarURL(recipient?.workEmail)}/>}
              label={recipient?.name}
            />
            <Typography variant="body1">received kudos from</Typography>
            <Chip
              avatar={<Avatar src={getAvatarURL(sender?.workEmail)}/>}
              label={sender?.name}
            />
          </>}
        </div>
        <div className="kudos-status-container">
          <Typography color={dateApproved ? "green" : "orange"}>
            {dateApproved ? "Approved" : "Pending"}
          </Typography>
          {type === "RECEIVED" && <>
            <Typography variant="body2" color="gray" fontSize="10px">
              Received {dateApproved ? dateUtils.format(dateApproved, "MM/dd/yyyy") : ""}
            </Typography>
          </>}
          {type === "SENT" && <>
            <Typography variant="body2" color="gray" fontSize="10px">
              Created {dateUtils.format(dateCreated, "MM/dd/yyyy")}
            </Typography>
          </>}
          {type === "MANAGE" && <>
            <Button
              variant="outlined"
              color="success"
              size="medium"
            >
              <CheckIcon/>
            </Button>
            <Button
              variant="outlined"
              color="error"
              size="medium"
            >
              <CloseIcon/>
            </Button>
          </>}
        </div>
      </div>
      <Divider/>
      <Collapse in={expanded}>
        <div className="kudos-card-content">
          <Typography variant="body1">{kudos.message}</Typography>
        </div>
      </Collapse>
    </Paper>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;