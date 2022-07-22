import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Link } from "react-router-dom";
import "./OnboardProgressPage.css";
import { Box } from "@mui/system";
import FileUploadIcon from "@mui/icons-material/FileUpload"
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import { Button, Modal, Typography, Grid, Divider, Select, IconButton } from "@mui/material";
import { useState, useEffect } from "react";

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '75%',
  backgroundColor: '#fff',
  border: '2px solid #000',
  boxShadow: 24,
  pt: 2,
  px: 4,
  pb: 3,
  m: 2,
};

export default function OnboardProgressPage() {
  const [open, setOpen] = useState(false);
  const [empFile, setEmpFile] = useState('temp');

  // useEffect(() => {
  //   setEmpFile(document.getElementById('empAgreement'));
  // }, [document.getElementById('empAgreement').name])

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

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

        <Button onClick={handleOpen} variant="contained" sx={{ ml: "64%" }}>
          Add Onboardee
        </Button>
        <Modal
          open={open}
          onClose={handleClose}
          aria-labelledby="title"
          aria-describedby="description">
          <Box sx={style}>
            <Typography align="center" id="title" variant="h3" component="h2">
              Add Onboardee:
            </Typography>
            <Grid container space={2}>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  Position:
                </Typography>
                <Select sx={{ width: "75%" }} label="position"></Select>
              </Grid>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  Hire Type:
                </Typography>
                <Select sx={{ width: "75%" }}></Select>
              </Grid>
            </Grid>
            <Grid container space={2}>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  First Name:
                </Typography>
                <TextField sx={{ width: "75%" }} variant="outlined" />
              </Grid>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  Last Name:
                </Typography>
                <TextField sx={{ width: "75%" }} variant="outlined" />
              </Grid>
            </Grid>
            <Grid container space={3}>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  Email:
                </Typography>
                <TextField sx={{ width: "75%" }} variant="outlined" />
              </Grid>
              <Grid item xs={6}>
                <Typography id="description" sx={{ mt: 2 }}>
                  PDL/Manager:
                </Typography>
                <Select sx={{ width: "75%" }}></Select>
              </Grid>
            </Grid>
            <Divider sx={{ m: 3 }} variant="middle" />
            <Grid container flexDirection="column" alignItems="center" rowSpacing={3}>
              <Grid item xs={'auto'}>
                <Typography align="center" id="description" sx={{ mt: 0, display: "inline-flex" }}>
                  Offer Letter:
                </Typography>
                  <input accept=".pdf" type="file" id="offerLetter" />         
              </Grid>
              <Grid item id="description" xs={'auto'}>
                <Typography align="center" id="description" sx={{ mt: 2, display: "inline-flex" }}>
                  Employment Agreement:
                </Typography>
                  <input accept=".pdf" type="file" id="empAgreement" />
              </Grid>
            </Grid>
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
