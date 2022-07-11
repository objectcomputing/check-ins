import React, {useCallback, useContext, useEffect, useState} from "react";
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import AddIcon from "@mui/icons-material/Add";
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';

import GuideLink from "./GuideLink";
import PropTypes from "prop-types";
import {createDocument, getDocumentsByRole} from "../../api/document";
import {selectRoles} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {UPDATE_TOAST} from "../../context/actions";
import {
  CardContent, Collapse,
  IconButton, Typography
} from "@mui/material";
import "./GuidesPanel.css";
import DocumentModal from "../document_modal/DocumentModal";

const propTypes = {
  role: PropTypes.oneOf(["ADMIN", "PDL", "MEMBER"]).isRequired,
  title: PropTypes.string.isRequired
};

const GuidesPanel = ({ role, title }) => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [guides, setGuides] = useState([]);
  const [guideDialogOpen, setGuideDialogOpen] = useState(false);
  const [expanded, setExpanded] = useState(true);
  const allRoles = selectRoles(state);

  const loadDocuments = useCallback(async () => {
    if (!role || !allRoles || !csrf) {
      return;
    }
    const roleId = allRoles.find(r => r.role === role)?.id;
    if (!roleId) {
      return;
    }
    const res = await getDocumentsByRole(roleId, csrf);
    const data = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      return data;
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: `Could not retrieve documents for role ${role}`
        }
      });
    }
  }, [allRoles, dispatch, role, csrf]);

  useEffect(() => {
    loadDocuments().then(res => {
      if (res) {
        setGuides(res);
      }
    });
  }, [allRoles, loadDocuments]);

  const saveDocument = async (document) => {
    console.log(document);
    setGuideDialogOpen(false);

    const docRes = await createDocument(document, csrf);
    const data = docRes && docRes.payload && docRes.payload.data && !docRes.error ? docRes.payload.data : null;

    let toastMessage;
    if (data) {
      toastMessage = "Saved new document";
    } else {
      toastMessage = "Failed to create document";
    }

    dispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: data ? "success" : "error",
        toast: toastMessage
      }
    });

    if (data) {
      loadDocuments().then(documents => {
        if (documents) {
          setGuides(documents);
        }
      });
    }
  }

  return (
    <>
      <DocumentModal
        open={guideDialogOpen}
        onClose={() => setGuideDialogOpen(false)}
        onSave={saveDocument}
        initialRole={role}
      />
      <Card>
        <CardHeader
          avatar={<PdfIcon/>}
          title={title}
          onClick={() => setExpanded(!expanded)}
          style={{ cursor: "pointer" }}
          action={
            <IconButton onClick={(event) => {
              event.stopPropagation();
              setGuideDialogOpen(true);
            }}>
              <AddIcon/>
            </IconButton>
          }
        />
        <Collapse in={expanded} timeout="auto">
          <CardContent style={{ padding: 0 }}>
            {guides.length > 0
              ? (
                <List dense style={{ paddingTop: 0 }}>
                  {guides.map((guide) => (
                    <GuideLink key={guide.id} document={guide}/>
                  ))}
                </List>
              )
              : (
                <div className="no-documents-message">
                  <Typography fontSize={14}>This section has no documents</Typography>
                </div>
              )
            }
          </CardContent>
        </Collapse>
      </Card>
    </>
  );
};

GuidesPanel.propTypes = propTypes;

export default GuidesPanel;
