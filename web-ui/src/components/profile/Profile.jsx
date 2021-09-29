import React, { useContext, useEffect, useState } from "react";
import { Avatar, Typography, Hidden } from "@mui/material";
import makeStyles from '@mui/styles/makeStyles';
import { AppContext } from "../../context/AppContext";
import { selectProfileMap } from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";
import { getMember } from "../../api/member";

const useStyles = makeStyles((theme) => ({
  profileInfo: {
    display: "flex",
    flexDirection: "row",
    margin: "14px",
  },
  profileImage: {
    marginRight: "20px",
    marginTop: "10px",
    marginBottom: "10px",
    cursor: "pointer",
    width: "160px",
    height: "160px",
  },
  flexRow: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "center",
    marginBottom: "16px",
  },
  header: {
    display: "flex",
    flexDirection: "row",
    marginBottom: "16px",
    alignItems: "center",
  },
  title: {
    display: "flex",
    flexDirection: "column",
  },
  smallAvatar: {
    marginRight: "16px",
  },
}));

const Profile = ({ memberId, pdlId, checkinPdlId }) => {
  const classes = useStyles();
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const userProfile = selectProfileMap(state)[memberId];

  const { workEmail, name, title, location, supervisorid } = userProfile
    ? userProfile
    : {};

  const [pdl, setPDL] = useState();
  const [checkinPdl, setCheckinPdl] = useState("");
  const [supervisor, setSupervisor] = useState("");

  const areSamePdls = checkinPdl && pdl && checkinPdl === pdl;

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  // Get Checkin PDL's name
  useEffect(() => {
    async function getCheckinPDLName() {
      if (checkinPdlId) {
        let res = await getMember(checkinPdlId, csrf);
        let checkinPdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setCheckinPdl(checkinPdlProfile ? checkinPdlProfile.name : "");
      }
    }
    if (csrf) {
      getCheckinPDLName();
    }
  }, [csrf, checkinPdlId]);

  // Get Supervisor's name
  useEffect(() => {
    async function getSupervisorName() {
      if (supervisorid) {
        let res = await getMember(supervisorid, csrf);
        let supervisorProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setSupervisor(supervisorProfile ? supervisorProfile.name : "");
      }
    }
    if (csrf) {
      getSupervisorName();
    }
  }, [csrf, supervisorid]);

  return (
    <div className={classes.flexRow}>
      <Hidden smDown>
        <Avatar
          className={classes.profileImage}
          alt="Profile"
          src={getAvatarURL(workEmail)}
        />
      </Hidden>
      <div className={classes.profileInfo}>
        <div>
          <div className={classes.header}>
            <Hidden smUp>
              <Avatar
                className={classes.smallAvatar}
                src={getAvatarURL(workEmail)}
              />
            </Hidden>
            <div className={classes.title}>
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
              <Typography color="textSecondary" component="h3">
                {title}
              </Typography>
            </div>
          </div>
          <Typography variant="body2" color="textSecondary" component="p">
            <a
              href={`mailto:${workEmail}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              {workEmail}
            </a>
            <br />
            Location: {location}
            <br />
            Supervisor: {supervisor}
            <br />
            Current PDL: {pdl}
            <br />
            {checkinPdl &&
              !areSamePdls &&
              `PDL @ Time of Check-In: ${checkinPdl}`}
          </Typography>
        </div>
      </div>
    </div>
  );
};

export default Profile;
