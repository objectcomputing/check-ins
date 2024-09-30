import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit, Handshake } from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  Card,
  CardContent,
  CardHeader,
  IconButton,
  TextField,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle
} from '@mui/material';

import { resolve } from '../../api/api';
import { createNewOrganization } from '../../api/volunteer';  // Importing the new API function
import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import OrganizationDialog from '../dialogs/OrganizationDialog';  // Importing the new reusable component
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfileMap
} from '../../context/selectors';
import { formatDate } from '../../helpers/datetime';

const relationshipBaseUrl = '/services/volunteer/relationship';

const propTypes = { forceUpdate: PropTypes.func, onlyMe: PropTypes.bool };

const VolunteerRelationships = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [organizationMap, setOrganizationMap] = useState({});
  const [organizations, setOrganizations] = useState([]);
  const [relationshipDialogOpen, setRelationshipDialogOpen] = useState(false);
  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false); // New dialog for adding an organization
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
  const profiles = Object.values(profileMap).filter(profile => profile && profile.name); // Filter out undefined profiles
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const sortableTableColumns = ['Organization', 'Start Date', 'End Date'];
  if (!onlyMe) sortableTableColumns.unshift('Member');

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
  }, []);

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
  }, [organizationMap]);

  useEffect(() => {
    loadOrganizations();
  }, []);

  useEffect(() => {
    if (Object.keys(organizationMap).length > 0) loadRelationships();
  }, [organizationMap]);

  useEffect(() => {
    sortRelationships(relationships);
    setRelationships([...relationships]);
  }, [sortAscending, sortColumn]);

  const addRelationship = useCallback(() => {
    setSelectedRelationship({
      memberId: onlyMe ? currentUser.id : '',
      organizationId: '',
      startDate: '',
      endDate: ''
    });
    setRelationshipDialogOpen(true);
  }, []);

  const cancelRelationship = useCallback(() => {
    setSelectedRelationship(null);
    setRelationshipDialogOpen(false);
  }, []);

  const confirmDelete = useCallback(relationship => {
    setSelectedRelationship(relationship);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteRelationship = useCallback(async relationship => {
    if (!relationship) return;
    relationship.active = false;
    const res = await resolve({
      method: 'PUT',
      url: relationshipBaseUrl + '/' + relationship.id,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: relationship
    });
    if (res.error) return;

    setRelationships(orgs => orgs.filter(org => org.id !== relationship.id));
  }, []);

  const editRelationship = useCallback(
    relationship => {
      setSelectedRelationship(relationship);
      setRelationshipDialogOpen(true);
    },
    [relationshipMap]
  );

  const getDate = dateString => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-');
    return new Date(Number(year), Number(month) - 1, Number(day));
  };

  const handleCreateNewOrganization = async () => {
    if (!newOrganization.name || !newOrganization.description) {
      console.error('Organization name and description are required.');
      return;
    }

    const res = await createNewOrganization(csrf, newOrganization);  // Call the API function

    if (res.error) {
      console.error('Error saving new organization', res.error);
      return;
    }

    const newOrg = res.payload.data;

    if (newOrg && newOrg.name) {
      setOrganizations([...organizations, newOrg]); // Add new organization to the list
      setOrganizationMap({ ...organizationMap, [newOrg.id]: newOrg });
      setSelectedRelationship({
        ...selectedRelationship,
        organizationId: newOrg.id
      });
      setOrganizationDialogOpen(false); // Close the organization creation dialog
    } else {
      console.error('Invalid organization data received');
    }
  };

  const relationshipRow = useCallback(
    relationship => {
      const org = organizationMap[relationship.organizationId];
      if (!org) {
        console.error('Organization not found for relationship:', relationship);
        return null;
      }
      return (
        <tr key={relationship.id}>
          {!onlyMe && <td>{profileMap[relationship.memberId]?.name ?? ''}</td>}
          <td>{org.name ?? 'N/A'}</td> {/* Defensive check */}
          <td>{relationship.startDate}</td>
          <td>{relationship.endDate}</td>
          <td>
            <Tooltip title="Edit">
              <IconButton
                aria-label="Edit"
                onClick={() => editRelationship(relationship)}
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
          </td>
        </tr>
      );
    },
    [organizationMap, profileMap, editRelationship, confirmDelete, onlyMe]
  );

  const relationshipDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'volunteer-dialog' }}
        open={relationshipDialogOpen}
        onClose={cancelRelationship}
      >
        <DialogTitle>
          {selectedRelationship?.id ? 'Edit' : 'Add'} Relationship
        </DialogTitle>
        <DialogContent>
          {!onlyMe && (
            <Autocomplete
              disableClearable
              getOptionLabel={profile => profile?.name ?? ''}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              onChange={(event, profile) => {
                setSelectedRelationship({
                  ...selectedRelationship,
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
                selectedRelationship?.memberId
                  ? profileMap[selectedRelationship.memberId]
                  : null
              }
            />
          )}
          <Autocomplete
            disableClearable
            getOptionLabel={organization => organization?.name ?? ''}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, organization) => {
              if (organization && organization.inputValue === 'Add new organization') {
                setOrganizationDialogOpen(true); // Open the dialog to create a new organization
              } else {
                setSelectedRelationship({
                  ...selectedRelationship,
                  organizationId: organization.id
                });
              }
            }}
            options={[...organizations, { name: 'Add new organization', inputValue: 'Add new organization' }]}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Organization"
              />
            )}
            value={
              selectedRelationship?.organizationId
                ? organizationMap[selectedRelationship.organizationId]
                : null
            }
          />
          <DatePickerField
            date={getDate(selectedRelationship?.startDate)}
            label="Start Date"
            setDate={date => {
              const startDate = formatDate(date);
              let { endDate } = selectedRelationship;
              if (endDate && startDate > endDate) endDate = startDate;
              setSelectedRelationship({
                ...selectedRelationship,
                startDate,
                endDate
              });
            }}
          />
          <DatePickerField
            date={getDate(selectedRelationship?.endDate)}
            label="End Date"
            setDate={date => {
              const endDate = date ? formatDate(date) : '';
              let { startDate } = selectedRelationship;
              if (endDate && (!startDate || endDate < startDate)) {
                startDate = endDate;
              }
              setSelectedRelationship({
                ...selectedRelationship,
                startDate,
                endDate
              });
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelRelationship}>Cancel</Button>
          <Button disabled={!validRelationship()} onClick={saveRelationship}>
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [relationshipDialogOpen, selectedRelationship, onlyMe, profiles, profileMap, organizationMap, organizations]
  );

  const relationshipsTable = useCallback(
    () => (
      <Card>
        <CardHeader
          avatar={<Handshake />}
          title={onlyMe ? 'Organizations' : 'Relationships'}
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
              <tbody>{relationships.map(relationshipRow)}</tbody>
            </table>
            <IconButton
              aria-label="Add Volunteer Relationship"
              classes={{ root: 'add-button' }}
              onClick={addRelationship}
            >
              <AddCircleOutline />
            </IconButton>
          </div>
        </CardContent>
      </Card>
    ),
    [relationships, sortAscending, sortColumn, relationshipRow]
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

  const saveRelationship = useCallback(async () => {
    const { id } = selectedRelationship;
    const res = await resolve({
      method: id ? 'PUT' : 'POST',
      url: id ? `${relationshipBaseUrl}/${id}` : relationshipBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: selectedRelationship
    });
    if (res.error) {
      console.error("Error saving relationship", res.error);
      return;
    }

    const newRel = res.payload.data;

    if (id) {
      const index = relationships.findIndex(rel => rel.id === id);
      relationships[index] = newRel;
    } else {
      relationships.push(newRel);
    }
    sortRelationships(relationships);
    setRelationships(relationships);

    setSelectedRelationship(null);
    setRelationshipDialogOpen(false);
  }, [selectedRelationship]);

  const sortRelationships = useCallback(
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

  const validRelationship = useCallback(() => {
    const rel = selectedRelationship;
    return rel?.memberId && rel?.organizationId;
  });

  return (
    <div id="volunteer-relationships">
      {relationshipsTable()}

      {relationshipDialog()}

      <OrganizationDialog
        open={organizationDialogOpen}
        onClose={() => setOrganizationDialogOpen(false)}
        onSave={handleCreateNewOrganization}
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