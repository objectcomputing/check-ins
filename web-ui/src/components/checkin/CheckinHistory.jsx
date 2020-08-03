import React, { useContext, useState } from "react";
import { SkillsContext, UPDATE_CHECKIN } from "../../context/SkillsContext";
import Avatar from "@material-ui/core/Avatar";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";

import "./CheckinHistory.css";

const CheckinsHistory = () => {
  const { state, dispatch } = useContext(SkillsContext);
  const { defaultProfile } = state;

  const { email, image_url, name, pdl, role, checkins } = defaultProfile;

  const [checkinIndex, setCheckinIndex] = useState(checkins.length - 1);

  let checkinDate = new Date(checkins[checkinIndex].checkInDate);
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

  const pickDate = (date) => {
    dispatch({
      type: UPDATE_CHECKIN,
      payload: { date, index: checkinIndex },
    });
  };
  const ExampleCustomInput = ({ value, onClick }) => (
    <div style={{ display: "flex", alignItems: "center" }}>
      <p style={{ margin: "0px" }}>{value}</p>
      <CalendarTodayIcon onClick={onClick}>stuff</CalendarTodayIcon>
    </div>
  );

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
      <div className="date-picker">
        <ArrowBackIcon className={leftArrowClass} onClick={previousCheckin} />
        <DatePicker
          customInput={<ExampleCustomInput />}
          dateFormat="MMMM dd, yyyy h:mm aa"
          selected={checkinDate}
          showTimeSelect
          onChange={pickDate}
        />
        <ArrowForwardIcon className={rightArrowClass} onClick={nextCheckin} />
      </div>
    </div>
  );
};

export default CheckinsHistory;
