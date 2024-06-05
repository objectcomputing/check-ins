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
import './Organizations.css';

const organizationBaseUrl = 'http://localhost:3000/organization';

const formatDate = date => {
  return !date
    ? ''
    : date instanceof Date
      ? format(date, 'yyyy-MM-dd')
      : `${date.$y}-${date.$M + 1}-${date.$D}`;
};

const tableColumns = ['Name', 'Description', 'Website'];

const propTypes = { forceUpdate: PropTypes.func };

const Organizations = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const { state } = useContext(AppContext);
  const [badgeUrl, setBadgeUrl] = useState('');
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false);
  const [organizationMap, setOrganizationMap] = useState({});
  const [organizations, setOrganizations] = useState([]);
  const [selectedOrganization, setSelectedOrganization] = useState(null);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Member');

  const loadOrganizations = useCallback(async () => {
    try {
      let res = await fetch(organizationBaseUrl);
      const organizations = await res.json();
      setOrganizations(
        organizations.sort((org1, org2) => org1.name.localeCompare(org2.name))
      );
    } catch (err) {
      console.error(err);
    }
  }, []);

  useEffect(() => {
    loadOrganizations();
  }, []);

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
    const url = organizationBaseUrl + '/' + organization.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setOrganizations(orgs => orgs.filter(org => org.id !== organization.id));
    } catch (err) {
      console.error(err);
    }
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
      </tr>
    ),
    [organizationMap]
  );

  const organizationDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'organization-dialog' }}
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

  const tableColumnsToUse = onlyMe ? tableColumns.slice(1) : tableColumns;

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
                  {tableColumnsToUse.map(column => (
                    <th
                      key={column}
                      onClick={() => sortTable(column)}
                      style={{ cursor: 'pointer' }}
                    >
                      {column}
                      {sortIndicator(column)}
                    </th>
                  ))}
                  <th key="Actions">Actions</th>
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
    [organizationMap, sortColumn]
  );

  const editOrganization = useCallback(
    org => {
      setSelectedOrganization(org);
      setOrganizationDialogOpen(true);
    },
    [organizationMap]
  );

  const saveOrganization = useCallback(async () => {
    const { id } = selectedOrganization;
    const url = id ? `${organizationBaseUrl}/${id}` : organizationBaseUrl;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedOrganization)
      });
      const newOrg = await res.json();
      setOrganizations(orgs => {
        if (id) {
          const index = orgs.findIndex(org => org.id === id);
          orgs[index] = newOrg;
        } else {
          orgs.push(newOrg);
        }
        return [...orgs];
      });
      setSelectedOrganization(null);
    } catch (err) {
      console.error(err);
    }
    setOrganizationDialogOpen(false);
  }, [selectedOrganization]);

  const sortOrganizations = useCallback(
    orgs => {
      orgs.sort((org1, org2) => {
        const v1 = organizationValue(org1);
        const v2 = organizationValue(org2);
        const compare = sortAscending
          ? v1.localeCompare(v2)
          : v2.localeCompare(v1);
        // console.log('v1 =', v1, 'v2 =', v2, 'compare =', compare);
        return compare;
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
