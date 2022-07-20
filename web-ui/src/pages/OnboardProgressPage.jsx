import React from "react";
import { DataGrid } from "@mui/x-data-grid";

import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";

export default function OnboardProgressPage() {
  const columns = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "name", headerName: "Name", width: 100 },
    { field: "progress", headerName: "Progress", width: 150 },
    { field: "dateAdded", headerName: "Date Added", width: 150 },
    { field: "openButton", headerName: "Details", width: 150 },
  ];

  const rows = [
    {
      id: 1,
      name: "Daniel Ryu",
      progress: "Incomplete",
      dateAdded: "Jul 15, 2022",
      openButton: "open",
    },
  ];

  const columnsNotif = [
    { field: "id", headerName: "#", width: 10 },
    { field: "notificationMsg", headerName: "Notification Message", width: 1200 },
  ];

  const rowsNotif = [{ id: 1, notificationMsg: "daniel finished something" }];

  return (
    <div className="onboard-page">
      <Box sx={{ height: 400, width: "60%" ,mt:"5%"}}>
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
        <Button variant="contained" sx={{ml:"64%" }} >Add Onboardee</Button>
        <DataGrid
          rows={rows}
          columns={columns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          checkboxSelection
          disableSelectionOnClick
        />
      </Box>
      <Box sx={{height:400, width: "20%", mt:"3%"}}>
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
