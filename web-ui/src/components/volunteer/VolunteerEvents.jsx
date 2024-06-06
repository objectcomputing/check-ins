import { format } from 'date-fns';
import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit, FoodBank } from '@mui/icons-material';
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
import './VolunteerEvents.css';

const eventBaseUrl = 'http://localhost:3000/volunteer-event';
const organizationBaseUrl = 'http://localhost:3000/organization';
const relationshipBaseUrl = 'http://localhost:3000/volunteer-relationship';

const formatDate = date => {
  if (!date) return '';
  if (!(date instanceof Date)) date = new Date(date.$y, date.$M, date.$D);
  return format(date, 'yyyy-MM-dd');
};

const sortableTableColumns = ['Relationship', 'Date', 'Hours', 'Notes'];

const propTypes = { forceUpdate: PropTypes.func };

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
      console.log('VolunteerEvents.jsx loadEvents: events =', events);
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
    try {
      let res = await fetch(relationshipBaseUrl);
      const relationships = await res.json();
      setRelationshipMap(
        relationships.reduce((acc, rel) => ({ ...acc, [rel.id]: rel }), {})
      );
    } catch (err) {
      console.error(err);
    }
  }, []);

  useEffect(() => {
    loadEvents();
    loadOrganizations();
    loadRelationships();
  }, []);

  useEffect(() => {
    if (Object.keys(organizationMap).length > 0) loadEvents();
  }, [organizationMap]);

  useEffect(() => {
    sortEvents(relationships);
    setEvents([...relationships]);
  }, [sortAscending, sortColumn]);

  const addEvent = useCallback(() => {
    setSelectedEvent({
      memberId: '',
      organizationId: '',
      startDate: '',
      endDate: ''
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
    const url = relationshipBaseUrl + '/' + relationship.id;
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

  const getDate = dateString => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-');
    return new Date(Number(year), Number(month) - 1, Number(day));
  };

  const eventDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'relationship-dialog' }}
        open={eventDialogOpen}
        onClose={cancelEvent}
      >
        <DialogTitle>{selectedEvent?.id ? 'Edit' : 'Add'} Event</DialogTitle>
        <DialogContent>
          <Autocomplete
            disableClearable
            getOptionLabel={profile => profile.name ?? ''}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, profile) => {
              setSelectedEvent({
                ...selectedEvent,
                memberId: profile.id
              });
            }}
            options={profiles}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Team Member"
              />
            )}
            value={
              selectedEvent?.memberId
                ? profileMap[selectedEvent.memberId]
                : null
            }
          />
          <Autocomplete
            disableClearable
            getOptionLabel={event => {
              const member = profileMap[event.memberId];
              const organization = organizationMap[event.organizationId];
              return `${member.name} - ${organization.name}`;
            }}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, organization) => {
              setSelectedEvent({
                ...selectedEvent,
                organizationId: organization.id
              });
            }}
            options={events}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Organization"
              />
            )}
            value={
              selectedEvent?.organizationId
                ? organizationMap[selectedEvent.organizationId]
                : null
            }
          />
          <DatePickerField
            date={getDate(selectedEvent?.startDate)}
            label="Start Date"
            setDate={date => {
              const startDate = formatDate(date);
              let { endDate } = selectedEvent;
              if (endDate && startDate > endDate) endDate = startDate;
              setSelectedEvent({
                ...selectedEvent,
                startDate,
                endDate
              });
            }}
          />
          <DatePickerField
            date={getDate(selectedEvent?.endDate)}
            label="End Date"
            setDate={date => {
              const endDate = date ? formatDate(date) : '';
              let { startDate } = selectedEvent;
              if (endDate && (!startDate || endDate < startDate)) {
                startDate = endDate;
              }
              setSelectedEvent({
                ...selectedEvent,
                startDate,
                endDate
              });
            }}
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
      const member = profileMap[event.memberId];
      const organization = organizationMap[event.organizationId];
      return (
        <tr key={event.id}>
          <td>{member.name + ' - ' + organization.name}</td>
          <td>{event.date}</td>
          <td>{event.hours}</td>
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
    [organizationMap, profileMap]
  );

  const eventsTable = useCallback(
    () => (
      <Card>
        <CardHeader
          avatar={<FoodBank />}
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
    ),
    [relationships, sortAscending, sortColumn]
  );

  const relationshipValue = useCallback(
    relationship => {
      switch (sortColumn) {
        case 'Member':
          return profileMap[relationship.memberId]?.name ?? '';
        case 'Organization':
          return organizationMap[relationship.organizationId]?.name ?? '';
        case 'Start Date':
          return relationship.startDate || '';
        case 'End Date':
          return relationship.endDate || '';
      }
    },
    [relationshipMap, sortColumn]
  );

  const saveEvent = useCallback(async () => {
    const { id } = selectedEvent;
    const url = id ? `${relationshipBaseUrl}/${id}` : relationshipBaseUrl;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedEvent)
      });
      const newRel = await res.json();

      if (id) {
        const index = relationships.findIndex(rel => rel.id === id);
        relationships[index] = newRel;
      } else {
        relationships.push(newRel);
      }
      sortEvents(relationships);
      setEvents(relationships);

      setSelectedEvent(null);
    } catch (err) {
      console.error(err);
    }
    setEventDialogOpen(false);
  }, [selectedEvent]);

  const sortEvents = useCallback(
    orgs => {
      orgs.sort((org1, org2) => {
        const v1 = relationshipValue(org1);
        const v2 = relationshipValue(org2);
        return sortAscending ? v1.localeCompare(v2) : v2.localeCompare(v1);
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
    const rel = selectedEvent;
    return rel?.memberId && rel?.organizationId;
  });

  return (
    <div id="volunteer-relationships">
      {eventsTable()}

      {eventDialog()}

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteEvent(selectedEvent)}
        question="Are you sure you want to delete this relationship?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Event"
      />
    </div>
  );
};

VolunteerEvents.propTypes = propTypes;

export default VolunteerEvents;
