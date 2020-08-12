import React, { useState } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import DescriptionIcon from "@material-ui/icons/Description";
import FileUploader from "./FileUploader";
import Button from "@material-ui/core/Button";
import { CircularProgress } from "@material-ui/core";
import { uploadFile } from "../../api/upload";

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
    setFiles([...files, file]);
    addFile(file);
  };

  const addFile = async (file) => {
    if (!file) {
      //if user doesn't select a file
      setResponseText("Please select a file before uploading.");
      setSeverity("error");
      setLoading(false);
      setOpen(true);
      return;
    }
    setLoading(true);
    let res = await uploadFile(file);
    if (res.error) {
      setLoading(false);
      setOpen(false);
      console.log(res.error);
    } else {
      const resJson = res.payload.data();
      console.log({ resJson });
      setResponseText(Object.values(resJson)[0]);
      Object.keys(resJson)[0] === "completeMessage"
        ? setSeverity("success")
        : setSeverity("error");
      setOpen(true);
      setLoading(false);
    }
  };

  // const removeFile = async (file) => {
  //   try {
  //     setLoading(true);

  //     let res = deleteFile(file);
  //     const resJson = await res.json();
  //     setResponseText(Object.values(resJson)[0]);
  //     Object.keys(resJson)[0] === "completeMessage"
  //       ? setSeverity("success")
  //       : setSeverity("error");
  //     setOpen(true);
  //     setLoading(false);
  //   } catch (error) {
  //     setLoading(false);
  //     setOpen(false);
  //     console.log(error);
  //   }
  // };

  const fileMapper = () => {
    const divs = files.map((file) => {
      if (file.name) {
        return (
          <div className="file-name" key={file.name}>
            {file.name}
            <Button
              className="remove-file"
              onClick={() => {
                setFiles(
                  files.filter((e) => {
                    return e.name !== file.name;
                  })
                );
              }}
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
        <div className="file-upload">
          <div className="file-name-container">{fileMapper()}</div>
          {loading ? (
            <CircularProgress />
          ) : (
            <FileUploader handleFile={handleFile} fileRef={hiddenFileInput} />
          )}
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
