import React, { useContext, useEffect, useState } from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinDocs from "../components/checkin/CheckinDocs";
import Personnel from "../components/personnel/Personnel";
import Modal from "../components/modal/Modal";
import GuidesPanel from "../components/guides/GuidesPanel";
import CheckinProfile from "../components/checkin/CheckinProfile";
import ActionItemsPanel from "../components/action_item/ActionItemsPanel";
import AgendaItems from "../components/agenda/Agenda";
import Note from "../components/notes/Note";
import { AppContext } from "../context/AppContext";

import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";

import "./CheckinsPage.css";

const CheckinsPage = ({ history }) => {
  const [show, setShow] = useState(false);
  const { state } = useContext(AppContext);
  const { currentCheckin, userProfile, selectedProfile } = state;
  const memberProfile = userProfile ? userProfile.memberProfile : undefined;
  const id = memberProfile && memberProfile.id ? memberProfile.id : undefined;
  const canSeePersonnel =
    userProfile && userProfile.role && userProfile.role.includes("PDL");
  const canViewPrivateNote =
    memberProfile && currentCheckin && id !== currentCheckin.teamMemberId;

  useEffect(() => {
    if (currentCheckin && currentCheckin.id) {
      history.push(`/checkins/${currentCheckin.id}`);
    }
  }, [currentCheckin, history]);

  const showModal = () => {
    setShow(!show);
  };
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
                  <Modal close={showModal} show={show}>
                    The checkin will no longer be able to be edited. Are you
                    sure that you are ready to close this check-in?
                  </Modal>
                  <Button
                    style={{
                      backgroundColor: "#3f51b5",
                      color: "white",
                      display: show ? "none" : "",
                    }}
                    onClick={() => showModal()}
                  >
                    Submit
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
