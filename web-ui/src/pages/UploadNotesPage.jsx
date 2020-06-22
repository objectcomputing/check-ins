import React, { useState } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import "./UploadNotesPage.css";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const HomePage = () => {
  const [responseText, setResponseText] = useState("");
  const [severity, setSeverity] = useState("");
  const [open, setOpen] = useState(false);

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
        setOpen(true);
        return;
      }

      let body = new FormData();
      body.append("file", input.files[0]);

      const result = await fetch("/upload", { method: "POST", body });
      const resJson = await result.json();
      setResponseText(Object.values(resJson)[0]);
      Object.keys(resJson)[0] === "completeMessage"
        ? setSeverity("success")
        : setSeverity("error");
      setOpen(true);
    } catch (error) {
      setOpen(false);
      console.log(error);
    }
  }
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <h2>Quarterly Development Check-ins</h2>
      <p>
        The role of Professional Development Lead is vital to the continued
        growth of our organization and our team members. Thank you for your time
        and dedication to our team!
      </p>
      <p>
        You can submit any Check-in notes or other relevant documentation that
        you would like to persist for future PDLs or for consideration in merit
        discussions via the form below. Access to files uploaded here will be
        restricted to the team member, as well as those who require them to
        perform their role (HR personnel, PDLs, etc).
      </p>
      <p>
        If you should require a copy of any of the documents that you have
        submitted here, please email&nbsp;
        <a href="mailto:hr@objectcomputing.com">hr@objectcomputing.com</a>
      </p>

      <p>
        Please note that the following form can only upload one file at a time
        (max size of 100MB).
      </p>
      {/* <form action="/upload" method="post" enctype="multipart/form-data"> */}
      <form onSubmit={onSubmit}>
        <fieldset id="upload-fs" className="fieldset">
          <input
            type="file"
            name="file"
            id="file"
            className="uploader"
            onchange="updateName();"
          />
          <label for="file" id="filesName">
            Choose a file
          </label>
        </fieldset>
        <p>
          <button type="submit" name="submit">
            Upload
          </button>
        </p>
        {/* <div className={className}>{responseText}</div> */}
      </form>
      <Snackbar open={open} autoHideDuration={5000} onClose={handleClose}>
        <Alert onClose={handleClose} severity={severity}>
          {responseText}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default HomePage;
