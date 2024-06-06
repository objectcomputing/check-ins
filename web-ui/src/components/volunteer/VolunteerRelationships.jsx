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
import './VolunteerRelationships.css';

const organizationBaseUrl = 'http://localhost:3000/organization';
const relationshipBaseUrl = 'http://localhost:3000/volunteer-relationship';

const formatDate = date => {
  return !date
    ? ''
    : date instanceof Date
      ? format(date, 'yyyy-MM-dd')
      : `${date.$y}-${date.$M + 1}-${date.$D}`;
};

const tableColumns = [
  'Member',
  'Organization',
  'Website',
  'Start Date',
  'End Date'
];

const propTypes = { forceUpdate: PropTypes.func };

const VolunteerRelationships = ({ forceUpdate = () => {}, onlyMe = false }) => {
  const { state } = useContext(AppContext);
  const [badgeUrl, setBadgeUrl] = useState('');
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [organizationMap, setOrganizationMap] = useState({});
  const [relationshipDialogOpen, setRelationshipDialogOpen] = useState(false);
  const [relationshipMap, setRelationshipMap] = useState({});
  const [relationships, setRelationships] = useState([]);
  const [selectedRelationship, setSelectedRelationship] = useState(null);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Member');

  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

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
      relationships.sort((rel1, rel2) => {
        const org1 = organizationMap[rel1.organizationId];
        const org2 = organizationMap[rel2.organizationId];
        const name1 = org1.name;
        const name2 = org2.name;
        return name1.localeCompare(name2);
      });
      setRelationships(relationships);
    } catch (err) {
      console.error(err);
    }
  }, [organizationMap]);

  useEffect(() => {
    loadOrganizations();
  }, []);

  useEffect(() => {
    if (Object.keys(organizationMap).length > 0) loadRelationships();
  }, [organizationMap]);

  const addRelationship = useCallback(() => {
    setSelectedRelationship({ name: '', description: '', website: '' });
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
    const url = relationshipBaseUrl + '/' + relationship.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setRelationships(orgs => orgs.filter(org => org.id !== relationship.id));
    } catch (err) {
      console.error(err);
    }
  }, []);

  const relationshipRow = useCallback(
    relationship => {
      const org = organizationMap[relationship.organizationId];
      return (
        <tr key={relationship.id}>
          <td>{profileMap[relationship.memberId].name}</td>
          <td>{org.name}</td>
          <td>
            <a alt="website" href={org.website} target="_blank">
              website
            </a>
          </td>
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
    [organizationMap, profileMap]
  );

  const relationshipDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'relationship-dialog' }}
        open={relationshipDialogOpen}
        onClose={cancelRelationship}
      >
        <DialogTitle>
          {selectedRelationship?.id ? 'Edit' : 'Add'} Relationship
        </DialogTitle>
        <DialogContent>
          <TextField
            className="fullWidth"
            label="Name"
            required
            onChange={e =>
              setSelectedRelationship({
                ...selectedRelationship,
                name: e.target.value
              })
            }
            value={selectedRelationship?.name ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Description"
            required
            onChange={e =>
              setSelectedRelationship({
                ...selectedRelationship,
                description: e.target.value
              })
            }
            value={selectedRelationship?.description ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Website URL"
            onChange={e =>
              setSelectedRelationship({
                ...selectedRelationship,
                website: e.target.value
              })
            }
            value={selectedRelationship?.website ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelRelationship}>Cancel</Button>
          <Button
            disabled={
              !selectedRelationship?.name || !selectedRelationship?.description
            }
            onClick={saveRelationship}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [relationshipDialogOpen, selectedRelationship]
  );

  const tableColumnsToUse = onlyMe ? tableColumns.slice(1) : tableColumns;

  const relationshipsTable = useCallback(
    () => (
      <Card>
        <CardHeader
          avatar={<FoodBank />}
          title="Relationships"
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
                  <th className="actions-th" key="actions">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody>{relationships.map(relationshipRow)}</tbody>
            </table>
            <IconButton
              aria-label="Add Relationship"
              classes={{ root: 'add-button' }}
              onClick={addRelationship}
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
        case 'Name':
          return relationship.name;
        case 'Description':
          return relationship.description;
        case 'Website':
          return relationship.website || '';
      }
    },
    [relationshipMap, sortColumn]
  );

  const editRelationship = useCallback(
    org => {
      setSelectedRelationship(org);
      setRelationshipDialogOpen(true);
    },
    [relationshipMap]
  );

  const saveRelationship = useCallback(async () => {
    const { id } = selectedRelationship;
    const url = id ? `${relationshipBaseUrl}/${id}` : relationshipBaseUrl;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedRelationship)
      });
      const newOrg = await res.json();
      setRelationships(orgs => {
        if (id) {
          const index = orgs.findIndex(org => org.id === id);
          orgs[index] = newOrg;
        } else {
          orgs.push(newOrg);
        }
        return [...orgs];
      });
      setSelectedRelationship(null);
    } catch (err) {
      console.error(err);
    }
    setRelationshipDialogOpen(false);
  }, [selectedRelationship]);

  const sortRelationships = useCallback(
    orgs => {
      orgs.sort((org1, org2) => {
        const v1 = relationshipValue(org1);
        const v2 = relationshipValue(org2);
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
    <div id="volunteer-relationships">
      {relationshipsTable()}

      {relationshipDialog()}

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
