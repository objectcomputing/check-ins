import React, { useContext, useEffect, useState } from "react";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import { UPDATE_INDEX } from "../../context/AppContext";
import { AppContext } from "../../context/AppContext";
import { updateCheckin } from "../../api/checkins";

import "./Checkin.css";

const CheckinsHistory = ({ checkins, index }) => {
  const { dispatch } = useContext(AppContext);
  const [checkinDate, setCheckinDate] = useState(null);

  useEffect(() => {
    if (checkins[index]) {
      setCheckinDate(new Date(checkins[index].checkInDate));
    }
  }, [checkins]);

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

  const pickDate = async (date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const checkin = checkins[index];
    const dateArray = [year, month, day];
    await updateCheckin({
      ...checkin,
      checkInDate: dateArray,
    });
    setCheckinDate(new Date(dateArray));
  };

  const DateInput = ({ value, onClick }) => (
    <div className="date-input">
      <p style={{ margin: "0px" }}>{value}</p>
      <CalendarTodayIcon onClick={onClick}>stuff</CalendarTodayIcon>
    </div>
  );

  return (
    <div>
      {checkinDate && (
        <div className="date-picker">
          <ArrowBackIcon
            className={leftArrowClass}
            onClick={previousCheckin}
            style={{ fontSize: "50px" }}
          />
          <DatePicker
            closeOnScroll
            customInput={<DateInput />}
            dateFormat="MMMM dd, yyyy h:mm aa"
            onChange={pickDate}
            selected={checkinDate}
            showTimeSelect
            withPortal
          />
          <ArrowForwardIcon
            className={rightArrowClass}
            onClick={nextCheckin}
            style={{ fontSize: "50px" }}
          />
        </div>
      )}
    </div>
  );
};

export default CheckinsHistory;
