import React, { useContext } from "react";
import SnackBar from "./SnackBar";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

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
    <SnackBar
      handleClose={closeToast}
      open={toast !== "" && severity !== ""}
      toast={toast}
    />
  );
};

export default SnackBarWithContext;
