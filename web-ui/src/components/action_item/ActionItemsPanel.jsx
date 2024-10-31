import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import {
  findActionItem,
  deleteActionItem,
  updateActionItem,
  createActionItem
} from '../../api/actionitem.js';
import { AppContext } from '../../context/AppContext';
import { UPDATE_TOAST } from '../../context/actions';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectCheckin
} from '../../context/selectors';
import { debounce } from 'lodash/function';
import DragIndicator from '@mui/icons-material/DragIndicator';
import Skeleton from '@mui/material/Skeleton';
import IconButton from '@mui/material/IconButton';
import SaveIcon from '@mui/icons-material/Done';
import RemoveIcon from '@mui/icons-material/Remove';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardContent from '@mui/material/CardContent';

import './ActionItemsPanel.css';

const doUpdate = async (actionItem, csrf) => {
  if (actionItem && csrf) {
    await updateActionItem(actionItem, csrf);
  }
};

const updateItem = debounce(doUpdate, 1500);

const ActionItemsPanel = () => {
  const { state, dispatch } = useContext(AppContext);
  const { checkinId } = useParams();
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const currentCheckin = selectCheckin(state, checkinId);

  const [actionItems, setActionItems] = useState([]);
  const [description, setDescription] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const getActionItems = async (checkinId, csrf) => {
    setIsLoading(true);
    let res = await findActionItem(checkinId, null, csrf);
    let actionItemList;
    if (res && res.payload) {
      actionItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
      if (actionItemList) {
        setActionItems(actionItemList);
      }
    }
    setIsLoading(false);
  };

  const deleteItem = async (id, csrf) => {
    if (id && csrf) {
      await deleteActionItem(id, csrf);
    }
  };

  const doUpdate = async (actionItem, csrf) => {
    if (actionItem && csrf) {
      await updateActionItem(actionItem, csrf);
    }
  };

  useEffect(() => {
    if (csrf) {
      getActionItems(checkinId, csrf);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [checkinId, csrf]);

  const reorder = (list, startIndex, endIndex) => {
    const [removed] = list.splice(startIndex, 1);
    list.splice(endIndex, 0, removed);
  };

  const getItemStyle = (isDragging, draggableStyle) => ({
    display: 'flex',
    background: isDragging ? 'lightgreen' : undefined,
    ...draggableStyle
  });

  const onDragEnd = result => {
    if (!result || !result.destination) {
      return;
    }

    const { index } = result.destination;
    const sourceIndex = result.source.index;
    if (index !== sourceIndex) {
      const lastIndex = actionItems.length - 1;
      const precedingPriority =
        index === 0 ? 0 : actionItems[index - 1].priority;
      const followingPriority =
        index === lastIndex
          ? actionItems[lastIndex].priority
          : actionItems[index].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;
      if (actionItems[sourceIndex].priority <= followingPriority) {
        newPriority += 1;
      }

      setActionItems(actionItems => {
        actionItems[sourceIndex].priority = newPriority;
        reorder(actionItems, sourceIndex, index);
        return actionItems;
      });

      doUpdate(actionItems[result.destination.index], csrf);
    }
  };

  const makeActionItem = async () => {
    if (!checkinId || !currentUserId || description === '' || !csrf) {
      return;
    }
    let newActionItem = {
      checkinid: checkinId,
      createdbyid: currentUserId,
      description: description
    };
    const res = await createActionItem(newActionItem, csrf);
    if (!res.error && res.payload && res.payload.data) {
      newActionItem.id = res.payload.data.id;
      newActionItem.priority = res.payload.data.priority;
      setDescription('');
      setActionItems([...actionItems, newActionItem]);
    }
  };

  const handleDescriptionChange = (index, e) => {
    if (actionItems[index].createdbyid !== currentUserId) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Action Items can only be edited by creator'
        }
      });
      return;
    }
    const { value } = e.target;
    actionItems[index].description = value;
    setActionItems(() => {
      updateItem(actionItems[index], csrf);
      return [...actionItems];
    });
  };

  const killActionItem = id => {
    if (csrf) {
      deleteItem(id, csrf);
      let newItems = actionItems.filter(actionItem => {
        return actionItem.id !== id;
      });
      setActionItems(newItems);
    }
  };

  const createActionItemEntries = () => {
    if (actionItems && actionItems.length > 0) {
      return actionItems.map((actionItem, index) => (
        <Draggable
          isDragDisabled={currentCheckin?.completed}
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
                <span style={{ cursor: 'grab' }} {...provided.dragHandleProps}>
                  <DragIndicator />
                </span>
                {isLoading ? (
                  <div className="skeleton">
                    <Skeleton className="test" variant="text" height={'2rem'} />
                    <Skeleton variant="text" height={'2rem'} />
                    <Skeleton variant="text" height={'2rem'} />
                  </div>
                ) : (
                  <input
                    disabled={currentCheckin?.completed}
                    className="text-input"
                    onChange={e => handleDescriptionChange(index, e)}
                    value={actionItem.description}
                  />
                )}
                <div className="action-item-button-div">
                  <IconButton
                    disabled={currentCheckin?.completed}
                    aria-label="delete"
                    className="delete-icon"
                    onClick={() => killActionItem(actionItem.id)}
                    size="large"
                  >
                    <RemoveIcon />
                  </IconButton>
                </div>
              </div>
            </div>
          )}
        </Draggable>
      ));
    }
  };

  return (
    <Card className="action-items">
      <CardHeader
        avatar={<ArrowForwardIcon />}
        title="Action Items"
        titleTypographyProps={{ variant: 'h5', component: 'h2' }}
      />
      <CardContent className="action-items-container">
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable
            isDropDisabled={currentCheckin?.completed}
            droppableId="droppable"
          >
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
            disabled={currentCheckin?.completed}
            className="text-input"
            placeholder="Add action item"
            onChange={e => setDescription(e.target.value)}
            onKeyPress={e => {
              if (e.key === 'Enter' && description !== '') {
                makeActionItem();
              }
            }}
            value={description ? description : ''}
          />
          <IconButton
            disabled={currentCheckin?.completed}
            aria-label="create"
            className="edit-icon"
            onClick={() => makeActionItem()}
            size="large"
          >
            <SaveIcon />
          </IconButton>
        </div>
      </CardContent>
    </Card>
  );
};

export default ActionItemsPanel;
