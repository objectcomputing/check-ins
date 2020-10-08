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
      await updateActionItem(actionItem);
    }
  }

  async function doCreate(actionItem) {
    if (actionItem) {
      return await createActionItem(actionItem);
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
  }, [actionItems, checkinId, mockActionItems]);

  /*const getActionItemStyle = (actionItem) => {
    if (actionItem && actionItem.description) {
      return "action-items-info";
    }
    return "action-items-info-hidden";
  };

  const getActionItemText = (actionItem) => {
    if (actionItem && actionItem.description) {
      return actionItem.description;
    }
    return "Lorem Ipsum Etcetera";
  };*/

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

    let thisList = reorder(
      actionItems,
      result.source.index,
      result.destination.index
    );

    let precedingPriority = 0;
    if (result.destination.index > 0) {
      precedingPriority = thisList[result.destination.index - 1].priority;
    }

    let followingPriority = thisList[thisList.length - 1].priority + 1;
    if (result.destination.index < thisList.length - 1) {
      followingPriority = thisList[result.destination.index + 1].priority;
    }

    let newPriority = (precedingPriority + followingPriority) / 2;

    thisList[result.destination.index].priority = newPriority;

    doUpdate(thisList[result.destination.index]);

    getActionItems(checkinId, null);

  };

  const [newActionItemDescription, setNewActionItemDescription] = useState("");

  const makeActionItem = (newDesc, event) => {
    let newActionItem = {};
    newActionItem.description = newDesc;
    newActionItem.createdbyid = id;
    newActionItem.checkinid = checkinId;

    doCreate(newActionItem);

    setNewActionItemDescription("");
  };

  const editActionItem = (index, event) => {
    if (!actionItems[index].enabled) {
      console.log("enabling");
      actionItems[index].enabled = true;
    } else {
      console.log("disabling");
      doUpdate(actionItems[index]);
      getActionItems(checkinId, null);
      actionItems[index].enabled = false;
    }

  };

  const handleNewDescriptionChange = (event) => {
    setNewActionItemDescription(event.target.value);
  };

  const handleDescriptionChange = (index, event) => {
    actionItems[index].description = event.target.value;
  };

  const killActionItem = (id, event) => {
    doDelete(id);
    var arrayDupe = actionItems;
    for (var i = 0; i < arrayDupe.length; i++) {
      if (arrayDupe[i].id === id) {
        arrayDupe.splice(i, 1);
        break;
      }
    }
    getActionItems(checkinId, null);
  };

  /*const createFakeEntry = (item) => {
    return (
      <div key={item.id} className="image-div">
        <span>
          <DragIndicator />
        </span>
        <p className="action-items-info-hidden">Lorem Ipsum etc1</p>
      </div>
    );
  };*/

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
    } /*else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createFakeEntry({ id: `${i + 1}Action` });
      }
      return fake;
    }*/
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
