import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit } from '@mui/icons-material';
import {
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
  const [newOrganization, setNewOrganization] = useState({ name: '', description: '', website: '' }); // Add new state for new organization
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Name');

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  // Load organizations from the server
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

  // Add organization handler
  const addOrganization = useCallback(() => {
    setNewOrganization({ name: '', description: '', website: '' }); // Reset the new organization form
    setOrganizationDialogOpen(true);
  }, []);

  // Cancel organization creation/edit
  const cancelOrganization = useCallback(() => {
    setSelectedOrganization(null);
    setOrganizationDialogOpen(false);
  }, []);

  // Confirm delete
  const confirmDelete = useCallback(organization => {
    setSelectedOrganization(organization);
    setConfirmDeleteOpen(true);
  }, []);

  // Delete organization handler
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

  // Edit organization handler
  const editOrganization = useCallback(org => {
    setSelectedOrganization(org);
    setOrganizationDialogOpen(true);
  }, []);

  // Handle form submission for adding or editing an organization
  const saveOrganization = useCallback(async () => {
    const { id, name, description, website } = selectedOrganization || newOrganization; // Use selected or new organization
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

    if (!id) {
      setOrganizations(prevOrgs => [...prevOrgs, newOrg]); // Add new organization to the list
    } else {
      const index = organizations.findIndex(org => org.id === id);
      organizations[index] = newOrg; // Update the edited organization
      setOrganizations([...organizations]);
    }
    setOrganizationDialogOpen(false); // Close dialog after saving
  }, [selectedOrganization, newOrganization, csrf]);

  // Render each organization row
  const organizationRow = useCallback(
    organization => (
      <tr key={organization.id}>
        <td>{organization.name}</td>
        <td>{organization.description}</td>
        <td>
          <a alt="website" href={organization.website} target="_blank" rel="noopener noreferrer">
            website
          </a>
        </td>
        {!onlyMe && (
          <td>
            <Tooltip title="Edit">
              <IconButton
                aria-label="Edit"
                onClick={() => editOrganization(organization)}
              >
                <Edit />
              </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
              <IconButton
                aria-label="Delete"
                onClick={() => confirmDelete(organization)}
              >
                <Delete />
              </IconButton>
            </Tooltip>
          </td>
        )}
      </tr>
    ),
    [editOrganization, confirmDelete, onlyMe]
  );

  // Organization table rendering
  const organizationsTable = useCallback(
    () => (
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
          onClick={addOrganization} // Open the dialog to add an organization
        >
          {console.log("Add Organization button rendered")}
          <AddCircleOutline />
        </IconButton>
      </div>
    ),
    [organizations, sortAscending, sortColumn, organizationRow]
  );

  // Sort organizations
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

  // Value sorting helper
  const organizationValue = useCallback(
    organization => {
      switch (sortColumn) {
        case 'Name':
          return organization.name;
        case 'Description':
          return organization.description;
        case 'Website':
          return organization.website || '';
        default:
          return '';
      }
    },
    [sortColumn]
  );

  return (
    <div id="organizations">
      {organizationsTable()}
      {/* Dialog for adding/editing an organization */}
      <Dialog
        open={organizationDialogOpen}
        onClose={cancelOrganization}
      >
        <DialogTitle>
          {selectedOrganization?.id ? 'Edit Organization' : 'Add Organization'}
        </DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            required
            fullWidth
            onChange={e =>
              selectedOrganization
                ? setSelectedOrganization({ ...selectedOrganization, name: e.target.value })
                : setNewOrganization({ ...newOrganization, name: e.target.value })
            }
            value={selectedOrganization?.name ?? newOrganization.name}
          />
          <TextField
            label="Description"
            required
            fullWidth
            onChange={e =>
              selectedOrganization
                ? setSelectedOrganization({ ...selectedOrganization, description: e.target.value })
                : setNewOrganization({ ...newOrganization, description: e.target.value })
            }
            value={selectedOrganization?.description ?? newOrganization.description}
          />
          <TextField
            label="Website URL"
            fullWidth
            onChange={e =>
              selectedOrganization
                ? setSelectedOrganization({ ...selectedOrganization, website: e.target.value })
                : setNewOrganization({ ...newOrganization, website: e.target.value })
            }
            value={selectedOrganization?.website ?? newOrganization.website}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelOrganization}>Cancel</Button>
          <Button
            disabled={!newOrganization.name || !newOrganization.description} // Ensure mandatory fields are filled
            onClick={saveOrganization}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>

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
