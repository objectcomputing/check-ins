import React from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Link, useHistory } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import { AppContext } from "../context/AppContext";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import AddOnboardModal from "../components/modal/AddOnboardeeModal";
import OnboardeeAddedModal from "../components/modal/OnboardeeAddedModal";
import {
  Grid,
  Button,
  Modal,
  Typography
} from "@mui/material";
import { useState, useContext} from "react";
import { UPDATE_ONBOARDEE_MEMBER_PROFILES } from "../context/actions";
import { createOnboardee } from "../api/onboardeeMember";


const modalBoxStyleMini = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "25%",
  backgroundColor: "#fff",
  border: "2px solid #000",
  boxShadow: 24,
  pt: 2,
  px: 4,
  pb: 3,
  m: 2,
};

export default function OnboardProgressPage(onboardee){
  const [open, setOpen] = useState(false);
  //const [empFile, setEmpFile] = useState(" ");
  //const [offer, setOfferFile] = useState(" ");
  const [addOnboardeeModal, setAddOnboardeeModal] = useState(false);
  const { state, dispatch } = useContext(AppContext);
  const { csrf , onboardeeProfiles} = state;

  const handleAddModalClose = () => setOpen(false);
  const handleAddModalOpen = () => setOpen(true);

  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    //setEmpFile(" ");
    //setOfferFile(" ");
  };
  // const handleSubmitClose = () => {
  //   setOpen(false);
  //   setAddOnboardeeModal(true);
  // };
  const handleMsgModalClose = () => {
    setAddOnboardeeModal(false);
  };
  // const handleEmployeeAgreement = (e) => {
  //   //setEmpFile(e.target.value.replace(/^.*[\\/]/, ""));
  // };
  // const handleOfferLetter = (e) => {
  //   //setOfferFile(e.target.value.replace(/^.*[\\/]/, ""));
  // };

  const history = useHistory();
  const handleRowClick = (name, email, hireType, userID) => {
    history.push({
      pathname: `/onboard/progress/${userID}`,
      state: {
        name: name,
        email: email,
        hireType: hireType,
      },
    });
  };

  const columns = [
    { field: "id", headerName: "ID", width: 50 },
    { field: "name", headerName: "Name", width: 130 },
    { field: "email", headerName: "Email", width: 220 },
    { field: "hireType", headerName: "Hire Type", width: 150 },
    {
      field: "completed",
      headerName: "Completed",
      width: 150,
    },
    { field: "dateAdded", headerName: "Date Added", width: 100 },
    {
      field: "Open Progress Details",
      renderCell: (cellValues) => {
        return (
          <Link
            to={{
              pathname: `/onboard/progress/${cellValues.row.id}`,
              state: {
                name: cellValues.row.name,
                email: cellValues.row.email,
                hireType: cellValues.row.hireType,
              },
            }}
          >
            OPEN
          </Link>
        );
      },
      width: 200,
    },
  ];

  const rows = [
    {
      id: 1,
      name: "Daniel Ryu",
      email: "d97shryu@gmail.com",
      hireType: "Intern",
      completed: "No",
      dateAdded: "Jul 15, 2022",
    },
    {
      id: 2,
      name: "Brandon Li",
      email: "li.brandon@outlook.com",
      hireType: "Intern",
      completed: "No",
      dateAdded: "Jul 15, 2022",
    },
  ];

  const columnsNotif = [
    { field: "id", headerName: "#", width: 10 },
    {
      field: "notificationMsg",
      headerName: "Notification Message",
      flex: 1
    },
  ];

  const rowsNotif = [
    {
      id: 1,
      notificationMsg: "daniel finished something.",
      userID: 1,
      name: "Daniel Ryu",
      email: "ryud@objectcomputing.com",
      hireType: "Intern",
    },
    {
      id: 2,
      notificationMsg: "Brandon did something.",
      userID: 2,
      name: "Brandon",
      email: "lib@objectcomputing.com",
      hireType: "Intern",
    },
    {
      id: 3,
      notificationMsg: "Daniel did something else.",
      userID: 1,
      name: "Daniel Ryu",
      email: "ryud@objectcomputing.com",
      hireType: "Intern",
    },
  ];

  return (
    <div className="onboard-page">
      <Box sx={{ height: 400, width: "60%", mt: "5%" }}>
        <Button
          onClick={handleOpen}
          variant="contained"
          sx={{ ml: "64%" }}
          startIcon={<PersonAddIcon />}
        >
          Add Onboardee
        </Button>
        {/* <Modal
          open={open}
          onClose={handleClose}
          aria-labelledby="title"
          aria-describedby="description"
        >
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
                    <TextField variant="outlined" {...option} />
                  )}
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
                    <TextField variant="outlined" {...option} />
                  )}
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
                    <TextField variant="outlined" {...option} />
                  )}
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
                <Button variant="contained" onClick={handleClose}>
                  Cancel
                </Button>
              </Grid>
              <Grid
                item
                xs={6}
                style={{ display: "flex", justifyContent: "flex-end" }}
              >
                <Button variant="contained" onClick={handleSubmitClose}>
                  Submit
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Modal> */}
         <AddOnboardModal
              onboardee={{}}
              open={open}
              onClose={handleAddModalClose}
              onSave={async (onboardee) => {
                if (
                  onboardee.firstName &&
                  onboardee.lastName &&
                  onboardee.position &&
                  onboardee.email &&
                  onboardee.hireType &&
                  onboardee.pdl &&
                  csrf
                )
                {
                  let res = await createOnboardee(onboardee, csrf);
                  let data = res.payload && res.payload.data && !res.error ? res.payload.data : null;
                  if (data) {
                    dispatch({
                      type: UPDATE_ONBOARDEE_MEMBER_PROFILES,
                      payload: [...onboardeeProfiles, data],
                    });
                  }
                  }
              }}
            />
        <DataGrid
          rows={rows}
          columns={columns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          checkboxSelection
          disableSelectionOnClick
          components={{ Toolbar: GridToolbar }}
          componentsProps={{
            toolbar: {
              showQuickFilter: true,
              quickFilterProps: { debounceMs: 500 },
            },
          }}
        />
      </Box>
      <Box sx={{ height: 400, width: "20%", mt: "3%" }}>
        <h1>Notifications</h1>
        <DataGrid
          sx={{ cursor: "pointer" }}
          rows={rowsNotif}
          columns={columnsNotif}
          pageSize={5}
          rowsPerPageOptions={[10]}
          onRowClick={(params, event) => {
            if (!event.ignore) {
              handleRowClick(
                params.row.name,
                params.row.email,
                params.row.hireType,
                params.row.userID
              );
            }
          }}
        />
      </Box>
    </div>
  );
}
