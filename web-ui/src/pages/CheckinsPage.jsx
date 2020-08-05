import React from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import UploadNotesPage from "../pages/UploadNotesPage";
import Button from "@material-ui/core/Button";

const CheckinsPage = () => {
  return (
    <div>
      <CheckinsHistory />
      <UploadNotesPage />
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
