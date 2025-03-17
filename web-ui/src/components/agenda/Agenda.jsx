import React, { useContext, useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import {
  getAgendaItem,
  deleteAgendaItem,
  updateAgendaItem,
  createAgendaItem
} from '../../api/agenda.js';
import { AppContext } from '../../context/AppContext';
import { UPDATE_TOAST } from '../../context/actions';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectCheckin
} from '../../context/selectors';
import { debounce } from 'lodash/function';
import DragIndicator from '@mui/icons-material/DragIndicator';
import AdjustIcon from '@mui/icons-material/Adjust';
import Skeleton from '@mui/material/Skeleton';
import IconButton from '@mui/material/IconButton';
import SaveIcon from '@mui/icons-material/Done';
import RemoveIcon from '@mui/icons-material/Remove';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardContent from '@mui/material/CardContent';

import './Agenda.css';

const doUpdate = async (agendaItem, csrf) => {
  if (agendaItem && csrf) {
    await updateAgendaItem(agendaItem, csrf);
  }
};

const updateItem = debounce(doUpdate, 1500);

const AgendaItems = () => {
  const { state, dispatch } = useContext(AppContext);
  const { checkinId } = useParams();
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const currentCheckin = selectCheckin(state, checkinId);

  const [agendaItems, setAgendaItems] = useState([]);
  const [description, setDescription] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const getAgendaItems = async (checkinId, csrf) => {
    setIsLoading(true);
    let res = await getAgendaItem(checkinId, null, csrf);
    let agendaItemList;
    if (res && res.payload) {
      agendaItemList =
        res.payload.data && !res.error ? res.payload.data : undefined;
      if (agendaItemList) {
        agendaItemList.sort((a, b) => {
          return a.priority - b.priority;
        });
        setAgendaItems(agendaItemList);
      }
    }
    setIsLoading(false);
  };

  const deleteItem = async (id, csrf) => {
    if (id && csrf) {
      await deleteAgendaItem(id, csrf);
    }
  };

  useEffect(() => {
    if (csrf) {
      getAgendaItems(checkinId, csrf);
    }
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
      const lastIndex = agendaItems.length - 1;
      const precedingPriority =
        index === 0 ? 0 : agendaItems[index - 1].priority;
      const followingPriority =
        index === lastIndex
          ? agendaItems[lastIndex].priority + 1
          : agendaItems[index].priority;

      let newPriority = (precedingPriority + followingPriority) / 2;
      if (agendaItems[sourceIndex].priority <= followingPriority) {
        newPriority += 1;
      }

      setAgendaItems(agendaItems => {
        agendaItems[sourceIndex].priority = newPriority;
        reorder(agendaItems, sourceIndex, index);
        return agendaItems;
      });

      doUpdate(agendaItems[result.destination.index], csrf);
    }
  };

  const makeAgendaItem = async () => {
    if (!checkinId || !currentUserId || description === '' || !csrf) {
      return;
    }
    let newAgendaItem = {
      checkinid: checkinId,
      createdbyid: currentUserId,
      description: description
    };
    const res = await createAgendaItem(newAgendaItem, csrf);
    if (!res.error && res.payload && res.payload.data) {
      newAgendaItem.id = res.payload.data.id;
      newAgendaItem.priority = res.payload.data.priority;
      setDescription('');
      setAgendaItems([...agendaItems, newAgendaItem]);
    }
  };

  const killAgendaItem = id => {
    if (csrf) {
      deleteItem(id, csrf);
      let newItems = agendaItems.filter(agendaItem => {
        return agendaItem.id !== id;
      });
      setAgendaItems(newItems);
    }
  };

  const handleDescriptionChange = (index, e) => {
    if (agendaItems[index].createdbyid !== currentUserId) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Agenda Items can only be edited by creator'
        }
      });
      return;
    }
    const { value } = e.target;
    agendaItems[index].description = value;
    setAgendaItems(() => {
      if (csrf) {
        updateItem(agendaItems[index], csrf);
        return [...agendaItems];
      }
    });
  };

  const createAgendaItemEntries = () => {
    if (agendaItems && agendaItems.length > 0) {
      return agendaItems.map((agendaItem, index) => (
        <Draggable
          isDragDisabled={currentCheckin?.completed}
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
                    <Skeleton variant="text" height={'2rem'} />
                    <Skeleton variant="text" height={'2rem'} />
                    <Skeleton variant="text" height={'2rem'} />
                  </div>
                ) : (
                  <input
                    disabled={currentCheckin?.completed}
                    className="text-input"
                    onChange={e => handleDescriptionChange(index, e)}
                    value={agendaItem.description}
                  />
                )}
                <div className="agenda-item-button-div">
                  <IconButton
                    disabled={currentCheckin?.completed}
                    aria-label="delete"
                    className="delete-icon"
                    onClick={() => killAgendaItem(agendaItem.id)}
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
    <Card className="agenda-items">
      <CardHeader
        avatar={<AdjustIcon />}
        title="Agenda Items"
        titleTypographyProps={{ variant: 'h5', component: 'h2' }}
      />
      <CardContent className="agenda-items-container">
        <DragDropContext
          isDropDisabled={currentCheckin?.completed}
          onDragEnd={onDragEnd}
        >
          <Droppable droppableId="droppable">
            {(provided, snapshot) => (
              <div {...provided.droppableProps} ref={provided.innerRef}>
                {createAgendaItemEntries()}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
        <div className="add-agenda-item-div">
          <input
            disabled={currentCheckin?.completed}
            className="text-input"
            placeholder="Add an agenda item"
            onChange={e => setDescription(e.target.value)}
            onKeyPress={e => {
              if (e.key === 'Enter' && description !== '') {
                makeAgendaItem();
              }
            }}
            value={description ? description : ''}
          />
          <IconButton
            disabled={currentCheckin?.completed}
            aria-label="create"
            className="edit-icon"
            onClick={() => makeAgendaItem()}
            size="large"
          >
            <SaveIcon />
          </IconButton>
        </div>
      </CardContent>
    </Card>
  );
};

export default AgendaItems;
