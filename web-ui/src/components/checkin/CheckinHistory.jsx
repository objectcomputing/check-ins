import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import { getCheckinByPdlId } from "../../api/checkins";

import Avatar from "@material-ui/core/Avatar";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import { getMember } from "../../api/member";

import "./Checkin.css";

const CheckinsHistory = ({ setIndex }) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { workEmail, role, id, pdlId } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};
  const { imageUrl, name } = userProfile ? userProfile : {};
  const [checkins, setCheckins] = useState([]);
  const [checkinIndex, setCheckinIndex] = useState(0);
  const [pdl, setPDL] = useState();

  // Get PDL's name
  React.useEffect(() => {
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

  // Get checkins
  React.useEffect(() => {
    async function updateCheckins() {
      if (id) {
        let res = await getCheckinByPdlId(id);
        let data =
          res && res.payload && res.payload.status === 200
            ? res.payload.data
            : null;
        let checkinList = data && !res.error ? data : [];
        checkinList.sort((a, b) => (a.checkInDate > b.checkInDate ? -1 : 1));
        setCheckins(checkinList);
      }
    }
    updateCheckins();
  }, [id]);

  let checkinDate =
    checkins.length > 0
      ? new Date(checkins[checkinIndex].checkInDate)
      : new Date();
  const lastIndex = checkins.length - 1;
  const leftArrowClass =
    "arrow " + (checkinIndex < lastIndex ? "enabled" : "disabled");
  const rightArrowClass =
    "arrow " + (checkinIndex > 0 ? "enabled" : "disabled");

  const previousCheckin = () => {
    setCheckinIndex((index) => (index === lastIndex ? lastIndex : index + 1));
    setIndex(checkinIndex);
    // TODO: change checkin on click
  };

  const nextCheckin = () => {
    setCheckinIndex((index) => (index === 0 ? 0 : index - 1));
    setIndex(checkinIndex);
  };

  const pickDate = (date) => {};

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
