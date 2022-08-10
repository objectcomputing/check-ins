import React, { useContext, useState } from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Link, useHistory } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import AddOnboardeeModal from "../components/modal/AddOnboardeeModal";
import { AppContext } from "../context/AppContext";
import { createOnboardee } from "../api/onboardeeMember";
import { UPDATE_ONBOARDEE_MEMBER_PROFILES } from "../context/actions";

import { Button, Modal, Typography } from "@mui/material";

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

export default function OnboardProgressPage() {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, onboardeeMemberProfiles } = state;
  const [open, setOpen] = useState(false);
  const [AddOnboardeeModalBool, setAddOnboardeeModalBool] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
  };
  const handleSubmitClose = () => {
    setOpen(false);
    setAddOnboardeeModalBool(true);
  };
  const handleMsgModalClose = () => {
    setAddOnboardeeModalBool(false);
  };

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
      width: 400,
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
        <TextField
          id="input-with-icon-textfield"
          label="Search Onboardees"
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          variant="standard"
        />

        <Button
          onClick={handleOpen}
          variant="contained"
          sx={{ ml: "64%" }}
          startIcon={<PersonAddIcon />}
        >
          Add Onboardee
        </Button>
        <AddOnboardeeModal
          onboardee={{}}
          open={open}
          onClose={handleClose}
          onSave={async (onboardee) => {
            if (
              onboardee.employeeId &&
              onboardee.firstName &&
              onboardee.lastName &&
              onboardee.position &&
              onboardee.email &&
              onboardee.hireType &&
              onboardee.pdl &&
              csrf
            ) {
              let res = await createOnboardee(onboardee, csrf);
              let data =
                res.payload && res.payload.data && !res.error
                  ? res.payload.data
                  : null;
              if (data) {
                dispatch({
                  type: UPDATE_ONBOARDEE_MEMBER_PROFILES,
                  payload: [...onboardeeMemberProfiles, data],
                });
              }
              handleSubmitClose();
            }
          }}
        />
        <Modal
          open={AddOnboardeeModalBool}
          onClose={handleClose}
          aria-labelledby="title"
          aria-describedby="description"
        >
          <Box sx={modalBoxStyleMini}>
            <div
              style={{
                textAlign: "center",
                marginLeft: "auto",
                marginRight: "auto",
                marginTop: "auto",
                marginBottom: "auto",
              }}
            >
              <Typography variant="p" component="h3">
                Onboardee added!
              </Typography>
            </div>
            <div>
              <Button
                variant="contained"
                onClick={handleMsgModalClose}
                style={{
                  display: "flex",
                  justifyContent: "centered",
                  gap: "10px",
                }}
              >
                Okay
              </Button>
            </div>
          </Box>
        </Modal>
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
