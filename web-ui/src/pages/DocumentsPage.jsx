import React, {useCallback, useContext, useEffect, useState} from "react";
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
import {addRoleAccessToDocument, createDocument, getAllDocuments} from "../api/document";
import DocumentModal from "../components/document_modal/DocumentModal";
import DocumentCard from "../components/document_card/DocumentCard";
import {UPDATE_TOAST} from "../context/actions";
import {selectRoles} from "../context/selectors";

const Root = styled("div")({
  margin: "4rem"
});

const DocumentsPage = () => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const allRoles = selectRoles(state);
  const [documents, setDocuments] = useState([]);
  const [filteredDocuments, setFilteredDocuments] = useState([]);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const loadDocuments = useCallback(async () => {
    const res = await getAllDocuments(csrf);
    const data = res && res.payload && res.payload.data ? res.payload.data : null;
    if (data) {
      data.forEach((element) => {
        if (!("roles" in element)) {
          element.roles = [];
        }
      });
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
  }, [csrf, dispatch]);


  useEffect(() => {
    loadDocuments().then(data => {
      if (data) {
        data.sort((a, b) => a.name.localeCompare(b.name));
        setDocuments(data);
      }
    });
  }, [loadDocuments, csrf]);

  useEffect(() => {
    if (searchText.trim()) {
      const filtered = documents.filter(doc => doc.name.toLowerCase().includes(searchText.trim().toLowerCase()));
      setFilteredDocuments(filtered);
    } else {
      setFilteredDocuments(documents);
    }
  }, [documents, searchText]);

  const createNewDocument = async (documentInfo) => {
    const res = await createDocument({
      name: documentInfo.name,
      description: documentInfo.description,
      url: documentInfo.url
    });

    const docData = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (docData) {
      const documentId = docData.id;
      for (let roleName of documentInfo.roles) {
        const roleId = allRoles.find(role => role.role === roleName).id;
        const roleRes = await addRoleAccessToDocument(roleId, documentId, csrf);
        const roleData = roleRes && roleRes.payload && roleRes.payload.data && !roleRes.error ? roleRes.payload.data : null;
        if (!roleData) {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: `Failed to give access to role ${roleName}`
            }
          });
        }
      }

      loadDocuments().then(data => {
        if (data) {
          data.sort((a, b) => a.name.localeCompare(b.name));
          setDocuments(data);
          setCreateDialogOpen(false);
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "success",
              toast: `Saved new document "${docData.name}"`
            }
          });
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to save document"
        }
      });
    }
  }

  return (
    <Root className="documents-page">
      <DocumentModal
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        onSave={createNewDocument}
      />
      <div className="documents-header">
        <Typography variant="h4">Documents</Typography>
        <div className="documents-header-actions">
          <TextField
            label="Search"
            placeholder="Document Name"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
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
        {filteredDocuments.length === 0
          ? (
            <div className="empty-documents-message">
              {documents.length === 0 ? "No documents exist" : `No documents matching "${searchText}"`}
            </div>
          )
          : (
            filteredDocuments.map(document => (
              <DocumentCard key={document.id} document={document}/>
            ))
          )
        }
      </div>
    </Root>
  );
};

export default DocumentsPage;