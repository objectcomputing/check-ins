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
import Skeleton from "@material-ui/lab/Skeleton";
import IconButton from "@material-ui/core/IconButton";
import SaveIcon from "@material-ui/icons/Done";
import EditIcon from "@material-ui/icons/Edit";
import RemoveIcon from "@material-ui/icons/Remove";
import ArrowForwardIcon from "@material-ui/icons/ArrowForward";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

const ActionItemsPanel = ({ checkinId, memberName }) => {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile } = state;
  const { id } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};

  const [actionItems, setActionItems] = useState([]);
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const getActionItems = async (checkinId) => {
    setIsLoading(true);
    let res = await findActionItem(checkinId, null);
    if (res && res.payload) {
      let actionItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
      setActionItems(actionItemList);
      setIsLoading(false);
    }
  };

  const deleteItem = async (id) => {
    if (id) {
      await deleteActionItem(id);
    }
  };

  const doUpdate = async (actionItem) => {
    if (actionItem) {
      await updateActionItem(actionItem);
    }
  };

  useEffect(() => {
    getActionItems(checkinId);
  }, [checkinId]);

  const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);
    return result;
  };

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    padding: "12px 8px",
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
    console.log({ result });

    const { index } = result.destination;
    const sourceIndex = result.source.index;
    if (index !== sourceIndex) {
      const lastIndex = actionItems.length - 1;
      const precedingPriority =
        index === 0 ? 0 : actionItems[index - 1].priority;
      const followingPriority =
        index === lastIndex
          ? actionItems[lastIndex].priority + 1
          : actionItems[index].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;
      console.log({ newPriority, followingPriority, precedingPriority });

      setActionItems((actionItems) => {
        actionItems[sourceIndex].priority = newPriority;
        reorder(actionItems, sourceIndex, index);
        return actionItems;
      });

      doUpdate(actionItems[result.destination.index]);
    }
  };

  const makeActionItem = async () => {
    if (!checkinId || !id || description === "") {
      return;
    }
    let newActionItem = {
      checkinid: checkinId,
      createdbyid: id,
      description: description,
    };
    const res = await createActionItem(newActionItem);
    if (!res.error && res.payload && res.payload.data) {
      setDescription("");
      setActionItems([...actionItems, newActionItem]);
    }
  };

  const handleDescriptionChange = (index, event) => {
    actionItems[index].description = event.target.value;
    setActionItems([...actionItems]);
  };

  const editActionItem = (index, event) => {
    let enabled;
    // if (actionItems[index].createdbyid !== id) {
    //   dispatch({
    //     type: UPDATE_TOAST,
    //     payload: {
    //       severity: "error",
    //       toast: "Action Items can only be edited by creator",
    //     },
    //   });
    //   return;
    // }
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

  const killActionItem = (id) => {
    deleteItem(id);
    let newItems = actionItems.filter((actionItem) => {
      return actionItem.id !== id;
    });
    setActionItems(newItems);
  };

  const createFakeEntry = (item) => {
    return (
      <div key={item.id} className="skeleton-div">
        <div className="drag-icon">
          <DragIndicator />
        </div>
        <div className="skeleton">
          <Skeleton variant="text" height={"2rem"} />
        </div>
      </div>
    );
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
                {isLoading ? (
                  <div className="skeleton">
                    <Skeleton className="test" variant="text" height={"2rem"} />
                    <Skeleton variant="text" height={"2rem"} />
                    <Skeleton variant="text" height={"2rem"} />
                  </div>
                ) : (
                  <input
                    className="text-input"
                    disabled={!actionItem.enabled}
                    onChange={(e) => handleDescriptionChange(index, e)}
                    value={actionItem.description}
                  />
                )}
                <div className="action-item-button-div">
                  <IconButton
                    aria-label="edit"
                    className="edit-icon"
                    onClick={(e) => editActionItem(index, e)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    aria-label="delete"
                    className="delete-icon"
                    onClick={(e) => killActionItem(actionItem.id, e)}
                  >
                    <RemoveIcon />
                  </IconButton>
                </div>
              </div>
            </div>
          )}
        </Draggable>
      ));
    } else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createFakeEntry({ id: `${i + 1}Action` });
      }
      return fake;
    }
  };

  return (
    <div className="action-items">
      <h1>
        <ArrowForwardIcon style={{ fontSize: "larger", marginRight: "10px" }} />
        My Action Items for {memberName}
      </h1>
      <div className="action-items-container">
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="droppable">
            {(provided, snapshot) => (
              <div {...provided.droppableProps} ref={provided.innerRef}>
                {createActionItemEntries()}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
        <div className="add-action-item-div">
          <input
            className="text-input"
            placeholder="Add action item"
            onChange={(e) => setDescription(e.target.value)}
            value={description ? description : ""}
          />
          <IconButton
            aria-label="create"
            style={{ paddingLeft: "5px" }}
            onClick={() => makeActionItem()}
          >
            <SaveIcon />
          </IconButton>
        </div>
      </div>
    </div>
  );
};

export default ActionItemsPanel;
