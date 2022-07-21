import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Link } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";

export default function OnboardProgressPage() {
  const columns = [
    { field: "id", headerName: "ID", width: 50 },
    { field: "name", headerName: "Name", width: 130 },
    { field: "email", headerName: "Email", width: 220 },
    { field: "hireType", headerName: "Hire Type", width: 150 },
    { field: "progress", headerName: "Progress", width: 150 },
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
      email: "ryud@objectcomputing.com",
      hireType: "Intern",
      progress: "Incomplete",
      dateAdded: "Jul 15, 2022",
    },
    {
      id: 2,
      name: "Brandon Li",
      email: "brandonli@objectcomputing.com",
      hireType: "Intern",
      progress: "Incomplete",
      dateAdded: "Jul 15, 2022",
    },
  ];

  const columnsNotif = [
    { field: "id", headerName: "#", width: 10 },
    {
      field: "notificationMsg",
      headerName: "Notification Message",
      width: 1200,
    },
  ];

  const rowsNotif = [{ id: 1, notificationMsg: "daniel finished something" }];

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

        <Button variant="contained" sx={{ ml: "64%" }}>
          Add Onboardee
        </Button>
        <DataGrid
          rows={rows}
          columns={columns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          checkboxSelection
          disableSelectionOnClick
        />
      </Box>
      <Box sx={{ height: 400, width: "20%", mt: "3%" }}>
        <h1>Notifications</h1>
        <DataGrid
          rows={rowsNotif}
          columns={columnsNotif}
          pageSize={5}
          rowsPerPageOptions={[10]}
        />
      </Box>
    </div>
  );
}
