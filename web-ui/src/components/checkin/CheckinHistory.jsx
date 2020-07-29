import React, { useContext } from "react";
import { SkillsContext } from "../../context/SkillsContext";
import Avatar from "@material-ui/core/Avatar";

import "./CheckinHistory.css";

const CheckinsHistory = () => {
  const { state } = useContext(SkillsContext);
  const { defaultProfile } = state;

  const { email, image_url, name, pdl, role, nextCheckinDate } = defaultProfile;
  const date = Date(nextCheckinDate);

  const today = new Date();
  const quarter = Math.floor((today.getMonth() + 3) / 3);

  return (
    <div>
      <div className="profile-section">
        <Avatar
          src={
            image_url ? image_url : require("../../images/default_profile.jpg")
          }
          style={{ height: "220px", width: "200px" }}
        />
        <div className="info">
          <p>{name}</p>
          <p>{role}</p>
          <p>PDL: {pdl}</p>
          <p>Company Email: {email}</p>
        </div>
      </div>
      <div>
        <p>
          Q{quarter} - {today.getFullYear()} Check-in
        </p>
        <p>Scheduled For: {date}</p>
      </div>
    </div>
  );
};

export default CheckinsHistory;
