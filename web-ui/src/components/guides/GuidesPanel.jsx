import React, {useContext, useEffect, useState} from "react";
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import AddIcon from "@mui/icons-material/Add";
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';

import GuideLink from "./GuideLink";
import PropTypes from "prop-types";
import {getDocumentsByRole} from "../../api/document";
import {selectRoles} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {UPDATE_TOAST} from "../../context/actions";
import {
  Button,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField
} from "@mui/material";
import "./GuidesPanel.css";

const propTypes = {
  role: PropTypes.oneOf(["ADMIN", "PDL", "MEMBER"]).isRequired,
  title: PropTypes.string.isRequired
};

const GuidesPanel = ({ role, title }) => {

  const { state, dispatch } = useContext(AppContext);
  const [guides, setGuides] = useState([]);
  const [editGuideDialogOpen, setEditGuideDialogOpen] = useState(false);
  const allRoles = selectRoles(state);

  useEffect(() => {
    const loadDocuments = async () => {
      if (!role || !allRoles) {
        return;
      }
      const roleId = allRoles.find(r => r.role === role)?.id;
      if (!roleId) {
        return;
      }
      const res = await getDocumentsByRole(roleId);
      console.log(res);
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
    }

    loadDocuments().then(res => {
      if (res) {
        setGuides(res);
      }
    });

  }, [dispatch, role, allRoles]);

  return (
    <>
      <Dialog open={editGuideDialogOpen}>
        <DialogTitle>Add Document</DialogTitle>
        <DialogContent>
          <TextField label="Name"/>
          <TextField label="Description" multiline/>
          <TextField label="URL"/>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditGuideDialogOpen(false)}>
            Cancel
          </Button>
          <Button>
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <Card>
        <CardHeader
          avatar={<PdfIcon/>}
          title={title}
          action={
            <IconButton>
              <AddIcon/>
            </IconButton>
          }
        />
        <CardContent>
          <List dense>
            {guides.map((guide) => (
              <GuideLink key={guide.id} name={guide.name} url={guide.url}/>
            ))}
          </List>
        </CardContent>
      </Card>
    </>
  );
};

GuidesPanel.propTypes = propTypes;

export default GuidesPanel;
