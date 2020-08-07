import React, { useState } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import CircularProgress from "@material-ui/core/CircularProgress";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import DescriptionIcon from "@material-ui/icons/Description";

import "./Checkin.css";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const UploadDocs = () => {
  const [responseText, setResponseText] = useState("");
  const [severity, setSeverity] = useState("");
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setOpen(false);
  };

  async function onSubmit(e) {
    e.preventDefault();
    try {
      let input = document.querySelector('input[type="file"]');
      if (!input.files[0]) {
        //if user doesn't select a file
        setResponseText("Please select a file before uploading.");
        setSeverity("error");
        setLoading(false);
        setOpen(true);
        return;
      }

      setLoading(true);
      let body = new FormData();
      body.append("file", input.files[0]);

      const result = await fetch("/upload", { method: "POST", body });
      const resJson = await result.json();
      setResponseText(Object.values(resJson)[0]);
      Object.keys(resJson)[0] === "completeMessage"
        ? setSeverity("success")
        : setSeverity("error");
      setOpen(true);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      setOpen(false);
      console.log(error);
    }
  }
  return (
    <div className="documents">
      <div>
        <h1 className="title">
          <DescriptionIcon />
          Documents
        </h1>
        <form onSubmit={onSubmit}>
          <input
            type="file"
            name="file"
            id="file"
            className="uploader"
            onchange="updateName();"
          />
          {loading ? (
            <CircularProgress />
          ) : (
            <div style={{ display: "flex", alignItems: "center" }}>
              <button className="plus-button" type="submit" name="submit">
                <AddCircleIcon></AddCircleIcon>
              </button>
              Upload a document
            </div>
          )}
        </form>
        <Snackbar
          autoHideDuration={5000}
          open={open}
          onClose={handleClose}
          style={{ left: "56%", bottom: "50px" }}
        >
          <Alert onClose={handleClose} severity={severity}>
            {responseText}
          </Alert>
        </Snackbar>
      </div>
    </div>
  );
};

export default UploadDocs;
