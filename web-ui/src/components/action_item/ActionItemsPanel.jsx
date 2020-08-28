import React, { useState } from "react";
//import { AppContext } from "../../context/AppContext";
import "./ActionItemsPanel.css";
import {  findActionItem,
          //getActionItem,
          //deleteActionItem,
          //updateActionItem,
          //createMassActionItem,
          /*createActionItem*/ } from "../../api/actionitem.js";
import { useDrag } from 'react-dnd';
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
  let thisCheckinId = params.checkinId;
  var Reorder = require('react-reorder');

  let [actionItems, setActionItems] = useState();
  let infoClassName = "action-items-info-hidden";
//createSingleEntry(this.props.item);
  var actionItemListEntry = React.createFactory(
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
  );

  React.useEffect(() => {
    async function getActionItems() {
      if (thisCheckinId) {
        let res = await findActionItem(thisCheckinId, null);
        let actionItemList =
          res.payload.data && !res.error ? res.payload.data : undefined;
          setActionItems(actionItemList);      }
    }
    getActionItems();
  }, [thisCheckinId]);

  const createSingleEntry = (actionItem) => {
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

  return (
    <fieldset className="action-items-container">
      <legend>My Action Items</legend>
      <Reorder
        itemKey='id'
        list={actionItems}
        lock='horizontal'
        template={actionItemListEntry}/>
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