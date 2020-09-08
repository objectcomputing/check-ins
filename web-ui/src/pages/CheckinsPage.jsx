import React, { useContext, useEffect, useState } from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinDocs from "../components/checkin/CheckinDocs";
import Personnel from "../components/personnel/Personnel";
import Modal from "../components/modal/Modal";
import GuidesPanel from "../components/guides/GuidesPanel";
import CheckinProfile from "../components/checkin/CheckinProfile";
import Note from "../components/notes/Note";
import { AppContext } from "../context/AppContext";

import Button from "@material-ui/core/Button";

import "./CheckinsPage.css";

const CheckinsPage = ({ history }) => {
  const [show, setShow] = useState(false);
  const { state } = useContext(AppContext);
  const { checkins, index, userProfile } = state;
  const checkin = checkins[index];

  useEffect(
    (checkin) => {
      if (checkin && checkin.id) {
        history.push(`/checkins/${checkin.id}`);
      }
    },
    [checkin, history]
  );

  const showModal = () => {
    setShow(!show);
  };

  return (
    <div>
      <div className="container">
        <div className="contents">
          <CheckinProfile state={state} />
          <CheckinsHistory checkins={checkins} index={index} />
          {checkin && checkin.id && (
            <Note checkin={checkin} memberName={userProfile.name} />
          )}
          <CheckinDocs />
        </div>
        <div className="right-sidebar">
          <Personnel />
          <GuidesPanel />
        </div>
      </div>
      <div className="modal-container">
        <Modal close={showModal} show={show}>
          The checkin will no longer be able to be edited. Are you sure that you
          are ready to close this check-in?
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
    </div>
  );
};

export default CheckinsPage;
