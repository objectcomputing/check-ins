import React from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const SnackBar = ({ handleClose, open, severity, toast }) => {
  return (
    <Snackbar
      autoHideDuration={2500}
      open={open}
      onClose={handleClose}
      style={{ bottom: "10%" }}
    >
      <Alert onClose={handleClose} severity={severity ? severity : "error"}>
        {toast}
      </Alert>
    </Snackbar>
  );
};

export default SnackBar;
