import React, {useCallback, useContext, useEffect, useState} from "react";
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';

import GuideLink from "./GuideLink";
import PropTypes from "prop-types";
import {getDocumentsByRole, updateRoleDocumentOrder} from "../../api/document";
import {selectIsAdmin, selectRoles} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {UPDATE_TOAST} from "../../context/actions";
import {
  CardContent, Collapse, Typography
} from "@mui/material";
import "./GuidesPanel.css";
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import {DragIndicator} from "@mui/icons-material";

const propTypes = {
  role: PropTypes.oneOf(["ADMIN", "PDL", "MEMBER"]).isRequired,
  title: PropTypes.string.isRequired
};

const GuidesPanel = ({ role, title }) => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [guides, setGuides] = useState([]);
  const [expanded, setExpanded] = useState(true);
  const allRoles = selectRoles(state);
  const isAdmin = selectIsAdmin(state);

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

  const handleDragEnd = async (result) => {
    if (!result || !result.destination || result.source.index === result.destination.index) {
      return;
    }
    const originalGuides = [...guides];
    const items = [...guides]
    const [reorderedItem] = items.splice(result.source.index, 1);
    items.splice(result.destination.index, 0, reorderedItem);
    setGuides(items);  // Immediately update list to reflect moved item

    const roleId = allRoles.find(r => r.role === role)?.id;
    const res = await updateRoleDocumentOrder(roleId, reorderedItem.id, result.destination.index + 1, csrf);
    const reorderedGuides = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;

    if (reorderedGuides) {
      setGuides(reorderedGuides);  // Adjust state with updated document numbers
    } else {
      setGuides(originalGuides);  // If there is a problem saving, revert local state
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to reorder guides"
        }
      });
    }
  }

  return (
    <Card>
      <CardHeader
        avatar={<PdfIcon/>}
        title={title}
        onClick={() => setExpanded(!expanded)}
        style={{ cursor: "pointer" }}
      />
      <Collapse in={expanded} timeout="auto">
        <CardContent style={{ padding: 0 }}>
          {guides.length > 0
            ? (
              <DragDropContext onDragEnd={handleDragEnd}>
                <Droppable droppableId={`${role}-droppable`}>
                  {(provided) => (
                    <List dense {...provided.droppableProps} ref={provided.innerRef} style={{ paddingTop: 0 }}>
                      {guides.map((guide, index) => (
                        <Draggable key={guide.id} draggableId={guide.id} index={index}>
                          {(provided, snapshot) => (
                            <div className="draggable-guide-container" ref={provided.innerRef} {...provided.draggableProps}>
                              {isAdmin &&
                                <span
                                  {...provided.dragHandleProps}
                                  style={{ cursor: snapshot.isDragging ? "grabbing" : "grab", marginLeft: "12px"}}>
                                  <DragIndicator style={{ color: "gray" }}/>
                                </span>
                              }
                              <GuideLink document={guide} draggable={isAdmin}/>
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </List>
                  )}
                </Droppable>
              </DragDropContext>
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
  );
};

GuidesPanel.propTypes = propTypes;

export default GuidesPanel;
