import React, { useState } from "react";
//import { AppContext } from "../../context/AppContext";
import "./ActionItemsPanel.css";
import {DragDropContext, Droppable, Draggable} from "react-beautiful-dnd"
import {  //findActionItem,
          //getActionItem,
          //deleteActionItem,
          //updateActionItem,
          //createMassActionItem,
          /*createActionItem*/ } from "../../api/actionitem.js";
//import { useDrag } from 'react-dnd';
/*
export interface ActionItemProps {
    id: string
    description: string
}

interface DragItem {
    index: number
    id: string
    type: string
}
*/
const ActionItemsPanel = (params) => {
  //let thisCheckinId = params.checkinId;

  let [actionItems/*, setActionItems*/] = useState();
  //let infoClassName = "action-items-info-hidden";
//createSingleEntry(this.props.item);
  /*var actionItemListEntry = React.createFactory(
    React.createClass({
      render: function () {
        return
        React.createElement('div', {
          className: 'inner',
          style: {
            color: this.props.item.color
          }
        }, this.props.sharedProps ? this.props.sharedProps.prefix : undefined, this.props.item.name);
      }
    })
  );*/

  actionItems = [
    {
        "id":"1",
        "description":"What's up?"
    },
    {
        "id":"2",
        "description":"asdf"
    },
    {
        "id":"3",
        "description":"Oh no"
    },
    {
        "id":"4",
        "description":"Bacon"
    },
  ]

  /*React.useEffect(() => {
    async function getActionItems() {
      if (thisCheckinId) {
        let res = await findActionItem(thisCheckinId, null);
        let actionItemList =
          res.payload.data && !res.error ? res.payload.data : undefined;
          setActionItems(actionItemList);      }
    }
    getActionItems();
  }, [thisCheckinId]);*/

  /*const createSingleEntry = (actionItem) => {
    var useInfoClass = infoClassName;
    var actionItemText = "Lorem Ipsum Etcetera";
    if (actionItem && actionItem.description) {
      useInfoClass = "action-items-info";
      actionItemText = actionItem.description;
    }
    return (
      <div key={actionItem.id} className="image-div">
        <div className="info-div">
          <p className={useInfoClass}>{actionItemText}</p>
        </div>
        <button align="right">-</button>
      </div>
    );
  };*/

  const getActionItemStyle = (actionItem) => {
    if (actionItem && actionItem.description) {
        return "action-items-info";
    }
    return "action-items-info-hidden";
  }

  const getActionItemText = (actionItem) => {
    if (actionItem && actionItem.description) {
        return actionItem.description;
    }
    return "Lorem Ipsum Etcetera";

  }

  const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const[removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);
    return result;
  };

  const grid = 8;

  const getListStyle = (isDraggingOver) => ({
    //background: (isDraggingOver ? "lightblue" : "lightgrey"),
    padding: grid,
    //width: 250,,
  });

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    padding: grid*2,
    margin: '0 0 {grid}px 0',

    background: isDragging ? "lightgreen" : "grey",

    ...draggableStyle
  });

  const getContainerStyle = (listLength) => ({
    height : 'fit-content',
    border: 'solid 3px black',
  });

  /*const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    padding: grid*2,
    margin: '0 0 ${grid}px 0',

    background: isDragging ? "lightgreen" : "grey",

    ...draggableStyle
  });*/

  const onDragEnd = (result) => {
    console.log("DONE DRAGGING!");
    console.log(result);
        if (!result || !result.destination) {
            return;
        }

        actionItems = reorder(
            actionItems,
            result.source.index,
            result.destination.index
        );

        /*this.setState({
            items
        });*/
    };

  /*const createActionItemEntries = () => {
    if (actionItems && actionItems.length > 0) {
      return actionItems.map((actionItem) => createSingleEntry(actionItem));
    } else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createSingleEntry({id: `${i+1}Action`});
      }
      return fake;
    }
  };*/

/*
                                <div
                                    ref={provided.innerRef}
                                    {...provided.draggableProps}
                                    {...provided.dragHandleProps}
                                    style={getItemStyle(
                                        snapshot.isDragging,
                                        provided.draggableProps.style
                                    )}
                                >
                                    {item.content}
                                </div>

                                style={getListStyle(snapshot.isDraggingOver)}*/

  return (
    <fieldset style={getContainerStyle(actionItems.length)}>
      <legend>My Action Items</legend>
        <DragDropContext onDragEnd={onDragEnd}>
            <Droppable droppableId="droppable">
                {(provided, snapshot) =>(
                    <div
                        {...provided.droppableProps}
                        ref={provided.innerRef}
                        style={getListStyle(snapshot.isDraggingOver)}
                    >
                        {actionItems.map((actionItem, index) => (
                            <Draggable key={actionItem.id} draggableId={actionItem.id} index={index}>
                                {(provided, snapshot) => (
                                  <div key={actionItem.id}
                                    ref={provided.innerRef}
                                    {...provided.draggableProps}
                                    {...provided.dragHandleProps}
                                    style={getItemStyle(
                                        snapshot.isDragging,
                                        provided.draggableProps.style
                                    )}
                                  >
                                    <div >
                                      <p className={getActionItemStyle(actionItem)}>{getActionItemText(actionItem)}</p>
                                      <button align="right">-</button>
                                    </div>
                                  </div>
                                )}
                            </Draggable>
                        ))}
                        {provided.placeholder}
                    </div>
                )}
            </Droppable>
        </DragDropContext>
    </fieldset>
  )

  /*return(
    <fieldset className="action-items-container">
      <legend>My Action Items</legend>
      {createActionItemEntries()}
    </fieldset>
  )*/
};

export default ActionItemsPanel;