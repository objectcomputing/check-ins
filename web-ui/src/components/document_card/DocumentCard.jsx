import React, {useContext, useState} from "react";
import {
  Alert,
  Button,
  Card,
  CardContent,
  CardHeader,
  Collapse,
  Dialog, DialogActions, DialogContent, DialogContentText,
  DialogTitle,
  IconButton,
  Tooltip,
  Typography
} from "@mui/material";
import OpenLinkIcon from "@mui/icons-material/OpenInNew";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import "./DocumentCard.css";
import DocumentModal from "../document_modal/DocumentModal";
import PropTypes from "prop-types";
import {AppContext} from "../../context/AppContext";
import {selectRoles} from "../../context/selectors";
import {addRoleAccessToDocument, deleteDocument, removeRoleAccessToDocument, updateDocument} from "../../api/document";
import {UPDATE_TOAST} from "../../context/actions";

const propTypes = {
  document: PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    description: PropTypes.string,
    url: PropTypes.string.isRequired,
    roles: PropTypes.arrayOf(PropTypes.object).isRequired
  }).isRequired,
  onChange: PropTypes.func
};

const DocumentCard = ({ document, onChange }) => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [cardExpanded, setCardExpanded] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const allRoles = selectRoles(state);

  const getRolesText = () => {
    if (document.roles.length === 0) {
      return "No roles";
    }

    const roleIds = document.roles.map(role => role.roleDocumentId.roleId);
    const roles = allRoles
      .filter(role => roleIds.includes(role.id))
      .map(role => {
        if (role.role === "PDL") {
          return `${role.role}s`;
        } else {
          return `${role.role.charAt(0).toUpperCase()}${role.role.slice(1).toLowerCase()}s`;
        }
      });
    return roles.join(", ");
  }

  const updateThisDocument = async (documentInfo) => {
    setEditDialogOpen(false);
    const res = await updateDocument({
      id: document.id,
      name: documentInfo.name,
      description: documentInfo.description,
      url: documentInfo.url
    });

    const docData = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (docData) {
      for (let role of allRoles) {
        const previouslySelected = document.roles.find(r => r.roleDocumentId.roleId === role.id);
        const currentlySelected = documentInfo.roles.has(role.role);
        if (!previouslySelected && currentlySelected) {
          const roleRes = await addRoleAccessToDocument(role.id, docData.id, csrf);
          const roleData = roleRes && roleRes.payload && roleRes.payload.data && !roleRes.error ? roleRes.payload.data : null;
          if (!roleData) {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: "error",
                toast: `Failed to give access to role ${role.role}`
              }
            });
          }
        } else if (previouslySelected && !currentlySelected) {
          const roleRes = await removeRoleAccessToDocument(role.id, docData.id, csrf);
          const success = roleRes && roleRes.payload && roleRes.payload.status === 204 && !roleRes.error;
          if (!success) {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: "error",
                toast: `Failed to remove access from role ${role.role}`
              }
            });
          }
        }
      }
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Saved changes to document"
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to update document"
        }
      });
    }
    onChange();
  }

  const deleteThisDocument = async () => {
    setDeleteDialogOpen(false);
    const res = await deleteDocument(document.id, csrf);
    if (res?.payload?.status === 204 && !res.error) {
      onChange();
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Deleted document "${document.name}"`
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to delete document"
        }
      });
    }
  }

  return (
    <>
      <DocumentModal
        open={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
        onSave={updateThisDocument}
        document={document}
      />
      <Dialog open={deleteDialogOpen}>
        <DialogTitle>Delete Document</DialogTitle>
        <DialogContent>
          <DialogContentText style={{ marginBottom: "1rem" }}>
            Are you sure you would like to delete the document "{document.name}"?
          </DialogContentText>
          {document.roles.length > 0
            ? <Alert severity="warning">The following roles will lose access to this document: {getRolesText()}</Alert>
            : <Alert severity="info">There are no roles using this document</Alert>
          }
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} style={{ color: "gray" }}>Cancel</Button>
          <Button onClick={deleteThisDocument} color="error">Delete</Button>
        </DialogActions>
      </Dialog>
      <Card className="document-card">
        <CardHeader
          title={<Typography variant="h6" fontSize={18}>{document.name}</Typography>}
          action={
            <div className="document-card-actions">
              <Tooltip arrow title="Open in New Tab">
                <IconButton
                  onClick={(event) => {
                    event.stopPropagation();
                    window.open(document.url, "_blank");
                  }}
                >
                  <OpenLinkIcon/>
                </IconButton>
              </Tooltip>
              <Tooltip arrow title="Edit">
                <IconButton
                  onClick={(event) => {
                    event.stopPropagation();
                    setEditDialogOpen(true);
                  }}
                >
                  <EditIcon/>
                </IconButton>
              </Tooltip>
              <Tooltip arrow title="Delete">
                <IconButton
                  onClick={(event) => {
                    event.stopPropagation();
                    setDeleteDialogOpen(true);
                  }}
                >
                  <DeleteIcon/>
                </IconButton>
              </Tooltip>
            </div>
          }
          onClick={() => setCardExpanded(!cardExpanded)}
          style={{ cursor: "pointer" }}
        />
        <Collapse in={cardExpanded}>
          <CardContent style={{ paddingTop: 0 }}>
            <Typography color="gray" style={{ marginBottom: "0.5rem" }}>{document.description}</Typography>
            <Typography color="gray">Accessible to: {getRolesText()}</Typography>
          </CardContent>
        </Collapse>
      </Card>
    </>
  );
}

DocumentCard.propTypes = propTypes;

export default DocumentCard;