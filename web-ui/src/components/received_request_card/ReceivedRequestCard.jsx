import React, { useContext } from "react";
import { styled } from "@mui/material/styles";
import { Avatar, Card, Tooltip, IconButton } from "@mui/material";
import { getAvatarURL } from "../../api/api";
import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import { selectProfile } from "../../context/selectors";
import DateFnsUtils from "@date-io/date-fns";
import { AppContext } from "../../context/AppContext";
import {
  Edit as EditIcon,
  Visibility as VisibilityIcon
} from "@mui/icons-material";
import {FeedbackRequestStatus} from "../../context/util";

const dateFns = new DateFnsUtils();
const PREFIX = 'ReceivedRequestCard';
const classes = {
  redTypography: `${PREFIX}-redTypography`,
  yellowTypography: `${PREFIX}-yellowTypography`,
  greenTypography: `${PREFIX}-greenTypography`,
  darkGrayTypography: `${PREFIX}-darkGrayTypography`,
  grayTypography: `${PREFIX}-grayTypography`
};

const StyledCard = styled(Card)({
  [`& .${classes.redTypography}`]: {
    color: "#FF0000",
  },
  [`& .${classes.yellowTypography}`]: {
    color: "#EE8C00",
  },
  [`& .${classes.greenTypography}`]: {
    color: "#006400",
  },
  [`& .${classes.darkGrayTypography}`]: {
    color: "#333333",
  },
  [`& .${classes.grayTypography}`]: {
    color: "gray",
  },
});

const propTypes = {
  request: PropTypes.object.isRequired,
};

const ReceivedRequestCard = ({ request }) => {
  let { submitDate, dueDate, sendDate } = request;
  const { state } = useContext(AppContext);
  const requestCreator = selectProfile(state, request?.creatorId);
  const requestee = selectProfile(state, request?.requesteeId);
  submitDate = submitDate
    ? dateFns.format(new Date(submitDate.join("/")), "MM/dd/yyyy")
    : null;
  dueDate = dueDate
    ? dateFns.format(new Date(dueDate.join("/")), "LLLL dd, yyyy")
    : null;
  sendDate = dateFns.format(new Date(sendDate.join("/")), "LLLL dd, yyyy");

  const Submitted = () => {
    if (request.dueDate) {
      const today = new Date();
      const due = new Date(request.dueDate);
      if (!request.submitDate && today > due) {
        return (
          <Typography className={classes.redTypography}>Overdue</Typography>
        );
      }
    }
    if (request.submitDate) {
      return (
        <Typography className={classes.greenTypography}>
          Submitted {submitDate}
        </Typography>
      );
    } else if (request.status === FeedbackRequestStatus.CANCELED) {
      return (
        <Typography className={classes.grayTypography}>
          Canceled
        </Typography>
      );
    } else
      return (
        <Typography className={classes.yellowTypography}>
          Not Submitted
        </Typography>
      );
  };

  return (
    <StyledCard style={{ paddingLeft: "16px", paddingRight: "16px" }}>
      <div className="card-content-grid">
        <div className="request-members-container">
          <div className="member-chip">
            <Avatar
              style={{ width: "40px", height: "40px", marginRight: "0.5em" }}
              src={getAvatarURL(requestCreator?.workEmail)}
            />
            <div>
              <Typography className="person-name">
                {requestCreator?.name}
              </Typography>
              <Typography
                className="position-text"
                style={{ fontSize: "14px" }}
              >
                {requestCreator?.title}
              </Typography>
            </div>
          </div>
          <Typography style={{ margin: "0 5px 0 5px" }} variant="body1">
            requested feedback on
          </Typography>
          <div className="member-chip">
            <Avatar
              style={{ width: "40px", height: "40px", marginRight: "0.5em" }}
              src={getAvatarURL(requestee?.workEmail)}
            />
            <div>
              <Typography className="person-name">{requestee?.name}</Typography>
              <Typography
                className="position-text"
                style={{ fontSize: "14px" }}
              >
                {requestee?.title}
              </Typography>
            </div>
          </div>
        </div>
        <div className="request-details-container">
          <div className="request-dates-container">
            <Typography className={classes.darkGrayTypography} variant="body1">
              Sent on {sendDate}
            </Typography>
            <Typography variant="body2">
              {request?.dueDate ? `Due on ${dueDate}` : "No due date"}
            </Typography>
          </div>
          <div className="request-status-container">
            <Submitted />
          </div>
          <div className="submission-link-container">
            {request && request.id && request.status !== FeedbackRequestStatus.CANCELED ? (
              <Link
                to={`/feedback/submit?request=${request.id}`}
                style={{ textDecoration: "none" }}
              >
                <Tooltip title={request.submitDate ? "View feedback" : "Give feedback"} arrow>
                  <IconButton size="large">
                    {request.submitDate ? <VisibilityIcon/> : <EditIcon/>}
                  </IconButton>
                </Tooltip>
              </Link>
            ) : null}
          </div>
        </div>
      </div>
    </StyledCard>
  );
};
ReceivedRequestCard.propTypes = propTypes;

export default ReceivedRequestCard;
