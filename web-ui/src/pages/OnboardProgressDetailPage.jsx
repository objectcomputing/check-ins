import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Box } from "@mui/system";
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import "./../components/modal/Modal";
import getDocuments from "../api/signrequest";
import "./OnboardProgressDetailPage.css";
import Accordion from "../components/accordion/Accordion";
import { isArrayPresent } from "../utils/helperFunction";
import Modal from '@mui/material/Modal';


const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 800,
  backgroundColor: 'white',
  border: '2px solid #000',
  boxShadow: 50,
  p: 4,
};

export default function OnboardProgressDetailPage() {
  // get document info from signrequest API
  const [documentArr, setDocumentArr] = useState([]);
  useEffect(() => {
    async function getData() {
      let res = await getDocuments();
      let document;
      if (res && res.payload) {
        document =
          res?.payload?.data && !res.error ? res.payload.data : undefined;
        if (document) {
          setDocumentArr([...document.results]);
        }
      }
    }
    getData();
  }, []);

  // get user info from OnboardProgressPage
  const location = useLocation();
  const { name, email, hireType } = location.state;
  const [open, setOpen] = React.useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const accordionArr = [
    {
      title: "Personal Information",
    },
    {
      title: "Employment Eligbility",
    },
    {
      title: "Employment Desired and Availability",
    },
    {
      title: "Education",
    },
    {
      title: "Employment History",
    },
    {
      title: "Referral Type and Signature",
    },
  ];
  const columns = [
    { field: "id", headerName: "#", width: 50 },
    { field: "documentName", headerName: "Document Name", width: 300 },
    { field: "viewCheck", headerName: "Viewed", width: 100 },
    {
      field: "completed",
      headerName: "Completed",
      width: 100,
    },
    { field: "completeDate", headerName: "Date Completed", width: 150 },
    {
      field: "viewFile",
      headerName: "View File",
      renderCell: (cellValues) => {
        let documentNum = cellValues.row.id - 1;
        let fileLink = documentArr[documentNum].file_from_url;
        return (
          <a href={fileLink} target="_blank" rel="noreferrer">
            OPEN
          </a>
        );
      },
      width: 80,
    },
  ];

  const rows = documentArr.map((e, i) => ({
    id: i + 1,
    documentName: e.name,
    viewCheck: e.signrequest.signers[1].viewed === false ? "No" : "Yes",
    completed: e.status === "sd" || e.status === "si" ? "Yes" : "No",
    completeDate:
      e.signrequest.signers[1].signed === false
        ? "N/A"
        : e.signrequest.signers[1].signed_on,
  }));

  return (
    <div className="detail-onboard">
      <Box sx={{ height: 400, width: "30%", mt: "5%", ml: "5%" }}>
        <Button
          onClick={handleOpen}
          variant="contained"
          sx={{ fontSize: "1vw" }}
        >
          Personal Information
        </Button>
        <Modal
          open={open}
          onClose={handleClose}
        >
          <Box sx={modalStyle}>
          <div>
            {isArrayPresent(accordionArr) &&
              accordionArr.map((arr, i) => {
                return (
                  <Accordion
                    key={i}
                    title={arr.title}
                    open={i === 0 ? true : false}
                    index={i}
                    content={arr.content}
                  />
                );
              })}
          </div>
          </Box>
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
      <Box sx={{ height: 400, width: "30%", mt: "5%", ml: "5%" }}>
        <Button variant="contained" href="/onboard/progress">
          Back
        </Button>
      </Box>
    </div>
  );
}
