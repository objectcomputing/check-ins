import React from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import UploadDocs from "../components/checkin/UploadDocs";
import Personnel from "../components/personnel/Personnel";
import Button from "@material-ui/core/Button";

import "./CheckinsPage.css";

const CheckinsPage = () => {
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
      <UploadDocs />
      <Button
        onClick={() => alert("The checkin will no longer be able to be edited")}
        style={{ backgroundColor: "#255aa8", color: "white" }}
      >
        Submit Check-in
      </Button>
    </div>
  );
};

export default CheckinsPage;
