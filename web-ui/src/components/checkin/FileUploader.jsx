import React from "react";
import Button from "@material-ui/core/Button";
import AddCircleIcon from "@material-ui/icons/AddCircle";
// Style the Button component

const FileUploader = (props) => {
  // Create a reference to the hidden file input element
  const { fileRef, handleFile } = props;

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
      <Button onClick={handleClick}>
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
