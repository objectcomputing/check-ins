import React, { useContext, useState, useEffect } from "react";
import "./Agenda.css";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import {
  getAgendaItem,
  deleteAgendaItem,
  updateAgendaItem,
  createAgendaItem,
} from "../../api/agenda.js";

import DragIndicator from "@material-ui/icons/DragIndicator";
import AdjustIcon from "@material-ui/icons/Adjust";
import Skeleton from "@material-ui/lab/Skeleton";
import IconButton from "@material-ui/core/IconButton";
import SaveIcon from "@material-ui/icons/Done";
import EditIcon from "@material-ui/icons/Edit";
import RemoveIcon from "@material-ui/icons/Remove";
import { AppContext } from "../../context/AppContext";

const AgendaItems = ({ checkinId, mockAgendaItems, memberName }) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { id } = userProfile && userProfile.memberProfile;
  const [agendaItems, setAgendaItems] = useState();
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const getAgendaItems = async (checkinId, mockAgendaItems, setAgendaItems) => {
    if (mockAgendaItems) {
      setAgendaItems(mockAgendaItems);
      return;
    }

    setIsLoading(true);
    let res = await getAgendaItem(checkinId, null);
    if (res && res.payload) {
      let agendaItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
      agendaItemList.sort((a, b) => {
        return a.priority - b.priority;
      });
      setAgendaItems(agendaItemList);
      setIsLoading(false);
    }
  };

  async function deleteItem(id) {
    if (id) {
      await deleteAgendaItem(id);
    }
  }

  async function doUpdate(agendaItem) {
    if (agendaItem) {
      await updateAgendaItem(agendaItem);
    }
  }

  useEffect(() => {
    getAgendaItems(checkinId, mockAgendaItems, setAgendaItems);
  }, [checkinId, mockAgendaItems, setAgendaItems]);

  const reorder = (list, startIndex, endIndex) => {
    const [removed] = list.splice(startIndex, 1);
    list.splice(endIndex, 0, removed);
  };

  const getItemStyle = (isDragging, draggableStyle) => ({
    borderBottom: "2px solid black",
    display: "flex",
    padding: "12px 8px",
    background: isDragging ? "lightgreen" : "#fafafa",
    ...draggableStyle,
  });

  const onDragEnd = (result) => {
    if (!result || !result.destination) {
      return;
    }

    const { index } = result.destination;
    const sourceIndex = result.source.index;
    if (index !== sourceIndex) {
      const lastIndex = agendaItems.length - 1;
      const precedingPriority =
        index === 0 ? 0 : agendaItems[index - 1].priority;
      const followingPriority =
        index === lastIndex
          ? agendaItems[lastIndex].priority + 1
          : agendaItems[index].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;

      setAgendaItems((agendaItems) => {
        agendaItems[sourceIndex].priority = newPriority;
        reorder(agendaItems, sourceIndex, index);
        return agendaItems;
      });

      doUpdate(agendaItems[result.destination.index]);
    }
  };

  const makeAgendaItem = async () => {
    if (!checkinId || !id || !description === "") {
      return;
    }
    let newAgendaItem = {
      checkinId: checkinId,
      createdbyid: id,
      description: description,
    };
    const res = await createAgendaItem(newAgendaItem);
    if (!res.error && res.payload && res.payload.data) {
      setDescription("");
      setAgendaItems([...agendaItems, newAgendaItem]);
    }
  };

  const killAgendaItem = (id) => {
    deleteItem(id);
    let newItems = agendaItems.filter((agendaItem) => {
      return agendaItem.id !== id;
    });
    setAgendaItems(newItems);
  };

  const handleDescriptionChange = (index, event) => {
    agendaItems[index].description = event.target.value;
    setAgendaItems([...agendaItems]);
  };

  const editAgendaItem = (index, event) => {
    let enabled;
    if (!agendaItems[index].enabled) {
      enabled = true;
    } else {
      doUpdate(agendaItems[index]);
      enabled = false;
    }

    setAgendaItems((agendaItems) => {
      agendaItems[index].enabled = enabled;
      return [...agendaItems];
    });
  };

  const createFakeEntry = (item) => {
    return (
      <div key={item.id} className="image-div">
        <div className="drag-icon">
          <DragIndicator />
        </div>
        <div className="skeleton">
          <Skeleton variant="text" height={"2rem"} />
          <Skeleton variant="text" height={"2rem"} />
          <Skeleton variant="text" height={"2rem"} />
        </div>
      </div>
    );
  };

  const createAgendaItemEntries = () => {
    if (agendaItems && agendaItems.length > 0) {
      return agendaItems.map((agendaItem, index) => (
        <Draggable
          key={agendaItem.id}
          draggableId={agendaItem.id}
          index={index}
        >
          {(provided, snapshot) => (
            <div
              key={agendaItem.id}
              ref={provided.innerRef}
              {...provided.draggableProps}
              style={getItemStyle(
                snapshot.isDragging,
                provided.draggableProps.style
              )}
            >
              <div className="description-field">
                <span {...provided.dragHandleProps}>
                  <DragIndicator />
                </span>
                {isLoading ? (
                  <div className="skeleton">
                    <Skeleton variant="text" height={"2rem"} />
                    <Skeleton variant="text" height={"2rem"} />
                    <Skeleton variant="text" height={"2rem"} />
                  </div>
                ) : (
                  <input
                    className="text-input"
                    disabled={!agendaItem.enabled}
                    onChange={(e) => handleDescriptionChange(index, e)}
                    value={agendaItem.description}
                  />
                )}
                <div className="button-div">
                  <IconButton
                    aria-label="edit"
                    className="edit-icon"
                    onClick={(e) => editAgendaItem(index, e)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    aria-label="delete"
                    className="delete-icon"
                    onClick={(e) => killAgendaItem(agendaItem.id, e)}
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
        fake[i] = createFakeEntry({ id: `${i + 1}Agenda` });
      }
      return fake;
    }
  };

  return (
    <div className="agenda-items">
      <h1>
        <AdjustIcon style={{ fontSize: "larger", marginRight: "10px" }} />
        Agenda Items for {memberName}
      </h1>
      <div className="agenda-items-container">
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="droppable">
            {(provided, snapshot) => (
              <div {...provided.droppableProps} ref={provided.innerRef}>
                {createAgendaItemEntries()}
                <div
                  style={{
                    borderBottom: "2px solid black",
                    display: "flex",
                    padding: "12px 8px",
                  }}
                >
                  <input
                    className="text-input"
                    placeholder="Add agenda item"
                    onChange={(e) => setDescription(e.target.value)}
                    value={description ? description : ""}
                  />
                  <IconButton
                    aria-label="create"
                    className="edit-icon"
                    onClick={() => makeAgendaItem()}
                  >
                    <SaveIcon />
                  </IconButton>
                </div>
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </div>
  );
};

export default AgendaItems;
