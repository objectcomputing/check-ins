import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

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
import Autocomplete, { createFilterOptions } from '@mui/material/Autocomplete';

import ConfirmationDialog from '../dialogs/ConfirmationDialog';

import './Certifications.css';

const certificationBaseUrl = 'http://localhost:3000/certification';

const propTypes = {
  forceUpdate: PropTypes.func
};

const Certifications = ({ forceUpdate = () => {} }) => {
  const [adding, setAdding] = useState(false); // true to add, false to edit
  const [badgeUrl, setBadgeUrl] = useState('');
  // const [color, setColor] = useState({ letter: '', name: '' });
  const [color, setColor] = useState('');
  const [colors, setColors] = useState([
    'blue',
    'green',
    'red'
    // { letter: 'b', label: 'blue' },
    // { letter: 'g', label: 'green' },
    // { letter: 'r', label: 'red' }
  ]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [certificationMap, setCertificationMap] = useState({});
  const [certifications, setCertifications] = useState([]);
  console.log('Certifications.jsx : certifications =', certifications);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [name, setName] = useState('');
  const [selectedCertification, setSelectedCertification] = useState(null);
  console.log(
    'Certifications.jsx : selectedCertification =',
    selectedCertification
  );
  const [selectedTarget, setSelectedTarget] = useState(null);

  const filter = createFilterOptions();

  const loadCertifications = useCallback(async () => {
    try {
      let res = await fetch(certificationBaseUrl);
      const certs = await res.json();
      setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

      //TODO: Just for debugging.
      setSelectedCertification(certs[0]);

      const certMap = certs.reduce((map, cert) => {
        map[cert.id] = cert;
        return map;
      }, {});
      setCertificationMap(certMap);
    } catch (err) {
      console.error(err);
    }
  }, []);

  useEffect(() => {
    loadCertifications();
  }, []);

  const addCertification = useCallback(name => {
    setAdding(true);
    setName(name);
    setDialogOpen(true);
  }, []);

  const cancelCertification = useCallback(() => {
    setName('');
    setDialogOpen(false);
  }, []);

  const certificationDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={dialogOpen}
        onClose={cancelCertification}
      >
        <DialogTitle>{adding ? 'Add' : 'Edit'} Certification</DialogTitle>
        <DialogContent>
          <TextField
            className="fullWidth"
            label="Certification Name"
            placeholder="Certification Name"
            required
            onChange={e => setName(e.target.value)}
            value={name}
          />
          <TextField
            className="fullWidth"
            label="Certification Badge URL"
            placeholder="Certification Badge URL"
            required
            onChange={e => setBadgeUrl(e.target.value)}
            value={badgeUrl}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelCertification}>Cancel</Button>
          <Button
            disabled={certifications.some(cert => cert.name === name)}
            onClick={saveCertification}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [dialogOpen, name]
  );

  const certificationSelect = useCallback(
    (label, selected, setSelected) => (
      <Autocomplete
        blurOnSelect
        clearOnBlur
        filterOptions={(options, params) => {
          console.log('Certifications.jsx filterOptions: options =', options);
          console.log('Certifications.jsx filterOptions: params =', params);
          const filtered = filter(options, params);
          console.log('Certifications.jsx filterOptions: filtered =', filtered);
          // const name = params.inputValue;
          // if (name !== '') {
          //   filtered.push({ name, displayLabel: `Add "${name}"` });
          // }
          return filtered;
        }}
        getOptionLabel={option => {
          console.log('Certifications.jsx getOptionLabel: option =', option);
          return option.displayLabel || '';
        }}
        handleHomeEndKeys
        isOptionEqualToValue={(option, value) => {
          const equal = option.id === value?.id;
          if (equal) {
            console.log(
              'Certifications.jsx isOptionEqualToValue: matched on',
              option.name
            );
          }
          return equal;
        }}
        key={label}
        onChange={(event, cert) => {
          if (cert === null) return;
          const nameUp = cert.name.toUpperCase();
          const inCertList = certifications.some(
            cert => cert.name.toUpperCase() === nameUp
          );
          console.log('Certifications.jsx onChange: inCertList =', inCertList);
          if (inCertList) {
            setSelected(cert);
          } else {
            setSelected({ name: cert.name, badgeImageUrl: '' });
            setName(cert.name);
            setDialogOpen(true);
          }
        }}
        options={certifications}
        sx={{ width: 400 }}
        renderInput={params => {
          console.log('Certifications.jsx renderInput: params =', params);
          return (
            <TextField
              {...params}
              className="fullWidth"
              label={label}
              placeholder="Enter a certification name"
              value={selected?.name ?? ''}
              variant="standard"
            />
          );
        }}
        renderOption={(props, option) => {
          // React keys must be passed directly to JSX without using spread.
          delete props.key;
          return (
            <li
              key={option.id}
              style={{ backgroundColor: 'lightgray' }}
              {...props}
            >
              {option.name}
            </li>
          );
        }}
        selectOnFocus
        value={selected}
      />
    ),
    [certifications]
  );

  const deleteCertification = useCallback(async () => {
    const { id, name } = selectedCertification;
    //TODO: What should we do if the certification has some earned certifications?
    const url = certificationBaseUrl + '/' + id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      if (!res.ok) {
        throw new Error(`Failed to delete certification ${name}`);
      }
      setCertificationMap(map => {
        delete map[id];
        return map;
      });
      setCertifications(certs => certs.filter(c => c.id !== id));
      setSelectedCertification(null);
      forceUpdate();
    } catch (err) {
      console.error(err);
    }
  }, [certificationMap, certifications, selectedCertification]);

  const editCertification = useCallback(() => {
    setAdding(false);
    setName(selectedCertification.name);
    setDialogOpen(true);
  }, [selectedCertification]);

  const mergeCertification = useCallback(async () => {
    const url = certificationBaseUrl + '/merge';
    const sourceId = selectedCertification.id;
    const targetId = selectedTarget.id;
    try {
      const res = await fetch(url, {
        method: 'POST',
        body: JSON.stringify({ sourceId, targetId })
      });
      setCertifications(certs => certs.filter(cert => cert.id !== sourceId));
      setCertificationMap(map => {
        delete map[sourceId];
        return map;
      });
      alert(
        `Successfully merged ${selectedCertification.name} certification to ${selectedTarget.name}.`
      );
      setSelectedCertification(null);
      forceUpdate();
    } catch (err) {
      console.error(err);
    }
    setDialogOpen(false);
  }, [selectedCertification, selectedTarget]);

  const renameCertification = useCallback(async () => {
    const url = certificationBaseUrl + '/' + selectedCertification.id;
    selectedCertification.name = selectedTarget.name;
    try {
      const res = await fetch(url, {
        method: 'PUT',
        body: JSON.stringify(selectedCertification)
      });
      alert(
        `Successfully renamed ${selectedCertification.name} certification to ${selectedTarget.name}.`
      );
      forceUpdate();
    } catch (err) {
      console.error(err);
    }
    setDialogOpen(false);
  }, [selectedCertification, selectedTarget]);

  const saveCertification = useCallback(
    async create => {
      const url = adding
        ? certificationBaseUrl
        : certificationBaseUrl + '/' + selectedCertification.id;
      try {
        const res = await fetch(url, {
          method: adding ? 'POST' : 'PUT',
          body: JSON.stringify({ name })
        });
        const newCert = await res.json();
        setCertifications(certs =>
          [...certs, newCert].sort((c1, c2) => c1.name.localeCompare(c2.name))
        );
        setCertificationMap(map => {
          map[newCert.id] = newCert;
          return map;
        });
        setSelectedCertification(newCert);
        setName('');
        forceUpdate();
      } catch (err) {
        console.error(err);
      }
      setDialogOpen(false);
    },
    [certificationMap, certifications, name, selectedCertification]
  );

  return (
    <div>
      <Autocomplete
        clearOnBlur
        freeSolo
        // getOptionLabel={option => option.name || ''}
        handleHomeEndKeys
        // isOptionEqualToValue={(option, value) =>
        //   value ? value.name === option.name : false
        // }
        onChange={(event, value) => {
          setColor(value);
          if (value && !colors.includes(value)) {
            // colors.push({ letter: value[0], name: value });
            colors.push(value);
            setColors(colors.sort());
          }
        }}
        options={colors}
        // Why is the renderInput prop required?
        renderInput={params => <TextField {...params} label="Color" />}
        selectOnFocus
      />
      <div>You selected {color || 'nothing'}.</div>

      {/*
      {certificationSelect(
        'Source Certification',
        selectedCertification,
        setSelectedCertification
      )}

      <IconButton
        aria-label="Add Certification"
        classes={{ root: 'add-button' }}
        onClick={() => addCertification('')}
      >
        <AddCircleOutline />
      </IconButton>

      {selectedCertification && (
        <>
          <Tooltip title="Edit">
            <IconButton aria-label="Edit" onClick={editCertification}>
              <Edit />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              aria-label="Delete"
              onClick={() => setConfirmDeleteOpen(true)}
            >
              <Delete />
            </IconButton>
          </Tooltip>
        </>
      )}

      {certificationSelect(
        'Target Certification',
        selectedTarget,
        setSelectedTarget
      )}
      <Button
        disabled={!selectedCertification || !selectedTarget}
        onClick={renameCertification}
      >
        Rename Source to Target
      </Button>
      <Button
        disabled={!selectedCertification || !selectedTarget}
        onClick={mergeCertification}
      >
        Merge Source to Target
      </Button>

      {certificationDialog()}

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={deleteCertification}
        question={`Are you sure you want to delete the ${selectedCertification?.name} certification?`}
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />
      */}
    </div>
  );
};

Certifications.propTypes = propTypes;

export default Certifications;
