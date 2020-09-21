import React, { useContext, useEffect, useState } from "react";

import {
  UPDATE_CURRENT_CHECKIN,
  UPDATE_CHECKINS,
} from "../../context/AppContext";
import { AppContext } from "../../context/AppContext";
import { updateCheckin } from "../../api/checkins";

import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

import "./Checkin.css";

const CheckinsHistory = ({ history }) => {
  const { state, dispatch } = useContext(AppContext);
  const { checkins, currentCheckin } = state;
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (checkins.length) {
      const { pathname } = document.location;
      const [, , checkinid] = pathname.split("/");
      const i = checkinid
        ? checkins.findIndex((checkin) => checkin.id === checkinid)
        : checkins.length - 1;
      setIndex(i);
      const checkin = checkins[index];
      dispatch({ type: UPDATE_CURRENT_CHECKIN, payload: checkin });
      history.push(`/checkins/${checkin.id}`);
    }
  }, [checkins, index, dispatch, history]);

  const getCheckinDate = () => {
    if (currentCheckin.checkInDate) {
      return new Date(currentCheckin.checkInDate);
    }
    return new Date();
  };

  const lastIndex = checkins.length - 1;
  const leftArrowClass = "arrow " + (index > 0 ? "enabled" : "disabled");
  const rightArrowClass =
    "arrow " + (index < lastIndex ? "enabled" : "disabled");

  const previousCheckin = () => {
    if (index !== 0) {
      setIndex((index) => index - 1);
    }
  };

  const nextCheckin = () => {
    if (index !== lastIndex) {
      setIndex((index) => index + 1);
    }
  };

  const pickDate = async (date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const checkin = checkins[index];
    const dateArray = [year, month, day];
    const updatedCheckin = await updateCheckin({
      ...checkin,
      checkInDate: dateArray,
    });
    const newCheckin = updatedCheckin.payload.data;
    const filtered = checkins.filter((e) => {
      return e.id !== checkin.id;
    });
    filtered.push(newCheckin);
    dispatch({
      type: UPDATE_CHECKINS,
      payload: filtered,
    });
    dispatch({
      type: UPDATE_CURRENT_CHECKIN,
      payload: newCheckin,
    });
  };

  const DateInput = React.forwardRef((props, ref) => (
    <div className="date-input" ref={ref}>
      <p style={{ margin: "0px" }}>{props.value}</p>
      <CalendarTodayIcon onClick={props.onClick}>stuff</CalendarTodayIcon>
    </div>
  ));

  return (
    <div>
      {getCheckinDate() && (
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
            selected={getCheckinDate()}
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
