import React, { useContext, useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Box } from "@mui/system";
import { Button, Grid } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import getDocuments from "../api/signrequest";
import "./OnboardProgressDetailPage.css";
import Accordion from "../components/accordion/Accordion";
import EditOnboardModal from "../components/modal/EditOnboardeeModal";
import { isArrayPresent } from "../utils/helperFunction";
import Modal from "@mui/material/Modal";
import ProgressIndicator from "../components/onboard_progress/ProgressIndicator";
import { UPDATE_ONBOARDEE_MEMBER_PROFILES } from "../context/actions";
import { updateOnboardee } from "../api/onboardeeMember";
import { AppContext } from "../context/AppContext";

const modalStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "75%",
  backgroundColor: "#fff",
  border: "2px solid #000",
  boxShadow: 24,
  pt: 2,
  px: 4,
  pb: 3,
  m: 2,
};

export default function OnboardProgressDetailPage(onboardee){
  // get document info from signrequest API
  const [documentArr, setDocumentArr] = useState([]);
  const location = useLocation();
  const { state, dispatch } = useContext(AppContext);
  const { csrf , onboardeeProfiles} = state;
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
  const [openEdit, setOpenEdit] = useState(false);
  const handleOpenEdit = () => setOpenEdit(true);
  const handleCloseEdit = () => setOpenEdit(false);
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
    { field: "viewCheck", headerName: "Viewed", width: 60 },
    {
      field: "completed",
      headerName: "Completed",
      width: 80,
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
          return <p>Cannot Open</p>;
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
      completed: "Yes",
    },
    {
      id: 2,
      surveyName: "IT Equipment Page",
      completed: "Yes",
    },
    {
      id: 3,
      surveyName: "About You",
      completed: "No",
    },
  ];
  return (
    <div className="detail-onboard">
      <Grid container >
        <Grid item xs={5}>
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
                  <div>
                    <Button
                      variant="contained"
                      onClick={handleClose}
                      sx={{ fontSize: "1vw" }}
                    >
                      Close
                    </Button>
                  </div>
                </div>
              </Box>
            </Modal>

            <EditOnboardModal
              onboardee={onboardee}
              open={openEdit}
              onClose={handleCloseEdit}
              onSave={async (onboardee) => {
                let res = await updateOnboardee(onboardee, csrf);
                let data =
                  res.payload && res.payload.data && !res.error
                    ? res.payload.data
                    : null;
                if (data) {
                  const copy = [...onboardeeProfiles];
                  const index = copy.findIndex(
                    (profile) => profile.id === data.id
                  );
                  copy[index] = data;
                  dispatch({
                    type: UPDATE_ONBOARDEE_MEMBER_PROFILES,
                    payload: copy,
                  });
                  handleClose();
                }
              }}
            />
           
      

            <h1>Name: {name}</h1>
            <h1>Email: {email} </h1>
            <h1>Hire Type: {hireType}</h1>
          </Box>
        </Grid>

        <Grid item xs={7} sx={{ height: 650 }}>
          <Box sx={{ height: 250, width: "100%", mt: "5%" }}>
            <div style={{ display: "flex" }}>
              <h1>Documents/Surveys</h1>
              <ProgressIndicator
                dataDocument={documentRows}
                dataSurvey={surveyRows}
              />
            </div>

            <DataGrid
              rows={documentRows}
              columns={documentColumns}
              pageSize={5}
              rowsPerPageOptions={[5]}
              disableSelectionOnClick
            />
            <DataGrid
              rows={surveyRows}
              columns={surveyColumns}
              pageSize={5}
              rowsPerPageOptions={[5]}
              disableSelectionOnClick
            />
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Button variant="contained" href="/onboard/progress">
            Back
          </Button>
        </Grid>
        <Grid item xs={6} style={{ display: "flex", justifyContent: "flex-end"}}>
          <Button
            variant="contained"
            onClick={handleOpenEdit}
            sx={{ fontSize: "1vw" }}
          >
            Edit Onboardee
          </Button>

          <Button variant="contained">Finish Onboarding</Button>
          <Button variant="contained">Delete</Button>
        </Grid>
      </Grid>
    </div>
  );
}
