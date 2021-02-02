import React, { useContext, useEffect, useState } from "react";

import ActionItemsPanel from "../components/action_item/ActionItemsPanel";
import AgendaItems from "../components/agenda/Agenda";
import {AppContext, UPDATE_CHECKINS, UPDATE_CURRENT_CHECKIN} from "../context/AppContext";
import CheckinDocs from "../components/checkin/CheckinDocs";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinProfile from "../components/checkin/CheckinProfile";
import GuidesPanel from "../components/guides/GuidesPanel";
import Note from "../components/notes/Note";
import Personnel from "../components/personnel/Personnel";

import { Button, Container, Grid, Modal } from "@material-ui/core";

import "./CheckinsPage.css";
import {updateCheckin} from "../api/checkins";

const CheckinsPage = ({ history }) => {
  const [open, setOpen] = useState(false);
  const { state } = useContext(AppContext);
  const { currentCheckin, userProfile, selectedProfile, csrf } = state;
  const memberProfile = userProfile ? userProfile.memberProfile : undefined;
  const id = memberProfile && memberProfile.id ? memberProfile.id : undefined;
  const canSeePersonnel =
    userProfile && userProfile.role && userProfile.role.includes("PDL");
  const canViewPrivateNote =
    memberProfile && currentCheckin && id !== currentCheckin.teamMemberId;

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const getCheckinDate = () => {
    console.log("currentCheckin.checkInDate: "+currentCheckin.checkInDate);
    if (currentCheckin && currentCheckin.checkInDate) {
      const [year, month, day, hour, minute] = currentCheckin.checkInDate;
      console.log("year: "+year+" month: "+month+" day: "+day)
      return new Date(year, month - 1, day, hour, minute, 0);
    }
    // return new date unless you are running a Jest test
    return process.env.JEST_WORKER_ID ? new Date(2020, 9, 21) : new Date();
  };

  const completeCheckin = async () => {
    if (csrf) {
      currentCheckin.completed = true;
      currentCheckin.checkInDate = getCheckinDate();
      let checkinDate = getCheckinDate();
      const updatedCheckin = await updateCheckin({
        ...currentCheckin,
        checkinDate
      }, csrf);
      // const newCheckin = updatedCheckin.payload.data;

      // dispatch({
      //   type: UPDATE_CURRENT_CHECKIN,
      //   payload: newCheckin,
      // });
    }
  };

  useEffect(() => {
    if (currentCheckin && currentCheckin.id) {
      history.push(`/checkins/${currentCheckin.id}`);
    }
  }, [currentCheckin, history]);

  return (
    <div>
      <Container maxWidth="xl">
        <Grid container spacing={3}>
          <Grid item sm={9} justify="center">
            <Container maxWidth="md">
              <div className="contents">
                <CheckinProfile />
                <CheckinsHistory history={history} />
                {currentCheckin && currentCheckin.id && (
                  <React.Fragment>
                    <Note
                      memberName={
                        selectedProfile
                          ? selectedProfile.name
                          : userProfile.name
                      }
                    />
                    <ActionItemsPanel
                      checkinId={currentCheckin.id}
                      memberName={
                        selectedProfile
                          ? selectedProfile.name
                          : userProfile.name
                      }
                    />
                    <AgendaItems
                      checkinId={currentCheckin.id}
                      memberName={
                        selectedProfile
                          ? selectedProfile.name
                          : userProfile.name
                      }
                    />
                    <CheckinDocs />
                  </React.Fragment>
                )}
              </div>
              {canViewPrivateNote && (
                <div className="modal-container">
                  <Modal open={open} close={handleClose}>
                    <div className="submit-checkin-modal">
                      The Check-In will no longer be able to be edited. Are you
                      sure that you are ready to close this Check-In?
                      <div className="submit-modal-actions">
                        <Button onClick={handleClose} color="secondary">
                          Cancel
                        </Button>
                        <Button color="primary">Complete and Close</Button>
                      </div>
                    </div>
                  </Modal>
                  <Button
                    color="primary"
                    onClick={() => {
                      // handleOpen();
                      completeCheckin();
                      handleClose();
                    }}
                    variant="contained"
                  >
                    Complete and Close Checkin
                  </Button>
                </div>
              )}
            </Container>
          </Grid>
          <Grid item sm={3} justify="flex-end">
            <Container maxWidth="md">
              <div className="right-sidebar">
                {canSeePersonnel && <Personnel history={history} />}
                <GuidesPanel />
              </div>
            </Container>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

export default CheckinsPage;
