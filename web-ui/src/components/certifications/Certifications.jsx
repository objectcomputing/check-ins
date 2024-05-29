import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

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

import ConfirmationDialog from '../dialogs/ConfirmationDialog';

import './Certifications.css';

const certificationBaseUrl = 'http://localhost:3000/certification';

const propTypes = {
  forceUpdate: PropTypes.func
};

const Certifications = ({ forceUpdate = () => {} }) => {
  const [adding, setAdding] = useState(false); // true to add, false to edit
  const [certificationDialogOpen, setCertificationDialogOpen] = useState(false);
  const [certificationMap, setCertificationMap] = useState({});
  const [certificationName, setCertificationName] = useState('');
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [selectedCertification, setSelectedCertification] = useState(null);
  const [selectedTarget, setSelectedTarget] = useState(null);

  const loadCertifications = useCallback(async () => {
    try {
      let res = await fetch(certificationBaseUrl);
      const certs = await res.json();
      setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

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

  const addCertification = useCallback(() => {
    setAdding(true);
    setCertificationName('');
    setCertificationDialogOpen(true);
  }, []);

  const cancelCertification = useCallback(() => {
    setCertificationName('');
    setCertificationDialogOpen(false);
  }, []);

  const certificationDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={certificationDialogOpen}
        onClose={cancelCertification}
      >
        <DialogTitle>{adding ? 'Add' : 'Edit'} Certification</DialogTitle>
        <DialogContent>
          <TextField
            className="fullWidth"
            label="Certification Name"
            placeholder="Certification Name"
            required
            onChange={e => setCertificationName(e.target.value)}
            value={certificationName}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelCertification}>Cancel</Button>
          <Button
            disabled={certifications.some(
              cert => cert.name === certificationName
            )}
            onClick={saveCertification}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [certificationDialogOpen, certificationName]
  );

  const certificationSelect = useCallback(
    (label, selected, setSelected) => (
      <Autocomplete
        disableClearable
        getOptionLabel={cert => cert?.name || 'unknown'}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        onChange={(event, cert) => {
          setSelected({ ...cert });
        }}
        options={certifications}
        sx={{ width: 400 }}
        renderInput={params => (
          <TextField {...params} className="fullWidth" label={label} />
        )}
        renderOption={(props, option) => {
          // React won't allow a key to be present in spread props.
          const { key } = props;
          delete props.key;
          return (
            <span {...props} key={key} style={{ backgroundColor: 'lightgray' }}>
              {option.name}
            </span>
          );
        }}
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
    setCertificationName(selectedCertification.name);
    setCertificationDialogOpen(true);
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
    setCertificationDialogOpen(false);
  }, [selectedCertification, selectedTarget]);

  const saveCertification = useCallback(
    async create => {
      const url = adding
        ? certificationBaseUrl
        : certificationBaseUrl + '/' + selectedCertification.id;
      try {
        const res = await fetch(url, {
          method: adding ? 'POST' : 'PUT',
          body: JSON.stringify({ name: certificationName })
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
        setCertificationName('');
        forceUpdate();
      } catch (err) {
        console.error(err);
      }
      setCertificationDialogOpen(false);
    },
    [certificationMap, certificationName, certifications, selectedCertification]
  );

  return (
    <div>
      {certificationSelect(
        'Source Certification',
        selectedCertification,
        setSelectedCertification
      )}

      <IconButton
        aria-label="Add Certification"
        classes={{ root: 'add-button' }}
        onClick={addCertification}
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
    </div>
  );
};

Certifications.propTypes = propTypes;

export default Certifications;
