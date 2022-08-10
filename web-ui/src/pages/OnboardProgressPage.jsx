import React from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Link, useHistory } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import { Button } from "@mui/material";
import { AppContext } from "../context/AppContext";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import AddOnboardeeModal from "../components/modal/AddOnboardeeModal";
import { useState, useContext } from "react";
import { UPDATE_ONBOARDEE_MEMBER_PROFILES } from "../context/actions";
import { createOnboardee } from "../api/onboardeeMember";

export default function OnboardProgressPage(onboardee) {
  const [open, setOpen] = useState(false);
  const { state, dispatch } = useContext(AppContext);
  const { csrf, onboardeeProfiles } = state;
  const handleAddModalClose = () => setOpen(false);
  const handleOpen = () => setOpen(true);

  const history = useHistory();
  const handleRowClick = (name, email, hireType, userID, title) => {
    history.push({
      pathname: `/onboard/progress/${userID}`,
      state: {
        name: name,
        email: email,
        hireType: hireType,
        title: title,
      },
    });
  };

  const columns = [
    { field: "id", headerName: "ID", width: 50 },
    { field: "name", headerName: "Name", width: 130 },
    {
      field: "email",
      headerName: "Email",
      renderCell: (cellValues) => {
        return (
          <a href={"mailto:" + cellValues.row.email}>{cellValues.row.email}</a>
        );
      },
      width: 220,
    },
    { field: "title", headerName: "Title", width: 150 },
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
                title: cellValues.row.title,
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
      title: "Intern",
      completed: "No",
      dateAdded: "Jul 15, 2022",
      hireType: "Hourly",
    },
    {
      id: 2,
      name: "Brandon Li",
      email: "li.brandon@outlook.com",
      title: "Intern",
      completed: "No",
      dateAdded: "Jul 15, 2022",
      hireType: "Hourly",
    },
  ];

  const columnsNotif = [
    { field: "id", headerName: "#", width: 10 },
    {
      field: "notificationMsg",
      headerName: "Notification Message",
      flex: 1,
    },
  ];

  const rowsNotif = [
    {
      id: 1,
      notificationMsg: "daniel finished something.",
      userID: 1,
      name: "Daniel Ryu",
      email: "ryud@objectcomputing.com",
      title: "Intern",
      hireType: "Hourly",
    },
    {
      id: 2,
      notificationMsg: "Brandon did something.",
      userID: 2,
      name: "Brandon",
      email: "lib@objectcomputing.com",
      title: "Intern",
      hireType: "Hourly",
    },
    {
      id: 3,
      notificationMsg: "Daniel did something else.",
      userID: 1,
      name: "Daniel Ryu",
      email: "ryud@objectcomputing.com",
      title: "Intern",
      hireType: "Hourly",
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
        <AddOnboardeeModal
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
            ) {
              let res = await createOnboardee(onboardee, csrf);
              let data =
                res.payload && res.payload.data && !res.error
                  ? res.payload.data
                  : null;
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
                params.row.userID,
                params.row.title
              );
            }
          }}
        />
      </Box>
    </div>
  );
}
