import React, { useContext, useState } from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinDocs from "../components/checkin/CheckinDocs";
import Personnel from "../components/personnel/Personnel";
import Modal from "../components/modal/Modal";
import Button from "@material-ui/core/Button";
import GuidesPanel from "../components/guides/GuidesPanel";
import ActionItemsPanel from "../components/action_item/ActionItemsPanel"
import Note from "../components/notes/Note";
import { AppContext } from "../context/AppContext";
import "./CheckinsPage.css";

const CheckinsPage = () => {
  const [show, setShow] = useState(false);
  const { state } = useContext(AppContext);
  const { checkins, userProfile } = state;
  const [index, setIndex] = useState(0);
  const checkin = checkins[index];

  const showModal = () => {
    setShow(!show);
  };
  return (
    <div>
      <div className="container">
        <div className="contents">
          <CheckinsHistory setIndex={setIndex} />
        </div>
        <div className="right-sidebar">
          <Personnel />
          <GuidesPanel />
        </div>
      </div>
      {checkin && checkin.id && (
        <Note
          checkin={checkin}
          memberName={userProfile.name}
        />
      )}
      <CheckinDocs />
      <div className="modal-container">
        <ActionItemsPanel checkinId="b92b2499-0951-4e9c-bfe4-a62f314f3fd2" />
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
