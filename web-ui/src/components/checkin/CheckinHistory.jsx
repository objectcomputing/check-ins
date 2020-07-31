import React, { useContext, useState } from "react";
import { SkillsContext } from "../../context/SkillsContext";
import Avatar from "@material-ui/core/Avatar";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

import "./CheckinHistory.css";

const CheckinsHistory = () => {
  const { state, dispatch } = useContext(SkillsContext);
  const { defaultProfile } = state;

  const {
    email,
    image_url,
    name,
    pdl,
    role,
    nextCheckinDate,
    checkins,
  } = defaultProfile;
  const date = new Date(nextCheckinDate);

  const [checkinIndex, setCheckinIndex] = useState(checkins.length - 1);

  const checkinDate = new Date(checkins[checkinIndex].checkInDate);
  const lastIndex = checkins.length - 1;
  const leftArrowClass = "arrow " + (checkinIndex > 0 ? "enabled" : "disabled");
  const rightArrowClass =
    "arrow " + (checkinIndex < lastIndex ? "enabled" : "disabled");

  const previousCheckin = () => {
    setCheckinIndex((index) => (index === 0 ? 0 : index - 1));
  };

  const nextCheckin = () => {
    setCheckinIndex((index) => (index === lastIndex ? lastIndex : index + 1));
  };

  const pickDate = () => {
    console.log("Not yet implemented");
    return date;
  };

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
      <div
        style={{ display: "flex", justifyContent: "center", marginTop: "50px" }}
      >
        <ArrowBackIcon className={leftArrowClass} onClick={previousCheckin} />
        <DatePicker selected={checkinDate} onChange={pickDate} />
        <ArrowForwardIcon className={rightArrowClass} onClick={nextCheckin} />
      </div>
    </div>
  );
};

export default CheckinsHistory;
