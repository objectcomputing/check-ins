import React, { useContext, useState } from "react";
import { useHistory } from "react-router-dom";
import {
  Grid,
  Typography,
  Box,
  Divider,
  TextField,
  IconButton,
  Button,
} from "@mui/material";
import { useCallback } from "react";
import FileUploadIcon from "@mui/icons-material/FileUpload";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import OnboardeeResetModal from "./OnboardeeResetModal";
import { AppContext } from "../../context/AppContext";
import { UPDATE_TOAST } from "../../context/actions";
import OnboardeeEditedModal from "./OnboardeeEditedModal";

const modalBoxStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "75%",
  backgroundColor: "#fff",
  border: "2px solid #000",
  boxShadow: 24,
  pt: 2,
  px: 4,
  pb: 3,
  m: 2,
};
const emptyOnboardee = {
  employeeId: "",
  firstName: "",
  lastName: "",
  position: "",
  email: "",
  hireType: "",
  manager: "",
};

const EditOnboardee = ({ onboardee, open, onSave, onClose }) => {
  const history = useHistory();
  const { dispatch } = useContext(AppContext);
  const [editedOnboardee, setOnboardee] = useState(onboardee);
  const [empFile, setEmpFile] = useState(" ");
  const [isNewOnboardee, setIsNewOnboardee] = useState(
    Object.keys(onboardee).length === 0 ? true : false
  );
  const [offer, setOfferFile] = useState(" ");
  const [open2, setOpen2] = useState(false);
  const [resetBool, setResetBool] = useState(false);

  const handleReturn = () => {
    history.push({ pathname: "/onboard/progress" });
  };

  const [openSavedModal, setOpenSavedModal] = useState(false);

  const handleCloseEdit = () => {
    setOpenSavedModal(false);
  };

  const handleClose = () => {
    setOpen2(false);
  };

  const handleAccountReset = () => {
    setOpen2(false);
  };

  const handleEmployeeAgreement = (e) => {
    setEmpFile(e.target.value.replace(/^.*[\\/]/, ""));
    setResetBool(true);
  };

  const handleOfferLetter = (e) => {
    setOfferFile(e.target.value.replace(/^.*[\\/]/, ""));
    setResetBool(true);
  };

  const validateInputs = useCallback(() => {
    let regEmail =
      /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/; // eslint-disable-line
    if (!regEmail.test(editedOnboardee.email)) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Please enter a valid email",
        },
      });
      return false;
    }
    return true;
  }, [editedOnboardee, dispatch]);
  const validateRequiredInputsPresent = useCallback(() => {
    return (
      editedOnboardee.firstName?.length > 0 &&
      editedOnboardee.lastName?.length > 0 &&
      editedOnboardee.email?.length > 0 &&
      editedOnboardee.postition?.length > 0 &&
      editedOnboardee.hireType?.length > 0 &&
      editedOnboardee.employeeId?.length > 0 &&
      editedOnboardee.manager?.length > 0
    );
  }, [editedOnboardee]);

  const submitOnboardeeClick = useCallback(async () => {
    let required = validateRequiredInputsPresent();

    let inputsFeasible = validateInputs();
    if (!required) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast:
            "One or more required fields are empty. Check starred input fields",
        },
      });
    } else if (required && inputsFeasible) {
      onSave(editedOnboardee).then(() => {
        if (isNewOnboardee.current) {
          setOnboardee({ emptyOnboardee });
          setIsNewOnboardee(true);
        }
      });
    }
  }, [
    validateRequiredInputsPresent,
    onSave,
    dispatch,
    validateInputs,
    editedOnboardee,
    isNewOnboardee,
  ]);

  async function resetOnboardeeClick() {
    // TODO: query backend to delete user

    setOpen2(true);
    onSave(editedOnboardee).then(() => {
      if (isNewOnboardee.current) {
        setOnboardee({ emptyOnboardee });
        setIsNewOnboardee(true);
      }
      handleReturn();
    });
  }

  return (
    <>
      <OnboardeeResetModal
        open={open2}
        onClose={handleClose}
        onSave={handleAccountReset}
      />
      <Box sx={modalBoxStyle}>
        <Typography align="center" id="title" variant="h3" component="h2">
          Edit Onboardee
        </Typography>
        <Grid container space={2}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              First Name:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="firstName"
              variant="standard"
              value={editedOnboardee.firstName ? editedOnboardee.firstName : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, firstName: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Last Name:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="lastName"
              variant="standard"
              value={editedOnboardee.lastName ? editedOnboardee.lastName : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, lastName: e.target.value })
              }
            />
          </Grid>
        </Grid>
        <Grid container space={2}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Position:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="position"
              variant="standard"
              value={editedOnboardee.position ? editedOnboardee.position : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, position: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Hire Type:
            </Typography>
            <Select
              sx={{ width: "75%" }}
              id="hireType"
              variant="standard"
              value={editedOnboardee.hireType ? editedOnboardee.hireType : ""}
              onChange={(e) => {
                setOnboardee({ ...editedOnboardee, hireType: e.target.value });
              }}
            >
              <MenuItem value={"hourly"}>Hourly</MenuItem>
              <MenuItem value={"fulltime"}>FullTime</MenuItem>
              <MenuItem value={"contract"}>Contract</MenuItem>
            </Select>
          </Grid>
        </Grid>
        <Grid container space={3}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              External Email:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="email"
              variant="standard"
              value={editedOnboardee.email ? editedOnboardee.email : ""}
              onChange={(e) => {
                setOnboardee({ ...editedOnboardee, email: e.target.value });
                setResetBool(true);
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Manager:
            </Typography>
            <Select
              sx={{ width: "75%" }}
              id="manager"
              variant="standard"
              value={editedOnboardee.manager ? editedOnboardee.manager : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, manager: e.target.value })
              }
            >
              <MenuItem value={"dummy1"}>dummy1</MenuItem>
              <MenuItem value={"dummy2"}>dummy2</MenuItem>
              <MenuItem value={"dummy3"}>dummy3</MenuItem>
            </Select>
          </Grid>
        </Grid>
        <Divider sx={{ m: 3 }} variant="middle" />
        <Grid
          container
          flexDirection="column"
          alignItems="center"
          rowSpacing={3}
        >
          <Grid item xs={"auto"}>
            <Typography
              align="center"
              id="description"
              sx={{ mt: 0, display: "inline-flex" }}
            >
              Offer Letter:
            </Typography>
            <IconButton component="label">
              <input
                hidden
                accept=".pdf"
                type="file"
                id="offerLetter"
                onChange={handleOfferLetter}
              />
              <FileUploadIcon />
            </IconButton>
            <Typography
              sx={{
                display: "inline-flex",
                fontStyle: "italic",
                fontSize: 12,
                marginLeft: 5,
              }}
            >
              {offer}
            </Typography>
          </Grid>
          <Grid item id="description" xs={"auto"}>
            <Typography
              align="center"
              id="description"
              sx={{ mt: 2, display: "inline-flex" }}
            >
              Employment Agreement:
            </Typography>
            <IconButton component="label">
              <input
                hidden
                accept=".pdf"
                type="file"
                id="empAgreement"
                onChange={handleEmployeeAgreement}
              />
              <FileUploadIcon />
            </IconButton>
            <Typography
              sx={{
                display: "inline-flex",
                fontStyle: "italic",
                fontSize: 12,
                marginLeft: 5,
              }}
            >
              {empFile}
            </Typography>
          </Grid>
        </Grid>
        <Grid container>
          <Grid
            item
            xs={6}
            style={{ display: "flex", justifyContent: "flex-start" }}
          >
            <Button variant="contained" onClick={onClose}>
              Cancel
            </Button>
          </Grid>
          <Grid
            item
            xs={6}
            style={{ display: "flex", justifyContent: "flex-end" }}
          >
            {!resetBool ? (
              ((
                <OnboardeeEditedModal
                  open={openSavedModal}
                  onClose={handleCloseEdit}
                />
              ),
              (
                <Button variant="contained" onClick={submitOnboardeeClick}>
                  Submit
                </Button>
              ))
            ) : (
              <Button variant="contained" onClick={resetOnboardeeClick}>
                Reset Onboardee
              </Button>
            )}
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

export default EditOnboardee;