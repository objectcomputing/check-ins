import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit, FoodBank } from '@mui/icons-material';
import {
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

import { resolve } from '../../api/api.js';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';

const organizationBaseUrl = '/services/volunteer/organization';

const sortableTableColumns = ['Name', 'Description'];

const propTypes = { onlyMe: PropTypes.bool };

const Organizations = ({ onlyMe = false }) => {
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false);
  const [organizations, setOrganizations] = useState([]);
  const [selectedOrganization, setSelectedOrganization] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Name');

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

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
    setOrganizations(
      organizations.sort((org1, org2) => org1.name.localeCompare(org2.name))
    );
  }, [csrf]);

  useEffect(() => {
    if (csrf) loadOrganizations();
  }, [csrf]);

  useEffect(() => {
    sortOrganizations(organizations);
    setOrganizations([...organizations]);
  }, [sortAscending, sortColumn]);

  const addOrganization = useCallback(() => {
    setSelectedOrganization({ name: '', description: '', website: '' });
    setOrganizationDialogOpen(true);
  }, []);

  const cancelOrganization = useCallback(() => {
    setSelectedOrganization(null);
    setOrganizationDialogOpen(false);
  }, []);

  const confirmDelete = useCallback(organization => {
    setSelectedOrganization(organization);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteOrganization = useCallback(async organization => {
    organization.active = false;
    const res = await resolve({
      method: 'PUT',
      url: organizationBaseUrl + '/' + organization.id,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: organization
    });
    if (res.error) return;

    setOrganizations(orgs => orgs.filter(org => org.id !== organization.id));
  }, []);

  const editOrganization = useCallback(org => {
    setSelectedOrganization(org);
    setOrganizationDialogOpen(true);
  }, []);

  const organizationRow = useCallback(
    organization => (
      <tr key={organization.id}>
        <td>{organization.name}</td>
        <td>{organization.description}</td>
        <td>
          <a alt="website" href={organization.website} target="_blank">
            website
          </a>
        </td>
        {!onlyMe && (
          <td>
            <Tooltip title="Edit">
              <IconButton
                aria-label="Edit"
                onClick={() => editOrganization(organization)}
                style={{ color: 'black' }} // Default for light mode
              >
                <Edit />
              </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
              <IconButton
                aria-label="Delete"
                onClick={() => confirmDelete(organization)}
                style={{ color: 'black' }} // Default for light mode
              >
                <Delete />
              </IconButton>
            </Tooltip>
          </td>
        )}
      </tr>
    ),
    []
  );

  const organizationDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'volunteer-dialog' }}
        open={organizationDialogOpen}
        onClose={cancelOrganization}
      >
        <DialogTitle>
          {selectedOrganization?.id ? 'Edit' : 'Add'} Organization
        </DialogTitle>
        <DialogContent>
          <TextField
            className="fullWidth"
            label="Name"
            required
            onChange={e =>
              setSelectedOrganization({
                ...selectedOrganization,
                name: e.target.value
              })
            }
            value={selectedOrganization?.name ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Description"
            required
            onChange={e =>
              setSelectedOrganization({
                ...selectedOrganization,
                description: e.target.value
              })
            }
            value={selectedOrganization?.description ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Website URL"
            onChange={e =>
              setSelectedOrganization({
                ...selectedOrganization,
                website: e.target.value
              })
            }
            value={selectedOrganization?.website ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelOrganization}>Cancel</Button>
          <Button
            disabled={
              !selectedOrganization?.name || !selectedOrganization?.description
            }
            onClick={saveOrganization}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [organizationDialogOpen, selectedOrganization]
  );

  const organizationsTable = useCallback(
    () => (
      <Card>
        <CardHeader
          avatar={<FoodBank />}
          title="Organizations"
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
                  <th key="website">Website</th>
                  {!onlyMe && (
                    <th className="actions-th" key="actions">
                      Actions
                    </th>
                  )}
                </tr>
              </thead>
              <tbody>{organizations.map(organizationRow)}</tbody>
            </table>
            <IconButton
              aria-label="Add Organization"
              classes={{ root: 'add-button' }}
              onClick={addOrganization}
            >
              <AddCircleOutline />
            </IconButton>
          </div>
          {onlyMe && (
            <p className="warning">
              The administrator will edit and delete organizations to ensure accuracy.
            </p>
          )}
        </CardContent>
      </Card>
    ),
    [organizations, sortAscending, sortColumn]
  );

  const organizationValue = useCallback(
    organization => {
      switch (sortColumn) {
        case 'Name':
          return organization.name;
        case 'Description':
          return organization.description;
        case 'Website':
          return organization.website || '';
      }
    },
    [sortColumn]
  );

  const saveOrganization = useCallback(async () => {
    const { id, name, description, website } = selectedOrganization;
    const url = id ? `${organizationBaseUrl}/${id}` : organizationBaseUrl;

    const res = await resolve({
      method: id ? 'PUT' : 'POST',
      url,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: { name, description, website }
    });

    if (res.error) return;

    const newOrg = res.payload.data;

    // Add the organization to both global and user's list
    if (!id) {
      organizations.push(newOrg);
    } else {
      const index = organizations.findIndex(org => org.id === id);
      organizations[index] = newOrg;
    }
    setOrganizations([...organizations]);
    setOrganizationDialogOpen(false);
  }, [selectedOrganization]);

  const sortOrganizations = useCallback(
    orgs => {
      orgs.sort((org1, org2) => {
        const v1 = organizationValue(org1);
        const v2 = organizationValue(org2);
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

  return (
    <div id="organizations">
      {organizationsTable()}

      {organizationDialog()}

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteOrganization(selectedOrganization)}
        question="Are you sure you want to delete this organization?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Organization"
      />
    </div>
  );
};

Organizations.propTypes = propTypes;

export default Organizations;