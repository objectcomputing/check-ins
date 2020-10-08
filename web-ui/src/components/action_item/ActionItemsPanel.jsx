import React, { useState, useEffect, useContext } from "react";
import "./ActionItemsPanel.css";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import {
  findActionItem,
  deleteActionItem,
  updateActionItem,
  createActionItem,
} from "../../api/actionitem.js";
import DragIndicator from "@material-ui/icons/DragIndicator";
import IconButton from "@material-ui/core/IconButton";
import SaveIcon from "@material-ui/icons/Done";
import EditIcon from "@material-ui/icons/Edit";
import RemoveIcon from "@material-ui/icons/Remove";
import { AppContext } from "../../context/AppContext";


const ActionItemsPanel = ({ checkinId, mockActionItems }) => {
  let [actionItems, setActionItems] = useState([]);

  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { id } = userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};

  async function doDelete(id) {
    if (id) {
      await deleteActionItem(id);
    }
  }

  async function doUpdate(actionItem) {
    if (actionItem) {
      return await updateActionItem(actionItem);
    }
  }

  async function getActionItems(checkinId, mockActionItems) {
    if (mockActionItems) {
      setActionItems(mockActionItems);
      return;
    }

    let res = await findActionItem(checkinId, null);
    if (res && res.payload) {
      let actionItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
        console.log("new action item");
        console.log(actionItemList);
        setActionItems(actionItemList);
    }
  }

  useEffect(() => {
    getActionItems(checkinId, mockActionItems);
  }, [checkinId, mockActionItems, setActionItems]);

  const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);
    return result;
  };

  const grid = 8;

  const getListStyle = (isDraggingOver) => ({
    padding: grid,
  });

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    padding: grid * 2,
    margin: "0 0 {grid}px 0",
    textAlign: "left",
    marginBottom: "1px",
    marginTop: "1px",
    display: "flex",
    flexDirection: "row",

    background: isDragging ? "lightgreen" : "#fafafa",

    ...draggableStyle,
  });

  const onDragEnd = (result) => {
    if (!result || !result.destination) {
      return;
    }

    const destIndex = result.destination.index;
    const sourceIndex = result.source.index;

    if (destIndex !== sourceIndex) {
      let tempArr = reorder(actionItems, sourceIndex, destIndex);
      const lastIndex = actionItems.length - 1;
      console.log(actionItems);
      console.log(destIndex);
      const precedingPriority =
        destIndex === 0 ? 0 : actionItems[destIndex - 1].priority;
      const followingPriority = destIndex === lastIndex
        ? actionItems[lastIndex].priority + 1
        : actionItems[destIndex].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;
      tempArr[destIndex].priority = newPriority
      setActionItems(tempArr);

      doUpdate(actionItems[destIndex]);
    }
  };

  const [newActionItemDescription, setNewActionItemDescription] = useState("");

  const makeActionItem = async () => {
    if (!checkinId || !id || !newActionItemDescription === "") {
        return;
    }
    let newActionItem = {
      description: newActionItemDescription,
      createdbyid: id,
      checkinid: checkinId,
    };

    const res = await createActionItem(newActionItem);
    if (!res.error && res.payload && res.payload.data) {
      setNewActionItemDescription("");
      setActionItems([...actionItems, res.payload.data]);
    }
  };

  const editActionItem = (index, event) => {
    let enabled;
    if (!actionItems[index].enabled) {
      enabled = true;
    } else {
      doUpdate(actionItems[index]);
      enabled = false;
    }

    setActionItems((actionItems) => {
      actionItems[index].enabled = enabled;
      return [...actionItems];
    });
  };

  const handleNewDescriptionChange = (event) => {
    setNewActionItemDescription(event.target.value);
  };

  const handleDescriptionChange = (index, event) => {
    actionItems[index].description = event.target.value;
    setActionItems([...actionItems]);
  };

  const killActionItem = (id, event) => {
    doDelete(id);
    let newItems = actionItems.filter((actionItem) => {
        return actionItem.id !== id;
    });
    setActionItems(newItems);
  };

  const createActionItemEntries = () => {
    if (actionItems && actionItems.length > 0) {
      return actionItems.map((actionItem, index) => (
        <Draggable
          key={actionItem.id}
          draggableId={actionItem.id}
          index={index}
        >
          {(provided, snapshot) => (
            <div
              key={actionItem.id}
              ref={provided.innerRef}
              {...provided.draggableProps}
              style={getItemStyle(
                snapshot.isDragging,
                provided.draggableProps.style
              )}
            >
              <div className="description-field">
                <span style={{ cursor: "grab" }} {...provided.dragHandleProps}>
                  <DragIndicator />
                </span>
                <input
                  className="text-input"
                  disabled={!actionItem.enabled}
                  onChange={(e) => handleDescriptionChange(index, e)}
                  value={actionItem.description}
                />
                {actionItem.enabled}
              </div>
              <div className="button-div">
                <IconButton
                  aria-label="edit"
                  onClick={(e) => editActionItem(index, e)}
                >
                  <EditIcon />
                </IconButton>
                <IconButton
                  aria-label="delete"
                  onClick={(e) => killActionItem(actionItem.id, e)}
                >
                  <RemoveIcon />
                </IconButton>
              </div>
            </div>
          )}
        </Draggable>
      ));
    } 
  };

  return (
    <fieldset className="action-items-container">
      <legend>My Action Items</legend>
      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="droppable">
          {(provided, snapshot) => (
            <div
              {...provided.droppableProps}
              ref={provided.innerRef}
              style={getListStyle(snapshot.isDraggingOver)}
            >
              {createActionItemEntries()}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
      <div className="button-div">
        <input
          className="text-input"
          placeholder="Add action item"
          onChange={(e) => handleNewDescriptionChange(e)}
          value={newActionItemDescription}
        />
        <IconButton
          aria-label="create"
          style={{ paddingLeft: "5px" }}
          onClick={(e) => makeActionItem(newActionItemDescription, e)}
        >
          <SaveIcon />
        </IconButton>
      </div>
    </fieldset>
  );
};

export default ActionItemsPanel;
