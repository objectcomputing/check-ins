import React, { useContext, useState } from "react";
import FileUploader from "./FileUploader";
import { uploadFile } from "../../api/upload";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

import DescriptionIcon from "@material-ui/icons/Description";
import Button from "@material-ui/core/Button";
import { CircularProgress } from "@material-ui/core";

import "./Checkin.css";

const UploadDocs = () => {
  const { dispatch } = useContext(AppContext);
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState([]);
  const [fileColors, setFileColors] = useState({});

  const handleFile = (file) => {
    setFiles([...files, file]);
    addFile(file);
  };

  const addFile = async (file) => {
    let formData = new FormData();
    formData.append("file", file);
    if (!file) {
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      let res = await uploadFile(formData);
      if (res.error) throw new Error(res.error);
      const { data, status } = res.payload;
      if (status !== 200) throw new Error("status equals " + status);
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: data.completeMessage,
        },
      });
      setFileColors((fileColors) => ({ ...fileColors, [file.name]: "green" }));
    } catch (e) {
      setFileColors((fileColors) => ({ ...fileColors, [file.name]: "red" }));
      console.log({ e });
    } finally {
      setLoading(false);
    }
  };

  const fileMapper = () => {
    const divs = files.map((file) => {
      if (!file.name) {
        return null;
      } else {
        return (
          <div key={file.name} style={{ color: fileColors[file.name] }}>
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
      </div>
    </div>
  );
};

export default UploadDocs;
