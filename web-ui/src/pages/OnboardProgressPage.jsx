import React from "react";
import { DataGrid } from "@mui/x-data-grid";

import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import { Button, Modal, Typography } from "@mui/material";
import { useState } from "react";

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  backgroundColor: '#fff',
  border: '2px solid #000',
  boxShadow: 24,
  pt: 2,
  px: 4,
  pb: 3,
};

export default function OnboardProgressPage() {
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);


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
          variant="standard" />

        <Button onClick={handleOpen} variant="contained" sx={{ ml: "64%" }}>
          Add Onboardee
        </Button>
        <Modal
          open={open}
          onClose={handleClose}
          aria-labelledby="modal-modal-title"
          aria-describedby="modal-modal-description">
          <Box sx={style}>
            <Typography id="modal-modal-title" variant="h6" component="h2">
              Text in a modal
            </Typography>
            <Typography id="modal-modal-description" sx={{ mt: 2 }}>
              Duis mollis, est non commodo luctus, nisi erat porttitor ligula.
            </Typography>
          </Box>

        </Modal>
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
