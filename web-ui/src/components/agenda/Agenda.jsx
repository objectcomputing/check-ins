import React, { useContext, useState, useEffect } from "react";

import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import {
  getAgendaItem,
  deleteAgendaItem,
  updateAgendaItem,
  createAgendaItem,
} from "../../api/agenda.js";
import { AppContext, UPDATE_TOAST } from "../../context/AppContext";

import { debounce } from "lodash/function";
import DragIndicator from "@material-ui/icons/DragIndicator";
import AdjustIcon from "@material-ui/icons/Adjust";
import Skeleton from "@material-ui/lab/Skeleton";
import IconButton from "@material-ui/core/IconButton";
import SaveIcon from "@material-ui/icons/Done";
import RemoveIcon from "@material-ui/icons/Remove";

import "./Agenda.css";

const doUpdate = async (agendaItem) => {
  if (agendaItem) {
    await updateAgendaItem(agendaItem);
  }
};

const updateItem = debounce(doUpdate, 1500);

const AgendaItems = ({ checkinId, memberName }) => {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile } = state;
  const { memberProfile } = userProfile;
  const { id } = memberProfile;
  const pdlorAdmin =
    (memberProfile && userProfile.role && userProfile.role.includes("PDL")) ||
    userProfile.role.includes("ADMIN");

  const [agendaItems, setAgendaItems] = useState();
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const getAgendaItems = async (checkinId) => {
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

  const deleteItem = async (id) => {
    if (id) {
      await deleteAgendaItem(id);
    }
  };

  useEffect(() => {
    getAgendaItems(checkinId);
  }, [checkinId]);

  const reorder = (list, startIndex, endIndex) => {
    const [removed] = list.splice(startIndex, 1);
    list.splice(endIndex, 0, removed);
  };

  const getItemStyle = (isDragging, draggableStyle) => ({
    display: "flex",
    padding: "12px 8px",
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
      const lastIndex = agendaItems.length - 1;
      const precedingPriority = index === 0 ? 0 : agendaItems[index].priority;
      const followingPriority =
        index === lastIndex
          ? agendaItems[lastIndex].priority + 1
          : agendaItems[index].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;
      console.log({ newPriority, followingPriority, precedingPriority });

      setAgendaItems((agendaItems) => {
        agendaItems[sourceIndex].priority = newPriority;
        reorder(agendaItems, sourceIndex, index);
        return agendaItems;
      });

      doUpdate(agendaItems[result.destination.index]);
    }
  };

  const makeAgendaItem = async () => {
    if (!checkinId || !id || description === "") {
      return;
    }
    let newAgendaItem = {
      checkinid: checkinId,
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

  const handleDescriptionChange = (index, e) => {
    if (agendaItems[index].createdbyid !== id) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Agenda Items can only be edited by creator",
        },
      });
      return;
    }
    const { value } = e.target;
    agendaItems[index].description = value;
    setAgendaItems(() => {
      updateItem(agendaItems[index]);
      return [...agendaItems];
    });
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

  const createAgendaItemEntries = () => {
    if (agendaItems && agendaItems.length > 0) {
      return agendaItems.map((agendaItem, index) => (
        <Draggable
          disabled={!pdlorAdmin}
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
                    onChange={(e) => handleDescriptionChange(index, e)}
                    value={agendaItem.description}
                  />
                )}
                <div className="agenda-item-button-div">
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
                <div className="add-agenda-item-div">
                  <input
                    className="text-input"
                    placeholder="Add an agenda item"
                    onChange={(e) => setDescription(e.target.value)}
                    onKeyPress={(e) => {
                      if (e.key === "Enter" && description !== "") {
                        makeAgendaItem();
                      }
                    }}
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
