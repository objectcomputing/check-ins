import React, { useState } from "react";
import {
  Grid,
  TextField,
  Typography,
  Box,
  Divider,
  Modal,
  IconButton,
  Button,
} from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { UPDATE_TOAST } from "../../context/actions";
import { useCallback } from "react";
import FileUploadIcon from "@mui/icons-material/FileUpload";
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
  pdl: "",
};

const posOptions = ["dummy1", "dummy2", "dummy3"];
const hireOptions = ["dummy4", "dummy5", "dummy6"];
const pdlOptions = ["dummy7", "dummy8", "dummy9"];

const AddOnboardeeModal = ({ onboardee, open, onSave, onClose }) => {
  const [editedOnboardee, setOnboardee] = useState(onboardee);
  const [emptyFile, setEmptyFile] = useState(" ");

  const [isNewOnboardee, setIsNewOnboardee] = useState(
    Object.keys(onboardee).length === 0 ? true : false
  );
  const handleEmployeeAgreement = (e) => {
    setEmptyFile(e.target.value.replace(/^.*[\\/]/, ""));
  };
  const [offer, setOfferFile] = useState(" ");
  const handleOfferLetter = (e) => {
    setOfferFile(e.target.value.replace(/^.*[\\/]/, ""));
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
      editedOnboardee.pdl?.length > 0
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

  return (
    <Modal open={open} onClose={onClose}>
      <Box sx={modalBoxStyle}>
        <Typography align="center" id="title" variant="h3" component="h2">
          Add Onboardee
        </Typography>
        <Grid container space={2}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Position:
            </Typography>
            <Autocomplete
              disablePortal
              options={posOptions}
              sx={{ width: "75%" }}
              renderInput={(option) => (
                <TextField
                  variant="outlined"
                  {...option}
                  sx={{ width: "75%" }}
                  id="position"
                />
              )}
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

            <Autocomplete
              disablePortal
              options={hireOptions}
              sx={{ width: "75%" }}
              renderInput={(option) => (
                <TextField
                  variant="outlined"
                  {...option}
                  sx={{ width: "75%" }}
                  id="hireType"
                />
              )}
              value={editedOnboardee.hireType ? editedOnboardee.hireType : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, hireType: e.target.value })
              }
            />
          </Grid>
        </Grid>
        <Grid container space={2}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              First Name:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="firstName"
              variant="outlined"
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
              variant="outlined"
              value={editedOnboardee.lastName ? editedOnboardee.lastName : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, lastName: e.target.value })
              }
            />
          </Grid>
        </Grid>
        <Grid container space={3}>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Email:
            </Typography>

            <TextField
              sx={{ width: "75%" }}
              id="email"
              variant="outlined"
              value={editedOnboardee.email ? editedOnboardee.email : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, email: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              PDL/Manager:
            </Typography>
            <Autocomplete
              disablePortal
              options={pdlOptions}
              sx={{ width: "75%" }}
              renderInput={(option) => (
                <TextField
                  variant="outlined"
                  {...option}
                  sx={{ width: "75%" }}
                  id="pdl"
                />
              )}
              value={editedOnboardee.pdl ? editedOnboardee.pdl : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, pdl: e.target.value })
              }
            />
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
              {emptyFile}
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
            <Button variant="contained" onClick={submitOnboardeeClick}>
              Submit
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Modal>
  );
};

export default AddOnboardeeModal;
