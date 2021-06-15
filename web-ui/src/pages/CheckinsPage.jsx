import React, { useContext, useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import ActionItemsPanel from "../components/action_item/ActionItemsPanel";
import AgendaItems from "../components/agenda/Agenda";
import { AppContext } from "../context/AppContext";
import { selectMostRecentCheckin, selectCurrentUser, selectIsAdmin, selectIsPDL, selectCsrfToken, selectCheckin, selectProfile } from "../context/selectors";
import { getCheckins, createNewCheckin } from "../context/thunks";
import { UPDATE_CHECKIN } from "../context/actions";
import CheckinDocs from "../components/checkin/documents/CheckinDocs";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import Profile from "../components/profile/Profile";
import GuidesPanel from "../components/guides/GuidesPanel";
import PDLGuidesPanel from "../components/guides/PDLGuidesPanel";
import Note from "../components/notes/Note";
import PrivateNote from "../components/private-note/PrivateNote";
import Personnel from "../components/personnel/Personnel";
import { makeStyles } from "@material-ui/core/styles";
import { Button, Grid, Modal } from "@material-ui/core";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";


import "./CheckinsPage.css";
import {updateCheckin} from "../api/checkins";

const useStyles = makeStyles({
  navigate: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "baseline",
  },
  addButton: {
    height: "3em",
  }
});

const CheckinsPage = () => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const history = useHistory();
  const { memberId, checkinId } = useParams();
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const mostRecent = selectMostRecentCheckin(state, memberId);
  const selectedProfile = selectProfile(state, memberId);

  useEffect(() => {
    if(selectedProfile) {
        getCheckins(memberId, selectedProfile.pdlId, dispatch, csrf);
    }
  }, [memberId,selectedProfile,csrf,dispatch])

  useEffect(() => {
    if (memberId === undefined) {
      history.push(`/checkins/${currentUserId}`);
    } else if (checkinId === undefined && mostRecent) {
      history.push(`/checkins/${memberId}/${mostRecent.id}`);
    }
  }, [currentUserId, memberId, checkinId, mostRecent, history]);

  const currentCheckin = selectCheckin(state, checkinId);
  const isAdmin = selectIsAdmin(state);
  const isPdl = selectIsPDL(state);

  const canViewPrivateNote = (isAdmin || selectedProfile?.pdlId === currentUserId) && currentUserId !== memberId;

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const completeCheckin = async () => {
    if (csrf) {
      const res = await updateCheckin({...currentCheckin, pdlId: selectedProfile.pdlId, completed: true}, csrf);
      const updated =
                res.payload && res.payload.data && !res.error
                  ? res.payload.data
                  : null;
      dispatch({type: UPDATE_CHECKIN, payload: updated});
    }
    handleClose();
  };

  const handleCreate = async () => {
    const newId = await createNewCheckin(selectedProfile, dispatch, csrf);
    if (newId) history.push(`/checkins/${memberId}/${newId}`);
  };

  return (
    <div style={{padding:12}}>
    <Grid container spacing={3} >
      <Grid item xs={12} sm={9}>
        <Profile memberId={selectedProfile?.id || currentUserId} pdlId={currentCheckin?currentCheckin.pdlId:null} />
        <div className={classes.navigate}>
          <CheckinsHistory history={history} memberId={memberId} checkinId={checkinId} />
          { ( isAdmin || isPdl || currentUserId === memberId ) && (<Button className={classes.addButton} startIcon={<CheckCircleIcon/>} onClick={handleCreate}>Create Check-In</Button>) }
        </div>
        {currentCheckin && currentCheckin.id && (
          <React.Fragment>
            <AgendaItems
              checkinId={currentCheckin.id}
              memberName={
                selectedProfile
                  ? selectedProfile.name
                  : memberProfile.name
              }
            />
            <ActionItemsPanel
              checkinId={currentCheckin.id}
              memberName={
                selectedProfile
                  ? selectedProfile.name
                  : memberProfile.name
              }
            />
            <Note
              memberName={
                selectedProfile
                  ? selectedProfile.name
                  : memberProfile.name
              }
            />
            {canViewPrivateNote && (
            <PrivateNote
              memberName={
                selectedProfile
                  ? selectedProfile.name
                  : memberProfile.name
              }
            />
            )}
            <CheckinDocs />
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
                      <Button
                          color="primary"
                          onClick={completeCheckin}
                      >
                        Complete and Close
                      </Button>
                    </div>
                  </div>
                </Modal>
                <Button
                  disabled={currentCheckin?.completed}
                  color="primary"
                  onClick={handleOpen}
                  variant="contained"
                >
                  Complete and Close Checkin
                </Button>
              </div>
            )}
          </React.Fragment>
        )}
      </Grid>
      <Grid item xs={12} sm={3}>
          <div className="right-sidebar">
            {isPdl && <Personnel history={history} />}
            <GuidesPanel />
            <PDLGuidesPanel />
          </div>
      </Grid>
    </Grid>
    </div>
  );
};

export default CheckinsPage;
