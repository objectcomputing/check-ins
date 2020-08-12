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
      <div>
        <div className="content">{props.children}</div>
        <Button onClick={close}>Okay</Button>
        <Button onClick={close}>Cancel</Button>
      </div>
    </div>
  );
};

export default Modal;
