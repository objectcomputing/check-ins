import React, { useEffect, useState } from "react";
import { getMember } from "../../api/member";
import Avatar from "@material-ui/core/Avatar";

import "./Checkin.css";

const CheckinProfile = ({ state }) => {
  const { userProfile } = state;
  const { workEmail, role, pdlId } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};
  const { imageUrl, name } = userProfile ? userProfile : {};
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
        <p>Role: {role}</p>
        <p>PDL: {pdl}</p>
        <p>Company Email: {workEmail}</p>
      </div>
    </div>
  );
};

export default CheckinProfile;
