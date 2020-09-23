import React, { useContext, useEffect, useState } from "react";

import {
  getAgendaItemByCheckinId,
  deleteAgendaItem,
  updateAgendaItem,
} from "../../api/agenda.js";
import { AppContext } from "../../context/AppContext";

import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import DragIndicator from "@material-ui/icons/DragIndicator";
import AdjustIcon from "@material-ui/icons/Adjust";

import "./Agenda.css";

const Agenda = ({ checkinId, mockAgendaItems }) => {
  const { state } = useContext(AppContext);
  const { currentCheckin } = state;
  const [prevAgendaItems, setPrevAgendaItems] = useState();
  const [agendaItems, setAgendaItems] = useState();

  //   console.log({ agendaItems, currentCheckin, prevAgendaItems });

  const deleteItem = async (id) => {
    if (id) {
      await deleteAgendaItem(id);
    }
  };

  const updateItem = async (agendaItem) => {
    if (agendaItem) {
      await updateAgendaItem(agendaItem);
    }
  };

  const agendaItemsCompare = (currItems) => {
    if (!prevAgendaItems) {
      setPrevAgendaItems(currItems);
      return true;
    }
    if (prevAgendaItems.length !== currItems.length) {
      setPrevAgendaItems(currItems);
      return true;
    }
    for (var i = 0; i < prevAgendaItems.length; i++) {
      if (prevAgendaItems[i].id !== currItems[i].id) {
        setPrevAgendaItems(currItems);
        return true;
      }
    }
    return false;
  };

  async function getAgendaItems() {
    if (mockAgendaItems) {
      setAgendaItems(mockAgendaItems);
      return;
    }

    let res = await getAgendaItemByCheckinId(currentCheckin.id);
    if (res && res.payload) {
      let agendaItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
      setAgendaItems(agendaItemList);
    }
  }

  useEffect(() => {
    if (agendaItemsCompare(agendaItems)) {
      getAgendaItems();
    }
  });

  const getAgendaItemStyle = (agendaItem) => {
    if (agendaItem && agendaItem.description) {
      return "agenda-items-info";
    }
    return "agenda-items-info-hidden";
  };

  const getAgendaItemText = (agendaItem) => {
    if (agendaItem && agendaItem.description) {
      return agendaItem.description;
    }
    return "Lorem Ipsum Etcetera";
  };

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

    setAgendaItems(
      reorder(agendaItems, result.source.index, result.destination.index)
    );

    let precedingPriority = 0;
    if (result.destination.index > 0) {
      precedingPriority = agendaItems[result.destination.index - 1].priority;
    }

    let followingPriority = agendaItems[agendaItems.length - 1].priority + 1;
    if (result.destination.index < agendaItems.length - 1) {
      followingPriority = agendaItems[result.destination.index + 1].priority;
    }

    let newPriority = (precedingPriority + followingPriority) / 2;

    agendaItems[result.destination.index].priority = newPriority;

    updateItem(agendaItems[result.destination.index]);
    getAgendaItems();
    // update entire agenda items array
  };

  const killAgendaItem = (id, event) => {
    deleteItem(id);
    var arrayDupe = agendaItems;
    for (var i = 0; i < arrayDupe.length; i++) {
      if (arrayDupe[i].id === id) {
        arrayDupe.splice(i, 1);
        break;
      }
    }
    setAgendaItems(arrayDupe);
    getAgendaItems();
  };

  const createFakeEntry = (item) => {
    return (
      <div key={item.id} className="image-div">
        <span>
          <DragIndicator />
        </span>
        <div className="description-field">
          <p className="agenda-items-info-hidden">Lorem Ipsum etc</p>
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
                <p className={getAgendaItemStyle(agendaItem)}>
                  {getAgendaItemText(agendaItem)}
                </p>
              </div>
              <div>
                <button
                  className="delete-button"
                  onClick={(e) => killAgendaItem(agendaItem.id, e)}
                >
                  -
                </button>
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
    <div className="agenda-items-container">
      <h1>
        <AdjustIcon style={{ marginRight: "10px" }} />
        Agenda Items
      </h1>
      <div className="agenda-container">
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="droppable">
            {(provided, snapshot) => (
              <div
                {...provided.droppableProps}
                ref={provided.innerRef}
                style={getListStyle(snapshot.isDraggingOver)}
              >
                {createAgendaItemEntries()}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </div>
  );
};

export default Agenda;
