import React from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Link, useHistory } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import { AppContext } from "../context/AppContext";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import AddOnboardeeModal from "../components/modal/AddOnboardeeModal";
import { Button, Typography } from "@mui/material";
import { useState, useContext } from "react";
import { UPDATE_ONBOARDEE_MEMBER_PROFILES } from "../context/actions";
import { createOnboardee, initializeOnboardee } from "../api/onboardeeMember";

export default function OnboardProgressPage(onboardee) {
  const [open, setOpen] = useState(false);
  const { state, dispatch } = useContext(AppContext);
  const { csrf, onboardeeProfiles } = state;
  const handleAddModalClose = () => setOpen(false);
  const handleOpen = () => setOpen(true);

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
                completed: cellValues.row.completed,
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

  async function submitInfo(onboardee) {
    console.log(
      "Submit info"
    )
    if (csrf) {

      console.log("CSRF", csrf)
      let res1 = await initializeOnboardee(onboardee.email, csrf);
      console.log(res1?.payload?.data?.success);

      if (res1?.payload?.data?.success === true) {
        let res2 = await createOnboardee(onboardee, csrf);
        console.log(res2);
        if (res2?.payload?.data?.success === true) {
          dispatch({
            type: UPDATE_ONBOARDEE_MEMBER_PROFILES,
            payload: [...onboardeeProfiles, res2.payload.data],
          });
        }
      }
    }
  }

  return (
    <div className="onboard-page">
      <Box sx={{ height: 400, width: "60%", mt: "5%" }}>
        <Button
          onClick={handleOpen}
          variant="contained"
          sx={{ ml: "83%" }}
          startIcon={<PersonAddIcon />}
        >
          Add Onboardee
        </Button>
        <AddOnboardeeModal
          onboardee={{}}
          open={open}
          onClose={handleAddModalClose}
          onSave={submitInfo}
        />
        <DataGrid
          className="grid"
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
        <Box className="notification-header">
          <Typography variant="h4" align="center">Notifications</Typography>
        </Box>
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