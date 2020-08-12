import React from "react";
import Button from "@material-ui/core/Button";

import "./Modal.css";

const Modal = (props) => {
  const { close, show } = props;
  if (!show) {
    return null;
  }
  return (
    <div className="modal" id="modal">
      <div className="content">{props.children}</div>
      <div className="modal-wrapper">
        <Button
          style={{ backgroundColor: "lightgray", color: "white" }}
          onClick={close}
        >
          Cancel
        </Button>
        <Button
          style={{
            backgroundColor: "#3f51b5",
            color: "white",
            marginLeft: "10px",
          }}
          onClick={close}
        >
          Okay
        </Button>
      </div>
    </div>
  );
};

export default Modal;
