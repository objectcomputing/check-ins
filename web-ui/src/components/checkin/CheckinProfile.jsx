import React, { useContext, useEffect, useState } from "react";
import { getMember } from "../../api/member";
import { AppContext } from "../../context/AppContext";

import Avatar from "@material-ui/core/Avatar";

import "./Checkin.css";
const displayName = "CheckinProfile";

const CheckinProfile = () => {
  const { state } = useContext(AppContext);
  const { selectedProfile, userProfile } = state;
  const { name, pdlId, title, workEmail } = selectedProfile
    ? selectedProfile
    : userProfile && userProfile.memberProfile
    ? userProfile.memberProfile
    : {};
  const { imageUrl } = selectedProfile
    ? selectedProfile
    : userProfile
    ? userProfile
    : {};
  const [pdl, setPDL] = useState();

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId);
        let pdlProfile =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    getPDLName();
  }, [pdlId]);

  return (
    <div className="profile-section">
      <Avatar
        src={imageUrl ? imageUrl : "/default_profile.jpg"}
        style={{ height: "220px", width: "200px" }}
      />
      <div className="info">
        <p>{name}</p>
        <p>Job Title: {title}</p>
        <p>PDL: {pdl}</p>
        <p>Company Email: {workEmail}</p>
      </div>
    </div>
  );
};

CheckinProfile.displayName = displayName;
export default CheckinProfile;