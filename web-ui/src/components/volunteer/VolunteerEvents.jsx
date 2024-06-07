import { format } from 'date-fns';
import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit, Event } from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  Card,
  CardContent,
  CardHeader,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip
} from '@mui/material';

import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { AppContext } from '../../context/AppContext';
import { selectCurrentUser, selectProfileMap } from '../../context/selectors';

const eventBaseUrl = 'http://localhost:3000/volunteer-event';
const organizationBaseUrl = 'http://localhost:3000/organization';
const relationshipBaseUrl = 'http://localhost:3000/volunteer-relationship';

const formatDate = date => {
  if (!date) return '';
  if (!(date instanceof Date)) date = new Date(date.$y, date.$M, date.$D);
  return format(date, 'yyyy-MM-dd');
};

const sortableTableColumns = ['Relationship', 'Date', 'Hours', 'Notes'];

const propTypes = { forceUpdate: PropTypes.func, onlyMe: PropTypes.bool };

const VolunteerEvents = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const { state } = useContext(AppContext);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [eventDialogOpen, setEventDialogOpen] = useState(false);
  const [events, setEvents] = useState([]);
  const [organizationMap, setOrganizationMap] = useState({});
  const [relationshipMap, setRelationshipMap] = useState({});
  const [relationships, setRelationships] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Member');

  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadEvents = useCallback(async () => {
    try {
      let res = await fetch(eventBaseUrl);
      const events = await res.json();
      events.sort((event1, event2) => event1.date.localeCompare(event2.date));
      setEvents(events);
    } catch (err) {
      console.error(err);
    }
  }, []);

  const loadOrganizations = useCallback(async () => {
    try {
      let res = await fetch(organizationBaseUrl);
      const organizations = await res.json();
      setOrganizationMap(
        organizations.reduce((acc, org) => ({ ...acc, [org.id]: org }), {})
      );
    } catch (err) {
      console.error(err);
    }
  }, []);

  const loadRelationships = useCallback(async () => {
    if (isEmpty(profileMap)) return;
    try {
      let res = await fetch(relationshipBaseUrl);
      const relationships = await res.json();
      relationships.sort((rel1, rel2) => {
        const member1 = profileMap[rel1.memberId];
        const member2 = profileMap[rel2.memberId];
        return member1.name.localeCompare(member2.name);
      });
      setRelationships(relationships);
      setRelationshipMap(
        relationships.reduce((acc, rel) => ({ ...acc, [rel.id]: rel }), {})
      );
    } catch (err) {
      console.error(err);
    }
  }, [profileMap]);

  useEffect(() => {
    loadEvents();
    loadOrganizations();
    loadRelationships();
  }, [profileMap]);

  useEffect(() => {
    if (!isEmpty(organizationMap)) loadEvents();
  }, [organizationMap]);

  useEffect(() => {
    sortEvents(events);
    setEvents([...events]);
  }, [sortAscending, sortColumn]);

  const addEvent = useCallback(() => {
    setSelectedEvent({
      relationshipId: '',
      date: '',
      hours: 0,
      notes: ''
    });
    setEventDialogOpen(true);
  }, []);

  const cancelEvent = useCallback(() => {
    setSelectedEvent(null);
    setEventDialogOpen(false);
  }, []);

  const confirmDelete = useCallback(relationship => {
    setSelectedEvent(relationship);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteEvent = useCallback(async relationship => {
    const url = eventBaseUrl + '/' + event.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setEvents(orgs => orgs.filter(org => org.id !== relationship.id));
    } catch (err) {
      console.error(err);
    }
  }, []);

  const editEvent = useCallback(
    relationship => {
      setSelectedEvent(relationship);
      setEventDialogOpen(true);
    },
    [relationshipMap]
  );

  const eventDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'volunteer-dialog' }}
        open={eventDialogOpen}
        onClose={cancelEvent}
      >
        <DialogTitle>{selectedEvent?.id ? 'Edit' : 'Add'} Event</DialogTitle>
        <DialogContent>
          <Autocomplete
            disableClearable
            getOptionLabel={relationshipName}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, relationship) => {
              setSelectedEvent({
                ...selectedEvent,
                relationshipId: relationship.id
              });
            }}
            options={relationships}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Volunteer Relationship"
              />
            )}
            value={
              selectedEvent?.relationshipId
                ? relationshipMap[selectedEvent.relationshipId]
                : null
            }
          />
          <DatePickerField
            date={getDate(selectedEvent?.date)}
            label="Date"
            setDate={date => {
              setSelectedEvent({
                ...selectedEvent,
                date: formatDate(date)
              });
            }}
          />
          <TextField
            label="Hours"
            onChange={e => {
              const hours = Number(e.target.value);
              if (hours >= 0) setSelectedEvent({ ...selectedEvent, hours });
            }}
            type="number"
            value={selectedEvent?.hours ?? 0}
          />
          <TextField
            label="Notes"
            onChange={e =>
              setSelectedEvent({ ...selectedEvent, notes: e.target.value })
            }
            value={selectedEvent?.notes ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelEvent}>Cancel</Button>
          <Button disabled={!validEvent()} onClick={saveEvent}>
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [eventDialogOpen, selectedEvent]
  );

  const eventRow = useCallback(
    event => {
      const relationship = relationshipMap[event.relationshipId];
      const member = profileMap[relationship.memberId];
      const organization = organizationMap[relationship.organizationId];
      return (
        <tr key={event.id}>
          <td>{member.name + ' - ' + organization.name}</td>
          <td>{event.date}</td>
          <td>{event.hours}</td>
          <td>{event.notes}</td>
          <td>
            <Tooltip title="Edit">
              <IconButton aria-label="Edit" onClick={() => editEvent(event)}>
                <Edit />
              </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
              <IconButton
                aria-label="Delete"
                onClick={() => confirmDelete(event)}
              >
                <Delete />
              </IconButton>
            </Tooltip>
          </td>
        </tr>
      );
    },
    [organizationMap, profileMap, relationshipMap]
  );

  const eventsTable = useCallback(() => {
    if (isEmpty(profileMap)) return null;
    if (isEmpty(organizationMap)) return null;
    if (isEmpty(relationshipMap)) return null;

    return (
      <Card>
        <CardHeader
          avatar={<Event />}
          title="Events"
          titleTypographyProps={{ variant: 'h5', component: 'h2' }}
        />
        <CardContent>
          <div className="row">
            <table>
              <thead>
                <tr>
                  {sortableTableColumns.map(column => (
                    <th
                      key={column}
                      onClick={() => sortTable(column)}
                      style={{ cursor: 'pointer' }}
                    >
                      {column}
                      {sortIndicator(column)}
                    </th>
                  ))}
                  <th className="actions-th" key="actions">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody>{events.map(eventRow)}</tbody>
            </table>
            <IconButton
              aria-label="Add Volunteer Event"
              classes={{ root: 'add-button' }}
              onClick={addEvent}
            >
              <AddCircleOutline />
            </IconButton>
          </div>
        </CardContent>
      </Card>
    );
  }, [
    events,
    organizationMap,
    profileMap,
    relationshipMap,
    sortAscending,
    sortColumn
  ]);

  const getDate = dateString => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-');
    return new Date(Number(year), Number(month) - 1, Number(day));
  };

  const isEmpty = map => Object.keys(map).length === 0;

  const eventValue = useCallback(
    event => {
      switch (sortColumn) {
        case 'Relationship':
          const relationship = relationshipMap[event.relationshipId];
          return relationshipName(relationship);
        case 'Date':
          return event.date || '';
        case 'Hours':
          return event.hours || 0;
        case 'Notes':
          return event.notes || '';
      }
    },
    [relationshipMap, sortColumn]
  );

  const relationshipName = relationship => {
    const member = profileMap[relationship.memberId];
    const org = organizationMap[relationship.organizationId];
    return `${member?.name} - ${org?.name}`;
  };

  const saveEvent = useCallback(async () => {
    const { id } = selectedEvent;
    const url = id ? `${eventBaseUrl}/${id}` : eventBaseUrl;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedEvent)
      });
      const newRel = await res.json();

      if (id) {
        const index = events.findIndex(rel => rel.id === id);
        events[index] = newRel;
      } else {
        events.push(newRel);
      }
      sortEvents(events);
      setEvents(events);

      setSelectedEvent(null);
    } catch (err) {
      console.error(err);
    }
    setEventDialogOpen(false);
  }, [selectedEvent]);

  const sortEvents = useCallback(
    events => {
      events.sort((event1, event2) => {
        const v1 = eventValue(event1);
        const v2 = eventValue(event2);
        if (typeof v1 === 'number' && typeof v2 === 'number') {
          return sortAscending ? v1 - v2 : v2 - v1;
        } else {
          return sortAscending ? v1.localeCompare(v2) : v2.localeCompare(v1);
        }
      });
    },
    [sortAscending, sortColumn]
  );

  const sortIndicator = useCallback(
    column => {
      if (column !== sortColumn) return '';
      return ' ' + (sortAscending ? '🔼' : '🔽');
    },
    [sortAscending, sortColumn]
  );

  const sortTable = useCallback(
    column => {
      if (column === sortColumn) {
        setSortAscending(ascending => !ascending);
      } else {
        setSortColumn(column);
        setSortAscending(true);
      }
    },
    [sortAscending, sortColumn]
  );

  const validEvent = useCallback(() => {
    const event = selectedEvent;
    return event?.relationshipId && event?.date && event?.hours;
  });

  return (
    <div id="volunteer-relationships">
      {eventsTable()}

      {eventDialog()}

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteEvent(selectedEvent)}
        question="Are you sure you want to delete this event?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Event"
      />
    </div>
  );
};

VolunteerEvents.propTypes = propTypes;

export default VolunteerEvents;
