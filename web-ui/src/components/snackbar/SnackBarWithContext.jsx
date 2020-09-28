import React, { useContext } from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const SnackBarWithContext = () => {
  const { state, dispatch } = useContext(AppContext);
  window.snackDispatch = dispatch;
  const { severity, toast } = state.toast;

  const closeToast = () => {
    dispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: "",
        toast: "",
      },
    });
  };

  return (
    <Snackbar
      autoHideDuration={2500}
      open={toast !== "" && severity !== ""}
      onClose={closeToast}
      style={{ bottom: "10%" }}
      toast={toast}
    >
      {severity === "" ? null : (
        <Alert onClose={closeToast} severity={severity}>
          {toast}
        </Alert>
      )}
    </Snackbar>
  );
};

export default SnackBarWithContext;
