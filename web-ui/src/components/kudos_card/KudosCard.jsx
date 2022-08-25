import React, {useContext, useState} from "react";
import PropTypes from "prop-types";
import {Paper, Collapse, Divider, Typography, Avatar} from "@mui/material";
import {selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {getAvatarURL} from "../../api/api";
import DateFnsUtils from "@date-io/date-fns";

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
  type: PropTypes.oneOf(["RECEIVED", "SENT"]).isRequired
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
        </div>
        <div className="kudos-status-container">
          <Typography color={dateApproved ? "green" : "orange"}>
            {dateApproved ? "Approved" : "Pending"}
          </Typography>
          {type === "RECEIVED" && <>
            <Typography variant="body2" color="gray" fontSize="10px">
              Received {dateUtils.format(dateCreated, "MM/dd/yyyy")}
            </Typography>
          </>}
          {type === "SENT" && <>
            <Typography variant="body2" color="gray" fontSize="10px">
              Created {dateUtils.format(dateCreated, "MM/dd/yyyy")}
            </Typography>
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