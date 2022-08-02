import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import {
  selectOrderedPdls,
  selectOrderedMemberFirstName,
  selectCurrentMembers,
} from "../../context/selectors";

import { Modal, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import DatePicker from "@mui/lab/DatePicker";
import { format } from "date-fns";
import { Button } from "@mui/material";
import { UPDATE_TOAST } from "../../context/actions";

import { useCallback } from "react";

const emptyOnboardee = {
  employeeId: "",
  firstName: "",
  lastName: "",
  position: "",
  email: "",
  hireType: "",
  pdl: "",
};

const OnboardeeModal = ({ onboardee, open, onSave, onClose }) => {
  const { state, dispatch } = useContext(AppContext);
  const onboardeeProfiles = selectCurrentOnboardee(state);
  const [editedOnboardee, setOnboardee] = useState(onboardee);
  const sortedPdls = selectOrderedPdls(state);
  const [isNewOnboardee, setIsNewOnboardee] = useState(
    Object.keys(member).length === 0 ? true : false
  );
  const sortedOnboardees = selectOrderedOnboardeeFirstName(state);

  const submitOnboardeeClick = useCallback(async () => {
    onSave(editedOnboardee).then(() => {
      if (isNewOnboardee.current) {
        setOnboardee({ emptyOnboardee });
        setIsNewOnboardee(true);
      }
    });
  }, [onSave, dispatch, editedOnboardee, isNewOnboardee]);

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
            <TextField
              sx={{ width: "75%" }}
              id="position"
              variant="outlined"
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
              variant="outlined"
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
            <TextField sx={{ width: "75%" }} id="email" variant="outlined" />
          </Grid>
          <Grid item xs={6}>
            <Typography id="description" sx={{ mt: 2 }}>
              PDL/Manager:
            </Typography>
            <TextField
              sx={{ width: "75%" }}
              id="pdl"
              variant="outlined"
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
            <Button variant="contained" onClock={submitOnboardeeClick}>Submit</Button>
          </Grid>
        </Grid>
      </Box>
    </Modal>
  );
};
