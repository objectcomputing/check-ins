import React, { useContext, useEffect, useState } from "react";
import Avatar from "@material-ui/core/Avatar";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import { UPDATE_INDEX } from "../../context/AppContext";
import { getMember } from "../../api/member";
import { AppContext } from "../../context/AppContext";
import { updateCheckin } from "../../api/checkins";

import "./Checkin.css";

const CheckinsHistory = ({ checkins, index, userProfile }) => {
  const { dispatch } = useContext(AppContext);
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
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    getPDLName();
  }, [pdlId]);

  let checkinDate =
    checkins.length > 0 ? new Date(checkins[index].checkInDate) : new Date();
  const lastIndex = checkins.length - 1;
  const leftArrowClass = "arrow " + (index > 0 ? "enabled" : "disabled");
  const rightArrowClass =
    "arrow " + (index < lastIndex ? "enabled" : "disabled");

  const previousCheckin = () => {
    if (index !== 0) {
      dispatch({ type: UPDATE_INDEX, payload: index - 1 });
    }
  };

  const nextCheckin = () => {
    if (index !== lastIndex) {
      dispatch({ type: UPDATE_INDEX, payload: index + 1 });
    }
  };

  const pickDate = (date) => {
    console.log({ date });
    // update checkin with new date
  };

  const DateInput = ({ value, onClick }) => (
    <div className="date-input">
      <p style={{ margin: "0px" }}>{value}</p>
      <CalendarTodayIcon onClick={onClick}>stuff</CalendarTodayIcon>
    </div>
  );

  return (
    <div>
      <div className="profile-section">
        <Avatar
          src={imageUrl ? imageUrl : "/default_profile.jpg"}
          style={{ height: "220px", width: "200px" }}
        />
        <div className="info">
          <p>{name}</p>
          <p>{role}</p>
          <p>PDL: {pdl}</p>
          <p>Company Email: {workEmail}</p>
        </div>
      </div>
      <div className="date-picker">
        <ArrowBackIcon
          className={leftArrowClass}
          onClick={previousCheckin}
          style={{ fontSize: "50px" }}
        />
        <DatePicker
          customInput={<DateInput />}
          dateFormat="MMMM dd, yyyy h:mm aa"
          selected={checkinDate}
          showTimeSelect
          onChange={pickDate}
        />
        <ArrowForwardIcon
          className={rightArrowClass}
          onClick={nextCheckin}
          style={{ fontSize: "50px" }}
        />
      </div>
    </div>
  );
};

export default CheckinsHistory;
