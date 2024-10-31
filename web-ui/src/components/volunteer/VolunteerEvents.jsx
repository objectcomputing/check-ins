import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit } from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip
} from '@mui/material';

import { resolve } from '../../api/api.js';
import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import OrganizationDialog from '../dialogs/OrganizationDialog'; // Include OrganizationDialog
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfileMap,
  selectHasVolunteeringEventsPermission,
} from '../../context/selectors';
import { formatDate } from '../../helpers/datetime';

const eventBaseUrl = '/services/volunteer/event';
const organizationBaseUrl = '/services/volunteer/organization';
const relationshipBaseUrl = '/services/volunteer/relationship';

const propTypes = { forceUpdate: PropTypes.func, onlyMe: PropTypes.bool };

const VolunteerEvents = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [eventDialogOpen, setEventDialogOpen] = useState(false);
  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false); // Organization dialog state
  const [events, setEvents] = useState([]);
  const [organizationMap, setOrganizationMap] = useState({});
  const [relationshipMap, setRelationshipMap] = useState({});
  const [relationships, setRelationships] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [newOrganization, setNewOrganization] = useState({ name: '', description: '', website: '' });
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Relationship');

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  let sortableTableColumns = ['Date', 'Hours', 'Notes'];
  sortableTableColumns.unshift(onlyMe ? 'Organization' : 'Relationship');

  const loadEvents = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: eventBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    let events = res.payload.data;
    if (onlyMe) {
      events = events.filter(e => Boolean(relationshipMap[e.relationshipId]));
    }
    events.sort((event1, event2) => event1.eventDate.localeCompare(event2.eventDate));
    setEvents(events);
  }, [csrf, relationshipMap]);

  const loadOrganizations = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: organizationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const organizations = res.payload.data;
    setOrganizationMap(organizations.reduce((acc, org) => ({ ...acc, [org.id]: org }), {}));
  }, [csrf]);

  const loadRelationships = useCallback(async () => {
    if (isEmpty(profileMap)) return;
    let url = relationshipBaseUrl;
    if (onlyMe) url += '?memberId=' + currentUser.id;
    const res = await resolve({
      method: 'GET',
      url,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const relationships = res.payload.data;
    relationships.sort((rel1, rel2) => {
      const member1 = profileMap[rel1.memberId];
      const member2 = profileMap[rel2.memberId];
      return member1.name.localeCompare(member2.name);
    });
    setRelationships(relationships);
    setRelationshipMap(relationships.reduce((acc, rel) => ({ ...acc, [rel.id]: rel }), {}));
  }, [csrf, onlyMe, profileMap]);

  useEffect(() => {
    if (csrf) {
      loadOrganizations();
      loadRelationships();
    }
  }, [csrf, profileMap]);

  useEffect(() => {
    if (csrf && !isEmpty(organizationMap) && !isEmpty(relationshipMap)) {
      loadEvents();
    }
  }, [csrf, organizationMap, relationshipMap]);

  useEffect(() => {
    sortEvents(events);
    setEvents([...events]);
  }, [relationshipMap, sortAscending, sortColumn]);

  const addEvent = useCallback(() => {
    setSelectedEvent({
      relationshipId: '',
      eventDate: '',
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

  const deleteEvent = useCallback(async event => {
    const res = await resolve({
      method: 'DELETE',
      url: eventBaseUrl + '/' + event.id,
      headers: { 'X-CSRF-Header': csrf }
    });
    if (res.error) return;

    setEvents(events => events.filter(e => e.id !== event.id));
  }, []);

  const editEvent = useCallback(
    relationship => {
      setSelectedEvent(relationship);
      setEventDialogOpen(true);
    },
    [relationshipMap]
  );

  const openCreateOrganizationDialog = useCallback(() => {
    setNewOrganization({ name: '', description: '', website: '' });
    setOrganizationDialogOpen(true);
  }, []);

  const saveOrganizationAndRelationship = useCallback(async () => {
    const { name, description, website } = newOrganization;
    if (!name || !description) {
        console.error('Missing organization name or description');
        return;
    }

    try {
        // Step 1: Create the organization
        const res = await resolve({
            method: 'POST',
            url: organizationBaseUrl,
            headers: {
                'X-CSRF-Header': csrf,
                Accept: 'application/json',
                'Content-Type': 'application/json;charset=UTF-8'
            },
            data: { name, description, website }
        });

        if (res.error) {
            console.error('Error creating organization', res.error);
            return;
        }

        const createdOrg = res.payload.data;

        // Step 2: Create a relationship between the current user and the newly created organization
        const relationshipRes = await resolve({
            method: 'POST',
            url: relationshipBaseUrl, // Ensure the correct URL is used
            headers: {
                'X-CSRF-Header': csrf,
                Accept: 'application/json',
                'Content-Type': 'application/json;charset=UTF-8'
            },
            data: {
                memberId: currentUser.id,
                organizationId: createdOrg.id,
                startDate: formatDate(new Date()), // Set the start date as the current date
                endDate: null // Leave endDate as null for an active relationship
            }
        });

        if (relationshipRes.error) {
            console.error('Error creating relationship', relationshipRes.error);
            return;
        }

        const createdRelationship = relationshipRes.payload.data;

        // Step 3: Update the organization and relationship maps
        setOrganizationMap(prev => ({ ...prev, [createdOrg.id]: createdOrg }));
        setRelationshipMap(prev => ({ ...prev, [createdRelationship.id]: createdRelationship }));

        // Step 4: Update selectedEvent with the new relationship
        setSelectedEvent({
            ...selectedEvent,
            relationshipId: createdRelationship.id // Set the new relationship ID
        });

        // Step 5: Close organization dialog and open event dialog
        setOrganizationDialogOpen(false);
        setEventDialogOpen(true);

    } catch (error) {
        console.error('Failed to create organization and relationship', error);
    }
}, [newOrganization, csrf, currentUser.id, selectedEvent]);

  const eventDialog = useCallback(
    () => (
      <Dialog classes={{ root: 'volunteer-dialog' }} open={eventDialogOpen} onClose={cancelEvent}>
        <DialogTitle>{selectedEvent?.id ? 'Edit' : 'Add'} Event</DialogTitle>
        <DialogContent>
        <Autocomplete
          disableClearable
          getOptionLabel={(option) => 
            option === 'new' ? 'Create a New Organization' : (relationshipMap[option]?.organizationId && organizationMap[relationshipMap[option].organizationId]?.name) || 'Unknown'
          }
          options={['new', ...relationships.filter((rel) => !rel.endDate).map((rel) => rel.id)]} // Use relationship IDs
          onChange={(event, value) => {
            if (value === 'new') {
              openCreateOrganizationDialog(); // Open the organization creation dialog
            } else {
              setSelectedEvent({
                ...selectedEvent,
                relationshipId: value // Set relationshipId correctly
              });
            }
          }}
          renderInput={(params) => <TextField {...params} className="fullWidth" label="Organization" />}
          value={selectedEvent?.relationshipId || ''} // Bind to the correct relationship ID
        />
          <DatePickerField
            date={getDate(selectedEvent?.eventDate)}
            label="Date"
            setDate={(date) => setSelectedEvent({ ...selectedEvent, eventDate: formatDate(date) })}
          />
          <TextField
            label="Hours You Volunteered"
            onChange={(e) => {
              const hours = Number(e.target.value);
              if (hours >= 0) setSelectedEvent({ ...selectedEvent, hours });
            }}
            type="number"
            value={selectedEvent?.hours ?? 0}
          />
          <TextField
            label="Notes"
            onChange={(e) => setSelectedEvent({ ...selectedEvent, notes: e.target.value })}
            value={selectedEvent?.notes ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelEvent}>Cancel</Button>
          <Button disabled={!validEvent()} onClick={saveEvent}>Save</Button>
        </DialogActions>
      </Dialog>
    ),
    [eventDialogOpen, selectedEvent, relationships, relationshipMap]
  );

  const eventRow = useCallback(
    (event) => {
      const relationship = relationshipMap[event.relationshipId];
  
      if (!relationship) {
        console.error(`Relationship ${event.relationshipId} not found in relationshipMap.`);
        return null;
      }
  
      const member = profileMap[relationship?.memberId];
      const org = organizationMap[relationship?.organizationId];
  
      if (!member || !org) {
        console.error(`Member or Organization not found for relationship ${event.relationshipId}`);
        return null;
      }
  
      return (
        <tr key={event.id}>
          <td>{onlyMe ? org.name : `${member.name} - ${org.name}`}</td>
          <td>{event.eventDate}</td>
          <td>{event.hours}</td>
          <td>{event.notes}</td>
          <td>
            {(member.id == currentUser.id ||
              selectHasVolunteeringEventsPermission(state)) &&
            <>
            <Tooltip title="Edit">
              <IconButton aria-label="Edit" onClick={() => editEvent(event)}>
                <Edit />
              </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
              <IconButton aria-label="Delete" onClick={() => confirmDelete(event)}>
                <Delete />
              </IconButton>
            </Tooltip>
            </>}
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
      <div className="row">
        <table>
          <thead>
            <tr>
              {sortableTableColumns.map(column => (
                <th key={column} onClick={() => sortTable(column)} style={{ cursor: 'pointer' }}>
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
        {(onlyMe || selectHasVolunteeringEventsPermission(state)) &&
        <IconButton aria-label="Add Volunteer Event" classes={{ root: 'add-button' }} onClick={addEvent}>
          <AddCircleOutline />
        </IconButton>}
      </div>
    );
  }, [events, organizationMap, profileMap, relationshipMap, sortAscending, sortColumn]);

  const getDate = dateString => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-');
    return new Date(Number(year), Number(month) - 1, Number(day));
  };

  const isEmpty = map => Object.keys(map).length === 0;

  const eventValue = useCallback(
    event => {
      switch (sortColumn) {
        case 'Organization':
        case 'Relationship':
          const relationship = relationshipMap[event.relationshipId];
          return relationshipName(relationship);
        case 'Date':
          return event.eventDate || '';
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
    return onlyMe ? org?.name : `${member?.name} - ${org?.name}`;
  };

  const saveEvent = useCallback(async () => {
    const { id, relationshipId, eventDate, hours, notes } = selectedEvent;
    
    // Check if relationshipId is valid
    if (!relationshipId) {
      console.error('No relationship selected for the event.');
      return;
    }
  
    // Check that all required fields are filled in
    if (!eventDate || hours <= 0) {
      console.error("Missing required fields: date or hours.");
      return;
    }
  
    try {
      // Ensure the relationship exists
      const relationshipCheck = await resolve({
        method: 'GET',
        url: `${relationshipBaseUrl}/${relationshipId}`,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
  
      if (relationshipCheck.error || !relationshipCheck.payload) {
        console.error(`Relationship ${relationshipId} doesn't exist.`);
        return;
      }
  
      // Proceed with saving the event
      const res = await resolve({
        method: id ? 'PUT' : 'POST',
        url: id ? `${eventBaseUrl}/${id}` : eventBaseUrl,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        },
        data: { relationshipId, eventDate, hours, notes }
      });
  
      if (res.error) {
        console.error('Error saving event:', res.error);
        return;
      }
  
      // Update event list and close dialog
      const newEvent = res.payload.data;
      if (id) {
        const index = events.findIndex(e => e.id === id);
        events[index] = newEvent;
      } else {
        events.push(newEvent);
      }
  
      sortEvents(events);
      setEvents(events);
      setSelectedEvent(null);
      setEventDialogOpen(false); // Close dialog after save
    } catch (error) {
      console.error('Failed to save event:', error);
    }
  }, [selectedEvent, events, csrf]);

  const sortEvents = useCallback(
    events => {
      events.sort((event1, event2) => {
        const v1 = eventValue(event1);
        const v2 = eventValue(event2);
        if (typeof v1 === 'number' && typeof v2 === 'number') {
          return sortAscending ? v1 - v2 : v2 - v1;
        } else {
          return sortAscending ? v1?.localeCompare(v2) : v2?.localeCompare(v1);
        }
      });
    },
    [relationshipMap, sortAscending, sortColumn]
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
    return event?.relationshipId && event?.eventDate && event?.hours;
  });

  return (
    <div id="volunteer-relationships">
      {eventsTable()}

      {eventDialog()}

      {/* Dialog for creating a new organization */}
      <OrganizationDialog
        open={organizationDialogOpen}
        onClose={() => setOrganizationDialogOpen(false)}
        onSave={saveOrganizationAndRelationship}
        organization={newOrganization}
        setOrganization={setNewOrganization}
      />

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
