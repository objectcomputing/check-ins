import React, { useState } from "react";
import { Grid, Typography, Box, Divider } from "@mui/material";
import { Modal, TextField, IconButton } from "@mui/material";
import { Button } from "@mui/material";
import OnboardeeAddedModal from "./OnboardeeAddedModal";
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

const AddOnboardeeModal = ({ onboardee, open, onSave, onClose }) => {
  const [editedOnboardee, setOnboardee] = useState(onboardee);
  const [empFile, setEmpFile] = useState(" ");
  const [isNewOnboardee, setIsNewOnboardee] = useState(
    Object.keys(onboardee).length === 0 ? true : false
  );
  const handleEmployeeAgreement = (e) => {
    setEmpFile(e.target.value.replace(/^.*[\\/]/, ""));
  };

  const[offer, setOfferFile] = useState(" ");
  const handleOfferLetter = (e) => {
    setOfferFile(e.target.value.replace(/^.*[\\/]/, ""));
  };
  const submitOnboardeeClick = useCallback(async () => {
    onSave(editedOnboardee).then(() => {
      if (isNewOnboardee.current) {
        setOnboardee({ emptyOnboardee });
        setIsNewOnboardee(true);
      }
    });
  }, [onSave, editedOnboardee, isNewOnboardee]);

  return (
    <Modal open={open} onClose={onClose}>
      <Box sx={modalBoxStyle}>
        <Typography align="center" id="title" variant="h3" component="h2">
          Add Onboardee
        </Typography>
        <Grid container space={2}>
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
            <TextField
              sx={{ width: "75%" }}
              id="hireType"
              variant="standard"
              value={editedOnboardee.hireType ? editedOnboardee.hireType : ""}
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, hireType: e.target.value })
              }
            />
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
              onChange={(e) =>
                setOnboardee({ ...editedOnboardee, email: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              Manager:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="pdl"
              variant="standard"
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
            <Button variant="contained" onClick={submitOnboardeeClick}><OnboardeeAddedModal/></Button>
          </Grid>
        </Grid>
        
      </Box>
    </Modal>
  );
};
export default AddOnboardeeModal;
