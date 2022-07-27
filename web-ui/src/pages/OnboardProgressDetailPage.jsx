import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Box } from "@mui/system";
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import getDocuments from "../api/signrequest";
import "./OnboardProgressDetailPage.css";
import Accordion from "../components/accordion/Accordion";
import { isArrayPresent } from "../utils/helperFunction";
import Modal from "@mui/material/Modal";

const modalStyle = {
  position: "absolute",
  top: "50%",
  overflow: "auto",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 800,
  backgroundColor: "white",
  border: "2px solid #000",
  boxShadow: 50,
  p: 4,
};

export default function OnboardProgressDetailPage() {
  // get document info from signrequest API
  const [documentArr, setDocumentArr] = useState([]);
  // get user info from OnboardProgressPage
  const location = useLocation();
  const { name, email, hireType } = location.state;

  // This function gets the JSON from the localhost:8080/signrequest-documents and sets the JSON into an array.
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

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const accordionArr = [
    {
      title: "Personal Information",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
    {
      title: "Employment Eligbility",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
    {
      title: "Employment Desired and Availability",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
    {
      title: "Education",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
    {
      title: "Employment History",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
    {
      title: "Referral Type and Signature",
      content:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Et leo duis ut diam quam nulla. Et netus et malesuada fames ac turpis egestas maecenas. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Lacus suspendisse faucibus interdum posuere lorem.",
    },
  ];
  const documentColumns = [
    { field: "id", headerName: "#", width: 50, hide: true },
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
      // gets the uuid of the document from reading the same row cell value, and then finds the document with the same uuid,
      // then returns the url of the file. Note that you can only open google drive documents from this page currently.
      renderCell: (cellValues) => {
        let documentId = cellValues.row.id;
        let fileLink = documentArr.find((e) => {
          return e.uuid === documentId;
        }).file_from_url;

        if (fileLink === null) {
          return <p>File Unable to Open</p>;
        }
        return (
          <a href={fileLink} target="_blank" rel="noreferrer">
            OPEN
          </a>
        );
      },
      width: 150,
    },
  ];

  const documentRows = documentArr
    .filter((e) => e.signrequest.signers[1].email === email)
    .map((filteredE, i) => ({
      id: filteredE.uuid,
      documentName: filteredE.name,
      viewCheck:
        filteredE.signrequest.signers[1].viewed === false ? "No" : "Yes", //we check the element at index 1 of the signers array because it is the recipient's index.
      completed:
        filteredE.status === "sd" || filteredE.status === "si" ? "Yes" : "No", // si stands for signed, sd stands for signed and downloaded.
      completeDate:
        filteredE.signrequest.signers[1].signed === false
          ? "N/A"
          : filteredE.signrequest.signers[1].signed_on,
    }));

  const surveyColumns = [
    { field: "id", headerName: "Step", width: 50 },
    { field: "surveyName", headerName: "Survey Name", width: 300 },
    {
      field: "completed",
      headerName: "Completed",
      width: 100,
    },
  ];

  const surveyRows = [
    {
      id: 1,
      surveyName: "Personal Information",
      completed: "No",
    },
    {
      id: 2,
      surveyName: "IT Equipment Page",
      completed: "No",
    },
    {
      id: 3,
      surveyName: "About You",
      completed: "No",
    },
  ];

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
        <Modal open={open} onClose={handleClose}>
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

      <Box sx={{ height: 300, width: "80%", mt: "5%" }}>
        <h1>Documents</h1>
        <DataGrid
          rows={documentRows}
          columns={documentColumns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          disableSelectionOnClick
        />
        <h1>Survey</h1>
        <DataGrid
          rows={surveyRows}
          columns={surveyColumns}
          pageSize={5}
          rowsPerPageOptions={[5]}
          disableSelectionOnClick
        />
      </Box>

      <Box
        sx={{
          height: 50,
          width: "30%",
          mt: "5%",
          ml: "5%",
          display: "flex",
          flexDirection: "row",
          columnGap: "10px",
        }}
      >
        <Button variant="contained" href="/onboard/progress">
          Back
        </Button>
        <Button variant="contained">Edit</Button>
        <Button variant="contained">Finish Onboarding</Button>
        <Button variant="contained">Delete</Button>
      </Box>
    </div>
  );
}
