import React from "react";
import { useLocation } from "react-router-dom";
import { Box } from "@mui/system";
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import "./../components/modal/Modal"
import "./OnboardProgressDetailPage.css";
import Modal from "./../components/modal/Modal";
import { useState } from "react";

export default function OnboardProgressDetailPage() {
  const location = useLocation();
  const { name, email, hireType } = location.state;
  const [show, setShow] = useState(false);
  const columns = [
    { field: "id", headerName: "#", width: 50 },
    { field: "documentName", headerName: "Document Name", width: 300 },
    {
      field: "sentDate",
      headerName: "Sent Date",
      width: 150,
    },
    {
      field: "completed",
      headerName: "Completed",
      width: 100,
    },
    { field: "completeDate", headerName: "Date Completed", width: 150 },
  ];

  const rows = [
    {
      id: 1,
      documentName: "Employment Agreement",
      sentDate: "Jul 15th 2022",
      completed: "no",
      completeDate: "N/A",
    },
  ];
  return (
    <div className="detail-onboard">
      <Box sx={{ height: 400, width: "30%", mt: "5%", ml: "5%" }}>
        <Button onClick={()=>setShow(true)} variant="contained" sx={{ fontSize: "1vw" }}>
          Personal Information
        </Button>
        <Modal title="My Modal" onClose={() => setShow(false)} show={show}>
        <p>This is modal body</p>
      </Modal>
        <h1>Name: {name}</h1>
        <h1>Email: {email} </h1>
        <h1>Hire Type: {hireType}</h1>
      </Box>
      <Box sx={{ height: 400, width: "45%", mt: "5%" }}>
        <h1>Documents/Survey</h1>
        <DataGrid
          rows={rows}
          columns={columns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          disableSelectionOnClick
        />
      </Box>
    </div>
  );
}
