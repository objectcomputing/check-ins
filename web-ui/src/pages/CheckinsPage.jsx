import React, { useState } from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinDocs from "../components/checkin/CheckinDocs";
import Personnel from "../components/personnel/Personnel";
import Modal from "../components/modal/Modal";
import Button from "@material-ui/core/Button";
import Notes from "../components/notes/Notes";

import "./CheckinsPage.css";

const CheckinsPage = () => {
  const [show, setShow] = useState(false);

  const showModal = () => {
    setShow(!show);
  };

  return (
    <div>
      <div className="container">
        <div className="contents">
          <CheckinsHistory />
        </div>
        <div className="right-sidebar">
          <Personnel />
        </div>
      </div>
      <Notes
        checkin={{
          id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1a",
          checkinid: "bf9975f8-a5b2-4551-b729-afd56b49e2cc",
          createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f28",
          description: "updated string",
        }}
        memberName={"Tester"}
        // TODO: get name of checkin member
      />
      <CheckinDocs />
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
