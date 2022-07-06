import React, {useContext, useState} from "react";
import { useHistory, useParams } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import {
  selectCheckinsForMember,
  selectCsrfToken,
  selectCurrentUser,
  selectIsAdmin, selectIsPDL,
  selectProfile
} from "../../context/selectors";
import IconButton from "@mui/material/IconButton";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import {StaticDateTimePicker} from "@mui/lab";
import TextField from "@mui/material/TextField";

import "./Checkin.css";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle, Tooltip
} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import {updateCheckin} from "../../api/checkins";
import {UPDATE_CHECKIN, UPDATE_TOAST} from "../../context/actions";
import {createNewCheckinWithDate} from "../../context/thunks";

const PREFIX = "CheckinsPage";
const classes = {
  addButton: `${PREFIX}-addButton`
};

const CheckinsHistory = () => {
  const { state, dispatch } = useContext(AppContext);
  const { checkinId, memberId } = useParams();
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const [checkinDatetime, setCheckinDatetime] = useState(null);
  const [defaultDatetimeChanged, setDefaultDatetimeChanged] = useState(false);
  const [datetimeDialogOpen, setDatetimeDialogOpen] = useState(false);
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);

  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;

  const checkins = selectCheckinsForMember(state, memberId);
  const index = checkins.findIndex(checkin => checkin.id === checkinId);

  const selectedProfile = selectProfile(state, memberId);
  const memberCheckins = selectCheckinsForMember(
    state,
    selectedProfile ? selectedProfile.id : currentUserId
  );
  const hasOpenCheckins = memberCheckins.some((checkin) => !checkin.completed);

  const isAdmin = selectIsAdmin(state);
  const isPdl = selectIsPDL(state);

  const createCheckIn = async (date) => {
    const newId = await createNewCheckinWithDate(selectedProfile, date, dispatch, csrf);
    if (newId) history.push(`/checkins/${memberId}/${newId}`);
  }

  const updateCheckInDate = async (date) => {
    if (csrf) {
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hours = date.getHours();
      const minutes = date.getMinutes();
      const checkin = checkins[index];
      const dateArray = [year, month, day, hours, minutes, 0];
      const res = await updateCheckin({
        ...checkin,
        pdlId: selectedProfile?.pdlId,
        checkInDate: dateArray,
      }, csrf);
      const updatedCheckin = res.payload && res.payload.data && !res.error
        ? res.payload.data : null;
      updatedCheckin && dispatch({type: UPDATE_CHECKIN, payload: updatedCheckin});
    }
  };

  const handleSave = () => {
    setDatetimeDialogOpen(false);
    if (checkins) {
      if (checkins[index]) {
        updateCheckInDate(checkinDatetime);
      } else {
        console.log("creating new...");
        createCheckIn(checkinDatetime);
      }
    }
  }

  const handleCreateClick = async () => {
    if (!selectedProfile.pdlId) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "You must have an assigned PDL in order to create a Check In",
        },
      });
    } else {
      // Default date is 30 days in the future
      const futureDate = new Date();
      futureDate.setDate(futureDate.getDate() + 30);
      setCheckinDatetime(futureDate);
      setDatetimeDialogOpen(true);
    }
  };

  const getCheckinDate = () => {
    if (checkins && checkins[index]?.checkInDate) {
      const [year, month, day, hour, minute] = checkins[index].checkInDate;
      return new Date(year, month - 1, day, hour, minute, 0);
    }
    // return new date unless you are running a Jest test
    return process.env.JEST_WORKER_ID ? new Date(2020, 9, 21) : new Date();
  };

  const getFormattedCheckinDate = () => {
    let checkinDate = new Date();
    if (checkins && checkins[index]?.checkInDate) {
      const [year, month, day, hour, minute] = checkins[index].checkInDate;
      checkinDate = new Date(year, month - 1, day, hour, minute, 0);
    } else {
      return "No check-in selected";
    }
    const dateString = checkinDate.toDateString();
    const timeString = checkinDate.toLocaleTimeString([], {hour: "numeric", minute: "2-digit"});
    return `${dateString}, ${timeString}`;
  }

  const leftArrowClass = "arrow " + (index > 0 ? "enabled" : "disabled");
  const rightArrowClass =
    "arrow " + (index < checkins.length - 1 ? "enabled" : "disabled");

  const previousCheckin = () => {
    if (index > 0) {
      history.push(`/checkins/${memberId}/${checkins[index - 1].id}`);
    }
  };

  const nextCheckin = () => {
    if (index < checkins.length - 1) {
      history.push(`/checkins/${memberId}/${checkins[index + 1].id}`);
    }
  };

  return (
    <>
      {getCheckinDate() && (
      <div className="date-picker">
        <IconButton
          disabled={index <= 0}
          aria-label="Previous Check-in`"
          onClick={previousCheckin}
          size="large">
          <ArrowBackIcon
            className={leftArrowClass}
            style={{ fontSize: "1.2em" }}
          />
        </IconButton>
        <Dialog className="check-in-datetime-dialog" open={datetimeDialogOpen}>
          <DialogTitle>Check-In Date</DialogTitle>
          <DialogContent>
            {(checkins && checkins[index]?.checkInDate ? false : !defaultDatetimeChanged) &&
              <Alert severity="info" style={{width: "300px"}}>By default, this check-in is set 30 days in the future</Alert>
            }
            <StaticDateTimePicker
              onChange={(datetime) => {
                setCheckinDatetime(datetime);
                setDefaultDatetimeChanged(true);
              }}
              value={checkinDatetime}
              ampm
              hideTabs
              format="MMMM dd, yyyy @hh:mm aaaa"
              minDate={new Date()}
              disablePast
              displayStaticWrapperAs="mobile"
              label=" "
              renderInput={(props) => (
                <TextField fullWidth {...props} />
              )}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => {
              setCheckinDatetime(null);
              setDefaultDatetimeChanged(false);
              setDatetimeDialogOpen(false);
            }} style={{color: "gray"}}>Cancel</Button>
            <Button onClick={() => handleSave()} color="primary">Save</Button>
          </DialogActions>
        </Dialog>
        <TextField
          placeholder="testing"
          value={getFormattedCheckinDate()}
          label="Check-In Date"
          onClick={() => {
            if (checkins?.length && !checkins[index]?.completed) {
              setDatetimeDialogOpen(true);
            }
          }}
          style={{width: "18em"}}
          disabled={!checkins?.length || checkins[index]?.completed}
          focused={false}
        />
        <IconButton
          disabled={index >= checkins.length - 1}
          aria-label="Next Check-in`"
          onClick={nextCheckin}
          size="large">
          <ArrowForwardIcon
            className={rightArrowClass}
            style={{ fontSize: "1.2em" }}
          />
        </IconButton>
      </div>)}
      <Tooltip
        open={tooltipIsOpen && hasOpenCheckins}
        onOpen={() => setTooltipIsOpen(true)}
        onClose={() => setTooltipIsOpen(false)}
        enterTouchDelay={0}
        placement="top-start"
        title={
          "This is disabled because there is already an open Check-In"
        }
      >
        <div
          aria-describedby="checkin-tooltip-wrapper"
          className="create-checkin-tooltip-wrapper"
        >
          {(isAdmin || isPdl || currentUserId === memberId) && (
            <Button
              disabled={hasOpenCheckins}
              className={classes.addButton}
              startIcon={<CheckCircleIcon />}
              onClick={handleCreateClick}
            >
              Create Check-In
            </Button>
          )}
        </div>
      </Tooltip>
    </>
  );
};

export default CheckinsHistory;
