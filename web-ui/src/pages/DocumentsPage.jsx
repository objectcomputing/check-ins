import React, {useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import {
  Button,
  InputAdornment,
  TextField,
  Typography
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import AddIcon from "@mui/icons-material/Add";
import "./DocumentsPage.css";
import {AppContext} from "../context/AppContext";
import {getAllDocuments} from "../api/document";
import DocumentModal from "../components/document_modal/DocumentModal";
import DocumentCard from "../components/document_card/DocumentCard";
import {UPDATE_TOAST} from "../context/actions";

const Root = styled("div")({
  margin: "4rem"
});

const DocumentsPage = () => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [documents, setDocuments] = useState([]);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);

  useEffect(() => {
    const loadDocuments = async () => {
      const res  = await getAllDocuments(csrf);
      const data = res && res.payload && res.payload.data ? res.payload.data : null;
      if (data) {
        return data;
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve documents"
          }
        });
      }
    }

    loadDocuments().then(data => {
      if (data) {
        setDocuments(data);
      }
    });
  }, [csrf, dispatch]);

  useEffect(() => {
    console.log(documents);
  }, [documents]);

  return (
    <Root className="documents-page">
      <DocumentModal
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
      />
      <div className="documents-header">
        <Typography variant="h4">Documents</Typography>
        <div className="documents-header-actions">
          <TextField
            label="Search"
            placeholder="Document Name"
            style={{ width: "400px" }}
            InputProps={{
              endAdornment: (
                <InputAdornment style={{ color: "gray"}} position="end">
                  <SearchIcon/>
                </InputAdornment>
              )
            }}
          />
          <Button
            variant="contained"
            startIcon={<AddIcon/>}
            onClick={() => setCreateDialogOpen(true)}
          >New Document</Button>
        </div>
      </div>
      <div className="documents-list">
        {documents.map(document => (
          <DocumentCard key={document.id} document={document}/>
        ))}
      </div>
    </Root>
  );

};

export default DocumentsPage;