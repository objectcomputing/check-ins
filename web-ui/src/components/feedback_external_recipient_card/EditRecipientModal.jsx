import React, { useState, useEffect } from 'react';
import { Box, Typography, TextField, Button, Modal, Select, MenuItem } from '@mui/material';
import PropTypes from 'prop-types';

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    bgcolor: 'background.paper',
    boxShadow: 24,
    p: 4,
};

const EditRecipientModal = ({ open, onClose, profile, onChange, onSubmit }) => {
    const [error, setError] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!profile.firstName || !profile.lastName || !profile.email) {
            setError('All fields must be filled');
            return;
        }
        if (!emailRegex.test(profile.email)) {
            setError('Invalid email address');
            return;
        }
        setError('');
        onSubmit();
    };

    return (
        <>
            { profile ? (
                <Modal open={open} onClose={onClose}>
                    <Box sx={{ ...modalStyle, width: 400 }}>
                        <Typography variant="h6" component="h2">
                            Edit Recipient Profile
                        </Typography>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Email"
                                name="email"
                                value={profile.email}
                                onChange={onChange}
                                fullWidth
                                margin="normal"
                                error={!!error && (!profile.email || error === 'Invalid email address')}
                                helperText={!!error && (!profile.email ? 'Email is required' : error === 'Invalid email address' ? 'Invalid email address' : '')}
                            />
                            <TextField
                                label="First Name"
                                name="firstName"
                                value={profile.firstName}
                                onChange={onChange}
                                fullWidth
                                margin="normal"
                                error={!!error && !profile.firstName}
                                helperText={!!error && !profile.firstName ? 'First Name is required' : ''}
                            />
                            <TextField
                                label="Last Name"
                                name="lastName"
                                value={profile.lastName}
                                onChange={onChange}
                                fullWidth
                                margin="normal"
                                error={!!error && !profile.lastName}
                                helperText={!!error && !profile.lastName ? 'Last Name is required' : ''}
                            />
                            <TextField
                                label="Company Name"
                                name="companyName"
                                value={profile.companyName}
                                onChange={onChange}
                                fullWidth
                                margin="normal"
                            />
                            <Select
                                label="Status"
                                name="inactive"
                                value={profile.inactive ? 'Inactive' : 'Active'}
                                onChange={(e) => onChange({ target: { name: 'inactive', value: e.target.value === 'Inactive' } })}
                                fullWidth
                                margin="normal"
                            >
                                <MenuItem value="Active">Active</MenuItem>
                                <MenuItem value="Inactive">Inactive</MenuItem>
                            </Select>
                            <Box mt={2} />
                            <Button type="submit" variant="contained" color="primary">
                                Save
                            </Button>
                        </form>
                    </Box>
                </Modal>
            ) : null }
        </>
    );
};

EditRecipientModal.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    profile: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
};

export default EditRecipientModal;
