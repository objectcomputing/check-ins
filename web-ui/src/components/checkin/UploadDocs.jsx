import React, { useState } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import DescriptionIcon from "@material-ui/icons/Description";
import FileUploader from "./FileUploader";
import Button from "@material-ui/core/Button";

import "./Checkin.css";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const UploadDocs = () => {
  const [responseText, setResponseText] = useState("");
  const [severity, setSeverity] = useState("");
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState([]);

  const handleClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setOpen(false);
  };

  const handleFile = (file) => {
    console.log({ file });
    setFiles([...files, file]);
  };

  console.log({ files });

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

  const fileMapper = () => {
    const divs = files.map((file) => {
      if (file.name) {
        return (
          <div key={file.name}>
            {file.name}
            <Button
              onClick={() =>
                setFiles(
                  files.filter((e) => {
                    return e.name !== file.name;
                  })
                )
              }
            >
              X
            </Button>
          </div>
        );
      }
    });
    return divs;
  };

  const hiddenFileInput = React.useRef(null);

  return (
    <div className="documents">
      <div>
        <h1 className="title">
          <DescriptionIcon />
          Documents
        </h1>
        <div class="file-upload">
          {fileMapper()}
          <FileUploader handleFile={handleFile} fileRef={hiddenFileInput} />
        </div>
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
