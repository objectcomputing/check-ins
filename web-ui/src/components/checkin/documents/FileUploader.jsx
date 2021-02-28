import React, { useContext } from "react";
import { useParams } from "react-router-dom";
import { AppContext } from "../../../context/AppContext";
import { selectCheckin } from "../../../context/selectors";
import Button from "@material-ui/core/Button";
import AddCircleIcon from "@material-ui/icons/AddCircle";
// Style the Button component

const FileUploader = ({ fileRef, handleFile }) => {
  const { state } = useContext(AppContext);
  const { checkinId } = useParams();
  const currentCheckin = selectCheckin(state, checkinId);

  // Programatically click the hidden file input element when the Button component is clicked
  const handleClick = (event) => {
    fileRef.current.click();
  };
  // Call a function (passed as a prop from the parent component) to handle the user-selected file
  const handleChange = (event) => {
    const fileUploaded = event.target.files[0];
    handleFile(fileUploaded);
  };
  return (
    <>
      <Button
        disabled={currentCheckin?.completed}
        onClick={handleClick}
      >
        <AddCircleIcon></AddCircleIcon>Upload a document
      </Button>
      <input
        type="file"
        ref={fileRef}
        onChange={handleChange}
        style={{ display: "none" }} /* Make the file input element invisible */
      />
    </>
  );
};

export default FileUploader;
