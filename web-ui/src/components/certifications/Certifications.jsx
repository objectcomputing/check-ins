import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  TextField
} from '@mui/material';
import Autocomplete, { createFilterOptions } from '@mui/material/Autocomplete';

import ConfirmationDialog from '../dialogs/ConfirmationDialog';

import './Certifications.css';

const certificationBaseUrl = 'http://localhost:3000/certification';

const propTypes = {
  forceUpdate: PropTypes.func,
  open: PropTypes.bool,
  onClose: PropTypes.func
};

const Certifications = ({ forceUpdate = () => {}, open, onClose }) => {
  const [adding, setAdding] = useState(true); // true to add, false to edit
  const [badgeUrl, setBadgeUrl] = useState('');
  const [certificationMap, setCertificationMap] = useState({});
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [mergeDialogOpen, setMergeDialogOpen] = useState(false);
  const [name, setName] = useState('');
  const [selectedCertification, setSelectedCertification] = useState(null);
  const [selectedTarget, setSelectedTarget] = useState(null);

  const close = () => {
    reset();
    onClose();
  };

  const reset = () => {
    setAdding(true);
    setBadgeUrl('');
    setName('');
    setSelectedCertification(null);
    setSelectedTarget(null);
  };

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

  const certificationSelect = useCallback(
    (label, selected, setSelected) => (
      <Autocomplete
        // blurOnSelect
        clearOnBlur
        freeSolo
        handleHomeEndKeys
        key={label}
        onChange={(event, value) => {
          if (value === null) return;
          let foundCert = certifications.find(
            cert => cert.name.toUpperCase() === value.toUpperCase()
          );
          if (!foundCert) {
            foundCert = { badgeUrl: '', name: value };
            setCertifications(certs => [...certs, foundCert]);
          }
          setAdding(!Boolean(foundCert));
          setName(foundCert.name);
          setBadgeUrl(foundCert.badgeUrl);
          setSelected(foundCert);
        }}
        options={certifications.map(cert => cert.name)}
        renderInput={params => {
          return (
            <TextField
              {...params}
              label={label}
              placeholder="Enter a certification name"
              variant="standard"
            />
          );
        }}
        selectOnFocus
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
      forceUpdate();
      close();
    } catch (err) {
      console.error(err);
    }
  }, [certificationMap, certifications, selectedCertification]);

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
      forceUpdate();
      close();
    } catch (err) {
      console.error(err);
    }
  }, [selectedCertification, selectedTarget]);

  const saveCertification = useCallback(async () => {
    const url = adding
      ? certificationBaseUrl
      : certificationBaseUrl + '/' + selectedCertification.id;
    try {
      const res = await fetch(url, {
        method: adding ? 'POST' : 'PUT',
        body: JSON.stringify({ name, badgeUrl })
      });
      const newCert = await res.json();
      certificationMap[newCert.id] = newCert;
      setCertificationMap(certificationMap);
      setCertifications(
        Object.values(certificationMap).sort((c1, c2) =>
          c1.name.localeCompare(c2.name)
        )
      );
      close();
      forceUpdate();
    } catch (err) {
      console.error(err);
    }
  }, [badgeUrl, certificationMap, name, selectedCertification]);

  return (
    <>
      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={open}
        onClose={close}
      >
        <DialogTitle>Manage Certifications</DialogTitle>
        <DialogContent>
          {certificationSelect(
            'Certification',
            selectedCertification,
            setSelectedCertification
          )}
          <TextField
            label="Certification Name"
            required
            onChange={e => setName(e.target.value)}
            value={name}
          />
          <TextField
            label="Badge URL"
            required
            onChange={e => setBadgeUrl(e.target.value)}
            value={badgeUrl}
          />

          <div className="row">
            <Button disabled={!name || !badgeUrl} onClick={saveCertification}>
              Save
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setConfirmDeleteOpen(true)}
            >
              Delete
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setMergeDialogOpen(true)}
            >
              Merge
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={deleteCertification}
        question={`Are you sure you want to delete the ${selectedCertification?.name} certification?`}
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />

      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={mergeDialogOpen}
        onClose={() => setMergeDialogOpen(false)}
      >
        <DialogTitle>
          Merge {selectedCertification?.name} Certification Into
        </DialogTitle>
        <DialogContent>
          {certificationSelect(
            'Target Certification',
            selectedTarget,
            setSelectedTarget
          )}
          <div className="row">
            <Button disabled={!selectedTarget} onClick={mergeCertification}>
              Merge
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setMergeDialogOpen(false)}
            >
              Cancel
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

Certifications.propTypes = propTypes;

export default Certifications;
