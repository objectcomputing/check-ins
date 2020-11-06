import React, { useContext, useEffect, useState } from "react";
import FileUploader from "./FileUploader";
import {
  getFiles,
  // getAllFiles,
  deleteFile,
  uploadFile,
} from "../../api/upload";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

import DescriptionIcon from "@material-ui/icons/Description";
import Button from "@material-ui/core/Button";
import { CircularProgress } from "@material-ui/core";

import "./Checkin.css";

const UploadDocs = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, currentCheckin, userProfile } = state;
  const { memberProfile } = userProfile;
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState([]);
  const [fileColors, setFileColors] = useState({});

  const pdlorAdmin =
    (memberProfile && userProfile.role && userProfile.role.includes("PDL")) ||
    userProfile.role.includes("ADMIN");
  const canView =
    pdlorAdmin && memberProfile.id !== currentCheckin.teamMemberId;
  const checkinId = currentCheckin && currentCheckin.id;

  useEffect(() => {
    async function getCheckinFiles() {
      try {
        let res = await getFiles(checkinId, csrf);
        if (res.error) throw new Error(res.error);
        let checkinFiles =
          res.payload && res.payload.data && res.payload.data.length > 0
            ? res.payload.data
            : null;
        if (checkinFiles) {
          setFiles(...files, checkinFiles);
          checkinFiles.forEach((file) => {
            setFileColors((fileColors) => ({
              ...fileColors,
              [file.name]: "green",
            }));
          });
        }
      } catch (e) {
        console.log(e);
      }
    }
    if (csrf) {
      getCheckinFiles();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [checkinId, csrf]);

  const handleFile = (file) => {
    if (csrf) {
      setFiles([...files, file]);
      addFile(file);
    }
  };

  const addFile = async (file) => {
    let formData = new FormData();
    formData.append("file", file);
    if (!file || files.includes(file)) {
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      let res = await uploadFile(formData, checkinId, csrf);
      if (res.error) throw new Error(res.error);
      const { data, status } = res.payload;
      if (status !== 200) {
        throw new Error("status equals " + status);
      }
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `${data.name} was successfully uploaded`,
        },
      });
      setFileColors((fileColors) => ({ ...fileColors, [file.name]: "green" }));
      setFiles([...files, data]);
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
          <div key={file.fileId} style={{ color: fileColors[file.name] }}>
            {file.name}
            <Button
              className="remove-file"
              onClick={async () => {
                if (csrf) {
                  await deleteFile(file.fileId, csrf);
                  setFiles(
                    files.filter((e) => {
                      return e.name !== file.name;
                    })
                  );
                }
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
      {canView && (
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
      )}
    </div>
  );
};

export default UploadDocs;
