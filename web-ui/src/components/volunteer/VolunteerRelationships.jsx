import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit } from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  IconButton,
  TextField,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle
} from '@mui/material';

import { resolve } from '../../api/api';
import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import OrganizationDialog from '../dialogs/OrganizationDialog';
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfileMap,
  selectHasVolunteeringRelationshipsPermission,
  selectHasVolunteeringOrganizationsPermission,
} from '../../context/selectors';
import { formatDate } from '../../helpers/datetime';
import { showError } from '../../helpers/toast';

const relationshipBaseUrl = '/services/volunteer/relationship';

const propTypes = { forceUpdate: PropTypes.func, onlyMe: PropTypes.bool };

const VolunteerRelationships = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [organizationMap, setOrganizationMap] = useState({});
  const [organizations, setOrganizations] = useState([]);
  const [relationshipDialogOpen, setRelationshipDialogOpen] = useState(false);
  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false);
  const [newOrganization, setNewOrganization] = useState({ name: '', description: '', website: '' });
  const [relationshipMap, setRelationshipMap] = useState({});
  const [relationships, setRelationships] = useState([]);
  const [selectedRelationship, setSelectedRelationship] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Member');

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap).filter(profile => profile && profile.name);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const sortableTableColumns = ['Organization', 'Start Date', 'End Date'];
  if (!onlyMe) sortableTableColumns.unshift('Member');

  // Fetch organizations
  const loadOrganizations = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: '/services/volunteer/organization',
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const organizations = res.payload.data || [];
    organizations.sort((org1, org2) => (org1.name || '').localeCompare(org2.name || ''));
    setOrganizations(organizations);
    setOrganizationMap(
      organizations.reduce((acc, org) => ({ ...acc, [org.id]: org }), {})
    );
  }, [csrf]);

  // Fetch relationships
  const loadRelationships = useCallback(async () => {
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
  
    const relationships = res.payload.data || [];
    relationships.sort((rel1, rel2) => {
      const member1 = profileMap[rel1.memberId];
      const member2 = profileMap[rel2.memberId];
      return (member1?.name || '').localeCompare(member2?.name || '');
    });
    setRelationships(relationships);
    setRelationshipMap(relationships.reduce((acc, rel) => ({ ...acc, [rel.id]: rel }), {}));
  }, [currentUser.id, onlyMe, profileMap, csrf]);

  useEffect(() => {
    loadOrganizations();
  }, [loadOrganizations]);

  useEffect(() => {
    if (Object.keys(organizationMap).length > 0) loadRelationships();
  }, [organizationMap, loadRelationships]);

  const refreshRelationships = async () => {
    await loadRelationships();
  };

  const addRelationship = useCallback(() => {
    setSelectedRelationship({
      memberId: onlyMe ? currentUser.id : '',
      organizationId: '',
      startDate: null,
      endDate: null
    });
    setRelationshipDialogOpen(true);
  }, [currentUser.id, onlyMe]);

  const cancelRelationship = useCallback(() => {
    setSelectedRelationship(null);
    setRelationshipDialogOpen(false);
  }, []);

  const cancelOrganizationCreation = useCallback(() => {
    setOrganizationDialogOpen(false);
  }, []);

  const confirmDelete = useCallback(relationship => {
    setSelectedRelationship(relationship);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteRelationship = useCallback(async relationship => {
    if (!relationship) return;
    const res = await resolve({
      method: 'PUT',
      url: relationshipBaseUrl + '/' + relationship.id,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: { ...relationship, active: false }
    });
    if (res.error) return;

    // Refresh the relationships list after deletion
    await refreshRelationships();
    setConfirmDeleteOpen(false);
  }, [csrf, refreshRelationships]);

  const saveRelationship = useCallback(async () => {
    const { id, organizationId, startDate, endDate } = selectedRelationship;
  
    // Restrict adding duplicate active relationships only for new ones, exclude current relationship being edited
    const existingRelationship = relationships.find(
      (rel) => rel.organizationId === organizationId && !rel.endDate && rel.id !== id
    );
    if (existingRelationship) {
      showError("Cannot add duplicate active relationships.");
      return;
    }
  
    // Ensure start date is before or equal to the current date
    if (new Date(startDate) > new Date()) {
      showError("Start date cannot be in the future.");
      return;
    }

    // Ensure end date is after the start date
    if (endDate && new Date(endDate) <= new Date(startDate)) {
      showError("End date must be after the start date.");
      return;
    }
  
    const formattedStartDate = startDate ? formatDate(new Date(startDate)) : null;
    const formattedEndDate = endDate ? formatDate(new Date(endDate)) : null;
  
    const res = await resolve({
      method: id ? 'PUT' : 'POST',
      url: id ? `${relationshipBaseUrl}/${id}` : relationshipBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
      },
      data: { ...selectedRelationship, startDate: formattedStartDate, endDate: formattedEndDate },
    });
    
    if (res.error) return;
  
    await refreshRelationships();
    setSelectedRelationship(null);
    setRelationshipDialogOpen(false);
  }, [selectedRelationship, relationships, csrf, refreshRelationships]);

  const openCreateOrganizationDialog = useCallback(() => {
    setNewOrganization({ name: '', description: '', website: '' });
    setOrganizationDialogOpen(true);
  }, []);

  const saveOrganizationAndRelationship = useCallback(async () => {
    const { name, description, website } = newOrganization;
    if (!name || !description) {
      showError('Missing organization name or description');
      return;
    }

    const res = await resolve({
      method: 'POST',
      url: '/services/volunteer/organization',
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

    // Update the organization map with the new organization
    setOrganizationMap(prev => ({ ...prev, [createdOrg.id]: createdOrg }));

    // Set the relationship to reference the newly created organization by its name
    setSelectedRelationship({
      ...selectedRelationship,
      organizationId: createdOrg.id
    });

    setOrganizationDialogOpen(false);
    setRelationshipDialogOpen(true);
  }, [csrf, newOrganization, selectedRelationship]);

  const sortRelationships = useCallback(
    orgs => {
      orgs.sort((org1, org2) => {
        const v1 = profileMap[org1.memberId]?.name ?? '';
        const v2 = profileMap[org2.memberId]?.name ?? '';
        return sortAscending ? v1.localeCompare(v2) : v2.localeCompare(v1);
      });
    },
    [sortAscending, profileMap]
  );

  const sortIndicator = useCallback(
    column => {
      if (column !== sortColumn) return '';
      return sortAscending ? '🔼' : '🔽';
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

  const organizationOptions = Object.keys(organizationMap);
  if (selectHasVolunteeringOrganizationsPermission(state)) {
    organizationOptions.unshift('new');
  }

  return (
    <div id="volunteer-relationships">
      {/* Table for showing relationships */}
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
          <tbody>
            {relationships.map(relationship => (
              <tr key={relationship.id}>
                {!onlyMe && (
                  <td>{profileMap[relationship.memberId]?.name ?? ''}</td>
                )}
                <td>{organizationMap[relationship.organizationId]?.name ?? 'N/A'}</td>
                <td>{relationship.startDate}</td>
                <td>{relationship.endDate}</td>
                <td>
                  {(relationship.memberId == currentUser.id ||
                    selectHasVolunteeringRelationshipsPermission(state)) &&
                  <>
                  <Tooltip title="Edit">
                    <IconButton
                      aria-label="Edit"
                      onClick={() => {
                        setSelectedRelationship(relationship);
                        setRelationshipDialogOpen(true);
                      }}
                    >
                      <Edit />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Delete">
                    <IconButton
                      aria-label="Delete"
                      onClick={() => confirmDelete(relationship)}
                    >
                      <Delete />
                    </IconButton>
                  </Tooltip>
                  </>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {(onlyMe || selectHasVolunteeringRelationshipsPermission(state)) &&
        <IconButton
          aria-label="Add Volunteer Relationship"
          onClick={addRelationship}
        >
          <AddCircleOutline />
        </IconButton>}
      </div>

      {/* Message below the table */}
      <p className="warning">The administrator may edit organizations to ensure accuracy.</p>

      {/* Dialog for creating/editing a relationship */}
      <Dialog open={relationshipDialogOpen} onClose={cancelRelationship}>
        <DialogTitle>
          {selectedRelationship?.id ? 'Edit Relationship' : 'Add Relationship'}
        </DialogTitle>
        <DialogContent>
        <Autocomplete
          disableClearable
          getOptionLabel={(option) => 
            option === 'new' ? 'Create a New Organization' : organizationMap[option]?.name || option
          }
          options={organizationOptions}
          onChange={(event, value) => {
            if (value === 'new') {
              setRelationshipDialogOpen(false); // Close the relationship dialog
              openCreateOrganizationDialog(); // Open the organization creation dialog
            } else {
              setSelectedRelationship({ ...selectedRelationship, organizationId: value });
            }
          }}
          renderInput={(params) => (
            <TextField {...params} label="Organization" fullWidth />
          )}
          value={selectedRelationship?.organizationId || ''}
        />
          <DatePickerField
            date={selectedRelationship?.startDate || null}
            label="Start Date"
            setDate={date => setSelectedRelationship({ ...selectedRelationship, startDate: date })}
          />
          <DatePickerField
            date={selectedRelationship?.endDate}
            label="End Date"
            setDate={date => setSelectedRelationship({ ...selectedRelationship, endDate: date })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelRelationship}>Cancel</Button>
          <Button onClick={saveRelationship} disabled={!selectedRelationship?.organizationId}>
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog for creating a new organization */}
      <OrganizationDialog
        open={organizationDialogOpen}
        onClose={cancelOrganizationCreation}
        onSave={saveOrganizationAndRelationship}
        organization={newOrganization}
        setOrganization={setNewOrganization}
      />

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteRelationship(selectedRelationship)}
        question="Are you sure you want to delete this relationship?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Relationship"
      />
    </div>
  );
};

VolunteerRelationships.propTypes = propTypes;

export default VolunteerRelationships;
