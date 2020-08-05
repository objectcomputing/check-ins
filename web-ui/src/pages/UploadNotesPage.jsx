import React, { useState } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import CircularProgress from "@material-ui/core/CircularProgress";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import DescriptionIcon from "@material-ui/icons/Description";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const HomePage = () => {
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
    <div
      style={{
        display: "flex",
        marginLeft: "150px",
        marginTop: "50px",
      }}
    >
      <div>
        <h1 style={{ display: "flex", alignItems: "center" }}>
          <DescriptionIcon />
          Documents
        </h1>
        <form onSubmit={onSubmit}>
          {/* <fieldset
        id="upload-fs" className="fieldset" style={{ border: "none" }}
        > */}
          <input
            type="file"
            name="file"
            id="file"
            className="uploader"
            onchange="updateName();"
          />
          {/* <label for="file" id="filesName">
            Choose a file
          </label> */}
          {/* </fieldset> */}
          {/* <p> */}
          {loading ? (
            <CircularProgress />
          ) : (
            <div style={{ display: "flex" }}>
              <button
                type="submit"
                name="submit"
                style={{
                  background: "none",
                  border: "none",
                  minWidth: "50px",
                  paddingLeft: "0px",
                }}
              >
                <AddCircleIcon></AddCircleIcon>
              </button>
              <p>Upload</p>
            </div>
          )}
          {/* </p> */}
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

export default HomePage;
